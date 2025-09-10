package org.acme;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Path("/books") // é utilizando este @path que indica que esta classe está relacionada a rotas da API
public class BookResource {

    @GET
    @Operation( // Dando uma descrição no nosso swagger sobre o que faz a rota
        summary = "Retorna todos os livros (getAll)",
        description = "Retorna uma lista de livros por padrão no formato JSON"
    )
    @APIResponse( // Detalha algumas coisas na parte de responses no swagger
        responseCode = "200",
        description = "Retornado a lista corretamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Book.class, type = SchemaType.ARRAY) // O implementation é a classe especifica que estou utilizando e o tipo é um arraylist class
        )
    )
    public Response getAll(){
        return Response.ok(Book.listAll()).build(); // index que retorna todos os livros cadastrados, por isso não possui um path
    }

    @GET
    @Path("{id}") // pois ja temos um @get, então precisamos de um path para diferenciar
    public Response getById(
            @Parameter(description = "Id do livro a ser pesquisado", required = true)
            @PathParam("id") int id){
        Book entity = Book.findById(id);
        if(entity == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }

    //@GET
    //@Path("/search")
    //public Response search(@QueryParam("title") String title,
    //                       @QueryParam("sort") String sort,
    //                       @QueryParam("direction") String direction){ // queryparam viria depois de um ? em uma url, pathparam viria antes de uma ? na url
    //    var books = Book.find("titulo = ?1", title); // utilizamos ? para evitar sql injection
    //    return Response.ok(books).build(); // fazendo uma busca por titulo
    //}

    @GET
    @Operation( // Dando uma descrição no nosso swagger sobre o que faz a rota
            summary = "Retorna os livros conforme o sistema de pesquisa (search)",
            description = "Retorna uma lista de livros filtrada conforme a pesquisa por padrão no formato JSON"
    )
    @Path("/search")
    public Response search(
            @Parameter(description = "Query de buscar por titulo ou autor ou editora")
            @QueryParam("q") String q,
            @Parameter(description = "Campo de ordenacao da lista de retorno")
            @QueryParam("sort") @DefaultValue("id") String sort,
            @Parameter(description = "Esquema de filtragem por ordem crescente ou decrescente")
            @QueryParam("direction") @DefaultValue("asc") String direction,
            @Parameter(description = "Define qual página será retornada na response")
            @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Define quantos objetos irão ser retornados por query")
            @QueryParam("size") @DefaultValue("10") int size
    ){
        Set<String> allowed = Set.of("id", "titulo", "autor", "editora", "anoLancamento", "estaDisponivel");
        if(!allowed.contains(sort)){
            sort = "id";
        }

        Sort sortObj = Sort.by(
                sort,
                "desc".equalsIgnoreCase(direction) ? Sort.Direction.Descending : Sort.Direction.Ascending // if e else para caso seja ordenado por decrescente ou crescente
        );

        int effectivePage = page <= 1 ? 0 : page - 1; // se pagina for menor ou igual a 1 i indice vai ser 0 ou valor da pagina menos 1

        var query = (q == null || q.isBlank() ? Book.findAll(sortObj) : Book.find("lower(titulo) like ?1 or lower(autor) like ?1", sortObj, "%" + q.toLowerCase() + "%"));

        List<Book> books = query.page(effectivePage, size).list();

        var response = new SearchBookResponse();
        response.Books = books;
        response.TotalBooks = query.list().size();
        response.TotalPages = query.pageCount();
        response.HasMore = page < query.pageCount();
        response.NextPage = response.HasMore ? "http://localhost:8080/books/search?q="+q+"&page="+(page + 1) + (size >  0 ? "&size="+size : "") : "";

        return Response.ok(response).build();
    };

    @POST // não é necessário um path pois temos apenas um post
    @RequestBody( // Explica o esquema da requisão
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Book.class)
            )
    )
    @APIResponse( // Requisição 201
        responseCode = "201",
        description = "Created",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Book.class))
    )
    @APIResponse( // Requisição
        responseCode = "400",
        description = "Bad Request",
        content = @Content(
            mediaType = "text/plain",
            schema = @Schema(implementation = String.class))
    )
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
