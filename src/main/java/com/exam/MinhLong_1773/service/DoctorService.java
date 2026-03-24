package com.exam.MinhLong_1773.service;

import com.exam.MinhLong_1773.model.Department;
import com.exam.MinhLong_1773.model.Doctor;
import com.exam.MinhLong_1773.repository.DepartmentRepository;
import com.exam.MinhLong_1773.repository.DoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;

    public DoctorService(DoctorRepository doctorRepository, DepartmentRepository departmentRepository) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
    }

    public Page<Doctor> getDoctorPage(int page, int size) {
        int safePage = Math.max(page, 0);
        Pageable pageable = PageRequest.of(safePage, size, Sort.by("name").ascending());
        return doctorRepository.findAll(pageable);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll(Sort.by("name").ascending());
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findWithDepartmentById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + id));
    }

    @Transactional
    public Doctor createDoctor(String name, String specialty, String image, Long departmentId) {
        Department department = findDepartmentOrThrow(departmentId);

        Doctor doctor = new Doctor();
        doctor.setName(name.trim());
        doctor.setSpecialty(specialty.trim());
        doctor.setImage(normalizeImage(image));
        doctor.setDepartment(department);

        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor updateDoctor(Long id, String name, String specialty, String image, Long departmentId) {
        Doctor doctor = getDoctorById(id);
        Department department = findDepartmentOrThrow(departmentId);

        doctor.setName(name.trim());
        doctor.setSpecialty(specialty.trim());
        doctor.setImage(normalizeImage(image));
        doctor.setDepartment(department);

        return doctorRepository.save(doctor);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new EntityNotFoundException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
    }

    private Department findDepartmentOrThrow(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + departmentId));
    }

    private String normalizeImage(String image) {
        if (image == null || image.isBlank()) {
            return null;
        }
        return image.trim();
    }
}
