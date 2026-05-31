package pt.ul.fc.css.tascaeats.delivery.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pt.ul.fc.css.tascaeats.common.dto.CourierDto;
import pt.ul.fc.css.tascaeats.common.dto.OrderDto;
import pt.ul.fc.css.tascaeats.common.kafka.Topics;
import pt.ul.fc.css.tascaeats.delivery.service.DeliveryService;

@Component
public class Listener {

  private static final Logger log = LoggerFactory.getLogger(Listener.class);

  private final DeliveryService deliveryService;
  private final ObjectMapper objectMapper;

  public Listener(DeliveryService deliveryService, ObjectMapper objectMapper) {
    this.deliveryService = deliveryService;
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics = Topics.COURIER_REGISTERED, groupId = "delivery-group")
  public void onCourierRegistered(String message) {
    try {
      CourierDto dto = objectMapper.readValue(message, CourierDto.class);
      log.info("Received COURIER_REGISTERED: {}", dto);
      deliveryService.registerCourier(dto);
    } catch (Exception e) {
      log.error("Error processing COURIER_REGISTERED", e);
    }
  }

  @KafkaListener(topics = Topics.COURIER_UPDATED, groupId = "delivery-group")
  public void onCourierUpdated(String message) {
    try {
      CourierDto dto = objectMapper.readValue(message, CourierDto.class);
      log.info("Received COURIER_UPDATED: {}", dto);
      deliveryService.updateCourier(dto);
    } catch (Exception e) {
      log.error("Error processing COURIER_UPDATED", e);
    }
  }

  @KafkaListener(topics = Topics.COURIER_REMOVED, groupId = "delivery-group")
  public void onCourierRemoved(String message) {
    try {
      CourierDto dto = objectMapper.readValue(message, CourierDto.class);
      log.info("Received COURIER_REMOVED: {}", dto);
      deliveryService.removeCourier(dto);
    } catch (Exception e) {
      log.error("Error processing COURIER_REMOVED", e);
    }
  }

  @KafkaListener(topics = Topics.ORDER_READY, groupId = "delivery-group")
  public void onOrderReady(String message) {
    try {
      OrderDto dto = objectMapper.readValue(message, OrderDto.class);
      log.info("Received ORDER_READY: {}", dto);
      deliveryService.handleOrderReady(dto);
    } catch (Exception e) {
      log.error("Error processing ORDER_READY", e);
    }
  }
}
