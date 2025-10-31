package com.example.amsys.model;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lessontime")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonTime {
	
	@Id
	@Column(name = "lessontime_code")
	private Byte lessontimeCode;
	
	@Column(name = "lessontime_name", nullable = false, length = 4)
	private String lessontimeName;
	
	@Column(name = "start_time", nullable = false)
	private LocalTime startTime;
	
	@Column(name = "finish_time", nullable = false)
	private LocalTime finishTime;

}
