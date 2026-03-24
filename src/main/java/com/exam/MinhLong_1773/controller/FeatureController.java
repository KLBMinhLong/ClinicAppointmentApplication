package com.exam.MinhLong_1773.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FeatureController {

    @GetMapping("/courses")
    public String courses() {
        return "courses";
    }

    @GetMapping("/enroll")
    public String enrollHome() {
        return "enroll-home";
    }
}
