# QooERP Common 模块 - API接口文档

## 说明

Common模块为公共基础模块，主要提供工具类、公共组件和基础设施，不直接暴露REST API接口。本章节列出Common模块对内提供的核心工具类和组件接口。

---

## 1. 响应体工具

### 统一响应体

**Response<T>**

提供统一的API响应格式。

#### 静态方法

**成功响应**
```java
Response<T> success(T data)
```
- **说明**: 返回成功响应
- **参数**: 
  - data: 响应数据
- **返回**: Response对象，code=200

```java
Response<T> success(String message, T data)
```
- **说明**: 返回带消息的成功响应
- **参数**: 
  - message: 响应消息
  - data: 响应数据
- **返回**: Response对象，code=200

**失败响应**
```java
Response<T> error(String message)
```
- **说明**: 返回失败响应
- **参数**: 
  - message: 错误消息
- **返回**: Response对象，code=500

```java
Response<T> error(Integer code, String message)
```
- **说明**: 返回自定义错误码的失败响应
- **参数**: 
  - code: 错误码
  - message: 错误消息
- **返回**: Response对象

**分页响应**
```java
PageResponse<T> page(Page<T> page)
```
- **说明**: 返回分页响应
- **参数**: 
  - page: MyBatis-Plus分页对象
- **返回**: PageResponse对象

---

## 2. 异常处理

### 异常类

**BusinessException**

业务异常基类。

#### 构造方法
```java
BusinessException(String message)
```
```java
BusinessException(String message, Throwable cause)
```

**ValidationException**

参数校验异常。

#### 构造方法
```java
ValidationException(String message)
```
```java
ValidationException(String fieldName, String message)
```

**PermissionException**

权限异常。

#### 构造方法
```java
PermissionException(String message)
```

**DataNotFoundException**

数据不存在异常。

#### 构造方法
```java
DataNotFoundException(String message)
```
```java
DataNotFoundException(Class<?> entityClass, Object id)
```

---

## 3. 工具类接口

### DateTimeUtils

日期时间工具类。

#### 静态方法

**日期格式化**
```java
String format(Date date, String pattern)
```
- **说明**: 格式化日期
- **参数**: 
  - date: 日期对象
  - pattern: 日期格式，如"yyyy-MM-dd HH:mm:ss"
- **返回**: 格式化后的日期字符串

```java
Date parse(String dateStr, String pattern)
```
- **说明**: 解析日期字符串
- **参数**: 
  - dateStr: 日期字符串
  - pattern: 日期格式
- **返回**: Date对象

**日期计算**
```java
Date addDays(Date date, int days)
```
- **说明**: 日期加天数
- **参数**: 
  - date: 原始日期
  - days: 要加的天数
- **返回**: 计算后的日期

```java
Date addMonths(Date date, int months)
```
- **说明**: 日期加月数

```java
Date addYears(Date date, int years)
```
- **说明**: 日期加年数

**日期比较**
```java
boolean isSameDay(Date date1, Date date2)
```
- **说明**: 判断两个日期是否为同一天
- **返回**: true/false

```java
long betweenDays(Date start, Date end)
```
- **说明**: 计算两个日期之间的天数
- **返回**: 天数

### StringUtils

字符串工具类。

#### 静态方法

**判断工具**
```java
boolean isEmpty(String str)
```
- **说明**: 判断字符串是否为空
- **返回**: true/false

```java
boolean isNotEmpty(String str)
```
- **说明**: 判断字符串是否非空

```java
boolean isBlank(String str)
```
- **说明**: 判断字符串是否为空白

```java
boolean isNotBlank(String str)
```
- **说明**: 判断字符串是否非空白

**转换工具**
```java
String toCamelCase(String str)
```
- **说明**: 转换为驼峰命名
- **示例**: "hello_world" -> "helloWorld"

```java
String toSnakeCase(String str)
```
- **说明**: 转换为下划线命名
- **示例**: "helloWorld" -> "hello_world"

```java
String toKebabCase(String str)
```
- **说明**: 转换为短横线命名
- **示例**: "helloWorld" -> "hello-world"

**字符串处理**
```java
String abbreviate(String str, int maxWidth)
```
- **说明**: 缩略字符串
- **返回**: 缩略后的字符串

```java
String substringBefore(String str, String separator)
```
- **说明**: 获取分隔符之前的字符串

```java
String substringAfter(String str, String separator)
```
- **说明**: 获取分隔符之后的字符串

### JsonUtils

JSON工具类。

#### 静态方法

