package com.qoobot.qooerp.finance.voucher.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qoobot.qooerp.common.response.PageResult;
import com.qoobot.qooerp.finance.voucher.domain.FinanceVoucher;
import com.qoobot.qooerp.finance.voucher.dto.FinanceVoucherDTO;
import com.qoobot.qooerp.finance.voucher.dto.FinanceVoucherQueryDTO;

import java.util.List;

/**
 * 会计凭证服务接口
 *
 * @author QooERP Team
 * @since 20xx-xx-xx
 */
public interface IFinanceVoucherService extends IService<FinanceVoucher> {

    /**
     * 分页查询凭证列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<FinanceVoucher> queryPage(FinanceVoucherQueryDTO queryDTO);

    /**
     * 创建凭证
     *
     * @param dto 凭证信息
     * @return 凭证ID
     */
    Long create(FinanceVoucherDTO dto);

    /**
     * 更新凭证
     *
     * @param dto 凭证信息
     * @return 是否成功
     */
    Boolean update(FinanceVoucherDTO dto);

    /**
     * 删除凭证
     *
     * @param id 凭证ID
     * @return 是否成功
     */
    Boolean delete(Long id);

    /**
     * 提交审核
     *
     * @param id 凭证ID
     * @return 是否成功
     */
    Boolean submit(Long id);

    /**
     * 审核凭证
     *
     * @param id 凭证ID
     * @param approved 是否通过
     * @param reviewComment 审核意见
     * @return 是否成功
     */
    Boolean review(Long id, Boolean approved, String reviewComment);

    /**
     * 记账
     *
     * @param id 凭证ID
     * @return 是否成功
     */
    Boolean posting(Long id);

    /**
     * 反记账
     *
     * @param id 凭证ID
     * @return 是否成功
     */
    Boolean unPosting(Long id);

    /**
     * 获取凭证详情（包含明细）
     *
     * @param id 凭证ID
     * @return 凭证详情
     */
    FinanceVoucher getDetail(Long id);

    /**
     * 批量生成自动凭证
     *
     * @param voucherType 凭证类型
     * @param businessType 业务类型
     * @param relatedOrderId 关联单据ID
     * @return 凭证ID
     */
    Long generateAutoVoucher(String voucherType, String businessType, Long relatedOrderId);
}
