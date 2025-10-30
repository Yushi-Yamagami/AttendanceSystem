-- Insert test users
-- Password for all users is 'password' (BCrypt hashed)
-- BCrypt hash of 'password': $2a$10$8x3rNJcFRfvjVvlEWgYgAeLGLqXpGE/kWYdLwJGLPxGqLz5xq7fxy

-- Student user
INSERT INTO user (user_id, password, email, role, created_at, updated_at) 
VALUES ('student1', '$2a$10$8x3rNJcFRfvjVvlEWgYgAeLGLqXpGE/kWYdLwJGLPxGqLz5xq7fxy', 'student1@example.com', 'STUDENT', NOW(), NOW())
ON DUPLICATE KEY UPDATE user_id=user_id;

-- Teacher user
INSERT INTO user (user_id, password, email, role, created_at, updated_at) 
VALUES ('teacher1', '$2a$10$8x3rNJcFRfvjVvlEWgYgAeLGLqXpGE/kWYdLwJGLPxGqLz5xq7fxy', 'teacher1@example.com', 'TEACHER', NOW(), NOW())
ON DUPLICATE KEY UPDATE user_id=user_id;
