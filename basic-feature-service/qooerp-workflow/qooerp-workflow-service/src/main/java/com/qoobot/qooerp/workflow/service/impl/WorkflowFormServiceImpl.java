package com.qoobot.qooerp.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.workflow.dto.FormDataDTO;
import com.qoobot.qooerp.workflow.dto.FormDTO;
import com.qoobot.qooerp.workflow.dto.FormFieldDTO;
import com.qoobot.qooerp.workflow.entity.WorkflowForm;
import com.qoobot.qooerp.workflow.entity.WorkflowFormField;
import com.qoobot.qooerp.workflow.entity.WorkflowFormRecord;
import com.qoobot.qooerp.workflow.enums.WorkflowErrorCode;
import com.qoobot.qooerp.workflow.mapper.WorkflowFormFieldMapper;
import com.qoobot.qooerp.workflow.mapper.WorkflowFormMapper;
import com.qoobot.qooerp.workflow.mapper.WorkflowFormRecordMapper;
import com.qoobot.qooerp.workflow.service.WorkflowFormService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程表单服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowFormServiceImpl implements WorkflowFormService {

    private final WorkflowFormMapper formMapper;
    private final WorkflowFormFieldMapper formFieldMapper;
    private final WorkflowFormRecordMapper formRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createForm(FormDTO formDTO) {
        try {
            // 检查编码是否存在
            LambdaQueryWrapper<WorkflowForm> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WorkflowForm::getCode, formDTO.getCode());
            if (formMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(WorkflowErrorCode.FORM_CODE_EXISTS.getCode(), WorkflowErrorCode.FORM_CODE_EXISTS.getMessage());
            }

            WorkflowForm form = new WorkflowForm();
            form.setId(java.util.UUID.randomUUID().toString());
            form.setName(formDTO.getName());
            form.setCode(formDTO.getCode());
            form.setVersion(1);
            form.setFormType(formDTO.getFormType());
            form.setFormConfig(formDTO.getFormConfig());
            form.setDescription(formDTO.getDescription());
            form.setStatus(0); // 草稿状态
            form.setTenantId(getCurrentTenantId());
            formMapper.insert(form);

            log.info("创建表单成功: formId={}, code={}", form.getId(), form.getCode());
            return form.getId();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建表单失败: code={}", formDTO.getCode(), e);
            throw new BusinessException(WorkflowErrorCode.FORM_CREATE_FAILED.getCode(), WorkflowErrorCode.FORM_CREATE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateForm(FormDTO formDTO) {
        try {
            WorkflowForm form = formMapper.selectById(formDTO.getId());
            if (form == null) {
                throw new BusinessException(WorkflowErrorCode.FORM_NOT_FOUND.getCode(), WorkflowErrorCode.FORM_NOT_FOUND.getMessage());
            }

            form.setName(formDTO.getName());
            form.setFormType(formDTO.getFormType());
            form.setFormConfig(formDTO.getFormConfig());
            form.setDescription(formDTO.getDescription());
            formMapper.updateById(form);

            log.info("更新表单成功: formId={}", form.getId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新表单失败: formId={}", formDTO.getId(), e);
            throw new BusinessException(WorkflowErrorCode.FORM_UPDATE_FAILED.getCode(), WorkflowErrorCode.FORM_UPDATE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteForm(String formId) {
        try {
            formMapper.deleteById(formId);

            // 删除关联的字段
            LambdaQueryWrapper<WorkflowFormField> fieldWrapper = new LambdaQueryWrapper<>();
            fieldWrapper.eq(WorkflowFormField::getFormId, formId);
            formFieldMapper.delete(fieldWrapper);

            log.info("删除表单成功: formId={}", formId);
        } catch (Exception e) {
            log.error("删除表单失败: formId={}", formId, e);
            throw new BusinessException(WorkflowErrorCode.FORM_DELETE_FAILED.getCode(), WorkflowErrorCode.FORM_DELETE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishForm(String formId) {
        try {
            WorkflowForm form = formMapper.selectById(formId);
            if (form == null) {
                throw new BusinessException(WorkflowErrorCode.FORM_NOT_FOUND.getCode(), WorkflowErrorCode.FORM_NOT_FOUND.getMessage());
            }

            form.setStatus(1); // 已发布
            formMapper.updateById(form);

            log.info("发布表单成功: formId={}", formId);
        } catch (Exception e) {
            log.error("发布表单失败: formId={}", formId, e);
            throw new BusinessException(WorkflowErrorCode.FORM_PUBLISH_FAILED.getCode(), WorkflowErrorCode.FORM_PUBLISH_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unpublishForm(String formId) {
        try {
            WorkflowForm form = formMapper.selectById(formId);
            if (form == null) {
                throw new BusinessException(WorkflowErrorCode.FORM_NOT_FOUND.getCode(), WorkflowErrorCode.FORM_NOT_FOUND.getMessage());
            }

            form.setStatus(2); // 已停用
            formMapper.updateById(form);

            log.info("停用表单成功: formId={}", formId);
        } catch (Exception e) {
            log.error("停用表单失败: formId={}", formId, e);
            throw new BusinessException(WorkflowErrorCode.FORM_UNPUBLISH_FAILED.getCode(), WorkflowErrorCode.FORM_UNPUBLISH_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String copyForm(String formId, String newName, String newCode) {
        try {
            WorkflowForm oldForm = formMapper.selectById(formId);
            if (oldForm == null) {
                throw new BusinessException(WorkflowErrorCode.FORM_NOT_FOUND.getCode(), WorkflowErrorCode.FORM_NOT_FOUND.getMessage());
            }

            // 复制表单
            WorkflowForm newForm = new WorkflowForm();
            newForm.setId(java.util.UUID.randomUUID().toString());
            newForm.setName(newName);
            newForm.setCode(newCode);
            newForm.setVersion(1);
            newForm.setFormType(oldForm.getFormType());
            newForm.setFormConfig(oldForm.getFormConfig());
            newForm.setDescription(oldForm.getDescription());
            newForm.setStatus(0);
            newForm.setTenantId(getCurrentTenantId());
            formMapper.insert(newForm);

            // 复制字段
            LambdaQueryWrapper<WorkflowFormField> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WorkflowFormField::getFormId, formId);
            List<WorkflowFormField> oldFields = formFieldMapper.selectList(wrapper);

            for (WorkflowFormField oldField : oldFields) {
                WorkflowFormField newField = new WorkflowFormField();
                newField.setId(java.util.UUID.randomUUID().toString());
                newField.setFormId(newForm.getId());
                newField.setFieldName(oldField.getFieldName());
                newField.setFieldType(oldField.getFieldType());
                newField.setFieldLabel(oldField.getFieldLabel());
                newField.setRequired(oldField.getRequired());
                newField.setReadonly(oldField.getReadonly());
                newField.setHidden(oldField.getHidden());
                newField.setDefaultValue(oldField.getDefaultValue());
                newField.setValidationRules(oldField.getValidationRules());
                newField.setFieldConfig(oldField.getFieldConfig());
                newField.setSort(oldField.getSort());
                formFieldMapper.insert(newField);
            }

            log.info("复制表单成功: oldFormId={}, newFormId={}", formId, newForm.getId());
            return newForm.getId();
        } catch (Exception e) {
            log.error("复制表单失败: formId={}", formId, e);
            throw new BusinessException(WorkflowErrorCode.FORM_COPY_FAILED.getCode(), WorkflowErrorCode.FORM_COPY_FAILED.getMessage());
        }
    }

    @Override
    public FormDTO getForm(String formId) {
        try {
            WorkflowForm form = formMapper.selectById(formId);
            if (form == null) {
                throw new BusinessException(WorkflowErrorCode.FORM_NOT_FOUND.getCode(), WorkflowErrorCode.FORM_NOT_FOUND.getMessage());
            }

            FormDTO dto = convertToDTO(form);

            // 查询字段列表
            LambdaQueryWrapper<WorkflowFormField> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WorkflowFormField::getFormId, formId)
                    .orderByAsc(WorkflowFormField::getSort);
            List<WorkflowFormField> fields = formFieldMapper.selectList(wrapper);

            List<FormFieldDTO> fieldDTOs = fields.stream()
                    .map(this::convertToFieldDTO)
                    .collect(Collectors.toList());
            dto.setFields(fieldDTOs);

            return dto;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询表单失败: formId={}", formId, e);
            throw new BusinessException(WorkflowErrorCode.FORM_QUERY_FAILED.getCode(), WorkflowErrorCode.FORM_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public FormDTO getFormByCode(String code) {
        try {
            LambdaQueryWrapper<WorkflowForm> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WorkflowForm::getCode, code);
            WorkflowForm form = formMapper.selectOne(wrapper);

            if (form == null) {
                throw new BusinessException(WorkflowErrorCode.FORM_NOT_FOUND.getCode(), WorkflowErrorCode.FORM_NOT_FOUND.getMessage());
            }

            return getForm(form.getId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询表单失败: code={}", code, e);
            throw new BusinessException(WorkflowErrorCode.FORM_QUERY_FAILED.getCode(), WorkflowErrorCode.FORM_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public Page<WorkflowForm> pageForms(Long current, Long size, String name, String code, Integer status) {
        try {
            LambdaQueryWrapper<WorkflowForm> wrapper = new LambdaQueryWrapper<>();

            if (name != null && !name.isEmpty()) {
                wrapper.like(WorkflowForm::getName, name);
            }
            if (code != null && !code.isEmpty()) {
                wrapper.eq(WorkflowForm::getCode, code);
            }
            if (status != null) {
                wrapper.eq(WorkflowForm::getStatus, status);
            }

            wrapper.orderByDesc(WorkflowForm::getCreateTime);

            Page<WorkflowForm> page = new Page<>(current, size);
            return formMapper.selectPage(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询表单失败", e);
            throw new BusinessException(WorkflowErrorCode.FORM_QUERY_FAILED.getCode(), WorkflowErrorCode.FORM_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public List<FormDTO> listAllForms() {
        try {
            LambdaQueryWrapper<WorkflowForm> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByDesc(WorkflowForm::getCreateTime);
            List<WorkflowForm> forms = formMapper.selectList(wrapper);

            return forms.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询所有表单失败", e);
            throw new BusinessException(WorkflowErrorCode.FORM_QUERY_FAILED.getCode(), WorkflowErrorCode.FORM_QUERY_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addFormField(FormFieldDTO formFieldDTO) {
        try {
            WorkflowFormField field = new WorkflowFormField();
            field.setId(java.util.UUID.randomUUID().toString());
            field.setFormId(formFieldDTO.getFormId());
            field.setFieldName(formFieldDTO.getFieldName());
            field.setFieldType(formFieldDTO.getFieldType());
            field.setFieldLabel(formFieldDTO.getFieldLabel());
            field.setRequired(formFieldDTO.getRequired());
            field.setReadonly(formFieldDTO.getReadonly());
            field.setHidden(formFieldDTO.getHidden());
            field.setDefaultValue(formFieldDTO.getDefaultValue());
            field.setValidationRules(formFieldDTO.getValidationRules());
            field.setFieldConfig(formFieldDTO.getFieldConfig());
            field.setSort(formFieldDTO.getSort());
            formFieldMapper.insert(field);

            log.info("添加表单字段成功: fieldId={}, fieldName={}", field.getId(), field.getFieldName());
            return field.getId();
        } catch (Exception e) {
            log.error("添加表单字段失败", e);
            throw new BusinessException(WorkflowErrorCode.FORM_FIELD_CREATE_FAILED.getCode(), WorkflowErrorCode.FORM_FIELD_CREATE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFormField(FormFieldDTO formFieldDTO) {
        try {
            WorkflowFormField field = formFieldMapper.selectById(formFieldDTO.getId());
            if (field == null) {
                throw new BusinessException(WorkflowErrorCode.FORM_FIELD_NOT_FOUND.getCode(), WorkflowErrorCode.FORM_FIELD_NOT_FOUND.getMessage());
            }

            field.setFieldName(formFieldDTO.getFieldName());
            field.setFieldLabel(formFieldDTO.getFieldLabel());
            field.setFieldType(formFieldDTO.getFieldType());
            field.setRequired(formFieldDTO.getRequired());
            field.setReadonly(formFieldDTO.getReadonly());
            field.setHidden(formFieldDTO.getHidden());
            field.setDefaultValue(formFieldDTO.getDefaultValue());
            field.setValidationRules(formFieldDTO.getValidationRules());
            field.setFieldConfig(formFieldDTO.getFieldConfig());
            field.setSort(formFieldDTO.getSort());
            formFieldMapper.updateById(field);

            log.info("更新表单字段成功: fieldId={}", field.getId());
        } catch (Exception e) {
            log.error("更新表单字段失败: fieldId={}", formFieldDTO.getId(), e);
            throw new BusinessException(WorkflowErrorCode.FORM_FIELD_UPDATE_FAILED.getCode(), WorkflowErrorCode.FORM_FIELD_UPDATE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFormField(String fieldId) {
        try {
            formFieldMapper.deleteById(fieldId);
            log.info("删除表单字段成功: fieldId={}", fieldId);
        } catch (Exception e) {
            log.error("删除表单字段失败: fieldId={}", fieldId, e);
            throw new BusinessException(WorkflowErrorCode.FORM_FIELD_DELETE_FAILED.getCode(), WorkflowErrorCode.FORM_FIELD_DELETE_FAILED.getMessage());
        }
    }

    @Override
    public List<FormFieldDTO> listFormFields(String formId) {
        try {
            LambdaQueryWrapper<WorkflowFormField> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WorkflowFormField::getFormId, formId)
                    .orderByAsc(WorkflowFormField::getSort);
            List<WorkflowFormField> fields = formFieldMapper.selectList(wrapper);

            return fields.stream()
                    .map(this::convertToFieldDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询表单字段失败: formId={}", formId, e);
            throw new BusinessException(WorkflowErrorCode.FORM_FIELD_QUERY_FAILED.getCode(), WorkflowErrorCode.FORM_FIELD_QUERY_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveFormData(FormDataDTO formDataDTO) {
        try {
            WorkflowFormRecord record = new WorkflowFormRecord();
            record.setId(java.util.UUID.randomUUID().toString());
            record.setFormId(formDataDTO.getFormId());
            record.setBusinessType(formDataDTO.getBusinessType());
            record.setBusinessId(formDataDTO.getBusinessId());
            record.setFormData(formDataDTO.getFormData());
            record.setTenantId(getCurrentTenantId());
            formRecordMapper.insert(record);

            log.info("保存表单数据成功: recordId={}", record.getId());
            return record.getId();
        } catch (Exception e) {
            log.error("保存表单数据失败", e);
            throw new BusinessException(WorkflowErrorCode.FORM_DATA_SAVE_FAILED.getCode(), WorkflowErrorCode.FORM_DATA_SAVE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFormData(String recordId, String formData) {
        try {
            WorkflowFormRecord record = formRecordMapper.selectById(recordId);
            if (record == null) {
                throw new BusinessException(WorkflowErrorCode.FORM_DATA_NOT_FOUND.getCode(), WorkflowErrorCode.FORM_DATA_NOT_FOUND.getMessage());
            }

            record.setFormData(formData);
            formRecordMapper.updateById(record);

            log.info("更新表单数据成功: recordId={}", recordId);
        } catch (Exception e) {
            log.error("更新表单数据失败: recordId={}", recordId, e);
            throw new BusinessException(WorkflowErrorCode.FORM_DATA_UPDATE_FAILED.getCode(), WorkflowErrorCode.FORM_DATA_UPDATE_FAILED.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFormData(String recordId) {
        try {
            formRecordMapper.deleteById(recordId);
            log.info("删除表单数据成功: recordId={}", recordId);
        } catch (Exception e) {
            log.error("删除表单数据失败: recordId={}", recordId, e);
            throw new BusinessException(WorkflowErrorCode.FORM_DATA_DELETE_FAILED.getCode(), WorkflowErrorCode.FORM_DATA_DELETE_FAILED.getMessage());
        }
    }

    @Override
    public FormDataDTO getFormData(String recordId) {
        try {
            WorkflowFormRecord record = formRecordMapper.selectById(recordId);
            if (record == null) {
                throw new BusinessException(WorkflowErrorCode.FORM_DATA_NOT_FOUND.getCode(), WorkflowErrorCode.FORM_DATA_NOT_FOUND.getMessage());
            }

            FormDataDTO dto = new FormDataDTO();
            dto.setId(record.getId());
            dto.setFormId(record.getFormId());
            dto.setBusinessType(record.getBusinessType());
            dto.setBusinessId(record.getBusinessId());
            dto.setFormData(record.getFormData());
            return dto;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询表单数据失败: recordId={}", recordId, e);
            throw new BusinessException(WorkflowErrorCode.FORM_DATA_QUERY_FAILED.getCode(), WorkflowErrorCode.FORM_DATA_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public FormDataDTO getFormDataByBusiness(String businessType, String businessId) {
        try {
            LambdaQueryWrapper<WorkflowFormRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WorkflowFormRecord::getBusinessType, businessType)
                    .eq(WorkflowFormRecord::getBusinessId, businessId);
            WorkflowFormRecord record = formRecordMapper.selectOne(wrapper);

            if (record == null) {
                throw new BusinessException(WorkflowErrorCode.FORM_DATA_NOT_FOUND.getCode(), WorkflowErrorCode.FORM_DATA_NOT_FOUND.getMessage());
            }

            return getFormData(record.getId());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询表单数据失败: businessType={}, businessId={}", businessType, businessId, e);
            throw new BusinessException(WorkflowErrorCode.FORM_DATA_QUERY_FAILED.getCode(), WorkflowErrorCode.FORM_DATA_QUERY_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> validateFormData(String formId, Map<String, Object> formData) {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("valid", true);
            result.put("errors", new ArrayList<>());

            // 查询表单字段
            List<FormFieldDTO> fields = listFormFields(formId);

            List<String> errors = new ArrayList<>();
            for (FormFieldDTO field : fields) {
                String fieldName = field.getFieldName();
                Object value = formData.get(fieldName);

                // 必填验证
                if (field.getRequired() != null && field.getRequired() == 1 && (value == null || value.toString().trim().isEmpty())) {
                    errors.add(field.getFieldLabel() + "不能为空");
                }

                // TODO: 其他验证规则
            }

            if (!errors.isEmpty()) {
                result.put("valid", false);
                result.put("errors", errors);
            }

            return result;
        } catch (Exception e) {
            log.error("验证表单数据失败: formId={}", formId, e);
            throw new BusinessException(WorkflowErrorCode.FORM_VALIDATION_FAILED.getCode(), WorkflowErrorCode.FORM_VALIDATION_FAILED.getMessage());
        }
    }

    @Override
    public Map<String, Object> getFormPermissions(String formId, String taskId) {
        try {
            // 查询表单字段
            List<FormFieldDTO> fields = listFormFields(formId);

            Map<String, Object> permissions = new HashMap<>();
            List<String> readable = new ArrayList<>();
            List<String> writable = new ArrayList<>();
            List<String> hidden = new ArrayList<>();

            for (FormFieldDTO field : fields) {
                String fieldName = field.getFieldName();

                // 基础权限判断
                if (field.getHidden() != null && field.getHidden() == 1) {
                    // 隐藏字段
                    hidden.add(fieldName);
                } else if (field.getReadonly() != null && field.getReadonly() == 1) {
                    // 只读字段
                    readable.add(fieldName);
                } else {
                    // 可编辑字段
                    writable.add(fieldName);
                    readable.add(fieldName);
                }
            }

            // TODO: 根据任务节点和角色进行更细粒度的权限控制
            // 可以从流程定义的BPMN中读取表单权限配置
            // 或者从数据库的权限配置表中读取

            permissions.put("readable", readable);
            permissions.put("writable", writable);
            permissions.put("hidden", hidden);

            return permissions;
        } catch (Exception e) {
            log.error("获取表单权限失败: formId={}, taskId={}", formId, taskId, e);
            throw new BusinessException(WorkflowErrorCode.FORM_QUERY_FAILED.getCode(), WorkflowErrorCode.FORM_QUERY_FAILED.getMessage());
        }
    }

    /**
     * 转换为DTO
     */
    private FormDTO convertToDTO(WorkflowForm form) {
        FormDTO dto = new FormDTO();
        dto.setId(form.getId());
        dto.setName(form.getName());
        dto.setCode(form.getCode());
        dto.setVersion(form.getVersion());
        dto.setFormType(form.getFormType());
        dto.setFormConfig(form.getFormConfig());
        dto.setDescription(form.getDescription());
        dto.setStatus(form.getStatus());
        return dto;
    }

    /**
     * 转换为字段DTO
     */
    private FormFieldDTO convertToFieldDTO(WorkflowFormField field) {
        FormFieldDTO dto = new FormFieldDTO();
        dto.setId(field.getId());
        dto.setFieldName(field.getFieldName());
        dto.setFieldType(field.getFieldType());
        dto.setFieldLabel(field.getFieldLabel());
        dto.setFieldConfig(field.getFieldConfig());
        dto.setDefaultValue(field.getDefaultValue());
        dto.setRequired(field.getRequired());
        dto.setReadonly(field.getReadonly());
        dto.setHidden(field.getHidden());
        dto.setValidationRules(field.getValidationRules());
        dto.setSort(field.getSort());
        return dto;
    }

    /**
     * 获取当前租户ID
     */
    private String getCurrentTenantId() {
        return "default";
    }
}
