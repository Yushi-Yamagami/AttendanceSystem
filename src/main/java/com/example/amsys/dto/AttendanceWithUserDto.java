package com.example.amsys.dto;

import java.time.LocalDate;

import com.example.amsys.model.Attendance.AttendanceStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 出欠情報と学生情報を含むDTO
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceWithUserDto {
	
	private LocalDate date;
	private Long studentId;
	private String userId;
	private Byte lessontimeCode;
	private String lessontimeName;
	private AttendanceStatus statusCode;
	private String statusName;
	private String reason;
	private String lastName;
	private String firstName;
	private String lastKanaName;
	private String firstKanaName;
	private Byte gradeCode;

}
