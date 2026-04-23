-- 4) 教师大屏增强数据（近30天活跃度/错题热力/排行榜）
-- 建议在执行 01~03 后再执行本脚本
USE space_knowledge;

-- 为避免重复堆积，先删除演示学生近30天的练习来源数据
DELETE FROM attempts
WHERE user_id IN (2002,2003,2004,2005)
  AND source = 'practice'
  AND created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 生成近30天练习数据（每个自然日 4 条）
WITH RECURSIVE day_seq AS (
    SELECT 0 AS n
    UNION ALL
    SELECT n + 1 FROM day_seq WHERE n < 29
),
user_seq AS (
    SELECT 0 AS u UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3
)
INSERT INTO attempts (user_id, question_id, level_id, answer, is_correct, time_spent, attempt_no, source, created_at)
SELECT
    CASE u
        WHEN 0 THEN 2002
        WHEN 1 THEN 2003
        WHEN 2 THEN 2004
        ELSE 2005
    END AS user_id,
    4001 + ((n + u) % 10) AS question_id,
    CASE
        WHEN (4001 + ((n + u) % 10)) IN (4001,4002,4008) THEN 3001
        WHEN (4001 + ((n + u) % 10)) IN (4003,4004,4009) THEN 3002
        ELSE 3003
    END AS level_id,
    CASE ((4001 + ((n + u) % 10)) % 4)
        WHEN 0 THEN 'A'
        WHEN 1 THEN 'B'
        WHEN 2 THEN 'C'
        ELSE 'ABD'
    END AS answer,
    CASE
        WHEN ((n + u) % 7) IN (0,1,2,3,4) THEN 1
        ELSE 0
    END AS is_correct,
    8 + ((n * 3 + u) % 20) AS time_spent,
    1 AS attempt_no,
    'practice' AS source,
    DATE_ADD(DATE_SUB(CURDATE(), INTERVAL n DAY), INTERVAL (8 + u * 3) HOUR) AS created_at
FROM day_seq
CROSS JOIN user_seq;

-- 将最近30天的错误练习题写入错题本（去重）
INSERT IGNORE INTO wrong_questions (user_id, question_id, note, added_at)
SELECT a.user_id,
       a.question_id,
       CONCAT('来自大屏演示数据，日期：', DATE_FORMAT(a.created_at, '%Y-%m-%d')),
       a.created_at
FROM attempts a
WHERE a.user_id IN (2002,2003,2004,2005)
  AND a.source='practice'
  AND a.is_correct=0
  AND a.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 根据总通过关卡数和练习量微调积分（用于排行榜）
UPDATE users u
LEFT JOIN (
    SELECT user_id,
           SUM(CASE WHEN passed=1 THEN 20 ELSE 0 END) AS level_bonus
    FROM user_level_progress
    WHERE user_id IN (2002,2003,2004,2005)
    GROUP BY user_id
) p ON p.user_id = u.id
LEFT JOIN (
    SELECT user_id,
           COUNT(*) AS practice_cnt,
           SUM(is_correct) AS correct_cnt
    FROM attempts
    WHERE user_id IN (2002,2003,2004,2005)
      AND created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
    GROUP BY user_id
) a ON a.user_id = u.id
SET u.points = 50
             + COALESCE(p.level_bonus, 0)
             + COALESCE(a.correct_cnt, 0)
             + FLOOR(COALESCE(a.practice_cnt, 0) / 5)
WHERE u.id IN (2002,2003,2004,2005);
