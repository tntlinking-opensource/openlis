package com.lis.controller;

import com.lis.service.SampleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/sample", "/api/sample"})
@Slf4j
public class SampleHistoryController {

    @Autowired
    private SampleService sampleService;

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getHistoryRecords(@RequestParam String date) {
        List<Map<String, Object>> records = sampleService.getHistoryRecords(date);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/application")
    public ResponseEntity<List<Map<String, Object>>> getApplicationInfo(@RequestParam String date) {
        List<Map<String, Object>> records = sampleService.getApplicationInfo(date);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/warning")
    public ResponseEntity<List<Map<String, Object>>> getWarningInfo(@RequestParam String date) {
        List<Map<String, Object>> records = sampleService.getWarningInfo(date);
        return ResponseEntity.ok(records);
    }

    @PostMapping("/warning/clear")
    public ResponseEntity<Map<String, Object>> clearWarnings() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "已清除所有警示信息");
        return ResponseEntity.ok(result);
    }
}