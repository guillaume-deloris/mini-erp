package fr.guillaume.minierp.ln;

import fr.guillaume.minierp.common.ExternalServiceException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.StringReader;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * HTTP client for the LN API.
 */
@ApplicationScoped
public class LnClient {

    // In real life: in the server configuration, never in the code
    private static final String BASE_URL = "http://localhost:8080/mini-erp/api/fake-ln";

    private Client client;

    // A Client is expensive to create: one for the whole application
    @PostConstruct
    void init() {
        client = ClientBuilder.newBuilder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    @PreDestroy
    void close() {
        client.close();
    }

    public Optional<LnItem> fetchItem(String itemId, boolean simulateFailure) {
        try (Response response = client.target(BASE_URL)
                .path("items")
                .path(itemId)
                .queryParam("fail", simulateFailure)
                .request(MediaType.APPLICATION_XML)
                .get()) {
            int status = response.getStatus();
            if (status == 404) {
                return Optional.empty();
            }
            if (status != 200) {
                throw new ExternalServiceException("LN answered with status " + status, null);
            }
            return Optional.of(parse(response.readEntity(String.class)));
        } catch (ProcessingException e) {
            // Connection refused, timeout...
            throw new ExternalServiceException("LN is unreachable", e);
        }
    }

    private LnItem parse(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Security: refuses DOCTYPE declarations (XXE attacks)
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            XPath xpath = XPathFactory.newInstance().newXPath();
            String id = xpath.evaluate("/SyncItemMaster/DataArea/ItemMaster/ItemMasterHeader/ItemID/ID", document);
            String description = xpath.evaluate("/SyncItemMaster/DataArea/ItemMaster/ItemMasterHeader/Description", document);
            if (id.isBlank()) {
                throw new ExternalServiceException("Invalid BOD: no item ID", null);
            }
            return new LnItem(id, description);
        } catch (ExternalServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalServiceException("Unreadable BOD from LN", e);
        }
    }
}