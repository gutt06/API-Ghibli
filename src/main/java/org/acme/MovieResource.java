package org.acme;

import io.quarkus.panache.common.Sort;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.Set;

@Path("/movies")
public class MovieResource {

    @GET
    public Response getAll(){
        return Response.ok(Movie.listAll()).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") int id){
        Movie entity = Movie.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }

    @POST
    @Transactional
    public Response insert(Movie movie){
        Movie.persist(movie);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Transactional
    @Path("{id}")
    public Response delete(@PathParam("id") int id){
        Movie entity = Movie.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Movie.deleteById(id);
        return Response.noContent().build();
    }

    @PUT
    @Transactional
    @Path("{id}")
    public Response update(@PathParam("id") int id, Movie newMovie){
        Movie entity = Movie.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        entity.titulo = newMovie.titulo;
        entity.autor = newMovie.autor;
        entity.anoLancamento = newMovie.anoLancamento;
        entity.nota = newMovie.nota;

        return Response.status(Response.Status.OK).entity(entity).build();
    }

    @GET
    @Path("/search")
    public Response search(
            @QueryParam("q") String q,
            @QueryParam("sort") @DefaultValue("id") String sort,
            @QueryParam("direction") @DefaultValue("asc") String direction,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ){
        Set<String> allowed = Set.of("id", "titulo", "autor", "anoLancamento", "nota");
        if(!allowed.contains(sort)){
            sort = "id";
        }

        Sort sortObj = Sort.by(
                sort,
                "desc".equalsIgnoreCase(direction) ? Sort.Direction.Descending : Sort.Direction.Ascending
        );

        int effectivePage = page <= 1 ? 0 : page - 1;

        var query = (q == null || q.isBlank() ? Movie.findAll(sortObj) : Movie.find("lower(titulo) like ?1 or lower(autor) like ?1", sortObj, "%" + q.toLowerCase() + "%"));

        var movies = query.page(effectivePage, size).list();

        return Response.ok(movies).build();
    };
}
