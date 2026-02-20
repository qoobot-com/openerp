# qooerp-user 用户管理 - DDD领域驱动设计文档

> 模块版本：2.0.0-REDESIGN
> 设计日期：2026-02-18
> 文档作者：QooERP团队
> 设计原则：DDD领域驱动设计

---

## 一、领域模型概述

### 1.1 限界上下文（Bounded Context）

**用户上下文（User Context）** 是QooERP系统的核心上下文之一，负责管理用户全生命周期管理，与以下上下文协作：

```
┌─────────────────────────────────────────────────────────────┐
│                    用户上下文（User Context）                   │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ 用户聚合     │  │ 用户档案聚合 │  │ 用户账户聚合 │      │
│  │ (User)       │  │ (UserProfile)│  │ (UserAccount)│      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ 用户偏好聚合 │  │ 用户关系聚合 │  │ 用户活动聚合 │      │
│  │ (UserPref)   │  │ (UserRelation)│  │ (UserActivity)│    │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│ 身份认证上下文│      │ 组织管理上下文│      │ 权限管理上下文│
│ (Identity)   │      │ (Organization)│      │ (Permission) │
└──────────────┘      └──────────────┘      └──────────────┘
        │                     │                     │
        ▼                     ▼                     ▼
┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│ 人力资源上下文│      │ 系统管理上下文│      │ 工作流上下文  │
│ (HRM)        │      │ (System)     │      │ (Workflow)   │
└──────────────┘      └──────────────┘      └──────────────┘
```

### 1.2 领域划分

#### 1.2.1 用户核心域（User Core Domain）
- **用户聚合（User Aggregate）**：用户身份、状态、认证信息
- **用户档案聚合（UserProfile Aggregate）**：用户基本信息、扩展信息
- **用户账户聚合（UserAccount Aggregate）**：账户安全、密码、MFA

#### 1.2.2 用户扩展域（User Extended Domain）
- **用户偏好聚合（UserPreference Aggregate）**：个性化设置、界面偏好
- **用户关系聚合（UserRelation Aggregate）**：用户关联关系、关注、好友
- **用户活动聚合（UserActivity Aggregate）**：活动记录、行为分析

### 1.3 领域事件

| 领域事件 | 触发条件 | 订阅者 | 用途 |
|---------|---------|--------|------|
| UserCreatedEvent | 用户创建成功 | HRM, Organization | 员工入职流程 |
| UserUpdatedEvent | 用户信息更新 | System, Workflow | 审计、通知 |
| UserDeletedEvent | 用户删除 | HRM, Organization | 员工离职流程 |
| UserStatusChangedEvent | 用户状态变更 | Auth, Permission | 权限重新计算 |
| UserPasswordChangedEvent | 密码修改 | Auth | 会话失效 |
| UserAssignedToDepartmentEvent | 用户分配部门 | Organization | 组织关系更新 |
| UserAssignedToRoleEvent | 用户分配角色 | Permission | 权限刷新 |
| UserLoggedInEvent | 用户登录 | System | 活动追踪 |
| UserMfaEnabledEvent | MFA启用 | Auth | 安全加固 |
| UserPreferenceChangedEvent | 偏好变更 | System | 个性化服务 |

---

## 二、聚合设计

### 2.1 用户聚合（User Aggregate）

**聚合根**：User

**职责**：管理用户身份、状态、认证等核心信息

#### 2.1.1 聚合根设计

