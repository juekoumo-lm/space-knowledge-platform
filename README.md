# 航天知识闯关学习平台

中小学按年级分层的航天知识点闯关式学习平台，支持智能题目推荐、自适应闯关路径、教师端大屏可视化、题库管理与错题本等。

## 技术栈

- **后端**: Spring + Spring MVC + MyBatis (SSM)，JDK 11
- **数据库**: MySQL 8
- **缓存**: Redis（推荐结果缓存，可选）
- **认证**: BCrypt 密码加密 + JWT
- **Excel**: Apache POI 批量导入
- **前端**: Bootstrap 5 + 原生 JS，教师大屏 ECharts 5

## 快速开始

> 需要更详细的部署/排障文档请看：`docs/RUNBOOK.md`

### 1. 数据库

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

修改 `src/main/resources/application.properties` 中的 `jdbc.url`、`jdbc.username`、`jdbc.password`。

### 2. 构建与运行

```bash
mvn clean package
mvn -DskipTests jetty:run
```

（默认使用 Jetty 插件启动，端口 **8081**。若 8081 被占用，可在 `pom.xml` 中修改 `jetty-maven-plugin` 的端口配置。）

或将 `target/space-knowledge.war` 部署到独立 Tomcat，或使用 IDE 以 War 形式部署，端口通常为 8080。

### 2.1 启动后快速自检（防止 403 / 路由错误）

```bash
bash scripts/deploy_smoke_check.sh
```

该脚本会自动：
- 打包项目；
- 启动 Jetty；
- 检查首页与登录页是否 200；
- 检查匿名访问受保护接口时是否返回“用户未登录”（而不是 403/404 异常）。

### 2.2 数据初始化与登录接口自检

```bash
# 初始化 schema + 演示数据
bash scripts/init_demo_db.sh -u root -p'你的密码'

# 启动项目后检查登录与学生接口
bash scripts/api_auth_smoke.sh
```

### 2.3 一键预检（升级版）

```bash
bash scripts/full_preflight.sh
# 无数据库或未导入演示账号时可先跳过鉴权检查
bash scripts/full_preflight.sh --skip-auth
```

会按顺序执行：`compile -> test -> deploy_smoke -> api_auth_smoke`。

### 3. 访问

- 首页: `http://localhost:8081/space-knowledge/index.html`
- 登录: `http://localhost:8081/space-knowledge/login.html`
- 注册后学生进入学生端首页，教师进入教师端大屏/题库管理。

### 4. API 基础路径

所有接口前缀: `/api`（需登录的接口在 Header 中携带 `Authorization: Bearer <token>`）。

- 认证: `POST /api/auth/login`, `POST /api/auth/register`
- 学生: `/api/student/levels`, `/api/student/recommend`, `/api/student/wrong`, `/api/student/answer`, `/api/student/dashboard` 等
- 教师: `/api/teacher/questions`（CRUD、Excel 导入）
- 可视化: `/api/dashboard/level-pass-rate`, `/api/dashboard/student-rank`, `/api/dashboard/wrong-hot`, `/api/dashboard/kp-mastery`, `/api/dashboard/overview`
- 公共: `/api/grades`, `/api/knowledge-points`

## Excel 批量导入题目

模板列顺序（第一行为表头）：

| 题干 | 题型 | 难度 | 选项A | 选项B | 选项C | 选项D | 正确答案 | 知识点ID |
|------|------|------|-------|-------|-------|-------|----------|----------|
| 题目内容... | SINGLE/MULTIPLE/JUDGE/FILL/SUBJECTIVE | 1-5 | 文本 | 文本 | 文本 | 文本 | A 或 A,B | 1,2,3 |

- 题型: SINGLE(单选), MULTIPLE(多选), JUDGE(判断), FILL(填空), SUBJECTIVE(主观)
- 难度: 1-5
- 正确答案: 单选填 A/B/C/D，多选填 A,B 或 A,B,C
- 知识点ID: 与 `knowledge_points` 表 id 对应，多个用逗号分隔

导入接口: `POST /api/teacher/questions/import`，表单字段 `file`。

## 功能模块概览

- **用户与权限**: 注册/登录（学生/教师）、BCrypt、JWT、年级/班级
- **学生端**: 分年级关卡、闯关答题、实时答题记录、错题本、勋章、推荐题目、掌握度
- **教师端**: 题目 CRUD、按年级/知识点/难度/关键词筛选、Excel 导入、可视化大屏（关卡通过率、活跃度排行、高频错题、知识点掌握热力图）
- **智能推荐**: 规则 + 内容混合（错题优先、未做、难度匹配、薄弱知识点）
- **自适应**: 根据最近答题正确率建议难度档位（升/降/保持）

## 项目结构

```
src/main/java/com/space/knowledge/
├── common/       # Result 等通用类
├── config/       # WebMvc 等配置
├── controller/   # 认证、学生、教师、仪表盘、年级、知识点
├── entity/       # 实体
├── interceptor/  # JWT 认证拦截
├── mapper/       # MyBatis 接口
├── service/      # 业务（认证、题目、关卡、答题、推荐、Excel 导入）
└── util/         # JWT 等工具
src/main/resources/
├── spring/       # Spring 与 Spring MVC 配置
├── mybatis/      # MyBatis 配置与 mapper XML
├── application.properties
└── db/schema.sql
src/main/webapp/
├── index.html, login.html, register.html
├── student/      # 学生首页、关卡、错题等
└── teacher/      # 题库管理、可视化大屏
```

## 后续扩展建议

- 协同过滤/矩阵分解推荐（独立 Python/Java 推荐服务 + Redis 缓存）
- RabbitMQ/Redis 队列处理大批量导入与统计
- 班级管理、布置任务与完成度
- 勋章自动发放规则与用户勋章表写入
- 题目导出 Excel、日志与操作审计
