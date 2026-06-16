package com.lis.controller;

import com.lis.service.SampleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.lis.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/instrument", "/api/instrument"})
@Slf4j
public class InstrumentExtractController {

    @Autowired
    private SampleService sampleService;

    @OperationLog(value = "仪器提取数据", module = "仪器通信")
    @PostMapping("/extract")
    public ResponseEntity<Map<String, Object>> extract(@RequestBody Map<String, Object> params) {
        try {
            Integer sbDjid = toInt(params.get("sbDjid"));
            String beginDate = (String) params.get("beginDate");
            String czydm = (String) params.get("czydm");
            Integer bz = params.get("bz") != null ? toInt(params.get("bz")) : 1;
            String patientName = (String) params.get("patientName");
            return ResponseEntity.ok(sampleService.extractFromInstrument(sbDjid, beginDate, czydm, bz, patientName));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "提取失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/extract-status")
    public ResponseEntity<Map<String, Object>> extractStatus(@RequestParam Integer sbDjid, @RequestParam(required = false) String extractDate, @RequestParam(required = false) String patientName) {
        Map<String, Object> result = new HashMap<>();
        result.put("pending", sampleService.getExtractPendingCount(sbDjid, extractDate, patientName));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/extract-preview")
    public ResponseEntity<Map<String, Object>> extractPreview(@RequestParam Integer sbDjid, @RequestParam(required=false) String beginDate, @RequestParam(required = false) String patientName) {
        try {
            List<Map<String, Object>> data = sampleService.getExtractPreviewData(sbDjid, beginDate, patientName);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", data);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取预览失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取预览失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    private Integer toInt(Object val) {
        if (val == null) return null;
        try { return Integer.parseInt(String.valueOf(val)); } catch (Exception e) { return null; }
    }
}
