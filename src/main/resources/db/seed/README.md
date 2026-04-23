# 演示数据 SQL 使用说明

这些 SQL 用于快速初始化“航天知识闯关平台”的可演示数据（老师/学生账号、题库、关卡、答题记录等）。

## 文件说明

- `00_reset_demo_data.sql`：清理演示数据（按固定 ID 范围删除）。
- `01_demo_users_and_classes.sql`：插入班级、老师学生账号、勋章定义。
- `02_demo_knowledge_questions_levels.sql`：插入知识点、题目、选项、关卡和关卡题目池。
- `03_demo_attempts_progress.sql`：插入答题记录、关卡进度、掌握度、错题、用户勋章。
- `04_demo_dashboard_heavy_data.sql`：补充近30天练习数据，用于教师大屏活跃度、错题热力、排行榜等图表。
- `05_demo_exam_and_assignment_data.sql`：补充“教师布置任务/学生作业答题/导入日志”场景数据。
- `06_dashboard_views.sql`：创建教师大屏常用统计视图（通过率、日活、高频错题）。
- `07_dashboard_query_examples.sql`：常用统计查询样例（排行榜、雷达图、活跃度、关卡通过率）。
- `08_badge_and_growth_data.sql`：勋章成长数据补充（新增勋章、用户勋章、掌握度增强）。
- `09_multi_class_competition_data.sql`：多班级竞赛数据（额外学生、近7天对比、关卡进度）。
- `10_data_consistency_checks.sql`：导入后的快速数据自检脚本（核验账号/题库/视图等）。
- `11_image_questions_and_recommendation_data.sql`：补充图文题与推荐弱项场景数据（错题本/推荐链路演示）。
- `12_performance_indexes.sql`：为推荐/统计高频查询补充可选索引。
- `99_bootstrap_demo.sql`：按顺序串联执行以上脚本（适合 MySQL 客户端一键导入）。

## 推荐执行顺序

1. （可选）执行 `00_reset_demo_data.sql`
2. 执行 `01_demo_users_and_classes.sql`
3. 执行 `02_demo_knowledge_questions_levels.sql`
4. 执行 `03_demo_attempts_progress.sql`
5. （推荐）执行 `04_demo_dashboard_heavy_data.sql`
6. （推荐）执行 `05_demo_exam_and_assignment_data.sql`
7. （可选）执行 `06_dashboard_views.sql`
8. （可选）参考 `07_dashboard_query_examples.sql` 进行接口联调
9. （推荐）执行 `08_badge_and_growth_data.sql`
10. （推荐）执行 `09_multi_class_competition_data.sql`
11. （可选）执行 `10_data_consistency_checks.sql` 进行导入结果核验
12. （推荐）执行 `11_image_questions_and_recommendation_data.sql` 强化图文题与推荐演示
13. （可选）执行 `12_performance_indexes.sql` 优化查询性能

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
