# D&D 角色表 v2.0 - 项目文档

**最后更新**: 2026-02-08
**版本**: v2.0-iOS-fix
**状态**: ✅ iOS Safari 完美兼容

---

## 📋 项目概述

这是一个基于 **Vue 3 + Spring Boot** 的 D&D 角色表 Web 应用，采用中世纪复古主题风格，支持角色管理、HP 追踪、资源管理等 RPG 核心功能。

### 技术栈
- **前端**: Vue 3 + Vite + Pinia + Tailwind CSS
- **后端**: Spring Boot 17 + PostgreSQL + JPA
- **部署**: Docker + Docker Compose + Nginx
- **隧道**: Cloudflare Tunnel（公网访问）

---

## 🚀 快速启动指南

### 前置要求
- Docker 和 Docker Compose 已安装
- 端口 80 未被占用

### 启动服务器

```bash
# 1. 进入项目目录
cd /home/jameslj/dnd_char_sheet

# 2. 启动所有服务（包括 Cloudflare Tunnel）
docker compose up -d --build

# 3. 等待服务启动（约 1-2 分钟）
# 检查容器状态
docker ps
```

### 访问地址
- **本地**: http://localhost
- **公网**: https://www.jameslj2001.com（通过 Cloudflare Tunnel）

---

## 📦 代码结构

```
dnd_char_sheet/
├── server/                    # Spring Boot 后端
│   ├── src/main/java/com/jameslj/
│   │   ├── config/          # 配置类（CORS、Security）
│   │   ├── controller/      # REST API 控制器
│   │   ├── entity/          # JPA 实体（Character, Resource）
│   │   ├── repository/      # 数据访问层
│   │   └── init/            # 数据初始化
│   ├── pom.xml
│   └── Dockerfile
│
├── web/                       # Vue 3 前端
│   ├── src/
│   │   ├── assets/          # 静态资源、CSS
│   │   ├── stores/          # Pinia 状态管理
│   │   ├── views/           # 页面组件
│   │   ├── router/          # 路由配置
│   │   └── main.js
│   ├── nginx.conf           # Nginx 反向代理配置
│   ├── index.html
│   ├── package.json
│   └── Dockerfile
│
├── docker-compose.yml        # 多容器编排配置
└── CLAUDE.md                 # 本文档
```

---

## 🔄 开发工作流

### 每次代码修改后的标准流程

#### 1️⃣ 修改代码
修改以下任一文件：
- 后端：`server/src/`
- 前端：`web/src/`
- 配置：`docker-compose.yml`, `nginx.conf`

#### 2️⃣ 提交代码（Git）
```bash
# 查看修改状态
git status

# 添加所有修改
git add -A

# 提交（写清楚改动内容）
git commit -m "feat: 简短描述改动内容"

# 示例：
# git commit -m "fix: 修复 iOS Safari 按钮粘滞问题"
# git commit -m "feat: 添加角色头像显示功能"
# git commit -m "refactor: 优化 Store 数据管理逻辑"
```

#### 3️⃣ 重建并重启容器
```bash
# 方案 A：重建所有服务（修改了配置文件时）
docker compose down && docker compose up -d --build

# 方案 B：只重建前端（只改了前端代码时）
docker compose up -d --build frontend

# 方案 C：只重建后端（只改了后端代码时）
docker compose up -d --build backend

# 方案 D：只重建 Cloudflare Tunnel（极少需要）
docker compose up -d --build tunnel
```

#### 4️⃣ 验证服务
```bash
# 检查容器状态
docker ps

# 检查后端日志
docker logs dnd-prod-backend --tail 50

# 检查前端日志
docker logs dnd-prod-frontend --tail 30

# 检查 Tunnel 日志
docker logs dnd-tunnel --tail 20
```

#### 5️⃣ 清除浏览器缓存（重要！）
**iOS Safari**：
```
设置 → Safari → 清除历史记录与网站数据
```

**桌面浏览器**：
- Chrome: F12 → Network 标签 → 勾选 "Disable cache"
- Firefox: F12 → Network 标签 → 设置 → 禁用缓存

