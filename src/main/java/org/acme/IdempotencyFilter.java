package org.acme;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Provider
@Priority(2000) // Executar DEPOIS do CORS (que geralmente é 1000 ou menos)
public class IdempotencyFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    private static final String IDEMPOTENCY_KEY_PROPERTY = "idempotency.key";
    private static final String REQUEST_BODY_PROPERTY = "original.request.body";

    @Inject
    IdempotencyService idempotencyService;

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Only apply idempotency to POST requests
        if (!"POST".equalsIgnoreCase(requestContext.getMethod())) {
            return;
        }

        String idempotencyKey = requestContext.getHeaderString(IDEMPOTENCY_KEY_HEADER);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            // Store the idempotency key for response filter
            requestContext.setProperty(IDEMPOTENCY_KEY_PROPERTY, idempotencyKey);

            // Check if we've seen this idempotency key before
            Response cachedResponse = idempotencyService.checkIdempotency(idempotencyKey);

            if (cachedResponse != null) {
                // IMPORTANTE: Adicionar headers CORS manualmente quando abortamos a requisição
                Response responseWithCors = Response.fromResponse(cachedResponse)
                        .header("Access-Control-Allow-Origin", "https://gutt06.github.io")
                        .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                        .header("Access-Control-Allow-Headers", "Content-Type, Accept, Authorization, Idempotency-Key")
                        .header("Access-Control-Allow-Credentials", "true")
                        .type(MediaType.APPLICATION_JSON)
                        .build();

                requestContext.abortWith(responseWithCors);
                return;
            }

            // Mark this key as being processed
            idempotencyService.markAsProcessing(idempotencyKey);

            // Cache the request body so it can be read again by the endpoint
            if (requestContext.hasEntity()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream entityStream = requestContext.getEntityStream();
                byte[] buffer = new byte[1024];
                int length;

                while ((length = entityStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }

                byte[] requestBody = baos.toByteArray();
                requestContext.setEntityStream(new ByteArrayInputStream(requestBody));
                requestContext.setProperty(REQUEST_BODY_PROPERTY, requestBody);
            }
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // Only process POST requests with idempotency key
        if (!"POST".equalsIgnoreCase(requestContext.getMethod())) {
            return;
        }

        String idempotencyKey = (String) requestContext.getProperty(IDEMPOTENCY_KEY_PROPERTY);

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            int statusCode = responseContext.getStatus();

            // Only cache successful responses (2xx) or client errors (4xx)
            if (statusCode >= 200 && statusCode < 500) {
                // Get response body and serialize it to JSON
                Object entity = responseContext.getEntity();
                String responseBody = "";

                if (entity != null) {
                    try {
                        responseBody = objectMapper.writeValueAsString(entity);
                    } catch (Exception e) {
                        // Log do erro para debug
                        System.err.println("Erro ao serializar resposta para idempotência: " + e.getMessage());
                        responseBody = "{\"error\": \"Serialization failed\"}";
                    }
                }

                // Store the response for future requests with the same idempotency key
                idempotencyService.storeResponse(idempotencyKey, statusCode, responseBody);
            } else {
                // For server errors (5xx), remove the processing mark to allow retry
                idempotencyService.removeKey(idempotencyKey);
            }
        }

        // Garantir que os headers CORS estão sempre presentes
        if (!responseContext.getHeaders().containsKey("Access-Control-Allow-Origin")) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", "https://gutt06.github.io");
            responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Accept, Authorization, Idempotency-Key");
            responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        }
    }
}