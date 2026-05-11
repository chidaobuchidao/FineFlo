# FineFlo — 普惠金融管理系统

面向中小微企业的贷款审批管理平台，SpringBoot + Vue 3 + Python Flask 三端协作，集成 AI 风控引擎。

## 项目结构

```
FineFlo/
├── loan-api/              后端服务 (SpringBoot 3.5 + JPA + Redis)
│   ├── controller/         5 个 REST 控制器 + 1 个 MVC 控制器
│   ├── service/            4 个业务服务（Auth / Enterprise / Approval / Statistics）
│   ├── repository/         6 个 JPA Repository
│   ├── entity/             6 个实体类
│   ├── dto/                12 个请求/响应 DTO
│   ├── security/           JWT 认证 + Spring Security 角色鉴权
│   ├── config/             Redis / RestTemplate / WebMvc 配置
│   └── resources/
│       ├── templates/      企业端 Thymeleaf 页面（8 个）
│       └── application.yml
│
├── loan-ui/                审批端前端 (Vue 3 + Vite + Pinia + GSAP + ECharts)
│   ├── views/              17 个页面（登录 / Dashboard / 审批 / 还款 / 统计 等）
│   ├── components/         12 个通用组件（DataTable / StatsCard / ChartPanel 等）
│   ├── stores/             5 个 Pinia 状态管理模块
│   ├── api/                5 个 API 模块（与后端接口一一对应）
│   ├── composables/        3 个组合式函数（Auth / NumberScroll / ParticleBg）
│   └── styles/             CSS 设计令牌 + 深色科技风全局样式
│
├── risk-engine/            AI 风控引擎 (Python Flask + DeepSeek)
│   ├── app.py              风控服务主程序（双引擎：本地 LR 模型 + DeepSeek LLM）
│   ├── model/train.py      逻辑回归模型训练脚本（AUC 0.96）
│   └── requirements.txt
│
├── eureka-server/          服务注册中心 (Spring Cloud Netflix Eureka)
│
├── docker/                 Docker Compose 部署
│   ├── docker-compose.yml  MySQL + Redis + Nginx + Eureka + 各服务
│   ├── mysql/init.sql      建表 DDL + 测试数据（5 企业 / 8 用户 / 12 贷款）
│   └── nginx/nginx.conf    反向代理配置
│
├── scripts/                独立启动/停止脚本
│   ├── start-backend.sh    启动后端 (dev 模式，无需 Redis/Eureka)
│   ├── start-frontend.sh   启动前端开发服务器
│   ├── start-risk-engine.sh 启动风控引擎
│   └── stop-all.sh         一键停止全部服务
│
├── docs/                   设计文档
├── .env.example            环境变量模板
├── start-all.sh            一键启动全部服务
└── stop-all.sh             一键停止全部服务
```

## 技术栈

| 层 | 技术 |
|---|------|
| 后端框架 | Spring Boot 3.5 |
| ORM | Spring Data JPA + Hibernate 6 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7（生产）+ Caffeine（开发） |
| 认证 | Spring Security + JWT (jjwt 0.12) |
| 前端 | Vue 3 Composition API + Vite 6 |
| 状态管理 | Pinia 2 |
| 路由 | Vue Router 4 |
| 动画 | GSAP |
| 图表 | ECharts 5 |
| AI 风控 | Python Flask + Scikit-learn + DeepSeek API |
| 服务注册 | Spring Cloud Netflix Eureka |
| 部署 | Docker Compose + Nginx |

## 快速开始

### 前置条件

- JDK 17+
- Node.js 22+
- Maven 3.9+
- MySQL 8.0（或使用 Docker）
- Python 3.11+（仅风控引擎需要）

### 1. 克隆项目

```bash
git clone git@github.com:chidaobuchidao/FineFlo.git
cd FineFlo
cp .env.example .env
```

### 2. 配置环境

编辑 `.env`，填入数据库密码和 DeepSeek API Key：

```bash
DB_PASSWORD=你的数据库密码
DEEPSEEK_API_KEY=你的DeepSeek-API-Key   # 可选，不填则使用规则引擎
```

### 3. 启动数据库（二选一）

**方式 A — Docker（推荐）**
```bash
docker-compose -f docker/docker-compose.yml up -d mysql
```

**方式 B — 本地 MySQL**
```bash
# 确保 MySQL 已运行，然后初始化数据库
mysql -u root -p < docker/mysql/init.sql
```

### 4. 启动服务

