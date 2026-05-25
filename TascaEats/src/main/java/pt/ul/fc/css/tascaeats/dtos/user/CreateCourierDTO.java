package pt.ul.fc.css.tascaeats.dtos.user;

import jakarta.validation.constraints.NotBlank;

public record CreateCourierDTO(@NotBlank String name, @NotBlank String username, @NotBlank String password) {}
