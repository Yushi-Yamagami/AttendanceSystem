-- Insert test users for H2 database
-- Password for all users is 'password' (BCrypt hashed)
-- BCrypt hash of 'password': $2a$10$8x3rNJcFRfvjVvlEWgYgAeLGLqXpGE/kWYdLwJGLPxGqLz5xq7fxy

-- Student user
MERGE INTO user (id, user_id, password, email, role, created_at, updated_at) 
KEY(user_id) 
VALUES (1, 'student1', '$2a$10$8x3rNJcFRfvjVvlEWgYgAeLGLqXpGE/kWYdLwJGLPxGqLz5xq7fxy', 'student1@example.com', 'STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Teacher user
MERGE INTO user (id, user_id, password, email, role, created_at, updated_at) 
KEY(user_id) 
VALUES (2, 'teacher1', '$2a$10$8x3rNJcFRfvjVvlEWgYgAeLGLqXpGE/kWYdLwJGLPxGqLz5xq7fxy', 'teacher1@example.com', 'TEACHER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
