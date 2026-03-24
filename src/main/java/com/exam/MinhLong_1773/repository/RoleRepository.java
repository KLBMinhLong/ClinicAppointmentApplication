package com.exam.MinhLong_1773.repository;

import com.exam.MinhLong_1773.model.Role;
import com.exam.MinhLong_1773.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
