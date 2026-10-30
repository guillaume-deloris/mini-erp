package fr.guillaume.minierp.common;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Translates business exceptions into HTTP responses.
 * @Provider: discovered automatically by JAX-RS, like a @Path resource.
 */
@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {
    @Override
    public Response toResponse(BusinessException e) {
        Response.Status status;
        if (e instanceof DuplicateReferenceException)  {
            status = Response.Status.CONFLICT;
        } else {
            // InsufficientStockException, InvalidDataException
            status = Response.Status.BAD_REQUEST;
        }
        return Response.status(status)
                .entity(new ErrorResponse(e.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
