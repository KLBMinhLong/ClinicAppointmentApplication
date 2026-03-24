package com.exam.MinhLong_1773.controller;

import com.exam.MinhLong_1773.dto.RegisterForm;
import com.exam.MinhLong_1773.service.PatientAccountService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final PatientAccountService patientAccountService;

    public AuthController(PatientAccountService patientAccountService) {
        this.patientAccountService = patientAccountService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerForm") RegisterForm registerForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (patientAccountService.usernameExists(registerForm.getUsername())) {
            bindingResult.rejectValue("username", "username.duplicate", "Username da ton tai");
        }

        if (patientAccountService.emailExists(registerForm.getEmail())) {
            bindingResult.rejectValue("email", "email.duplicate", "Email da ton tai");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        patientAccountService.registerPatient(registerForm);
        redirectAttributes.addFlashAttribute("successMessage", "Dang ky thanh cong. Vui long dang nhap.");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
