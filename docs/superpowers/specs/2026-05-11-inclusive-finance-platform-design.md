# 普惠金融管理系统 — 架构设计文档

> **项目周期**：单人 3 周 | **架构模式**：轻量微服务（Eureka 注册中心 + 3 独立服务）
> **多 Agent 协同开发**：4 个 Agent 并行开发，通过 API 文档接口对接

---

## 1. 系统架构总览

```
┌──────────────────────────────────────────────────────────────────┐
│                        Nginx :80 (Reverse Proxy)                  │
│                    static/ → Vue dist  |  /api/ → services        │
└────────┬──────────────────────┬──────────────────────┬───────────┘
         │                      │                      │
    ┌────▼────────┐      ┌──────▼──────┐       ┌──────▼──────────┐
    │  :5173       │      │  :8081      │       │  :5000          │
    │  Loan-UI     │      │  Loan-API   │       │  Risk-Engine    │
    │  Vue 3 + Vite│ ───→ │  SpringBoot │ ───→  │  Flask (Python) │
    │              │      │  + JPA      │       │  Scikit-learn   │
    └──────────────┘      └──────┬──────┘       └─────────────────┘
                                 │
                          ┌──────▼──────┐
                          │  MySQL :3306 │
                          │  Redis :6379 │
                          └─────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                      Eureka Server :8761                          │
│            Service Registry (loan-api, risk-engine)              │
└──────────────────────────────────────────────────────────────────┘
```

### 子系统划分

| 子系统 | 端口 | 技术栈 | 说明 |
|--------|:----:|--------|------|
| **eureka-server** | 8761 | SpringBoot + Eureka | 服务注册中心 |
| **loan-api** | 8081 | SpringBoot 3.5 + JPA + Redis | 核心业务：企业贷款 + 贷款审批 |
| **risk-engine** | 5000 | Python Flask + Scikit-learn | AI 风控：信用评分预测 |
| **loan-ui** | 5173 | Vue 3 + Vite + Pinia | 审批端 SPA（管理后台） |
| **loan-enterprise** | 内嵌于 loan-api | Thymeleaf SSR | 企业端页面（B端用户） |

---

## 2. 技术栈明细

### 2.1 后端 (loan-api)

| 层 | 技术 | 版本 |
|---|------|------|
| 框架 | Spring Boot | 3.5.x |
| ORM | Spring Data JPA + Hibernate | — |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis（Spring Cache + Redisson） | 7.x |
| 服务注册 | Spring Cloud Netflix Eureka Client | 4.3.0 |
| 安全 | Spring Security + JWT | — |
| 构建 | Maven | 3.9+ |
| JDK | OpenJDK | 17 |

### 2.2 AI 风控 (risk-engine)

| 层 | 技术 | 版本 |
|---|------|------|
| 框架 | Flask | 3.x |
| ML | Scikit-learn | 1.5+ |
| 数据 | Pandas + NumPy | — |
| 服务注册 | 自定义 Eureka Client（REST 注册） | — |

### 2.3 前端 (loan-ui)

| 层 | 技术 | 版本 |
|---|------|------|
| 框架 | Vue 3 Composition API | 3.5+ |
| 构建 | Vite | 6.x |
| 路由 | Vue Router | 4.x |
| 状态管理 | Pinia | 2.x |
| HTTP | Axios | 1.x |
| 动画 | GSAP + CSS Custom Properties | — |
| UI 组件 | 自研科技风格组件（玻璃态 + 粒子效果） | — |
| 图表 | ECharts 5 | — |

### 2.4 基础设施

| 组件 | 用途 |
|------|------|
| Nginx | 反向代理 + 静态资源 |
| Docker Compose | 一键启动全部服务 |
| Git | 版本管理，主干开发 |

---

## 3. 数据库设计（核心表）

### 3.1 ER 关系

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  enterprise  │────→│  loan_apply  │←────│  bank_info   │
│  企业信息     │     │  贷款申请表   │     │  银行信息     │
└──────┬───────┘     └──────┬───────┘     └──────────────┘
       │                    │
       ▼                    ▼
┌──────────────┐     ┌──────────────┐
│  user_info   │     │ repayment    │
│  用户表       │     │  还款记录     │
└──────────────┘     └──────────────┘
                              │
