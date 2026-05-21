# 主题频道交流平台

---
## 项目简介
### <div align="center">*在网络世界中随意啦史。*</div>
***<div align="right">—— 《 C--程序设计 》</div>***

--- 
## 核心功能
### 基础模块
- **用户中心**：手机号注册/登录、个人信息修改、密码找回、账号注销。
- **管理后台**：系统管理员拥有审核、管理所有用户和内容的最高权限。

### 业务模块
- **频道管理**
  - **创建/解散**：用户可申请创建主题频道，经管理员审核通过后生效，创建者可随时解散。
  - **加入/退出**：用户可自由加入任何频道，也可随时退出。
  - **信息修改**：频道创建者可修改频道名称、简介等信息。

- **内容**
  - **主题帖**：频道成员可发布、修改、删除自己的帖子。
  - **评论**：频道成员可在帖子下发表评论，支持对评论的删除操作。
  - **收藏**：用户可收藏感兴趣的主题帖，方便日后查阅。

- **审核**：双重审核
  - **系统管理员**负责审核频道的创建与信息修改。
  - **频道管理员**负责审核本频道内的帖子和评论，确保内容合规。

--- 
## 技术架构
## 本地开发环境配置

### 前置条件
- JDK 17+
- MySQL 8.0+
- Maven（项目自带 Maven Wrapper，无需手动安装）

### 数据库配置
1. 复制示例配置：`cp src/main/resources/application.example.yaml src/main/resources/application.yaml`
2. 修改 `application.yaml` 中的 MySQL 用户名和密码，或设置环境变量：
   ```bash
   export DB_USERNAME=your_mysql_user
   export DB_PASSWORD=your_mysql_password
   ```
3. 首次启动时 JPA 会自动建表，无需手动执行 SQL

### 启动项目
```bash
cd backend/topic-channel-platform
./mvnw spring-boot:run
```
访问 `http://localhost:8080`

### 默认账号
| 角色 | 账号 | 密码 |
|---|---|---|
| 管理员 | admin | admin123 |
| 测试用户 | 13800000000 | 123456 |

首次启动时由 `DataInitializer` 自动创建，后续启动不会重复插入。

### Android 开发环境配置

#### 前置条件
- Android Studio (最新稳定版)
- Android SDK (API 34+)
- JDK 17（推荐通过 Android Studio 内置或 Gradle Toolchain 自动管理）

#### JDK 配置

**不要**在 `gradle.properties` 中硬编码本地 JDK 路径。该文件应被提交到 Git，硬编码路径会导致 CI 和其他开发者构建失败。

正确的 `gradle.properties` 配置：

```properties
# JDK toolchain: prefer JAVA_HOME; Gradle will auto-download if missing.
# DO NOT hardcode local paths here (use local.properties for that).
org.gradle.java.installations.auto-download=true
org.gradle.java.installations.fromEnv=JAVA_HOME
```

| 文件 | 用途 | 是否提交 Git |
|------|------|-------------|
| `gradle.properties` | 共享构建配置（JDK toolchain、JVM 参数等） | 是 |
| `local.properties` | 本地 SDK/JDK 路径，Android Studio 自动生成 | **否**（.gitignore） |

#### Android SDK 路径

`local.properties` 中配置 SDK 路径（由 Android Studio 自动生成，无需手动编辑）：

```properties
sdk.dir=/path/to/Android/Sdk
```

#### 模拟器与后端通信

Android 官方模拟器使用 `10.0.2.2` 访问宿主机 localhost。`RetrofitClient` 中已配置：

```java
private static final String BASE_URL = "http://10.0.2.2:8080/";
```

如使用第三方模拟器（如 Mumu、BlueStacks），需改为宿主机局域网 IP。

#### 启动步骤

1. 用 Android Studio 打开 `android/` 目录
2. 等待 Gradle Sync 完成（首次需下载依赖）
3. 先启动后端（`./mvnw spring-boot:run`）
4. 选择模拟器或真机，点击 Run

---
## 技术架构

本项目采用前后端分离的B/S架构和原生移动端开发。

| 端          | 技术栈                            | 开发环境            |
| ----------- | --------------------------------- | ------------------- |
| Web后端     | Java + Spring Boot                | IntelliJ IDEA       |
| Web前端     | HTML + CSS + JavaScript (可选Vue) | -                   |
| 移动端(APP) | Android + Java                    | Android Studio      |
| 数据库      | MySQL                             | Navicat / IDEA 内置 |

---
## 系统设计要点
### 关键流程
- **用户注册/找回密码**：均通过手机短信验证码确保操作安全。
- **内容审核**状态机：频道的创建、帖子和评论的发布都遵循 “待审核 → 审核通过/审核未通过” 的状态流转。
- **权限控制**：
  - **系统管理员**：最高权限，可管理所有用户、审核频道创建。
  - **频道管理员**（创建者）：可管理频道信息、审核内容、删除本频道任意帖子和评论。
  - **普通用户**：可加入频道、发布/管理自己的帖子和评论、收藏帖子。

### 数据库核心实体
- User / OrdinaryUser (用户)
- Administrator (管理员)
- Forum (主题频道)
- ForumMember (频道成员)
- ThemePost (主题帖)
- Comment (评论)
- Collect (收藏)

---
## 项目进度记录

- [TODO 与开发计划](TODO.md)：记录待开发事项、优先级、负责人和验收标准。
- [已开发功能与项目特点](FEATURES.md)：记录已经完成或已经具备代码基础的功能，便于周报、答辩和团队交接。

建议规则：计划做但尚未完成的内容写入 `TODO.md`；已经完成并可说明入口、关键文件或验收状态的内容写入 `FEATURES.md`。

git commit说明
- feat：新增功能
- fix：修复 BUG
- refactor：代码重构 / 优化逻辑
- docs：注释、文档修改
- style：格式、缩进、空格调整
- perf：性能优化
- test：新增 / 修改测试用例
--- 
## 项目成员

|     姓名     |           代号           |   职责   |
| :----------: | :----------------------: | :------: |
| OrionAstesia | 神镐人、项目灭迹人若叶睦 | 结束乐队   |
|     濯缨     |        郑在摸鱼           | 开始乐队 |
|              |                          |          |
|              |                          |          |

<div align="center">© 2024-2026 C--程序设计 版权所有</div>