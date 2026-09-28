package com.library.management.controller;

import com.library.management.dto.IssueRequest;
import com.library.management.dto.IssueResponse;
import com.library.management.service.IssueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping
    public ResponseEntity<List<IssueResponse>> getAllIssues() {
        List<IssueResponse> issues = issueService.getAllIssues();
        return ResponseEntity.ok(issues);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssueById(@PathVariable Long id) {
        IssueResponse issue = issueService.getIssueById(id);
        return ResponseEntity.ok(issue);
    }

    @GetMapping("/search")
    public ResponseEntity<List<IssueResponse>> searchIssues(@RequestParam(required = false) String keyword) {
        List<IssueResponse> issues = issueService.searchIssues(keyword);
        return ResponseEntity.ok(issues);
    }

    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(@Valid @RequestBody IssueRequest request) {
        IssueResponse created = issueService.createIssue(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
