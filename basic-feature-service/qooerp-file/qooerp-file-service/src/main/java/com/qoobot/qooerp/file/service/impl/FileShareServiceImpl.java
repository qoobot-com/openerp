package com.qoobot.qooerp.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qoobot.qooerp.common.exception.BusinessException;
import com.qoobot.qooerp.common.tenant.TenantContextHolder;
import com.qoobot.qooerp.common.util.BeanUtils;
import com.qoobot.qooerp.file.dto.FileInfoDTO;
import com.qoobot.qooerp.file.dto.FileShareCreateDTO;
import com.qoobot.qooerp.file.dto.FileShareDTO;
import com.qoobot.qooerp.file.entity.FileInfo;
import com.qoobot.qooerp.file.entity.FileShare;
import com.qoobot.qooerp.file.mapper.FileShareMapper;
import com.qoobot.qooerp.file.service.FileService;
import com.qoobot.qooerp.file.service.FileShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件分享服务实现
 *
 * @author QooERP
 * @date 2026-02-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileShareServiceImpl extends ServiceImpl<FileShareMapper, FileShare> implements FileShareService {

    private final FileService fileService;

    @Value("${file.share.base-url:http://localhost:8097/api/file/share}")
    private String shareBaseUrl;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileShareDTO createShare(Long fileId, FileShareCreateDTO dto) {
        FileInfo file = fileService.getById(fileId);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }

        // 生成分享码
        String shareCode = generateShareCode();

        // 计算过期时间
        LocalDateTime expireTime = dto.getExpireDays() != null ?
                LocalDateTime.now().plusDays(dto.getExpireDays()) : null;

        // 保存分享信息
        FileShare share = new FileShare();
        share.setFileId(fileId);
        share.setShareCode(shareCode);
        share.setSharePassword(encryptPassword(dto.getSharePassword()));
        share.setShareName(dto.getShareName());
        share.setShareDesc(dto.getShareDesc());
        share.setExpireTime(expireTime);
        share.setVisitCount(0);
        share.setDownloadCount(0);
        share.setStatus(1);
        share.setTenantId(TenantContextHolder.getTenantId());
        share.setCreatorId(getCurrentUserId());
        save(share);

        FileShareDTO shareDTO = convertToDTO(share);
        shareDTO.setHasPassword(dto.getSharePassword() != null && !dto.getSharePassword().isEmpty());
        shareDTO.setShareUrl(shareBaseUrl + "/" + shareCode);
        shareDTO.setFileInfo(BeanUtils.copyBean(file, FileInfoDTO.class));

        return shareDTO;
    }

    @Override
    public FileInfoDTO accessShare(String code, String password) {
        FileShare share = baseMapper.selectByShareCode(code);
        if (share == null) {
            throw new BusinessException("分享链接不存在");
        }

        if (share.getStatus() == 2) {
            throw new BusinessException("分享链接已取消");
        }

        if (share.getExpireTime() != null && share.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("分享链接已过期");
        }

        if (share.getSharePassword() != null) {
            if (!decryptPassword(share.getSharePassword()).equals(password)) {
                throw new BusinessException("分享密码错误");
            }
        }

        // 更新访问次数
        share.setVisitCount(share.getVisitCount() + 1);
        updateById(share);

        FileInfo file = fileService.getById(share.getFileId());
        return BeanUtils.copyBean(file, FileInfoDTO.class);
    }

    @Override
    public FileShareDTO getShareInfo(String code) {
        FileShare share = baseMapper.selectByShareCode(code);
        if (share == null) {
            throw new BusinessException("分享链接不存在");
        }

        FileShareDTO dto = convertToDTO(share);
        dto.setHasPassword(share.getSharePassword() != null);
        dto.setShareUrl(shareBaseUrl + "/" + code);

        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelShare(Long id) {
        FileShare share = getById(id);
        if (share == null) {
            throw new BusinessException("分享不存在");
        }

        share.setStatus(2);
        updateById(share);
    }

    @Override
    public List<FileShareDTO> listShares(Long fileId) {
        List<FileShare> shares = baseMapper.selectByFileId(fileId, TenantContextHolder.getTenantId());
        return shares.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private String generateShareCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private String encryptPassword(String password) {
        return password; // TODO: 实现密码加密
    }

    private String decryptPassword(String encryptedPassword) {
        return encryptedPassword; // TODO: 实现密码解密
    }

    private Long getCurrentUserId() {
        return 1L;
    }

    private FileShareDTO convertToDTO(FileShare share) {
        return BeanUtils.copyBean(share, FileShareDTO.class);
    }
}
