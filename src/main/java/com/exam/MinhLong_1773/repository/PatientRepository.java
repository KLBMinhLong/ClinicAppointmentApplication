package com.exam.MinhLong_1773.repository;

import com.exam.MinhLong_1773.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