```java
/**
 * 用户聚合根
 *
 * 领域职责：
 * - 用户身份管理
 * - 用户状态管理
 * - 用户认证信息管理
 * - 用户生命周期管理
 */
@Data
@Entity
@AggregateRoot
@Table(name = "user_aggregate")
public class User extends AggregateRootBase {

    /**
     * 用户ID（聚合根标识）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UserId userId;

    /**
     * 租户ID（多租户支持）
     */
    @TenantId
    private Long tenantId;

    /**
     * 用户名（唯一标识）
     */
    @Column(unique = true, nullable = false, length = 50)
    private Username username;

    /**
     * 用户状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    /**
     * 账户状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus accountStatus;

    /**
     * 用户类型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserType userType;

    /**
     * 是否超级管理员
     */
    @Column(nullable = false)
    private Boolean isSuperAdmin;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 登录失败次数
     */
    @Column(nullable = false)
    private Integer loginFailedCount;

    /**
     * 账户锁定时间
     */
    private LocalDateTime lockedTime;

    /**
     * 用户档案（实体）
     */
    @Embedded
    private UserProfile profile;

    /**
     * 用户账户（实体）
     */
    @Embedded
    private UserAccount account;

    /**
     * 用户偏好（实体）
     */
    @Embedded
    private UserPreference preference;

    /**
     * 用户组织关联（值对象集合）
     */
    @ElementCollection
    @CollectionTable(name = "user_organization_relation",
                     joinColumns = @JoinColumn(name = "user_id"))
    private List<UserOrganizationRelation> organizationRelations;

    /**
     * 用户角色关联（值对象集合）
     */
    @ElementCollection
    @CollectionTable(name = "user_role_relation",
                     joinColumns = @JoinColumn(name = "user_id"))
    private List<UserRoleRelation> roleRelations;

    // ============ 领域行为 ============

    /**
     * 创建用户
     *
     * @param command 创建用户命令
     * @return 用户聚合
     */
    public static User create(CreateUserCommand command) {
        User user = new User();

        // 设置基本信息
        user.userId = UserId.generate();
        user.tenantId = command.getTenantId();
        user.username = command.getUsername();

        // 设置默认状态
        user.status = UserStatus.ACTIVE;
        user.accountStatus = AccountStatus.NORMAL;
        user.userType = command.getUserType() != null ? command.getUserType() : UserType.INTERNAL;
        user.isSuperAdmin = false;
        user.loginFailedCount = 0;

        // 初始化子实体
        user.profile = UserProfile.create(command.getProfileCommand());
        user.account = UserAccount.create(command.getAccountCommand());
        user.preference = UserPreference.createDefault();

        // 初始化关联关系
        user.organizationRelations = new ArrayList<>();
        user.roleRelations = new ArrayList<>();

        // 记录领域事件
        user.registerDomainEvent(new UserCreatedEvent(
            user.userId,
            user.tenantId,
            user.username,
            LocalDateTime.now()
        ));

        return user;
    }

    /**
     * 激活用户
     */
    public void activate() {
        if (this.status == UserStatus.ACTIVE) {
            return;
        }

        this.status = UserStatus.ACTIVE;
        this.registerDomainEvent(new UserStatusChangedEvent(
            this.userId,
            this.status,
            LocalDateTime.now()
        ));
    }

    /**
     * 停用用户
     */
    public void deactivate() {
        if (this.status == UserStatus.INACTIVE) {
            return;
        }

        this.status = UserStatus.INACTIVE;
        this.registerDomainEvent(new UserStatusChangedEvent(
            this.userId,
            this.status,
            LocalDateTime.now()
        ));
    }

    /**
     * 锁定账户
     *
     * @param reason 锁定原因
     */
    public void lockAccount(String reason) {
        if (this.accountStatus == AccountStatus.LOCKED) {
            return;
        }

        this.accountStatus = AccountStatus.LOCKED;
        this.lockedTime = LocalDateTime.now();

        this.registerDomainEvent(new UserAccountLockedEvent(
            this.userId,
            reason,
            LocalDateTime.now()
        ));
    }

    /**
     * 解锁账户
     */
    public void unlockAccount() {
        if (this.accountStatus != AccountStatus.LOCKED) {
            return;
        }

        this.accountStatus = AccountStatus.NORMAL;
        this.lockedTime = null;
        this.loginFailedCount = 0;

        this.registerDomainEvent(new UserAccountUnlockedEvent(
            this.userId,
            LocalDateTime.now()
        ));
    }

    /**
     * 记录登录
     *
     * @param loginIp 登录IP
     */
    public void recordLogin(String loginIp) {
        this.lastLoginTime = LocalDateTime.now();
        this.lastLoginIp = loginIp;
        this.loginFailedCount = 0;

        this.registerDomainEvent(new UserLoggedInEvent(
            this.userId,
            this.username,
            loginIp,
            LocalDateTime.now()
        ));
    }

    /**
     * 记录登录失败
     */
    public void recordLoginFailed() {
        this.loginFailedCount++;

        // 失败次数超过阈值，自动锁定
        if (this.loginFailedCount >= 5) {
            lockAccount("登录失败次数过多");
        }
    }

    /**
     * 分配到组织
     *
     * @param orgId 组织ID
     * @param orgName 组织名称
     * @param isPrimary 是否主组织
     */
    public void assignToOrganization(OrganizationId orgId, String orgName, boolean isPrimary) {
        UserOrganizationRelation relation = UserOrganizationRelation.create(
            orgId,
            orgName,
            isPrimary
        );

        // 如果设置为主组织，清除其他主组织标记
        if (isPrimary) {
            this.organizationRelations.forEach(r -> r.setPrimary(false));
        }

        this.organizationRelations.add(relation);

        this.registerDomainEvent(new UserAssignedToOrganizationEvent(
            this.userId,
            orgId,
            orgName,
            LocalDateTime.now()
        ));
    }

    /**
     * 分配角色
     *
     * @param roleId 角色ID
     * @param roleName 角色名称
     * @param isPrimary 是否主角色
     */
    public void assignRole(RoleId roleId, String roleName, boolean isPrimary) {
        UserRoleRelation relation = UserRoleRelation.create(
            roleId,
            roleName,
            isPrimary
        );

        // 如果设置为主角色，清除其他主角色标记
        if (isPrimary) {
            this.roleRelations.forEach(r -> r.setPrimary(false));
        }

        this.roleRelations.add(relation);

        this.registerDomainEvent(new UserAssignedToRoleEvent(
            this.userId,
            roleId,
            roleName,
            LocalDateTime.now()
        ));
    }

    /**
     * 移除角色
     *
     * @param roleId 角色ID
     */
    public void removeRole(RoleId roleId) {
        this.roleRelations.removeIf(r -> r.getRoleId().equals(roleId));

        this.registerDomainEvent(new UserRoleRemovedEvent(
            this.userId,
            roleId,
            LocalDateTime.now()
        ));
    }

    /**
     * 更新用户档案
     *
     * @param command 更新用户档案命令
     */
    public void updateProfile(UpdateUserProfileCommand command) {
        this.profile.update(command);

        this.registerDomainEvent(new UserProfileUpdatedEvent(
            this.userId,
            LocalDateTime.now()
        ));
    }

    /**
     * 更新用户偏好
     *
     * @param command 更新用户偏好命令
     */
    public void updatePreference(UpdateUserPreferenceCommand command) {
        this.preference.update(command);

        this.registerDomainEvent(new UserPreferenceChangedEvent(
            this.userId,
            LocalDateTime.now()
        ));
    }
}
```

