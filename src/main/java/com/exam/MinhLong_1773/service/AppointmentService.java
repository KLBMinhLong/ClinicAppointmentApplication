package com.exam.MinhLong_1773.service;

import com.exam.MinhLong_1773.model.Appointment;
import com.exam.MinhLong_1773.model.Doctor;
import com.exam.MinhLong_1773.model.Patient;
import com.exam.MinhLong_1773.repository.AppointmentRepository;
import com.exam.MinhLong_1773.repository.DoctorRepository;
import com.exam.MinhLong_1773.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public Appointment createAppointment(String username, Long doctorId, LocalDate appointmentDate) {
        Patient patient = patientRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found: " + username));

        Doctor doctor = doctorRepository.findWithDepartmentById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + doctorId));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(toStartOfDay(appointmentDate));

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getMyAppointments(String username) {
        return appointmentRepository.findByPatientUsernameOrderByAppointmentDateDesc(username);
    }

    public Doctor getDoctorForBooking(Long doctorId) {
        return doctorRepository.findWithDepartmentById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + doctorId));
    }

    private LocalDateTime toStartOfDay(LocalDate appointmentDate) {
        if (appointmentDate == null) {
            throw new IllegalArgumentException("Appointment date is required");
        }
        if (appointmentDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngay kham phai tu hom nay tro di");
        }
        return appointmentDate.atStartOfDay();
    }
}
