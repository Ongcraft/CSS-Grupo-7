package pt.ul.fc.css.tascaeats.common.dto;

import java.util.UUID;

public record DeliveryEventDto(UUID orderId, UUID courierId) {}
