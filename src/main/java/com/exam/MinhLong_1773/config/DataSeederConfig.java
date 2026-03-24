package com.exam.MinhLong_1773.config;

import com.exam.MinhLong_1773.model.Department;
import com.exam.MinhLong_1773.model.Doctor;
import com.exam.MinhLong_1773.model.Patient;
import com.exam.MinhLong_1773.model.Role;
import com.exam.MinhLong_1773.model.RoleName;
import com.exam.MinhLong_1773.repository.DepartmentRepository;
import com.exam.MinhLong_1773.repository.DoctorRepository;
import com.exam.MinhLong_1773.repository.PatientRepository;
import com.exam.MinhLong_1773.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
public class DataSeederConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSeederConfig.class);

    @Bean
    CommandLineRunner seedDoctorsAndDepartments(
            DepartmentRepository departmentRepository,
            DoctorRepository doctorRepository,
            RoleRepository roleRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            seedDefaultRoles(roleRepository);
            seedDefaultAdminAccount(roleRepository, patientRepository, passwordEncoder);
            Map<String, Department> departments = buildDepartments(departmentRepository);

            List<DoctorSeed> seedData = List.of(
                    new DoctorSeed("Dr. Nguyen Van An", "Noi tong quat", "Noi"),
                    new DoctorSeed("Dr. Tran Thi Bich", "Nhi khoa", "Nhi"),
                    new DoctorSeed("Dr. Le Quang Huy", "Tim mach", "Tim mach"),
                    new DoctorSeed("Dr. Pham Minh Chau", "Da lieu", "Da lieu"),
                    new DoctorSeed("Dr. Vo Thanh Long", "Than - tiet nieu", "Noi"),
                    new DoctorSeed("Dr. Hoang Mai Anh", "Noi tiet", "Noi"),
                    new DoctorSeed("Dr. Bui Gia Bao", "Ngoai chan thuong", "Ngoai"),
                    new DoctorSeed("Dr. Dang Thu Ha", "Tai mui hong", "Tai mui hong"),
                    new DoctorSeed("Dr. Do Duc Khang", "Co xuong khop", "Ngoai"),
                    new DoctorSeed("Dr. Phan Ngoc Lan", "Noi than kinh", "Noi")
            );

            int inserted = 0;
            for (DoctorSeed item : seedData) {
                boolean exists = doctorRepository.findByName(item.name()).isPresent();
                if (exists) {
                    continue;
                }

                Department department = departments.get(item.departmentName());
                if (department == null) {
                    logger.warn("Skip doctor {} because department {} was not found", item.name(), item.departmentName());
                    continue;
                }

                Doctor doctor = new Doctor();
                doctor.setName(item.name());
                doctor.setSpecialty(item.specialty());
                doctor.setDepartment(department);
                doctor.setImage("https://picsum.photos/seed/" + item.name().replace(" ", "-") + "/300/300");

                doctorRepository.save(doctor);
                inserted++;
            }

            logger.info("Doctor seeding completed. Inserted {} new doctors. Total doctors: {}", inserted, doctorRepository.count());
        };
    }

    private void seedDefaultRoles(RoleRepository roleRepository) {
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName).isPresent()) {
                continue;
            }

            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            logger.info("Inserted role {}", roleName);
        }
    }

    private void seedDefaultAdminAccount(
            RoleRepository roleRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder
    ) {
        if (patientRepository.existsByUsername("admin")) {
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

        Patient admin = new Patient();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@clinic.local");
        admin.setRoles(Set.of(adminRole));
        patientRepository.save(admin);

        logger.info("Inserted default admin account: username=admin, password=admin123");
    }

    private Map<String, Department> buildDepartments(DepartmentRepository departmentRepository) {
        List<String> departmentNames = List.of(
                "Noi",
                "Ngoai",
                "Nhi",
                "Tim mach",
                "Da lieu",
                "Tai mui hong"
        );

        Map<String, Department> result = new HashMap<>();
        for (String name : departmentNames) {
            Department department = departmentRepository.findByName(name)
                    .orElseGet(() -> {
                        Department d = new Department();
                        d.setName(name);
                        return departmentRepository.save(d);
                    });
            result.put(name, department);
        }

        return result;
    }

    private record DoctorSeed(String name, String specialty, String departmentName) {
    }
}
