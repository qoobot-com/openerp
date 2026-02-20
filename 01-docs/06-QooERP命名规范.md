# QooERP 命名规范

> 版本：v2.0
> 创建日期：20xx-xx-xx
> 更新日期：20xx-xx-xx

---

## 一、概述

### 1.1 命名原则

QooERP 采用**模块显式命名规范**，类名和表名都加上模块前缀，明确标识归属：

- **包隔离**：每个模块独立的包路径 `com.qoobot.{模块名}`
- **库隔离**：每个模块独立的数据库（如 `qooerp_auth`）
- **表前缀**：数据库表使用模块名小写作为前缀
- **类命名**：类名加上模块前缀（如 `AuthUser` 而不是 `User`）

### 1.2 核心目标

- ✅ **零冲突**：独立的数据库、包路径和显式类名，完全隔离
- ✅ **清晰归属**：类名和表名都明确标识所属模块
- ✅ **易于维护**：统一的命名规范，避免歧义
- ✅ **团队协作**：多团队并行开发，类名不冲突

---

## 二、模块与数据库映射表

| 服务名称 | 数据库名 | 包路径 | 模块前缀 | 示例类名 | 示例表名 |
|---------|---------|--------|----------|---------|---------|
| qooerp-auth | qooerp_auth | com.qoobot.qooerp.auth | auth | AuthUser, AuthLoginLog | auth_user, auth_login_log |
| qooerp-gateway | qooerp_gateway | com.qoobot.qooerp.gateway | gateway | GatewayRoute, GatewayFilter | gateway_route, gateway_filter |
| qooerp-system | qooerp_system | com.qoobot.qooerp.system | system | SystemUser, SystemRole | system_user, system_role |
| qooerp-finance | qooerp_finance | com.qoobot.qooerp.finance | finance | FinanceAccount, FinanceInvoice | finance_account, finance_invoice |
| qooerp-hr | qooerp_hr | com.qoobot.qooerp.hr | hr | HrEmployee, HrPosition | hr_employee, hr_position |
| qooerp-inventory | qooerp_inventory | com.qoobot.qooerp.inventory | inventory | InventoryWarehouse, InventoryStock | inventory_warehouse, inventory_stock |
| qooerp-sales | qooerp_sales | com.qoobot.qooerp.sales | sales | SalesOrder, SalesCustomer | sales_order, sales_customer |
| qooerp-scm | qooerp_scm | com.qoobot.qooerp.scm | scm | ScmSupplier, ScmPurchase | scm_supplier, scm_purchase |
| qooerp-production | qooerp_production | com.qoobot.qooerp.production | production | ProductionPlan, ProductionOrder | production_plan, production_order |
| qooerp-crm | qooerp_crm | com.qoobot.qooerp.crm | crm | CrmLead, CrmOpportunity | crm_lead, crm_opportunity |
| qooerp-project | qooerp_project | com.qoobot.qooerp.project | project | ProjectTask, ProjectMilestone | project_task, project_milestone |
| qooerp-workflow | qooerp_workflow | com.qoobot.qooerp.workflow | workflow | WorkflowProcess, WorkflowTask | workflow_process, workflow_task |
| qooerp-document | qooerp_document | com.qoobot.qooerp.document | document | DocumentFile, DocumentFolder | document_file, document_folder |
| qooerp-notification | qooerp_notification | com.qoobot.qooerp.notify | notify | NotifyMessage, NotifyTemplate | notify_message, notify_template |
| qooerp-scheduler | qooerp_scheduler | com.qoobot.qooerp.scheduler | scheduler | SchedulerJob, SchedulerTrigger | scheduler_job, scheduler_trigger |
| qooerp-report | qooerp_report | com.qoobot.qooerp.report | report | ReportTemplate, ReportData | report_template, report_data |
| qooerp-file | qooerp_file | com.qoobot.qooerp.file | file | FileUpload, FileDownload | file_upload, file_download |
| qooerp-search | qooerp_search | com.qoobot.qooerp.search | search | SearchIndex, SearchQuery | search_index, search_query |
| qooerp-monitor | qooerp_monitor | com.qoobot.qooerp.monitor | monitor | MonitorMetric, MonitorAlert | monitor_metric, monitor_alert |
| qooerp-tenant | qooerp_tenant | com.qoobot.qooerp.tenant | tenant | TenantInfo, TenantConfig | tenant_info, tenant_config |
| qooerp-integration | qooerp_integration | com.qoobot.qooerp.integration | integration | IntegrationMapping, IntegrationLog | integration_mapping, integration_log |
| qooerp-message | qooerp_message | com.qoobot.qooerp.message | message | MessageQueue, MessageTopic | message_queue, message_topic |

