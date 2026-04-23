# 航天知识闯关平台运行手册（Runbook）

> 目标：从“空环境”到“可登录、可闯关、可看大屏”一次跑通。  
> 适用环境：JDK 11+、Maven 3.8+、MySQL 8。

---

## 1. 环境准备

### 1.1 必备软件

- Java：`11+`
- Maven：`3.8+`
- MySQL：`8.x`

### 1.2 快速自检

```bash
java -version
mvn -version
mysql --version
```

---

## 2. 数据库初始化（推荐脚本方式）

### 2.1 一键初始化 schema + 演示数据

```bash
bash scripts/init_demo_db.sh -h 127.0.0.1 -P 3306 -u root -p'你的密码'
```

> 如果你不希望清空演示区间数据，可加 `--no-reset`：

```bash
bash scripts/init_demo_db.sh -u root -p'你的密码' --no-reset
```

### 2.2 手工方式（按需）

```bash
mysql -u root -p < src/main/resources/db/schema.sql
mysql -u root -p < src/main/resources/db/seed/99_bootstrap_demo.sql
```

---

## 3. 修改数据库配置

编辑文件：`src/main/resources/application.properties`

至少确认以下字段：

- `jdbc.url`
- `jdbc.username`
- `jdbc.password`

---

## 4. 启动项目（Jetty）

```bash
mvn clean package
mvn -DskipTests jetty:run
```

默认访问地址：

- 首页：`http://localhost:8081/space-knowledge/index.html`
- 登录：`http://localhost:8081/space-knowledge/login.html`
- API 前缀：`/api`

---

## 5. 启动后自检（强烈推荐）

### 5.1 基础可用性自检（页面 + 匿名 API）

```bash
bash scripts/deploy_smoke_check.sh
```

该脚本会自动检查：

- `index.html` 是否 `200`
- `login.html` 是否 `200`
- 匿名访问 `/api/student/dashboard` 是否返回“用户未登录”业务响应

### 5.2 登录后接口自检（鉴权 + 业务 API）

```bash
# 默认 student01 / student123
bash scripts/api_auth_smoke.sh

# 或指定
bash scripts/api_auth_smoke.sh http://127.0.0.1:8081/space-knowledge/api student01 student123
```

### 5.3 一键全链路预检（推荐）

```bash
bash scripts/full_preflight.sh

# 如果尚未导入演示账号，可先跳过鉴权检查
bash scripts/full_preflight.sh --skip-auth
```

该脚本会执行：

1. `mvn -q -DskipTests compile`
2. `mvn -q test`
3. `bash scripts/deploy_smoke_check.sh`
4. `bash scripts/api_auth_smoke.sh`

---

## 6. 演示账号

- 教师：`teacher01 / teacher123`
- 学生：
  - `student01 / student123`
  - `student02 / student123`
  - `student03 / student123`
  - `student04 / 123456`

---

## 7. 常见问题排查

### 7.1 访问 API 404

- 现象：访问 `/student/...` 404。
- 原因：项目 API 统一挂在 `/api` 下。
- 正确示例：`/api/student/dashboard`

### 7.2 访问受保护接口返回“用户未登录”

- 这是正常行为（未带 JWT）。
- 登录后在请求头携带：
  - `Authorization: Bearer <token>`

### 7.3 端口冲突

- 默认端口 `8081`。
- 修改 `pom.xml` 中 `jetty-maven-plugin` 的端口配置后重启。

### 7.4 启动报数据库连接错误

- 检查 `application.properties` 中 JDBC 配置。
- 确认 MySQL 已启动、账号有权限。

### 7.5 页面能开但数据为空

- 通常是未导入演示 SQL。
- 重新执行：

```bash
bash scripts/init_demo_db.sh -u root -p'你的密码'
```

---

## 8. 部署到外部容器（可选）

```bash
mvn -DskipTests package
```

生成：`target/space-knowledge.war`，可部署到外部 Tomcat。

---

## 9. 发布前检查清单

- [ ] `mvn -q -DskipTests package` 通过
- [ ] `bash scripts/deploy_smoke_check.sh` 通过
- [ ] `bash scripts/api_auth_smoke.sh` 通过
- [ ] 教师端可打开大屏并有图表数据
- [ ] 学生端可登录、闯关、查看推荐题与错题本
