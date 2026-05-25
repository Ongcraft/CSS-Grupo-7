package pt.ul.fc.css.tascaeats.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import pt.ul.fc.css.tascaeats.enums.FoodCategory;

// import jakarta.persistence.Table;
// import jakarta.persistence.UniqueConstraint;
import java.util.UUID;

/**
 * Represents a product available in a restaurant menu.
 *
 * <p>A product has a name, description, price, and availability state. Each product belongs to
 * exactly one restaurant.
 */
@Entity
// @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"restaurant_id", "name"}))
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String name;

  private String description;

  private double price;

  private boolean available = true;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FoodCategory category;

  private int popularity = 0;

  // @ManyToOne(optional = true)
  // @JoinColumn(name = "restaurant_id", nullable = false)
  // private Restaurant restaurant;

  /** Default constructor required by JPA. */
  protected Product() {}

  /**
   * Creates a new Product.
   *
   * @param name product name
   * @param description product description
   * @param price product price (must be >= 0)
   * @throws IllegalArgumentException if name is invalid or price is negative
   */
  public Product(String name, String description, double price, FoodCategory category) {

    if (name == null || name.isBlank())
      throw new IllegalArgumentException("Name cannot be null or empty");

    if (price < 0) throw new IllegalArgumentException("Price cannot be negative");

    this.name = name;
    this.description = description;
    this.price = price;
    this.category = category;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public double getPrice() {
    return price;
  }

  public boolean isAvailable() {
    return available;
  }

  public FoodCategory getCategory() {
    return category;
  }

  public int getPopularity() {
    return popularity;
  }
  
  public void setPopularity(Integer popularity) {
    this.popularity = popularity;
  }

  // public Restaurant getRestaurant() {    
  //   return restaurant;
  // }

  /** Marks the product as unavailable. */
  public void deactivate() {
    this.available = false;
  }

  /** Marks the product as available. */
  public void activate() {
    this.available = true;
  }

  /** Sets the restaurant (used internally to maintain relationships). */
  // public void setRestaurant(Restaurant restaurant) {
  //   this.restaurant = restaurant;
  // }

  public void update(String name, String description, Double price, Boolean available, FoodCategory category) {
    if (name != null) this.name = name;

    if (description != null) this.description = description;

    if (price != null) this.price = price;

    if (available != null) this.available = available;

    if (category != null) this.category = category;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Product)) return false;
    Product that = (Product) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