### 2.2 用户档案实体（UserProfile Entity）

```java
/**
 * 用户档案实体
 *
 * 职责：管理用户的基本信息和扩展信息
 */
@Data
@Embeddable
public class UserProfile {

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别
     */
    @Enumerated(EnumType.STRING)
    private Gender gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 手机号
     */
    @Column(unique = true, length = 20)
    private Phone phone;

    /**
     * 邮箱
     */
    @Column(unique = true, length = 100)
    private Email email;

    /**
     * 员工编号
     */
    private String employeeNo;

    /**
     * 入职日期
     */
    private LocalDate hireDate;

    /**
     * 工作地点
     */
    private String workLocation;

    /**
     * 工作邮箱
     */
    private String workEmail;

    /**
     * 工作电话
     */
    private String workPhone;

    /**
     * 地址信息
     */
    @Embedded
    private Address address;

    /**
     * 教育背景
     */
    @ElementCollection
    @CollectionTable(name = "user_education",
                     joinColumns = @JoinColumn(name = "user_id"))
    private List<Education> educations;

    /**
     * 工作经历
     */
    @ElementCollection
    @CollectionTable(name = "user_work_experience",
                     joinColumns = @JoinColumn(name = "user_id"))
    private List<WorkExperience> workExperiences;

    /**
     * 技能信息
     */
    @ElementCollection
    @CollectionTable(name = "user_skill",
                     joinColumns = @JoinColumn(name = "user_id"))
    private List<Skill> skills;

    /**
     * 证件信息
     */
    @ElementCollection
    @CollectionTable(name = "user_document",
                     joinColumns = @JoinColumn(name = "user_id"))
    private List<Document> documents;

    /**
     * 社交账号
     */
    @ElementCollection
    @CollectionTable(name = "user_social_account",
                     joinColumns = @JoinColumn(name = "user_id"))
    private List<SocialAccount> socialAccounts;

    /**
     * 扩展属性（JSON格式）
     */
    private Map<String, Object> extendedAttributes;

    // ============ 领域行为 ============

    /**
     * 创建用户档案
     */
    public static UserProfile create(CreateUserProfileCommand command) {
        UserProfile profile = new UserProfile();
        profile.realName = command.getRealName();
        profile.nickname = command.getNickname();
        profile.gender = command.getGender() != null ? command.getGender() : Gender.UNKNOWN;
        profile.phone = command.getPhone();
        profile.email = command.getEmail();
        profile.employeeNo = command.getEmployeeNo();
        profile.hireDate = command.getHireDate();
        profile.address = command.getAddress();
        profile.educations = new ArrayList<>();
        profile.workExperiences = new ArrayList<>();
        profile.skills = new ArrayList<>();
        profile.documents = new ArrayList<>();
        profile.socialAccounts = new ArrayList<>();
        profile.extendedAttributes = new HashMap<>();
        return profile;
    }

    /**
     * 更新用户档案
     */
    public void update(UpdateUserProfileCommand command) {
        if (command.getRealName() != null) {
            this.realName = command.getRealName();
        }
        if (command.getNickname() != null) {
            this.nickname = command.getNickname();
        }
        if (command.getAvatar() != null) {
            this.avatar = command.getAvatar();
        }
        if (command.getGender() != null) {
            this.gender = command.getGender();
        }
        if (command.getBirthday() != null) {
            this.birthday = command.getBirthday();
        }
        if (command.getPhone() != null) {
            this.phone = command.getPhone();
        }
        if (command.getEmail() != null) {
            this.email = command.getEmail();
        }
        if (command.getWorkLocation() != null) {
            this.workLocation = command.getWorkLocation();
        }
        if (command.getWorkEmail() != null) {
            this.workEmail = command.getWorkEmail();
        }
        if (command.getWorkPhone() != null) {
            this.workPhone = command.getWorkPhone();
        }
        if (command.getAddress() != null) {
            this.address = command.getAddress();
        }
        if (command.getExtendedAttributes() != null) {
            this.extendedAttributes.putAll(command.getExtendedAttributes());
        }
    }

    /**
     * 添加教育背景
     */
    public void addEducation(Education education) {
        this.educations.add(education);
    }

    /**
     * 添加工作经历
     */
    public void addWorkExperience(WorkExperience experience) {
        this.workExperiences.add(experience);
    }

    /**
     * 添加技能
     */
    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }

    /**
     * 添加证件
     */
    public void addDocument(Document document) {
        this.documents.add(document);
    }

    /**
     * 添加社交账号
     */
    public void addSocialAccount(SocialAccount account) {
        this.socialAccounts.add(account);
    }
}
```

### 2.3 用户账户实体（UserAccount Entity）

