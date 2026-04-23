-- 5) 任务/考试场景演示数据（用于教师布置任务、学生作业来源统计）
USE space_knowledge;

-- 清理旧的演示任务
DELETE FROM assignments WHERE id BETWEEN 8001 AND 8010;

-- 教师布置任务（闯关 + 指定题练习）
INSERT INTO assignments (id, teacher_id, class_id, level_id, question_ids, title, due_at, created_at) VALUES
(8001, 2001, 6001, 3001, NULL, '三年级太阳系闯关作业', DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(8002, 2001, 6002, 3002, NULL, '五年级火箭结构闯关', DATE_ADD(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(8003, 2001, 6003, NULL, '4005,4006,4007,4010', '初一空间站专项练习', DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY))
ON DUPLICATE KEY UPDATE
teacher_id = VALUES(teacher_id),
class_id = VALUES(class_id),
level_id = VALUES(level_id),
question_ids = VALUES(question_ids),
title = VALUES(title),
due_at = VALUES(due_at),
created_at = VALUES(created_at);

-- 插入 assigned 来源答题，支持“作业完成情况”演示
DELETE FROM attempts
WHERE user_id IN (2002,2003,2004,2005)
  AND source = 'assigned'
  AND created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY);

INSERT INTO attempts (user_id, question_id, level_id, answer, is_correct, time_spent, attempt_no, source, created_at) VALUES
(2002, 4001, 3001, 'A', 1, 11, 1, 'assigned', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2002, 4002, 3001, 'B', 1, 15, 1, 'assigned', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2003, 4001, 3001, 'A', 1, 9, 1, 'assigned', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2003, 4008, 3001, 'C', 1, 10, 1, 'assigned', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2004, 4003, 3002, 'ABD', 1, 18, 1, 'assigned', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2004, 4004, 3002, 'B', 1, 12, 1, 'assigned', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2005, 4005, NULL, 'C', 1, 10, 1, 'assigned', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2005, 4006, NULL, 'A', 1, 9, 1, 'assigned', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 教师导入日志样例（用于后台“导入记录”页面演示）
INSERT INTO teacher_import_logs (teacher_id, file_name, total_count, success_count, fail_count, status, error_detail, created_at) VALUES
(2001, '航天题库_三年级.xlsx', 120, 118, 2, 'finished', '第17行图片路径缺失；第96行题型字段为空', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(2001, '航天题库_初一.xlsx', 150, 150, 0, 'finished', NULL, DATE_SUB(NOW(), INTERVAL 3 DAY));
