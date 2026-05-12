package pt.ul.fc.css.tascaeats.dtos.menu;

import jakarta.validation.constraints.NotBlank;

public record CreateMenuDTO(
    @NotBlank String name
) {
}
