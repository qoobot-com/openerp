package com.qoobot.qooerp.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.workflow.dto.ProcessDefinitionDTO;
import com.qoobot.qooerp.workflow.entity.WorkflowCategory;
import com.qoobot.qooerp.workflow.entity.WorkflowTemplate;
import com.qoobot.qooerp.workflow.enums.WorkflowErrorCode;
import com.qoobot.qooerp.workflow.mapper.WorkflowCategoryMapper;
import com.qoobot.qooerp.workflow.mapper.WorkflowTemplateMapper;
import com.qoobot.qooerp.workflow.service.WorkflowDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.image.ProcessDiagramGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.flowable.common.engine.api.io.InputStreamProvider;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程定义服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowDefinitionServiceImpl implements WorkflowDefinitionService {

    private final RepositoryService repositoryService;
    private final ProcessDiagramGenerator processDiagramGenerator;
    private final WorkflowCategoryMapper categoryMapper;
    private final WorkflowTemplateMapper templateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deployProcess(String bpmnXml, String processName, String processKey,
                                String description, String categoryId, String formId) {
        try {
            // 验证BPMN XML格式
            BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
            BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(
                new InputStreamProvider() {
                    @Override
                    public InputStream getInputStream() {
                        return new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8));
                    }
                },
                true,
                false
            );

            if (bpmnModel.getProcesses().isEmpty()) {
                throw new BusinessException(WorkflowErrorCode.PROCESS_DEPLOY_FAILED.getCode(),
                        WorkflowErrorCode.PROCESS_DEPLOY_FAILED.getMessage());
            }

            // 部署流程
            Deployment deployment = repositoryService.createDeployment()
                    .name(processName)
                    .addBytes(processKey + ".bpmn", bpmnXml.getBytes(StandardCharsets.UTF_8))
                    .deploy();

            // 获取流程定义
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .deploymentId(deployment.getId())
                    .singleResult();

            // 保存流程模板记录
            WorkflowTemplate template = new WorkflowTemplate();
            template.setId(java.util.UUID.randomUUID().toString());
            template.setProcessDefinitionId(processDefinition.getId());
            template.setProcessDefinitionKey(processKey);
            template.setCode(processKey);
            template.setName(processName);
            template.setDescription(description);
            template.setCategoryId(categoryId);
            template.setFormId(formId);
            template.setTenantId(getCurrentTenantId());
            templateMapper.insert(template);

            log.info("部署流程成功: processKey={}, processDefinitionId={}", processKey, processDefinition.getId());
            return processDefinition.getId();
        } catch (Exception e) {
            log.error("部署流程失败: processKey={}", processKey, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_DEPLOY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_DEPLOY_FAILED.getMessage());
        }
    }

    @Override
    public void publishProcess(String processDefinitionId) {
        try {
            repositoryService.activateProcessDefinitionById(processDefinitionId);
            log.info("发布流程成功: processDefinitionId={}", processDefinitionId);
        } catch (Exception e) {
            log.error("发布流程失败: processDefinitionId={}", processDefinitionId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_ACTIVATE_DEFINITION_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_ACTIVATE_DEFINITION_FAILED.getMessage());
        }
    }

    @Override
    public void suspendProcess(String processDefinitionId) {
        try {
            repositoryService.suspendProcessDefinitionById(processDefinitionId);
            log.info("停用流程成功: processDefinitionId={}", processDefinitionId);
        } catch (Exception e) {
            log.error("停用流程失败: processDefinitionId={}", processDefinitionId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_SUSPEND_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_SUSPEND_FAILED.getMessage());
        }
    }

    @Override
    public void activateProcess(String processDefinitionId) {
        publishProcess(processDefinitionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProcess(String deploymentId, boolean cascade) {
        try {
            // 删除流程定义
            repositoryService.deleteDeployment(deploymentId, cascade);

            // 删除关联的模板记录
            List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery()
                    .deploymentId(deploymentId)
                    .list();

            for (ProcessDefinition definition : definitions) {
                LambdaQueryWrapper<WorkflowTemplate> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(WorkflowTemplate::getProcessDefinitionId, definition.getId());
                templateMapper.delete(wrapper);
            }

            log.info("删除流程成功: deploymentId={}", deploymentId);
        } catch (Exception e) {
            log.error("删除流程失败: deploymentId={}", deploymentId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_DELETE_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_DELETE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String copyProcess(String processDefinitionId, String newName, String newKey) {
        try {
            // 获取原流程定义
            ProcessDefinition oldDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processDefinitionId)
                    .singleResult();

            if (oldDefinition == null) {
                throw new BusinessException(WorkflowErrorCode.PROCESS_DEFINITION_NOT_FOUND.getCode(),
                        WorkflowErrorCode.PROCESS_DEFINITION_NOT_FOUND.getMessage());
            }

            // 获取BPMN XML
            String bpmnXml = getProcessDefinitionXml(processDefinitionId);

            // 替换流程名称和Key
            bpmnXml = bpmnXml.replace("id=\"" + oldDefinition.getKey() + "\"", "id=\"" + newKey + "\"");
            bpmnXml = bpmnXml.replace("name=\"" + oldDefinition.getName() + "\"", "name=\"" + newName + "\"");

            // 部署新流程
            Deployment deployment = repositoryService.createDeployment()
                    .name(newName)
                    .addBytes(newKey + ".bpmn", bpmnXml.getBytes(StandardCharsets.UTF_8))
                    .deploy();

            ProcessDefinition newDefinition = repositoryService.createProcessDefinitionQuery()
                    .deploymentId(deployment.getId())
                    .singleResult();

            // 复制模板记录
            LambdaQueryWrapper<WorkflowTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WorkflowTemplate::getProcessDefinitionId, processDefinitionId);
            WorkflowTemplate oldTemplate = templateMapper.selectOne(wrapper);

            if (oldTemplate != null) {
                WorkflowTemplate newTemplate = new WorkflowTemplate();
                newTemplate.setId(java.util.UUID.randomUUID().toString());
                newTemplate.setProcessDefinitionId(newDefinition.getId());
                newTemplate.setProcessDefinitionKey(newKey);
                newTemplate.setName(newName);
                newTemplate.setDescription(oldTemplate.getDescription());
                newTemplate.setCategoryId(oldTemplate.getCategoryId());
                newTemplate.setFormId(oldTemplate.getFormId());
                newTemplate.setTenantId(getCurrentTenantId());
                templateMapper.insert(newTemplate);
            }

            log.info("复制流程成功: oldDefinitionId={}, newDefinitionId={}", processDefinitionId, newDefinition.getId());
            return newDefinition.getId();
        } catch (Exception e) {
            log.error("复制流程失败: processDefinitionId={}", processDefinitionId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_COPY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_COPY_FAILED.getMessage());
        }
    }

    @Override
    public String exportProcess(String processDefinitionId) {
        return getProcessDefinitionXml(processDefinitionId);
    }

    @Override
    public String importProcess(String bpmnXml) {
        try {
            Deployment deployment = repositoryService.createDeployment()
                    .addBytes("imported.bpmn", bpmnXml.getBytes(StandardCharsets.UTF_8))
                    .deploy();

            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .deploymentId(deployment.getId())
                    .singleResult();

            log.info("导入流程成功: processDefinitionId={}", processDefinition.getId());
            return processDefinition.getId();
        } catch (Exception e) {
            log.error("导入流程失败", e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_DEPLOY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_DEPLOY_FAILED.getMessage());
        }
    }

    @Override
    public ProcessDefinitionDTO getProcessDefinition(String processDefinitionId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();

        if (processDefinition == null) {
            throw new BusinessException(WorkflowErrorCode.PROCESS_DEFINITION_NOT_FOUND.getCode(),
                    WorkflowErrorCode.PROCESS_DEFINITION_NOT_FOUND.getMessage());
        }

        return convertToDTO(processDefinition);
    }

    @Override
    public PageResult<ProcessDefinitionDTO> pageProcessDefinitions(Long current, Long size,
                                                                   String processKey, String processName,
                                                                   String categoryId, Integer suspended) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion()
                .desc();

        if (processKey != null && !processKey.isEmpty()) {
            query.processDefinitionKey(processKey);
        }
        if (processName != null && !processName.isEmpty()) {
            query.processDefinitionNameLike("%" + processName + "%");
        }
        if (suspended != null) {
            if (suspended == 1) {
                query.suspended();
            } else {
                query.active();
            }
        }

        // 查询总数
        long total = query.count();

        // 分页查询
        List<ProcessDefinition> definitions = query.listPage(
                (int) ((current - 1) * size),
                (int) size.longValue()
        );

        List<ProcessDefinitionDTO> dtoList = definitions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 如果指定了分类ID，过滤结果
        if (categoryId != null && !categoryId.isEmpty()) {
            dtoList = dtoList.stream()
                    .filter(dto -> categoryId.equals(dto.getCategoryId()))
                    .collect(Collectors.toList());
        }

        return new PageResult<>(current, size, total, dtoList);
    }

    @Override
    public List<ProcessDefinitionDTO> listAllProcessDefinitions() {
        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion()
                .desc()
                .list();

        return definitions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProcessDefinitionDTO getProcessDefinitionByKey(String processKey) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .latestVersion()
                .singleResult();

        if (processDefinition == null) {
            throw new BusinessException(WorkflowErrorCode.PROCESS_DEFINITION_NOT_FOUND.getCode(),
                    WorkflowErrorCode.PROCESS_DEFINITION_NOT_FOUND.getMessage());
        }

        return convertToDTO(processDefinition);
    }

    @Override
    public List<ProcessDefinitionDTO> listProcessVersions(String processKey) {
        List<ProcessDefinition> definitions = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .orderByProcessDefinitionVersion()
                .asc()
                .list();

        return definitions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String getProcessDefinitionXml(String processDefinitionId) {
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processDefinitionId)
                    .singleResult();

            if (processDefinition == null) {
                throw new BusinessException(WorkflowErrorCode.PROCESS_DEFINITION_NOT_FOUND.getCode(),
                        WorkflowErrorCode.PROCESS_DEFINITION_NOT_FOUND.getMessage());
            }

            InputStream inputStream = repositoryService.getResourceAsStream(
                    processDefinition.getDeploymentId(),
                    processDefinition.getResourceName()
            );

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("获取流程定义XML失败: processDefinitionId={}", processDefinitionId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public byte[] getProcessDiagram(String processDefinitionId) {
        try {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processDefinitionId)
                    .singleResult();

            if (processDefinition == null) {
                throw new BusinessException(WorkflowErrorCode.PROCESS_DEFINITION_NOT_FOUND.getCode(),
                        WorkflowErrorCode.PROCESS_DEFINITION_NOT_FOUND.getMessage());
            }

            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

            try (var inputStream = processDiagramGenerator.generateDiagram(
                    bpmnModel,
                    "png",
                    new ArrayList<String>(),
                    new ArrayList<String>(),
                    false
            )) {
                return inputStream.readAllBytes();
            }
        } catch (Exception e) {
            log.error("获取流程图失败: processDefinitionId={}", processDefinitionId, e);
            throw new BusinessException(WorkflowErrorCode.PROCESS_INSTANCE_QUERY_FAILED.getCode(),
                    WorkflowErrorCode.PROCESS_INSTANCE_QUERY_FAILED.getMessage());
        }
    }

    /**
     * 转换为DTO
     */
    private ProcessDefinitionDTO convertToDTO(ProcessDefinition processDefinition) {
        ProcessDefinitionDTO dto = new ProcessDefinitionDTO();
        dto.setId(processDefinition.getId());
        dto.setKey(processDefinition.getKey());
        dto.setName(processDefinition.getName());
        dto.setVersion(processDefinition.getVersion());
        dto.setDescription(processDefinition.getDescription());
        dto.setDeploymentId(processDefinition.getDeploymentId());
        dto.setResourceName(processDefinition.getResourceName());
        dto.setSuspended(processDefinition.isSuspended() ? 1 : 0);

        // Flowable 7.0.0 中 ProcessDefinition 不再有 getDeploymentTime() 方法
        // 需要通过 Deployment 获取部署时间
        Deployment deployment = repositoryService.createDeploymentQuery()
                .deploymentId(processDefinition.getDeploymentId())
                .singleResult();
        if (deployment != null && deployment.getDeploymentTime() != null) {
            dto.setDeploymentTime(deployment.getDeploymentTime().toString());
        }

        // 查询关联的分类和表单信息
        LambdaQueryWrapper<WorkflowTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WorkflowTemplate::getProcessDefinitionId, processDefinition.getId());
        WorkflowTemplate template = templateMapper.selectOne(wrapper);

        if (template != null) {
            dto.setCategoryId(template.getCategoryId());
            dto.setFormId(template.getFormId());

            if (template.getCategoryId() != null) {
                WorkflowCategory category = categoryMapper.selectById(template.getCategoryId());
                if (category != null) {
                    dto.setCategoryName(category.getName());
                }
            }
        }

        return dto;
    }

    /**
     * 获取当前租户ID
     * TODO: 从租户上下文获取
     */
    private String getCurrentTenantId() {
        return "default";
    }
}
