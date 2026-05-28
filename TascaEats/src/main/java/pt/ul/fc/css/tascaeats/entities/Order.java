package pt.ul.fc.css.tascaeats.entities;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import pt.ul.fc.css.tascaeats.enums.OrderStatus;
import pt.ul.fc.css.tascaeats.enums.PaymentType;
import pt.ul.fc.css.tascaeats.exception.BusinessRuleException;
import pt.ul.fc.css.tascaeats.exception.InvalidInputException;

/**
 * Represents a customer order.
 *
 * <p>An order contains products, belongs to a restaurant and a customer, and goes through several
 * status transitions (CREATED → PAID → PREPARING → READY → DELIVERING → DELIVERED).
 */
@Entity
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private LocalDateTime createdAt;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  @ManyToOne private Customer customer;

  @Embedded private Address address;

  // @OneToOne private Restaurant restaurant;

  // @ManyToMany private List<Product> products = new ArrayList<>();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> orderItems = new ArrayList<>();

  @ManyToOne private Courier courier;

  private double total;

  @OneToOne(cascade = CascadeType.ALL)
  private Payment payment;

  private double amountPaid;

  /** Default constructor required by JPA. */
  protected Order() {}

  /** Creates a new order in CREATED state. */
  public Order(Payment payment, Customer customer, Address address) {

    // if (restaurant == null) throw new InvalidInputException("Restaurant must be provided");

    if (customer == null) throw new InvalidInputException("Customer must be provided");

    if (address == null) throw new InvalidInputException("Delivery address must be provided");

    if (payment == null) throw new InvalidInputException("Payment must be provided");

    // this.restaurant = restaurant;
    this.payment = payment;
    this.customer = customer;
    this.address = address;
    this.createdAt = LocalDateTime.now();
    this.status = OrderStatus.CREATED;
  }

  public UUID getId() {
    return id;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public Customer getCustomer() {
    return customer;
  }

  public Address getDeliveryAddress() {
    return address;
  }

  public List<Restaurant> getRestaurant() {
    return orderItems.stream().map(OrderItem::getRestaurant).toList();
  }

  public List<Product> getProducts() {
    return List.copyOf(orderItems.stream().map(OrderItem::getProduct).toList());
  }

  public List<OrderItem> getOrderItems() {
      return List.copyOf(orderItems);
  }

  public Courier getCourier() {
    return courier;
  }

  public double getTotal() {
    return total;
  }

  public PaymentType getPaymentType() {
    return payment != null ? payment.getPaymentType() : null;
  }

  public Payment getPayment() {
    return payment;
  }

  public double getAmountPaid() {
    return amountPaid;
  }

  public double getChange() {
    if (payment == null || payment.getPaymentType() != PaymentType.CASH) return 0.0;
    return ((CashPayment) payment).getChange();
  }

  /** Adds a product to the order. */
  public void addProduct(Product product, Restaurant restaurant, int quantity) {
    if (status != OrderStatus.CREATED)
      throw new BusinessRuleException(
          "Cannot add products to an order that is not in CREATED state");

    if (product == null) throw new InvalidInputException("Product cannot be null");

    if (!product.isAvailable()) throw new BusinessRuleException("Product is not available");

    // if (!product.getRestaurant().isOpen()) throw new BusinessRuleException("Restaurant is closed"); <---- has to check if the restaurant of the product is open

    if (!this.isProductInOrder(product)) {
      // se o produto não estiver no pedido, adiciona um novo item
      orderItems.add(new OrderItem(this, restaurant, product, quantity, product.getPrice()));      
    } else {
      // procura o item correspondente ao produto e adiciona a quantidade
      orderItems.stream().filter(item -> item.getProduct().equals(product)).findFirst().ifPresent(item -> {
        item.addQuantity(quantity);
      });
    }

    recalculateTotal();
  }

  /** Removes a product from the order. */
  public void removeProduct(Product product, int quantity) {
    if (status != OrderStatus.CREATED)
      throw new BusinessRuleException(
          "Cannot remove products from an order that is not in CREATED state");
    
    if (!this.isProductInOrder(product))
      throw new BusinessRuleException("Product is not part of the order");

    // se o produto estiver no pedido, procura o item correspondente e remove a quantidade se a quantidade for suficiente, caso contrário elimina o item completamente
    Iterator<OrderItem> it = orderItems.iterator();

    while (it.hasNext()) {
        OrderItem item = it.next();

        if (item.getProduct().equals(product)) {
            boolean stillExists = item.removeQuantity(quantity);

            if (!stillExists) {
                it.remove();
            }
            break;
        }
    }

    recalculateTotal();
  }

  private boolean isProductInOrder(Product product) {
    return orderItems.stream().anyMatch(item -> item.getProduct().equals(product));
  }

  private void recalculateTotal() {
    this.total = this.orderItems.stream().mapToDouble(OrderItem::getTotalPrice).sum();
  }

  /** Pays for the order. */
  public double payOrder(Payment payment, double amount) {
    if (status != OrderStatus.CREATED)
      throw new BusinessRuleException("Order cannot be paid in its current state");

    if (payment == null) throw new InvalidInputException("Payment must be provided");

    if (payment.getPaymentType() != this.payment.getPaymentType())
      throw new InvalidInputException("Payment type does not match the one specified at order creation");

    switch (payment.getPaymentType()) {
      case MULTIBANCO:
        if (!((MultibancoPayment) payment).getCardFlag().equals(((MultibancoPayment) this.payment).getCardFlag()))
          throw new InvalidInputException("Card flag does not match the one specified at order creation");

        if (amount != total)
          throw new InvalidInputException("Payment amount must be exactly the total for that type of payment");
        break;
      case MBWAY:
        if (!((MBWayPayment) payment).getPhoneNumber().equals(((MBWayPayment) this.payment).getPhoneNumber()))
          throw new InvalidInputException("Phone number does not match the one specified at order creation");

        if (amount != total)
          throw new InvalidInputException("Payment amount must be exactly the total for that type of payment");
        break;
      case CASH:
        if (amount < total)
          throw new InvalidInputException("Insufficient payment amount");
        break;
    }

    this.payment = payment;
    this.amountPaid = amount;
    double change = amount - total;
    if (payment instanceof CashPayment) {
      ((CashPayment) payment).setChange(change);
    }
    this.status = OrderStatus.PAID;
    return change;
  }

  /** Starts preparing the order. */
  public void startPreparation() {
    if (status != OrderStatus.PAID)
      throw new BusinessRuleException("Order must be paid before preparation");

    this.status = OrderStatus.PREPARING;
  }

  /** Marks the order as ready. */
  public void markAsReady() {
    if (status != OrderStatus.PREPARING)
      throw new BusinessRuleException("Order is not being prepared");

    this.status = OrderStatus.READY;
  }

  /** Assigns a courier to the order. */
  public void assignCourier(Courier courier) {
    if (status != OrderStatus.READY)
      throw new BusinessRuleException("Order must be ready before assigning courier");

    this.courier = courier;
  }

  /** Starts delivery. */
  public void startDelivery() {
    if (status != OrderStatus.READY)
      throw new BusinessRuleException("Order is not ready for delivery");

    if (courier == null)
      throw new BusinessRuleException("Courier must be assigned before starting delivery");

    this.status = OrderStatus.DELIVERING;
  }

  /** Marks the order as delivered. */
  public void completeDelivery() {
    if (status != OrderStatus.DELIVERING)
      throw new BusinessRuleException("Order is not currently being delivered");

    this.status = OrderStatus.DELIVERED;

    for (Restaurant r : getRestaurant()) {
      customer.addRestaurantToRate(r);
    }
  }

  /** Cancels the order. */
  public void cancel() {
    if (status != OrderStatus.CREATED && status != OrderStatus.PAID)
      throw new BusinessRuleException("Only orders in CREATED or PAID state can be cancelled");

    this.status = OrderStatus.CANCELLED;
  }
}
