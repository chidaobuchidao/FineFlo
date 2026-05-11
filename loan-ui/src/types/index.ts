// ==================== API Response ====================
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
  timestamp?: number
}

export interface PaginatedData<T> {
  total: number
  page: number
  size: number
  records: T[]
}

// ==================== Auth ====================
export interface User {
  id: number
  username: string
  realName: string
  role: 'ENTERPRISE' | 'APPROVER' | 'ADMIN'
  enterpriseId?: number
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  expiresIn: number
  user: User
}

export interface RegisterRequest {
  username: string
  password: string
  realName: string
  phone: string
  enterpriseId?: number
}

// ==================== Enterprise ====================
export interface Enterprise {
  id: number
  name: string
  creditCode: string
  legalPerson: string
  legalIdCard: string
  contactPhone: string
  address: string
  industry: string
  registeredCapital: number
  establishDate: string
  employeeCount: number
  annualRevenue: number
  status: number
  createdAt: string
  updatedAt: string
}

// ==================== Loan ====================
export type LoanStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'GRANTED' | 'REPAID' | 'OVERDUE'

export interface LoanApplication {
  id: number
  enterpriseId: number
  userId: number
  loanAmount: number
  loanTerm: number
  loanPurpose: string
  interestRate: number | null
  repaymentMethod: string
  status: LoanStatus
  applyDate: string
  approveDate: string | null
  approveUserId: number | null
  approveComment: string | null
  creditScore: number | null
  enterpriseName?: string
}

export interface LoanApplyRequest {
  loanAmount: number
  loanTerm: number
  loanPurpose: string
  repaymentMethod: string
}

export interface LoanApproveRequest {
  action: 'APPROVE' | 'REJECT'
  comment: string
}

// ==================== Repayment ====================
export type RepaymentStatus = 'UNPAID' | 'PAID' | 'OVERDUE'

export interface Repayment {
  id: number
  loanId: number
  periodNo: number
  amount: number
  paidAmount: number | null
  dueDate: string
  paidDate: string | null
  status: RepaymentStatus
}

// ==================== Overdue ====================
export interface Overdue {
  id: number
  loanId: number
  enterpriseId: number
  overdueDays: number
  overdueAmount: number
  penalty: number
  startDate: string
  endDate: string | null
  status: 'ACTIVE' | 'SETTLED'
  enterpriseName?: string
}

// ==================== Disbursement ====================
export interface Disbursement {
  id: number
  enterpriseId: number
  enterpriseName: string
  loanAmount: number
  loanTerm: number
  interestRate: number
  approveDate: string
  status: string
}

// ==================== Credit Score ====================
export interface CreditScore {
  id: number
  enterpriseId: number
  score: number
  modelVersion: string
  features: string
  evaluatedAt: string
}

export interface RiskEvaluation {
  enterpriseId: number
  creditScore: number
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH'
  confidence: number
  modelVersion: string
}

export interface RiskEvaluateRequest {
  enterpriseId: number
}

// ==================== Statistics ====================
export interface LoanOverview {
  totalApply: number
  totalApproved: number
  totalDisbursed: number
  totalOverdue: number
  approvalRate: number
  monthly: MonthlyStats[]
}

export interface MonthlyStats {
  month: string
  apply: number
  approved: number
  disbursed: number
}

export interface TrendItem {
  month: string
  value: number
}

export interface OverdueAnalysis {
  byIndustry: { name: string; count: number }[]
  byAmount: { range: string; count: number }[]
  byTerm: { range: string; count: number }[]
}

// ==================== Loan Calculator ====================
export interface CalculatorInput {
  amount: number
  term: number
  rate: number
}

export interface CalculatorResult {
  mode: string
  monthlyPayment: number
  totalInterest: number
  totalPayment: number
  schedule: CalculatorScheduleItem[]
}

export interface CalculatorScheduleItem {
  period: number
  principal: number
  interest: number
  remaining: number
}

// ==================== Navigation ====================
export interface NavItem {
  path: string
  label: string
  icon: string
}
