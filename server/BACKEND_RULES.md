# 后端开发守则 (Server)

## 1. 角色定位
你负责 Spring Boot 后端。这是一个**无鉴权、内部使用**的 API 服务。

## 2. 关键任务：数据初始化 (Data Seeding)
- **核心逻辑**：创建一个 `CommandLineRunner`。在项目启动时，检查数据库 `dnd_characters` 表是否为空。
- **如果为空**：自动插入 4 个预设的初始角色数据（对应 Context 里的 4 个人）。
- **如果不为空**：什么都不做（保留上次的存档）。

## 3. API 规范
- **CORS**: 必须允许所有跨域 (`*`)，方便前端直连。
- **RESTful**:
    - `GET /api/characters`: 返回列表（用于大厅页）。
    - `GET /api/characters/{id}`: 返回详情（用于详情页）。
    - `PUT /api/characters/{id}`: 通用更新接口。接收 JSON Body，更新该角色的任意字段（Level, HP, Stats 等）。

## 4. 简化原则
- 不需要 Security/JWT/Login。
- 使用 JPA 的 `save()` 方法即可实现 Update 逻辑。