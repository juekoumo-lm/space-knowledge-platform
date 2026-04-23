# 演示数据 SQL 使用说明

这些 SQL 用于快速初始化“航天知识闯关平台”的可演示数据（老师/学生账号、题库、关卡、答题记录等）。

## 文件说明

- `00_reset_demo_data.sql`：清理演示数据（按固定 ID 范围删除）。
- `01_demo_users_and_classes.sql`：插入班级、老师学生账号、勋章定义。
- `02_demo_knowledge_questions_levels.sql`：插入知识点、题目、选项、关卡和关卡题目池。
- `03_demo_attempts_progress.sql`：插入答题记录、关卡进度、掌握度、错题、用户勋章。
- `99_bootstrap_demo.sql`：按顺序串联执行以上脚本（适合 MySQL 客户端一键导入）。

## 推荐执行顺序

1. （可选）执行 `00_reset_demo_data.sql`
2. 执行 `01_demo_users_and_classes.sql`
3. 执行 `02_demo_knowledge_questions_levels.sql`
4. 执行 `03_demo_attempts_progress.sql`

## 演示账号

- 教师：`teacher01 / teacher123`
- 学生：
  - `student01 / student123`
  - `student02 / student123`
  - `student03 / student123`
  - `student04 / 123456`

## 注意事项

- 请先执行项目自带 `schema.sql` 建库建表。
- SQL 默认基于数据库名 `space_knowledge`。
- 题目 ID、用户 ID 使用固定演示范围，方便反复重置。
