package fr.guillaume.minierp.order;

import fr.guillaume.minierp.product.Product;
import fr.guillaume.minierp.product.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class OrderService {

    @PersistenceContext(unitName = "erpPU")
    private EntityManager em;

    public List<CustomerOrder> findAll() {
        // JOIN FETCH: loads orders and their lines in a single query
        return em.createQuery(
                        "SELECT DISTINCT o FROM CustomerOrder o LEFT JOIN FETCH o.lines ORDER BY o.id",
                        CustomerOrder.class)
                .getResultList();
    }

    public Optional<CustomerOrder> findById(Long id) {
        return em.createQuery(
                        "SELECT o FROM CustomerOrder o LEFT JOIN FETCH o.lines WHERE o.id = :id",
                        CustomerOrder.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Transactional
    public CustomerOrder create(CreateOrderRequest request) {
        if (request.lines() == null || request.lines().isEmpty()) {
            throw badRequest("An order needs at least one line");
        }
        CustomerOrder order = new CustomerOrder();
        order.setReference(request.reference());
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.DRAFT);
        for (CreateOrderRequest.LineRequest lineRequest : request.lines()) {
            Product product = em.find(Product.class, lineRequest.productId());
            if (product == null) {
                throw badRequest("Product " + lineRequest.productId() + " not found");
            }
            OrderLine line = new OrderLine();
            line.setProduct(product);
            line.setQuantity(lineRequest.quantity());
            line.setUnitPrice(product.getPrice());
            order.addLine(line);
        }
        // Cascade: persisting the order also persists its lines
        em.persist(order);
        return order;
    }

    @Inject
    private ProductRepository productRepository;

    @Transactional
    public Optional<CustomerOrder> confirm(Long id) {
        CustomerOrder order = em.find(CustomerOrder.class, id);
        if (order == null) {
            return Optional.empty();
        }
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new WebApplicationException(Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("error", "Order " + order.getReference() + " is already " + order.getStatus()))
                    .type(MediaType.APPLICATION_JSON)
                    .build());
        }
        // Reuses the stock rule: lock the row, refuse a negative stock
        for (OrderLine line : order.getLines()) {
            productRepository.updateStock(line.getProduct().getId(), -line.getQuantity());
        }
        order.setStatus(OrderStatus.CONFIRMED);
        return Optional.of(order);
    }

    private BadRequestException badRequest(String message) {
        return new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", message))
                .type(MediaType.APPLICATION_JSON)
                .build());
    }
}