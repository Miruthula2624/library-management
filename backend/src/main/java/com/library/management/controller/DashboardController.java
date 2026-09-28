package com.library.management.controller;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.DashboardResponse;
import com.library.management.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /** GET /api/dashboard — returns all library statistics in one response */
    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        DashboardResponse stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved successfully", stats));
    }
}
