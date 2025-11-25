package com.example.amsys.form;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 出席情報入力フォーム
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceInputForm {
    
    @NotNull(message = "日付は必須です")
    private LocalDate date;
    
    @NotNull(message = "学籍番号は必須です")
    private Long studentId;
    
    @NotNull(message = "時限は必須です")
    private Byte lessontimeCode;
    
    @NotNull(message = "状態は必須です")
    private String statusCode;
    
    private String reason;
    
    // 表示用
    private String studentName;
    private String lessontimeName;

}
