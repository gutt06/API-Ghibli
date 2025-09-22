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

import java.util.HashSet;
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
    @Path("{id}")
    @Operation(
        summary = "Retorna um filme pela busca por ID (getById)",
        description = "Retorna um filme específico pela busca de ID colocado na URL no formato JSON por padrão"
    )
    @APIResponse(
            responseCode = "200",
            description = "Item retornado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Filme.class, type = SchemaType.ARRAY)
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
            @Parameter(description = "Id do filme a ser pesquisado", required = true)
            @PathParam("id") long id){
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
    @APIResponse(
            responseCode = "200",
            description = "Item retornado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Filme.class, type = SchemaType.ARRAY)
            )
    )
    @Path("/search")
    public Response search(
            @Parameter(description = "Query de buscar por titulo, ano de lançamento ou idade indicativa")
            @QueryParam("q") String q,
            @Parameter(description = "Campo de ordenação da lista de retorno")
            @QueryParam("sort") @DefaultValue("id") String sort,
            @Parameter(description = "Esquema de filtragem de filmes por ordem crescente ou decrescente")
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

        int effectivePage = Math.max(page, 0);

        PanacheQuery<Filme> query;

        if (q == null || q.isBlank()) {
            query = Filme.findAll(sortObj);
        } else {
            try {
                // Tenta converter a pesquisa em número
                int numero = Integer.parseInt(q);

                // Busca apenas em campos numéricos
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
        response.HasMore = effectivePage < query.pageCount() - 1; // Faz o pagecount - 1 pois a pagina 1 seria o indice 0, a comparação é feita com o índice da última página valida
        response.NextPage = response.HasMore ? "http://localhost:8080/filmes/search?q="+(q != null ? q : "")+"&page="+(effectivePage + 1) + (size > 0 ? "&size="+size : "") : "";

        return Response.ok(response).build();
    }

    @POST
    @Operation(
            summary = "Adiciona um registro a lista de filmes (insert)",
            description = "Adiciona um item a lista de filmes por meio de POST e request body JSON"
    )
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
    public Response insert(@Valid Filme filme){

        // Resolver diretor (pode ter apenas id)
        if(filme.diretor != null && filme.diretor.id != null){
            Diretor d = Diretor.findById(filme.diretor.id);
            if(d == null){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Diretor com id " + filme.diretor.id + " não existe").build();
            }
            filme.diretor = d;
        } else {
            filme.diretor = null;
        }

        // Resolver generos (se vierem com id)
        if(filme.generos != null && !filme.generos.isEmpty()){
            Set<Genero> resolved = new HashSet<>();
            for(Genero g : filme.generos){
                if(g == null || g.id == 0){
                    continue;
                }
                Genero fetched = Genero.findById(g.id);
                if(fetched == null){
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Genero com id " + g.id + " não existe").build();
                }
                resolved.add(fetched);
            }
            filme.generos = resolved;
        } else {
            filme.generos = new HashSet<>();
        }

        Filme.persist(filme);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Operation(
            summary = "Remove um registro da lista de filmes (delete)",
            description = "Remove um item da lista de filmes por meio de Id na URL"
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
    @Transactional
    @Path("{id}")
    public Response delete(@PathParam("id") long id){
        Filme entity = Filme.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Remove associações ManyToMany antes de deletar
        entity.generos.clear();
        entity.persist(); // atualiza o estado

        Filme.deleteById(id);
        return Response.noContent().build();
    }

    @PUT
    @Operation(
            summary = "Altera um registro da lista de filmes (update)",
            description = "Edita um item da lista de filmes por meio de Id na URL e request body JSON"
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Filme.class)
            )
    )
    @APIResponse(
            responseCode = "200",
            description = "Item editado com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Filme.class, type = SchemaType.ARRAY)
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
    public Response update(@PathParam("id") long id,@Valid Filme newFilme){
        Filme entity = Filme.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        entity.titulo = newFilme.titulo;
        entity.sinopse = newFilme.sinopse;
        entity.anoLancamento = newFilme.anoLancamento;
        entity.nota = newFilme.nota;
        entity.idadeIndicativa = newFilme.idadeIndicativa;

        // Resolver diretor
        if(newFilme.diretor != null && newFilme.diretor.id != null){
            Diretor d = Diretor.findById(newFilme.diretor.id);
            if(d == null){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Diretor com id " + newFilme.diretor.id + " não existe").build();
            }
            entity.diretor = d;
        } else {
            entity.diretor = null;
        }

        // Resolver generos
        if(newFilme.generos != null){
            Set<Genero> resolved = new HashSet<>();
            for(Genero g : newFilme.generos){
                if(g == null || g.id == 0) continue;
                Genero fetched = Genero.findById(g.id);
                if(fetched == null){
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Genero com id " + g.id + " não existe").build();
                }
                resolved.add(fetched);
            }
            entity.generos = resolved;
        } else {
            entity.generos = new HashSet<>();
        }

        return Response.status(Response.Status.OK).entity(entity).build();
    }
}
