package fr.guillaume.minierp.product;

import fr.guillaume.minierp.common.DuplicateReferenceException;
import fr.guillaume.minierp.common.InsufficientStockException;
import fr.guillaume.minierp.common.NegativePriceException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Product storage in PostgreSQL, through JPA.
 */
@ApplicationScoped
public class ProductRepository {

    // Injected by Payara, linked to jdbc/erp through persistence.xml
    @PersistenceContext(unitName = "erpPU")
    private EntityManager em;

    public List<Product> findAll() {
        return em.createQuery("SELECT p FROM Product p ORDER BY p.id", Product.class)
                .getResultList();
    }

    public List<Product> findByName(String name) {
        return em.createQuery(
                        "SELECT p FROM Product p WHERE LOWER(p.name) LIKE :pattern ORDER BY p.id", Product.class)
                .setParameter("pattern", "%" + name.toLowerCase() + "%")
                .getResultList();
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(em.find(Product.class, id));
    }

    @Transactional
    public Product create(Product product) {
        if (referenceExists(product.getReference(), null)) {
            //throw error(Response.Status.CONFLICT, "The reference " + product.getReference() + " already exists");
            throw new DuplicateReferenceException(product.getReference());
        }
        if (negativePrice(product.getPrice())) {
            //throw error(Response.Status.BAD_REQUEST, "The price must be a positive number");
            throw new NegativePriceException("The price must be a positive number");

        }
        em.persist(product);
        // Sends the INSERT now: a constraint violation is raised here, inside the method
        em.flush();
        return product;
    }

    @Transactional
    public Optional<Product> update(Long id, Product data) {
        Product product = em.find(Product.class, id);
        if (product == null) {
            return Optional.empty();
        }
        if (referenceExists(data.getReference(), id)) {
            //throw error(Response.Status.CONFLICT, "The reference " + data.getReference() + " already exists");
            throw new DuplicateReferenceException("The reference " + data.getReference() + " already exists");
        }
        if (negativePrice(data.getPrice())) {
            //throw error(Response.Status.BAD_REQUEST, "The price must be a positive number");
            throw new NegativePriceException("The price must be a positive number");

        }
        // The product is "managed": changes are saved automatically at commit
        product.setReference(data.getReference());
        product.setName(data.getName());
        product.setStock(data.getStock());
        product.setPrice(data.getPrice());
        em.flush();
        return Optional.of(product);
    }

    @Transactional
    public boolean delete(Long id) {
        Product product = em.find(Product.class, id);
        if (product == null) {
            return false;
        }
        em.remove(product);
        return true;
    }

    @Transactional
    public Optional<Product> updateStock(Long id, int delta) {
        // PESSIMISTIC_WRITE: SELECT ... FOR UPDATE, the row is locked until commit
        Product product = em.find(Product.class, id, LockModeType.PESSIMISTIC_WRITE);
        if (product == null) {
            return Optional.empty();
        }
        int newStock = product.getStock() + delta;

        if (newStock < 0) {
            /*throw error(Response.Status.BAD_REQUEST, "Insufficient stock for product " + product.getReference()
                    + " (" + product.getName() + "): " + product.getStock()
                    + " available, " + (-delta) + " requested");*/
            throw new InsufficientStockException(product.getReference(), product.getStock(), -delta);
        }


        product.setStock(newStock);
        em.flush();
        return Optional.of(product);
    }

    /**
     * Checks whether another product already uses this reference.
     * excludedId: id of the product being updated, null for a creation.
     */
    private boolean referenceExists(String reference, Long excludedId) {
        List<Product> found = em.createQuery(
                        "SELECT p FROM Product p WHERE LOWER(p.reference) = LOWER(:reference)", Product.class)
                .setParameter("reference", reference)
                .getResultList();
        return found.stream().anyMatch(p -> !p.getId().equals(excludedId));
        //return false;
    }

    /*private WebApplicationException error(Response.Status status, String message) {
        return new WebApplicationException(Response.status(status)
                .entity(Map.of("error", message))
                .type(MediaType.APPLICATION_JSON)
                .build());
    }*/

    private boolean negativePrice(BigDecimal price) {
        return price.compareTo(BigDecimal.ZERO) < 0;
    }
}