package pt.ul.fc.css.tascaeats.common.dto;

import java.util.UUID;

public record OrderDto(UUID orderId, String street, String city, String postalCode) {}
