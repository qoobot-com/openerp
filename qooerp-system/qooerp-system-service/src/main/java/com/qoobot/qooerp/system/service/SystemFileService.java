package com.qoobot.qooerp.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.system.entity.SystemFile;
import com.qoobot.qooerp.system.vo.SystemFileVO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件服务接口
 *
 * @author QooERP Team
 * @version 1.0.0
 */
public interface SystemFileService extends IService<SystemFile> {

    /**
     * 上传文件
     *
     * @param file   文件
     * @param module 模块名
     * @return 文件VO
     */
    SystemFileVO uploadFile(MultipartFile file, String module) throws IOException;

    /**
     * 获取文件信息
     *
     * @param id 文件ID
     * @return 文件VO
     */
    SystemFileVO getFile(Long id);

    /**
     * 删除文件
     *
     * @param id 文件ID
     * @return 是否成功
     */
    boolean deleteFile(Long id);

    /**
     * 下载文件
     *
     * @param id      文件ID
     * @param response 响应
     */
    void downloadFile(Long id, HttpServletResponse response) throws IOException;

    /**
     * 预览文件
     *
     * @param id      文件ID
     * @param response 响应
     */
    void previewFile(Long id, HttpServletResponse response) throws IOException;

    /**
     * 分页查询文件
     *
     * @param current   当前页
     * @param size      每页大小
     * @param fileName  文件名
     * @param fileExt   文件扩展名
     * @param uploader  上传人
     * @return 分页结果
     */
    Page<SystemFileVO> pageFile(Long current, Long size, String fileName, String fileExt, String uploader);
}
