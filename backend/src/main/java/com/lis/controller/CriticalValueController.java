package com.lis.controller;

import com.lis.dto.CriticalValueRequest;
import com.lis.service.CriticalValueService;
import com.lis.entity.BgxtBrxx;
import com.lis.mapper.BgxtBrxxMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/critical-value")
@Slf4j
public class CriticalValueController {

    @Autowired
    private CriticalValueService criticalValueService;

    @Autowired
    private BgxtBrxxMapper bgxtBrxxMapper;

    @GetMapping("/patient-preview")
    public ResponseEntity<Map<String, Object>> patientPreview(@RequestParam Integer reportId) {
        try {
            BgxtBrxx patient = bgxtBrxxMapper.selectById(reportId);
            if (patient != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("brxx_id", patient.getBrxxId());
                result.put("brxm", patient.getBrxm());
                result.put("brxb", patient.getBrxb());
                result.put("brtmh", patient.getBrxxTmh());
                result.put("ksdm", patient.getKsdm());
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.ok(new HashMap<>());
        } catch (Exception e) {
            log.error("查询患者预览失败", e);
            return ResponseEntity.ok(new HashMap<>());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(required = false) String beginDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(criticalValueService.list(beginDate, endDate));
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> add(@RequestBody CriticalValueRequest data) {
        return ResponseEntity.ok(criticalValueService.add(data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> softDelete(@PathVariable Integer id, @RequestBody CriticalValueRequest data) {
        return ResponseEntity.ok(criticalValueService.softDelete(id, data.getCancelOperCode()));
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> statistics(
            @RequestParam String beginDate, @RequestParam String endDate) {
        return ResponseEntity.ok(criticalValueService.statistics(beginDate, endDate));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam String beginDate, @RequestParam String endDate) {
        String csv = criticalValueService.exportCsv(beginDate, endDate);
        byte[] bytes = ('\uFEFF' + csv).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=critical_value_stats.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processBatch(@RequestBody Map<String, Object> payload) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) payload.get("ids");
        String processOperName = (String) payload.getOrDefault("processOperName", "");
        return ResponseEntity.ok(criticalValueService.processBatch(ids, processOperName));
    }
}