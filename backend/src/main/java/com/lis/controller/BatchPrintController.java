package com.lis.controller;

import com.lis.dto.BatchRequest;
import com.lis.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.lis.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/batch-print")
@Slf4j
public class BatchPrintController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/query")
    public ResponseEntity<List<Map<String, Object>>> querySamples(@RequestBody Map<String, Object> params) {
        String beginDate = (String) params.get("beginDate");
        String endDate = (String) params.get("endDate");
        Integer brlb = params.get("brlb") != null ? Integer.parseInt(params.get("brlb").toString()) : null;
        String ksdm = params.get("ksdm") != null ? params.get("ksdm").toString() : null;
        String tjdw = params.get("tjdw") != null ? params.get("tjdw").toString() : null;
        Integer sbDjid = params.get("sbDjid") != null ? Integer.parseInt(params.get("sbDjid").toString()) : null;
        return ResponseEntity.ok(reportService.queryBatchPrintSamplesWithFilters(beginDate, endDate, brlb, ksdm, tjdw, sbDjid));
    }

    @OperationLog(value = "批量打印报告", module = "报告打印")
    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> execute(@RequestBody BatchRequest params) {
        return ResponseEntity.ok(reportService.executeBatchPrint(
                params.getBrxxIds(), params.getCzydm(), params.getSkipPrinted()));
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Map<String, Object>>> getDepartments() {
        return ResponseEntity.ok(reportService.getDepartments());
    }
}