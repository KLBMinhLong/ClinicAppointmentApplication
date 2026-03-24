package com.exam.MinhLong_1773.service;

import com.exam.MinhLong_1773.model.Patient;
import com.exam.MinhLong_1773.model.Role;
import com.exam.MinhLong_1773.repository.PatientRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PatientRepository patientRepository;

    public CustomUserDetailsService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Patient patient = patientRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<GrantedAuthority> authorities = patient.getRoles()
                .stream()
                .map(Role::getName)
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName.name()))
                .collect(Collectors.toSet());

        return new User(patient.getUsername(), patient.getPassword(), authorities);
    }
}
