package com.lis.controller;

import com.lis.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tat")
@Slf4j
public class TatStatController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> statistics(
            @RequestParam String beginDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticsService.tatStatistics(beginDate, endDate));
    }

    @GetMapping("/overtime")
    public ResponseEntity<Map<String, Object>> overtime(
            @RequestParam String beginDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticsService.tatOvertime(beginDate, endDate));
    }

    @GetMapping("/trend")
    public ResponseEntity<List<Map<String, Object>>> trend(
            @RequestParam String beginDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticsService.tatTrendWithTarget(beginDate, endDate));
    }

    @GetMapping("/phase-stats")
    public ResponseEntity<List<Map<String, Object>>> phaseStats(
            @RequestParam String beginDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticsService.tatPhaseStats(beginDate, endDate));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(defaultValue = "statistics") String type,
            @RequestParam String beginDate, @RequestParam String endDate) {
        String csv = statisticsService.exportTatCsv(type, beginDate, endDate);
        byte[] bytes = ('\uFEFF' + csv).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tat_stats.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }
}