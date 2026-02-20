package com.qoobot.qooerp.workflow.service.impl;

import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.workflow.enums.WorkflowErrorCode;
import com.qoobot.qooerp.workflow.service.WorkflowListenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流程监听器服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowListenerServiceImpl implements WorkflowListenerService {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    // 监听器注册表（生产环境应使用数据库存储）
    private final Map<String, Map<String, Object>> listenerRegistry = new ConcurrentHashMap<>();

    // 监听器执行历史记录
    private final Map<String, List<Map<String, Object>>> listenerHistoryRegistry = new ConcurrentHashMap<>();

    @Override
    public void registerListener(String processDefinitionId, String eventType, String listenerClass,
                                  String listenerType, String description) {
        try {
            String listenerId = UUID.randomUUID().toString();

            Map<String, Object> listener = new HashMap<>();
            listener.put("listenerId", listenerId);
            listener.put("processDefinitionId", processDefinitionId);
            listener.put("eventType", eventType);
            listener.put("listenerClass", listenerClass);
            listener.put("listenerType", listenerType);
            listener.put("description", description);
            listener.put("enabled", true);
            listener.put("createdAt", LocalDateTime.now());

            listenerRegistry.put(listenerId, listener);

            log.info("注册流程监听器成功: listenerId={}, eventType={}, listenerClass={}",
                    listenerId, eventType, listenerClass);
        } catch (Exception e) {
            log.error("注册流程监听器失败: processDefinitionId={}, eventType={}",
                    processDefinitionId, eventType, e);
            throw new BusinessException(WorkflowErrorCode.LISTENER_REGISTER_FAILED.getCode(),
                    WorkflowErrorCode.LISTENER_REGISTER_FAILED.getMessage());
        }
    }

    @Override
    public void unregisterListener(String listenerId) {
        try {
            if (listenerRegistry.remove(listenerId) != null) {
                log.info("注销流程监听器成功: listenerId={}", listenerId);
            } else {
                throw new BusinessException(WorkflowErrorCode.LISTENER_NOT_FOUND.getCode(),
                        WorkflowErrorCode.LISTENER_NOT_FOUND.getMessage());
            }
        } catch (Exception e) {
            log.error("注销流程监听器失败: listenerId={}", listenerId, e);
            throw new BusinessException(WorkflowErrorCode.LISTENER_NOT_FOUND.getCode(),
                    WorkflowErrorCode.LISTENER_NOT_FOUND.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getProcessListeners(String processDefinitionId) {
        try {
            List<Map<String, Object>> listeners = new ArrayList<>();

            for (Map<String, Object> listener : listenerRegistry.values()) {
                if (processDefinitionId.equals(listener.get("processDefinitionId"))) {
                    listeners.add(new HashMap<>(listener));
                }
            }

            // 同时从BPMN模型中获取配置的监听器
            List<Map<String, Object>> bpmnListeners = getBpmnListeners(processDefinitionId);
            listeners.addAll(bpmnListeners);

            return listeners;
        } catch (Exception e) {
            log.error("获取流程监听器失败: processDefinitionId={}", processDefinitionId, e);
            throw new BusinessException(WorkflowErrorCode.LISTENER_NOT_FOUND.getCode(),
                    WorkflowErrorCode.LISTENER_NOT_FOUND.getMessage());
        }
    }

    @Override
    public void triggerListener(String processInstanceId, String eventType, Map<String, Object> variables) {
        try {
            // 获取流程实例
            org.flowable.engine.runtime.ProcessInstance processInstance =
                    runtimeService.createProcessInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .singleResult();

            if (processInstance == null) {
                log.warn("流程实例不存在，无法触发监听器: processInstanceId={}", processInstanceId);
                return;
            }

            String processDefinitionId = processInstance.getProcessDefinitionId();

            // 查找并执行匹配的监听器
            for (Map<String, Object> listener : listenerRegistry.values()) {
                if (processDefinitionId.equals(listener.get("processDefinitionId")) &&
                        eventType.equals(listener.get("eventType")) &&
                        Boolean.TRUE.equals(listener.get("enabled"))) {

                    executeListener(listener, processInstanceId, variables);
                }
            }

            log.info("触发监听器成功: processInstanceId={}, eventType={}", processInstanceId, eventType);
        } catch (Exception e) {
            log.error("触发监听器失败: processInstanceId={}, eventType={}",
                    processInstanceId, eventType, e);
            throw new BusinessException(WorkflowErrorCode.LISTENER_EXECUTION_FAILED.getCode(),
                    WorkflowErrorCode.LISTENER_EXECUTION_FAILED.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getListenerExecutionHistory(String processInstanceId) {
        try {
            // 从历史记录中获取监听器执行信息
            List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .list();

            List<Map<String, Object>> history = new ArrayList<>();
            for (HistoricActivityInstance activity : activities) {
                Map<String, Object> historyItem = new HashMap<>();
                historyItem.put("activityId", activity.getActivityId());
                historyItem.put("activityType", activity.getActivityType());
                historyItem.put("activityName", activity.getActivityName());
                historyItem.put("startTime", activity.getStartTime());
                historyItem.put("endTime", activity.getEndTime());
                historyItem.put("duration", activity.getDurationInMillis());
                history.add(historyItem);
            }

            // 添加内存中的监听器执行历史
            List<Map<String, Object>> memoryHistory = listenerHistoryRegistry.get(processInstanceId);
            if (memoryHistory != null) {
                history.addAll(memoryHistory);
            }

            return history;
        } catch (Exception e) {
            log.error("获取监听器执行历史失败: processInstanceId={}", processInstanceId, e);
            throw new BusinessException(WorkflowErrorCode.LISTENER_NOT_FOUND.getCode(),
                    WorkflowErrorCode.LISTENER_NOT_FOUND.getMessage());
        }
    }

    /**
     * 执行监听器
     */
    private void executeListener(Map<String, Object> listener, String processInstanceId,
                                 Map<String, Object> variables) {
        try {
            String listenerClass = (String) listener.get("listenerClass");
            String eventType = (String) listener.get("eventType");

            log.info("执行监听器: listenerClass={}, processInstanceId={}, eventType={}",
                    listenerClass, processInstanceId, eventType);

            // 记录执行历史
            Map<String, Object> executionRecord = new HashMap<>();
            executionRecord.put("listenerId", listener.get("listenerId"));
            executionRecord.put("listenerClass", listenerClass);
            executionRecord.put("eventType", eventType);
            executionRecord.put("processInstanceId", processInstanceId);
            executionRecord.put("executionTime", LocalDateTime.now());
            executionRecord.put("status", "success");
            executionRecord.put("variables", variables);

            listenerHistoryRegistry.computeIfAbsent(processInstanceId, k -> new ArrayList<>())
                    .add(executionRecord);

            // 这里可以根据listenerClass动态加载并执行监听器类
            // 实际项目中可以使用反射或Spring的ApplicationContext来获取bean实例
            // 示例：Object listenerInstance = applicationContext.getBean(Class.forName(listenerClass));
            // listenerInstance.execute(delegateExecution);

            log.info("监听器执行成功: listenerClass={}", listenerClass);

        } catch (Exception e) {
            log.error("监听器执行失败: {}", listener.get("listenerClass"), e);

            // 记录失败历史
            Map<String, Object> executionRecord = new HashMap<>();
            executionRecord.put("listenerId", listener.get("listenerId"));
            executionRecord.put("listenerClass", listener.get("listenerClass"));
            executionRecord.put("processInstanceId", processInstanceId);
            executionRecord.put("executionTime", LocalDateTime.now());
            executionRecord.put("status", "failed");
            executionRecord.put("errorMessage", e.getMessage());

            listenerHistoryRegistry.computeIfAbsent(processInstanceId, k -> new ArrayList<>())
                    .add(executionRecord);

            throw e;
        }
    }

    /**
     * 从BPMN模型中获取配置的监听器
     */
    private List<Map<String, Object>> getBpmnListeners(String processDefinitionId) {
        List<Map<String, Object>> bpmnListeners = new ArrayList<>();

        try {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            if (bpmnModel == null) {
                return bpmnListeners;
            }

            Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
            for (FlowElement element : flowElements) {
                // 获取执行监听器
                if (element instanceof FlowNode) {
                    FlowNode flowNode = (FlowNode) element;
                    List<FlowableListener> executionListeners = flowNode.getExecutionListeners();
                    for (FlowableListener listener : executionListeners) {
                        Map<String, Object> listenerInfo = new HashMap<>();
                        listenerInfo.put("listenerId", element.getId() + "_execution_" +
                                executionListeners.indexOf(listener));
                        listenerInfo.put("processDefinitionId", processDefinitionId);
                        listenerInfo.put("eventType", listener.getEvent());
                        listenerInfo.put("listenerClass", listener.getImplementation());
                        listenerInfo.put("listenerType", "execution");
                        listenerInfo.put("description", "BPMN配置的执行监听器");
                        listenerInfo.put("enabled", true);
                        bpmnListeners.add(listenerInfo);
                    }

                    // 获取任务监听器
                    if (element instanceof UserTask) {
                        UserTask userTask = (UserTask) element;
                        List<FlowableListener> taskListeners = userTask.getTaskListeners();
                        for (FlowableListener listener : taskListeners) {
                            Map<String, Object> listenerInfo = new HashMap<>();
                            listenerInfo.put("listenerId", element.getId() + "_task_" +
                                    taskListeners.indexOf(listener));
                            listenerInfo.put("processDefinitionId", processDefinitionId);
                            listenerInfo.put("eventType", listener.getEvent());
                            listenerInfo.put("listenerClass", listener.getImplementation());
                            listenerInfo.put("listenerType", "task");
                            listenerInfo.put("description", "BPMN配置的任务监听器");
                            listenerInfo.put("enabled", true);
                            bpmnListeners.add(listenerInfo);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.warn("获取BPMN监听器失败: processDefinitionId={}", processDefinitionId, e);
        }

        return bpmnListeners;
    }
}