┌──────────────┐              ▼
│ credit_score │     ┌──────────────┐
│  信用评分     │     │  overdue     │
└──────────────┘     │  逾期记录     │
                     └──────────────┘
```

### 3.2 建表 DDL（核心表）

```sql
-- 企业信息表
CREATE TABLE enterprise (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '企业名称',
    credit_code VARCHAR(50) UNIQUE COMMENT '统一社会信用代码',
    legal_person VARCHAR(50) COMMENT '法人姓名',
    legal_id_card VARCHAR(18) COMMENT '法人身份证',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(255) COMMENT '注册地址',
    industry VARCHAR(50) COMMENT '所属行业',
    registered_capital DECIMAL(15,2) COMMENT '注册资本(万元)',
    establish_date DATE COMMENT '成立日期',
    employee_count INT COMMENT '员工人数',
    annual_revenue DECIMAL(15,2) COMMENT '年营收(万元)',
    status TINYINT DEFAULT 1 COMMENT '状态 1:正常 0:禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '企业信息';

-- 用户表
CREATE TABLE user_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enterprise_id BIGINT COMMENT '所属企业ID',
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    role VARCHAR(20) DEFAULT 'ENTERPRISE' COMMENT '角色 ENTERPRISE/APPROVER/ADMIN',
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (enterprise_id) REFERENCES enterprise(id)
) COMMENT '用户信息';

-- 贷款申请表
CREATE TABLE loan_apply (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enterprise_id BIGINT NOT NULL COMMENT '企业ID',
    user_id BIGINT NOT NULL COMMENT '申请人ID',
    loan_amount DECIMAL(15,2) NOT NULL COMMENT '贷款金额',
    loan_term INT NOT NULL COMMENT '贷款期限(月)',
    loan_purpose VARCHAR(255) COMMENT '贷款用途',
    interest_rate DECIMAL(5,4) COMMENT '年利率',
    repayment_method VARCHAR(20) COMMENT '还款方式',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态 PENDING/APPROVED/REJECTED/GRANTED/REPAID/OVERDUE',
    apply_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    approve_date DATETIME COMMENT '审批日期',
    approve_user_id BIGINT COMMENT '审批人ID',
    approve_comment VARCHAR(500) COMMENT '审批意见',
    credit_score DECIMAL(5,2) COMMENT 'AI信用评分',
    FOREIGN KEY (enterprise_id) REFERENCES enterprise(id),
    FOREIGN KEY (user_id) REFERENCES user_info(id)
) COMMENT '贷款申请';

-- 还款记录表
CREATE TABLE repayment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id BIGINT NOT NULL COMMENT '贷款申请ID',
    period_no INT NOT NULL COMMENT '还款期数',
    amount DECIMAL(15,2) NOT NULL COMMENT '应还金额',
    paid_amount DECIMAL(15,2) COMMENT '实还金额',
    due_date DATE NOT NULL COMMENT '到期日期',
    paid_date DATE COMMENT '实际还款日期',
    status VARCHAR(20) DEFAULT 'UNPAID' COMMENT '状态 UNPAID/PAID/OVERDUE',
    FOREIGN KEY (loan_id) REFERENCES loan_apply(id)
) COMMENT '还款记录';

-- 逾期记录表
CREATE TABLE overdue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_id BIGINT NOT NULL,
    enterprise_id BIGINT NOT NULL,
    overdue_days INT NOT NULL COMMENT '逾期天数',
    overdue_amount DECIMAL(15,2) NOT NULL COMMENT '逾期金额',
    penalty DECIMAL(15,2) DEFAULT 0 COMMENT '罚息',
    start_date DATE NOT NULL,
    end_date DATE COMMENT '结清日期',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT 'ACTIVE/SETTLED',
    FOREIGN KEY (loan_id) REFERENCES loan_apply(id),
    FOREIGN KEY (enterprise_id) REFERENCES enterprise(id)
) COMMENT '逾期记录';

-- AI信用评分表
CREATE TABLE credit_score (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enterprise_id BIGINT NOT NULL,
    score DECIMAL(5,2) NOT NULL COMMENT '信用评分(0-100)',
    model_version VARCHAR(20) COMMENT '模型版本',
    features TEXT COMMENT '特征值JSON',
    evaluated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (enterprise_id) REFERENCES enterprise(id)
) COMMENT '企业信用评分';
```

---

## 4. REST API 接口文档

> **Base URL**：`http://localhost:8081/api`
> **Auth**：JWT Bearer Token（`Authorization: Bearer <token>`）
> **Content-Type**：`application/json`

