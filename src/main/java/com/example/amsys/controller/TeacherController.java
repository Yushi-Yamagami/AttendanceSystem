package com.example.amsys.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.amsys.dto.AttendanceWithUserDto;
import com.example.amsys.form.AttendanceInputForm;
import com.example.amsys.form.BatchAttendanceForm;
import com.example.amsys.model.Attendance;
import com.example.amsys.model.AttendanceId;
import com.example.amsys.model.AttendanceRequest;
import com.example.amsys.model.AttendanceRequest.RequestType;
import com.example.amsys.model.LessonTime;
import com.example.amsys.model.User;
import com.example.amsys.model.User.UserRole;
import com.example.amsys.repository.AttendanceRepository;
import com.example.amsys.repository.AttendanceRequestRepository;
import com.example.amsys.repository.LessonTimeRepository;
import com.example.amsys.repository.UserRepository;
import com.example.amsys.service.AttendanceService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private AttendanceRequestRepository attendanceRequestRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private LessonTimeRepository lessonTimeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AttendanceService attendanceService;

    /**
     * 承認待ちの申請一覧を表示
     */
    @GetMapping("/approval")
    public String showApprovalList(Model model) {
        List<AttendanceRequest> pendingRequests = 
            attendanceRequestRepository.findByRequestTypeOrderByCreatedAtDesc(RequestType.PENDING);
        model.addAttribute("pendingRequests", pendingRequests);
        return "teachers/approval";
    }

    /**
     * 申請を承認する
     */
    @PostMapping("/approval/{requestId}/approve")
    public String approveRequest(
            @PathVariable Long requestId,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        
        Optional<AttendanceRequest> requestOpt = attendanceRequestRepository.findById(requestId);
        
        if (requestOpt.isPresent()) {
            AttendanceRequest request = requestOpt.get();
            
            // PENDING状態の申請のみ承認可能
            if (request.getRequestType() != RequestType.PENDING) {
                redirectAttributes.addFlashAttribute("errorMessage", "この申請は既に処理されています。");
                return "redirect:/teachers/approval";
            }
            
            request.setRequestType(RequestType.APPROVED);
            if (principal != null) {
                request.setTeacherId(principal.getName());
            }
            attendanceRequestRepository.save(request);
            redirectAttributes.addFlashAttribute("successMessage", "申請ID " + requestId + " を承認しました。");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "申請が見つかりませんでした。");
        }
        
        return "redirect:/teachers/approval";
    }

    /**
     * 出席情報入力画面を表示
     */
    @GetMapping("/attendance/input")
    public String showAttendanceInputForm(Model model) {
        if (!model.containsAttribute("attendanceInputForm")) {
            AttendanceInputForm form = new AttendanceInputForm();
            form.setDate(LocalDate.now());
            model.addAttribute("attendanceInputForm", form);
        }
        
        // 学生一覧を取得
        List<User> students = userRepository.findByRoleOrderByUserIdAsc(UserRole.STUDENT);
        model.addAttribute("students", students);
        
        // 時限一覧を取得
        List<LessonTime> lessonTimes = lessonTimeRepository.findAllByOrderByLessontimeCodeAsc();
        model.addAttribute("lessonTimes", lessonTimes);
        
        return "teachers/attendanceInput";
    }

    /**
     * 出席情報を登録する
     */
    @PostMapping("/attendance/input")
    public String inputAttendance(
            @Valid @ModelAttribute("attendanceInputForm") AttendanceInputForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            // 学生一覧を取得
            List<User> students = userRepository.findByRoleOrderByUserIdAsc(UserRole.STUDENT);
            model.addAttribute("students", students);
            
            // 時限一覧を取得
            List<LessonTime> lessonTimes = lessonTimeRepository.findAllByOrderByLessontimeCodeAsc();
            model.addAttribute("lessonTimes", lessonTimes);
            
            return "teachers/attendanceInput";
        }
        
        // 出席情報の複合キーを作成
        AttendanceId attendanceId = new AttendanceId(
            form.getDate(),
            form.getStudentId(),
            form.getLessontimeCode()
        );
        
        // 既存の出席情報があるか確認
        Optional<Attendance> existingAttendance = attendanceRepository.findById(attendanceId);
        
        Attendance attendance;
        if (existingAttendance.isPresent()) {
            // 既存レコードを更新
            attendance = existingAttendance.get();
        } else {
            // 新規レコードを作成
            attendance = new Attendance();
            attendance.setId(attendanceId);
        }
        
        // ステータスを設定
        attendance.setStatusCode(Attendance.AttendanceStatus.valueOf(form.getStatusCode()));
        attendance.setReason(form.getReason());
        
        // 保存
        attendanceRepository.save(attendance);
        
        redirectAttributes.addFlashAttribute("successMessage", "出席情報を登録しました。");
        return "redirect:/teachers/attendance/input";
    }

    /**
     * 出席確認画面を表示（GET）
     */
    @GetMapping("/attendanceList")
    public String showAttendanceList(Model model) {
        setupAttendanceListModel(model);
        return "teachers/attendanceList";
    }

    /**
     * 出席確認検索処理（POST）
     */
    @PostMapping("/attendanceList")
    public String searchAttendanceList(
            @RequestParam("gradeCode") Byte gradeCode,
            @RequestParam("lessontimeCode") Byte lessontimeCode,
            Model model) {
        
        setupAttendanceListModel(model);
        
        // 選択された値を保持
        model.addAttribute("selectedGrade", gradeCode);
        model.addAttribute("selectedLessonTime", lessontimeCode);
        
        // 出席情報を取得
        List<AttendanceWithUserDto> attendanceList = attendanceService.getAttendanceListByDateGradeAndLessonTime(
            LocalDate.now(), gradeCode, lessontimeCode);
        model.addAttribute("attendanceList", attendanceList);
        
        return "teachers/attendanceList";
    }

    /**
     * 出席確認画面の共通モデル設定
     */
    private void setupAttendanceListModel(Model model) {
        model.addAttribute("today", LocalDate.now());
        
        // 学年リストを設定（1年生から4年生まで）
        List<Byte> gradeList = List.of((byte) 1, (byte) 2, (byte) 3, (byte) 4);
        model.addAttribute("gradeList", gradeList);
        
        // 時限一覧を取得
        List<LessonTime> lessonTimeList = lessonTimeRepository.findAllByOrderByLessontimeCodeAsc();
        model.addAttribute("lessonTimeList", lessonTimeList);
    }

    /**
     * 一括出席登録画面を表示（GET）
     */
    @GetMapping("/confirmation")
    public String showBatchAttendanceForm(Model model) {
        setupAttendanceListModel(model);
        return "teachers/confirmation";
    }

    /**
     * 一括出席登録画面を表示（POST - 学年と時限で検索）
     */
    @PostMapping("/confirmation")
    public String searchForBatchAttendance(
            @RequestParam("gradeCode") Byte gradeCode,
            @RequestParam("lessontimeCode") Byte lessontimeCode,
            Model model) {
        
        setupAttendanceListModel(model);
        
        // 選択された値を保持
        model.addAttribute("selectedGrade", gradeCode);
        model.addAttribute("selectedLessonTime", lessontimeCode);
        
        // 出席情報を取得
        List<AttendanceWithUserDto> attendanceList = attendanceService.getAttendanceListByDateGradeAndLessonTime(
            LocalDate.now(), gradeCode, lessontimeCode);
        model.addAttribute("attendanceList", attendanceList);
        
        // バッチフォームを準備
        BatchAttendanceForm batchForm = new BatchAttendanceForm();
        batchForm.setDate(LocalDate.now());
        batchForm.setGradeCode(gradeCode);
        batchForm.setLessontimeCode(lessontimeCode);
        
        // 学生ごとのフォームを作成
        List<BatchAttendanceForm.StudentAttendanceForm> studentForms = new ArrayList<>();
        for (AttendanceWithUserDto dto : attendanceList) {
            BatchAttendanceForm.StudentAttendanceForm studentForm = new BatchAttendanceForm.StudentAttendanceForm();
            studentForm.setStudentId(dto.getStudentId());
            studentForm.setUserId(dto.getUserId());
            studentForm.setStudentName(dto.getLastName() + " " + dto.getFirstName());
            studentForm.setStatusCode(dto.getStatusCode() != null ? dto.getStatusCode().name() : Attendance.AttendanceStatus.PRESENT.name());
            studentForm.setReason(null);
            studentForms.add(studentForm);
        }
        batchForm.setStudentAttendances(studentForms);
        
        model.addAttribute("batchAttendanceForm", batchForm);
        
        return "teachers/confirmation";
    }

    /**
     * 一括出席登録を実行
     */
    @PostMapping("/confirmation/submit")
    public String submitBatchAttendance(
            @Valid @ModelAttribute("batchAttendanceForm") BatchAttendanceForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            setupAttendanceListModel(model);
            
            // エラーがある場合は出席情報を再取得
            List<AttendanceWithUserDto> attendanceList = attendanceService.getAttendanceListByDateGradeAndLessonTime(
                form.getDate(), form.getGradeCode(), form.getLessontimeCode());
            model.addAttribute("attendanceList", attendanceList);
            model.addAttribute("selectedGrade", form.getGradeCode());
            model.addAttribute("selectedLessonTime", form.getLessontimeCode());
            
            return "teachers/confirmation";
        }
        
        // 各学生の出席情報を保存
        for (BatchAttendanceForm.StudentAttendanceForm studentForm : form.getStudentAttendances()) {
            AttendanceId attendanceId = new AttendanceId(
                form.getDate(),
                studentForm.getStudentId(),
                form.getLessontimeCode()
            );
            
            // 既存の出席情報があるか確認
            Optional<Attendance> existingAttendance = attendanceRepository.findById(attendanceId);
            
            Attendance attendance;
            if (existingAttendance.isPresent()) {
                // 既存レコードを更新
                attendance = existingAttendance.get();
            } else {
                // 新規レコードを作成
                attendance = new Attendance();
                attendance.setId(attendanceId);
            }
            
            // ステータスを設定
            attendance.setStatusCode(Attendance.AttendanceStatus.valueOf(studentForm.getStatusCode()));
            attendance.setReason(null);
            
            // 保存
            attendanceRepository.save(attendance);
        }
        
        redirectAttributes.addFlashAttribute("successMessage", 
            form.getStudentAttendances().size() + "件の出席情報を登録しました。");
        return "redirect:/teachers/confirmation";
    }

}
