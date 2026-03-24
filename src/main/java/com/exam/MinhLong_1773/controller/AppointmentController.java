package com.exam.MinhLong_1773.controller;

import com.exam.MinhLong_1773.dto.AppointmentForm;
import com.exam.MinhLong_1773.model.Doctor;
import com.exam.MinhLong_1773.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/enroll")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/create")
    public String showBookingForm(@RequestParam("doctorId") Long doctorId, Model model) {
        Doctor doctor = appointmentService.getDoctorForBooking(doctorId);
        model.addAttribute("doctor", doctor);
        model.addAttribute("doctorId", doctorId);
        model.addAttribute("appointmentForm", new AppointmentForm());
        return "appointment-form";
    }

    @PostMapping("/create")
    public String createAppointment(
            @RequestParam("doctorId") Long doctorId,
            @Valid @ModelAttribute("appointmentForm") AppointmentForm appointmentForm,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Doctor doctor = appointmentService.getDoctorForBooking(doctorId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("doctor", doctor);
            model.addAttribute("doctorId", doctorId);
            return "appointment-form";
        }

        try {
            appointmentService.createAppointment(authentication.getName(), doctorId, appointmentForm.getAppointmentDate());
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("appointmentDate", "appointmentDate.invalid", ex.getMessage());
            model.addAttribute("doctor", doctor);
            model.addAttribute("doctorId", doctorId);
            return "appointment-form";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Dat lich thanh cong.");
        return "redirect:/enroll/my-appointments";
    }

    @GetMapping("/my-appointments")
    public String myAppointments(Authentication authentication, Model model) {
        model.addAttribute("appointments", appointmentService.getMyAppointments(authentication.getName()));
        return "my-appointments";
    }
}
