package com.example.amsys.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attendance")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Attendance {
	
	@EmbeddedId
	private AttendanceId id;
	
	@Column(name = "status_code", nullable = false)
	@Enumerated(EnumType.STRING)
	private AttendanceStatus statusCode;
	
	@Column(length = 100)
	private String reason;
	
	public enum AttendanceStatus {
		NONE, 
		PRESENT, 
		ABSENCE, 
		LATE,
		LEAVE_EARLY
	}
	

}
