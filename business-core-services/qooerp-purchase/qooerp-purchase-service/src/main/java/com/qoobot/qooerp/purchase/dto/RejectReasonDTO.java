package com.qoobot.qooerp.purchase.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class RejectReasonDTO {

    @NotBlank(message = "驳回原因不能为空")
    private String reason;
}
