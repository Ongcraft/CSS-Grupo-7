package pt.ul.fc.css.tascaeats.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pt.ul.fc.css.tascaeats.common.dto.CourierDto;
import pt.ul.fc.css.tascaeats.common.dto.OrderDto;
import pt.ul.fc.css.tascaeats.common.kafka.Topics;

@Component
public class KafkaSender {

  private static final Logger log = LoggerFactory.getLogger(KafkaSender.class);

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public KafkaSender(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  public void sendCourierRegistered(CourierDto dto) {
    send(Topics.COURIER_REGISTERED, dto);
  }

  public void sendCourierUpdated(CourierDto dto) {
    send(Topics.COURIER_UPDATED, dto);
  }

  public void sendCourierRemoved(CourierDto dto) {
    send(Topics.COURIER_REMOVED, dto);
  }

  public void sendOrderReady(OrderDto dto) {
    send(Topics.ORDER_READY, dto);
  }

  private void send(String topic, Object payload) {
    try {
      String json = objectMapper.writeValueAsString(payload);
      kafkaTemplate.send(topic, json);
      log.info("Published to [{}]: {}", topic, json);
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize payload for topic {}: {}", topic, e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