### 4.1 认证模块 (AuthController)

#### `POST /api/auth/login`
- **说明**：用户登录，返回 JWT Token
- **Request**：
```json
{
  "username": "enterprise01",
  "password": "123456"
}
```
- **Response** `200`：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOi...",
    "expiresIn": 86400,
    "user": { "id": 1, "username": "enterprise01", "realName": "张三", "role": "ENTERPRISE", "enterpriseId": 1 }
  }
}
```

#### `POST /api/auth/register`
- **说明**：企业用户注册
- **Request**：
```json
{
  "username": "newuser",
  "password": "123456",
  "realName": "李四",
  "phone": "13800001111",
  "enterpriseId": 1
}
```

---

### 4.2 企业端 — 企业贷款系统 (EnterpriseController) `ROLE_ENTERPRISE`

#### `GET /api/enterprise/loans?page=1&size=10&status=`
- **说明**：查询当前企业的贷款列表
- **Response**：
```json
{
  "code": 200,
  "data": {
    "total": 25,
    "page": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "loanAmount": 500000.00,
        "loanTerm": 12,
        "interestRate": 0.0435,
        "status": "APPROVED",
        "applyDate": "2026-04-15",
        "creditScore": 78.5
      }
    ]
  }
}
```

#### `POST /api/enterprise/loans`
- **说明**：提交贷款申请
- **Request**：
```json
{
  "loanAmount": 500000.00,
  "loanTerm": 12,
  "loanPurpose": "扩大生产线",
  "repaymentMethod": "EQUAL_INSTALLMENT"
}
```

#### `GET /api/enterprise/loans/{id}`
- **说明**：贷款详情（含还款计划、审批意见）

#### `POST /api/enterprise/loans/{id}/sign`
- **说明**：电子签约（状态 APPROVED → GRANTED）

#### `GET /api/enterprise/repayments?loanId={id}&status=`
- **说明**：还款计划查询

#### `POST /api/enterprise/repayments/{id}/pay`
- **说明**：单期还款

#### `GET /api/enterprise/overdue`
- **说明**：逾期记录查询

#### `GET /api/enterprise/calculator?amount=500000&term=12&rate=0.0435`
- **说明**：贷款计算器（等额本息/等额本金两种模式）
- **Response**：
```json
{
  "code": 200,
  "data": {
    "mode": "EQUAL_INSTALLMENT",
    "monthlyPayment": 42612.50,
    "totalInterest": 11350.00,
    "totalPayment": 511350.00,
    "schedule": [
      { "period": 1, "principal": 40796.50, "interest": 1816.00, "remaining": 459203.50 }
    ]
  }
}
```

---

### 4.3 审批端 — 贷款审批系统 (ApprovalController) `ROLE_APPROVER`

#### `GET /api/approval/enterprises?page=1&size=10&keyword=`
- **说明**：企业管理列表（搜索、分页）

#### `GET /api/approval/enterprises/{id}`
- **说明**：企业详情（含历史贷款、信用分）

#### `GET /api/approval/loans?page=1&size=10&status=PENDING`
- **说明**：贷款审批列表（可按状态筛选）

#### `PUT /api/approval/loans/{id}/approve`
- **说明**：贷款审批
- **Request**：
```json
{
  "action": "APPROVE",
  "comment": "企业信用良好，风险可控"
}
```

#### `GET /api/approval/loans/{id}`
- **说明**：贷款详情（含企业信息、AI评分、还款进度）

#### `GET /api/approval/disbursements?status=PENDING`
- **说明**：放款管理列表（审批通过待放款）

#### `PUT /api/approval/disbursements/{loanId}/grant`
- **说明**：确认放款

#### `GET /api/approval/repayments?loanId=&status=`
- **说明**：还款综合管理

#### `GET /api/approval/overdues?page=1&size=10`
- **说明**：逾期管理列表

#### `GET /api/approval/risk-query?enterpriseId=`
- **说明**：风控查询（企业信用分 + 历史评估记录）

#### `POST /api/approval/risk-evaluate`
- **说明**：触发风控评估（调用 risk-engine）
- **Request**：
```json
{
  "enterpriseId": 1
}
```

---

### 4.4 统计报表 (StatisticsController) `ROLE_APPROVER`

#### `GET /api/statistics/loan-overview`
- **说明**：贷款总览（申请数/审批数/放款数/逾期数，可按时间范围筛选）
- **Response**：
```json
{
  "code": 200,
  "data": {
    "totalApply": 156,
    "totalApproved": 132,
    "totalDisbursed": 128,
    "totalOverdue": 5,
    "approvalRate": 84.6,
    "monthly": [
      { "month": "2026-01", "apply": 45, "approved": 38, "disbursed": 36 }
    ]
  }
}
```

#### `GET /api/statistics/disbursement-trend`
- **说明**：放款趋势数据（ECharts 图表数据源）

#### `GET /api/statistics/repayment-trend`
- **说明**：还款趋势数据

#### `GET /api/statistics/overdue-analysis`
- **说明**：逾期分析（按行业/金额区间/期限）

---

### 4.5 AI 风控服务 (risk-engine :5000) — 供 loan-api 内部调用

#### `POST /api/risk/predict`
- **说明**：企业信用评分预测
- **Request**：
```json
{
  "enterpriseId": 1,
  "features": {
    "registeredCapital": 500,
    "employeeCount": 120,
    "annualRevenue": 3000,
    "establishYears": 8,
    "industry": "制造业",
    "previousLoans": 3,
    "previousOverdues": 0,
    "debtRatio": 0.35
  }
}
```
- **Response**：
```json
{
  "code": 200,
  "data": {
    "enterpriseId": 1,
    "creditScore": 78.5,
    "riskLevel": "LOW",
    "confidence": 0.89,
    "modelVersion": "v1.0"
  }
}
```

#### `POST /api/risk/evaluate`
- **说明**：批量评估（定时任务触发）

---

## 5. 前端页面结构 (loan-ui)

### 5.1 路由表

| 路径 | 组件 | 说明 |
|------|------|------|
| `/login` | LoginView | 登录页（玻璃态卡片 + 粒子背景） |
| `/dashboard` | DashboardView | 仪表盘大屏（实时数据卡片 + ECharts） |
| `/enterprises` | EnterpriseList | 企业管理列表（搜索 + 表格） |
| `/enterprises/:id` | EnterpriseDetail | 企业详情（信用分 + 历史贷款时间线） |
| `/approvals` | ApprovalList | 贷款审批列表（状态筛选 + 批量操作） |
| `/approvals/:id` | ApprovalDetail | 审批详情 + AI评分展示 |
| `/disbursements` | DisbursementList | 放款管理 |
| `/repayments` | RepaymentList | 还款综合管理 |
| `/overdues` | OverdueList | 逾期管理 |
| `/statistics` | StatisticsView | 数据统计（ECharts 图表） |

### 5.2 组件树

```
App.vue
├── SidebarNav.vue          ← 侧边栏导航（玻璃态）
├── TopBar.vue              ← 顶栏（用户信息 + 通知）
├── router-view
│   ├── LoginView.vue       ← 粒子背景 + 登录卡片
│   ├── DashboardView.vue   ← 4 个统计卡片 + 趋势图
│   │   └── StatsCard.vue   ← 数字滚动动画
│   ├── EnterpriseList.vue  ← 搜索框 + 表格 + 分页
│   │   └── DataTable.vue   ← 通用数据表（骨架屏）
│   ├── ApprovalDetail.vue
│   │   ├── CreditGauge.vue ← 信用分仪表盘（SVG 动画）
│   │   └── Timeline.vue    ← 审批时间线
│   └── StatisticsView.vue
│       └── ChartPanel.vue  ← ECharts 封装
└── Toast.vue               ← 全局通知
```

### 5.3 视觉风格规范

| 要素 | 规范 |
|------|------|
| 主色调 | `--color-primary: #6366F1` (Indigo) |
| 强调色 | `--color-accent: #06B6D4` (Cyan) |
| 背景 | `--color-bg: #0B0F19` (深蓝黑) |
| 卡片 | `background: rgba(255,255,255,0.04)` + `backdrop-filter: blur(20px)` 玻璃态 |
| 边框 | `border: 1px solid rgba(99,102,241,0.15)` |
| 动画 | GSAP + CSS transition，页面入场用 `opacity` + `translateY` 渐显 |
| 数字滚动 | requestAnimationFrame 实现数值从 0 到目标值的滚动效果 |
| 数据表 | 行悬浮发光边框 `box-shadow: inset 0 0 0 1px var(--color-primary)` |
| 粒子背景 | Canvas 粒子系统（login 页专用，60fps） |

