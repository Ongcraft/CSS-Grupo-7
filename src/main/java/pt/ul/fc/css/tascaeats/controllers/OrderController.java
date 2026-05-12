package pt.ul.fc.css.tascaeats.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ul.fc.css.tascaeats.dtos.order.CreateOrderDTO;
import pt.ul.fc.css.tascaeats.dtos.order.OrderDTO;
import pt.ul.fc.css.tascaeats.dtos.order.PaymentDTO;
import pt.ul.fc.css.tascaeats.dtos.order.AddProductDTO;
import pt.ul.fc.css.tascaeats.dtos.order.RemoveProductDTO;
import pt.ul.fc.css.tascaeats.entities.Order;
import pt.ul.fc.css.tascaeats.services.OrderService;

@Validated
@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Operations related to orders lifecycle")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  // -------------------- CREATE --------------------

  @PostMapping()
  @Operation(summary = "Create a new order")
  public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderDTO dto) {
    Order created = orderService.createOrder(dto);
    OrderDTO res = new OrderDTO(created);
    return ResponseEntity.created(URI.create("/orders/" + created.getId())).body(res);
  }

  // -------------------- READ --------------------

  @GetMapping("/{orderId}")
  @Operation(summary = "Get order by ID")
  public ResponseEntity<OrderDTO> getOrderById(@PathVariable UUID orderId) {
    Order found = orderService.getOrderById(orderId);
    OrderDTO res = new OrderDTO(found);
    return ResponseEntity.ok(res);
  }

  @GetMapping
  @Operation(summary = "Get all orders")
  public ResponseEntity<List<OrderDTO>> getAllOrders() {
    List<Order> results = orderService.getAllOrders();
    List<OrderDTO> dtoList = results.stream().map(OrderDTO::new).toList();
    return ResponseEntity.ok(dtoList);
  }

  // -------------------- ORDER MODIFICATION --------------------

  @PostMapping("/{orderId}/products")
  @Operation(summary = "Add product to order")
  public ResponseEntity<OrderDTO> addProduct(
      @PathVariable UUID orderId, @RequestParam UUID restaurantId, @RequestBody AddProductDTO dto) {
    Order order = orderService.addProduct(orderId, restaurantId, dto);
    OrderDTO res = new OrderDTO(order);
    return ResponseEntity.ok(res);
  }

  @DeleteMapping("/{orderId}/products")
  @Operation(summary = "Remove product from order")
  public ResponseEntity<OrderDTO> removeProduct(
      @PathVariable UUID orderId, @RequestBody RemoveProductDTO dto) {
    Order order = orderService.removeProduct(orderId, dto);
    OrderDTO res = new OrderDTO(order);
    return ResponseEntity.ok(res);
  }

  // -------------------- STATE TRANSITIONS --------------------

  @PatchMapping("/{orderId}/pay")
  @Operation(summary = "Pay for an order")
  public ResponseEntity<OrderDTO> payOrder(
      @PathVariable UUID orderId, @Valid @RequestBody PaymentDTO dto) {
    Order order = orderService.payOrder(orderId, dto);
    OrderDTO res = new OrderDTO(order);
    return ResponseEntity.ok(res);
  }

  @PatchMapping("/{orderId}/prepare")
  @Operation(summary = "Start order preparation")
  public ResponseEntity<OrderDTO> prepareOrder(@PathVariable UUID orderId) {
    Order order = orderService.prepareOrder(orderId);
    OrderDTO res = new OrderDTO(order);
    return ResponseEntity.ok(res);
  }

  @PatchMapping("/{orderId}/ready")
  @Operation(summary = "Mark order as ready")
  public ResponseEntity<OrderDTO> markOrderReady(@PathVariable UUID orderId) {
    Order order = orderService.markOrderReady(orderId);
    OrderDTO res = new OrderDTO(order);
    return ResponseEntity.ok(res);
  }

  @PatchMapping("/{orderId}/assign") // /{courierId}
  @Operation(summary = "Assign delivery person to order")
  public ResponseEntity<OrderDTO> assignCourier(
      @PathVariable UUID orderId) { // tirar courierId do path , @PathVariable UUID courierId
    Order order = orderService.assignCourier(orderId);
    OrderDTO res = new OrderDTO(order);
    return ResponseEntity.ok(res);
  }

  @PatchMapping("/{orderId}/start-delivery")
  @Operation(summary = "Start delivery")
  public ResponseEntity<OrderDTO> startDelivery(@PathVariable UUID orderId) {
    Order order = orderService.startDelivery(orderId);
    OrderDTO res = new OrderDTO(order);
    return ResponseEntity.ok(res);
  }

  @PatchMapping("/{orderId}/delivered")
  @Operation(summary = "Mark order as delivered")
  public ResponseEntity<OrderDTO> completeDelivey(@PathVariable UUID orderId) {
    Order order = orderService.completeDelivery(orderId);
    OrderDTO res = new OrderDTO(order);
    return ResponseEntity.ok(res);
  }

  @PatchMapping("/{orderId}/cancel")
  @Operation(summary = "Cancel order")
  public ResponseEntity<OrderDTO> cancelOrder(@PathVariable UUID orderId) {
    Order order = orderService.cancelOrder(orderId);
    OrderDTO res = new OrderDTO(order);
    return ResponseEntity.ok(res);
  }
}
