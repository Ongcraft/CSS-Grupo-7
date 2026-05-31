package pt.ul.fc.css.tascaeats.delivery.service;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.tascaeats.common.dto.CourierDto;
import pt.ul.fc.css.tascaeats.common.dto.DeliveryEventDto;
import pt.ul.fc.css.tascaeats.common.dto.OrderDto;
import pt.ul.fc.css.tascaeats.delivery.enums.DeliveryStatus;
import pt.ul.fc.css.tascaeats.delivery.kafka.Sender;
import pt.ul.fc.css.tascaeats.delivery.model.Courier;
import pt.ul.fc.css.tascaeats.delivery.model.Order;
import pt.ul.fc.css.tascaeats.delivery.repository.CourierRepository;
import pt.ul.fc.css.tascaeats.delivery.repository.OrderRepository;

@Service
public class DeliveryService {

  private final CourierRepository courierRepository;
  private final OrderRepository orderRepository;
  private final Sender sender;

  public DeliveryService(
      CourierRepository courierRepository, OrderRepository orderRepository, Sender sender) {
    this.courierRepository = courierRepository;
    this.orderRepository = orderRepository;
    this.sender = sender;
  }

  @Transactional
  public void registerCourier(CourierDto dto) {
    if (courierRepository.existsById(dto.id())) return;
    courierRepository.save(new Courier(dto.id(), dto.name(), dto.username()));
  }

  @Transactional
  public void updateCourier(CourierDto dto) {
    courierRepository
        .findById(dto.id())
        .ifPresent(c -> {
          c.update(dto.name(), dto.username());
          courierRepository.save(c);
        });
  }

  @Transactional
  public void removeCourier(CourierDto dto) {
    courierRepository.deleteById(dto.id());
  }
  
  @Transactional
  public void handleOrderReady(OrderDto dto) {
    Order order = orderRepository.findById(dto.orderId()).orElseGet(() -> new Order(dto.orderId(), dto.street(), dto.city(), dto.postalCode()));

    Courier courier = courierRepository.findFirstByAvailableTrue().orElseThrow(() -> new IllegalStateException("No available couriers"));

    order.assignCourier(courier);
    courier.setAvailable(false);

    courierRepository.save(courier);
    orderRepository.save(order);

    sender.sendCourierAssigned(new DeliveryEventDto(order.getId(), courier.getId()));
  }

  @Transactional
  public void startDelivery(UUID orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

    if (order.getCourier() == null)
      throw new IllegalStateException("No courier assigned to order " + orderId);

    order.startDelivery();
    orderRepository.save(order);
    sender.sendDeliveryStarted(new DeliveryEventDto(order.getId(), order.getCourier().getId()));
  }

  @Transactional
  public void completeDelivery(UUID orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

    Courier courier = order.getCourier();
    order.completeDelivery();

    if (courier != null) {
      courier.setAvailable(true);
      courierRepository.save(courier);
    }

    orderRepository.save(order);
    sender.sendDeliveryCompleted(new DeliveryEventDto(order.getId(), courier != null ? courier.getId() : null));
  }

  public List<Order> getOrdersByCourier(UUID courierId) {
    return orderRepository.findByCourierId(courierId);
  }

  public List<Order> getOrdersByStatus(DeliveryStatus status) {
    return orderRepository.findByStatus(status);
  }

  public List<Courier> getAllCouriers() {
    return courierRepository.findAll();
  }
}
