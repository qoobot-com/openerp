package com.qoobot.qooerp.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.file.dto.FileFolderCreateDTO;
import com.qoobot.qooerp.file.dto.FileFolderDTO;
import com.qoobot.qooerp.file.dto.FileFolderUpdateDTO;
import com.qoobot.qooerp.file.entity.FileFolder;

import java.util.List;

/**
 * 文件夹服务接口
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
public interface FileFolderService extends IService<FileFolder> {

    /**
     * 创建文件夹
     *
     * @param dto 创建DTO
     * @return 文件夹信息
     */
    FileFolderDTO create(FileFolderCreateDTO dto);

    /**
     * 更新文件夹
     *
     * @param id 文件夹ID
     * @param dto 更新DTO
     */
    void update(Long id, FileFolderUpdateDTO dto);

    /**
     * 删除文件夹
     *
     * @param id 文件夹ID
     */
    void delete(Long id);

    /**
     * 查询文件夹列表
     *
     * @param parentId 父文件夹ID
     * @return 文件夹列表
     */
    List<FileFolderDTO> list(Long parentId);

    /**
     * 获取文件夹树
     *
     * @param id 文件夹ID
     * @return 文件夹树
     */
    List<FileFolderDTO> getTree(Long id);
}
