
# 1. 项目概述

## 项目名称
主题频道交流平台

## 项目目标
构建一个支持 Web + Android 双端的主题社区平台，实现：

- 用户管理
- 频道管理
- 发帖与评论
- 收藏与关注
- 审核机制
- 社区运营

适用于：

- 兴趣社区
- 技术论坛
- 团队协作频道
- 内容创作者社群

---

# 2. 技术栈

## 后端
- Java
- Spring Boot
- Tomcat
- MySQL

## 前端
- HTML
- CSS
- JavaScript
- （计划支持 Vue）

## 移动端
- Android Studio
- Java

## 开发工具
- IntelliJ IDEA
- Navicat

---

# 3. 核心业务模型

> 说明：
>
> - PK：主码（Primary Key）
> - FK：外码（Foreign Key）
> - 建议所有主键统一使用：
>
> ```text
> BIGINT
> ```
>
> 或：
>
> ```text
> UUID
> ```

## 3.1 User（用户）

### 主码

```text
PK: user_id
```

### 字段
| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
| user_id | bigint | PK | 用户ID，9位随机整数 |
| phone_number | varchar | UNIQUE | 手机号 |
| user_password | varchar | NOT NULL | 密码 |
| user_name | varchar |  | 用户名 |
| real_name | varchar |  | 真实姓名 |
| gender | varchar |  | 性别 |
| birthday | datetime |  | 生日 |
| register_time | datetime |  | 注册时间 |
| phoneNumber | varchar | 手机号 |
| userPassword | varchar | 密码 |
| userName | varchar | 用户名 |
| realName | varchar | 真实姓名 |
| gender | varchar | 性别 |
| birthday | datetime | 生日 |
| registerTime | datetime | 注册时间 |

### 核心能力
- 注册
- 登录
- 修改资料
- 修改密码
- 找回密码
- 注销
- 查询用户

---

## 3.2 Forum（主题频道）

### 主码与外码

```text
PK: forum_id
FK: user_id -> user.user_id
```

### 字段
| 字段 | 类型 | 约束 | 说明 |
|---|---|---|---|
|---|---|---|
| forum_id | bigint | PK | 频道ID，从1开始的整数 |
| user_id | bigint | FK | 创建者ID |
| forum_name | varchar | NOT NULL | 频道名称 |
| content | text |  | 频道简介 |
| audit_state | varchar |  | 审核状态 |
| create_time | datetime |  | 创建时间 |

### 核心能力
- 创建频道
- 修改频道
- 审核频道
- 解散频道
- 搜索频道
- 查看频道详情

---

## 3.3 ForumMember（频道成员）

### 主码与外码

```text
PK: (user_id, forum_id)
FK: user_id -> user.user_id
FK: forum_id -> forum.forum_id
```

### 字段
| 字段 | 类型 | 说明 |
|---|---|---|
| user_id | bigint | PK, FK | 用户ID |
| forum_id | bigint | PK, FK | 频道ID |
| join_time | datetime |  | 加入时间 |

### 核心能力
- 加入频道
- 退出频道
- 查看已加入频道

---

## 3.4 ThemePost（主题帖）

### 主码与外码

```text
PK: theme_post_id
FK: forum_id -> forum.forum_id
FK: user_id -> user.user_id
```

### 字段
| 字段 | 类型 | 说明 |
|---|---|---|
| theme_post_id | bigint | PK | 帖子ID，从1开始的整数 |
| forum_id | bigint | FK | 所属频道 |
| user_id | bigint | FK | 作者ID |
| title | varchar | NOT NULL | 标题 |
| content | text | NOT NULL | 内容 |
| publish_time | datetime |  | 发布时间 |
| audit_state | varchar |  | 审核状态 |

### 核心能力
- 发布帖子
- 删除帖子
- 修改帖子
- 审核帖子
- 搜索帖子
- 查看帖子

---

## 3.5 Comment（评论）

### 主码与外码

```text
PK: comment_id
FK: theme_post_id -> theme_post.theme_post_id
FK: user_id -> user.user_id
```

### 字段
| 字段 | 类型 | 说明 |
|---|---|---|
| comment_id | bigint | PK | 评论ID，从1开始的整数 |
| theme_post_id | bigint | FK | 帖子ID |
| user_id | bigint | FK | 评论用户ID |
| content | text | NOT NULL | 评论内容 |
| publish_time | datetime |  | 发布时间 |
| audit_state | varchar |  | 审核状态 |

### 核心能力
- 发布评论
- 删除评论
- 审核评论
- 查看评论

---

## 3.6 Collect（收藏）

### 主码与外码

```text
PK: (user_id, theme_post_id)
FK: user_id -> user.user_id
FK: theme_post_id -> theme_post.theme_post_id
```

### 字段
| 字段 | 类型 | 说明 |
|---|---|---|
| user_id | bigint | PK, FK | 用户ID |
| theme_post_id | bigint | PK, FK | 帖子ID |
| collect_time | datetime |  | 收藏时间 |

### 核心能力
- 收藏帖子
- 取消收藏
- 查看收藏列表

---

# 4. 权限模型

## 普通用户
允许：

- 注册登录
- 修改个人信息
- 创建频道
- 加入频道
- 发帖
- 评论
- 收藏帖子

## 频道管理员
额外允许：

- 管理频道
- 删除频道内帖子
- 删除频道评论

## 系统管理员
允许：

- 审核频道
- 审核帖子
- 审核评论
- 管理用户

---

# 5. 功能模块划分

| 模块 | 功能 |
|---|---|
| 用户模块 | 注册、登录、密码管理、用户信息 |
| 频道模块 | 创建频道、搜索频道、审核频道 |
| 成员模块 | 加入/退出频道 |
| 帖子模块 | 发帖、删帖、审核 |
| 评论模块 | 评论、删除、审核 |
| 收藏模块 | 收藏帖子 |

---

# 6. 推荐后端结构（适合 Agent 生成代码）

## 推荐目录结构

```text
src/main/java
├── controller
├── service
├── service/impl
├── repository
├── entity
├── dto
├── vo
├── config
├── security
├── common
└── exception
```

---

# 7. 推荐数据库关系

## 关系说明

```text
User
 ├── Forum
 ├── ThemePost
 ├── Comment
 └── Collect

Forum
 ├── ForumMember
 └── ThemePost

ThemePost
 ├── Comment
 └── Collect
```