package com.exam.MinhLong_1773.repository;

import com.exam.MinhLong_1773.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByName(String name);

    @EntityGraph(attributePaths = "department")
    Optional<Doctor> findWithDepartmentById(Long id);

    @Override
    @EntityGraph(attributePaths = "department")
    Page<Doctor> findAll(Pageable pageable);
}
