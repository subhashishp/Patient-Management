package com.patientmanagement.patientservice.controller;

import com.patientmanagement.patientservice.dto.PatientRequestDTO;
import com.patientmanagement.patientservice.dto.PatientResponseDTO;
import com.patientmanagement.patientservice.dto.validators.CreatePatientValidationGroup;
import com.patientmanagement.patientservice.service.PatientService;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/")
    public ResponseEntity<List<PatientResponseDTO>> getPatients() {
        List<PatientResponseDTO> patientResponseDTOs = patientService.getPatients();

        return ResponseEntity.ok(patientResponseDTOs);
    }

    @PostMapping("/newPatientRegistration")
    public ResponseEntity<PatientResponseDTO> createPatient(@Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO patientResponseDTO = patientService.createPatient(patientRequestDTO);

        return ResponseEntity.ok(patientResponseDTO);
    }

    @PutMapping("/updatePatient/{patient_id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable(name = "patient_id") Long id,
                                                            @Validated({Default.class}) @RequestBody PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO patientResponseDTO = patientService.updatePatient(id, patientRequestDTO);
        return ResponseEntity.ok().body(patientResponseDTO);
    }

    @DeleteMapping("/deletePatient/{patient_id}")
    public ResponseEntity<Void> deletePatient(@PathVariable(name = "patient_id") Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