---

## 6. 多 Agent 开发分工

### 6.1 Agent A — 后端 Loan-API（核心业务）

**负责目录**：`loan-api/`

**产出物**：
1. SpringBoot 项目骨架（Maven 多模块）
2. 数据层：JPA Entity + Repository（全部 6 张表）
3. 业务层：Service 层（认证/企业贷款/审批/统计）
4. 控制层：Controller（约 20 个 API endpoint）
5. 安全层：JWT 认证过滤器 + 角色权限
6. 缓存层：Redis 配置（贷款列表缓存 / Session）
7. Eureka Client 配置
8. 全局异常处理 + 统一响应格式（Result<T>）

**依赖**：先完成数据库设计 → Agent D 提供 MySQL/Redis 环境

---

### 6.2 Agent B — 前端 Loan-UI（Vue 3 审批端 + Thymeleaf 企业端）

**负责目录**：`loan-ui/`（Vue） + `loan-api/src/main/resources/templates/`（Thymeleaf）

**产出物**：
1. Vue 3 项目骨架（Vite + Pinia + Vue Router + Axios 拦截器）
2. 登录页（粒子背景 + 玻璃态卡片）
3. Dashboard 大屏（StatsCard 数字滚动 + ECharts 趋势图）
4. 企业管理页（DataTable + 搜索 + 分页 + 骨架屏）
5. 企业详情页（信用分仪表盘 SVG + Timeline）
6. 贷款审批页（状态流转 + 审核弹窗）
7. 放款/还款/逾期管理页
8. 数据统计页（多图表联动）
9. 全局组件：Sidebar、TopBar、Toast、DataTable、ChartPanel
10. Thymeleaf 企业端页面（4 个页面：注册/登录/贷款列表/贷款申请）

