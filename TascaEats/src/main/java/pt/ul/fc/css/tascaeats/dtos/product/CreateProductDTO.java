package pt.ul.fc.css.tascaeats.dtos.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pt.ul.fc.css.tascaeats.enums.FoodCategory;

import org.hibernate.validator.constraints.Length;

public record CreateProductDTO(
    @Length(max = 200) @NotBlank String name,
    @Length(max = 1000) String description,
    @NotNull @Positive Double price,
    Boolean available,
    FoodCategory category) {}
