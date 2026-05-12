package pt.ul.fc.css.tascaeats.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

  private String city;
  private String postalCode;
  private String street;

  public Address() {} // obrigatório para JPA

  public Address(
      String city,
      String postalCode,
      String street) {
    this.city = city;
    this.postalCode = postalCode;
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getStreet() {
    return street;
  }
}
