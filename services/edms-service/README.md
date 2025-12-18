# EDMS Service (电子文档管理系统)

EDMS (Electronic Document Management System) 是GMP系统的核心组件之一，负责企业级文档和文件管理。本服务已完成与原File服务的合并，现在统一提供文件存储、文档管理和版本控制功能。

## 功能特性

- **文档管理**：完整的文档创建、编辑、存储和检索功能
- **版本控制**：支持文档版本管理和历史记录追踪
- **权限控制**：基于角色的访问控制(RBAC)确保文档安全
- **审批流程**：可配置的文档审批工作流
- **电子签名**：符合GMP要求的电子签名功能
- **在线协作**：通过集成NextCloud和OnlyOffice实现在线文档编辑和协作
- **合规审计**：完整的审计追踪功能，满足FDA 21 CFR Part 11要求
- **AI合规性审核**：利用人工智能技术进行文档合规性自动检查

## 技术架构

### 核心组件

- **控制器层**：处理HTTP请求，提供RESTful API接口
- **服务层**：实现业务逻辑，协调各组件工作
- **数据访问层**：负责数据持久化操作
- **存储层**：使用Minio进行对象存储，存储实际文件内容
- **兼容层**：确保原File服务的API请求能够正常工作

### 技术栈

- **后端**：Spring Boot 3.2.5, Spring Security, Spring Data JPA
- **数据库**：PostgreSQL (主数据库), H2 (测试环境)
- **存储**：Minio对象存储
- **缓存**：Redis
- **消息队列**：RabbitMQ
- **搜索**：Elasticsearch
- **文档协作**：NextCloud, OnlyOffice

## 系统集成

### NextCloud & OnlyOffice 集成

为了提供强大的在线文档编辑和协作功能，EDMS集成了NextCloud和OnlyOffice：

- **NextCloud**：作为文档协作平台，提供用户管理、文件存储和版本控制
- **OnlyOffice**：作为文档编辑器，支持Word、Excel、PowerPoint等格式的在线编辑
- **协作编辑**：支持多用户实时协作编辑文档
- **单点登录(SSO)**：用户在EDMS系统登录后可无缝访问NextCloud和OnlyOffice功能

详细集成方案请参考：[NEXTCLOUD_ONLYOFFICE_INTEGRATION_DESIGN.md](NEXTCLOUD_ONLYOFFICE_INTEGRATION_DESIGN.md)

### AI合规性审核

EDMS引入了AI技术来提升文档合规性审核的效率和准确性：

- **文档内容合规性检查**：自动检查文档内容是否符合GMP要求
- **语言质量审核**：检查文档的语法、拼写和表达清晰度
- **版本差异分析**：分析文档修订的变更内容和影响
- **智能建议**：提供文档改进建议

## API文档

系统提供完整的RESTful API接口，详细信息可通过Swagger UI访问：
- 本地开发环境: http://localhost:8085/swagger-ui.html

## 部署说明

### 环境要求

- JDK 17
- Maven 3.8+
- Docker (可选，用于依赖服务)
- PostgreSQL
- Minio
- Redis
- RabbitMQ
- Elasticsearch

### 运行方式

1. 使用Maven构建项目：
   ```bash
   mvn clean install
   ```

2. 运行应用：
   ```bash
   mvn spring-boot:run
   ```

   或者
   
   ```bash
   java -jar target/edms-service-1.1.2.jar
   ```

### 配置文件

应用配置位于 `src/main/resources/application.yml`，可根据环境需要进行调整。

## 测试

运行单元测试：
```bash
mvn test
```

生成测试覆盖率报告：
```bash
mvn jacoco:report
```

## 文档

- [架构设计文档](ARCHITECTURE.md)
- [NextCloud & OnlyOffice集成设计](NEXTCLOUD_ONLYOFFICE_INTEGRATION_DESIGN.md)
- [File服务合并计划](MERGE_PLAN.md)
- API文档: 通过Swagger UI访问

## 贡献

欢迎提交Issue和Pull Request来帮助改进EDMS服务。