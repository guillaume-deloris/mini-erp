package fr.guillaume.minierp.product;

public class Product {
    private Long id;
    private String reference;
    private String name;
    private int stock;

    public Product() {
    }

    public Product(String reference, String name, int stock) {
        this.reference = reference;
        this.name = name;
        this.stock = stock;
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
}

