package pt.ul.fc.css.tascaeats.dtos.user;

import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import java.util.UUID;

public record LoginUserDTO(
    @NotBlank String username, 
    @NotBlank String userPassword) {}
