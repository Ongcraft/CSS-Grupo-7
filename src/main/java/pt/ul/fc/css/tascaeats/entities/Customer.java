package pt.ul.fc.css.tascaeats.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import pt.ul.fc.css.tascaeats.enums.Role;

@Entity
public class Customer extends User {

  @ElementCollection
  private List<Address> addresses = new ArrayList<>();

  private int orderCount;

  @ManyToMany
  private List<Restaurant> restaurantsToRate = new ArrayList<>();

  public Customer() {}

  public Customer(String name, String username, String password, Address address) {
    this.name = name;
    this.username = username;
    this.password = password;
    this.role = Role.CUSTOMER;
    this.addresses.add(address);
    this.orderCount = 0;
  }

  public List<Address> getAddresses() {
    return List.copyOf(addresses);
  }

  public void addAddress(Address address) {
    this.addresses.add(address);
  }

  public void removeAddress(Address address) {
    this.addresses.remove(address);
  }

  public int getOrderCount() {
    return orderCount;
  }

  public void incrementOrderCount() {
    this.orderCount++;
  }

  public List<Restaurant> getRestaurantsToRate() {
    return List.copyOf(restaurantsToRate);
  }

  public void addRestaurantToRate(Restaurant r) {
    if (!restaurantsToRate.contains(r)) {
      restaurantsToRate.add(r);
    }
  }

  public void removeRestaurantToRate(Restaurant r) {
    restaurantsToRate.remove(r);
  }

  public boolean canRateRestaurant(Restaurant r) {
    return restaurantsToRate.contains(r);
  }
}
