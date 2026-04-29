# 平台介绍

## 🏠【关于我们】

![天天开源](https://open.tntlinking.com/assets/logo-b-BzFUYaRU.png) 

天天开源致力于构建开放共赢的平台，推动安全高效的开源应用普及。我们始终秉持“开源、众包、共享”的理念，致力于为医疗、教育、中小企业等行业提供优质的开源解决方案。

天天开源聚焦医疗、企业、教育三大行业信息化市场，现已发布OpenHIS、OpenCOM、OpenEDU系列开源软件产品。我们通过建立开源生态，联合生态伙伴共同打造创新行业协作模式，让数字化普惠、可信、安全。

天天开源的前身是新致开源，最早于2022年6月发布OpenHIS开源医疗，于2023年6月发布OpenCOM开源企业。2025年7月，新致开源品牌升级为天天开源。我们将持续践行开源精神，期待成为全球开源生态的引领者。

了解我们：https://open.tntlinking.com/about?site=gitee

## 💾【部署包下载】

请访问官网产品中心下载部署包：https://open.tntlinking.com/resource/productCenter?site=gitee

## 📚【支持文档】

技术文档：https://open.tntlinking.com/resource/openProductDoc?site=gitee
（含演示环境、操作手册、部署手册、开发手册、常见问题等）

产品介绍：https://open.tntlinking.com/resource/industryKnowledge?site=gitee

操作教程：https://open.tntlinking.com/resource/operationTutorial?site=gitee

沙龙回顾：https://open.tntlinking.com/resource/openSourceSalon#23?site=gitee

## 🤝【合作方式】

产品服务价格：https://open.tntlinking.com/cost?site=gitee

加入生态伙伴：https://open.tntlinking.com/ecology/becomePartner?site=gitee

## 🤗【技术社区】

请访问官网扫码加入技术社区交流：https://open.tntlinking.com/ecology/joinCommunity?site=gitee

请关注公众号【天天开源软件】以便获得最新产品更新信息。

# 项目介绍

OpenLIS实验室系统是一款适用于医院检验科、中心实验室、第三方检测机构的智能化信息管理平台，可实现样本全流程闭环管理，覆盖样本登记、检验录入、结果审核、报告打印等核心业务环节，有助于提升实验室工作效率，确保检验数据准确可靠。系统严
格遵循ISO15189医学实验室质量与能力认可标准，支持与HIS、PACS等医院信息系统无缝对接。

如需要其他产品源码，可访问天天开源官网。
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
