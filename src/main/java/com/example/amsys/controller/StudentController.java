package com.example.amsys.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.example.amsys.model.LessonTime;
import com.example.amsys.repository.AttendanceRequestRepository;
import com.example.amsys.repository.LessonTimeRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/students")
@SessionAttributes("attendanceRequestForm")
public class StudentController {
	
	@Autowired
	private AttendanceRequestRepository attendanceRequestRepository;
	
	@Autowired
	private LessonTimeRepository lessonTimeRepository;
	
	@GetMapping("/request")
	public String showRequestForm(Model model, Principal principal) {
		if (!model.containsAttribute("attendanceRequestForm")) {
			model.addAttribute("attendanceRequestForm", new AttendanceRequestForm());
		}
		
		// Add student ID to model
		if (principal != null) {
			model.addAttribute("studentId", principal.getName());
		}
		
		// Load lesson times for the form
		List<LessonTime> lessonTimes = lessonTimeRepository.findAllByOrderByLessontimeCodeAsc();
		model.addAttribute("lessonTimes", lessonTimes);
		
		return "students/request";
	}
	
	@PostMapping("/request/confirm")
	public String confirmRequest(
			@Valid @ModelAttribute("attendanceRequestForm") AttendanceRequestForm form,
			BindingResult result,
			Model model) {
		
		if (result.hasErrors()) {
			// Reload lesson times for the form
			List<LessonTime> lessonTimes = lessonTimeRepository.findAllByOrderByLessontimeCodeAsc();
			model.addAttribute("lessonTimes", lessonTimes);
			return "students/request";
		}
		
		// Set display names for confirmation page
		form.setStatusName(getStatusName(form.getStatusCode()));
		form.setLessonTimeName(getLessonTimeName(form.getLessonTimeCodes()));
		
		model.addAttribute("request", form);
		
		return "students/checkrequest";
	}
	
	@PostMapping("/request/submit")
	public String submitRequest(
			@ModelAttribute("attendanceRequestForm") AttendanceRequestForm form,
			Principal principal,
			SessionStatus sessionStatus,
			Model model) {
		
		String studentId = principal.getName();
		AttendanceRequestStatus status = getAttendanceStatus(form.getStatusCode());
		
		// Create list of AttendanceRequest entities for batch insertion
		List<AttendanceRequest> requests = new ArrayList<>();
		
		// Convert String lesson time codes to Byte
		List<Byte> byteLessonTimeCodes = form.getLessonTimeCodes().stream()
				.map(Byte::valueOf)
				.collect(Collectors.toList());
		
		// For each selected lesson time, create a separate request
		for (Byte lessonTimeCode : byteLessonTimeCodes) {
			AttendanceRequest request = new AttendanceRequest();
			request.setStudentId(studentId);
			request.setDate(form.getDate());
			request.setStatus(status);
			request.setLessontimeCode(lessonTimeCode);
			request.setReason(form.getAttendanceReason());
			request.setRequestType(RequestType.PENDING);
			requests.add(request);
		}
		
		// Batch save to database
		attendanceRequestRepository.saveAll(requests);
		
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
	
	private String getLessonTimeName(List<String> lessonTimeCodes) {
		if (lessonTimeCodes == null || lessonTimeCodes.isEmpty()) {
			return "";
		}
		
		// Convert String to Byte
		List<Byte> byteCodes = lessonTimeCodes.stream()
				.map(Byte::valueOf)
				.collect(Collectors.toList());
		
		List<LessonTime> lessonTimes = lessonTimeRepository.findAllById(byteCodes);
		return lessonTimes.stream()
				.map(LessonTime::getLessontimeName)
				.collect(Collectors.joining(", "));
	}
	
	private AttendanceRequestStatus getAttendanceStatus(String statusCode) {
		switch (statusCode) {
			case "ABSENCE": return AttendanceRequestStatus.ABSENCE;
			case "LATE": return AttendanceRequestStatus.LATE;
			case "LEAVE_EARLY": return AttendanceRequestStatus.LEAVE_EARLY;
			default: throw new IllegalArgumentException("Invalid status code: " + statusCode);
		}
	}

}