```java
/**
 * 用户账户实体
 *
 * 职责：管理账户安全信息（密码、MFA等）
 */
@Data
@Embeddable
public class UserAccount {

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 密码最后修改时间
     */
    private LocalDateTime passwordLastChangedTime;

    /**
     * 密码过期时间
     */
    private LocalDateTime passwordExpireTime;

    /**
     * 是否需要修改密码
     */
    private Boolean needChangePassword;

    /**
     * MFA是否启用
     */
    private Boolean mfaEnabled;

    /**
     * MFA密钥
     */
    private String mfaSecret;

    /**
     * MFA备用码
     */
    private List<String> mfaBackupCodes;

    /**
     * 密码安全问题
     */
    @ElementCollection
    @CollectionTable(name = "user_password_question",
                     joinColumns = @JoinColumn(name = "user_id"))
    private List<PasswordQuestion> passwordQuestions;

    /**
     * 登录方式（支持多种登录方式）
     */
    @ElementCollection
    @CollectionTable(name = "user_login_method",
                     joinColumns = @JoinColumn(name = "user_id"))
    private List<LoginMethod> loginMethods;

    /**
     * 账户安全日志
     */
    @ElementCollection
    @CollectionTable(name = "user_security_log",
                     joinColumns = @JoinColumn(name = "user_id"))
    private List<SecurityLog> securityLogs;

    // ============ 领域行为 ============

    /**
     * 创建用户账户
     */
    public static UserAccount create(CreateUserAccountCommand command) {
        UserAccount account = new UserAccount();

        // 加密密码
        account.password = PasswordEncoder.encode(command.getPassword());
        account.passwordLastChangedTime = LocalDateTime.now();

        // 密码过期时间（默认90天）
        account.passwordExpireTime = LocalDateTime.now().plusDays(90);

        // 首次登录需要修改密码
        account.needChangePassword = true;

        // MFA默认关闭
        account.mfaEnabled = false;

        // 初始化登录方式
        account.loginMethods = new ArrayList<>();
        account.loginMethods.add(LoginMethod.PASSWORD); // 密码登录

        // 初始化安全问题列表
        account.passwordQuestions = new ArrayList<>();

        // 初始化安全日志
        account.securityLogs = new ArrayList<>();

        return account;
    }

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @throws BusinessException 旧密码错误
     */
    public void changePassword(String oldPassword, String newPassword) {
        // 验证旧密码
        if (!PasswordEncoder.matches(oldPassword, this.password)) {
            throw new BusinessException("旧密码错误");
        }

        // 验证新密码复杂度
        PasswordPolicy.validate(newPassword);

        // 加密新密码
        this.password = PasswordEncoder.encode(newPassword);
        this.passwordLastChangedTime = LocalDateTime.now();
        this.passwordExpireTime = LocalDateTime.now().plusDays(90);
        this.needChangePassword = false;

        // 清除MFA（如果启用）
        if (this.mfaEnabled) {
            this.mfaEnabled = false;
            this.mfaSecret = null;
        }
    }

    /**
     * 重置密码（管理员操作）
     *
     * @param newPassword 新密码
     */
    public void resetPassword(String newPassword) {
        this.password = PasswordEncoder.encode(newPassword);
        this.passwordLastChangedTime = LocalDateTime.now();
        this.passwordExpireTime = LocalDateTime.now().plusDays(90);
        this.needChangePassword = true;

        // 清除MFA
        this.mfaEnabled = false;
        this.mfaSecret = null;
    }

    /**
     * 启用MFA
     *
     * @param secret MFA密钥
     * @param backupCodes 备用码
     */
    public void enableMfa(String secret, List<String> backupCodes) {
        this.mfaEnabled = true;
        this.mfaSecret = secret;
        this.mfaBackupCodes = backupCodes;
    }

    /**
     * 禁用MFA
     */
    public void disableMfa() {
        this.mfaEnabled = false;
        this.mfaSecret = null;
        this.mfaBackupCodes = null;
    }

    /**
     * 验证MFA码
     *
     * @param code MFA验证码
     * @return 是否验证成功
     */
    public boolean verifyMfaCode(String code) {
        if (!this.mfaEnabled) {
            return true;
        }

        // 验证备用码
        if (this.mfaBackupCodes != null && this.mfaBackupCodes.contains(code)) {
            this.mfaBackupCodes.remove(code);
            return true;
        }

        // 验证MFA码
        return MfaUtil.verifyCode(this.mfaSecret, code);
    }

    /**
     * 添加登录方式
     */
    public void addLoginMethod(LoginMethod method) {
        if (!this.loginMethods.contains(method)) {
            this.loginMethods.add(method);
        }
    }

    /**
     * 移除登录方式
     */
    public void removeLoginMethod(LoginMethod method) {
        this.loginMethods.remove(method);

        // 至少保留一种登录方式
        if (this.loginMethods.isEmpty()) {
            this.loginMethods.add(LoginMethod.PASSWORD);
        }
    }

    /**
     * 添加安全问题
     */
    public void addPasswordQuestion(PasswordQuestion question) {
        this.passwordQuestions.add(question);
    }

    /**
     * 记录安全日志
     */
    public void recordSecurityLog(SecurityLog log) {
        this.securityLogs.add(log);

        // 保留最近100条日志
        if (this.securityLogs.size() > 100) {
            this.securityLogs.remove(0);
        }
    }

    /**
     * 检查密码是否过期
     */
    public boolean isPasswordExpired() {
        return LocalDateTime.now().isAfter(this.passwordExpireTime);
    }
}
```

