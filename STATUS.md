# 普惠金融管理系统 — 多 Agent 协同看板

> 更新：2026-05-11 17:30 | 统筹 Agent：Claude Code (coordinator)

---

## 模块完成度

| Agent | 模块 | 完成度 | 状态 |
|-------|------|:--:|:--:|
| A — 后端 | loan-api (SpringBoot) | ████████░ 80% | 核心完成，待联调 |
| B — 前端 | src/ (Vue 3) | ██░░░░░░░ 20% | 仅完成脚手層，缺 10 个页面 |
| C — AI | risk-engine (Flask) | ███████░░ 70% | 基本完成，待验证 |
| D — 基础设施 | docker/eureka-server | ████████░ 80% | 配置齐全，待实测 |

---

## 待完成任务

### Agent B — 前端（优先级最高）

```
□ [ ] src/views/LoginView.vue          — 登录页（粒子背景 + 玻璃态卡片）
□ [ ] src/views/DashboardView.vue      — 仪表盘（4 统计卡片 + ECharts 趋势图）
□ [ ] src/views/EnterpriseList.vue     — 企业管理列表
□ [ ] src/views/EnterpriseDetail.vue   — 企业详情（信用分仪表盘 + 时间线）
□ [ ] src/views/ApprovalList.vue       — 贷款审批列表
□ [ ] src/views/ApprovalDetail.vue     — 审批详情 + AI评分展示
□ [ ] src/views/DisbursementList.vue   — 放款管理
□ [ ] src/views/RepaymentList.vue      — 还款管理
□ [ ] src/views/OverdueList.vue        — 逾期管理
□ [ ] src/views/StatisticsView.vue     — 数据统计（ECharts）
□ [ ] src/components/layout/Sidebar.vue    — 侧边栏导航
□ [ ] src/components/layout/TopBar.vue     — 顶栏
□ [ ] src/components/common/DataTable.vue  — 通用数据表
□ [ ] src/components/common/StatsCard.vue  — 统计卡片（数字滚动）
□ [ ] src/components/charts/ChartPanel.vue — ECharts 封装
□ [ ] src/stores/auth.ts                   — 认证状态管理
□ [ ] src/assets/styles/tokens.css         — 设计令牌（CSS变量）
□ [ ] src/assets/styles/global.css         — 全局样式
```

### Agent A — 后端（联调修复）

```
□ [ ] 验证所有 27 个 API endpoint 可访问
□ [ ] 确认 /api/auth/login 返回格式与前端对接一致
□ [ ] 确认 CORS 配置允许 localhost:5173
□ [ ] 提供 loan-api 启动用的 application.yml 到 docker 目录
□ [ ] 修复编译错误（如有）
```

### Agent C — AI 风控（验证）

```
□ [ ] 确认 risk-engine/app.py 可独立启动
□ [ ] 确认模型文件 .pkl 存在或自动训练
□ [ ] 验证 /api/risk/predict 接口返回正确格式
```

### Agent D — 基础设施（补充）

```
□ [ ] docker/mysql/init.sql 包含建表 + 测试数据
□ [ ] docker/nginx/nginx.conf 配置完成
□ [ ] 整理 .env.example 供各 agent 参考
```

---

## API 契约（前后端对接关键）

| 前端调用 | Method | Path | 所在文件 |
|---------|--------|------|---------|
| authApi.login() | POST | /api/auth/login | src/api/auth.ts |
| authApi.register() | POST | /api/auth/register | src/api/auth.ts |
| enterpriseApi.getLoans() | GET | /api/enterprise/loans | src/api/enterprise.ts |
| enterpriseApi.applyLoan() | POST | /api/enterprise/loans | src/api/enterprise.ts |
| enterpriseApi.getCalculator() | GET | /api/enterprise/calculator | src/api/enterprise.ts |
| approvalApi.getEnterprises() | GET | /api/approval/enterprises | src/api/approval.ts |
| approvalApi.getLoans() | GET | /api/approval/loans | src/api/approval.ts |
| approvalApi.approveLoan() | PUT | /api/approval/loans/{id}/approve | src/api/approval.ts |
| approvalApi.getDisbursements() | GET | /api/approval/disbursements | src/api/approval.ts |
| approvalApi.grantLoan() | PUT | /api/approval/disbursements/{id}/grant | src/api/approval.ts |
| statisticsApi.getOverview() | GET | /api/statistics/loan-overview | src/api/statistics.ts |
| riskApi.predict() | POST | /api/risk/predict | 内部调用 |

**统一响应格式**：`{ code: 200, message: "success", data: {...} }`

---

## 开发顺序建议

```
现在 → 先做前端（最大缺口）
  ├─ Step 1: tokens.css + global.css（设计令牌先行）
  ├─ Step 2: Sidebar + TopBar（布局框架）
  ├─ Step 3: LoginView（认证流验证）
  ├─ Step 4: DashboardView（主视觉面）
  ├─ Step 5: EnterpriseList + Detail
  ├─ Step 6: ApprovalList + Detail
  └─ Step 7: 其他管理页面
        ↓
前端完成 → 前后端联调
  ├─ 启动 docker-compose up
  ├─ 验证登录流
  └─ 端到端测试贷款全流程
        ↓
联调通过 → 系统测试 → 文档
```

---

## 环境变量（共用）

```
MYSQL_ROOT_PASSWORD=root123456
MYSQL_DATABASE=inclusive_finance
MYSQL_USER=fin_user
MYSQL_PASSWORD=fin_pass_2026
REDIS_PASSWORD=redis123
JWT_SECRET=inclusive-finance-jwt-secret-2026
RISK_ENGINE_URL=http://localhost:5000
```
