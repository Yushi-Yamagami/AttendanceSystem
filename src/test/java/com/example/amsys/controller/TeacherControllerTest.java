package com.example.amsys.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.amsys.model.AttendanceRequest;
import com.example.amsys.model.AttendanceRequest.AttendanceRequestStatus;
import com.example.amsys.model.AttendanceRequest.RequestType;
import com.example.amsys.repository.AttendanceRequestRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.Principal;

/**
 * TeacherControllerのテスト
 */
class TeacherControllerTest {

    @Mock
    private AttendanceRequestRepository attendanceRequestRepository;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @Mock
    private RedirectAttributes redirectAttributes;

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
}
