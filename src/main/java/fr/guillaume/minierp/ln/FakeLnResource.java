package fr.guillaume.minierp.ln;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.UUID;

/**
 * Simulates an Infor LN API returning items as simplified SyncItemMaster BODs.
 */
@Path("/fake-ln/items")
public class FakeLnResource {

    private static final Map<String, String> ITEMS = Map.of(
            "P010", "Pied de table chene",
            "P011", "Plateau noyer 180x90");

    @GET
    @Path("/{itemId}")
    @Produces(MediaType.APPLICATION_XML)
    //@QueryParam("slow") boolean slow)
    //@QueryParam("slow") boolean slow)
    public Response getItem(@PathParam("itemId") String itemId, @QueryParam("fail") boolean fail, @QueryParam("slow") boolean slow) throws InterruptedException {
        if (slow) {
            Thread.sleep(10000);
        }
        if (fail) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
        String description = ITEMS.get(itemId);
        if (description == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        String xml = """
                <SyncItemMaster>
                    <ApplicationArea>
                        <Sender><LogicalID>infor.ln.erp</LogicalID></Sender>
                        <CreationDateTime>2026-10-31T10:00:00Z</CreationDateTime>
                        <BODID>%s</BODID>
                    </ApplicationArea>
                    <DataArea>
                        <Sync>
                            <ActionCriteria><ActionExpression actionCode="Replace"/></ActionCriteria>
                        </Sync>
                        <ItemMaster>
                            <ItemMasterHeader>
                                <ItemID><ID>%s</ID></ItemID>
                                <Description>%s</Description>
                            </ItemMasterHeader>
                        </ItemMaster>
                    </DataArea>
                </SyncItemMaster>
                """.formatted(UUID.randomUUID(), itemId, description);
        return Response.ok(xml).build();
    }
}