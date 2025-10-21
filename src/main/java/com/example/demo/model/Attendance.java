package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attendance")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Attendance {
	
	@EmbeddedId
	private AttendanceId id;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AttendanceStatus status;
	
	@Column(nullable = false)
	private String reason;
	
	@RequiredArgsConstructor
    @Getter
	public enum AttendanceStatus {
		
		PRESENT("未入力"), 
		ATTENDANCE("出席"), 
		ABSENCE("欠席"), 
		LATENESS("遅刻"),
		EARLY("早退");
		
		private final String displayName;
		
	}
	

}