**对象转JSON**
```java
String toJson(Object obj)
```
- **说明**: 对象转JSON字符串
- **参数**: 
  - obj: 要转换的对象
- **返回**: JSON字符串

```java
String toJsonPretty(Object obj)
```
- **说明**: 对象转格式化的JSON字符串

**JSON转对象**
```java
<T> T fromJson(String json, Class<T> clazz)
```
- **说明**: JSON字符串转对象
- **参数**: 
  - json: JSON字符串
  - clazz: 目标类型
- **返回**: 转换后的对象

```java
<T> T fromJson(String json, TypeReference<T> typeRef)
```
- **说明**: JSON字符串转泛型对象
- **参数**: 
  - json: JSON字符串
  - typeRef: 类型引用
- **返回**: 转换后的对象

**JSON节点操作**
```java
JsonNode parseNode(String json)
```
- **说明**: 解析JSON为节点树
- **返回**: JsonNode对象

```java
String getValue(String json, String path)
```
- **说明**: 获取JSON路径对应的值
- **参数**: 
  - json: JSON字符串
  - path: JSON路径，如"data.user.name"
- **返回**: 对应的值

### CryptoUtils

加密工具类。

#### 静态方法

**哈希加密**
```java
String md5(String input)
```
- **说明**: MD5加密
- **参数**: 
  - input: 输入字符串
- **返回**: 32位MD5字符串

```java
String sha256(String input)
```
- **说明**: SHA-256加密
- **返回**: SHA-256字符串

**AES加密解密**
```java
String aesEncrypt(String data, String key)
```
- **说明**: AES加密
- **参数**: 
  - data: 待加密数据
  - key: 加密密钥
- **返回**: 加密后的Base64字符串

```java
String aesDecrypt(String encryptedData, String key)
```
- **说明**: AES解密
- **参数**: 
  - encryptedData: 加密数据
  - key: 解密密钥
- **返回**: 解密后的原始数据

**RSA加密解密**
```java
String rsaEncrypt(String data, String publicKey)
```
- **说明**: RSA公钥加密

```java
String rsaDecrypt(String encryptedData, String privateKey)
```
- **说明**: RSA私钥解密

**Base64编解码**
```java
String base64Encode(String data)
```
- **说明**: Base64编码

```java
String base64Decode(String encodedData)
```
- **说明**: Base64解码

---

## 4. 注解定义

### @OperationLog

操作日志注解，用于记录方法调用日志。

#### 注解属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| module | String | 是 | 模块名称 |
| operation | String | 是 | 操作名称 |
| description | String | 否 | 操作描述，默认空 |
| type | LogType | 否 | 日志类型，默认LogType.OTHER |

#### 使用示例
```java
@OperationLog(module = "用户管理", operation = "创建用户", type = LogType.INSERT)
public Long createUser(UserDTO userDTO) {
    // ...
}
```

### @RequirePermission

权限校验注解，用于方法级别的权限验证。

#### 注解属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| value | String | 是 | 权限标识，支持多个，逗号分隔 |
| logical | LogicalType | 否 | 逻辑类型，AND/OR，默认AND |

#### 使用示例
```java
@RequirePermission(value = "user:create,user:edit", logical = LogicalType.OR)
public void updateUser(UserDTO userDTO) {
    // ...
}
```

### @DataScope

数据权限注解，用于SQL级别的数据权限过滤。

#### 注解属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| column | String | 是 | 数据权限列名，如"dept_id" |
| alias | String | 否 | 表别名，默认空 |

#### 使用示例
```java
@DataScope(column = "dept_id", alias = "u")
public List<UserVO> listUsers() {
    // SQL会自动注入数据权限条件
}
```

### @PreventDuplicateSubmit

防重复提交注解。

#### 注解属性

| 属性 | 类型 | 必填 | 说明 |
|------|------|------|------|
| timeout | long | 否 | 超时时间(毫秒)，默认3000 |
| message | String | 否 | 提示消息，默认"请勿重复提交" |

#### 使用示例
```java
@PreventDuplicateSubmit(timeout = 5000)
public void submitOrder(OrderDTO orderDTO) {
    // ...
}
```

---

## 5. 枚举定义

### ResponseCode

响应码枚举。

| 枚举值 | code | message | 说明 |
|--------|------|---------|------|
| SUCCESS | 200 | 操作成功 | 成功响应 |
| BAD_REQUEST | 400 | 请求参数错误 | 参数校验失败 |
| UNAUTHORIZED | 401 | 未授权 | 未登录或token过期 |
| FORBIDDEN | 403 | 无权限访问 | 权限不足 |
| NOT_FOUND | 404 | 资源不存在 | 资源未找到 |
| INTERNAL_ERROR | 500 | 系统内部错误 | 服务器错误 |