### 2.4 用户偏好实体（UserPreference Entity）

```java
/**
 * 用户偏好实体
 *
 * 职责：管理用户的个性化设置和界面偏好
 */
@Data
@Embeddable
public class UserPreference {

    /**
     * 语言偏好
     */
    @Column(length = 10)
    private String language;

    /**
     * 时区
     */
    @Column(length = 50)
    private String timezone;

    /**
     * 日期格式
     */
    @Column(length = 20)
    private String dateFormat;

    /**
     * 时间格式
     */
    @Column(length = 20)
    private String timeFormat;

    /**
     * 主题（dark/light）
     */
    @Column(length = 20)
    private String theme;

    /**
     * 首页布局
     */
    @Column(length = 50)
    private String homePageLayout;

    /**
     * 每页显示条数
     */
    private Integer pageSize;

    /**
     * 通知设置
     */
    @Embedded
    private NotificationSettings notificationSettings;

    /**
     * 界面设置
     */
    @Embedded
    private UISettings uiSettings;

    /**
     * 工作区设置
     */
    @ElementCollection
    @CollectionTable(name = "user_workspace_setting",
                     joinColumns = @JoinColumn(name = "user_id"))
    private List<WorkspaceSetting> workspaceSettings;

    /**
     * 快捷方式
     */
    @ElementCollection
    @CollectionTable(name = "user_shortcut",
                     joinColumns = @JoinColumn(name = "user_id"))
    private List<Shortcut> shortcuts;

    // ============ 领域行为 ============

    /**
     * 创建默认用户偏好
     */
    public static UserPreference createDefault() {
        UserPreference preference = new UserPreference();

        // 默认语言（中文）
        preference.language = "zh-CN";

        // 默认时区（系统时区）
        preference.timezone = ZoneId.systemDefault().getId();

        // 默认日期格式
        preference.dateFormat = "yyyy-MM-dd";

        // 默认时间格式
        preference.timeFormat = "HH:mm:ss";

        // 默认主题
        preference.theme = "light";

        // 默认每页显示条数
        preference.pageSize = 20;

        // 初始化通知设置
        preference.notificationSettings = NotificationSettings.createDefault();

        // 初始化界面设置
        preference.uiSettings = UISettings.createDefault();

        // 初始化工作区设置
        preference.workspaceSettings = new ArrayList<>();

        // 初始化快捷方式
        preference.shortcuts = new ArrayList<>();

        return preference;
    }

    /**
     * 更新用户偏好
     */
    public void update(UpdateUserPreferenceCommand command) {
        if (command.getLanguage() != null) {
            this.language = command.getLanguage();
        }
        if (command.getTimezone() != null) {
            this.timezone = command.getTimezone();
        }
        if (command.getDateFormat() != null) {
            this.dateFormat = command.getDateFormat();
        }
        if (command.getTimeFormat() != null) {
            this.timeFormat = command.getTimeFormat();
        }
        if (command.getTheme() != null) {
            this.theme = command.getTheme();
        }
        if (command.getHomePageLayout() != null) {
            this.homePageLayout = command.getHomePageLayout();
        }
        if (command.getPageSize() != null) {
            this.pageSize = command.getPageSize();
        }
        if (command.getNotificationSettings() != null) {
            this.notificationSettings = command.getNotificationSettings();
        }
        if (command.getUiSettings() != null) {
            this.uiSettings = command.getUiSettings();
        }
    }

    /**
     * 添加工作区设置
     */
    public void addWorkspaceSetting(WorkspaceSetting setting) {
        this.workspaceSettings.add(setting);
    }

    /**
     * 添加快捷方式
     */
    public void addShortcut(Shortcut shortcut) {
        this.shortcuts.add(shortcut);
    }

    /**
     * 移除快捷方式
     */
    public void removeShortcut(String shortcutId) {
        this.shortcuts.removeIf(s -> s.getId().equals(shortcutId));
    }
}
```

### 2.5 值对象（Value Objects）

#### 2.5.1 用户组织关系（UserOrganizationRelation）

```java
/**
 * 用户组织关系值对象
 */
@Data
@Embeddable
public class UserOrganizationRelation {

    /**
     * 组织ID
     */
    private Long orgId;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 是否主组织
     */
    private Boolean isPrimary;

    /**
     * 入职时间
     */
    private LocalDate joinDate;

    /**
     * 离职时间
     */
    private LocalDate leaveDate;

    /**
     * 在职状态
     */
    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    /**
     * 职位
     */
    private String position;

    /**
     * 职级
     */
    private String grade;

    public static UserOrganizationRelation create(
            Long orgId, String orgName, boolean isPrimary) {
        UserOrganizationRelation relation = new UserOrganizationRelation();
        relation.orgId = orgId;
        relation.orgName = orgName;
        relation.isPrimary = isPrimary;
        relation.joinDate = LocalDate.now();
        relation.employmentStatus = EmploymentStatus.ACTIVE;
        return relation;
    }
}
```

#### 2.5.2 用户角色关系（UserRoleRelation）

