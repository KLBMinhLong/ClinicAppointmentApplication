package com.exam.MinhLong_1773.repository;

import com.exam.MinhLong_1773.model.Appointment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @EntityGraph(attributePaths = {"doctor", "doctor.department"})
    List<Appointment> findByPatientUsernameOrderByAppointmentDateDesc(String username);
}
