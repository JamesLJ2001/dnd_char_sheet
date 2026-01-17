# D&D Character Sheet - 项目全景文档 (Private Group Edition)

## 1. 项目愿景
这是一个专为 **4人固定跑团小组** 定制的角色状态管理工具。
- **核心场景**：跑团时，玩家在大屏/手机上点开网页，直接查看和修改自己的血量、状态。
- **无登录模式**：因为是熟人局，不需要登录验证，直接进入选择界面。

## 2. 核心架构 (Monorepo)
- **Backend (`/server`)**: Java 17, Spring Boot, JPA, PostgreSQL.
- **Frontend (`/web`)**: Vue 3, Pinia, Vue Router.

## 3. 页面流转 (User Flow)
1.  **大厅页 (Lobby)**: 访问首页，展示 4 张角色卡片（头像+名字）。
2.  **详情页 (Sheet)**: 点击某张卡片 -> 进入该角色的详细控制面板（调整HP、查看属性、升级）。

## 4. 预设数据 (Hardcoded Scope)
系统初始化时，必须包含以下 4 个固定角色 (ID 1-4)：
1.  **ID 1**: 战士 (你的角色名)
2.  **ID 2**: 法师 (朋友A)
3.  **ID 3**: 牧师 (朋友B)
4.  **ID 4**: 盗贼 (朋友C)
*(具体数值我们在后端初始化脚本中配置)*

## 5. 核心数据模型 (DndCharacter)
- `id`: Long (1-4)
- `name`: String (角色名)
- `playerName`: String (玩家名)
- `dndClass`: String (职业)
- `level`: int (当前等级)
- `maxHp`: int (最大血量)
- `currentHp`: int (当前血量 - 最常修改的字段)
- `armorClass`: int (AC/护甲等级)
- `strength`: int (力量)
- `dexterity`: int (敏捷)
- `constitution`: int (体质)
- `intelligence`: int (智力)
- `wisdom`: int (感知)
- `charisma`: int (魅力)