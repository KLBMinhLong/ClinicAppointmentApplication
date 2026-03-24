package com.exam.MinhLong_1773.service;

import com.exam.MinhLong_1773.model.Patient;
import com.exam.MinhLong_1773.model.Role;
import com.exam.MinhLong_1773.model.RoleName;
import com.exam.MinhLong_1773.repository.PatientRepository;
import com.exam.MinhLong_1773.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GoogleOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public GoogleOAuth2UserService(
            PatientRepository patientRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.patientRepository = patientRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = readEmail(attributes);
        Patient patient = patientRepository.findByEmail(email)
                .orElseGet(() -> createPatientFromGoogle(email));

        Set<GrantedAuthority> authorities = patient.getRoles()
                .stream()
                .map(Role::getName)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toCollection(HashSet::new));

        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        if (nameAttributeKey == null || nameAttributeKey.isBlank() || !attributes.containsKey(nameAttributeKey)) {
            nameAttributeKey = attributes.containsKey("sub") ? "sub" : "email";
        }

        return new DefaultOAuth2User(authorities, attributes, nameAttributeKey);
    }

    private String readEmail(Map<String, Object> attributes) {
        Object emailObj = attributes.get("email");
        if (emailObj == null) {
            throw new OAuth2AuthenticationException("Email not provided by Google account");
        }
        String email = emailObj.toString().trim().toLowerCase();
        if (email.isBlank()) {
            throw new OAuth2AuthenticationException("Email is empty");
        }
        return email;
    }

    private Patient createPatientFromGoogle(String email) {
        Role patientRole = roleRepository.findByName(RoleName.PATIENT)
                .orElseThrow(() -> new EntityNotFoundException("PATIENT role not found"));

        Patient patient = new Patient();
        patient.setEmail(email);
        patient.setUsername(generateUniqueUsername(email));
        patient.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        patient.setRoles(Set.of(patientRole));

        return patientRepository.save(patient);
    }

    private String generateUniqueUsername(String email) {
        String localPart = email.split("@")[0]
                .toLowerCase()
                .replaceAll("[^a-z0-9._-]", "");

        if (localPart.isBlank()) {
            localPart = "google_user";
        }

        String candidate = localPart;
        int counter = 1;
        while (patientRepository.existsByUsername(candidate)) {
            candidate = localPart + counter;
            counter++;
        }
        return candidate;
    }
}
