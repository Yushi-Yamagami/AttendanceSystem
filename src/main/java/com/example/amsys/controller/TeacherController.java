package com.example.amsys.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.amsys.dto.AttendanceWithUserDto;
import com.example.amsys.model.LessonTime;
import com.example.amsys.repository.LessonTimeRepository;
import com.example.amsys.service.AttendanceService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {
	
	private final AttendanceService attendanceService;
	private final LessonTimeRepository lessonTimeRepository;
	
	// 学年の一覧（1年～3年を想定）
	private static final List<Integer> GRADE_LIST = List.of(1, 2, 3);
	
	/**
	 * 出欠席一覧画面を表示
	 */
	@GetMapping("/attendanceList")
	public String showAttendanceList(Model model) {
		setupCommonModelAttributes(model, null, null);
		return "teachers/attendanceList";
	}
	
	/**
	 * 出欠席一覧を検索
	 */
	@PostMapping("/attendanceList")
	public String searchAttendanceList(
			@RequestParam(required = false) Byte gradeCode,
			@RequestParam(required = false) Byte lessontimeCode,
			Model model) {
		
		setupCommonModelAttributes(model, gradeCode, lessontimeCode);
		
		// 学年とコマが選択されている場合のみ検索
		if (gradeCode != null && lessontimeCode != null) {
			LocalDate today = LocalDate.now();
			List<AttendanceWithUserDto> attendanceList = 
					attendanceService.getAttendanceListByDateGradeAndLessonTime(today, gradeCode, lessontimeCode);
			model.addAttribute("attendanceList", attendanceList);
		}
		
		return "teachers/attendanceList";
	}
	
	/**
	 * 共通のモデル属性を設定
	 */
	private void setupCommonModelAttributes(Model model, Byte selectedGrade, Byte selectedLessonTime) {
		// 今日の日付を設定
		LocalDate today = LocalDate.now();
		model.addAttribute("today", today);
		
		// コマの一覧を取得
		List<LessonTime> lessonTimeList = lessonTimeRepository.findAllByOrderByLessontimeCodeAsc();
		model.addAttribute("lessonTimeList", lessonTimeList);
		
		// 学年の一覧
		model.addAttribute("gradeList", GRADE_LIST);
		
		// 選択した値を保持
		if (selectedGrade != null) {
			model.addAttribute("selectedGrade", selectedGrade);
		}
		if (selectedLessonTime != null) {
			model.addAttribute("selectedLessonTime", selectedLessonTime);
		}
	}

}
