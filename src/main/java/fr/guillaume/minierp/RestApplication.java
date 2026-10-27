package fr.guillaume.minierp;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Activates JAX-RS. Every REST resource of the application is exposed under /api.
 * No method to implement: Payara scans the classes annotated with @Path.
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
}
