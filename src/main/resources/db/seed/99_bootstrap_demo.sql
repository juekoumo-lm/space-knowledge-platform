-- 一键导入演示数据（在 MySQL 客户端中执行）
-- 示例：mysql -u root -p < src/main/resources/db/seed/99_bootstrap_demo.sql

SOURCE src/main/resources/db/seed/00_reset_demo_data.sql;
SOURCE src/main/resources/db/seed/01_demo_users_and_classes.sql;
SOURCE src/main/resources/db/seed/02_demo_knowledge_questions_levels.sql;
SOURCE src/main/resources/db/seed/03_demo_attempts_progress.sql;
SOURCE src/main/resources/db/seed/04_demo_dashboard_heavy_data.sql;
