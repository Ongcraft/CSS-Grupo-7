package pt.ul.fc.css.tascaeats.dtos.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pt.ul.fc.css.tascaeats.dtos.common.AddressDTO;

public record CreateCustomerDTO(
    @NotBlank String name, @NotBlank String username, @NotBlank String password, @NotNull @Valid AddressDTO address) {}
