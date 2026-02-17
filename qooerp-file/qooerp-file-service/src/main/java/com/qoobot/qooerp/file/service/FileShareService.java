package com.qoobot.qooerp.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.file.dto.FileShareCreateDTO;
import com.qoobot.qooerp.file.dto.FileShareDTO;
import com.qoobot.qooerp.file.entity.FileShare;
import com.qoobot.qooerp.file.dto.FileInfoDTO;

import java.util.List;

/**
 * 文件分享服务接口
 *
 * @author QooERP
 * @date 2026-02-17
 */
public interface FileShareService extends IService<FileShare> {

    /**
     * 创建分享链接
     *
     * @param fileId 文件ID
     * @param dto 创建DTO
     * @return 分享信息
     */
    FileShareDTO createShare(Long fileId, FileShareCreateDTO dto);

    /**
     * 访问分享链接
     *
     * @param code 分享码
     * @param password 密码
     * @return 文件信息
     */
    FileInfoDTO accessShare(String code, String password);

    /**
     * 获取分享信息
     *
     * @param code 分享码
     * @return 分享信息
     */
    FileShareDTO getShareInfo(String code);

    /**
     * 取消分享
     *
     * @param id 分享ID
     */
    void cancelShare(Long id);

    /**
     * 查询文件的分享列表
     *
     * @param fileId 文件ID
     * @return 分享列表
     */
    List<FileShareDTO> listShares(Long fileId);
}
