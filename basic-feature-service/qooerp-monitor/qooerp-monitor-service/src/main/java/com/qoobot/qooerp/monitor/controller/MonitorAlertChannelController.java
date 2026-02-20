package com.qoobot.qooerp.monitor.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.monitor.dto.*;
import com.qoobot.qooerp.monitor.service.MonitorAlertChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor/alert-channel")
@RequiredArgsConstructor
public class MonitorAlertChannelController {

    private final MonitorAlertChannelService channelService;

    @PostMapping("/create")
    public Result<Long> createChannel(@Valid @RequestBody AlertChannelCreateDTO dto) {
        Long id = channelService.createChannel(dto);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    public Result<Void> updateChannel(@PathVariable Long id, @Valid @RequestBody AlertChannelUpdateDTO dto) {
        channelService.updateChannel(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteChannel(@PathVariable Long id) {
        channelService.deleteChannel(id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<AlertChannelDTO>> listChannels() {
        List<AlertChannelDTO> list = channelService.listChannels();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<AlertChannelDTO> getChannel(@PathVariable Long id) {
        AlertChannelDTO dto = channelService.getChannel(id);
        return Result.success(dto);
    }
}
