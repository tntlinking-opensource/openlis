# 服务启动指南

## 前提条件

确保已安装以下软件：
- JDK 17+
- Node.js 18+
- MySQL 8.0

## 配置说明

### 数据库配置

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://数据库地址:3306/lis?useSSL=false&serverTimezone=Asia/Shanghai
    username: 数据库用户名
    password: 数据库密码
```

### 前端配置

如有需要，修改 `frontend/vite.config.js` 中的服务端口。

## 启动服务

### 启动后端

```bash
cd backend
mvn spring-boot:run
```

### 启动前端

```bash
cd frontend
npm install
npm run dev
```

## 访问系统

启动完成后，打开浏览器访问前端地址（默认：http://localhost:3000）

## 常见问题

### 端口被占用

- 后端端口：修改 `application.yml` 中的 `server.port`
- 前端端口：修改 `vite.config.js` 中的 `server.port`

### 数据库连接失败

- 确认MySQL服务已启动
- 检查 `application.yml` 中的数据库连接配置
- 确认数据库用户有访问权限
