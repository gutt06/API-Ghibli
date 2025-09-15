package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.List;
import java.util.Set;

@Path("/filmes")
public class FilmeResource {

    @GET
    @Operation(
            summary = "Retorna todos os filmes (getAll)",
            description = "Retorna uma lista de filmes por padrão no formato JSON"
    )
    @APIResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Filme.class, type = SchemaType.ARRAY)
            )
    )
    public Response getAll(){
        return Response.ok(Filme.listAll()).build();
    }

    @GET
    @Path("{id}") // Temos 2 possiveis responses um achou e o outro nao encontrado not found
    public Response getById(
            @Parameter(description = "Id do filme a ser pesquisado", required = true)
            @PathParam("id") int id){
        Filme entity = Filme.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }

    @GET
    @Operation(
            summary = "Retorna os filmes conforme o sistema de pesquisa (search)",
            description = "Retorna uma lista de filmes filtrada conforme a pesquisa por padrão no formato JSON"
    )
    @Path("/search") // Temos uma response do tipo ok 200
    public Response search(
            @Parameter(description = "Query de buscar por titulo, ano de lançamento ou idade indicativa")
            @QueryParam("q") String q,
            @Parameter(description = "Campo de ordenação da lista de retorno")
            @QueryParam("sort") @DefaultValue("id") String sort,
            @Parameter(description = "Esquema de filtragem de livros por ordem crescente ou decrescente")
            @QueryParam("direction") @DefaultValue("asc") String direction,
            @Parameter(description = "Define qual página será retornada na response")
            @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Define quantos objetos serão retornados por query")
            @QueryParam("size") @DefaultValue("4") int size
    ){
        Set<String> allowed = Set.of("id", "titulo", "sinopse", "anoLancamento", "nota", "idadeIndicativa");
        if(!allowed.contains(sort)){
            sort = "id";
        }

        Sort sortObj = Sort.by(
                sort,
                "desc".equalsIgnoreCase(direction) ? Sort.Direction.Descending : Sort.Direction.Ascending
        );

        int effectivePage = page <= 1 ? 0 : page - 1;

        PanacheQuery<Filme> query;

        if (q == null || q.isBlank()) {
            query = Filme.findAll(sortObj);
        } else {
            try {
                // tenta converter a pesquisa em número
                int numero = Integer.parseInt(q);

                // busca apenas em campos numéricos
                query = Filme.find(
                        "anoLancamento = ?1 or idadeIndicativa = ?1",
                        sortObj,
                        numero
                );

            } catch (NumberFormatException e) {
                // se não for número, busca só em campos textuais
                query = Filme.find(
                        "lower(titulo) like ?1",
                        sortObj,
                        "%" + q.toLowerCase() + "%"
                );
            }
        }

        List<Filme> filmes = query.page(effectivePage, size).list();

        var response = new SearchFilmeResponse();
        response.Filmes = filmes;
        response.TotalFilmes = query.list().size();
        response.TotalPages = query.pageCount();
        response.HasMore = page < query.pageCount();
        response.NextPage = response.HasMore ? "http://localhost:8080/filmes/search?q="+q+"&page="+(page + 1) + (size > 0 ? "&size="+size : "") : "";

        return Response.ok(response).build();
    }

    @POST // Temos 2 tipos de response um 201 created e um 400 bad request
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Filme.class)
            )
    )
    @APIResponse(
            responseCode = "201",
            description = "Created",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Filme.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))
    )
    @Transactional
    public Response insert(Filme filme){
        Filme.persist(filme);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Transactional
    @Path("{id}") // Temos 2 responses uma de not found e outra de no content
    public Response delete(@PathParam("id") int id){
        Filme entity = Filme.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Filme.deleteById(id);
        return Response.noContent().build();
    }

    @PUT
    @Transactional
    @Path("{id}") // Temos 2 responses uma de not found e outra de ok
    public Response update(@PathParam("id") int id, Filme newFilme){
        Filme entity = Filme.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        entity.titulo = newFilme.titulo;
        entity.sinopse = newFilme.sinopse;
        entity.anoLancamento = newFilme.anoLancamento;
        entity.nota = newFilme.nota;
        entity.idadeIndicativa = newFilme.idadeIndicativa;

        return Response.status(Response.Status.OK).entity(entity).build();
    }
}
