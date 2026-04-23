-- 9) 班级竞赛扩展数据（排行榜与班级对比）
USE space_knowledge;

-- 追加学生账号（同一老师管理下不同班级）
INSERT INTO users (id, username, password_hash, role_id, real_name, grade_id, class_id, points, create_time)
VALUES
(2006, 'student05', '$2a$10$aY6/lgRMKrEkQmALb675yOIN/AWFb/MOsaq1NrsBiAch0GmRV7M7G', 1, '飞鸿', 5, 6002, 110, NOW()),
(2007, 'student06', '$2a$10$aY6/lgRMKrEkQmALb675yOIN/AWFb/MOsaq1NrsBiAch0GmRV7M7G', 1, '远航', 7, 6003, 160, NOW()),
(2008, 'student07', '$2a$10$aY6/lgRMKrEkQmALb675yOIN/AWFb/MOsaq1NrsBiAch0GmRV7M7G', 1, '凌云', 3, 6001, 75, NOW())
ON DUPLICATE KEY UPDATE
real_name = VALUES(real_name), grade_id = VALUES(grade_id), class_id = VALUES(class_id), points = VALUES(points);

-- 近7天竞赛练习记录
DELETE FROM attempts
WHERE user_id IN (2006,2007,2008)
  AND created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY);

INSERT INTO attempts (user_id, question_id, level_id, answer, is_correct, time_spent, attempt_no, source, created_at) VALUES
(2006, 4003, 3002, 'ABD', 1, 14, 1, 'practice', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(2006, 4004, 3002, 'B', 1, 13, 1, 'practice', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2006, 4009, 3002, 'A', 1, 12, 1, 'practice', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2007, 4005, 3003, 'C', 1, 9, 1, 'practice', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2007, 4006, 3003, 'A', 1, 8, 1, 'practice', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2007, 4007, 3003, 'ABD', 1, 11, 1, 'practice', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2008, 4001, 3001, 'A', 1, 16, 1, 'practice', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(2008, 4002, 3001, 'A', 0, 17, 1, 'practice', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2008, 4008, 3001, 'C', 1, 15, 1, 'practice', DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO user_level_progress (user_id, level_id, score, passed, best_time_spent, completed_at) VALUES
(2006, 3002, 100.00, 1, 39, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2007, 3003, 100.00, 1, 28, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2008, 3001, 66.00, 0, 48, DATE_SUB(NOW(), INTERVAL 2 DAY))
ON DUPLICATE KEY UPDATE
score = VALUES(score), passed = VALUES(passed), best_time_spent = VALUES(best_time_spent), completed_at = VALUES(completed_at);
