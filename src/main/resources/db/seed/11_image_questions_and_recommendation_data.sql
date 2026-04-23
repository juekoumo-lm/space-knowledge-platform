-- 11) 图片题与推荐增强数据（覆盖图文题、弱项推荐场景）
USE space_knowledge;

-- 清理本脚本使用的题目区间
DELETE FROM question_options WHERE question_id BETWEEN 4011 AND 4015;
DELETE FROM question_kp WHERE question_id BETWEEN 4011 AND 4015;
DELETE FROM level_questions WHERE question_id BETWEEN 4011 AND 4015;
DELETE FROM questions WHERE id BETWEEN 4011 AND 4015;

-- 图文题示例（题干/选项中使用相对图片路径）
INSERT INTO questions (id, content, type, difficulty, grade_id, teacher_id, analysis, status, created_at) VALUES
(4011, '观察图片并判断：图中展示的是哪一部分火箭结构？<br/><img src="/uploads/questions/rocket_stage.png" style="max-width:220px;"/>', 'SINGLE', 2, 5, 2001, '图中为火箭一级推进段结构示意。', 1, NOW()),
(4012, '根据示意图，哪个是中国空间站核心舱“天和”？<br/><img src="/uploads/questions/space_station_parts.png" style="max-width:260px;"/>', 'SINGLE', 2, 7, 2001, '核心舱通常标记为“天和（Core Module）”。', 1, NOW()),
(4013, '以下关于月相变化的描述，哪些是正确的？', 'MULTIPLE', 1, 3, 2001, '月相变化由日地月相对位置变化导致。', 1, NOW()),
(4014, '低轨道卫星的轨道周期通常小于地球同步轨道卫星。', 'JUDGE', 2, 7, 2001, '低轨卫星轨道周期更短。', 1, NOW()),
(4015, '选择与“第一宇宙速度”最接近的数值。', 'SINGLE', 3, 7, 2001, '第一宇宙速度约为 7.9 km/s。', 1, NOW())
ON DUPLICATE KEY UPDATE
content = VALUES(content), type = VALUES(type), difficulty = VALUES(difficulty), grade_id = VALUES(grade_id),
teacher_id = VALUES(teacher_id), analysis = VALUES(analysis), status = VALUES(status);

INSERT INTO question_options (question_id, option_label, option_text, is_correct) VALUES
(4011, 'A', '整流罩', 0),(4011, 'B', '一级推进段', 1),(4011, 'C', '返回舱', 0),(4011, 'D', '逃逸塔', 0),
(4012, 'A', '问天实验舱', 0),(4012, 'B', '梦天实验舱', 0),(4012, 'C', '天和核心舱', 1),(4012, 'D', '货运飞船', 0),
(4013, 'A', '月相变化周期约一个朔望月', 1),(4013, 'B', '月相变化与月球自发光有关', 0),(4013, 'C', '满月时月球大致位于地球与太阳相对方向', 1),(4013, 'D', '上弦月时月面总是全黑', 0),
(4014, 'A', '正确', 1),(4014, 'B', '错误', 0),
(4015, 'A', '3.1 km/s', 0),(4015, 'B', '7.9 km/s', 1),(4015, 'C', '11.2 km/s', 0),(4015, 'D', '16.7 km/s', 0);

INSERT INTO question_kp (question_id, kp_id) VALUES
(4011, 5003),(4012, 5005),(4013, 5002),(4014, 5006),(4015, 5006)
ON DUPLICATE KEY UPDATE kp_id = VALUES(kp_id);

-- 将新题加入对应关卡（含精英挑战关）
INSERT INTO level_questions (level_id, question_id) VALUES
(3002, 4011),
(3003, 4012),
(3001, 4013),
(3003, 4014),
(3004, 4015)
ON DUPLICATE KEY UPDATE question_id = VALUES(question_id);

-- 推荐场景：制造“薄弱点”数据（轨道与速度 5006）
DELETE FROM attempts
WHERE user_id = 2002
  AND question_id IN (4014, 4015)
  AND created_at >= DATE_SUB(NOW(), INTERVAL 15 DAY);

INSERT INTO attempts (user_id, question_id, level_id, answer, is_correct, time_spent, attempt_no, source, created_at) VALUES
(2002, 4014, 3003, 'B', 0, 19, 1, 'practice', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2002, 4015, 3004, 'C', 0, 23, 1, 'practice', DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT INTO wrong_questions (user_id, question_id, note, added_at) VALUES
(2002, 4014, '轨道周期概念易混淆，建议复习低轨道与同步轨道。', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2002, 4015, '第一宇宙速度和第二宇宙速度混淆。', DATE_SUB(NOW(), INTERVAL 2 DAY))
ON DUPLICATE KEY UPDATE note = VALUES(note), added_at = VALUES(added_at);
