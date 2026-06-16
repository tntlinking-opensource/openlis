package com.lis.controller;

import com.lis.dto.QueryRequest;
import com.lis.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/statistics")
@Slf4j
public class ComprehensiveStatController {

    @Autowired
    private StatisticsService statisticsService;

    @PostMapping("/comprehensive")
    public ResponseEntity<Map<String, Object>> comprehensive(@RequestBody QueryRequest params) {
        return ResponseEntity.ok(statisticsService.comprehensive(params));
    }

    @GetMapping("/by-department")
    public ResponseEntity<List<Map<String, Object>>> byDepartment(@RequestParam String beginDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticsService.byDepartment(beginDate, endDate));
    }

    @GetMapping("/by-doctor")
    public ResponseEntity<List<Map<String, Object>>> byDoctor(@RequestParam String beginDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticsService.byDoctor(beginDate, endDate));
    }

    @GetMapping("/by-item")
    public ResponseEntity<List<Map<String, Object>>> byItem(@RequestParam String beginDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticsService.byItem(beginDate, endDate));
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<Map<String, Object>>> byStatus(@RequestParam String beginDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticsService.byStatus(beginDate, endDate));
    }
}