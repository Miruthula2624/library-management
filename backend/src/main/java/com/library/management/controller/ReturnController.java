package com.library.management.controller;

import com.library.management.dto.ApiResponse;
import com.library.management.dto.ReturnRequest;
import com.library.management.dto.ReturnResponse;
import com.library.management.service.ReturnService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    private final ReturnService returnService;

    public ReturnController(ReturnService returnService) {
        this.returnService = returnService;
    }

    /** GET /api/returns — list all return records */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReturnResponse>>> getAllReturns() {
        List<ReturnResponse> returns = returnService.getAllReturns();
        return ResponseEntity.ok(ApiResponse.success("Returns retrieved successfully", returns));
    }

    /** GET /api/returns/{id} — get single return record */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReturnResponse>> getReturnById(@PathVariable Long id) {
        ReturnResponse ret = returnService.getReturnById(id);
        return ResponseEntity.ok(ApiResponse.success("Return record retrieved successfully", ret));
    }

    /** GET /api/returns/search?keyword=... — search return records */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ReturnResponse>>> searchReturns(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        List<ReturnResponse> returns = returnService.searchReturns(keyword);
        return ResponseEntity.ok(ApiResponse.success("Search completed", returns));
    }

    /** POST /api/returns — process a book return */
    @PostMapping
    public ResponseEntity<ApiResponse<ReturnResponse>> createReturn(
            @Valid @RequestBody ReturnRequest request) {
        ReturnResponse ret = returnService.createReturn(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book returned successfully", ret));
    }
}
