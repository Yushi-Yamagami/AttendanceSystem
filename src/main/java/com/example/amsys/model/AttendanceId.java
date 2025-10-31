package com.example.amsys.model;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AttendanceId implements Serializable {
	
	private LocalDate date;
	private Long studentId;
	private Byte lessontimeCode;

}
