package fr.guillaume.minierp.product;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * A product of the catalog, mapped to the "product" table.
 */
@Entity //announces a table
@Table(name = "product") //the name of the table
public class Product {
    // BIGSERIAL in PostgreSQL: the database generates the id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Primary key
    private Long id;
    private String reference;
    private String name;
    private int stock;
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    public Product() {
    }

    public Product(String reference, String name, int stock, BigDecimal price) {
        this.reference = reference;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}