---

## 三、Java类命名规范

### 3.1 实体类（Entity）

**格式：{模块前缀}{业务名}（驼峰命名）**

```java
// 认证模块 (com.qoobot.qooerp.auth.entity)
package com.qoobot.qooerp.auth.entity;

@TableName("auth_user")
public class AuthUser {
    private Long id;
    private String username;
    private String password;
    // ...
}

@TableName("auth_login_log")
public class AuthLoginLog {
    private Long id;
    private Long userId;
    private String loginIp;
    // ...
}

// 财务模块 (com.qoobot.qooerp.finance.entity)
package com.qoobot.qooerp.finance.entity;

@TableName("finance_account")
public class FinanceAccount {
    private Long id;
    private String accountCode;
    private String accountName;
    private BigDecimal balance;
    // ...
}

@TableName("finance_invoice")
public class FinanceInvoice {
    private Long id;
    private String invoiceNo;
    private BigDecimal amount;
    // ...
}

// 人力资源模块 (com.qoobot.qooerp.hr.entity)
package com.qoobot.qooerp.hr.entity;

@TableName("hr_employee")
public class HrEmployee {
    private Long id;
    private String empNo;
    private String empName;
    // ...
}

@TableName("hr_position")
public class HrPosition {
    private Long id;
    private String positionCode;
    private String positionName;
    // ...
}
```

### 3.2 Service接口

**格式：{模块前缀}{业务名}Service**

```java
package com.qoobot.qooerp.auth.service;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String token);
}

package com.qoobot.qooerp.finance.service;

public interface FinanceAccountService {
    FinanceAccount getById(Long id);
    void save(FinanceAccount account);
}

package com.qoobot.qooerp.hr.service;

public interface HrEmployeeService {
    HrEmployee getById(Long id);
    List<HrEmployee> listAll();
}
```

### 3.3 Service实现类

**格式：{模块前缀}{业务名}ServiceImpl**

```java
package com.qoobot.qooerp.auth.service.impl;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public LoginResponse login(LoginRequest request) {
        // 实现逻辑
    }
}

package com.qoobot.qooerp.finance.service.impl;

@Service
public class FinanceAccountServiceImpl implements FinanceAccountService {
    @Override
    public FinanceAccount getById(Long id) {
        return financeAccountMapper.selectById(id);
    }
}
```

### 3.4 Controller

**格式：{业务名}Controller**

```java
package com.qoobot.qooerp.auth.controller;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }
}

package com.qoobot.qooerp.finance.controller;

@RestController
@RequestMapping("/api/finance/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/{id}")
    public Result<Account> getById(@PathVariable Long id) {
        return Result.success(accountService.getById(id));
    }
}

package com.qoobot.qooerp.hr.controller;

@RestController
@RequestMapping("/api/hr/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        return Result.success(employeeService.getById(id));
    }
}
```

### 3.5 DTO（数据传输对象）

**格式：{业务名}Request / {业务名}Response / {模块前缀}{业务名}DTO**

```java
// Request
package com.qoobot.qooerp.auth.dto;

public class LoginRequest {
    private String username;
    private String password;
    // ...
}

// Response
package com.qoobot.qooerp.auth.dto;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    // ...
}

// DTO
package com.qoobot.qooerp.finance.dto;

public class FinanceAccountDTO {
    private Long id;
    private String accountCode;
    private String accountName;
    // ...
}
```

### 3.6 VO（视图对象）

**格式：{业务名}VO**

```java
package com.qoobot.qooerp.auth.vo;

public class UserInfoVO {
    private Long userId;
    private String username;
    private String realName;
    private List<String> roles;
    // ...
}

package com.qoobot.qooerp.finance.vo;

public class AccountVO {
    private Long id;
    private String accountCode;
    private String accountName;
    private BigDecimal balance;
    // ...
}
```

### 3.7 Mapper/Repository

**格式：{模块前缀}{业务名}Mapper**

```java
package com.qoobot.qooerp.auth.mapper;

@Mapper
public interface AuthUserMapper extends BaseMapper<AuthUser> {
    // 自定义查询方法
}

package com.qoobot.qooerp.finance.mapper;

@Mapper
public interface FinanceAccountMapper extends BaseMapper<FinanceAccount> {
    // 自定义查询方法
}

package com.qoobot.qooerp.hr.mapper;

@Mapper
public interface HrEmployeeMapper extends BaseMapper<HrEmployee> {
    // 自定义查询方法
}
```

