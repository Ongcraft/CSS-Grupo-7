package pt.ul.fc.css.tascaeats.delivery.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import pt.ul.fc.css.tascaeats.delivery.enums.DeliveryStatus;

@Entity
@Table(name = "orders")
public class Order {

  @Id
  private UUID id;

  @Column(nullable = false)
  private String street;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  private String postalCode;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DeliveryStatus status = DeliveryStatus.READY;

  @ManyToOne
  private Courier courier;

  protected Order() {}

  public Order(UUID id, String street, String city, String postalCode) {
      this.id = id;
      this.street = street;
      this.city = city;
      this.postalCode = postalCode;
      this.status = DeliveryStatus.READY;
  }

  public UUID getId() { 
    return id; 
  }

  public String getStreet() { 
    return street; 
  }

  public String getCity() { 
    return city; 
  }

  public String getPostalCode() { 
    return postalCode; 
  }

  public DeliveryStatus getStatus() { 
    return status; 
  }

  public Courier getCourier() { 
    return courier; 
  }

  public void assignCourier(Courier courier) {
    this.courier = courier;
  }

  public void startDelivery() {
    this.status = DeliveryStatus.DELIVERING;
  }

  public void completeDelivery() {
    this.status = DeliveryStatus.DELIVERED;
  }
}
