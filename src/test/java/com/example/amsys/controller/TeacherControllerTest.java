package com.example.amsys.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.amsys.form.AttendanceInputForm;
import com.example.amsys.model.Attendance;
import com.example.amsys.model.AttendanceId;
import com.example.amsys.model.AttendanceRequest;
import com.example.amsys.model.AttendanceRequest.AttendanceRequestStatus;
import com.example.amsys.model.AttendanceRequest.RequestType;
import com.example.amsys.model.LessonTime;
import com.example.amsys.model.User;
import com.example.amsys.model.User.UserRole;
import com.example.amsys.repository.AttendanceRepository;
import com.example.amsys.repository.AttendanceRequestRepository;
import com.example.amsys.repository.LessonTimeRepository;
import com.example.amsys.repository.UserRepository;
import com.example.amsys.service.AttendanceService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.stream.Collectors;

import com.example.amsys.dto.AttendanceWithUserDto;

/**
 * TeacherControllerのテスト
 */
class TeacherControllerTest {

    @Mock
    private AttendanceRequestRepository attendanceRequestRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private LessonTimeRepository lessonTimeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private TeacherController teacherController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowApprovalList_PendingRequestsExist() {
        // Given
        List<AttendanceRequest> pendingRequests = new ArrayList<>();
        AttendanceRequest request = new AttendanceRequest();
        request.setRequestId(1L);
        request.setStudentId("S001");
        request.setDate(LocalDate.now());
        request.setLessontimeCode((byte) 1);
        request.setStatus(AttendanceRequestStatus.ABSENCE);
        request.setReason("体調不良");
        request.setRequestType(RequestType.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        pendingRequests.add(request);

        when(attendanceRequestRepository.findByRequestTypeOrderByCreatedAtDesc(RequestType.PENDING))
            .thenReturn(pendingRequests);

        // When
        String viewName = teacherController.showApprovalList(model);

        // Then
        assertEquals("teachers/approval", viewName);
        verify(model).addAttribute("pendingRequests", pendingRequests);
        verify(attendanceRequestRepository).findByRequestTypeOrderByCreatedAtDesc(RequestType.PENDING);
    }

    @Test
    void testShowApprovalList_NoPendingRequests() {
        // Given
        when(attendanceRequestRepository.findByRequestTypeOrderByCreatedAtDesc(RequestType.PENDING))
            .thenReturn(new ArrayList<>());

        // When
        String viewName = teacherController.showApprovalList(model);

        // Then
        assertEquals("teachers/approval", viewName);
        verify(model).addAttribute(eq("pendingRequests"), anyList());
    }

    @Test
    void testApproveRequest_Success() {
        // Given
        Long requestId = 1L;
        AttendanceRequest request = new AttendanceRequest();
        request.setRequestId(requestId);
        request.setStudentId("S001");
        request.setRequestType(RequestType.PENDING);

        when(attendanceRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(attendanceRequestRepository.save(any(AttendanceRequest.class))).thenReturn(request);
        when(principal.getName()).thenReturn("T001");

        // When
        String viewName = teacherController.approveRequest(requestId, principal, redirectAttributes);

        // Then
        assertEquals("redirect:/teachers/approval", viewName);
        assertEquals(RequestType.APPROVED, request.getRequestType());
        assertEquals("T001", request.getTeacherId());
        verify(attendanceRequestRepository).save(request);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), contains("承認しました"));
    }