```java
/**
 * 用户角色关系值对象
 */
@Data
@Embeddable
public class UserRoleRelation {

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 是否主角色
     */
    private Boolean isPrimary;

    /**
     * 分配时间
     */
    private LocalDateTime assignedTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    public static UserRoleRelation create(
            Long roleId, String roleName, boolean isPrimary) {
        UserRoleRelation relation = new UserRoleRelation();
        relation.roleId = roleId;
        relation.roleName = roleName;
        relation.isPrimary = isPrimary;
        relation.assignedTime = LocalDateTime.now();
        return relation;
    }
}
```

---

## 三、领域服务

### 3.1 用户创建领域服务

```java
/**
 * 用户创建领域服务
 */
@DomainService
public class UserCreationDomainService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordPolicy passwordPolicy;

    /**
     * 创建用户（协调多个聚合的创建）
     */
    @Transactional
    public User CreateUser(CreateUserCommand command) {
        // 1. 校验用户名唯一性
        if (userRepository.existsByUsername(command.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 校验手机号唯一性
        if (command.getPhone() != null
            && userRepository.existsByPhone(command.getPhone())) {
            throw new BusinessException("手机号已存在");
        }

        // 3. 校验邮箱唯一性
        if (command.getEmail() != null
            && userRepository.existsByEmail(command.getEmail())) {
            throw new BusinessException("邮箱已存在");
        }

        // 4. 校验密码策略
        passwordPolicy.validate(command.getAccountCommand().getPassword());

        // 5. 校验组织是否存在
        if (command.getOrganizationIds() != null) {
            for (Long orgId : command.getOrganizationIds()) {
                if (!organizationRepository.existsById(orgId)) {
                    throw new BusinessException("组织不存在");
                }
            }
        }

        // 6. 校验角色是否存在
        if (command.getRoleIds() != null) {
            for (Long roleId : command.getRoleIds()) {
                if (!roleRepository.existsById(roleId)) {
                    throw new BusinessException("角色不存在");
                }
            }
        }

        // 7. 创建用户聚合
        User user = User.create(command);

        // 8. 保存用户
        userRepository.save(user);

        return user;
    }
}
```

### 3.2 用户查询领域服务

```java
/**
 * 用户查询领域服务（CQRS的查询端）
 */
@DomainService
public class UserQueryDomainService {

    @Autowired
    private UserQueryRepository userQueryRepository;

    /**
     * 根据用户名查询用户
     */
    public UserDTO getUserByUsername(String username) {
        User user = userQueryRepository.findByUsername(username);
        if (user == null) {
            throw new DataNotFoundException("用户不存在");
        }
        return UserMapper.toDTO(user);
    }

    /**
     * 根据手机号查询用户
     */
    public UserDTO getUserByPhone(String phone) {
        User user = userQueryRepository.findByPhone(phone);
        if (user == null) {
            throw new DataNotFoundException("用户不存在");
        }
        return UserMapper.toDTO(user);
    }

    /**
     * 分页查询用户
     */
    public Page<UserDTO> listUsers(UserQuery query) {
        return userQueryRepository.queryPage(query)
            .map(UserMapper::toDTO);
    }

    /**
     * 查询用户详情
     */
    public UserDetailDTO getUserDetail(UserId userId) {
        User user = userQueryRepository.findById(userId);
        if (user == null) {
            throw new DataNotFoundException("用户不存在");
        }
        return UserMapper.toDetailDTO(user);
    }
}
```

### 3.3 用户统计领域服务

```java
/**
 * 用户统计领域服务
 */
@DomainService
public class UserStatisticsDomainService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 统计活跃用户数
     */
    public Long countActiveUsers() {
        return userRepository.countByStatus(UserStatus.ACTIVE);
    }

    /**
     * 统计今日登录用户数
     */
    public Long countTodayLoginUsers() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        return userRepository.countByLastLoginTimeAfter(startOfDay);
    }

    /**
     * 统计组织用户数
     */
    public Long countUsersByOrganization(Long orgId) {
        return userRepository.countByOrganizationId(orgId);
    }

    /**
     * 统计用户增长趋势
     */
    public List<UserGrowthData> getUserGrowthTrend(
        LocalDate startDate, LocalDate endDate) {
        return userRepository.getUserGrowthTrend(startDate, endDate);
    }
}
```

---

## 四、仓储设计

### 4.1 用户仓储接口

```java
/**
 * 用户仓储接口
 */
public interface UserRepository extends AggregateRepository<User, UserId> {

    /**
     * 根据用户名查询用户
     */
    User findByUsername(Username username);

    /**
     * 根据手机号查询用户
     */
    User findByPhone(Phone phone);

    /**
     * 根据邮箱查询用户
     */
    User findByEmail(Email email);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(Username username);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(Phone phone);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(Email email);

    /**
     * 根据状态统计用户数
     */
    Long countByStatus(UserStatus status);

    /**
     * 统计最后登录时间之后的用户数
     */
    Long countByLastLoginTimeAfter(LocalDateTime time);

    /**
     * 统计组织用户数
     */
    Long countByOrganizationId(Long orgId);

    /**
     * 查询用户增长趋势
     */
    List<UserGrowthData> getUserGrowthTrend(
        LocalDate startDate, LocalDate endDate);
}
```

### 4.2 用户查询仓储接口（CQRS）