---

## 四、数据库表命名规范

### 4.1 表名规范

**格式：{模块名小写}_{业务名小写_下划线}**

| 模块 | 表前缀 | 示例表名 |
|------|--------|---------|
| 认证服务 | auth | auth_user, auth_login_log |
| 系统管理 | system | system_user, system_role, system_menu |
| 网关服务 | gateway | gateway_route, gateway_filter |
| 财务管理 | finance | finance_account, finance_invoice, finance_voucher |
| 人力资源 | hr | hr_employee, hr_position, hr_department |
| 库存管理 | inventory | inventory_warehouse, inventory_stock, inventory_product |
| 销售管理 | sales | sales_order, sales_customer, sales_contract |
| 供应链 | scm | scm_supplier, scm_purchase, scm_contract |
| 生产管理 | production | production_plan, production_order, production_bom |
| 客户关系 | crm | crm_lead, crm_opportunity, crm_customer |
| 项目管理 | project | project_task, project_milestone, project_gantt |
| 工作流 | workflow | workflow_process, workflow_task, workflow_definition |
| 文档管理 | document | document_file, document_folder, document_permission |
| 通知服务 | notify | notify_message, notify_template |
| 调度服务 | scheduler | scheduler_job, scheduler_trigger |
| 报表服务 | report | report_template, report_data |
| 文件服务 | file | file_upload, file_download |

### 4.2 建表SQL示例

```sql
-- 认证服务数据库: qooerp_auth
CREATE TABLE auth_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    status SMALLINT NOT NULL DEFAULT 1,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uk_auth_username ON auth_user(username) WHERE deleted = 0;

-- 财务服务数据库: qooerp_finance
CREATE TABLE finance_account (
    id BIGSERIAL PRIMARY KEY,
    account_code VARCHAR(50) NOT NULL,
    account_name VARCHAR(200) NOT NULL,
    balance DECIMAL(20,2) DEFAULT 0,
    status SMALLINT NOT NULL DEFAULT 1,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uk_finance_account_code ON finance_account(account_code) WHERE deleted = 0;

-- 人力资源数据库: qooerp_hr
CREATE TABLE hr_employee (
    id BIGSERIAL PRIMARY KEY,
    emp_no VARCHAR(20) NOT NULL,
    emp_name VARCHAR(100) NOT NULL,
    dept_id BIGINT,
    position_id BIGINT,
    status SMALLINT NOT NULL DEFAULT 1,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uk_hr_emp_no ON hr_employee(emp_no) WHERE deleted = 0;
```

### 4.3 字段命名规范

| 类型 | 命名格式 | 示例 |
|------|---------|------|
| 主键 | id | id |
| 编码 | {业务名}_code | account_code, emp_no |
| 名称 | {业务名}_name | account_name, emp_name |
| 外键 | {关联表}_id | dept_id, customer_id |
| 状态 | status | status |
| 创建时间 | create_time | create_time |
| 更新时间 | update_time | update_time |
| 删除标记 | deleted | deleted |
| 租户ID | tenant_id | tenant_id |
| 创建人 | create_by | create_by |
| 更新人 | update_by | update_by |

---

## 五、包结构规范

### 5.1 包路径规范

**格式：com.qoobot.qooerp.{模块名}.{子模块}.{层}**

```
com.qoobot.qooerp.auth.entity.AuthUser
com.qoobot.qooerp.auth.service.AuthService
com.qoobot.qooerp.auth.controller.AuthController
com.qoobot.qooerp.auth.mapper.AuthUserMapper

com.qoobot.qooerp.finance.account.entity.FinanceAccount
com.qoobot.qooerp.finance.account.service.FinanceAccountService
com.qoobot.qooerp.finance.account.controller.FinanceAccountController
com.qoobot.qooerp.finance.account.mapper.FinanceAccountMapper

com.qoobot.qooerp.hr.employee.entity.HrEmployee
com.qoobot.qooerp.hr.employee.service.HrEmployeeService
com.qoobot.qooerp.hr.employee.controller.HrEmployeeController
com.qoobot.qooerp.hr.employee.mapper.HrEmployeeMapper
```

### 5.2 模块包结构

