package com.lis.controller;

import com.lis.dto.QueryRequest;
import com.lis.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/report", "/api/report"})
@Slf4j
public class ReportQueryController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> query(@RequestBody QueryRequest params) {
        return ResponseEntity.ok(reportService.queryReportList(params));
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<List<Map<String, Object>>> getResults(@PathVariable Integer id) {
        return ResponseEntity.ok(reportService.getReportResults(id));
    }

    @PostMapping("/{id}/print")
    public ResponseEntity<Map<String, Object>> print(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        String czydm = (String) params.get("czydm");
        return ResponseEntity.ok(reportService.printReport(id, czydm));
    }

    @GetMapping("/filter-options")
    public ResponseEntity<Map<String, Object>> filterOptions() {
        return ResponseEntity.ok(reportService.getFilterOptions());
    }
}