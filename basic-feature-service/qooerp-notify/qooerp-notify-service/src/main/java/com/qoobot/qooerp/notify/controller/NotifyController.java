package com.qoobot.qooerp.notify.controller;

import com.qoobot.qooerp.notify.common.Result;
import com.qoobot.qooerp.notify.dto.NotifyRecordDTO;
import com.qoobot.qooerp.notify.dto.NotifySendDTO;
import com.qoobot.qooerp.notify.service.NotifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 通知控制器
 */
@Tag(name = "通知发送", description = "通知发送相关接口")
@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotifyController {

    private final NotifyService notifyService;

    @Operation(summary = "发送通知")
    @PostMapping("/send")
    public Result<NotifyRecordDTO> send(@Valid @RequestBody NotifySendDTO dto) {
        NotifyRecordDTO record = notifyService.send(dto);
        return Result.success(record);
    }

    @Operation(summary = "批量发送通知")
    @PostMapping("/sendBatch")
    public Result<Void> sendBatch(@Valid @RequestBody NotifySendDTO dto, @RequestParam String[] receivers) {
        notifyService.sendBatch(dto, receivers);
        return Result.success();
    }
}