---

## ⚙️ 服务器要求

### 必须运行的服务
```bash
dnd-prod-frontend    # Nginx 前端服务（端口 80）
dnd-prod-backend     # Spring Boot 后端（端口 8080）
dnd-prod-db          # PostgreSQL 数据库（端口 5432）
dnd-tunnel          # Cloudflare Tunnel（公网访问）
```

### 端口映射
- **80**: 前端 HTTP 服务
- **8080**: 后端 API（仅容器内部访问）
- **5432**: PostgreSQL（仅容器内部访问）

### 环境变量
后端环境变量在 `docker-compose.yml` 中配置：
```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/postgres
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: 123456
```

---

## 🐛 已知问题与解决方案

### ✅ iOS Safari 兼容性（已修复）
**问题**：按钮点击后数值不更新

**根本原因**：
1. ❌ CORS 配置只允许 `localhost:5173`，阻止了 Cloudflare Tunnel 域名
2. ❌ CSS `background-attachment: fixed` 导致 iOS 渲染阻塞
3. ❌ 按钮焦点粘滞（白色蒙版）

**解决方案**：
1. ✅ 后端 CORS 配置添加 `https://www.jameslj2001.com`
2. ✅ 删除 `background-attachment: fixed`，改用 `min-height: 100vh`
3. ✅ 添加 `:key="uiKey"` 强制 Vue 重绘机制
4. ✅ 添加 `@media (hover: hover)` 媒体查询
5. ✅ 移除 iOS 点击高亮（`-webkit-tap-highlight-color: transparent`）

**相关文件**：
- [server/src/main/java/com/jameslj/config/CorsConfig.java](server/src/main/java/com/jameslj/config/CorsConfig.java)
- [web/src/assets/medieval-theme.css](web/src/assets/medieval-theme.css)
- [web/src/views/CharacterDetailView.vue](web/src/views/CharacterDetailView.vue)

---

## 📝 重要配置说明

### 1. CORS 跨域配置
**文件**: `server/src/main/java/com/jameslj/config/CorsConfig.java`

```java
config.addAllowedOrigin("http://localhost:5173");
config.addAllowedOrigin("https://www.jameslj2001.com");
config.addAllowedOriginPattern("*"); // 允许所有来源
```

**⚠️ 注意**：
- 修改域名后必须同步更新此配置
- 生产环境建议使用具体域名而非通配符

### 2. Nginx 反向代理
**文件**: `web/nginx.conf`

```nginx
location /api/ {
    proxy_pass http://backend:8080/api/;
    add_header Cache-Control "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0";
}
```

**功能**：
- 转发 `/api/*` 请求到后端
- 禁止缓存（解决 iOS Safari 缓存问题）

### 3. Cloudflare Tunnel
**文件**: `docker-compose.yml`

```yaml
tunnel:
  image: cloudflare/cloudflared:latest
  command: tunnel --no-autoupdate run --token <YOUR_TOKEN>
```

**⚠️ 注意**：
- Token 已配置，无需修改
- Tunnel 自动连接到 `www.jameslj2001.com`

---

## 🧪 测试清单

### 功能测试
- [ ] 首页显示 4 个角色卡片
- [ ] 点击卡片进入详情页
- [ ] HP +/- 按钮功能正常
- [ ] 资源使用/恢复按钮功能正常
- [ ] 长休按钮重置所有状态
- [ ] 图片正确加载

### iOS Safari 测试
- [ ] 清除缓存后页面正常显示
- [ ] 按钮点击后数值立即更新
- [ ] 按钮不会卡在白色
- [ ] 触摸响应流畅

### API 测试
```bash
# 测试后端 API
curl http://localhost/api/characters

# 测试 HP 调整
curl -X PATCH http://localhost/api/characters/1/hp \
  -H "Content-Type: application/json" \
  -d '{"delta": -1}'
```

---

## 🗃️ Git 分支管理

### 当前分支
- `main` - 主分支（稳定版本）
- `feature/update_web` - 功能开发分支（当前）