**一键全部启动**
```bash
bash start-all.sh
```

**分步启动**
```bash
bash scripts/start-backend.sh       # 后端 → http://localhost:8081
bash scripts/start-frontend.sh      # 前端 → http://localhost:5173
bash scripts/start-risk-engine.sh   # 风控 → http://localhost:5000
```

### 5. 访问

| 入口 | 地址 |
|------|------|
| 审批端管理后台 (Vue) | http://localhost:5173 |
| 企业端门户 (Thymeleaf) | http://localhost:8081/enterprise/login |
| 后端 API 健康检查 | http://localhost:8081/actuator/health |
| 风控引擎健康检查 | http://localhost:5000/health |
| Eureka 控制台 | http://localhost:8761 |

### 6. 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| `admin` | `123456` | 管理员/审批员 |
| `enterprise01` | `123456` | 企业用户（星辉科技） |
| `approver01` | `123456` | 审批员 |

## API 接口

### 认证
| Method | Path | 说明 |
|--------|------|------|
| POST | `/api/auth/login` | 登录，返回 JWT Token |
| POST | `/api/auth/register` | 用户注册 |

### 企业端（需 ENTERPRISE 角色）
| Method | Path | 说明 |
|--------|------|------|
| GET | `/api/enterprise/loans` | 贷款列表 |
| POST | `/api/enterprise/loans` | 贷款申请（自动调用风控评分） |
| GET | `/api/enterprise/loans/{id}` | 贷款详情 |
| POST | `/api/enterprise/loans/{id}/sign` | 电子签约 |
| GET | `/api/enterprise/repayments` | 还款计划 |
| POST | `/api/enterprise/repayments/{id}/pay` | 单期还款 |
| GET | `/api/enterprise/calculator` | 贷款计算器 |

### 审批端（需 APPROVER 角色）
| Method | Path | 说明 |
|--------|------|------|
| GET | `/api/approval/enterprises` | 企业管理 |
| GET | `/api/approval/enterprises/{id}` | 企业详情 |
| GET | `/api/approval/loans` | 审批列表 |
| PUT | `/api/approval/loans/{id}/approve` | 贷款审批 |
| GET | `/api/approval/disbursements` | 放款管理 |
| PUT | `/api/approval/disbursements/{loanId}/grant` | 确认放款 |
| GET | `/api/approval/overdues` | 逾期管理 |
| GET | `/api/approval/risk-query` | 风控查询 |
| POST | `/api/approval/risk-evaluate` | 触发风控评估 |

### 统计
| Method | Path | 说明 |
|--------|------|------|
| GET | `/api/statistics/loan-overview` | 贷款总览 |
| GET | `/api/statistics/disbursement-trend` | 放款趋势 |
| GET | `/api/statistics/overdue-analysis` | 逾期分析 |

### 风控引擎（内部调用）
| Method | Path | 说明 |
|--------|------|------|
| POST | `/api/risk/predict` | 信用评分预测 |
| POST | `/api/risk/evaluate` | 批量评估 |

## 系统架构

```
                         Nginx :80
                      静态资源 + 反向代理
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
   Loan-UI :5173       Loan-API :8081      Risk-Engine :5000
   (Vue 3 SPA)     (SpringBoot REST)       (Flask + ML)
        │                   │                   │
        │                   ├─ MySQL :3306      ├─ DeepSeek API
        │                   ├─ Redis :6379      └─ credit_model.pkl
        │                   └─ Eureka :8761
        │
        └──── 审批端管理界面，JWT Auth，Axios 拦截
```

## Docker Compose 部署

```bash
# 配置环境变量
cp .env.example .env
# 编辑 .env 填入真实密码和 API Key

# 启动全部服务
cd docker
docker-compose up -d

# 查看运行状态
docker-compose ps

# 停止
docker-compose down
```

服务启动顺序：MySQL → Redis → Eureka → Loan-API / Risk-Engine → Nginx

## 开发说明

- **本地开发**使用 `dev` profile，自动关闭 Redis 和 Eureka 依赖，使用 Caffeine 本地缓存
- **风控引擎**支持三保险：本地逻辑回归模型 → 规则引擎兜底 → DeepSeek LLM 增强分析
- **前端**使用 Vite 代理转发 API 请求到 `localhost:8081`，解决跨域问题
- **认证**所有 API（除 `/api/auth/*`）需携带 `Authorization: Bearer <token>` 请求头

## 许可证

MIT License
