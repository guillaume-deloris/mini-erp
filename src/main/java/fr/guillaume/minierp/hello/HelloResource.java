package fr.guillaume.minierp.hello;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * First REST resource, used to check that the deployment works.
 * Full URL: http://localhost:8080/mini-erp/api/hello
 * receive http requests
 */
@Path("/hello")
public class HelloResource {

    // GET /api/hello -> plain text
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@QueryParam("lang") @DefaultValue("en") String lang) {
        if ("fr".equals(lang)) {
            return "Bonjour depuis Payara FR";
        }
        return "Hello from Payara EN";
    }

    // GET /api/hello/Guillaume -> JSON, serialized automatically by JSON-B
    // http://localhost:8080/mini-erp/api/hello/Toto
    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public HelloMessage helloName(@PathParam("name") String name) {
        return new HelloMessage("Hello " + name + " !");
    }

    //Add GET /api/hello/upper/{name} that returns the name in upper case.
    @GET
    @Path("/upper/{name}")
    @Produces(MediaType.TEXT_PLAIN)  // response format
    public String helloUpperName(@PathParam("name") String name) {
        String upperName = name.toUpperCase();
        return "Hello " + upperName + " !";
    }

    //Add a field int length to HelloMessage and check it appears in the JSON.
    /*@GET
    @Path("/length/{name}")
    @Produces(MediaType.TEXT_PLAIN)  // response format
    public String helloLength(@PathParam("name") String name) {
        int nameLength = name.length();
        return "Length is " + nameLength;
    }*/

}
