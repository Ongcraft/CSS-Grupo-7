package pt.ul.fc.css.tascaeats.dtos.user;

import jakarta.validation.Valid;
import pt.ul.fc.css.tascaeats.dtos.common.AddressDTO;

public record UpdateCustomerDTO(String name, String username, String password, @Valid AddressDTO address) {}
