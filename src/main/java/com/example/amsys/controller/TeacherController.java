package com.example.amsys.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.amsys.model.AttendanceRequest;
import com.example.amsys.model.AttendanceRequest.RequestType;
import com.example.amsys.repository.AttendanceRequestRepository;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private AttendanceRequestRepository attendanceRequestRepository;

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

}