    @Test
    void testApproveRequest_RequestNotFound() {
        // Given
        Long requestId = 999L;
        when(attendanceRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        // When
        String viewName = teacherController.approveRequest(requestId, principal, redirectAttributes);

        // Then
        assertEquals("redirect:/teachers/approval", viewName);
        verify(attendanceRequestRepository, never()).save(any());
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), contains("見つかりませんでした"));
    }

    @Test
    void testApproveRequest_NullPrincipal() {
        // Given
        Long requestId = 1L;
        AttendanceRequest request = new AttendanceRequest();
        request.setRequestId(requestId);
        request.setStudentId("S001");
        request.setRequestType(RequestType.PENDING);

        when(attendanceRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(attendanceRequestRepository.save(any(AttendanceRequest.class))).thenReturn(request);

        // When
        String viewName = teacherController.approveRequest(requestId, null, redirectAttributes);

        // Then
        assertEquals("redirect:/teachers/approval", viewName);
        assertEquals(RequestType.APPROVED, request.getRequestType());
        assertNull(request.getTeacherId());
        verify(attendanceRequestRepository).save(request);
    }

    @Test
    void testApproveRequest_AlreadyApproved() {
        // Given - 既に承認済みの申請
        Long requestId = 1L;
        AttendanceRequest request = new AttendanceRequest();
        request.setRequestId(requestId);
        request.setStudentId("S001");
        request.setRequestType(RequestType.APPROVED);  // 既に承認済み

        when(attendanceRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        // When
        String viewName = teacherController.approveRequest(requestId, principal, redirectAttributes);

        // Then
        assertEquals("redirect:/teachers/approval", viewName);
        // 保存されないことを確認
        verify(attendanceRequestRepository, never()).save(any());
        // エラーメッセージが設定されることを確認
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), contains("既に処理されています"));
    }

    @Test
    void testShowApprovalDetail_Success() {
        // Given
        Long requestId = 1L;
        AttendanceRequest request = new AttendanceRequest();
        request.setRequestId(requestId);
        request.setStudentId("S001");
        request.setDate(LocalDate.now());
        request.setLessontimeCode((byte) 1);
        request.setStatus(AttendanceRequestStatus.ABSENCE);
        request.setReason("体調不良");
        request.setRequestType(RequestType.PENDING);
        request.setCreatedAt(LocalDateTime.now());

        User student = new User();
        student.setUserId("S001");
        student.setLastName("山田");
        student.setFirstName("太郎");

        LessonTime lessonTime = new LessonTime();
        lessonTime.setLessontimeCode((byte) 1);
        lessonTime.setLessontimeName("1限");
        lessonTime.setStartTime(LocalTime.of(9, 0));
        lessonTime.setFinishTime(LocalTime.of(10, 30));

        when(attendanceRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(userRepository.findByUserId("S001")).thenReturn(Optional.of(student));
        when(lessonTimeRepository.findById((byte) 1)).thenReturn(Optional.of(lessonTime));

        // When
        String viewName = teacherController.showApprovalDetail(requestId, model, redirectAttributes);

        // Then
        assertEquals("teachers/approvalDetail", viewName);
        verify(model).addAttribute("request", request);
        verify(model).addAttribute("student", student);
        verify(model).addAttribute("lessonTime", lessonTime);
    }

    @Test
    void testShowApprovalDetail_RequestNotFound() {
        // Given
        Long requestId = 999L;
        when(attendanceRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        // When
        String viewName = teacherController.showApprovalDetail(requestId, model, redirectAttributes);

        // Then
        assertEquals("redirect:/teachers/approval", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), contains("見つかりませんでした"));
    }

    @Test
    void testRejectRequest_Success() {
        // Given
        Long requestId = 1L;
        AttendanceRequest request = new AttendanceRequest();
        request.setRequestId(requestId);
        request.setStudentId("S001");
        request.setRequestType(RequestType.PENDING);

        when(attendanceRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(attendanceRequestRepository.save(any(AttendanceRequest.class))).thenReturn(request);
        when(principal.getName()).thenReturn("T001");

        // When
        String viewName = teacherController.rejectRequest(requestId, principal, redirectAttributes);

        // Then
        assertEquals("redirect:/teachers/approval", viewName);
        assertEquals(RequestType.REJECTED, request.getRequestType());
        assertEquals("T001", request.getTeacherId());
        verify(attendanceRequestRepository).save(request);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), contains("拒否しました"));
    }

    @Test
    void testRejectRequest_RequestNotFound() {
        // Given
        Long requestId = 999L;
        when(attendanceRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        // When
        String viewName = teacherController.rejectRequest(requestId, principal, redirectAttributes);

        // Then
        assertEquals("redirect:/teachers/approval", viewName);
        verify(attendanceRequestRepository, never()).save(any());
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), contains("見つかりませんでした"));
    }

    @Test
    void testRejectRequest_AlreadyProcessed() {
        // Given
        Long requestId = 1L;
        AttendanceRequest request = new AttendanceRequest();
        request.setRequestId(requestId);
        request.setStudentId("S001");
        request.setRequestType(RequestType.APPROVED);  // 既に承認済み

        when(attendanceRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        // When
        String viewName = teacherController.rejectRequest(requestId, principal, redirectAttributes);

        // Then
        assertEquals("redirect:/teachers/approval", viewName);
        verify(attendanceRequestRepository, never()).save(any());
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), contains("既に処理されています"));
    }

    // 出席情報入力機能のテスト

    @Test
    void testShowAttendanceInputForm() {
        // Given
        List<User> students = new ArrayList<>();
        User student = new User();
        student.setId(1L);
        student.setUserId("S001");
        student.setLastName("山田");
        student.setFirstName("太郎");
        student.setRole(UserRole.STUDENT);
        students.add(student);

        List<LessonTime> lessonTimes = new ArrayList<>();
        LessonTime lessonTime = new LessonTime();
        lessonTime.setLessontimeCode((byte) 1);
        lessonTime.setLessontimeName("1限");
        lessonTime.setStartTime(LocalTime.of(9, 0));
        lessonTime.setFinishTime(LocalTime.of(10, 30));
        lessonTimes.add(lessonTime);

        when(model.containsAttribute("attendanceInputForm")).thenReturn(false);
        when(userRepository.findByRoleOrderByUserIdAsc(UserRole.STUDENT)).thenReturn(students);
        when(lessonTimeRepository.findAllByOrderByLessontimeCodeAsc()).thenReturn(lessonTimes);

        // When
        String viewName = teacherController.showAttendanceInputForm(model);

        // Then
        assertEquals("teachers/attendanceInput", viewName);
        verify(model).addAttribute(eq("attendanceInputForm"), any(AttendanceInputForm.class));
        verify(model).addAttribute("students", students);
        verify(model).addAttribute("lessonTimes", lessonTimes);
    }

    @Test
    void testInputAttendance_Success_NewRecord() {
        // Given
        AttendanceInputForm form = new AttendanceInputForm();
        form.setDate(LocalDate.now());
        form.setStudentId(1L);
        form.setLessontimeCode((byte) 1);
        form.setStatusCode("PRESENT");
        form.setReason("");

        AttendanceId attendanceId = new AttendanceId(form.getDate(), form.getStudentId(), form.getLessontimeCode());

        when(bindingResult.hasErrors()).thenReturn(false);
        when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.empty());
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String viewName = teacherController.inputAttendance(form, bindingResult, model, redirectAttributes);

        // Then
        assertEquals("redirect:/teachers/attendance/input", viewName);
        verify(attendanceRepository).save(any(Attendance.class));
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), contains("登録しました"));
    }

    @Test
    void testInputAttendance_Success_UpdateExisting() {
        // Given
        AttendanceInputForm form = new AttendanceInputForm();
        form.setDate(LocalDate.now());
        form.setStudentId(1L);
        form.setLessontimeCode((byte) 1);
        form.setStatusCode("ABSENCE");
        form.setReason("体調不良");

        AttendanceId attendanceId = new AttendanceId(form.getDate(), form.getStudentId(), form.getLessontimeCode());
        Attendance existingAttendance = new Attendance();
        existingAttendance.setId(attendanceId);
        existingAttendance.setStatusCode(Attendance.AttendanceStatus.PRESENT);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.of(existingAttendance));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String viewName = teacherController.inputAttendance(form, bindingResult, model, redirectAttributes);

        // Then
        assertEquals("redirect:/teachers/attendance/input", viewName);
        assertEquals(Attendance.AttendanceStatus.ABSENCE, existingAttendance.getStatusCode());
        assertEquals("体調不良", existingAttendance.getReason());
        verify(attendanceRepository).save(existingAttendance);
    }

    @Test
    void testInputAttendance_ValidationError() {
        // Given
        AttendanceInputForm form = new AttendanceInputForm();

        List<User> students = new ArrayList<>();
        List<LessonTime> lessonTimes = new ArrayList<>();

        when(bindingResult.hasErrors()).thenReturn(true);
        when(userRepository.findByRoleOrderByUserIdAsc(UserRole.STUDENT)).thenReturn(students);
        when(lessonTimeRepository.findAllByOrderByLessontimeCodeAsc()).thenReturn(lessonTimes);

        // When
        String viewName = teacherController.inputAttendance(form, bindingResult, model, redirectAttributes);

        // Then
        assertEquals("teachers/attendanceInput", viewName);
        verify(attendanceRepository, never()).save(any());
        verify(model).addAttribute("students", students);
        verify(model).addAttribute("lessonTimes", lessonTimes);
    }

    // 出席確認機能のテスト

    @Test
    void testShowAttendanceList_InitialDisplay() {
        // Given
        List<LessonTime> lessonTimes = new ArrayList<>();
        LessonTime lessonTime1 = new LessonTime();
        lessonTime1.setLessontimeCode((byte) 1);
        lessonTime1.setLessontimeName("1限");
        lessonTime1.setStartTime(LocalTime.of(9, 0));
        lessonTime1.setFinishTime(LocalTime.of(10, 30));
        lessonTimes.add(lessonTime1);

        when(lessonTimeRepository.findAllByOrderByLessontimeCodeAsc()).thenReturn(lessonTimes);

        // When
        String viewName = teacherController.showAttendanceList(model);

        // Then
        assertEquals("teachers/attendanceList", viewName);
        verify(model).addAttribute(eq("today"), any(LocalDate.class));
        verify(model).addAttribute(eq("gradeList"), anyList());
        verify(model).addAttribute("lessonTimeList", lessonTimes);
    }

    @Test
    void testSearchAttendanceList_WithResults() {
        // Given
        Byte gradeCode = 4;
        Byte lessontimeCode = 1;

        List<LessonTime> lessonTimes = new ArrayList<>();
        LessonTime lessonTime = new LessonTime();
        lessonTime.setLessontimeCode((byte) 1);
        lessonTime.setLessontimeName("1限");
        lessonTime.setStartTime(LocalTime.of(9, 0));
        lessonTime.setFinishTime(LocalTime.of(10, 30));
        lessonTimes.add(lessonTime);

        List<AttendanceWithUserDto> attendanceList = new ArrayList<>();
        AttendanceWithUserDto dto = new AttendanceWithUserDto();
        dto.setDate(LocalDate.now());
        dto.setUserId("T22001");
        dto.setLastName("秋山");
        dto.setFirstName("政人");
        dto.setLastKanaName("あきやま");
        dto.setFirstKanaName("まさと");
        dto.setGradeCode(gradeCode);
        dto.setStatusCode(Attendance.AttendanceStatus.PRESENT);
        dto.setStatusName("出席");
        attendanceList.add(dto);

        when(lessonTimeRepository.findAllByOrderByLessontimeCodeAsc()).thenReturn(lessonTimes);
        when(attendanceService.getAttendanceListByDateGradeAndLessonTime(any(LocalDate.class), eq(gradeCode), eq(lessontimeCode)))
            .thenReturn(attendanceList);

        // When
        String viewName = teacherController.searchAttendanceList(gradeCode, lessontimeCode, model);

        // Then
        assertEquals("teachers/attendanceList", viewName);
        verify(model).addAttribute(eq("today"), any(LocalDate.class));
        verify(model).addAttribute(eq("gradeList"), anyList());
        verify(model).addAttribute("lessonTimeList", lessonTimes);
        verify(model).addAttribute("selectedGrade", gradeCode);
        verify(model).addAttribute("selectedLessonTime", lessontimeCode);
        verify(model).addAttribute("attendanceList", attendanceList);
        verify(attendanceService).getAttendanceListByDateGradeAndLessonTime(any(LocalDate.class), eq(gradeCode), eq(lessontimeCode));
    }

    @Test
    void testSearchAttendanceList_NoResults() {
        // Given
        Byte gradeCode = 1;
        Byte lessontimeCode = 1;

        List<LessonTime> lessonTimes = new ArrayList<>();
        List<AttendanceWithUserDto> emptyList = new ArrayList<>();

        when(lessonTimeRepository.findAllByOrderByLessontimeCodeAsc()).thenReturn(lessonTimes);
        when(attendanceService.getAttendanceListByDateGradeAndLessonTime(any(LocalDate.class), eq(gradeCode), eq(lessontimeCode)))
            .thenReturn(emptyList);

        // When
        String viewName = teacherController.searchAttendanceList(gradeCode, lessontimeCode, model);

        // Then
        assertEquals("teachers/attendanceList", viewName);
        verify(model).addAttribute("attendanceList", emptyList);
    }
}
