package org.acme;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;

@Path("/books") // é utilizando este @path que indica que esta classe está relacionada a rotas da API
public class BookResource {

    @GET
    public Response getAll(){
        return Response.ok(Book.listAll()).build(); // index que retorna todos os livros cadastrados, por isso não possui um path
    }

    @GET
    @Path("{id}") // pois ja temos um @get, então precisamos de um path para diferenciar
    public Response getById(@PathParam("id") int id){
        Book entity = Book.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }

    @GET
    @Path("/search")
    public Response search(@QueryParam("title") String title,
                           @QueryParam("sort") String sort,
                           @QueryParam("direction") String direction){ // queryparam viria depois de um ? em uma url, pathparam viria antes de uma ? na url
        var books = Book.find("titulo = ?1", title); // utilizamos ? para evitar sql injection
        return Response.ok(books).build(); // fazendo uma busca por titulo
    }

    @POST // não é necessário um path pois temos apenas um post
    @Transactional
    public Response insert(Book book){
        Book.persist(book);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Transactional // colocamos transactional quando fazemos alguma alteração no banco de dados
    @Path("{id}")
    public Response delete(@PathParam("id") int id){
        Book entity = Book.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Book.deleteById(id);
        return Response.noContent().build();
    }

    @PUT
    @Transactional // colocamos transactional quando fazemos alguma alteração no banco de dados
    @Path("{id}")
    public Response update(@PathParam("id") int id, Book newBook){
        Book entity = Book.findById(id); // passamos o id para a variavel entity
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        entity.titulo = newBook.titulo;
        entity.autor = newBook.autor;
        entity.editora = newBook.editora;
        entity.anoLancamento = newBook.anoLancamento;
        entity.estaDisponivel = newBook.estaDisponivel;

        return Response.status(Response.Status.OK).entity(entity).build();
    }
}
