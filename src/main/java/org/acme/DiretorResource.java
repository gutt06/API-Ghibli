package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.List;
import java.util.Set;

@Path("/diretores")
@Timeout(30000)
public class DiretorResource {
    @GET
    @Operation(
            summary = "Retorna todos os diretores (getAll)",
            description = "Retorna uma lista de diretores por padrão no formato JSON"
    )
    @APIResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Diretor.class, type = SchemaType.ARRAY)
            )
    )
    @CircuitBreaker(
            requestVolumeThreshold = 4, // monitora as últimas 4 chamadas
            failureRatio = 0.5,         // abre o circuito se metade falhar
            delay = 5000,               // 5 segundos de espera
            successThreshold = 2        // precisa de 2 sucessos seguidos para fechar
    )
    @Fallback(fallbackMethod = "getAllFallback")
    public Response getAll(){
        return Response.ok(Diretor.listAll()).build();
    }

    public Response getAllFallback() {
        String mensagem = "Servico temporariamente indisponivel para listar os diretores. Por favor, tente novamente mais tarde.";
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(mensagem).build();
    }

    @GET
    @Path("{id}")
    @Timeout(25000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 4000, successThreshold = 2)
    @Fallback(fallbackMethod = "getByIdFallback")
    @Operation(
            summary = "Retorna um diretor pela busca por ID (getById)",
            description = "Retorna um diretor específico pela busca de ID colocado na URL no formato JSON por padrão"
    )
    @APIResponse(
            responseCode = "200",
            description = "Item retornado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Diretor.class, type = SchemaType.ARRAY)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Item não encontrado",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))
    )
    public Response getById(
            @Parameter(description = "Id do diretor a ser pesquisado", required = true)
            @PathParam("id") long id){
        Diretor entity = Diretor.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }

    public Response getByIdFallback(long id) {
        String mensagem = "Servico temporariamente indisponivel para consulta do diretor com id " + id + ". Por favor, tente novamente mais tarde.";
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(mensagem).build();
    }

    @GET
    @Operation(
            summary = "Retorna os diretores conforme o sistema de pesquisa (search)",
            description = "Retorna uma lista de diretores filtrada conforme a pesquisa por padrão no formato JSON"
    )
    @APIResponse(
            responseCode = "200",
            description = "Item retornado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Diretor.class, type = SchemaType.ARRAY)
            )
    )
    @Path("/search")
    @Timeout(15000)
    @CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.4, delay = 4000, successThreshold = 2)
    @Fallback(fallbackMethod = "searchFallback")
    public Response search(
            @Parameter(description = "Query de buscar por nome ou nacionalidade")
            @QueryParam("q") String q,
            @Parameter(description = "Campo de ordenação da lista de retorno")
            @QueryParam("sort") @DefaultValue("id") String sort,
            @Parameter(description = "Esquema de filtragem de diretores por ordem crescente ou decrescente")
            @QueryParam("direction") @DefaultValue("asc") String direction,
            @Parameter(description = "Define qual página será retornada na response")
            @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Define quantos objetos serão retornados por query")
            @QueryParam("size") @DefaultValue("4") int size
    ){
        Set<String> allowed = Set.of("id", "nome", "nascimento", "nacionalidade");
        if(!allowed.contains(sort)){
            sort = "id";
        }

        Sort sortObj = Sort.by(
                sort,
                "desc".equalsIgnoreCase(direction) ? Sort.Direction.Descending : Sort.Direction.Ascending
        );

        int effectivePage = Math.max(page, 0);

        PanacheQuery<Diretor> query;

        if (q == null || q.isBlank()) {
            query = Diretor.findAll(sortObj);
        } else {
            query = Diretor.find(
                    "lower(nome) like ?1 or lower(nacionalidade) like ?1", sortObj, "%" + q.toLowerCase() + "%");
        }

        List<Diretor> diretores = query.page(effectivePage, size).list();

        var response = new SearchDiretorResponse();
        response.Diretores = diretores;
        response.TotalDiretores = query.list().size();
        response.TotalPages = query.pageCount();
        response.HasMore = effectivePage < query.pageCount() - 1; // Faz o pagecount - 1 pois a pagina 1 seria o indice 0, a comparação é feita com o índice da última página valida
        response.NextPage = response.HasMore ? "http://localhost:8080/diretores/search?q="+(q != null ? q : "")+"&page="+(effectivePage + 1) + (size > 0 ? "&size="+size : "") : "";

        return Response.ok(response).build();
    }

    public Response searchFallback(String q, String sort, String direction, int page, int size) {
        String mensagem = "Servico temporariamente indisponivel para pesquisa de diretores. Por favor, tente novamente mais tarde.";
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(mensagem).build();
    }

    @POST
    @Timeout(10000)
    @CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.6, delay = 6000, successThreshold = 2)
    @Operation(
            summary = "Adiciona um registro a lista de diretores (insert)",
            description = "Adiciona um item a lista de diretores por meio de POST e request body JSON"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Diretor.class)
            )
    )
    @APIResponse(
            responseCode = "201",
            description = "Created",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Diretor.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))
    )
    @Transactional
    @Fallback(fallbackMethod = "insertFallback")
    public Response insert(@Valid Diretor diretor){
        Diretor.persist(diretor);
        return Response.status(Response.Status.CREATED).build();
    }

    public Response insertFallback(@Valid Diretor diretor) {
        String mensagem = "Servico temporariamente indisponivel para inserir diretores. Por favor, tente novamente mais tarde.";
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(mensagem).build();
    }

    @DELETE
    @Operation(
            summary = "Remove um registro da lista de diretores (delete)",
            description = "Remove um item da lista de diretores por meio de Id na URL"
    )
    @APIResponse(
            responseCode = "204",
            description = "Sem conteúdo",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))
    )
    @APIResponse(
            responseCode = "404",
            description = "Item não encontrado",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))
    )
    @APIResponse(
            responseCode = "409",
            description = "Conflito - Diretor possui filmes vinculados",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))
    )
    @Transactional
    @Path("{id}")
    @Timeout(10000)
    @CircuitBreaker(requestVolumeThreshold = 3, failureRatio = 0.5, delay = 5000, successThreshold = 2)
    @Fallback(fallbackMethod = "deleteFallback")
    public Response delete(@PathParam("id") long id){
        Diretor entity = Diretor.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        long filmesVinculados = Filme.count("diretor.id = ?1", id);
        if(filmesVinculados > 0){
            return Response.status(Response.Status.CONFLICT)
                    .entity("Não é possível deletar diretor. Existem " + filmesVinculados + " filme(s) vinculado(s).")
                    .build();
        }

        Diretor.deleteById(id);
        return Response.noContent().build();
    }

    public Response deleteFallback(long id) {
        String mensagem = "Servico temporariamente indisponivel para deletar diretor com id " + id + ". Por favor, tente novamente mais tarde.";
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(mensagem).build();
    }

    @PUT
    @Operation(
            summary = "Altera um registro da lista de diretores (update)",
            description = "Edita um item da lista de diretores por meio de Id na URL e request body JSON"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Diretor.class)
            )
    )
    @APIResponse(
            responseCode = "200",
            description = "Item editado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Diretor.class, type = SchemaType.ARRAY)
            )
    )
    @APIResponse(
            responseCode = "404",
            description = "Item não encontrado",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))
    )
    @Transactional
    @Path("{id}")
    @Timeout(20000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000, successThreshold = 2)
    @Fallback(fallbackMethod = "updateFallback")
    public Response update(@PathParam("id") long id, @Valid Diretor newDiretor){
        Diretor entity = Diretor.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        entity.nome = newDiretor.nome;
        entity.nascimento = newDiretor.nascimento;
        entity.nacionalidade = newDiretor.nacionalidade;
        // Atualizar biografia (será criada automaticamente se não existir devido ao CascadeType.ALL)
        if(newDiretor.biografia != null){
            if(entity.biografia == null){
                entity.biografia = new BiografiaDiretor();
            }
            entity.biografia.textoCompleto = newDiretor.biografia.textoCompleto;
            entity.biografia.resumo = newDiretor.biografia.resumo;
            entity.biografia.premiosRecebidos = newDiretor.biografia.premiosRecebidos;
        } else {
            // Se não vier biografia no request, limpa a biografia existente
            entity.biografia = null;
        }

        return Response.status(Response.Status.OK).entity(entity).build();
    }

    public Response updateFallback(long id, @Valid Diretor newDiretor) {
        String mensagem = "Servico temporariamente indisponivel para atualizar diretor com id " + id + ". Por favor, tente novamente mais tarde.";
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(mensagem).build();
    }
}
