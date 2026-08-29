-- MySQL Database Setup Script for TechApp
-- Run this script to create the database, user, and tables

-- Create database
CREATE DATABASE IF NOT EXISTS myappdb;
USE myappdb;

-- Create user and grant privileges
CREATE USER IF NOT EXISTS 'myappuser'@'localhost' IDENTIFIED BY 'mypassword';
GRANT ALL PRIVILEGES ON myappdb.* TO 'myappuser'@'localhost';
FLUSH PRIVILEGES;

-- Create tech_tips table
CREATE TABLE IF NOT EXISTS tech_tips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    video_link VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create app_links table
CREATE TABLE IF NOT EXISTS app_links (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    playstore_url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert sample data for tech_tips
INSERT INTO tech_tips (title, description, video_link) VALUES
('Android Development Tips', 'Learn essential Android development patterns and best practices', 'https://www.youtube.com/watch?v=sample1'),
('Java Programming Basics', 'Master the fundamentals of Java programming language', 'https://www.youtube.com/watch?v=sample2'),
('Spring Boot REST API', 'Build robust REST APIs with Spring Boot framework', 'https://www.youtube.com/watch?v=sample3');

-- Insert sample data for app_links
INSERT INTO app_links (name, playstore_url) VALUES
('WhatsApp', 'https://play.google.com/store/apps/details?id=com.whatsapp'),
('Instagram', 'https://play.google.com/store/apps/details?id=com.instagram.android'),
('TikTok', 'https://play.google.com/store/apps/details?id=com.zhiliaoapp.musically');

-- Display created tables
SHOW TABLES;

SELECT 'Database setup completed successfully!' AS Status;
