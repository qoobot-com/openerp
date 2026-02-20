package com.qoobot.qooerp.workflow.service;

import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.workflow.dto.ProcessDefinitionDTO;

import java.util.List;
import java.util.Map;

/**
 * 流程定义服务
 */
public interface WorkflowDefinitionService {

    /**
     * 部署流程定义
     * @param bpmnXml BPMN XML内容
     * @param processName 流程名称
     * @param processKey 流程Key
     * @param description 描述
     * @param categoryId 分类ID
     * @param formId 表单ID
     * @return 流程定义ID
     */
    String deployProcess(String bpmnXml, String processName, String processKey,
                         String description, String categoryId, String formId);

    /**
     * 发布流程定义
     * @param processDefinitionId 流程定义ID
     */
    void publishProcess(String processDefinitionId);

    /**
     * 停用流程定义
     * @param processDefinitionId 流程定义ID
     */
    void suspendProcess(String processDefinitionId);

    /**
     * 激活流程定义
     * @param processDefinitionId 流程定义ID
     */
    void activateProcess(String processDefinitionId);

    /**
     * 删除流程定义
     * @param deploymentId 部署ID
     * @param cascade 是否级联删除
     */
    void deleteProcess(String deploymentId, boolean cascade);

    /**
     * 复制流程定义
     * @param processDefinitionId 流程定义ID
     * @param newName 新名称
     * @param newKey 新Key
     * @return 新的流程定义ID
     */
    String copyProcess(String processDefinitionId, String newName, String newKey);

    /**
     * 导出流程定义
     * @param processDefinitionId 流程定义ID
     * @return BPMN XML
     */
    String exportProcess(String processDefinitionId);

    /**
     * 导入流程定义
     * @param bpmnXml BPMN XML
     * @return 流程定义ID
     */
    String importProcess(String bpmnXml);

    /**
     * 查询流程定义详情
     * @param processDefinitionId 流程定义ID
     * @return 流程定义DTO
     */
    ProcessDefinitionDTO getProcessDefinition(String processDefinitionId);

    /**
     * 分页查询流程定义列表
     * @param current 当前页
     * @param size 每页大小
     * @param processKey 流程Key
     * @param processName 流程名称
     * @param categoryId 分类ID
     * @param suspended 是否挂起
     * @return 分页结果
     */
    PageResult<ProcessDefinitionDTO> pageProcessDefinitions(Long current, Long size,
                                                     String processKey, String processName,
                                                     String categoryId, Integer suspended);

    /**
     * 查询所有流程定义
     * @return 流程定义列表
     */
    List<ProcessDefinitionDTO> listAllProcessDefinitions();

    /**
     * 根据Key查询流程定义
     * @param processKey 流程Key
     * @return 流程定义DTO
     */
    ProcessDefinitionDTO getProcessDefinitionByKey(String processKey);

    /**
     * 查询流程定义版本列表
     * @param processKey 流程Key
     * @return 流程定义列表
     */
    List<ProcessDefinitionDTO> listProcessVersions(String processKey);

    /**
     * 获取流程定义XML
     * @param processDefinitionId 流程定义ID
     * @return BPMN XML
     */
    String getProcessDefinitionXml(String processDefinitionId);

    /**
     * 获取流程图图片
     * @param processDefinitionId 流程定义ID
     * @return 流程图字节数组
     */
    byte[] getProcessDiagram(String processDefinitionId);
}
