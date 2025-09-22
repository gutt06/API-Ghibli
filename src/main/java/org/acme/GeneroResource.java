package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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

@Path("/generos")
public class GeneroResource {
    @GET
    @Operation(
            summary = "Retorna todos os generos (getAll)",
            description = "Retorna uma lista de generos por padrão no formato JSON"
    )
    @APIResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Genero.class, type = SchemaType.ARRAY)
            )
    )
    public Response getAll(){
        return Response.ok(Genero.listAll()).build();
    }

    @GET
    @Path("{id}")
    @Operation(
            summary = "Retorna um genero pela busca por ID (getById)",
            description = "Retorna um genero específico pela busca de ID colocado na URL no formato JSON por padrão"
    )
    @APIResponse(
            responseCode = "200",
            description = "Item retornado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Genero.class, type = SchemaType.ARRAY)
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
            @Parameter(description = "Id do genero a ser pesquisado", required = true)
            @PathParam("id") long id){
        Genero entity = Genero.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }

    @GET
    @Operation(
            summary = "Retorna os generos conforme o sistema de pesquisa (search)",
            description = "Retorna uma lista de generos filtrada conforme a pesquisa por padrão no formato JSON"
    )
    @APIResponse(
            responseCode = "200",
            description = "Item retornado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Genero.class, type = SchemaType.ARRAY)
            )
    )
    @Path("/search")
    public Response search(
            @Parameter(description = "Query de buscar por nome")
            @QueryParam("q") String q,
            @Parameter(description = "Campo de ordenação da lista de retorno")
            @QueryParam("sort") @DefaultValue("id") String sort,
            @Parameter(description = "Esquema de filtragem de generos por ordem crescente ou decrescente")
            @QueryParam("direction") @DefaultValue("asc") String direction,
            @Parameter(description = "Define qual página será retornada na response")
            @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Define quantos objetos serão retornados por query")
            @QueryParam("size") @DefaultValue("4") int size
    ){
        Set<String> allowed = Set.of("id", "nome", "descricao");
        if(!allowed.contains(sort)){
            sort = "id";
        }

        Sort sortObj = Sort.by(
                sort,
                "desc".equalsIgnoreCase(direction) ? Sort.Direction.Descending : Sort.Direction.Ascending
        );

        int effectivePage = Math.max(page, 0);

        PanacheQuery<Genero> query;

        if (q == null || q.isBlank()) {
            query = Genero.findAll(sortObj);
        } else {
            query = Genero.find(
                    "lower(nome) like ?1", sortObj, "%" + q.toLowerCase() + "%");
        }

        List<Genero> generos = query.page(effectivePage, size).list();

        var response = new SearchGeneroResponse();
        response.Generos = generos;
        response.TotalGeneros = query.list().size();
        response.TotalPages = query.pageCount();
        response.HasMore = effectivePage < query.pageCount() - 1; // Faz o pagecount - 1 pois a pagina 1 seria o indice 0, a comparação é feita com o índice da última página valida
        response.NextPage = response.HasMore ? "http://localhost:8080/generos/search?q="+(q != null ? q : "")+"&page="+(effectivePage + 1) + (size > 0 ? "&size="+size : "") : "";

        return Response.ok(response).build();
    }

    @POST
    @Operation(
            summary = "Adiciona um registro a lista de generos (insert)",
            description = "Adiciona um item a lista de generos por meio de POST e request body JSON"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Genero.class)
            )
    )
    @APIResponse(
            responseCode = "201",
            description = "Created",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Genero.class))
    )
    @APIResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))
    )
    @Transactional
    public Response insert(@Valid Genero genero){
        Genero.persist(genero);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Operation(
            summary = "Remove um registro da lista de generos (delete)",
            description = "Remove um item da lista de generos por meio de Id na URL"
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
            description = "Conflito - Gênero possui filmes vinculados",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class))
    )
    @Transactional
    @Path("{id}")
    public Response delete(@PathParam("id") long id){
        Genero entity = Genero.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        long filmesVinculados = Filme.count("?1 MEMBER OF generos", entity);
        if(filmesVinculados > 0){
            return Response.status(Response.Status.CONFLICT)
                    .entity("Não é possível deletar genero. Existem " + filmesVinculados + " filme(s) vinculado(s).")
                    .build();
        }

        Genero.deleteById(id);
        return Response.noContent().build();
    }

    @PUT
    @Operation(
            summary = "Altera um registro da lista de generos (update)",
            description = "Edita um item da lista de generos por meio de Id na URL e request body JSON"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Genero.class)
            )
    )
    @APIResponse(
            responseCode = "200",
            description = "Item editado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Genero.class, type = SchemaType.ARRAY)
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
    public Response update(@PathParam("id") long id,@Valid Genero newGenero){
        Genero entity = Genero.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        entity.nome = newGenero.nome;
        entity.descricao = newGenero.descricao;

        return Response.status(Response.Status.OK).entity(entity).build();
    }
}
