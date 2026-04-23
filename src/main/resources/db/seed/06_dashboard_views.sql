-- 6) 教师大屏通用视图（可选）
USE space_knowledge;

-- 各关卡通过率视图
CREATE OR REPLACE VIEW v_level_pass_rate AS
SELECT l.id AS level_id,
       l.name AS level_name,
       l.grade_id,
       COUNT(DISTINCT ulp.user_id) AS participants,
       SUM(CASE WHEN ulp.passed = 1 THEN 1 ELSE 0 END) AS passed_count,
       ROUND(100 * SUM(CASE WHEN ulp.passed = 1 THEN 1 ELSE 0 END) / NULLIF(COUNT(DISTINCT ulp.user_id), 0), 2) AS pass_rate
FROM levels l
LEFT JOIN user_level_progress ulp ON ulp.level_id = l.id
GROUP BY l.id, l.name, l.grade_id;

-- 近30天活跃度（日活答题人数）
CREATE OR REPLACE VIEW v_daily_active_30d AS
SELECT DATE(a.created_at) AS stat_date,
       COUNT(DISTINCT a.user_id) AS active_users,
       COUNT(*) AS attempt_count
FROM attempts a
WHERE a.created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY DATE(a.created_at)
ORDER BY stat_date;

-- 高频错题（近30天）
CREATE OR REPLACE VIEW v_top_wrong_questions_30d AS
SELECT q.id AS question_id,
       LEFT(q.content, 120) AS question_brief,
       COUNT(*) AS wrong_count,
       ROUND(100 * SUM(CASE WHEN a.is_correct = 0 THEN 1 ELSE 0 END) / COUNT(*), 2) AS wrong_rate
FROM attempts a
JOIN questions q ON q.id = a.question_id
WHERE a.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY q.id, q.content
HAVING wrong_count >= 1
ORDER BY wrong_count DESC, wrong_rate DESC;
