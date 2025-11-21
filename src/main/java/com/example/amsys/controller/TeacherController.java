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
	
	/**
	 * 出欠席一覧画面を表示
	 */
	@GetMapping("/attendanceList")
	public String showAttendanceList(Model model) {
		// 今日の日付を設定
		LocalDate today = LocalDate.now();
		model.addAttribute("today", today);
		
		// コマの一覧を取得
		List<LessonTime> lessonTimeList = lessonTimeRepository.findAllByOrderByLessontimeCodeAsc();
		model.addAttribute("lessonTimeList", lessonTimeList);
		
		// 学年の一覧（1年～3年を想定）
		List<Integer> gradeList = List.of(1, 2, 3);
		model.addAttribute("gradeList", gradeList);
		
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
		
		// 今日の日付を設定
		LocalDate today = LocalDate.now();
		model.addAttribute("today", today);
		
		// コマの一覧を取得
		List<LessonTime> lessonTimeList = lessonTimeRepository.findAllByOrderByLessontimeCodeAsc();
		model.addAttribute("lessonTimeList", lessonTimeList);
		
		// 学年の一覧
		List<Integer> gradeList = List.of(1, 2, 3);
		model.addAttribute("gradeList", gradeList);
		
		// 選択した値を保持
		model.addAttribute("selectedGrade", gradeCode);
		model.addAttribute("selectedLessonTime", lessontimeCode);
		
		// 学年とコマが選択されている場合のみ検索
		if (gradeCode != null && lessontimeCode != null) {
			List<AttendanceWithUserDto> attendanceList = 
					attendanceService.getAttendanceListByDateGradeAndLessonTime(today, gradeCode, lessontimeCode);
			model.addAttribute("attendanceList", attendanceList);
		}
		
		return "teachers/attendanceList";
	}

}
