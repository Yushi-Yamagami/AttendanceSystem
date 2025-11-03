package com.example.amsys.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.example.amsys.form.AttendanceRequestForm;
import com.example.amsys.model.AttendanceRequest;
import com.example.amsys.model.AttendanceRequest.AttendanceRequestStatus;
import com.example.amsys.model.AttendanceRequest.RequestType;
import com.example.amsys.repository.AttendanceRequestRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/students")
@SessionAttributes("attendanceRequestForm")
public class StudentController {
	
	@Autowired
	private AttendanceRequestRepository attendanceRequestRepository;
	
	@GetMapping("/request")
	public String showRequestForm(Model model, Principal principal) {
		if (!model.containsAttribute("attendanceRequestForm")) {
			model.addAttribute("attendanceRequestForm", new AttendanceRequestForm());
		}
		
		// Add student ID to model
		if (principal != null) {
			model.addAttribute("studentId", principal.getName());
		}
		
		return "students/request";
	}
	
	@PostMapping("/request/confirm")
	public String confirmRequest(
			@Valid @ModelAttribute("attendanceRequestForm") AttendanceRequestForm form,
			BindingResult result,
			Model model) {
		
		if (result.hasErrors()) {
			return "students/request";
		}
		
		// Set display names for confirmation page
		form.setStatusName(getStatusName(form.getStatusCode()));
		form.setLessonTimeName(getLessonTimeName(form.getLessonTimeFlag()));
		
		model.addAttribute("request", form);
		
		return "students/checkrequest";
	}
	
	@PostMapping("/request/submit")
	public String submitRequest(
			@ModelAttribute("attendanceRequestForm") AttendanceRequestForm form,
			Principal principal,
			SessionStatus sessionStatus,
			Model model) {
		
		// Create AttendanceRequest entity
		AttendanceRequest request = new AttendanceRequest();
		request.setStudentId(principal.getName());
		request.setDate(form.getDate());
		request.setStatus(getAttendanceStatus(form.getStatusCode()));
		request.setLessontimeCode(getLessonTimeCode(form.getLessonTimeFlag()));
		request.setReason(form.getAttendanceReason());
		request.setRequestType(RequestType.PENDING);
		
		// Save to database
		attendanceRequestRepository.save(request);
		
		// Clear session
		sessionStatus.setComplete();
		
		return "students/result";
	}
	
	private String getStatusName(String statusCode) {
		switch (statusCode) {
			case "ABSENCE": return "欠席";
			case "LATE": return "遅刻";
			case "LEAVE_EARLY": return "早退";
			default: return "";
		}
	}
	
	private String getLessonTimeName(String lessonTimeFlag) {
		if (lessonTimeFlag == null || lessonTimeFlag.isEmpty()) {
			return "";
		}
		switch (lessonTimeFlag) {
			case "AM": return "午前";
			case "PM": return "午後";
			case "ALL": return "1日";
			default: return "";
		}
	}
	
	private AttendanceRequestStatus getAttendanceStatus(String statusCode) {
		switch (statusCode) {
			case "ABSENCE": return AttendanceRequestStatus.ABSENCE;
			case "LATE": return AttendanceRequestStatus.LATE;
			case "LEAVE_EARLY": return AttendanceRequestStatus.LEAVE_EARLY;
			default: throw new IllegalArgumentException("Invalid status code: " + statusCode);
		}
	}
	
	private Byte getLessonTimeCode(String lessonTimeFlag) {
		if (lessonTimeFlag == null || lessonTimeFlag.isEmpty()) {
			return null;
		}
		switch (lessonTimeFlag) {
			case "AM": return 1;
			case "PM": return 2;
			case "ALL": return 3;
			default: return null;
		}
	}

}
