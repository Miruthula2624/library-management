package com.library.management.controller;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.OverdueResponse;
import com.library.management.service.OverdueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/overdue")
public class OverdueController {

    private final OverdueService overdueService;

    public OverdueController(OverdueService overdueService) {
        this.overdueService = overdueService;
    }

    /** GET /api/overdue — all currently overdue, unreturned issues */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OverdueResponse>>> getAllOverdue() {
        List<OverdueResponse> list = overdueService.getAllOverdue();
        return ResponseEntity.ok(ApiResponse.success("Overdue issues retrieved successfully", list));
    }

    /** GET /api/overdue/{issueId} — single overdue record */
    @GetMapping("/{issueId}")
    public ResponseEntity<ApiResponse<OverdueResponse>> getOverdueById(@PathVariable Long issueId) {
        OverdueResponse response = overdueService.getOverdueById(issueId);
        return ResponseEntity.ok(ApiResponse.success("Overdue record retrieved", response));
    }

    /** GET /api/overdue/search?keyword=... — search overdue issues */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<OverdueResponse>>> searchOverdue(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        List<OverdueResponse> list = overdueService.searchOverdue(keyword);
        return ResponseEntity.ok(ApiResponse.success("Overdue search completed", list));
    }
}
