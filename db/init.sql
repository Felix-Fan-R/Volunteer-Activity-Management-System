CREATE DATABASE IF NOT EXISTS eco_volunteer;
USE eco_volunteer;

-- 用户表
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'ORGANIZER', 'VOLUNTEER') NOT NULL,
    status ENUM('PENDING', 'ACTIVE', 'REJECTED', 'DISABLED') DEFAULT 'PENDING',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 活动分类表
CREATE TABLE activity_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 活动表
CREATE TABLE activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    category_id BIGINT,
    organizer_id BIGINT,
    location VARCHAR(200),
    start_time DATETIME,
    end_time DATETIME,
    status ENUM('DRAFT', 'PENDING', 'APPROVED', 'REJECTED', 'ONGOING', 'FINISHED', 'CANCELLED') DEFAULT 'DRAFT',
    reject_reason TEXT,
    max_participants INT DEFAULT 0,
    current_participants INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES activity_category(id),
    FOREIGN KEY (organizer_id) REFERENCES user(id)
);

-- 活动报名表
CREATE TABLE activity_registration (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT,
    volunteer_id BIGINT,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (activity_id) REFERENCES activity(id),
    FOREIGN KEY (volunteer_id) REFERENCES user(id)
);

-- 环保知识表
CREATE TABLE eco_knowledge (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    author_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES user(id)
);

-- 系统公告表
CREATE TABLE announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    author_id BIGINT,
    is_top BOOLEAN DEFAULT FALSE,  -- 是否置顶
    top_time DATETIME,            -- 置顶时间
    status ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') DEFAULT 'DRAFT',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES user(id)
);

-- 活动评价表
CREATE TABLE activity_feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT,
    volunteer_id BIGINT,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (activity_id) REFERENCES activity(id),
    FOREIGN KEY (volunteer_id) REFERENCES user(id)
);

-- 初始管理员账号
INSERT INTO user (username, password, role, status) 
VALUES ('admin', '123456', 'ADMIN', 'ACTIVE');

-- 添加测试用户
INSERT INTO user (username, password, role, status) 
VALUES 
('organizer1', '123456', 'ORGANIZER', 'ACTIVE'),
('volunteer1', '123456', 'VOLUNTEER', 'ACTIVE');

-- 添加活动分类
INSERT INTO activity_category (name, description) 
VALUES 
('环境保护', '保护环境、清洁地球'),
('生态保育', '保护野生动植物及其栖息地'),
('垃圾分类', '推广垃圾分类知识和实践'),
('节能减排', '推广节能环保理念');

-- 添加测试公告
INSERT INTO announcement (title, content, author_id, status, is_top) 
VALUES 
('系统上线公告', '环保公益活动管理系统正式上线，欢迎大家使用！', 1, 'PUBLISHED', TRUE),
('志愿者招募', '诚招环保志愿者，一起为环保事业贡献力量！', 1, 'PUBLISHED', FALSE);

-- 添加测试环保知识
INSERT INTO eco_knowledge (title, content, author_id) 
VALUES 
('垃圾分类基础知识', '垃圾分类包括可回收物、有害垃圾、厨余垃圾和其他垃圾...', 1),
('节能小贴士', '日常生活中的节能小技巧：1. 及时关闭不用的电器...', 1);