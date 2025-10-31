package com.example.amsys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @GetMapping("/students/menu")
    public String studentMenu() {
        return "students/menu";
    }

    @GetMapping("/teachers/menu")
    public String teacherMenu() {
        return "teachers/menu";
    }

}
