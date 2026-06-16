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
public class SampleRefreshController {

    @Autowired
    private SampleService sampleService;

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(
            @RequestParam(required = false) Integer currentId,
            @RequestParam(required = false) Integer sbDjid,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer brlb) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> samples = sampleService.refreshSamples(sbDjid, date, brlb);
            result.put("success", true);
            result.put("data", samples);
            result.put("total", samples.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "刷新失败：" + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}
