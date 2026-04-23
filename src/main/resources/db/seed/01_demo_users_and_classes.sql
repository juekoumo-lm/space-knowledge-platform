-- 1) 演示班级与账号（学生/老师）
USE space_knowledge;

-- 班级（与 grades 表关联：3=三年级，5=五年级，7=初一）
INSERT INTO classes (id, name, grade_id) VALUES
(6001, '三年级1班', 3),
(6002, '五年级2班', 5),
(6003, '初一3班', 7)
ON DUPLICATE KEY UPDATE name = VALUES(name), grade_id = VALUES(grade_id);

-- 账号密码说明：
-- teacher01 / teacher123
-- student01 / student123
-- student02 / student123
-- student03 / student123
-- student04 / 123456
INSERT INTO users (id, username, password_hash, role_id, real_name, grade_id, class_id, points, create_time)
VALUES
(2001, 'teacher01', '$2a$10$TqJ5levSUY9EnNlZKhQTc.OUcY/BWFzfL92FLNY/eh5xRNRkNUiSG', 2, '张老师', 7, 6003, 120, NOW()),
(2002, 'student01', '$2a$10$aY6/lgRMKrEkQmALb675yOIN/AWFb/MOsaq1NrsBiAch0GmRV7M7G', 1, '小航', 3, 6001, 85, NOW()),
(2003, 'student02', '$2a$10$aY6/lgRMKrEkQmALb675yOIN/AWFb/MOsaq1NrsBiAch0GmRV7M7G', 1, '小宇', 3, 6001, 96, NOW()),
(2004, 'student03', '$2a$10$aY6/lgRMKrEkQmALb675yOIN/AWFb/MOsaq1NrsBiAch0GmRV7M7G', 1, '星辰', 5, 6002, 140, NOW()),
(2005, 'student04', '$2a$10$ZpWjl4bBfZz9XA8w1xsU2uVK8rWJ3fZ3J3XIYGrtNtoN./CTBqwTa', 1, '天问', 7, 6003, 180, NOW())
ON DUPLICATE KEY UPDATE
password_hash = VALUES(password_hash),
role_id = VALUES(role_id),
real_name = VALUES(real_name),
grade_id = VALUES(grade_id),
class_id = VALUES(class_id),
points = VALUES(points);

-- 勋章定义（若已存在同名可手动去重）
INSERT INTO badges (id, name, condition_type, condition_value, icon, description) VALUES
(7001, '航天新星', 'first_pass', '1', '/assets/badges/new_star.png', '完成第一关'),
(7002, '火箭先锋', 'streak', '10', '/assets/badges/rocket_pioneer.png', '连续答对10题'),
(7003, '摘星大师', 'perfect_level', '100', '/assets/badges/star_master.png', '满分通关')
ON DUPLICATE KEY UPDATE
condition_type = VALUES(condition_type),
condition_value = VALUES(condition_value),
icon = VALUES(icon),
description = VALUES(description);
