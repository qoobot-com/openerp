package com.qoobot.qooerp.workflow.service.impl;

import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.workflow.enums.WorkflowErrorCode;
import com.qoobot.qooerp.workflow.service.WorkflowGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程网关服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowGatewayServiceImpl implements WorkflowGatewayService {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;

    @Override
    public List<Map<String, Object>> getGatewayConditions(String processDefinitionId, String gatewayId) {
        try {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            FlowElement gatewayElement = bpmnModel.getFlowElement(gatewayId);

            if (!(gatewayElement instanceof ExclusiveGateway)) {
                throw new BusinessException(WorkflowErrorCode.INVALID_GATEWAY_TYPE.getCode(),
                        "指定的网关不是条件网关");
            }

            ExclusiveGateway gateway = (ExclusiveGateway) gatewayElement;
            List<SequenceFlow> outgoingFlows = gateway.getOutgoingFlows();

            List<Map<String, Object>> conditions = new ArrayList<>();
            for (SequenceFlow flow : outgoingFlows) {
                Map<String, Object> conditionInfo = new HashMap<>();
                conditionInfo.put("flowId", flow.getId());
                conditionInfo.put("targetId", flow.getTargetRef());

                // 获取条件表达式
                String conditionExpression = flow.getConditionExpression();
                conditionInfo.put("conditionExpression", conditionExpression);
                conditionInfo.put("condition", conditionExpression);

                // 获取目标节点信息
                FlowElement targetElement = bpmnModel.getFlowElement(flow.getTargetRef());
                if (targetElement != null) {
                    conditionInfo.put("targetName", targetElement.getName());
                    conditionInfo.put("targetType", targetElement.getClass().getSimpleName());
                }

                conditions.add(conditionInfo);
            }

            return conditions;
        } catch (Exception e) {
            log.error("获取条件网关条件失败: processDefinitionId={}, gatewayId={}",
                    processDefinitionId, gatewayId, e);
            throw new BusinessException(WorkflowErrorCode.GATEWAY_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.GATEWAY_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> getGatewayDecision(String processInstanceId, String gatewayId) {
        try {
            Map<String, Object> decisionInfo = new HashMap<>();

            // 获取流程变量
            Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
            decisionInfo.put("variables", variables);

            // 获取流程定义
            org.flowable.engine.runtime.ProcessInstance processInstance =
                    runtimeService.createProcessInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .singleResult();

            if (processInstance == null) {
                throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getCode(),
                        WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getMessage());
            }

            String processDefinitionId = processInstance.getProcessDefinitionId();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

            // 获取网关信息
            FlowElement gatewayElement = bpmnModel.getFlowElement(gatewayId);
            if (!(gatewayElement instanceof ExclusiveGateway)) {
                throw new BusinessException(WorkflowErrorCode.INVALID_GATEWAY_TYPE.getCode(),
                        "指定的网关不是条件网关");
            }

            ExclusiveGateway gateway = (ExclusiveGateway) gatewayElement;
            List<SequenceFlow> outgoingFlows = gateway.getOutgoingFlows();

            // 评估每个条件
            List<Map<String, Object>> evaluatedConditions = new ArrayList<>();
            for (SequenceFlow flow : outgoingFlows) {
                Map<String, Object> evaluated = new HashMap<>();
                evaluated.put("flowId", flow.getId());
                evaluated.put("targetId", flow.getTargetRef());

                // 简单评估条件表达式
                boolean conditionMet = evaluateCondition(flow.getConditionExpression(), variables);
                evaluated.put("conditionMet", conditionMet);

                evaluatedConditions.add(evaluated);
            }

            decisionInfo.put("conditions", evaluatedConditions);
            decisionInfo.put("gatewayId", gatewayId);
            decisionInfo.put("gatewayName", gateway.getName());

            return decisionInfo;
        } catch (Exception e) {
            log.error("获取条件网关决策失败: processInstanceId={}, gatewayId={}",
                    processInstanceId, gatewayId, e);
            throw new BusinessException(WorkflowErrorCode.GATEWAY_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.GATEWAY_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public void setGatewayVariables(String processInstanceId, Map<String, Object> variables) {
        try {
            runtimeService.setVariables(processInstanceId, variables);
            log.info("设置网关变量成功: processInstanceId={}, variables={}", processInstanceId, variables);
        } catch (Exception e) {
            log.error("设置网关变量失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.VARIABLE_SET_FAILED.getCode(),
                    WorkflowErrorCode.VARIABLE_SET_FAILED.getMessage());
        }
    }

    @Override
    public List<String> previewGatewayPath(String processDefinitionId, String gatewayId,
                                             Map<String, Object> variables) {
        try {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            FlowElement gatewayElement = bpmnModel.getFlowElement(gatewayId);

            if (!(gatewayElement instanceof ExclusiveGateway)) {
                throw new BusinessException(WorkflowErrorCode.INVALID_GATEWAY_TYPE.getCode(),
                        "指定的网关不是条件网关");
            }

            ExclusiveGateway gateway = (ExclusiveGateway) gatewayElement;
            List<SequenceFlow> outgoingFlows = gateway.getOutgoingFlows();

            List<String> matchedPaths = new ArrayList<>();
            for (SequenceFlow flow : outgoingFlows) {
                boolean conditionMet = evaluateCondition(flow.getConditionExpression(), variables);
                if (conditionMet) {
                    matchedPaths.add(flow.getTargetRef());
                }
            }

            return matchedPaths;
        } catch (Exception e) {
            log.error("预览网关路径失败: processDefinitionId={}, gatewayId={}",
                    processDefinitionId, gatewayId, e);
            throw new BusinessException(WorkflowErrorCode.GATEWAY_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.GATEWAY_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> getParallelGatewayStatus(String processInstanceId, String gatewayId) {
        try {
            Map<String, Object> statusInfo = new HashMap<>();

            org.flowable.engine.runtime.ProcessInstance processInstance =
                    runtimeService.createProcessInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .singleResult();

            if (processInstance == null) {
                throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getCode(),
                        WorkflowErrorCode.PROCESS_INSTANCE_NOT_FOUND.getMessage());
            }

            String processDefinitionId = processInstance.getProcessDefinitionId();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

            FlowElement gatewayElement = bpmnModel.getFlowElement(gatewayId);
            if (!(gatewayElement instanceof ParallelGateway)) {
                throw new BusinessException(WorkflowErrorCode.INVALID_GATEWAY_TYPE.getCode(),
                        "指定的网关不是并行网关");
            }

            ParallelGateway gateway = (ParallelGateway) gatewayElement;
            List<SequenceFlow> outgoingFlows = gateway.getOutgoingFlows();

            // 获取所有分支
            List<Map<String, Object>> branches = new ArrayList<>();
            for (SequenceFlow flow : outgoingFlows) {
                Map<String, Object> branch = new HashMap<>();
                branch.put("flowId", flow.getId());
                branch.put("targetId", flow.getTargetRef());

                FlowElement targetElement = bpmnModel.getFlowElement(flow.getTargetRef());
                if (targetElement != null) {
                    branch.put("targetName", targetElement.getName());
                    branch.put("targetType", targetElement.getClass().getSimpleName());
                }

                branches.add(branch);
            }

            statusInfo.put("branches", branches);
            statusInfo.put("branchCount", branches.size());
            statusInfo.put("gatewayId", gatewayId);
            statusInfo.put("gatewayName", gateway.getName());

            // 查询并行分支的执行情况
            List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .activityType("parallelGateway")
                    .list();

            statusInfo.put("gatewayHistory", activities);

            return statusInfo;
        } catch (Exception e) {
            log.error("获取并行网关状态失败: processInstanceId={}, gatewayId={}",
                    processInstanceId, gatewayId, e);
            throw new BusinessException(WorkflowErrorCode.GATEWAY_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.GATEWAY_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getGatewayHistory(String processInstanceId) {
        try {
            List<Map<String, Object>> gatewayHistory = new ArrayList<>();

            List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .orderByHistoricActivityInstanceStartTime()
                    .asc()
                    .list();

            for (HistoricActivityInstance activity : activities) {
                String activityType = activity.getActivityType();
                if ("exclusiveGateway".equals(activityType) ||
                        "inclusiveGateway".equals(activityType) ||
                        "parallelGateway".equals(activityType)) {

                    Map<String, Object> history = new HashMap<>();
                    history.put("gatewayId", activity.getActivityId());
                    history.put("gatewayType", activityType);
                    history.put("gatewayName", activity.getActivityName());
                    history.put("startTime", activity.getStartTime());
                    history.put("endTime", activity.getEndTime());
                    history.put("duration", activity.getDurationInMillis());
                    history.put("assignee", activity.getAssignee());

                    gatewayHistory.add(history);
                }
            }

            return gatewayHistory;
        } catch (Exception e) {
            log.error("获取网关历史失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.GATEWAY_HISTORY_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.GATEWAY_HISTORY_QUERY_FAILED.getMessage());
        }
    }

    /**
     * 评估条件表达式
     */
    private boolean evaluateCondition(String conditionExpression,
                                      Map<String, Object> variables) {
        if (conditionExpression == null || conditionExpression.isEmpty()) {
            return true;  // 没有条件的默认路径
        }

        try {
            // 简单的条件表达式评估
            // 实际项目中应使用SpEL或其他表达式引擎
            return evaluateSimpleExpression(conditionExpression, variables);
        } catch (Exception e) {
            log.warn("评估条件表达式失败: {}", conditionExpression, e);
            return false;
        }
    }

    /**
     * 简单表达式评估
     * 支持基本的变量比较，如 ${amount > 1000}
     */
    private boolean evaluateSimpleExpression(String expression, Map<String, Object> variables) {
        try {
            // 移除 ${ 和 }
            String cleanExpr = expression.trim()
                    .replaceAll("^\\$\\{", "")
                    .replaceAll("}$", "");

            // 简单评估：检查变量是否存在
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                if (cleanExpr.contains(entry.getKey())) {
                    Object value = entry.getValue();
                    if (value instanceof Boolean) {
                        return (Boolean) value;
                    }
                    if (value instanceof Comparable) {
                        // 简单的比较操作
                        if (cleanExpr.contains(">")) {
                            String[] parts = cleanExpr.split(">");
                            if (parts.length == 2) {
                                String compareValue = parts[1].trim();
                                try {
                                    double num = Double.parseDouble(compareValue);
                                    double val = Double.parseDouble(value.toString());
                                    return val > num;
                                } catch (NumberFormatException e) {
                                    // 忽略数字解析错误
                                }
                            }
                        } else if (cleanExpr.contains("<")) {
                            String[] parts = cleanExpr.split("<");
                            if (parts.length == 2) {
                                String compareValue = parts[1].trim();
                                try {
                                    double num = Double.parseDouble(compareValue);
                                    double val = Double.parseDouble(value.toString());
                                    return val < num;
                                } catch (NumberFormatException e) {
                                    // 忽略数字解析错误
                                }
                            }
                        } else if (cleanExpr.contains("==")) {
                            String[] parts = cleanExpr.split("==");
                            if (parts.length == 2) {
                                String compareValue = parts[1].trim().replaceAll("'", "").replaceAll("\"", "");
                                return compareValue.equals(value.toString());
                            }
                        }
                    }
                    return true;  // 变量存在就返回true
                }
            }

            return false;
        } catch (Exception e) {
            log.warn("简单表达式评估失败: {}", expression, e);
            return false;
        }
    }
}
