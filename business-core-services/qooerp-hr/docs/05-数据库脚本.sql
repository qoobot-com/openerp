-- ============================================================================
-- PostgreSQL 数据库初始化脚本
-- ============================================================================

-- 创建更新时间触发器函数
CREATE OR REPLACE FUNCTION update_modified_time()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

\c qooerp_hr

-- QooERP 人力资源管理 - 数据库初始化脚本
-- 版本：v1.0
-- 创建日期：20xx-xx-xx
-- 数据库：qooerp_hr

-- ============================================
-- 1. 创建数据库
-- ============================================
CREATE DATABASE qooerp_hr WITH ENCODING 'UTF8';



-- ============================================
-- 2. 创建员工表
-- ============================================
DROP TABLE IF EXISTS hr_employee;

CREATE TABLE hr_employee (
    id BIGSERIAL NOT NULL COMMENT '主键ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    employee_code VARCHAR(32) NOT NULL COMMENT '员工编号',
    user_id BIGINT DEFAULT NULL COMMENT '用户ID',
    employee_name VARCHAR(64) NOT NULL COMMENT '员工姓名',
    gender CHAR(1) NOT NULL COMMENT '性别',
    birth_date DATE DEFAULT NULL COMMENT '出生日期',
    phone_number VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    id_card VARCHAR(32) DEFAULT NULL COMMENT '身份证号',
    organization_id BIGINT DEFAULT NULL COMMENT '组织ID',
    department_id BIGINT DEFAULT NULL COMMENT '部门ID',
    position_id BIGINT DEFAULT NULL COMMENT '岗位ID',
    hire_date DATE DEFAULT NULL COMMENT '入职日期',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '员工状态',
    employee_type VARCHAR(20) NOT NULL DEFAULT 'FULLTIME' COMMENT '员工类型',
    work_number VARCHAR(32) DEFAULT NULL COMMENT '工号',
    education VARCHAR(20) DEFAULT NULL COMMENT '学历',
    marital_status VARCHAR(20) DEFAULT NULL COMMENT '婚姻状况',
    political_status VARCHAR(20) DEFAULT NULL COMMENT '政治面貌',
    registered_address VARCHAR(256) DEFAULT NULL COMMENT '户籍地址',
    current_address VARCHAR(256) DEFAULT NULL COMMENT '现住址',
    emergency_contact VARCHAR(64) DEFAULT NULL COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) DEFAULT NULL COMMENT '紧急联系电话',
    remark VARCHAR(512) DEFAULT NULL COMMENT '备注',
    extend_info TEXT DEFAULT NULL COMMENT '扩展信息JSON',
    created_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    version INTEGER NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_employee_code (tenant_id, employee_code),
    INDEX idx_user_id (user_id),
    INDEX idx_department_id (department_id),
    INDEX idx_position_id (position_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- ============================================
-- 3. 创建员工教育经历表
-- ============================================
DROP TABLE IF EXISTS hr_employee_education;

CREATE TABLE hr_employee_education (
    id BIGSERIAL NOT NULL COMMENT '主键ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    school_name VARCHAR(128) NOT NULL COMMENT '学校名称',
    education VARCHAR(20) DEFAULT NULL COMMENT '学历',
    degree VARCHAR(64) DEFAULT NULL COMMENT '学位',
    major VARCHAR(128) DEFAULT NULL COMMENT '专业',
    start_date DATE DEFAULT NULL COMMENT '入学时间',
    end_date DATE DEFAULT NULL COMMENT '毕业时间',
    is_full_time SMALLINT NOT NULL DEFAULT 1 COMMENT '是否全日制',
    created_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- ============================================
-- 4. 创建员工工作经历表
-- ============================================
DROP TABLE IF EXISTS hr_employee_experience;

CREATE TABLE hr_employee_experience (
    id BIGSERIAL NOT NULL COMMENT '主键ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    company_name VARCHAR(128) NOT NULL COMMENT '公司名称',
    department VARCHAR(64) DEFAULT NULL COMMENT '部门',
    position VARCHAR(64) DEFAULT NULL COMMENT '岗位',
    start_date DATE DEFAULT NULL COMMENT '入职时间',
    end_date DATE DEFAULT NULL COMMENT '离职时间',
    leave_reason VARCHAR(256) DEFAULT NULL COMMENT '离职原因',
    job_description TEXT DEFAULT NULL COMMENT '工作内容',
    created_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- ============================================
-- 5. 创建考勤记录表
-- ============================================
DROP TABLE IF EXISTS hr_attendance;

CREATE TABLE hr_attendance (
    id BIGSERIAL NOT NULL COMMENT '主键ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    check_in_type VARCHAR(20) NOT NULL COMMENT '打卡类型',
    check_time TIMESTAMP NOT NULL COMMENT '打卡时间',
    location VARCHAR(256) DEFAULT NULL COMMENT '打卡地点',
    check_method VARCHAR(20) DEFAULT NULL COMMENT '打卡方式',
    device_id VARCHAR(64) DEFAULT NULL COMMENT '设备ID',
    device_ip VARCHAR(64) DEFAULT NULL COMMENT '设备IP',
    remark VARCHAR(512) DEFAULT NULL COMMENT '备注',
    created_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    INDEX idx_employee_date (employee_id, attendance_date),
    INDEX idx_check_time (check_time),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- ============================================
-- 6. 创建请假记录表
-- ============================================
DROP TABLE IF EXISTS hr_leave;

CREATE TABLE hr_leave (
    id BIGSERIAL NOT NULL COMMENT '主键ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    leave_code VARCHAR(32) NOT NULL COMMENT '请假编号',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    leave_type VARCHAR(20) NOT NULL COMMENT '请假类型',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    leave_days DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '请假天数',
    reason VARCHAR(512) DEFAULT NULL COMMENT '请假原因',
    approver_id BIGINT DEFAULT NULL COMMENT '审批人ID',
    approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审批状态',
    approval_time TIMESTAMP DEFAULT NULL COMMENT '审批时间',
    approval_comment VARCHAR(512) DEFAULT NULL COMMENT '审批意见',
    attachment_id BIGINT DEFAULT NULL COMMENT '附件ID',
    created_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    version INTEGER NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_leave_code (tenant_id, leave_code),
    INDEX idx_employee_id (employee_id),
    INDEX idx_approver_id (approver_id),
    INDEX idx_approval_status (approval_status),
    INDEX idx_date_range (start_date, end_date),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- ============================================
-- 7. 创建加班记录表
-- ============================================
DROP TABLE IF EXISTS hr_overtime;

CREATE TABLE hr_overtime (
    id BIGSERIAL NOT NULL COMMENT '主键ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    overtime_code VARCHAR(32) NOT NULL COMMENT '加班编号',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    overtime_type VARCHAR(20) NOT NULL COMMENT '加班类型',
    overtime_date DATE NOT NULL COMMENT '加班日期',
    start_time TIMESTAMP NOT NULL COMMENT '开始时间',
    end_time TIMESTAMP NOT NULL COMMENT '结束时间',
    overtime_hours DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '加班时长（小时）',
    reason VARCHAR(512) DEFAULT NULL COMMENT '加班原因',
    approver_id BIGINT DEFAULT NULL COMMENT '审批人ID',
    approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审批状态',
    approval_time TIMESTAMP DEFAULT NULL COMMENT '审批时间',
    approval_comment VARCHAR(512) DEFAULT NULL COMMENT '审批意见',
    created_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    version INTEGER NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_overtime_code (tenant_id, overtime_code),
    INDEX idx_employee_id (employee_id),
    INDEX idx_overtime_date (overtime_date),
    INDEX idx_approver_id (approver_id),
    INDEX idx_approval_status (approval_status),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- ============================================
-- 8. 创建出差记录表
-- ============================================
DROP TABLE IF EXISTS hr_business_trip;

CREATE TABLE hr_business_trip (
    id BIGSERIAL NOT NULL COMMENT '主键ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    trip_code VARCHAR(32) NOT NULL COMMENT '出差编号',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    destination VARCHAR(256) NOT NULL COMMENT '出差地点',
    purpose VARCHAR(512) DEFAULT NULL COMMENT '出差目的',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    trip_days DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '出差天数',
    budget_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '预算金额',
    actual_amount DECIMAL(18,2) DEFAULT 0.00 COMMENT '实际金额',
    approver_id BIGINT DEFAULT NULL COMMENT '审批人ID',
    approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审批状态',
    approval_time TIMESTAMP DEFAULT NULL COMMENT '审批时间',
    approval_comment VARCHAR(512) DEFAULT NULL COMMENT '审批意见',
    created_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    version INTEGER NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_trip_code (tenant_id, trip_code),
    INDEX idx_employee_id (employee_id),
    INDEX idx_date_range (start_date, end_date),
    INDEX idx_approver_id (approver_id),
    INDEX idx_approval_status (approval_status),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- ============================================
-- 9. 创建薪资记录表
-- ============================================
DROP TABLE IF EXISTS hr_salary;

CREATE TABLE hr_salary (
    id BIGSERIAL NOT NULL COMMENT '主键ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    salary_code VARCHAR(32) NOT NULL COMMENT '薪资编号',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    salary_month VARCHAR(7) NOT NULL COMMENT '薪资月份',
    base_salary DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '基本工资',
    position_salary DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '岗位工资',
    performance_salary DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '绩效工资',
    overtime_pay DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '加班费',
    bonus DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '奖金',
    allowance DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '补贴',
    gross_salary DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '应发工资',
    leave_deduction DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '请假扣款',
    social_insurance_personal DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '社保个人',
    housing_fund_personal DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '公积金个人',
    income_tax DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '个税',
    other_deduction DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '其他扣款',
    net_salary DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '实发工资',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '发放状态',
    payment_date DATE DEFAULT NULL COMMENT '发放日期',
    remark VARCHAR(512) DEFAULT NULL COMMENT '备注',
    created_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    version INTEGER NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_salary_code (tenant_id, salary_code),
    INDEX idx_employee_month (employee_id, salary_month),
    INDEX idx_salary_month (salary_month),
    INDEX idx_payment_status (payment_status),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- ============================================
-- 10. 创建薪资项目表
-- ============================================
DROP TABLE IF EXISTS hr_salary_item;

CREATE TABLE hr_salary_item (
    id BIGSERIAL NOT NULL COMMENT '主键ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    item_code VARCHAR(32) NOT NULL COMMENT '项目编码',
    item_name VARCHAR(64) NOT NULL COMMENT '项目名称',
    item_type VARCHAR(20) NOT NULL COMMENT '项目类型',
    item_category VARCHAR(20) NOT NULL COMMENT '项目分类',
    calculation_method VARCHAR(20) NOT NULL DEFAULT 'FIXED' COMMENT '计算方式',
    formula TEXT DEFAULT NULL COMMENT '计算公式',
    default_value DECIMAL(18,2) DEFAULT 0.00 COMMENT '默认值',
    is_enabled SMALLINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    sort_order INTEGER NOT NULL DEFAULT 0 COMMENT '排序号',
    remark VARCHAR(512) DEFAULT NULL COMMENT '备注',
    created_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_item_code (tenant_id, item_code),
    INDEX idx_item_type (item_type),
    INDEX idx_item_category (item_category),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- ============================================
-- 11. 创建绩效考核计划表
-- ============================================
DROP TABLE IF EXISTS hr_performance_plan;

CREATE TABLE hr_performance_plan (
    id BIGSERIAL NOT NULL COMMENT '主键ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    plan_code VARCHAR(32) NOT NULL COMMENT '计划编号',
    plan_name VARCHAR(128) NOT NULL COMMENT '计划名称',
    period_type VARCHAR(20) NOT NULL COMMENT '考核周期',
    year INTEGER NOT NULL COMMENT '考核年份',
    quarter INTEGER DEFAULT NULL COMMENT '考核季度',
    month INTEGER DEFAULT NULL COMMENT '考核月份',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    self_assessment_start DATE DEFAULT NULL COMMENT '自评开始日期',
    self_assessment_end DATE DEFAULT NULL COMMENT '自评结束日期',
    supervisor_assessment_start DATE DEFAULT NULL COMMENT '上评开始日期',
    supervisor_assessment_end DATE DEFAULT NULL COMMENT '上评结束日期',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '计划状态',
    remark VARCHAR(512) DEFAULT NULL COMMENT '备注',
    created_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    version INTEGER NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_plan_code (tenant_id, plan_code),
    INDEX idx_period (year, quarter, month),
    INDEX idx_date_range (start_date, end_date),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- ============================================
-- 12. 创建绩效评估表
-- ============================================
DROP TABLE IF EXISTS hr_performance_assessment;

CREATE TABLE hr_performance_assessment (
    id BIGSERIAL NOT NULL COMMENT '主键ID',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    assessment_code VARCHAR(32) NOT NULL COMMENT '评估编号',
    plan_id BIGINT NOT NULL COMMENT '计划ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    assessor_id BIGINT NOT NULL COMMENT '评估人ID',
    assessment_type VARCHAR(20) NOT NULL COMMENT '评估类型',
    self_score DECIMAL(5,2) DEFAULT NULL COMMENT '自评分',
    supervisor_score DECIMAL(5,2) DEFAULT NULL COMMENT '上评分',
    peer_score DECIMAL(5,2) DEFAULT NULL COMMENT '同事评分',
    subordinate_score DECIMAL(5,2) DEFAULT NULL COMMENT '下属评分',
    total_score DECIMAL(5,2) DEFAULT NULL COMMENT '综合得分',
    grade CHAR(1) DEFAULT NULL COMMENT '绩效等级',
    comment TEXT DEFAULT NULL COMMENT '评估意见',
    improvement_suggestion TEXT DEFAULT NULL COMMENT '改进建议',
    assessment_date DATE DEFAULT NULL COMMENT '评估日期',
    approver_id BIGINT DEFAULT NULL COMMENT '审批人ID',
    approval_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审批状态',
    approval_time TIMESTAMP DEFAULT NULL COMMENT '审批时间',
    created_by BIGINT DEFAULT NULL COMMENT '创建人ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT DEFAULT NULL COMMENT '更新人ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  COMMENT '更新时间',
    deleted SMALLINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    version INTEGER NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_assessment_code (tenant_id, assessment_code),
    INDEX idx_plan_employee (plan_id, employee_id),
    INDEX idx_assessor_id (assessor_id),
    INDEX idx_grade (grade),
    INDEX idx_approval_status (approval_status),
    INDEX idx_deleted (deleted),
    INDEX idx_tenant_id (tenant_id)
);

-- ============================================
-- 初始化数据
-- ============================================

-- 插入薪资项目默认数据
INSERT INTO hr_salary_item (tenant_id, item_code, item_name, item_type, item_category, calculation_method, default_value, is_enabled, sort_order) VALUES
(0, 'BASE_SALARY', '基本工资', 'ADD', 'BASE', 'FIXED', 0.00, 1, 1),
(0, 'POSITION_SALARY', '岗位工资', 'ADD', 'POSITION', 'FIXED', 0.00, 1, 2),
(0, 'PERFORMANCE_SALARY', '绩效工资', 'ADD', 'PERFORMANCE', 'FORMULA', 0.00, 1, 3),
(0, 'OVERTIME_PAY', '加班费', 'ADD', 'OVERTIME', 'FORMULA', 0.00, 1, 4),
(0, 'BONUS', '奖金', 'ADD', 'BONUS', 'FIXED', 0.00, 1, 5),
(0, 'ALLOWANCE', '补贴', 'ADD', 'ALLOWANCE', 'FIXED', 0.00, 1, 6),
(0, 'LEAVE_DEDUCTION', '请假扣款', 'DEDUCT', 'LEAVE', 'FORMULA', 0.00, 1, 1),
(0, 'SOCIAL_INSURANCE', '社保个人', 'DEDUCT', 'TAX', 'FORMULA', 0.00, 1, 2),
(0, 'HOUSING_FUND', '公积金个人', 'DEDUCT', 'TAX', 'FORMULA', 0.00, 1, 3),
(0, 'INCOME_TAX', '个税', 'DEDUCT', 'TAX', 'FORMULA', 0.00, 1, 4);

-- ============================================
-- 说明
-- ============================================
-- 本脚本创建了人力资源管理模块的所有数据表
-- 包含：员工管理、考勤管理、薪资管理、绩效管理
-- 执行此脚本后，将完成数据库结构的初始化