**依赖**：Agent A 提供 API 文档后开始 → 对接所有接口

---

### 6.3 Agent C — AI 风控 Risk-Engine

**负责目录**：`risk-engine/`

**产出物**：
1. Flask 项目骨架
2. Scikit-learn 信用评分模型（逻辑回归，UCI Credit 数据集）
3. `/api/risk/predict` 接口
4. `/api/risk/evaluate` 批量评估接口
5. 模型训练脚本 + pickle 模型文件
6. Dockerfile
7. Eureka 手动注册脚本

**依赖**：Agent A 提供企业特征数据格式

---

### 6.4 Agent D — 基础设施

**负责目录**：项目根目录

**产出物**：
1. `docker-compose.yml`（MySQL + Redis + Nginx + Eureka Server）
2. Eureka Server 项目（独立 SpringBoot 应用）
3. Nginx 配置文件
4. 数据库初始化 SQL（含测试数据 5 家企业 + 20 条贷款记录）
5. Redis 配置文件
6. Shell 脚本：`start-all.sh` / `stop-all.sh`
7. `.env` 环境变量文件

**依赖**：最先启动，为其他 Agent 提供基础设施

---

## 7. 开发时序（3 周）

```
Week 1                          Week 2                          Week 3
M  T  W  T  F  S  S    M  T  W  T  F  S  S    M  T  W  T  F  S  S
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
▐  Agent D: 基础设施  ▌
▐DB+Eureka+Redis+Nginx▌
  ▐  Agent A: 后端开发                                        ▌
  ▐认证→企业端API→审批端API→统计→联调                          ▌
  ▐  Agent C: AI风控   ▌
  ▐模型训练→Flask API▌
          ▐  Agent B: 前端开发                  ▌
          ▐项目骨架→登录→Dashboard→列表→详情→动画 ▌
                                          ▐集成联调▌▐文档▌
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Day 1-2     Day 3-8          Day 9-15         Day 16-19 Day 20-21
基础设施    后端+AI并行       前端+对接          集成测试   文档答辩
```

