package pt.ul.fc.css.tascaeats.delivery.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ul.fc.css.tascaeats.delivery.model.Courier;
import pt.ul.fc.css.tascaeats.delivery.service.DeliveryService;

@RestController
@RequestMapping("/couriers")
public class CourierController {

  private final DeliveryService deliveryService;

  public CourierController(DeliveryService deliveryService) {
    this.deliveryService = deliveryService;
  }

  @GetMapping
  public ResponseEntity<List<Courier>> getAllCouriers() {
    return ResponseEntity.ok(deliveryService.getAllCouriers());
  }
}
