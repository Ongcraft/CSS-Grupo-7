package pt.ul.fc.css.tascaeats.dtos.order;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AddProductDTO {
    @NotNull public UUID productId;
    @NotNull @Positive public Integer quantity;
}
