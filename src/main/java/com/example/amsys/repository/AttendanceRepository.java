package com.example.amsys.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.amsys.model.Attendance;
import com.example.amsys.model.AttendanceId;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceId> {
	
	/**
	 * 指定した日付の出欠情報を取得
	 */
	List<Attendance> findByIdDate(LocalDate date);
	
	/**
	 * 指定した日付とコマの出欠情報を取得
	 */
	List<Attendance> findByIdDateAndIdLessontimeCode(LocalDate date, Byte lessontimeCode);
	
	/**
	 * 指定した日付、学年、コマの出欠情報を取得（学生情報を含む）
	 */
	@Query("SELECT a FROM Attendance a JOIN User u ON a.id.studentId = u.id " +
	       "WHERE a.id.date = :date AND u.gradeCode = :gradeCode AND a.id.lessontimeCode = :lessontimeCode " +
	       "ORDER BY u.userId")
	List<Attendance> findByDateAndGradeAndLessonTime(
			@Param("date") LocalDate date,
			@Param("gradeCode") Byte gradeCode,
			@Param("lessontimeCode") Byte lessontimeCode);
	
	/**
	 * 指定した日付と学年の出欠情報を取得（学生情報を含む）
	 */
	@Query("SELECT a FROM Attendance a JOIN User u ON a.id.studentId = u.id " +
	       "WHERE a.id.date = :date AND u.gradeCode = :gradeCode " +
	       "ORDER BY a.id.lessontimeCode, u.userId")
	List<Attendance> findByDateAndGrade(
			@Param("date") LocalDate date,
			@Param("gradeCode") Byte gradeCode);

}
