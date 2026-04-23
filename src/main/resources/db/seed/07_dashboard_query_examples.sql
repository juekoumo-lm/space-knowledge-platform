-- 7) 教师后台统计查询样例（可直接用于接口 SQL 原型）
USE space_knowledge;

-- A. 班级排行榜（按积分 Top10）
SELECT u.id, u.username, u.real_name, u.points, c.name AS class_name
FROM users u
LEFT JOIN classes c ON c.id = u.class_id
WHERE u.role_id = 1
ORDER BY u.points DESC, u.id ASC
LIMIT 10;

-- B. 知识点掌握度雷达图数据（单个学生）
SELECT kp.id AS kp_id, kp.name AS kp_name, ROUND(ukm.mastery_score * 100, 2) AS mastery_percent
FROM user_kp_mastery ukm
JOIN knowledge_points kp ON kp.id = ukm.kp_id
WHERE ukm.user_id = 2005
ORDER BY kp.sort_order, kp.id;

-- C. 近7天每日活跃人数
SELECT DATE(created_at) AS stat_date, COUNT(DISTINCT user_id) AS active_users
FROM attempts
WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY DATE(created_at)
ORDER BY stat_date;

-- D. 各关卡通过人数与通过率
SELECT level_id, level_name, participants, passed_count, pass_rate
FROM v_level_pass_rate
ORDER BY grade_id, level_id;
