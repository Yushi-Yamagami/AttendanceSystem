package com.example.amsys.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* ログインユーザー用モデルクラス */

@Entity
@Table(name = "user")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name = "grade_code")
    private Byte gradeCode;

    @Column(name = "last_name", nullable = false, length = 16)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 16)
    private String firstName;

    @Column(name = "last_kana_name", length = 24)
    private String lastKanaName;

    @Column(name = "first_kana_name", length = 24)
    private String firstKanaName;

    @Column(unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;
    
    public enum UserRole {
        STUDENT, TEACHER
    }

}
