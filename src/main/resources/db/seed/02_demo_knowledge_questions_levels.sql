-- 2) 航天知识点、题库、关卡数据
USE space_knowledge;

-- 为保证可重复执行，先清理演示题目的子表数据
DELETE FROM question_options WHERE question_id BETWEEN 4001 AND 4010;
DELETE FROM question_kp WHERE question_id BETWEEN 4001 AND 4010;
DELETE FROM level_questions WHERE level_id BETWEEN 3001 AND 3004;

-- 知识点（示例）
INSERT INTO knowledge_points (id, name, parent_id, grade_level, description, sort_order) VALUES
(5001, '太阳系基础', 0, 3, '认识行星与恒星', 1),
(5002, '地球与月球', 0, 3, '月相、潮汐、绕转关系', 2),
(5003, '火箭结构', 0, 5, '推进系统、分级结构', 3),
(5004, '中国航天史', 0, 5, '东方红一号、神舟等', 4),
(5005, '空间站与载人航天', 0, 7, '空间站构造与任务', 5),
(5006, '轨道与速度', 0, 7, '第一宇宙速度、近地轨道', 6)
ON DUPLICATE KEY UPDATE
name = VALUES(name),
parent_id = VALUES(parent_id),
grade_level = VALUES(grade_level),
description = VALUES(description),
sort_order = VALUES(sort_order);

-- 题目
INSERT INTO questions (id, content, type, difficulty, grade_id, teacher_id, analysis, status, created_at)
VALUES
(4001, '太阳系中离太阳最近的行星是？', 'SINGLE', 1, 3, 2001, '离太阳最近的是水星。', 1, NOW()),
(4002, '月球本身会发光。', 'JUDGE', 1, 3, 2001, '月球反射太阳光，不会自行发光。', 1, NOW()),
(4003, '下列哪些属于火箭的主要组成部分？', 'MULTIPLE', 2, 5, 2001, '一般包括箭体结构、动力系统、控制系统等。', 1, NOW()),
(4004, '中国第一颗人造地球卫星是？', 'SINGLE', 2, 5, 2001, '1970年发射“东方红一号”。', 1, NOW()),
(4005, '中国空间站核心舱名称是？', 'SINGLE', 2, 7, 2001, '核心舱是“天和”。', 1, NOW()),
(4006, '第一宇宙速度约为 7.9 km/s。', 'JUDGE', 3, 7, 2001, '第一宇宙速度约 7.9 km/s。', 1, NOW()),
(4007, '以下哪些任务属于神舟载人航天任务？', 'MULTIPLE', 2, 7, 2001, '神舟系列属于中国载人航天。', 1, NOW()),
(4008, '地球绕太阳公转一周大约需要多长时间？', 'SINGLE', 1, 3, 2001, '约365天（1年）。', 1, NOW()),
(4009, '火箭采用多级结构的主要目的之一是减轻后续飞行负担。', 'JUDGE', 2, 5, 2001, '抛弃空箭体可显著提升效率。', 1, NOW()),
(4010, '“天宫课堂”主要用于什么活动？', 'SINGLE', 1, 7, 2001, '用于太空科普授课与实验演示。', 1, NOW())
ON DUPLICATE KEY UPDATE
content = VALUES(content), type = VALUES(type), difficulty = VALUES(difficulty), grade_id = VALUES(grade_id),
teacher_id = VALUES(teacher_id), analysis = VALUES(analysis), status = VALUES(status);

-- 选项（判断题也采用 A/B 作为“正确/错误”）
INSERT INTO question_options (question_id, option_label, option_text, is_correct) VALUES
(4001, 'A', '水星', 1),(4001, 'B', '金星', 0),(4001, 'C', '地球', 0),(4001, 'D', '火星', 0),
(4002, 'A', '正确', 0),(4002, 'B', '错误', 1),
(4003, 'A', '动力系统', 1),(4003, 'B', '控制系统', 1),(4003, 'C', '乘客舱座椅套装', 0),(4003, 'D', '箭体结构', 1),
(4004, 'A', '神舟一号', 0),(4004, 'B', '东方红一号', 1),(4004, 'C', '风云一号', 0),(4004, 'D', '嫦娥一号', 0),
(4005, 'A', '问天', 0),(4005, 'B', '梦天', 0),(4005, 'C', '天和', 1),(4005, 'D', '神舟', 0),
(4006, 'A', '正确', 1),(4006, 'B', '错误', 0),
(4007, 'A', '神舟五号', 1),(4007, 'B', '神舟十三号', 1),(4007, 'C', '悟空号', 0),(4007, 'D', '神舟十六号', 1),
(4008, 'A', '约30天', 0),(4008, 'B', '约180天', 0),(4008, 'C', '约365天', 1),(4008, 'D', '约730天', 0),
(4009, 'A', '正确', 1),(4009, 'B', '错误', 0),
(4010, 'A', '太空科普教学', 1),(4010, 'B', '商业直播带货', 0),(4010, 'C', '卫星回收', 0),(4010, 'D', '火箭制造', 0);

-- 题目-知识点
INSERT INTO question_kp (question_id, kp_id) VALUES
(4001, 5001),(4002, 5002),(4003, 5003),(4004, 5004),(4005, 5005),
(4006, 5006),(4007, 5005),(4008, 5001),(4009, 5003),(4010, 5005)
ON DUPLICATE KEY UPDATE kp_id = VALUES(kp_id);

-- 关卡（每个年级示例关）
INSERT INTO levels (id, name, grade_id, theme, difficulty, required_kps, question_count, pass_score, sort_order) VALUES
(3001, '三年级-太阳系启航', 3, '太阳系与地月', 1, '5001,5002', 5, 70.00, 1),
(3002, '五年级-火箭与航天史', 5, '火箭结构与中国航天史', 2, '5003,5004', 5, 70.00, 1),
(3003, '初一-空间站挑战', 7, '空间站与轨道速度', 2, '5005,5006', 5, 70.00, 1),
(3004, '初一-精英挑战关', 7, '综合拔高', 3, '5003,5005,5006', 5, 85.00, 2)
ON DUPLICATE KEY UPDATE
name = VALUES(name), theme = VALUES(theme), difficulty = VALUES(difficulty), required_kps = VALUES(required_kps),
question_count = VALUES(question_count), pass_score = VALUES(pass_score), sort_order = VALUES(sort_order);

-- 关卡题目池
INSERT INTO level_questions (level_id, question_id) VALUES
(3001, 4001),(3001, 4002),(3001, 4008),
(3002, 4003),(3002, 4004),(3002, 4009),
(3003, 4005),(3003, 4006),(3003, 4007),(3003, 4010),
(3004, 4003),(3004, 4005),(3004, 4006),(3004, 4007),(3004, 4009)
ON DUPLICATE KEY UPDATE question_id = VALUES(question_id);
