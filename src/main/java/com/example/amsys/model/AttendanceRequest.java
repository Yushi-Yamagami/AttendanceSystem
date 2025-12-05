package com.example.amsys.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attendance_request")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRequest {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "request_id")
	private Long requestId;
	
	@Column(name = "student_id", nullable = false, length = 10)
	private String studentId;
	
	@Column(nullable = false)
	private LocalDate date;
	
	@Column(name = "lessontime_code", nullable = false)
	private Byte lessontimeCode;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AttendanceRequestStatus status;
	
	@Column(length = 100)
	private String reason;
	
	@Column(name = "request_type")
	@Enumerated(EnumType.STRING)
	private RequestType requestType;
	
	@Column(name = "teacher_id", length = 10)
	private String teacherId;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	public enum AttendanceRequestStatus {
		ABSENCE,
		LATE,
		LEAVE_EARLY
	}
	
	public enum RequestType {
		PENDING,
		APPROVED,
		REJECTED
	}

}
