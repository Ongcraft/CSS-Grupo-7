package pt.ul.fc.css.tascaeats.delivery.controller;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ul.fc.css.tascaeats.delivery.service.DeliveryService;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private final DeliveryService deliveryService;

  public OrderController(DeliveryService deliveryService) {
    this.deliveryService = deliveryService;
  }

  @PostMapping("/{id}/start")
  public ResponseEntity<Void> startDelivery(@PathVariable UUID id) {
    deliveryService.startDelivery(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{id}/complete")
  public ResponseEntity<Void> completeDelivery(@PathVariable UUID id) {
    deliveryService.completeDelivery(id);
    return ResponseEntity.ok().build();
  }
}
