-- 航天知识闯关学习平台 - 数据库建表脚本 (MySQL 8+)
SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS space_knowledge DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE space_knowledge;

-- 年级
CREATE TABLE grades (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(32) NOT NULL COMMENT '年级名称',
    sort_order INT DEFAULT 0
) COMMENT '年级表';

-- 班级
CREATE TABLE classes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    grade_id INT NOT NULL,
    FOREIGN KEY (grade_id) REFERENCES grades(id)
) COMMENT '班级表';

-- 角色
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(32) NOT NULL UNIQUE COMMENT 'STUDENT/TEACHER/ADMIN',
    permissions VARCHAR(512) DEFAULT NULL
) COMMENT '角色表';

-- 用户
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR(128) NOT NULL,
    role_id INT NOT NULL DEFAULT 1,
    real_name VARCHAR(64),
    grade_id INT COMMENT '学生所属年级',
    class_id INT COMMENT '学生所属班级',
    avatar VARCHAR(256),
    points INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (grade_id) REFERENCES grades(id),
    FOREIGN KEY (class_id) REFERENCES classes(id)
) COMMENT '用户表';

-- 知识点树
CREATE TABLE knowledge_points (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    parent_id INT DEFAULT 0,
    grade_level INT COMMENT '适用年级层级',
    description TEXT,
    sort_order INT DEFAULT 0,
    FOREIGN KEY (parent_id) REFERENCES knowledge_points(id)
) COMMENT '知识点表';

-- 题目
CREATE TABLE questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL COMMENT '题干',
    type VARCHAR(16) NOT NULL COMMENT 'SINGLE/MULTIPLE/JUDGE/FILL/SUBJECTIVE',
    difficulty TINYINT NOT NULL DEFAULT 1 COMMENT '1-5',
    grade_id INT,
    teacher_id BIGINT,
    analysis TEXT COMMENT '解析',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    status TINYINT DEFAULT 1 COMMENT '0禁用 1正常',
    FOREIGN KEY (grade_id) REFERENCES grades(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
) COMMENT '题目表';

-- 题目选项
CREATE TABLE question_options (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question_id BIGINT NOT NULL,
    option_label VARCHAR(8) NOT NULL COMMENT 'A/B/C/D',
    option_text TEXT NOT NULL,
    is_correct TINYINT NOT NULL DEFAULT 0,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
) COMMENT '题目选项';

-- 题目-知识点关联
CREATE TABLE question_kp (
    question_id BIGINT NOT NULL,
    kp_id INT NOT NULL,
    PRIMARY KEY (question_id, kp_id),
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (kp_id) REFERENCES knowledge_points(id)
) COMMENT '题目知识点';

-- 题目标签(可选)
CREATE TABLE question_tags (
    question_id BIGINT NOT NULL,
    tag VARCHAR(64) NOT NULL,
    PRIMARY KEY (question_id, tag),
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
) COMMENT '题目标签';

-- 关卡
CREATE TABLE levels (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    grade_id INT NOT NULL,
    theme VARCHAR(64) COMMENT '主题/章节',
    difficulty TINYINT DEFAULT 1,
    required_kps VARCHAR(256) COMMENT '所需知识点ID逗号分隔',
    question_count INT DEFAULT 5 COMMENT '每关题目数',
    pass_score DECIMAL(5,2) DEFAULT 60.00 COMMENT '通过分数百分比',
    sort_order INT DEFAULT 0,
    FOREIGN KEY (grade_id) REFERENCES grades(id)
) COMMENT '关卡表';

-- 关卡-题目池(多对多)
CREATE TABLE level_questions (
    level_id INT NOT NULL,
    question_id BIGINT NOT NULL,
    PRIMARY KEY (level_id, question_id),
    FOREIGN KEY (level_id) REFERENCES levels(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id)
) COMMENT '关卡题目池';

-- 答题记录
CREATE TABLE attempts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    level_id INT COMMENT '若来自闯关',
    answer TEXT COMMENT '用户答案JSON或文本',
    is_correct TINYINT NOT NULL,
    time_spent INT COMMENT '用时秒',
    attempt_no INT DEFAULT 1,
    source VARCHAR(32) DEFAULT 'practice' COMMENT 'level/practice/assigned',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (question_id) REFERENCES questions(id),
    FOREIGN KEY (level_id) REFERENCES levels(id)
) COMMENT '答题记录';
CREATE INDEX idx_attempts_user_time ON attempts(user_id, created_at);
CREATE INDEX idx_attempts_question ON attempts(question_id);

-- 用户关卡通过记录
CREATE TABLE user_level_progress (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    level_id INT NOT NULL,
    score DECIMAL(5,2),
    passed TINYINT NOT NULL DEFAULT 0,
    best_time_spent INT,
    completed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_level (user_id, level_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (level_id) REFERENCES levels(id)
) COMMENT '用户关卡进度';

-- 知识点掌握度
CREATE TABLE user_kp_mastery (
    user_id BIGINT NOT NULL,
    kp_id INT NOT NULL,
    mastery_score DECIMAL(5,4) DEFAULT 0 COMMENT '0-1',
    last_update DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, kp_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (kp_id) REFERENCES knowledge_points(id)
) COMMENT '用户知识点掌握度';

-- 错题/收藏
CREATE TABLE wrong_questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    note TEXT,
    added_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_question (user_id, question_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (question_id) REFERENCES questions(id)
) COMMENT '错题本';

-- 勋章定义
CREATE TABLE badges (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    condition_type VARCHAR(32) COMMENT 'first_pass/streak_5/accuracy_90等',
    condition_value VARCHAR(128),
    icon VARCHAR(256),
    description TEXT
) COMMENT '勋章定义';

-- 用户勋章
CREATE TABLE user_badges (
    user_id BIGINT NOT NULL,
    badge_id INT NOT NULL,
    obtained_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, badge_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (badge_id) REFERENCES badges(id)
) COMMENT '用户获得勋章';

-- 教师导入日志
CREATE TABLE teacher_import_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL,
    file_name VARCHAR(256),
    total_count INT DEFAULT 0,
    success_count INT DEFAULT 0,
    fail_count INT DEFAULT 0,
    status VARCHAR(32) DEFAULT 'processing',
    error_detail TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id)
) COMMENT '批量导入日志';

-- 教师布置任务(闯关/练习)
CREATE TABLE assignments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL,
    class_id INT,
    level_id INT,
    question_ids VARCHAR(1024) COMMENT '题目ID逗号分隔，练习用',
    title VARCHAR(128),
    due_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id),
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (level_id) REFERENCES levels(id)
) COMMENT '布置任务';

-- 初始化角色
INSERT INTO roles (id, name) VALUES (1, 'STUDENT'), (2, 'TEACHER'), (3, 'ADMIN');
-- 初始化年级示例
INSERT INTO grades (id, name, sort_order) VALUES (1,'一年级',1),(2,'二年级',2),(3,'三年级',3),(4,'四年级',4),(5,'五年级',5),(6,'六年级',6),(7,'初一',7),(8,'初二',8),(9,'初三',9);
