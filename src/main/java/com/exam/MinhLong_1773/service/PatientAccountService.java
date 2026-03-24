package com.exam.MinhLong_1773.service;

import com.exam.MinhLong_1773.dto.RegisterForm;
import com.exam.MinhLong_1773.model.Patient;
import com.exam.MinhLong_1773.model.Role;
import com.exam.MinhLong_1773.model.RoleName;
import com.exam.MinhLong_1773.repository.PatientRepository;
import com.exam.MinhLong_1773.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class PatientAccountService {

    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public PatientAccountService(
            PatientRepository patientRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.patientRepository = patientRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean usernameExists(String username) {
        return patientRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return patientRepository.existsByEmail(email);
    }

    @Transactional
    public Patient registerPatient(RegisterForm form) {
        Role patientRole = roleRepository.findByName(RoleName.PATIENT)
                .orElseThrow(() -> new EntityNotFoundException("PATIENT role not found"));

        Patient patient = new Patient();
        patient.setUsername(form.getUsername().trim());
        patient.setPassword(passwordEncoder.encode(form.getPassword()));
        patient.setEmail(form.getEmail().trim());
        patient.setRoles(Set.of(patientRole));

        return patientRepository.save(patient);
    }
}
