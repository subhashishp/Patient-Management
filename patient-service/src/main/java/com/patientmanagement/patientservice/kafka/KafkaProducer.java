package com.patientmanagement.patientservice.kafka;

import com.patientmanagement.patientservice.dto.PatientEventDTO;
import com.patientmanagement.patientservice.entity.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;
import tools.jackson.databind.ObjectMapper;

@Service
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic}")
    private String topic;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sentEvent(Patient patient) {
        PatientEventDTO event = new PatientEventDTO();
        event.setPatientId(patient.getId().toString());
        event.setName(patient.getName());
        event.setEmail(patient.getEmail());
        event.setEventType("PATIENT_CREATED");

        try {
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(event));
        } catch (Exception ex) {
            log.error("Error sending PatientCreated event {} - exception {}", event, ex.getMessage(), ex);
        }
    }
}
