package com.example.amsys.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.amsys.dto.AttendanceWithUserDto;
import com.example.amsys.model.Attendance;
import com.example.amsys.model.LessonTime;
import com.example.amsys.model.User;
import com.example.amsys.repository.AttendanceRepository;
import com.example.amsys.repository.LessonTimeRepository;
import com.example.amsys.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {
	
	private final AttendanceRepository attendanceRepository;
	private final UserRepository userRepository;
	private final LessonTimeRepository lessonTimeRepository;
	
	/**
	 * 指定した日付、学年、コマの出欠情報を取得
	 */
	public List<AttendanceWithUserDto> getAttendanceListByDateGradeAndLessonTime(
			LocalDate date, Byte gradeCode, Byte lessontimeCode) {
		
		List<AttendanceWithUserDto> result = new ArrayList<>();
		
		// 指定した学年の学生を取得（データベースレベルでフィルタリング）
		List<User> students = userRepository.findByRoleAndGradeCodeOrderByUserId(User.UserRole.STUDENT, gradeCode);
		
		// コマの情報を取得
		LessonTime lessonTime = lessonTimeRepository.findById(lessontimeCode).orElse(null);
		
		for (User student : students) {
			AttendanceWithUserDto dto = new AttendanceWithUserDto();
			dto.setDate(date);
			dto.setStudentId(student.getId());
			dto.setUserId(student.getUserId());
			dto.setGradeCode(student.getGradeCode());
			dto.setLastName(student.getLastName());
			dto.setFirstName(student.getFirstName());
			dto.setLastKanaName(student.getLastKanaName());
			dto.setFirstKanaName(student.getFirstKanaName());
			dto.setLessontimeCode(lessontimeCode);
			
			if (lessonTime != null) {
				dto.setLessontimeName(lessonTime.getLessontimeName());
			}
			
			// 出欠情報を取得
			Attendance attendance = attendanceRepository.findById(
					new com.example.amsys.model.AttendanceId(date, student.getId(), lessontimeCode))
					.orElse(null);
			
			if (attendance != null) {
				dto.setStatusCode(attendance.getStatusCode());
				dto.setStatusName(getStatusName(attendance.getStatusCode()));
				dto.setReason(attendance.getReason());
			} else {
				// 出欠データがない場合は「未記録」
				dto.setStatusCode(Attendance.AttendanceStatus.NONE);
				dto.setStatusName("未記録");
			}
			
			result.add(dto);
		}
		
		return result;
	}
	
	/**
	 * ステータスコードから日本語のステータス名を取得
	 */
	private String getStatusName(Attendance.AttendanceStatus status) {
		return switch (status) {
			case PRESENT -> "出席";
			case ABSENCE -> "欠席";
			case LATE -> "遅刻";
			case LEAVE_EARLY -> "早退";
			case NONE -> "未記録";
		};
	}
	
	/**
	 * 指定した日付範囲、学年、コマの出欠情報を取得（月間レポート用）
	 */
	public List<AttendanceWithUserDto> getMonthlyAttendanceReport(
			LocalDate startDate, LocalDate endDate, Byte gradeCode, Byte lessontimeCode) {
		
		List<AttendanceWithUserDto> result = new ArrayList<>();
		
		// 学生リストを取得（学年フィルタがある場合はフィルタを適用）
		List<User> students;
		if (gradeCode != null) {
			students = userRepository.findByRoleAndGradeCodeOrderByUserId(User.UserRole.STUDENT, gradeCode);
		} else {
			students = userRepository.findByRoleOrderByUserIdAsc(User.UserRole.STUDENT);
		}
		
		// 時限リスト（フィルタがある場合は特定の時限のみ）
		List<LessonTime> lessonTimes;
		if (lessontimeCode != null) {
			LessonTime lessonTime = lessonTimeRepository.findById(lessontimeCode).orElse(null);
			lessonTimes = lessonTime != null ? List.of(lessonTime) : List.of();
		} else {
			lessonTimes = lessonTimeRepository.findAllByOrderByLessontimeCodeAsc();
		}
		
		// 日付範囲内の各日付について処理
		for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			for (User student : students) {
				for (LessonTime lessonTime : lessonTimes) {
					AttendanceWithUserDto dto = new AttendanceWithUserDto();
					dto.setDate(date);
					dto.setStudentId(student.getId());
					dto.setUserId(student.getUserId());
					dto.setGradeCode(student.getGradeCode());
					dto.setLastName(student.getLastName());
					dto.setFirstName(student.getFirstName());
					dto.setLastKanaName(student.getLastKanaName());
					dto.setFirstKanaName(student.getFirstKanaName());
					dto.setLessontimeCode(lessonTime.getLessontimeCode());
					dto.setLessontimeName(lessonTime.getLessontimeName());
					
					// 出欠情報を取得
					Attendance attendance = attendanceRepository.findById(
							new com.example.amsys.model.AttendanceId(date, student.getId(), lessonTime.getLessontimeCode()))
							.orElse(null);
					
					if (attendance != null) {
						dto.setStatusCode(attendance.getStatusCode());
						dto.setStatusName(getStatusName(attendance.getStatusCode()));
						dto.setReason(attendance.getReason());
					} else {
						// 出欠データがない場合は「未記録」
						dto.setStatusCode(Attendance.AttendanceStatus.NONE);
						dto.setStatusName("未記録");
					}
					
					result.add(dto);
				}
			}
		}
		
		return result;
	}

}
