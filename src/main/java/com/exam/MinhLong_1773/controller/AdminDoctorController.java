package com.exam.MinhLong_1773.controller;

import com.exam.MinhLong_1773.dto.DoctorForm;
import com.exam.MinhLong_1773.model.Doctor;
import com.exam.MinhLong_1773.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/doctors")
public class AdminDoctorController {

    private static final int PAGE_SIZE = 5;

    private final DoctorService doctorService;

    public AdminDoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public String list(
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        Page<Doctor> doctorPage = doctorService.getDoctorPage(page, PAGE_SIZE);

        model.addAttribute("doctorPage", doctorPage);
        model.addAttribute("doctors", doctorPage.getContent());
        model.addAttribute("currentPage", doctorPage.getNumber());
        model.addAttribute("totalPages", doctorPage.getTotalPages());
        return "admin/doctor-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("doctorForm", new DoctorForm());
        model.addAttribute("departments", doctorService.getAllDepartments());
        model.addAttribute("formTitle", "Create Doctor");
        model.addAttribute("formAction", "/admin/doctors/create");
        return "admin/doctor-form";
    }

    @PostMapping("/create")
    public String createDoctor(
            @Valid @ModelAttribute("doctorForm") DoctorForm doctorForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", doctorService.getAllDepartments());
            model.addAttribute("formTitle", "Create Doctor");
            model.addAttribute("formAction", "/admin/doctors/create");
            return "admin/doctor-form";
        }

        doctorService.createDoctor(
                doctorForm.getName(),
                doctorForm.getSpecialty(),
                doctorForm.getImage(),
                doctorForm.getDepartmentId()
        );

        redirectAttributes.addFlashAttribute("successMessage", "Tao bac si thanh cong.");
        return "redirect:/admin/doctors";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id);

        DoctorForm doctorForm = new DoctorForm(
                doctor.getName(),
                doctor.getSpecialty(),
                doctor.getImage(),
                doctor.getDepartment().getId()
        );

        model.addAttribute("doctorForm", doctorForm);
        model.addAttribute("departments", doctorService.getAllDepartments());
        model.addAttribute("formTitle", "Update Doctor");
        model.addAttribute("formAction", "/admin/doctors/" + id + "/edit");
        return "admin/doctor-form";
    }

    @PostMapping("/{id}/edit")
    public String updateDoctor(
            @PathVariable Long id,
            @Valid @ModelAttribute("doctorForm") DoctorForm doctorForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", doctorService.getAllDepartments());
            model.addAttribute("formTitle", "Update Doctor");
            model.addAttribute("formAction", "/admin/doctors/" + id + "/edit");
            return "admin/doctor-form";
        }

        doctorService.updateDoctor(
                id,
                doctorForm.getName(),
                doctorForm.getSpecialty(),
                doctorForm.getImage(),
                doctorForm.getDepartmentId()
        );

        redirectAttributes.addFlashAttribute("successMessage", "Cap nhat bac si thanh cong.");
        return "redirect:/admin/doctors";
    }

    @PostMapping("/{id}/delete")
    public String deleteDoctor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        doctorService.deleteDoctor(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xoa bac si thanh cong.");
        return "redirect:/admin/doctors";
    }
}
