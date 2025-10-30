-- データベース初期化用SQLスクリプト
-- このスクリプトはデータベースを初期状態にリセットするために使用されます

-- 既存のデータを削除
DELETE FROM attendance;
DELETE FROM user;

-- テスト用のユーザーを追加
-- パスワードは "password" をBCryptでハッシュ化したもの
-- BCrypt hash of "password": $2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.

-- 教師ユーザー
INSERT INTO user (id, user_id, password, email, role, created_at, updated_at) VALUES
(1, 'teacher1', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'teacher1@example.com', 'TEACHER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'teacher2', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'teacher2@example.com', 'TEACHER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 学生ユーザー
INSERT INTO user (id, user_id, password, email, role, created_at, updated_at) VALUES
(3, 'student1', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'student1@example.com', 'STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'student2', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'student2@example.com', 'STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'student3', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'student3@example.com', 'STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'student4', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'student4@example.com', 'STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'student5', '$2a$10$EblZqNptyYvcLm/VwDCVAuBjzZOI7khzdyGPBr08PpIi0na624b8.', 'student5@example.com', 'STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- サンプルの出席データ
-- lesson_time_code: 1限、2限、3限、4限、5限
INSERT INTO attendance (date, student_id, lesson_time_code, status, reason) VALUES
('2025-10-15', 'student1', '1限', 'ATTENDANCE', ''),
('2025-10-15', 'student1', '2限', 'ATTENDANCE', ''),
('2025-10-15', 'student2', '1限', 'ABSENCE', '体調不良'),
('2025-10-15', 'student2', '2限', 'ABSENCE', '体調不良'),
('2025-10-15', 'student3', '1限', 'ATTENDANCE', ''),
('2025-10-15', 'student3', '2限', 'LATENESS', '電車遅延'),
('2025-10-16', 'student1', '1限', 'ATTENDANCE', ''),
('2025-10-16', 'student2', '1限', 'ATTENDANCE', ''),
('2025-10-16', 'student3', '1限', 'EARLY', '体調不良'),
('2025-10-17', 'student1', '1限', 'ATTENDANCE', ''),
('2025-10-17', 'student1', '2限', 'ATTENDANCE', ''),
('2025-10-17', 'student1', '3限', 'ATTENDANCE', '');
