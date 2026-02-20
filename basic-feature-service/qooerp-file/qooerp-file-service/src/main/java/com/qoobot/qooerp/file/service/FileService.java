package com.qoobot.qooerp.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.file.dto.*;
import com.qoobot.qooerp.file.entity.FileInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件服务接口
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
public interface FileService extends IService<FileInfo> {

    /**
     * 单文件上传
     *
     * @param file 文件
     * @param folderId 文件夹ID
     * @return 文件信息
     */
    FileInfoDTO upload(MultipartFile file, Long folderId) throws IOException;

    /**
     * 批量文件上传
     *
     * @param files 文件列表
     * @param folderId 文件夹ID
     * @return 文件信息列表
     */
    java.util.List<FileInfoDTO> batchUpload(java.util.List<MultipartFile> files, Long folderId) throws IOException;

    /**
     * 初始化分片上传
     *
     * @param dto 分片上传初始化DTO
     * @return 任务ID
     */
    String initChunkUpload(ChunkUploadInitDTO dto);

    /**
     * 上传文件分片
     *
     * @param taskId 任务ID
     * @param chunkIndex 分片索引
     * @param file 分片文件
     */
    void uploadChunk(String taskId, Integer chunkIndex, MultipartFile file) throws IOException;

    /**
     * 完成分片上传
     *
     * @param dto 分片上传完成DTO
     * @return 文件信息
     */
    FileInfoDTO completeChunkUpload(ChunkUploadCompleteDTO dto) throws IOException;

    /**
     * 秒传（根据MD5查找已存在的文件）
     *
     * @param fileMd5 文件MD5
     * @return 文件信息
     */
    FileInfoDTO secondPassUpload(String fileMd5);

    /**
     * 文件下载
     *
     * @param id 文件ID
     * @param response HTTP响应
     */
    void download(Long id, HttpServletResponse response) throws IOException;

    /**
     * 批量下载
     *
     * @param ids 文件ID列表
     * @param response HTTP响应
     */
    void batchDownload(String ids, HttpServletResponse response) throws IOException;

    /**
     * 文件预览
     *
     * @param id 文件ID
     * @param response HTTP响应
     */
    void preview(Long id, HttpServletResponse response) throws IOException;

    /**
     * 获取缩略图
     *
     * @param id 文件ID
     * @param width 宽度
     * @param height 高度
     * @param response HTTP响应
     */
    void thumbnail(Long id, Integer width, Integer height, HttpServletResponse response) throws IOException;

    /**
     * 分页查询文件列表
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    PageResult<FileInfoDTO> list(FileQueryDTO dto);

    /**
     * 获取文件详情
     *
     * @param id 文件ID
     * @return 文件信息
     */
    FileInfoDTO getInfo(Long id);

    /**
     * 文件重命名
     *
     * @param id 文件ID
     * @param dto 重命名DTO
     */
    void rename(Long id, FileRenameDTO dto);

    /**
     * 删除文件
     *
     * @param id 文件ID
     */
    void delete(Long id);

    /**
     * 批量删除文件
     *
     * @param ids 文件ID列表
     */
    void batchDelete(String ids);

    /**
     * 移动文件
     *
     * @param id 文件ID
     * @param dto 移动DTO
     */
    void move(Long id, FileMoveDTO dto);
}
