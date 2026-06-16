package com.lis.controller;

import com.lis.service.SampleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.lis.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/specimen", "/api/specimen"})
@Slf4j
public class SpecimenHandleController {

    @Autowired
    private SampleService sampleService;

    @GetMapping("/error-types")
    public ResponseEntity<List<Map<String, Object>>> getErrorTypes() {
        return ResponseEntity.ok(sampleService.getErrorTypes());
    }

    @GetMapping("/handling-measures")
    public ResponseEntity<List<Map<String, Object>>> getHandlingMeasures() {
        return ResponseEntity.ok(sampleService.getHandlingMeasures());
    }

    @OperationLog(value = "标本不合格处理", module = "样本管理")
    @PostMapping("/{id}/error-handle")
    public ResponseEntity<Map<String, Object>> errorHandle(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(sampleService.handleSpecimen(id, data));
    }

    @OperationLog(value = "报告错误处理", module = "样本管理")
    @PostMapping("/{id}/report-incorrect")
    public ResponseEntity<Map<String, Object>> reportIncorrect(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        String reason = (String) data.get("reason");
        return ResponseEntity.ok(sampleService.reportIncorrect(id, reason));
    }

    @GetMapping("/reject-records")
    public ResponseEntity<List<Map<String, Object>>> getRejectRecords(
            @RequestParam(required = false) String beginDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(sampleService.queryRejectRecords(beginDate, endDate));
    }

    @GetMapping("/{id}/reject-info")
    public ResponseEntity<Map<String, Object>> getRejectInfo(@PathVariable Integer id) {
        return ResponseEntity.ok(sampleService.getRejectInfo(id));
    }
}
