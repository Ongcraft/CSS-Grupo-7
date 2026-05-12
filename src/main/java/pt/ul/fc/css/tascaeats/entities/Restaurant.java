package pt.ul.fc.css.tascaeats.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import pt.ul.fc.css.tascaeats.enums.KitchenType;

import java.util.List;
import java.util.UUID;

/**
 * Represents a restaurant in the system.
 *
 * <p>A restaurant has a unique NIF, a name, an address, and a menu composed of products. It can be
 * open or closed for receiving orders.
 */
@Entity
public class Restaurant {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  double rating;
  int nRatings;

  private String name;

  @Column(unique = true, nullable = false)
  private String nif;

  @Embedded private Address address;

  private boolean open;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private KitchenType kitchenType;

  // @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
  // private Set<Product> products = new HashSet<>();

  @ManyToOne private Menu menu;

  /** Default constructor required by JPA. */
  protected Restaurant() {}

  /**
   * Creates a new Restaurant.
   *
   * @param nif unique tax identification number
   * @param name restaurant name
   * @param address restaurant address
   * @param open initial open state
   * @throws IllegalArgumentException if any required field is invalid
   */
  public Restaurant(String nif, String name, Address address, KitchenType kitchenType, boolean open) {

    if (nif == null || nif.isBlank())
      throw new IllegalArgumentException("NIF cannot be null or empty");

    if (name == null || name.isBlank())
      throw new IllegalArgumentException("Name cannot be null or empty");

    if (address == null) throw new IllegalArgumentException("Address cannot be null");

    if (kitchenType == null)
      throw new IllegalArgumentException("Kitchen type cannot be null");

    this.kitchenType = kitchenType;
    this.rating = 0.0;
    this.nRatings = 0;
    this.nif = nif;
    this.name = name;
    this.address = address;
    this.open = open;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getNif() {
    return nif;
  }

  public Address getAddress() {
    return address;
  }

  public boolean isOpen() {
    return open;
  }

  public String getKitchenType() {
    return kitchenType.toString();
  }

  /** Opens the restaurant for receiving orders. */
  public void open() {
    this.open = true;
  }

  /** Closes the restaurant, preventing new orders. */
  public void close() {
    this.open = false;
  }

  /**
   * Adds a product to the restaurant menu. Maintains bidirectional consistency.
   *
   * @param product product to add
   * @throws IllegalArgumentException if product is null
   */
  public void addProduct(Product product) {
    if (product == null) throw new IllegalArgumentException("Product cannot be null");
    if (this.menu == null) throw new IllegalArgumentException("Menu cannot be null");
    
    this.menu.addProduct(product);
    // products.add(product);
    // product.setRestaurant(this);
  }

  /**
   * Removes a product from the restaurant menu. Maintains bidirectional consistency.
   *
   * @param product product to remove
   * @throws IllegalArgumentException if product is null
   */
  public void removeProduct(Product product) {
    if (product == null) throw new IllegalArgumentException("Product cannot be null");
    if (this.menu == null) throw new IllegalArgumentException("Menu cannot be null");
    
    this.menu.removeProduct(product);
    // products.remove(product);
    // product.setRestaurant(null);
  }

  

  /**
   * Set the Menu to the restaurant.
   *
   * @param menu menu to add
   * @throws IllegalArgumentException if menu is null
   */
  public void setMenu(Menu menu) {
    if (menu == null) throw new IllegalArgumentException("Menu cannot be null");

    this.menu = menu;
  }

  public Menu getMenu() {
      return this.menu;
  }

  public double rateRestaurant(double rating) {
    if (rating < 0.0 || rating > 5.0) throw new IllegalArgumentException("Rating should be in the rating of 0.0 to 5.0");

    this.rating = ((this.rating * nRatings) + rating) / (nRatings + 1);
    nRatings++;

    return this.rating;
  }

  public double getRating() {
    return this.rating;
  }

  public int getNRatings() {
    return this.nRatings;
  }

  public double getAveragePrice() {
    if (this.menu == null) throw new IllegalArgumentException("Menu cannot be null");

    List<Product> products = this.menu.getProducts();
    if (products.isEmpty()) return 0.0;

    double total = products.stream().mapToDouble(Product::getPrice).sum();
    return total / products.size();
  }

  /** Returns all available products. */
  public List<Product> getAvailableProducts() {
    if (this.menu == null) throw new IllegalArgumentException("Menu cannot be null");

    List<Product> products = this.menu.getProducts();
    return products.stream().filter(Product::isAvailable).toList();
  }

  /** Returns all products (immutable copy). */
  public List<Product> getAllProducts() {
    if (this.menu == null) throw new IllegalArgumentException("Menu cannot be null");

    List<Product> products = this.menu.getProducts();
    return List.copyOf(products);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Restaurant)) return false;
    Restaurant that = (Restaurant) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
