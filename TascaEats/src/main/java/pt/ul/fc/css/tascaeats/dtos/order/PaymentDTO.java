package pt.ul.fc.css.tascaeats.dtos.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pt.ul.fc.css.tascaeats.enums.CardFlag;
import pt.ul.fc.css.tascaeats.enums.PaymentType;

public record PaymentDTO(
    @NotNull PaymentType paymentType,
    @NotNull @Positive Double amount,
    CardFlag cardFlag,
    String phoneNumber
    ) {}
