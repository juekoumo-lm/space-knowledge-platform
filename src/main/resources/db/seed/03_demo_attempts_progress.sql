-- 3) 演示答题记录、关卡进度、知识点掌握度、错题本与勋章
USE space_knowledge;

-- 为保证可重复执行，先清理演示用户历史数据
DELETE FROM attempts WHERE user_id IN (2002,2003,2004,2005);
DELETE FROM user_level_progress WHERE user_id IN (2002,2003,2004,2005);
DELETE FROM user_kp_mastery WHERE user_id IN (2002,2003,2004,2005);
DELETE FROM wrong_questions WHERE user_id IN (2002,2003,2004,2005);
DELETE FROM user_badges WHERE user_id IN (2002,2003,2004,2005);

INSERT INTO attempts (user_id, question_id, level_id, answer, is_correct, time_spent, attempt_no, source, created_at) VALUES
(2002, 4001, 3001, 'A', 1, 12, 1, 'level', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2002, 4002, 3001, 'A', 0, 18, 1, 'level', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2002, 4008, 3001, 'C', 1, 16, 1, 'level', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2003, 4001, 3001, 'A', 1, 9, 1, 'level', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2003, 4002, 3001, 'B', 1, 10, 1, 'level', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2003, 4008, 3001, 'C', 1, 8, 1, 'level', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2004, 4003, 3002, 'ABD', 1, 20, 1, 'level', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2004, 4004, 3002, 'B', 1, 14, 1, 'level', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2004, 4009, 3002, 'A', 1, 12, 1, 'level', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2005, 4005, 3003, 'C', 1, 9, 1, 'level', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2005, 4006, 3003, 'A', 1, 7, 1, 'level', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2005, 4007, 3003, 'ABD', 1, 11, 1, 'level', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2005, 4010, 3003, 'A', 1, 8, 1, 'level', DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO user_level_progress (user_id, level_id, score, passed, best_time_spent, completed_at) VALUES
(2002, 3001, 66.00, 0, 46, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2003, 3001, 100.00, 1, 27, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2004, 3002, 100.00, 1, 46, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2005, 3003, 100.00, 1, 35, DATE_SUB(NOW(), INTERVAL 2 DAY))
ON DUPLICATE KEY UPDATE
score = VALUES(score),
passed = VALUES(passed),
best_time_spent = VALUES(best_time_spent),
completed_at = VALUES(completed_at);

INSERT INTO user_kp_mastery (user_id, kp_id, mastery_score, last_update) VALUES
(2002, 5001, 0.72, NOW()), (2002, 5002, 0.35, NOW()),
(2003, 5001, 0.95, NOW()), (2003, 5002, 0.92, NOW()),
(2004, 5003, 0.90, NOW()), (2004, 5004, 0.88, NOW()),
(2005, 5005, 0.96, NOW()), (2005, 5006, 0.91, NOW())
ON DUPLICATE KEY UPDATE mastery_score = VALUES(mastery_score), last_update = VALUES(last_update);

INSERT INTO wrong_questions (user_id, question_id, note, added_at) VALUES
(2002, 4002, '月球发光概念混淆，需要复习反射光。', DATE_SUB(NOW(), INTERVAL 5 DAY));

INSERT INTO user_badges (user_id, badge_id, obtained_at) VALUES
(2003, 7001, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2004, 7001, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2005, 7001, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2005, 7003, DATE_SUB(NOW(), INTERVAL 2 DAY))
ON DUPLICATE KEY UPDATE obtained_at = VALUES(obtained_at);
