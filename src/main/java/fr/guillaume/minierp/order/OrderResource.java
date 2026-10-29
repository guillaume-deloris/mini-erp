package fr.guillaume.minierp.order;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    private OrderService service;

    @GET
    public List<CustomerOrder> list() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public CustomerOrder get(@PathParam("id") Long id) {
        return service.findById(id)
                .orElseThrow(() -> new NotFoundException("Order " + id + " not found"));
    }

    @POST
    public Response create(CreateOrderRequest request, @Context UriInfo uriInfo) {
        CustomerOrder created = service.create(request);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(created.getId()))
                .build();
        return Response.created(location).entity(created).build();
    }

    // POST /api/orders/1/confirm
    @POST
    @Path("/{id}/confirm")
    public CustomerOrder confirm(@PathParam("id") Long id) {
        return service.confirm(id)
                .orElseThrow(() -> new NotFoundException("Order " + id + " not found"));
    }
}