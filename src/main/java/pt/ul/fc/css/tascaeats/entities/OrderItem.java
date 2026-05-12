package pt.ul.fc.css.tascaeats.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import pt.ul.fc.css.tascaeats.exception.InvalidInputException;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    private int quantity;

    private double price;

    private LocalDateTime createdAt;

    protected OrderItem() {}

    public OrderItem(Order order, Restaurant restaurant, Product product, int quantity, double price) {

        if (order == null) throw new InvalidInputException("Order cannot be null");

        if (restaurant == null) throw new InvalidInputException("Restaurant cannot be null");

        if (product == null) throw new InvalidInputException("Product cannot be null");

        if (quantity <= 0) throw new InvalidInputException("Quantity must be greater than zero");
        
        if (price <= 0) throw new InvalidInputException("Price cannot be negative or zero");

        this.order = order;
        this.product = product;
        this.restaurant = restaurant;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Product getProduct() {
        return product;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalPrice() {
        return price * quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public boolean removeQuantity(int quantity) {
        if (quantity > this.quantity) return false;
        this.quantity -= quantity;
        return true;
    }
}
