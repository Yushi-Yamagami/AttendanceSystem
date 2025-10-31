package com.example.amsys.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SecurityConfigのパスワードエンコーダーのテスト
 */
class SecurityConfigTest {

    @Test
    void testDelegatingPasswordEncoderSupportsNoopPasswords() {
        // DelegatingPasswordEncoderを作成
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        
        // {noop}プレフィックス付きの平文パスワードをテスト
        String rawPassword = "password123";
        String noopEncodedPassword = "{noop}password123";
        
        // 平文パスワードが正しくマッチすることを確認
        assertTrue(encoder.matches(rawPassword, noopEncodedPassword), 
            "{noop}プレフィックス付きの平文パスワードが正しくマッチしませんでした");
    }

    @Test
    void testDelegatingPasswordEncoderRejectsWrongPassword() {
        // DelegatingPasswordEncoderを作成
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        
        // 正しくないパスワード
        String wrongPassword = "wrongpassword";
        String correctEncodedPassword = "{noop}password123";
        
        // 間違ったパスワードがマッチしないことを確認
        assertFalse(encoder.matches(wrongPassword, correctEncodedPassword),
            "間違ったパスワードがマッチしてしまいました");
    }

    @Test
    void testDelegatingPasswordEncoderDefaultEncoding() {
        // DelegatingPasswordEncoderを作成
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        
        // 新しいパスワードをエンコード（デフォルトではbcryptを使用）
        String rawPassword = "newpassword123";
        String encodedPassword = encoder.encode(rawPassword);
        
        // エンコードされたパスワードが{bcrypt}で始まることを確認
        assertTrue(encodedPassword.startsWith("{bcrypt}"),
            "デフォルトのエンコーディングがBCryptではありません");
        
        // エンコードされたパスワードが正しくマッチすることを確認
        assertTrue(encoder.matches(rawPassword, encodedPassword),
            "エンコードされたパスワードが正しくマッチしませんでした");
    }
    
    @Test
    void testDelegatingPasswordEncoderSupportsBcryptPasswords() {
        // DelegatingPasswordEncoderを作成
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        
        // まず新しいパスワードをBCryptでエンコード
        String rawPassword = "testpassword";
        String encodedPassword = encoder.encode(rawPassword);
        
        // エンコードされたパスワードが正しくマッチすることを確認
        assertTrue(encoder.matches(rawPassword, encodedPassword),
            "BCryptでエンコードされたパスワードが正しくマッチしませんでした");
        
        // 間違ったパスワードがマッチしないことを確認
        assertFalse(encoder.matches("wrongpassword", encodedPassword),
            "間違ったパスワードがBCryptエンコードされたパスワードとマッチしてしまいました");
    }
}
