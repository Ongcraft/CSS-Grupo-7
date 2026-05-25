package pt.ul.fc.css.tascaeats.dtos.user;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.UUID;
import pt.ul.fc.css.tascaeats.dtos.common.AddressDTO;
import pt.ul.fc.css.tascaeats.entities.Admin;
import pt.ul.fc.css.tascaeats.entities.Courier;
import pt.ul.fc.css.tascaeats.entities.Customer;
import pt.ul.fc.css.tascaeats.entities.User;
import pt.ul.fc.css.tascaeats.enums.Role;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDTO(UUID id, String username, String name, Role role, Boolean availability, List<AddressDTO> addresses) {

  public UserDTO(Admin admin) {
    this(
      admin.getId(), 
      admin.getUsername(), 
      admin.getName(), 
      Role.ADMIN, 
      null, 
      null);
  }

  public UserDTO(Customer customer) {
    this(
        customer.getId(),
        customer.getUsername(),
        customer.getName(),
        Role.CUSTOMER,
        null,
        customer.getAddresses().isEmpty() ? null : customer.getAddresses().stream().map(AddressDTO::new).toList());
  }

  public UserDTO(Courier courier) {
    this(
      courier.getId(), 
      courier.getUsername(),
      courier.getName(), 
      Role.COURIER, 
      courier.isAvailable(), 
      null);
  }

  public static UserDTO from(User user) {
    return switch (user.getRole()) {
      case ADMIN -> new UserDTO((Admin) user);
      case CUSTOMER -> new UserDTO((Customer) user);
      case COURIER -> new UserDTO((Courier) user);
    };
  }
}
