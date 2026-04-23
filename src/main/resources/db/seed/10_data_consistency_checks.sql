-- 10) 数据一致性自检 SQL（导入后可手工执行）
USE space_knowledge;

-- 1. 检查演示账号是否齐全
SELECT id, username, role_id, grade_id, class_id
FROM users
WHERE id BETWEEN 2001 AND 2008
ORDER BY id;

-- 2. 检查题目与选项是否完整（每题至少2个选项）
SELECT q.id, LEFT(q.content, 50) AS question_brief, COUNT(o.id) AS option_count
FROM questions q
LEFT JOIN question_options o ON o.question_id = q.id
WHERE q.id BETWEEN 4001 AND 4010
GROUP BY q.id, q.content
HAVING option_count < 2;

-- 3. 检查关卡题池是否为空
SELECT l.id AS level_id, l.name, COUNT(lq.question_id) AS question_cnt
FROM levels l
LEFT JOIN level_questions lq ON lq.level_id = l.id
WHERE l.id BETWEEN 3001 AND 3004
GROUP BY l.id, l.name;

-- 4. 检查近30天是否有活跃数据（用于大屏）
SELECT COUNT(*) AS recent_attempts_30d
FROM attempts
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 5. 检查视图可用性
SELECT * FROM v_level_pass_rate LIMIT 5;
SELECT * FROM v_daily_active_30d ORDER BY stat_date DESC LIMIT 7;
SELECT * FROM v_top_wrong_questions_30d LIMIT 10;
