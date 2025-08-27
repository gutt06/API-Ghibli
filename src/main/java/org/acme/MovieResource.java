package org.acme;

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
}
