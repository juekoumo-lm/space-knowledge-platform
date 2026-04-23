-- 12) 推荐/大屏性能优化索引（可选）
USE space_knowledge;

-- MySQL 8 无 CREATE INDEX IF NOT EXISTS，使用 information_schema + 动态 SQL 方式
SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name='attempts' AND index_name='idx_attempts_user_level_time'),
  'SELECT "idx_attempts_user_level_time exists"',
  'CREATE INDEX idx_attempts_user_level_time ON attempts(user_id, level_id, created_at)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name='attempts' AND index_name='idx_attempts_source_time'),
  'SELECT "idx_attempts_source_time exists"',
  'CREATE INDEX idx_attempts_source_time ON attempts(source, created_at)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name='attempts' AND index_name='idx_attempts_correct_user'),
  'SELECT "idx_attempts_correct_user exists"',
  'CREATE INDEX idx_attempts_correct_user ON attempts(is_correct, user_id, question_id)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name='user_level_progress' AND index_name='idx_ulp_level_passed'),
  'SELECT "idx_ulp_level_passed exists"',
  'CREATE INDEX idx_ulp_level_passed ON user_level_progress(level_id, passed)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name='wrong_questions' AND index_name='idx_wrong_user_added'),
  'SELECT "idx_wrong_user_added exists"',
  'CREATE INDEX idx_wrong_user_added ON wrong_questions(user_id, added_at)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
