package fr.guillaume.minierp.product;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * REST API for products.
 * @Produces and @Consumes on the class apply to every method.
 */
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class ProductResource {
    //@Inject : CDI fournit automatiquement l’instance unique du repository
    @Inject
    private ProductRepository repository;

    //GET /api/products/
    @GET
    @Path ("/{id}")
    public Product get(@PathParam("id") Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product "+ id + "not found."));
    }

    // GET /api/products            -> all products
    // GET /api/products?name=vis   -> filtered in the database
    // curl "http://localhost:8080/mini-erp/api/products/name?name=VIS"
    @GET
    public List<Product> list(@QueryParam("name") String name) {
        if (name == null || name.isBlank()) {
            return repository.findAll();
        }
        return repository.findByName(name);
    }

    //Product creation
    //POST /api/products (JSPN body -> Product object, thanks to JSON-B)
    @POST
    public Response create(Product product, @Context UriInfo uriInfo) {
        if (product.getStock() < 0 || product.getName() == null) {
            throw new BadRequestException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity(Map.of("error", "Name is required and stock must be positive"))
                            .type(MediaType.APPLICATION_JSON)
                            .build());
        }
        Product created = repository.create(product);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(created.getId()))
                .build();
        return Response.created(location).entity(created).build();
    }

    //Stock update
    //POST /api/products/{id}/stock?delta=-10
    @POST
    @Path("/{id}/stock")
    public Product updateStock(@PathParam("id") Long id, @QueryParam("delta") Integer delta) {
        if (delta == null) {
            throw new BadRequestException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity(Map.of("error", "The delta parameter is required"))
                            .type(MediaType.APPLICATION_JSON)
                            .build());
        }
        return repository.updateStock(id, delta)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found."));
    }

    //PUT /api/products/1
    @PUT
    @Path("/{id}")
    public Product update(@PathParam("id") Long id, Product product) {
        //orElseThrow(() -> ...) : si l’Optional est vide, on lance l’exception.
        // () -> ... est une lambda, l’équivalent d’une fonction fléchée en JavaScript.
        return repository.update(id, product)
            .orElseThrow(() -> new NotFoundException("Product " + id + " not found."));
    }

    //DELETE /api/products/1
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        if (!repository.delete(id)) {
            throw new NotFoundException("Product "+ id + "not found.");
        }
        return Response.noContent().build();
    }
}
