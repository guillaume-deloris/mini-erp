package fr.guillaume.minierp.common;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Catches every other exception, so the client never receives an HTML page.
 */
@Provider
public class TechnicalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(TechnicalExceptionMapper.class.getName());

    // PostgreSQL error code for a UNIQUE constraint violation
    private static final String UNIQUE_VIOLATION = "23505";

    @Override
    public Response toResponse(Exception e) {
        // JAX-RS exceptions (NotFoundException...): keep the status, add a JSON body if missing
        if (e instanceof WebApplicationException webException) {
            Response response = webException.getResponse();
            if (response.hasEntity()) {
                return response;
            }
            return json(response.getStatus(), e.getMessage());
        }
        // Concurrent creation that passed the Java check: the database has the last word
        if (isUniqueViolation(e)) {
            return json(409, "This data already exists");
        }
        // Real bug: full details in server.log, generic message for the client
        LOG.log(Level.SEVERE, "Unexpected error", e);
        return json(500, "Internal error");
    }

    // Walks the cause chain to find the original SQL error
    private boolean isUniqueViolation(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql && UNIQUE_VIOLATION.equals(sql.getSQLState())) {
                return true;
            }
        }
        return false;
    }

    private Response json(int status, String message) {
        return Response.status(status)
                .entity(new ErrorResponse(message))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}