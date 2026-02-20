package com.qoobot.qooerp.workflow.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.workflow.dto.TaskDTO;

import java.util.List;
import java.util.Map;

/**
 * 任务服务
 */
public interface WorkflowTaskService {

    /**
     * 查询待办任务列表
     * @param userId 用户ID
     * @return 任务列表
     */
    List<TaskDTO> listTodoTasks(String userId);

    /**
     * 分页查询待办任务
     * @param current 当前页
     * @param size 每页大小
     * @param userId 用户ID
     * @param processDefinitionKey 流程定义Key
     * @param taskName 任务名称
     * @return 分页结果
     */
    PageResult<TaskDTO> pageTodoTasks(Long current, Long size, String userId,
                                String processDefinitionKey, String taskName);

    /**
     * 查询已办任务列表
     * @param userId 用户ID
     * @return 任务列表
     */
    List<TaskDTO> listDoneTasks(String userId);

    /**
     * 分页查询已办任务
     * @param current 当前页
     * @param size 每页大小
     * @param userId 用户ID
     * @param processDefinitionKey 流程定义Key
     * @param taskName 任务名称
     * @return 分页结果
     */
    PageResult<TaskDTO> pageDoneTasks(Long current, Long size, String userId,
                                String processDefinitionKey, String taskName);

    /**
     * 查询抄送任务列表
     * @param userId 用户ID
     * @return 任务列表
     */
    List<TaskDTO> listCcTasks(String userId);

    /**
     * 查询任务详情
     * @param taskId 任务ID
     * @return 任务DTO
     */
    TaskDTO getTask(String taskId);

    /**
     * 审批通过
     * @param taskId 任务ID
     * @param comment 审批意见
     * @param variables 流程变量
     * @param userId 用户ID
     */
    void approveTask(String taskId, String comment, Map<String, Object> variables, String userId);

    /**
     * 审批驳回
     * @param taskId 任务ID
     * @param comment 审批意见
     * @param userId 用户ID
     */
    void rejectTask(String taskId, String comment, String userId);

    /**
     * 任务转派
     * @param taskId 任务ID
     * @param targetUserId 目标用户ID
     * @param comment 转派说明
     * @param userId 用户ID
     */
    void delegateTask(String taskId, String targetUserId, String comment, String userId);

    /**
     * 任务委派
     * @param taskId 任务ID
     * @param targetUserId 目标用户ID
     * @param comment 委派说明
     * @param userId 用户ID
     */
    void assignTask(String taskId, String targetUserId, String comment, String userId);

    /**
     * 撤回任务
     * @param taskId 任务ID
     * @param reason 撤回原因
     * @param userId 用户ID
     */
    void withdrawTask(String taskId, String reason, String userId);

    /**
     * 加签（向前加签）
     * @param taskId 任务ID
     * @param userIds 加签用户ID列表
     * @param comment 加签说明
     * @param userId 用户ID
     */
    void addSignBefore(String taskId, List<String> userIds, String comment, String userId);

    /**
     * 加签（向后加签）
     * @param taskId 任务ID
     * @param userIds 加签用户ID列表
     * @param comment 加签说明
     * @param userId 用户ID
     */
    void addSignAfter(String taskId, List<String> userIds, String comment, String userId);

    /**
     * 减签
     * @param taskId 任务ID
     * @param userIds 减签用户ID列表
     * @param userId 用户ID
     */
    void removeSign(String taskId, List<String> userIds, String userId);

    /**
     * 抄送任务
     * @param taskId 任务ID
     * @param userIds 抄送用户ID列表
     * @param userId 用户ID
     */
    void ccTask(String taskId, List<String> userIds, String userId);

    /**
     * 获取任务表单
     * @param taskId 任务ID
     * @return 表单数据
     */
    Map<String, Object> getTaskForm(String taskId);

    /**
     * 保存任务表单
     * @param taskId 任务ID
     * @param formData 表单数据
     * @param userId 用户ID
     */
    void saveTaskForm(String taskId, Map<String, Object> formData, String userId);

    /**
     * 查询任务历史
     * @param taskId 任务ID
     * @return 任务历史列表
     */
    List<Map<String, Object>> listTaskHistory(String taskId);

    /**
     * 设置任务到期时间
     * @param taskId 任务ID
     * @param dueDate 到期时间
     */
    void setTaskDueDate(String taskId, String dueDate);

    /**
     * 会签任务
     * @param taskId 任务ID
     * @param comment 审批意见
     * @param userId 用户ID
     */
    void approveMultiInstanceTask(String taskId, String comment, String userId);

    /**
     * 或签任务 - 一人通过即可
     * @param taskId 任务ID
     * @param comment 审批意见
     * @param userId 用户ID
     */
    void approveOrSignTask(String taskId, String comment, String userId);

    /**
     * 获取会签任务的完成情况
     * @param taskId 任务ID
     * @return 会签完成情况
     */
    Map<String, Object> getMultiInstanceTaskStatus(String taskId);
}
