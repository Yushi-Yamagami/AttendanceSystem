package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.service.DatabaseResetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * データベースリセット機能のコントローラー
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class DatabaseResetController {

    private final DatabaseResetService databaseResetService;

    /**
     * データベースを初期状態にリセット
     * TEACHER権限を持つユーザーのみが実行可能
     * 
     * @return リセット結果
     */
    @PostMapping("/reset-database")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    @ResponseBody
    public ResponseEntity<String> resetDatabase() {
        try {
            log.info("データベースリセットがリクエストされました");
            databaseResetService.resetDatabase();
            return ResponseEntity.ok("データベースが正常にリセットされました");
        } catch (Exception e) {
            log.error("データベースのリセットに失敗しました", e);
            return ResponseEntity.internalServerError()
                    .body("データベースのリセットに失敗しました。管理者に連絡してください。");
        }
    }
}
