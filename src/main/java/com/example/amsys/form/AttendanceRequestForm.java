package com.example.amsys.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class AttendanceRequestForm {
	
	@NotNull
	private LocalDate date;
	
	@NotNull
	private String statusCode;
	
	@NotEmpty
	private List<String> lessonTimeCodes;
	
	@NotNull
	@Size(max = 100)
	private String attendanceReason;
	
	// For display in confirmation page
	private String statusName;
	private String lessonTimeName;
	
	// No-args constructor with proper initialization
	public AttendanceRequestForm() {
		this.lessonTimeCodes = new ArrayList<>();
	}

}
