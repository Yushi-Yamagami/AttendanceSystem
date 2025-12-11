package com.example.amsys.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.amsys.model.AttendanceRequest.AttendanceRequestStatus;
import com.example.amsys.model.AttendanceRequest.RequestType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 出欠席申請情報と学生情報を含むDTO
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequestWithUserDto {
	
	private Long requestId;
	private String studentId;
	private LocalDate date;
	private Byte lessontimeCode;
	private AttendanceRequestStatus status;
	private String reason;
	private RequestType requestType;
	private String teacherId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	// User information
	private String lastName;
	private String firstName;
	private String lastKanaName;
	private String firstKanaName;

}
