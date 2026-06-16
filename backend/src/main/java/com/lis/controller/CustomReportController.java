package com.lis.controller;

import com.lis.dto.QueryRequest;
import com.lis.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/custom-report")
@Slf4j
public class CustomReportController {

    @Autowired
    private StatisticsService statisticsService;

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> query(@RequestBody QueryRequest params) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (params.getBeginDate() == null || params.getEndDate() == null) {
                result.put("success", false);
                result.put("message", "日期范围不能为空");
                return ResponseEntity.ok(result);
            }
            result.putAll(statisticsService.customReportQuery(params));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(defaultValue = "department") String dimension,
            @RequestParam String beginDate, @RequestParam String endDate) {
        String csv = statisticsService.exportCustomReportCsv(dimension, beginDate, endDate);
        byte[] bytes = ('\uFEFF' + csv).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=custom_report.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    @GetMapping("/detail")
    public ResponseEntity<List<Map<String, Object>>> detail(
            @RequestParam String beginDate, @RequestParam String endDate,
            @RequestParam String dimension, @RequestParam String filter) {
        return ResponseEntity.ok(statisticsService.customReportDetail(beginDate, endDate, dimension, filter));
    }

    @GetMapping("/patient-type")
    public ResponseEntity<List<Map<String, Object>>> patientType(
            @RequestParam String beginDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticsService.patientTypeWithFee(beginDate, endDate));
    }
}