```java
/**
 * 用户查询仓储接口（CQRS查询端）
 */
public interface UserQueryRepository {

    /**
     * 根据ID查询用户
     */
    User findById(UserId userId);

    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    User findByPhone(String phone);

    /**
     * 根据邮箱查询用户
     */
    User findByEmail(String email);

    /**
     * 分页查询用户
     */
    Page<User> queryPage(UserQuery query);

    /**
     * 查询用户列表
     */
    List<User> queryList(UserQuery query);

    /**
     * 根据组织ID查询用户
     */
    List<User> findByOrganizationId(Long orgId);

    /**
     * 根据角色ID查询用户
     */
    List<User> findByRoleId(Long roleId);
}
```

---

## 五、应用服务设计

### 5.1 用户应用服务

```java
/**
 * 用户应用服务
 */
@ApplicationService
public class UserApplicationService {

    @Autowired
    private UserCreationDomainService userCreationDomainService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserQueryDomainService userQueryDomainService;

    /**
     * 创建用户命令
     */
    @Transactional
    @CommandLog(module = "用户管理", operation = "创建用户", type = CommandLogType.INSERT)
    public UserId createUser(CreateUserCommand command) {
        User user = userCreationDomainService.CreateUser(command);
        return user.getUserId();
    }

    /**
     * 更新用户命令
     */
    @Transactional
    @CommandLog(module = "用户管理", operation = "更新用户", type = CommandLogType.UPDATE)
    public void updateUser(UpdateUserCommand command) {
        User user = userRepository.findById(command.getUserId());
        if (user == null) {
            throw new DataNotFoundException("用户不存在");
        }

        user.updateProfile(command.getProfileCommand());
        user.updatePreference(command.getPreferenceCommand());

        userRepository.save(user);
    }

    /**
     * 激活用户命令
     */
    @Transactional
    @CommandLog(module = "用户管理", operation = "激活用户", type = CommandLogType.UPDATE)
    public void activateUser(UserId userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new DataNotFoundException("用户不存在");
        }

        user.activate();
        userRepository.save(user);
    }

    /**
     * 停用用户命令
     */
    @Transactional
    @CommandLog(module = "用户管理", operation = "停用用户", type = CommandLogType.UPDATE)
    public void deactivateUser(UserId userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new DataNotFoundException("用户不存在");
        }

        user.deactivate();
        userRepository.save(user);
    }

    /**
     * 删除用户命令
     */
    @Transactional
    @CommandLog(module = "用户管理", operation = "删除用户", type = CommandLogType.DELETE)
    public void deleteUser(UserId userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new DataNotFoundException("用户不存在");
        }

        // 超级管理员不能删除
        if (user.getIsSuperAdmin()) {
            throw new BusinessException("不能删除超级管理员");
        }

        // 软删除
        userRepository.delete(userId);
    }

    /**
     * 查询用户详情查询
     */
    @QueryLog(module = "用户管理", operation = "查询用户详情", type = QueryLogType.SELECT)
    public UserDetailDTO getUserDetail(UserId userId) {
        return userQueryDomainService.getUserDetail(userId);
    }

    /**
     * 分页查询用户查询
     */
    @QueryLog(module = "用户管理", operation = "分页查询用户", type = QueryLogType.SELECT)
    public Page<UserDTO> listUsers(UserQuery query) {
        return userQueryDomainService.listUsers(query);
    }
}
```

---

## 六、领域事件处理

### 6.1 用户创建事件处理器

```java
/**
 * 用户创建事件处理器
 */
@Component
public class UserCreatedEventHandler {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private HrmService hrmService;

    @Autowired
    private NotificationService notificationService;

    /**
     * 处理用户创建事件
     */
    @EventListener
    @Async
    public void handle(UserCreatedEvent event) {
        log.info("处理用户创建事件: userId={}, username={}",
            event.getUserId(), event.getUsername());

        // 1. 通知组织管理上下文：用户已创建
        organizationService.onUserCreated(event.getUserId(), event.getTenantId());

        // 2. 通知权限管理上下文：用户已创建
        roleService.onUserCreated(event.getUserId());

        // 3. 如果是内部用户，通知人力资源管理上下文
        if (event.getUserType() == UserType.INTERNAL) {
            hrmService.onEmployeeCreated(event.getUserId());
        }

        // 4. 发送欢迎通知
        notificationService.sendWelcomeNotification(event.getUserId());

        log.info("用户创建事件处理完成");
    }
}
```

### 6.2 用户状态变更事件处理器

```java
/**
 * 用户状态变更事件处理器
 */
@Component
public class UserStatusChangedEventHandler {

    @Autowired
    private AuthService authService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private WorkflowService workflowService;

    /**
     * 处理用户状态变更事件
     */
    @EventListener
    @Async
    public void handle(UserStatusChangedEvent event) {
        log.info("处理用户状态变更事件: userId={}, status={}",
            event.getUserId(), event.getStatus());

        // 1. 如果用户被停用，失效所有会话
        if (event.getStatus() == UserStatus.INACTIVE) {
            authService.invalidateAllSessions(event.getUserId());
        }

        // 2. 通知权限管理上下文：用户状态已变更
        permissionService.onUserStatusChanged(event.getUserId(), event.getStatus());

        // 3. 通知工作流上下文：用户状态已变更
        workflowService.onUserStatusChanged(event.getUserId(), event.getStatus());

        log.info("用户状态变更事件处理完成");
    }
}
```

