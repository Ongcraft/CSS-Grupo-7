package pt.ul.fc.css.tascaeats.dtos.order;

import jakarta.validation.constraints.NotNull;
import pt.ul.fc.css.tascaeats.enums.CardFlag;
import pt.ul.fc.css.tascaeats.enums.PaymentType;

public record PaymentInfoDTO(
    @NotNull PaymentType paymentType,
    CardFlag cardFlag,
    String phoneNumber
) {}
