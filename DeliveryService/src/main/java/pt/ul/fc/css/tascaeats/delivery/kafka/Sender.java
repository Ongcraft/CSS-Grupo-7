package pt.ul.fc.css.tascaeats.delivery.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pt.ul.fc.css.tascaeats.common.dto.DeliveryEventDto;
import pt.ul.fc.css.tascaeats.common.kafka.Topics;

@Component
public class Sender {

  private static final Logger log = LoggerFactory.getLogger(Sender.class);

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public Sender(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  public void sendCourierAssigned(DeliveryEventDto dto) {
    send(Topics.COURIER_ASSIGNED, dto);
  }

  public void sendDeliveryStarted(DeliveryEventDto dto) {
    send(Topics.DELIVERY_STARTED, dto);
  }

  public void sendDeliveryCompleted(DeliveryEventDto dto) {
    send(Topics.DELIVERY_COMPLETED, dto);
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
