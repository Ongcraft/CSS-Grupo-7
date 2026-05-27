package pt.ul.fc.css.tascaeats.delivery.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "couriers")
public class Courier {

  @Id
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private boolean available = true;

  protected Courier() {}

  public Courier(UUID id, String name, String username) {
    this.id = id;
    this.name = name;
    this.username = username;
    this.available = true;
  }

  public UUID getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }

  public String getUsername() {
      return username;
  }

  public boolean isAvailable() {
      return available;
  }

  public void setAvailable(boolean available) {
      this.available = available;
  }

  public void update(String name, String username) {
    this.name = name;
    this.username = username;
  }
}
