package pt.ul.fc.css.tascaeats.dtos.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import pt.ul.fc.css.tascaeats.dtos.common.AddressDTO;
import pt.ul.fc.css.tascaeats.dtos.restaurant.RestaurantDTO;
import pt.ul.fc.css.tascaeats.entities.Order;
import pt.ul.fc.css.tascaeats.entities.Product;
import pt.ul.fc.css.tascaeats.enums.OrderStatus;
import pt.ul.fc.css.tascaeats.enums.PaymentType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderDTO(
    UUID id,
    LocalDateTime createdAt,
    OrderStatus orderStatus,
    UUID customerId,
    AddressDTO address,
    List<OrderItemDTO> items,
    List<UUID> productIds,
    List<RestaurantDTO> restaurants,
    UUID courierId,
    double total,
    PaymentType paymentType,
    double amountPaid,
    double change) {

  public OrderDTO(Order order) {
    this(
        order.getId(),
        order.getCreatedAt(),
        order.getStatus(),
        order.getCustomer().getId(),
        new AddressDTO(order.getDeliveryAddress()),
        order.getOrderItems().stream().map(OrderItemDTO::new).toList(),
        order.getProducts().stream().map(Product::getId).toList(),
        getUniqueRestaurants(order),
        order.getCourier() != null ? order.getCourier().getId() : null,
        order.getTotal(),
        order.getPaymentType(),
        order.getAmountPaid(),
        order.getChange());
  }

  private static List<RestaurantDTO> getUniqueRestaurants(Order order) {
    Set<UUID> seenIds = new HashSet<>();
    return order.getRestaurant().stream()
        .filter(r -> seenIds.add(r.getId()))
        .map(RestaurantDTO::new)
        .toList();
  }
}
