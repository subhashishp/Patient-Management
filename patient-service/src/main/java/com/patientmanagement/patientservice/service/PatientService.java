package com.patientmanagement.patientservice.service;

import com.patientmanagement.patientservice.Repository.PatientRepository;
import com.patientmanagement.patientservice.dto.PatientRequestDTO;
import com.patientmanagement.patientservice.dto.PatientResponseDTO;
import com.patientmanagement.patientservice.entity.Patient;
import com.patientmanagement.patientservice.exception.EmailAlreadyExistsException;
import com.patientmanagement.patientservice.exception.PatientNotFoundException;
import com.patientmanagement.patientservice.grpc.BillingServiceGrpcClient;
import com.patientmanagement.patientservice.kafka.KafkaProducer;
import com.patientmanagement.patientservice.mapper.PatientMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient,
                          KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();
//        System.out.println(patientRepository.findAll().size());
        List<PatientResponseDTO> patientResponseDTOs = patients.parallelStream()
                        .map(PatientMapper::toDTO)
                        .toList();


        return patientResponseDTOs;
    }

    @Transactional
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
                throw new EmailAlreadyExistsException("A patient with this email already exists" + patientRequestDTO.getEmail());
        }

        log.info("Saving patient details in db for patient {}", patientRequestDTO.getName());
        Patient newPatient = patientRepository.save(
                PatientMapper.toEntity(patientRequestDTO)
        );

        log.info("Patient details saved to database ID - {}, Name - {}, calling billing service",newPatient.getId(), newPatient.getName());
        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(),
                newPatient.getName(), newPatient.getEmail());

        log.info("Sending event to Kafka for Patient - {}",newPatient.getName());
        kafkaProducer.sentEvent(newPatient);

        log.info("Event produced for patient - {}", newPatient.getName());
        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(Long id, PatientRequestDTO patientRequestDTO) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + id));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),id)) {
            throw new EmailAlreadyExistsException("A patient with this email already exists " +
                    patientRequestDTO.getEmail());
        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient =  patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
        }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }


}