```
qooerp-auth-service/
└── src/main/java/com/qoobot/qooerp/auth/
    ├── controller/
    │   └── AuthController.java
    ├── service/
    │   ├── AuthService.java
    │   └── impl/
    │       └── AuthServiceImpl.java
    ├── mapper/
    │   ├── AuthUserMapper.java
    │   └── AuthLoginLogMapper.java
    ├── entity/
    │   ├── AuthUser.java
    │   └── AuthLoginLog.java
    ├── dto/
    │   ├── LoginRequest.java
    │   └── LoginResponse.java
    ├── vo/
    │   └── AuthUserInfoVO.java
    ├── config/
    │   ├── SecurityConfig.java
    │   └── JwtConfig.java
    ├── util/
    │   └── JwtUtil.java
    └── constants/
        └── AuthConstant.java
```

---

## 六、API路径规范

### 6.1 API路径格式

**格式：/api/{模块}/{业务}**

| 模块 | 示例路径 |
|------|---------|
| 认证服务 | /api/auth/login, /api/auth/logout |
| 财务管理 | /api/finance/account, /api/finance/invoice |
| 人力资源 | /api/hr/employee, /api/hr/position |
| 销售管理 | /api/sales/order, /api/sales/customer |
| 供应链 | /api/scm/supplier, /api/scm/purchase |

### 6.2 API示例

```java
// 认证模块
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/login")           // POST /api/auth/login
    @PostMapping("/logout")          // POST /api/auth/logout
    @PostMapping("/refresh")         // POST /api/auth/refresh
    @GetMapping("/current")          // GET /api/auth/current
}

// 财务模块
@RestController
@RequestMapping("/api/finance/account")
public class FinanceAccountController {
    @GetMapping("/{id}")             // GET /api/finance/account/{id}
    @PostMapping("/")                 // POST /api/finance/account
    @PutMapping("/{id}")             // PUT /api/finance/account/{id}
    @DeleteMapping("/{id}")          // DELETE /api/finance/account/{id}
}

// 人力资源模块
@RestController
@RequestMapping("/api/hr/employee")
public class HrEmployeeController {
    @GetMapping("/{id}")             // GET /api/hr/employee/{id}
    @PostMapping("/")                 // POST /api/hr/employee
    @PutMapping("/{id}")             // PUT /api/hr/employee/{id}
    @DeleteMapping("/{id}")          // DELETE /api/hr/employee/{id}
}
```

---

## 七、配置文件规范

### 7.1 数据源配置

```yaml
# 认证服务 - application.yml
spring:
  application:
    name: qooerp-auth-service
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/qooerp_auth
    username: qooerp
    password: qooerp123

# 财务服务 - application.yml
spring:
  application:
    name: qooerp-finance-service
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/qooerp_finance
    username: qooerp
    password: qooerp123

# 人力资源服务 - application.yml
spring:
  application:
    name: qooerp-hr-service
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/qooerp_hr
    username: qooerp
    password: qooerp123
```

### 7.2 配置前缀

```yaml
# 认证服务配置
auth:
  jwt:
    secret: qooerp-secret-key
    expiration: 7200
  login:
    max-failed-count: 5
    lock-duration: 1800

# 财务服务配置
finance:
  account:
    default-currency: CNY
    default-balance: 0.00
  invoice:
    auto-number: true
    number-prefix: "INV"

# 人力资源服务配置
hr:
  employee:
    default-status: 1
    enable-attendance: true
  position:
    enable-level: true
```

---

## 八、All-In-One部署配置

### 8.1 启动类配置

```java
@SpringBootApplication(
    scanBasePackages = {
        "com.qoobot.qooerp.auth",          // 认证服务
        "com.qoobot.qooerp.gateway",       // 网关服务
        "com.qoobot.qooerp.system",        // 系统管理
        "com.qoobot.qooerp.finance",       // 财务管理
        "com.qoobot.qooerp.hr",            // 人力资源
        "com.qoobot.qooerp.inventory",     // 库存管理
        "com.qoobot.qooerp.sales",         // 销售管理
        "com.qoobot.qooerp.scm",           // 供应链
        "com.qoobot.qooerp.production",    // 生产管理
        "com.qoobot.qooerp.crm",           // 客户关系
        "com.qoobot.qooerp.project",       // 项目管理
        "com.qoobot.qooerp.workflow",      // 工作流
        // ... 其他模块
    }
)
public class QooERPAllApplication {
    public static void main(String[] args) {
        SpringApplication.run(QooERPAllApplication.class, args);
    }
}
```

### 8.2 多数据源配置

