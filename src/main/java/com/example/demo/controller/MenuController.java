package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @GetMapping("/teachers/menu")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public String teachersMenu() {
        return "teachers/menu";
    }

    @GetMapping("/students/menu")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public String studentsMenu() {
        return "students/menu";
    }
}
