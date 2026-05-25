package pt.ul.fc.css.tascaeats.dtos.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import pt.ul.fc.css.tascaeats.dtos.common.AddressDTO;

public record CreateOrderDTO(
    // @NotNull UUID restaurantId,
    @NotNull UUID customerId,
    @NotNull @Valid AddressDTO address,
    @NotNull @Valid PaymentInfoDTO payment) {}