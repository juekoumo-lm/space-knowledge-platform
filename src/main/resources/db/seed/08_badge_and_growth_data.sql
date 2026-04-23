-- 8) 勋章与成长轨迹补充数据
USE space_knowledge;

-- 补充勋章定义（若主键已存在则更新）
INSERT INTO badges (id, name, condition_type, condition_value, icon, description) VALUES
(7004, '勤学不辍', 'attempt_days', '7', '/assets/badges/diligent.png', '连续7天有答题记录'),
(7005, '轨道专家', 'kp_mastery', '0.9:5006', '/assets/badges/orbit_expert.png', '轨道与速度知识点掌握度达到90%')
ON DUPLICATE KEY UPDATE
name = VALUES(name),
condition_type = VALUES(condition_type),
condition_value = VALUES(condition_value),
icon = VALUES(icon),
description = VALUES(description);

-- 给演示用户补充“成长型”勋章
INSERT INTO user_badges (user_id, badge_id, obtained_at) VALUES
(2003, 7004, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2005, 7004, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2005, 7005, NOW())
ON DUPLICATE KEY UPDATE obtained_at = VALUES(obtained_at);

-- 强化 user_kp_mastery 展示（用于个人主页雷达图）
INSERT INTO user_kp_mastery (user_id, kp_id, mastery_score, last_update) VALUES
(2005, 5003, 0.83, NOW()),
(2005, 5004, 0.79, NOW()),
(2005, 5005, 0.96, NOW()),
(2005, 5006, 0.93, NOW())
ON DUPLICATE KEY UPDATE mastery_score = VALUES(mastery_score), last_update = VALUES(last_update);