```yaml
spring:
  datasource:
    # 主数据源（默认）
    primary:
      driver-class-name: org.postgresql.Driver
      url: jdbc:postgresql://localhost:5432/qooerp_auth
      username: qooerp
      password: qooerp123

    # 财务数据源
    finance:
      driver-class-name: org.postgresql.Driver
      url: jdbc:postgresql://localhost:5432/qooerp_finance
      username: qooerp
      password: qooerp123

    # 人力资源数据源
    hr:
      driver-class-name: org.postgresql.Driver
      url: jdbc:postgresql://localhost:5432/qooerp_hr
      username: qooerp
      password: qooerp123
```

---

## 九、命名规范总结

### 9.1 完整规范表

| 类型 | 命名规则 | 示例 |
|------|---------|------|
| **包名** | com.qoobot.qooerp.{模块名} | com.qoobot.qooerp.auth, com.qoobot.qooerp.finance |
| **类名** | {业务名}{类型} | User, AuthService, UserMapper |
| **表名** | {模块前缀}_{业务名小写_下划线} | sys_user, fin_account, hr_employee |
| **API路径** | /api/{模块}/{业务} | /api/auth/login, /api/finance/account |
| **配置前缀** | {模块}.{业务} | auth.jwt, finance.account |
| **Entity** | {业务名} | User, Account, Employee |
| **Service接口** | {业务名}Service | AuthService, AccountService |
| **Service实现** | {业务名}ServiceImpl | AuthServiceImpl, AccountServiceImpl |
| **Controller** | {业务名}Controller | AuthController, AccountController |
| **DTO Request** | {业务名}Request | LoginRequest, AccountDTO |
| **DTO Response** | {业务名}Response | LoginResponse, AccountVO |
| **VO** | {业务名}VO | UserInfoVO, AccountVO |
| **Mapper** | {业务名}Mapper | UserMapper, AccountMapper |

### 9.2 核心优势

| 优势 | 说明 |
|------|------|
| ✅ **零冲突** | 独立数据库 + 独立包路径，完全隔离 |
| ✅ **简洁明了** | 类名简洁，无需冗余前缀 |
| ✅ **语义清晰** | 包路径和表名清晰标识归属 |
| ✅ **易于维护** | 统一的表前缀和包结构 |
| ✅ **新人友好** | 直观易懂，无需记忆复杂映射 |
| ✅ **IDE友好** | 搜索精准，代码提示准确 |
| ✅ **团队协作** | 多团队并行开发，互不影响 |
| ✅ **All-in-One** | 多数据源支持，聚合部署无冲突 |

---

## 十、最佳实践

### 10.1 完整代码示例

```java
// ========== 认证服务 ==========
package com.qoobot.qooerp.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("auth_user")
public class AuthUser extends BaseEntity {
    private Long id;
    private String username;
    private String password;
    private String realName;
}

package com.qoobot.qooerp.auth.service;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String token);
}

package com.qoobot.qooerp.auth.service.impl;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthUserMapper authUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 实现逻辑
    }
}

package com.qoobot.qooerp.auth.controller;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }
}

// ========== 财务服务 ==========
package com.qoobot.qooerp.finance.account.entity;

@Data
@TableName("finance_account")
public class FinanceAccount extends BaseEntity {
    private Long id;
    private String accountCode;
    private String accountName;
    private BigDecimal balance;
}

package com.qoobot.qooerp.finance.account.service;

public interface FinanceAccountService {
    FinanceAccount getById(Long id);
    void save(FinanceAccount account);
}

package com.qoobot.qooerp.finance.account.controller;

@RestController
@RequestMapping("/api/finance/account")
public class FinanceAccountController {
    @Autowired
    private FinanceAccountService financeAccountService;

    @GetMapping("/{id}")
    public Result<FinanceAccount> getById(@PathVariable Long id) {
        return Result.success(financeAccountService.getById(id));
    }
}
```

### 10.2 数据库迁移示例

```sql
-- V1.0.0__init_schema.sql (qooerp_auth数据库)
CREATE TABLE auth_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50),
    status SMALLINT NOT NULL DEFAULT 1,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uk_auth_username ON auth_user(username) WHERE deleted = 0;

COMMENT ON TABLE auth_user IS '用户表';
COMMENT ON COLUMN auth_user.username IS '用户名';
COMMENT ON COLUMN auth_user.password IS '密码（BCrypt加密）';
```

---

## 十一、变更记录

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|---------|--------|
| v2.0 | 20xx-xx-xx | 更新命名规范，与实际代码保持一致 | - |
| v1.0 | 20xx-xx-xx | 初始版本 | - |

---

*文档维护：请严格遵循本规范进行开发，确保代码一致性和可维护性*
