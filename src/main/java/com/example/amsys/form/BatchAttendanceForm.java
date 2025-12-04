package com.example.amsys.form;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 一括出席情報入力フォーム
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatchAttendanceForm {
    
    @NotNull(message = "日付は必須です")
    private LocalDate date;
    
    @NotNull(message = "学年は必須です")
    private Byte gradeCode;
    
    @NotNull(message = "時限は必須です")
    private Byte lessontimeCode;
    
    @Valid
    private List<StudentAttendanceForm> studentAttendances;
    
    /**
     * 各学生の出席情報
     */
    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StudentAttendanceForm {
        
        @NotNull(message = "学生IDは必須です")
        private Long studentId;
        
        private String userId;
        private String studentName;
        
        @NotNull(message = "状態は必須です")
        private String statusCode;
        
        private String reason;
    }
}
