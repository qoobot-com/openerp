package com.qoobot.qooerp.workflow.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.workflow.dto.FormDTO;
import com.qoobot.qooerp.workflow.dto.FormDataDTO;
import com.qoobot.qooerp.workflow.dto.FormFieldDTO;
import com.qoobot.qooerp.workflow.entity.WorkflowForm;
import com.qoobot.qooerp.workflow.entity.WorkflowFormRecord;

import java.util.List;
import java.util.Map;

/**
 * 流程表单服务
 */
public interface WorkflowFormService {

    /**
     * 创建表单
     * @param formDTO 表单DTO
     * @return 表单ID
     */
    String createForm(FormDTO formDTO);

    /**
     * 更新表单
     * @param formDTO 表单DTO
     */
    void updateForm(FormDTO formDTO);

    /**
     * 删除表单
     * @param formId 表单ID
     */
    void deleteForm(String formId);

    /**
     * 发布表单
     * @param formId 表单ID
     */
    void publishForm(String formId);

    /**
     * 停用表单
     * @param formId 表单ID
     */
    void unpublishForm(String formId);

    /**
     * 复制表单
     * @param formId 表单ID
     * @param newName 新名称
     * @param newCode 新编码
     * @return 新表单ID
     */
    String copyForm(String formId, String newName, String newCode);

    /**
     * 查询表单详情
     * @param formId 表单ID
     * @return 表单DTO
     */
    FormDTO getForm(String formId);

    /**
     * 根据编码查询表单
     * @param code 表单编码
     * @return 表单DTO
     */
    FormDTO getFormByCode(String code);

    /**
     * 分页查询表单列表
     * @param current 当前页
     * @param size 每页大小
     * @param name 表单名称
     * @param code 表单编码
     * @param status 表单状态
     * @return 分页结果
     */
    Page<WorkflowForm> pageForms(Long current, Long size, String name, String code, Integer status);

    /**
     * 查询所有表单
     * @return 表单列表
     */
    List<FormDTO> listAllForms();

    /**
     * 添加表单字段
     * @param formFieldDTO 表单字段DTO
     * @return 字段ID
     */
    String addFormField(FormFieldDTO formFieldDTO);

    /**
     * 更新表单字段
     * @param formFieldDTO 表单字段DTO
     */
    void updateFormField(FormFieldDTO formFieldDTO);

    /**
     * 删除表单字段
     * @param fieldId 字段ID
     */
    void deleteFormField(String fieldId);

    /**
     * 查询表单字段列表
     * @param formId 表单ID
     * @return 字段列表
     */
    List<FormFieldDTO> listFormFields(String formId);

    /**
     * 保存表单数据
     * @param formDataDTO 表单数据DTO
     * @return 记录ID
     */
    String saveFormData(FormDataDTO formDataDTO);

    /**
     * 更新表单数据
     * @param recordId 记录ID
     * @param formData 表单数据
     */
    void updateFormData(String recordId, String formData);

    /**
     * 删除表单数据
     * @param recordId 记录ID
     */
    void deleteFormData(String recordId);

    /**
     * 查询表单数据
     * @param recordId 记录ID
     * @return 表单数据
     */
    FormDataDTO getFormData(String recordId);

    /**
     * 根据业务数据查询表单数据
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 表单数据
     */
    FormDataDTO getFormDataByBusiness(String businessType, String businessId);

    /**
     * 验证表单数据
     * @param formId 表单ID
     * @param formData 表单数据
     * @return 验证结果
     */
    Map<String, Object> validateFormData(String formId, Map<String, Object> formData);

    /**
     * 获取表单权限配置
     * @param formId 表单ID
     * @param taskId 任务ID
     * @return 权限配置
     */
    Map<String, Object> getFormPermissions(String formId, String taskId);
}
