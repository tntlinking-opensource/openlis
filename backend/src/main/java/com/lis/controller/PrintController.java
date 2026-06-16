package com.lis.controller;

import com.lis.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/print")
@Slf4j
public class PrintController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/collectionList")
    public ResponseEntity<Map<String, Object>> printCollectionList(@RequestBody Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> brxxIds = (List<Integer>) payload.get("brxxIds");
            return ResponseEntity.ok(reportService.printCollectionList(brxxIds));
        } catch (Exception e) {
            log.error("打印采集列表失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
}
