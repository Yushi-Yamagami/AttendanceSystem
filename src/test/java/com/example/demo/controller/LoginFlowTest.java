package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testIndexPageAccessible() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void testLoginPageAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("/login"));
    }

    @Test
    public void testStudentMenuRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/students/menu"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testTeacherMenuRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/teachers/menu"))
                .andExpect(status().is3xxRedirection());
    }
}
