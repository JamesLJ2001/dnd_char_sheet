# 前端开发守则 (Web)

## 1. 角色定位
你负责 Vue 3 前端。注重**交互体验**和**视觉风格**。

## 2. 页面结构 (Router)
- **`/` (HomeView)**:
    - 类似于游戏“选人界面”。
    - 展示 4 个巨大的卡片/头像。
    - 点击跳转到 `/character/:id`。
- **`/character/:id` (SheetView)**:
    - 核心控制台。
    - 包含：属性六边形图、巨大的 HP 进度条（带加减按钮）、等级调整、属性调整。

## 3. 交互逻辑
- **自动保存**：任何数据的修改（如点击扣血、点击升级），必须立刻触发 API 保存。
- **乐观更新 (Optimistic UI)**：先在界面上变数字，再发请求。如果请求失败，再滚回原值（可选）。
- **风格**: D&D 奇幻风格（深色背景、羊皮纸质感、金色边框）。

## 4. 技术栈细节
- Vue 3 + Script Setup.
- **Pinia Store (`useCharacterStore`)**:
    - `actions`:
        - `fetchCharacter(id)`: 从后端拉取数据。
        - `updateCharacter(id, payload)`: **万能更新函数**。
          - 例如：`updateCharacter(1, { currentHp: 5 })` 或 `updateCharacter(1, { level: 2 })`。