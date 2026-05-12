package pt.ul.fc.css.tascaeats.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import pt.ul.fc.css.tascaeats.enums.Role;

@Entity
public class Courier extends User {

  @Column(nullable = false)
  private boolean availability = true;

  private int deliveryCount;

  public Courier() {}

  public Courier(String name, String username, String password) {
    this.name = name;
    this.username = username;
    this.password = password;
    this.role = Role.COURIER;
    this.availability = true;
    this.deliveryCount = 0;
  }

  public boolean isAvailable() {
    return this.availability;
  }

  public void setAvailable() {
    this.availability = true;
  }

  public void setUnavailable() {
    this.availability = false;
  }

  public int getDeliveryCount() {
    return deliveryCount;
  }

  public void incrementDeliveryCount() {
    this.deliveryCount++;
  }
}
