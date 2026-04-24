# 开源LIS系统

## 产品概述

这是一套面向医疗实验室的信息管理系统，采用Java技术栈构建，提供样本管理、质量控制、数据查询等核心功能。

## 技术架构

- **后端**: Spring Boot 3.2 + MyBatis-Plus
- **前端**: Vue 3 + Element Plus + Vite
- **数据库**: MySQL 8.0

## 项目结构

```
lis/
├── backend/           # Spring Boot后端服务
│   ├── src/main/java/com/lis/
│   │   ├── controller/   # REST API控制器
│   │   ├── entity/        # 实体类
│   │   ├── mapper/        # 数据访问层
│   │   └── service/       # 业务逻辑
│   ├── src/main/resources/
│   │   └── application.yml  # 配置文件
│   └── pom.xml
├── frontend/         # Vue3前端应用
│   ├── src/
│   │   ├── views/       # 页面组件
│   │   ├── components/   # 公共组件
│   │   ├── api/          # API接口
│   │   └── router/       # 路由配置
│   └── package.json
└── database/        # 数据库脚本
    └── schema.sql
```

## 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0

## 快速部署

### 1. 配置数据库

编辑 `backend/src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lis?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 2. 初始化数据库

执行 `database/schema.sql` 创建数据库表结构。

### 3. 启动后端服务

```bash
cd backend
mvn spring-boot:run
```

后端服务启动后运行在 http://localhost:8080

### 4. 启动前端服务

```bash
cd frontend
npm install
npm run dev
```

前端服务启动后运行在 http://localhost:3000

### 5. 访问系统

打开浏览器访问 http://localhost:3000 ，使用系统管理员账号登录。

## 默认账号

请使用系统初始化时创建的管理员账号登录。

## 文档

- 部署手册：了解详细的部署配置
- 操作手册：了解各功能模块使用方法