### 提交历史
```
0f96012 fix: 解决 iOS Safari 按钮点击后数值不更新的问题
a3c9f3e 修复 iOS Safari 数值不更新问题
1cdd0d1 修复 iOS Safari 响应式更新问题
```

### 推送到远程
```bash
# 推送当前分支
git push origin feature/update_web

# 合并到 main（如果需要）
git checkout main
git merge feature/update_web
git push origin main
```

---

## 🚨 常见问题排查

### 问题 1：容器启动失败
**症状**：`docker compose up` 报错

**排查步骤**：
```bash
# 查看容器日志
docker compose logs backend
docker compose logs frontend

# 完全重建
docker compose down
docker compose up -d --build --force-recreate
```

### 问题 2：API 返回 403
**症状**：按钮点击后无反应，控制台显示 403 Forbidden

**解决方案**：
1. 检查 CORS 配置是否包含当前域名
2. 清除浏览器缓存
3. 重启后端容器：
   ```bash
   docker compose up -d --build backend
   ```

### 问题 3：页面无法访问
**症状**：浏览器显示 "无法访问此网站"

**排查步骤**：
```bash
# 1. 检查容器状态
docker ps

# 2. 检查前端日志
docker logs dnd-prod-frontend

# 3. 检查 Tunnel 是否运行
docker logs dnd-tunnel

# 4. 如果 Tunnel 停止，重启所有服务
docker compose down && docker compose up -d
```

### 问题 4：数值不更新（iOS）
**症状**：按钮点击无效果，数值不变化

**排查步骤**：
1. **强制清除缓存**：设置 → Safari → 清除历史记录
2. **关闭所有标签页**，重新打开
3. **检查控制台**（如果可以）：
   - 是否有 CORS 错误
   - 是否有 403 错误
4. **重启容器**：
   ```bash
   docker compose restart backend frontend
   ```

---

## 📦 部署清单

### 首次部署
1. ✅ 克隆代码
2. ✅ 配置 Cloudflare Tunnel Token
3. ✅ 启动服务：`docker compose up -d --build`
4. ✅ 验证访问：https://www.jameslj2001.com

### 更新部署
1. ✅ 修改代码
2. ✅ 提交代码：`git commit -m "..."`
3. ✅ 重建容器：`docker compose up -d --build`
4. ✅ 验证功能
5. ✅ 推送到远程：`git push`

### 回滚操作
```bash
# 查看提交历史
git log --oneline

# 回滚到上一个版本
git reset --hard HEAD~1

# 强制推送（谨慎使用）
git push origin feature/update_web --force
```

---

## 📞 联系与支持

### 技术栈文档
- **Vue 3**: https://vuejs.org/
- **Pinia**: https://pinia.vuejs.org/
- **Spring Boot**: https://spring.io/projects/spring-boot
- **Docker**: https://docs.docker.com/

### 相关工具
- **Docker Hub**: https://hub.docker.com/
- **Cloudflare Tunnel**: https://developers.cloudflare.com/cloudflare-one/connections/
- **Nginx**: https://nginx.org/en/docs/

---

## 📝 更新日志

### v2.0-iOS-fix (2026-02-08)
- ✅ 修复 iOS Safari 按钮点击后数值不更新问题
- ✅ CORS 配置支持 Cloudflare Tunnel 域名
- ✅ 添加 Vue 强制重绘机制（uiKey）
- ✅ 优化 iOS 触摸体验（移除点击高亮、焦点粘滞）
- ✅ 删除 CSS 渲染阻塞属性（background-attachment）

### v2.0 (2026-02-07)
- ✅ 完整的中世纪复古主题 UI
- ✅ 4 个预设角色（Paladin, Duskblade, Warlock, Bard）
- ✅ HP 管理与追踪
- ✅ 资源管理（使用/恢复）
- ✅ 长休功能
- ✅ 响应式设计（移动端/桌面端）
- ✅ Cloudflare Tunnel 公网访问

---

**文档维护**: 请在每次重大修改后更新此文档。
**问题反馈**: 遇到问题请检查"常见问题排查"章节，或查阅 Git 提交历史。
