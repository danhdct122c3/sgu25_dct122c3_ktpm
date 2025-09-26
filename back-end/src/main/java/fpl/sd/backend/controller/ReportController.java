package fpl.sd.backend.controller;

import fpl.sd.backend.dto.APIResponse;
import fpl.sd.backend.dto.response.report.*;
import fpl.sd.backend.service.ReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController {
    ReportService reportService;

    @GetMapping("/daily-report")
    public APIResponse<List<DailyRevenueReportDTO>> getDailyRevenueReport() {
        return APIResponse.<List<DailyRevenueReportDTO>>builder()
                .flag(true)
                .message("OK")
                .result(reportService.getDailyRevenueReports())
                .build();
    }


    @GetMapping("/top-seller")
    public APIResponse<List<ProductPerformanceDTO>> getProductPerformance() {
        return APIResponse.<List<ProductPerformanceDTO>>builder()
                .flag(true)
                .message("OK")
                .result(reportService.getProductPerformances())
                .build();
    }


    @GetMapping("/inventory-status")
    public APIResponse<List<InventoryStatusDTO>> getInventoryStatus() {
        return APIResponse.<List<InventoryStatusDTO>>builder()
                .flag(true)
                .message("OK")
                .result(reportService.getInventoryStatus())
                .build();
    }


    @GetMapping("/top-customer")
    public APIResponse<List<CustomerSegmentationDTO>> getTopCustomer() {
        return APIResponse.<List<CustomerSegmentationDTO>>builder()
                .flag(true)
                .message("OK")
                .result(reportService.getCustomerSegmentation())
                .build();
    }

    @GetMapping("/daily-totals")
    public APIResponse<List<DailyTotalDTO>> getDailyTotals(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return APIResponse.<List<DailyTotalDTO>>builder()
                .flag(true)
                .message("OK")
                .result(reportService.getDailyTotals(startDate, endDate))
                .build();
    }

    @GetMapping("/monthly-totals")
    public APIResponse<List<MonthlyTotalDTO>> getMonthlyTotals(
            @RequestParam(value = "year") int year
    ) {
        return APIResponse.<List<MonthlyTotalDTO>>builder()
                .flag(true)
                .message("OK")
                .result(reportService.getMonthlyTotals(year))
                .build();
    }
}