---

## 七、枚举与常量

### 7.1 用户状态枚举

```java
/**
 * 用户状态枚举
 */
public enum UserStatus {
    /**
     * 激活
     */
    ACTIVE("激活"),

    /**
     * 未激活
     */
    INACTIVE("未激活"),

    /**
     * 锁定
     */
    LOCKED("锁定"),

    /**
     * 已删除
     */
    DELETED("已删除");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }
}
```

### 7.2 账户状态枚举

```java
/**
 * 账户状态枚举
 */
public enum AccountStatus {
    /**
     * 正常
     */
    NORMAL("正常"),

    /**
     * 锁定
     */
    LOCKED("锁定"),

    /**
     * 密码过期
     */
    PASSWORD_EXPIRED("密码过期"),

    /**
     * 需要修改密码
     */
    NEED_CHANGE_PASSWORD("需要修改密码");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }
}
```

### 7.3 用户类型枚举

```java
/**
 * 用户类型枚举
 */
public enum UserType {
    /**
     * 内部用户（员工）
     */
    INTERNAL("内部用户"),

    /**
     * 外部用户（客户、供应商等）
     */
    EXTERNAL("外部用户"),

    /**
     * 系统用户
     */
    SYSTEM("系统用户");

    private final String description;

    UserType(String description) {
        this.description = description;
    }
}
```

---

## 八、与总体设计的对应关系

### 8.1 核心功能模块对应

根据总体设计文档，用户管理模块与以下核心功能模块对应：

| 总体设计模块 | 用户管理模块功能 | 实现方式 |
|------------|-----------------|---------|
| 身份认证与安全 | 用户认证、MFA、密码管理 | UserAccount实体 |
| 系统管理 | 用户状态管理、用户配置 | User聚合 |
| 全球化支持 | 多语言、多时区 | UserPreference实体 |
| 数据管理 | 用户档案、扩展属性 | UserProfile实体 |
| 人力资源管理 | 员工信息、组织关系 | UserOrganizationRelation值对象 |
| 权限与角色 | 用户角色关联 | UserRoleRelation值对象 |

### 8.2 领域事件对应

| 总体设计事件 | 用户管理模块事件 | 用途 |
|------------|-----------------|------|
| 员工变动事件 | UserCreatedEvent/UserUpdatedEvent | HR领域事件 |
| 权限变更事件 | UserAssignedToRoleEvent | 权限领域事件 |
| 组织变更事件 | UserAssignedToOrganizationEvent | 组织领域事件 |

---

## 九、实施路径

### 9.1 第一阶段（核心聚合）

1. **User聚合**：实现用户核心聚合根
2. **UserProfile实体**：实现用户档案实体
3. **UserAccount实体**：实现用户账户实体
4. **UserPreference实体**：实现用户偏好实体
5. **基础领域事件**：实现核心领域事件

### 9.2 第二阶段（仓储与应用服务）

1. **UserRepository**：实现用户仓储
2. **UserQueryRepository**：实现用户查询仓储（CQRS）
3. **UserCreationDomainService**：实现用户创建领域服务
4. **UserQueryDomainService**：实现用户查询领域服务
5. **UserApplicationService**：实现用户应用服务

### 9.3 第三阶段（领域事件与集成）

1. **事件处理器**：实现领域事件处理器
2. **事件总线**：集成事件总线
3. **跨上下文集成**：与其他上下文集成
4. **领域服务扩展**：实现统计、分析等扩展服务

### 9.4 第四阶段（高级功能）

1. **用户关系聚合**：实现用户关系管理
2. **用户活动聚合**：实现用户活动追踪
3. **用户画像**：实现用户画像分析
4. **用户推荐**：实现智能推荐

---

## 十、总结

### 10.1 DDD设计亮点

1. **聚合根清晰**：User作为聚合根，管理用户核心信息
2. **实体职责明确**：各实体职责单一，高内聚低耦合
3. **值对象丰富**：使用值对象封装业务概念
4. **领域事件驱动**：通过领域事件实现松耦合集成
5. **CQRS分离**：命令端和查询端分离，优化性能
6. **领域服务协调**：领域服务协调多个聚合的交互
7. **业务规则内聚**：业务规则封装在聚合根和实体中

### 10.2 与总体设计的契合

1. **DDD分层**：严格按照DDD分层设计
2. **限界上下文**：清晰的限界上下文划分
3. **领域事件**：完整的领域事件体系
4. **跨上下文集成**：与其他上下文的事件集成
5. **多租户支持**：完整的多租户支持
6. **全球化支持**：多语言、多时区支持
7. **企业规模适配**：支持小/中/大/集团企业

### 10.3 技术实现要点

1. **聚合根持久化**：使用JPA/Hibernate持久化聚合根
2. **事件发布**：使用Spring Event发布领域事件
3. **异步处理**：使用@Async异步处理事件
4. **事务管理**：使用@Transactional管理事务
5. **缓存策略**：使用Caffeine本地缓存
6. **日志追踪**：完整的操作日志和审计日志

---

**文档版本历史**

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|----------|------|
| 2.0.0-REDESIGN | 2026-02-18 | 基于DDD重新设计用户管理模块 | QooERP团队 |
| 1.0.0 | 2026-02-17 | 初始版本 | QooERP团队 |
