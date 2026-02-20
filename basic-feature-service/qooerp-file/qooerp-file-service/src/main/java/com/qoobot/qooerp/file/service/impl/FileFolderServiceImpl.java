package com.qoobot.qooerp.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.common.util.BeanUtils;
import com.qoobot.qooerp.file.dto.FileFolderCreateDTO;
import com.qoobot.qooerp.file.dto.FileFolderDTO;
import com.qoobot.qooerp.file.dto.FileFolderUpdateDTO;
import com.qoobot.qooerp.file.entity.FileFolder;
import com.qoobot.qooerp.file.mapper.FileFolderMapper;
import com.qoobot.qooerp.file.service.FileFolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件夹服务实现
 *
 * @author QooERP
 * @date 20xx-xx-xx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileFolderServiceImpl extends ServiceImpl<FileFolderMapper, FileFolder> implements FileFolderService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileFolderDTO create(FileFolderCreateDTO dto) {
        // 检查名称是否重复
        Long count = baseMapper.checkNameExists(dto.getParentId(), dto.getFolderName(),
                TenantContextHolder.getTenantId(), null);
        if (count > 0) {
            throw new BusinessException("文件夹名称已存在");
        }

        FileFolder folder = new FileFolder();
        folder.setFolderNo(generateFolderNo());
        folder.setFolderName(dto.getFolderName());
        folder.setParentId(dto.getParentId());
        folder.setFolderPath(generateFolderPath(dto.getParentId()));
        folder.setFolderLevel(calculateFolderLevel(dto.getParentId()));
        folder.setSort(0);
        folder.setStatus(1);
        folder.setTenantId(TenantContextHolder.getTenantId());
        folder.setCreatorId(getCurrentUserId());
        save(folder);

        return convertToDTO(folder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, FileFolderUpdateDTO dto) {
        FileFolder folder = getById(id);
        if (folder == null) {
            throw new BusinessException("文件夹不存在");
        }

        folder.setFolderName(dto.getFolderName());
        if (dto.getSort() != null) {
            folder.setSort(dto.getSort());
        }
        updateById(folder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        FileFolder folder = getById(id);
        if (folder == null) {
            throw new BusinessException("文件夹不存在");
        }

        // 检查是否有子文件夹
        List<FileFolder> children = baseMapper.selectByParentId(id, TenantContextHolder.getTenantId());
        if (!children.isEmpty()) {
            throw new BusinessException("文件夹下存在子文件夹，无法删除");
        }

        // TODO: 检查是否有文件

        removeById(id);
    }

    @Override
    public List<FileFolderDTO> list(Long parentId) {
        List<FileFolder> folders = baseMapper.selectByParentId(parentId, TenantContextHolder.getTenantId());
        return folders.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<FileFolderDTO> getTree(Long id) {
        List<FileFolder> allFolders = baseMapper.selectFolderTree(TenantContextHolder.getTenantId());
        return buildTree(allFolders, id);
    }

    private String generateFolderNo() {
        return "FOLDER" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + (int)(Math.random() * 1000);
    }

    private String generateFolderPath(Long parentId) {
        if (parentId == 0) {
            return "/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        FileFolder parent = getById(parentId);
        return parent != null ? parent.getFolderPath() + "/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) : "/";
    }

    private Integer calculateFolderLevel(Long parentId) {
        if (parentId == 0) {
            return 1;
        }
        FileFolder parent = getById(parentId);
        return parent != null ? parent.getFolderLevel() + 1 : 1;
    }

    private List<FileFolderDTO> buildTree(List<FileFolder> folders, Long parentId) {
        List<FileFolderDTO> result = new ArrayList<>();
        for (FileFolder folder : folders) {
            if (folder.getParentId().equals(parentId)) {
                FileFolderDTO dto = convertToDTO(folder);
                dto.setChildren(buildTree(folders, folder.getId()));
                result.add(dto);
            }
        }
        return result;
    }

    private Long getCurrentUserId() {
        return 1L;
    }

    private FileFolderDTO convertToDTO(FileFolder folder) {
        return BeanUtils.copyBean(folder, FileFolderDTO.class);
    }
}
