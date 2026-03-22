package com.patientmanagement.analyticsservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patientmanagement.analyticsservice.dto.PatientEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;

//    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
//    public void consumeEvent(String event, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition, @Header(KafkaHeaders.OFFSET) long offset) {
////        try {
////            PatientEvent patientEvent = PatientEvent.parseFrom(event);
////            // ... business logic after this
////
////            log.info("Received Patient Event: [PatientId={}, ParientName={}]",
////                    patientEvent.getPatientId(),
////                    patientEvent.getName());
////        } catch (InvalidProtocolBufferException e) {
////            log.error("Error deserializing event {}", e.getMessage());
////            throw new RuntimeException(e);
////        }
//
//          log.info("Event received {}, partition {},  offset{}",event,partition,offset);
//    }


        @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
        public void consumeEvent(String event) {
            log.info("Event received: {}", event);

            try {
                PatientEventDTO patientEvent = objectMapper.readValue(event, PatientEventDTO.class);

                log.info("Received Patient Event: [PatientId={}, ParientName={}]",
                        patientEvent.getPatientId(),
                        patientEvent.getName());

                // ... business logic after this

            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }
}
