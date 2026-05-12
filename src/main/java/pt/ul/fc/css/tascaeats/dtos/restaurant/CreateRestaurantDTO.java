package pt.ul.fc.css.tascaeats.dtos.restaurant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import pt.ul.fc.css.tascaeats.dtos.common.AddressDTO;
import pt.ul.fc.css.tascaeats.enums.KitchenType;

public record CreateRestaurantDTO(
    @NotBlank String name,
    @NotBlank @Pattern(regexp = "^\\d{9}$") String nif,
    @Valid AddressDTO address,
    @NotBlank KitchenType kitchenType) {}
