package pt.ul.fc.css.tascaeats.dtos.order;

import java.util.UUID;

import pt.ul.fc.css.tascaeats.entities.OrderItem;

public record OrderItemDTO(
    UUID productId,
    String productName,
    int quantity,
    double totalPrice,
    String restaurantId
) {
    public OrderItemDTO(OrderItem item) {
        this(
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getQuantity(),
            item.getTotalPrice(),
            item.getRestaurant().getId().toString()
        );
    }
}
