package pt.ul.fc.css.tascaeats.dtos.product;

import jakarta.validation.constraints.Positive;
import pt.ul.fc.css.tascaeats.enums.FoodCategory;

import org.hibernate.validator.constraints.Length;

public record UpdateProductDTO(
    @Length(max = 200) String name,
    @Length(max = 1000) String description,
    @Positive Double price,
    Boolean available,
    FoodCategory category) {}
