package com.qoobot.qooerp.workflow.service.impl;

import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.workflow.enums.WorkflowErrorCode;
import com.qoobot.qooerp.workflow.service.WorkflowSubProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.CallActivity;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 子流程服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowSubProcessServiceImpl implements WorkflowSubProcessService {

    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;
    private final HistoryService historyService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startSubProcess(String parentProcessInstanceId, String subProcessDefinitionKey,
                                 Map<String, Object> variables, String callActivityId, String userId) {
        try {
            // 获取父流程实例
            ProcessInstance parentProcess = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(parentProcessInstanceId)
                    .singleResult();

            if (parentProcess == null) {
                throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getCode(),
                        WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getMessage());
            }

            // 准备子流程变量
            if (variables == null) {
                variables = new HashMap<>();
            }
            variables.put("parentProcessInstanceId", parentProcessInstanceId);
            variables.put("parentBusinessKey", parentProcess.getBusinessKey());
            variables.put("callActivityId", callActivityId);
            variables.put("parentStartUserId", parentProcess.getStartUserId());

            // 启动子流程
            ProcessInstance subProcess = runtimeService.startProcessInstanceByKey(
                    subProcessDefinitionKey,
                    parentProcess.getBusinessKey() + "_sub_" + System.currentTimeMillis(),
                    variables
            );

            // 保存子流程与父流程的关联关系（使用流程变量）
            runtimeService.setVariable(subProcess.getId(), "parentProcessInstanceId", parentProcessInstanceId);
            runtimeService.setVariable(subProcess.getId(), "callActivityId", callActivityId);

            log.info("启动子流程成功: parentProcessInstanceId={}, subProcessInstanceId={}, " +
                    "subProcessDefinitionKey={}", parentProcessInstanceId, subProcess.getId(),
                    subProcessDefinitionKey);

            return subProcess.getId();
        } catch (Exception e) {
            log.error("启动子流程失败: parentProcessInstanceId={}, subProcessDefinitionKey={}",
                    parentProcessInstanceId, subProcessDefinitionKey, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_START_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_START_FAILED.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getSubProcesses(String parentProcessInstanceId) {
        try {
            List<HistoricProcessInstance> subProcesses = historyService.createHistoricProcessInstanceQuery()
                    .variableValueEquals("parentProcessInstanceId", parentProcessInstanceId)
                    .orderByProcessInstanceStartTime()
                    .desc()
                    .list();

            return subProcesses.stream()
                    .map(this::convertSubProcessToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取子流程失败: parentProcessInstanceId={}", parentProcessInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> getParentProcess(String subProcessInstanceId) {
        try {
            // 从子流程变量中获取父流程ID
            String parentProcessInstanceId = (String) runtimeService.getVariable(
                    subProcessInstanceId, "parentProcessInstanceId");

            if (parentProcessInstanceId == null) {
                // 如果运行时变量中没有，尝试从历史变量中获取
                HistoricProcessInstance historicSubProcess = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(subProcessInstanceId)
                        .singleResult();

                if (historicSubProcess != null) {
                    List<org.flowable.variable.api.history.HistoricVariableInstance> variables =
                            historyService.createHistoricVariableInstanceQuery()
                                    .processInstanceId(subProcessInstanceId)
                                    .variableName("parentProcessInstanceId")
                                    .list();

                    if (!variables.isEmpty()) {
                        parentProcessInstanceId = (String) variables.get(0).getValue();
                    }
                }
            }

            if (parentProcessInstanceId == null) {
                return null;
            }

            // 获取父流程信息
            HistoricProcessInstance parentProcess = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(parentProcessInstanceId)
                    .singleResult();

            if (parentProcess == null) {
                throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getCode(),
                        WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getMessage());
            }

            Map<String, Object> parentInfo = new HashMap<>();
            parentInfo.put("processInstanceId", parentProcess.getId());
            parentInfo.put("processDefinitionId", parentProcess.getProcessDefinitionId());
            parentInfo.put("processDefinitionKey", parentProcess.getProcessDefinitionKey());
            parentInfo.put("businessKey", parentProcess.getBusinessKey());
            parentInfo.put("startTime", parentProcess.getStartTime());
            parentInfo.put("endTime", parentProcess.getEndTime());
            parentInfo.put("duration", parentProcess.getDurationInMillis());
            parentInfo.put("startUserId", parentProcess.getStartUserId());
            parentInfo.put("status", parentProcess.getEndTime() != null ? "completed" : "running");

            return parentInfo;
        } catch (Exception e) {
            log.error("获取父流程失败: subProcessInstanceId={}", subProcessInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_QUERY_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncSubProcessVariables(String subProcessInstanceId) {
        try {
            // 获取子流程变量
            Map<String, Object> subVariables = runtimeService.getVariables(subProcessInstanceId);

            // 获取父流程ID
            String parentProcessInstanceId = (String) subVariables.get("parentProcessInstanceId");

            if (parentProcessInstanceId != null) {
                // 过滤掉内部变量
                Map<String, Object> filteredVariables = new HashMap<>();
                for (Map.Entry<String, Object> entry : subVariables.entrySet()) {
                    if (!entry.getKey().startsWith("parent")) {
                        String variableKey = "sub_" + entry.getKey();
                        filteredVariables.put(variableKey, entry.getValue());
                    }
                }

                // 同步到父流程
                runtimeService.setVariables(parentProcessInstanceId, filteredVariables);

                log.info("同步子流程变量到父流程: subProcessInstanceId={}, " +
                        "parentProcessInstanceId={}, variables={}",
                        subProcessInstanceId, parentProcessInstanceId, filteredVariables.size());
            }
        } catch (Exception e) {
            log.error("同步子流程变量失败: subProcessInstanceId={}", subProcessInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.VARIABLE_SET_FAILED.getCode(),
                    WorkflowErrorCode.VARIABLE_SET_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelSubProcess(String subProcessInstanceId, String reason) {
        try {
            ProcessInstance subProcess = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(subProcessInstanceId)
                    .singleResult();

            if (subProcess == null) {
                throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getCode(),
                        WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getMessage());
            }

            // 删除流程实例
            runtimeService.deleteProcessInstance(subProcessInstanceId, reason);

            log.info("取消子流程成功: subProcessInstanceId={}, reason={}", subProcessInstanceId, reason);
        } catch (Exception e) {
            log.error("取消子流程失败: subProcessInstanceId={}", subProcessInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_CANCEL_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_CANCEL_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> getProcessHierarchy(String processInstanceId) {
        try {
            Map<String, Object> hierarchy = new HashMap<>();
            hierarchy.put("root", buildProcessNode(processInstanceId, 0));

            // 获取所有子流程
            List<HistoricProcessInstance> allSubProcesses = historyService
                    .createHistoricProcessInstanceQuery()
                    .orderByProcessInstanceStartTime()
                    .desc()
                    .list();

            // 构建层级结构
            buildHierarchy(hierarchy, allSubProcesses);

            return hierarchy;
        } catch (Exception e) {
            log.error("获取流程层级结构失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getSubProcessHistory(String parentProcessInstanceId) {
        try {
            List<HistoricProcessInstance> subProcesses = historyService.createHistoricProcessInstanceQuery()
                    .variableValueEquals("parentProcessInstanceId", parentProcessInstanceId)
                    .orderByProcessInstanceStartTime()
                    .desc()
                    .list();

            return subProcesses.stream()
                    .map(this::convertSubProcessToMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取子流程执行历史失败: parentProcessInstanceId={}",
                    parentProcessInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_QUERY_FAILED.getMessage());
        }
    }

    /**
     * 构建流程节点
     */
    private Map<String, Object> buildProcessNode(String processInstanceId, int level) {
        HistoricProcessInstance process = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        Map<String, Object> node = new HashMap<>();
        node.put("processInstanceId", process.getId());
        node.put("processDefinitionKey", process.getProcessDefinitionKey());
        node.put("processDefinitionName", process.getProcessDefinitionName());
        node.put("businessKey", process.getBusinessKey());
        node.put("startTime", process.getStartTime());
        node.put("endTime", process.getEndTime());
        node.put("duration", process.getDurationInMillis());
        node.put("startUserId", process.getStartUserId());
        node.put("status", process.getEndTime() != null ? "completed" : "running");
        node.put("level", level);
        node.put("children", new ArrayList<>());

        return node;
    }

    /**
     * 构建层级结构
     */
    private void buildHierarchy(Map<String, Object> hierarchy,
                               List<HistoricProcessInstance> allProcesses) {
        // 按父子关系组织流程
        Map<String, List<Map<String, Object>>> parentToChildren = new HashMap<>();

        for (HistoricProcessInstance process : allProcesses) {
            String parentId = (String) historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(process.getId())
                    .variableName("parentProcessInstanceId")
                    .singleResult()
                    .getValue();

            if (parentId != null) {
                Map<String, Object> childNode = buildProcessNode(process.getId(), 0);
                parentToChildren.computeIfAbsent(parentId, k -> new ArrayList<>())
                        .add(childNode);
            }
        }

        // 递归添加子节点
        addChildren(hierarchy, parentToChildren);
    }

    /**
     * 递归添加子节点
     */
    private void addChildren(Map<String, Object> node, Map<String, List<Map<String, Object>>> parentToChildren) {
        @SuppressWarnings("unchecked")
        Map<String, Object> rootNode = (Map<String, Object>) node.get("root");
        String processId = (String) rootNode.get("processInstanceId");

        List<Map<String, Object>> children = parentToChildren.get(processId);
        if (children != null && !children.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> childList = (List<Map<String, Object>>) rootNode.get("children");
            for (Map<String, Object> child : children) {
                childList.add(child);
                addChildren(Collections.singletonMap("root", child), parentToChildren);
            }
        }
    }

    /**
     * 转换子流程信息为Map
     */
    private Map<String, Object> convertSubProcessToMap(HistoricProcessInstance process) {
        Map<String, Object> map = new HashMap<>();
        map.put("processInstanceId", process.getId());
        map.put("processDefinitionId", process.getProcessDefinitionId());
        map.put("processDefinitionKey", process.getProcessDefinitionKey());
        map.put("processDefinitionName", process.getProcessDefinitionName());
        map.put("businessKey", process.getBusinessKey());
        map.put("startTime", process.getStartTime());
        map.put("endTime", process.getEndTime());
        map.put("duration", process.getDurationInMillis());
        map.put("startUserId", process.getStartUserId());
        map.put("status", process.getEndTime() != null ? "completed" : "running");

        // 获取调用活动ID
        org.flowable.variable.api.history.HistoricVariableInstance callActivityVar =
                historyService.createHistoricVariableInstanceQuery()
                        .processInstanceId(process.getId())
                        .variableName("callActivityId")
                        .singleResult();
        if (callActivityVar != null) {
            map.put("callActivityId", callActivityVar.getValue());
        }

        return map;
    }
}
