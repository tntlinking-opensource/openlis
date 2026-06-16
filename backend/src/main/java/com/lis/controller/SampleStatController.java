package com.lis.controller;

import com.lis.service.BarcodeService;
import com.lis.service.SampleService;
import com.lis.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/sample", "/api/sample"})
@Slf4j
public class SampleStatController {

    @Autowired
    private SampleService sampleService;

    @Autowired
    private BarcodeService barcodeService;

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/stats/progress")
    public ResponseEntity<Map<String, Object>> getProgressStats(@RequestParam(required = false) String date) {
        try {
            return ResponseEntity.ok(sampleService.getProgressStats(date));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping("/daily-workload")
    public ResponseEntity<Map<String, Object>> getDailyWorkload(
            @RequestParam(required = false) String beginDate,
            @RequestParam(required = false) String endDate) {
        try {
            return ResponseEntity.ok(sampleService.getDailyWorkload(beginDate, endDate));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping("/sample/issues")
    public ResponseEntity<List<Map<String, Object>>> getSampleIssues(@RequestParam(required = false) String date) {
        try {
            return ResponseEntity.ok(sampleService.getSampleIssues(date));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @PostMapping("/sample/handle")
    public ResponseEntity<Map<String, Object>> handleSampleIssue(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> result = sampleService.handleSampleIssue(payload);
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping("/print-label/{brxxId}")
    public ResponseEntity<Map<String, Object>> printLabel(@PathVariable Integer brxxId) {
        return ResponseEntity.ok(barcodeService.printLabelSingle(brxxId));
    }

    @PostMapping("/batch-labels")
    public ResponseEntity<Map<String, Object>> batchLabels(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Integer> brxxIds = (List<Integer>) params.get("brxxIds");
        return ResponseEntity.ok(barcodeService.printLabel(brxxIds));
    }

    @GetMapping("/stats/processRate")
    public ResponseEntity<Map<String, Object>> getProcessRate(@RequestParam(required = false) String date) {
        try {
            return ResponseEntity.ok(statisticsService.getProcessRate(date));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}
