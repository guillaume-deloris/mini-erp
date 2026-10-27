package fr.guillaume.minierp.product;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory product storage.
 * One single instance for the whole application, so the data survives between requests.
 */

//@ApplicationScoped : CDI crée une seule instance pour toute l’application. C’est indispensable ici.
//Par défaut, une ressource JAX-RS est recréée à chaque requête :
//si la Map était dans ProductResource, elle serait vide à chaque appel.
@ApplicationScoped
public class ProductRepository
{
    //ConcurrentHashMap et AtomicLong : contrairement à Node, qui a un seul thread,
    //Payara traite les requêtes en parallèle sur plusieurs threads.
    //Une HashMap classique ou un simple long id++ pourraient se corrompre si deux requêtes arrivaient en même temps.
    //Ces deux classes sont conçues pour ça.
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    // Called once by CDI after the bean is created: sample data
    //@PostConstruct : une méthode appelée automatiquement après la création du bean. Ici, elle insère des données de test.
    @PostConstruct
    void init() {
        create(new Product ("P001", "Vis 40x4", 500));
        create(new Product("P002", "Cheville 6mm", 1200));
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    public Product create (Product product) {

        if (referenceExists(product.getReference(), product.getId())) {
            throw new WebApplicationException(
                    Response.status(Response.Status.CONFLICT)
                            .entity(Map.of("error", "The reference " + product.getReference() + " already exists"))
                            .type(MediaType.APPLICATION_JSON)
                            .build());
        }

        product.setId(sequence.incrementAndGet());
        products.put(product.getId(), product);
        return product;
    }

    //Optional<Product> : une boîte qui contient un produit ou rien.
    //C’est plus explicite que de renvoyer null : l’appelant est obligé de traiter le cas “absent”.
    public Optional<Product> update(Long id, Product product) {
        if (!products.containsKey(id)) {
            return Optional.empty();
        }
        if (referenceExists(product.getReference(), id)) {
            throw new WebApplicationException(
                    Response.status(Response.Status.CONFLICT)
                            .entity(Map.of("error", "The reference " + product.getReference() + " already exists"))
                            .type(MediaType.APPLICATION_JSON)
                            .build());
        }
        product.setId(id);
        products.put(id, product);
        return Optional.of(product);
    }

    private boolean referenceExists(String reference, Long excludedId) {
        for (Product existing : products.values()) {
            if (existing.getReference().equalsIgnoreCase(reference)
                    && !existing.getId().equals(excludedId)) {
                return true;
            }
        }
        return false;
    }

    public boolean delete(Long id) {
        return products.remove(id) != null;
    }

    public synchronized Optional<Product> updateStock(Long id, int delta) {
        Product product = products.get(id);
        if (product == null) {
            return Optional.empty();
        }
        int newStock = product.getStock() + delta;
        if (newStock < 0) {
            throw new BadRequestException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity(Map.of("error", "Insufficient stock: " + product.getStock()
                                    + " available, " + (-delta) + " requested"))
                            .type(MediaType.APPLICATION_JSON)
                            .build());
        }
        product.setStock(newStock);
        return Optional.of(product);
    }
}
