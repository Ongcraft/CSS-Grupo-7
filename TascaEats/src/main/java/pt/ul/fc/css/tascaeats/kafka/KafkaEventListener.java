package pt.ul.fc.css.tascaeats.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pt.ul.fc.css.tascaeats.common.dto.DeliveryEventDto;
import pt.ul.fc.css.tascaeats.common.kafka.Topics;
import pt.ul.fc.css.tascaeats.services.OrderService;

@Component
public class KafkaEventListener {

  private static final Logger log = LoggerFactory.getLogger(KafkaEventListener.class);

  private final OrderService orderService;
  private final ObjectMapper objectMapper;

  public KafkaEventListener(OrderService orderService, ObjectMapper objectMapper) {
    this.orderService = orderService;
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics = Topics.COURIER_ASSIGNED, groupId = "tascaeats-group")
  public void onCourierAssigned(String message) {
    try {
      DeliveryEventDto dto = objectMapper.readValue(message, DeliveryEventDto.class);
      log.info("Received COURIER_ASSIGNED: {}", dto);
      orderService.assignCourierFromKafka(dto.orderId(), dto.courierId());
    } catch (Exception e) {
      log.error("Error processing COURIER_ASSIGNED", e);
    }
  }

  @KafkaListener(topics = Topics.DELIVERY_STARTED, groupId = "tascaeats-group")
  public void onDeliveryStarted(String message) {
    try {
      DeliveryEventDto dto = objectMapper.readValue(message, DeliveryEventDto.class);
      log.info("Received DELIVERY_STARTED: {}", dto);
      orderService.startDelivery(dto.orderId());
    } catch (Exception e) {
      log.error("Error processing DELIVERY_STARTED", e);
    }
  }

  @KafkaListener(topics = Topics.DELIVERY_COMPLETED, groupId = "tascaeats-group")
  public void onDeliveryCompleted(String message) {
    try {
      DeliveryEventDto dto = objectMapper.readValue(message, DeliveryEventDto.class);
      log.info("Received DELIVERY_COMPLETED: {}", dto);
      orderService.completeDelivery(dto.orderId());
    } catch (Exception e) {
      log.error("Error processing DELIVERY_COMPLETED", e);
    }
  }
}