### OperationType

操作类型枚举。

| 枚举值 | 描述 |
|--------|------|
| INSERT | 新增 |
| UPDATE | 修改 |
| DELETE | 删除 |
| QUERY | 查询 |
| EXPORT | 导出 |
| IMPORT | 导入 |
| OTHER | 其他 |

### CommonStatus

通用状态枚举。

| 枚举值 | code | 描述 |
|--------|------|------|
| ENABLED | 1 | 启用 |
| DISABLED | 0 | 禁用 |

---

## 6. 常量定义

### SystemConstants

系统常量。

| 常量名 | 值 | 说明 |
|--------|---|------|
| DEFAULT_PASSWORD | "123456" | 默认密码 |
| SUPER_ADMIN | "admin" | 超级管理员账号 |
| SUPER_ADMIN_ID | 1L | 超级管理员ID |
| DEFAULT_TENANT | "default" | 默认租户标识 |

### CacheConstants

缓存常量。

| 常量名 | 值 | 说明 |
|--------|---|------|
| LOGIN_TOKEN_KEY | "login:token:" | 登录Token缓存前缀 |
| CAPTCHA_CODE_KEY | "captcha:code:" | 验证码缓存前缀 |
| DICT_DATA_KEY | "dict:data:" | 字典数据缓存前缀 |
| USER_INFO_KEY | "user:info:" | 用户信息缓存前缀 |

---

## 7. DTO和VO定义

### PageQueryDTO

分页查询请求体。

```java
public class PageQueryDTO {
    private Long current;        // 当前页，默认1
    private Long size;          // 每页大小，默认10
    private String sortField;   // 排序字段
    private String sortOrder;   // 排序方向：asc/desc
}
```

### TreeNodeVO

树形结构视图对象。

```java
public class TreeNodeVO {
    private Long id;                   // 节点ID
    private String name;               // 节点名称
    private Long parentId;             // 父节点ID
    private List<TreeNodeVO> children; // 子节点列表
    private Boolean hasChildren;       // 是否有子节点
}
```

### DictItemVO

字典项视图对象。

```java
public class DictItemVO {
    private String value;        // 字典值
    private String label;        // 字典标签
    private String type;         // 字典类型
    private Integer sort;        // 排序
    private Boolean isDefault;   // 是否默认
}
```

---

## 8. 使用示例

### 8.1 统一响应体使用

```java
@GetMapping("/users/{id}")
public Response<UserVO> getUser(@PathVariable Long id) {
    UserVO user = userService.getUserById(id);
    return Response.success(user);
}

@GetMapping("/users")
public PageResponse<UserVO> listUsers(PageQueryDTO query) {
    Page<UserVO> page = userService.listUsers(query);
    return Response.page(page);
}
```

### 8.2 异常使用

```java
@PostMapping("/users")
public Response<Long> createUser(@RequestBody UserDTO userDTO) {
    if (userDTO.getUsername() == null) {
        throw new ValidationException("用户名不能为空");
    }
    Long userId = userService.createUser(userDTO);
    return Response.success(userId);
}
```

### 8.3 工具类使用

```java
// 日期工具
String nowStr = DateTimeUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
Date yesterday = DateTimeUtils.addDays(new Date(), -1);

// 字符串工具
String camelCase = StringUtils.toCamelCase("user_name");  // "userName"
boolean isBlank = StringUtils.isBlank("");  // true

// JSON工具
String json = JsonUtils.toJson(userDTO);
UserVO user = JsonUtils.fromJson(json, UserVO.class);

// 加密工具
String md5 = CryptoUtils.md5("123456");
String encrypted = CryptoUtils.aesEncrypt("hello", "secretKey");
```

### 8.4 注解使用

```java
@RestController
@RequestMapping("/users")
public class UserController {
    
    @OperationLog(module = "用户管理", operation = "创建用户", type = OperationType.INSERT)
    @RequirePermission("user:create")
    @PreventDuplicateSubmit(timeout = 3000)
    @PostMapping
    public Response<Long> createUser(@RequestBody UserDTO userDTO) {
        // ...
    }
    
    @OperationLog(module = "用户管理", operation = "查询用户", type = OperationType.QUERY)
    @DataScope(column = "dept_id", alias = "u")
    @GetMapping("/list")
    public Response<List<UserVO>> listUsers() {
        // ...
    }
}
```

---

## 9. 版本历史

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|----------|------|
| 1.0.0 | 2025-01-17 | 初始版本 | Auto |
