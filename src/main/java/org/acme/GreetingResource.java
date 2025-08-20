package org.acme;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public MyEntity hello() {
        return new MyEntity("Hello from Quarkus REST");
    }

    @GET
    @Path("/GetAll")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MyEntity> GetAll(){
        return MyEntity.listAll();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetById(@PathParam("id") int id){
        // apenas vamos fazer isso se pegarmos itens individuais
        var entity = MyEntity.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build(); // retorna erro 404 caso não encontre o id
        }
        return Response.ok(entity).build(); // retornar status 200 OK
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)

    public Response insertEntity(MyEntity entity){
        MyEntity.persist(entity);
        return Response.status(Response.Status.CREATED).entity(entity).build(); // retorna status 201 created ao realizar o POST
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public void delete(@PathParam("id") int id){
        MyEntity.deleteById(id);
    }

    @PUT
    @Path("{id}")
    @Transactional
    public void update(@PathParam("id") int id, MyEntity entity){
        MyEntity oldEntity = MyEntity.findById(id);
        oldEntity.field = entity.field;
        MyEntity.persist(oldEntity); //persist é o update
    }

}
