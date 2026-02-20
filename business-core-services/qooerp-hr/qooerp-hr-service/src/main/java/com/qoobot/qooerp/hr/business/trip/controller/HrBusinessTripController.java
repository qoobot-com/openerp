package com.qoobot.qooerp.hr.business.trip.controller;

import com.qoobot.qooerp.common.response.Result;
import com.qoobot.qooerp.hr.business.trip.domain.HrBusinessTrip;
import com.qoobot.qooerp.hr.business.trip.service.IHrBusinessTripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 出差管理控制器
 *
 * @author QooERP Team
 * @since 2026-02-17
 */
@Slf4j
@RestController
@RequestMapping("/business-trip")
@RequiredArgsConstructor
@Tag(name = "出差管理", description = "出差申请与审批")
@Validated
public class HrBusinessTripController {

    private final IHrBusinessTripService businessTripService;

    @Operation(summary = "提交出差申请")
    @PostMapping
    public Result<Long> submitBusinessTrip(@Valid @RequestBody HrBusinessTrip trip) {
        Long tripId = businessTripService.submitBusinessTrip(trip);
        return Result.success(tripId);
    }

    @Operation(summary = "审批出差申请")
    @PostMapping("/approve/{tripId}")
    public Result<Boolean> approveBusinessTrip(
            @PathVariable Long tripId,
            @RequestParam Long approverId,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String comment) {
        Boolean result = businessTripService.approveBusinessTrip(tripId, approverId, approved, comment);
        return Result.success(result);
    }

    @Operation(summary = "撤销出差申请")
    @PostMapping("/cancel/{tripId}")
    public Result<Boolean> cancelBusinessTrip(@PathVariable Long tripId) {
        Boolean result = businessTripService.cancelBusinessTrip(tripId);
        return Result.success(result);
    }

    @Operation(summary = "获取出差申请详情")
    @GetMapping("/{tripId}")
    public Result<HrBusinessTrip> getBusinessTrip(@PathVariable Long tripId) {
        HrBusinessTrip trip = businessTripService.getById(tripId);
        return Result.success(trip);
    }

    @Operation(summary = "获取我的出差申请")
    @GetMapping("/my/{employeeId}")
    public Result<List<HrBusinessTrip>> getMyBusinessTrips(@PathVariable Long employeeId) {
        List<HrBusinessTrip> list = businessTripService.getMyBusinessTrips(employeeId);
        return Result.success(list);
    }

    @Operation(summary = "获取待审批的出差申请")
    @GetMapping("/pending/{approverId}")
    public Result<List<HrBusinessTrip>> getPendingApproval(@PathVariable Long approverId) {
        List<HrBusinessTrip> list = businessTripService.getPendingApproval(approverId);
        return Result.success(list);
    }
}
