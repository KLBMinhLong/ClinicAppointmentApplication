package com.exam.MinhLong_1773.controller;

import com.exam.MinhLong_1773.model.Doctor;
import com.exam.MinhLong_1773.service.DoctorService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private static final int PAGE_SIZE = 5;

    private final DoctorService doctorService;

    public HomeController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping({"/", "/home"})
    public String home(
            @RequestParam(name = "page", defaultValue = "0") int page,
            Model model
    ) {
        Page<Doctor> doctorPage = doctorService.getDoctorPage(page, PAGE_SIZE);

        model.addAttribute("doctorPage", doctorPage);
        model.addAttribute("doctors", doctorPage.getContent());
        model.addAttribute("currentPage", doctorPage.getNumber());
        model.addAttribute("totalPages", doctorPage.getTotalPages());

        return "home";
    }
}
