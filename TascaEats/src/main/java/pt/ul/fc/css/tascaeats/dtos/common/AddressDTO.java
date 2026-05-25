package pt.ul.fc.css.tascaeats.dtos.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import pt.ul.fc.css.tascaeats.entities.Address;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AddressDTO(
    @NotBlank String city,
    @NotBlank String postalCode,
    @NotBlank String street) {

  public AddressDTO(Address address) {
    this(
        address.getCity(),
        address.getPostalCode(),
        address.getStreet());
  }

  public Address toAddress() {
    return new Address(city, postalCode, street);
  }
}
