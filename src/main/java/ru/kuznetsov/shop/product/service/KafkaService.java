package ru.kuznetsov.shop.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kuznetsov.shop.represent.dto.AbstractDto;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    Logger logger = LoggerFactory.getLogger(KafkaService.class);

    public <E extends AbstractDto> boolean sendSaveMessage(E object, String topic) {
        logger.info("Sending save message to topic: {}, with object: {}", topic, object);
        try {
            String value = objectMapper.writeValueAsString(object);
            kafkaTemplate.send(topic, value);
            return true;
        } catch (JsonProcessingException e) {
            logger.error("Error during sending of the message to topic: {}, with object: {}", topic, object);
            logger.error(e.getMessage());
            return false;
        }
    }
}