### 并行窗口

| 阶段 | 天数 | Agent A | Agent B | Agent C | Agent D |
|------|:--:|---------|---------|---------|---------|
| 基础设施 | D1-2 | — | — | — | DB + Eureka + Redis + Nginx |
| 后端核心 | D3-8 | 认证+企业端+审批端全部API | — | 模型训练+Flask API | 测试数据+配置调优 |
| 前端开发 | D9-15 | 联调修复 | 全部页面+动画 | 联调修复 | 部署脚本 |
| 集成测试 | D16-19 | 修复Bug | 修复Bug | 修复Bug | Nginx+部署 |
| 文档答辩 | D20-21 | 论文/PPT | 论文/PPT | 论文/PPT | 论文/PPT |

---

## 8. 约定规范

### 8.1 API 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1715414400000
}
```

| code | 含义 |
|:----:|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器异常 |

### 8.2 命名规范

- **Java**：实体类 `PascalCase`（`EnterpriseInfo`），Controller `*Controller`，Service `*Service`
- **Vue**：组件 `PascalCase`（`DashboardView.vue`），composable `use*`（`useAuth.ts`）
- **API路径**：`/api/{module}/{resource}`，全部小写，复数名词
- **数据库**：表名 `snake_case`，字段名 `snake_case`，主键 `id`，时间字段 `created_at` / `updated_at`

### 8.3 代码提交规范

- 每个 Agent 在自己的目录下开发
- 提交信息：`feat(module): description`
- API 接口变更需同步更新此文档

---

## 9. 附录：API 接口速查表

| 序号 | Method | Path | 角色 | 说明 |
|:----:|--------|------|------|------|
| 1 | POST | `/api/auth/login` | Public | 用户登录 |
| 2 | POST | `/api/auth/register` | Public | 用户注册 |
| 3 | GET | `/api/enterprise/loans` | ENTERPRISE | 贷款列表 |
| 4 | POST | `/api/enterprise/loans` | ENTERPRISE | 贷款申请 |
| 5 | GET | `/api/enterprise/loans/{id}` | ENTERPRISE | 贷款详情 |
| 6 | POST | `/api/enterprise/loans/{id}/sign` | ENTERPRISE | 电子签约 |
| 7 | GET | `/api/enterprise/repayments` | ENTERPRISE | 还款计划 |
| 8 | POST | `/api/enterprise/repayments/{id}/pay` | ENTERPRISE | 单期还款 |
| 9 | GET | `/api/enterprise/overdue` | ENTERPRISE | 逾期记录 |
| 10 | GET | `/api/enterprise/calculator` | ENTERPRISE | 贷款计算器 |
| 11 | GET | `/api/approval/enterprises` | APPROVER | 企业管理 |
| 12 | GET | `/api/approval/enterprises/{id}` | APPROVER | 企业详情 |
| 13 | GET | `/api/approval/loans` | APPROVER | 审批列表 |
| 14 | PUT | `/api/approval/loans/{id}/approve` | APPROVER | 贷款审批 |
| 15 | GET | `/api/approval/loans/{id}` | APPROVER | 贷款详情 |
| 16 | GET | `/api/approval/disbursements` | APPROVER | 放款管理 |
| 17 | PUT | `/api/approval/disbursements/{loanId}/grant` | APPROVER | 确认放款 |
| 18 | GET | `/api/approval/repayments` | APPROVER | 还款管理 |
| 19 | GET | `/api/approval/overdues` | APPROVER | 逾期管理 |
| 20 | GET | `/api/approval/risk-query` | APPROVER | 风控查询 |
| 21 | POST | `/api/approval/risk-evaluate` | APPROVER | 触发风控评估 |
| 22 | GET | `/api/statistics/loan-overview` | APPROVER | 贷款总览 |
| 23 | GET | `/api/statistics/disbursement-trend` | APPROVER | 放款趋势 |
| 24 | GET | `/api/statistics/repayment-trend` | APPROVER | 还款趋势 |
| 25 | GET | `/api/statistics/overdue-analysis` | APPROVER | 逾期分析 |
| 26 | POST | `/api/risk/predict` | Internal | AI信用评分 |
| 27 | POST | `/api/risk/evaluate` | Internal | AI批量评估 |
