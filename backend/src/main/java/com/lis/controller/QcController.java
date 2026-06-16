package com.lis.controller;

import com.lis.service.QcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/qc", "/api/qc"})
@Slf4j
public class QcController {

    @Autowired
    private QcService qcService;

    @GetMapping("/evaluations")
    public ResponseEntity<List<Map<String, Object>>> listEvaluations(
            @RequestParam(required = false) Integer zkpid,
            @RequestParam(required = false) String date) {
        try {
            return ResponseEntity.ok(qcService.listEvaluations(zkpid, date));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @PostMapping("/evaluations")
    public ResponseEntity<Map<String, Object>> addEvaluation(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> result = qcService.addEvaluation(payload);
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

    @DeleteMapping("/evaluations/{id}")
    public ResponseEntity<Map<String, Object>> deleteEvaluation(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(qcService.deleteEvaluation(id));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping("/analysis")
    public ResponseEntity<Map<String, Object>> getQcAnalysis(
            @RequestParam(required = false) Integer zkpid,
            @RequestParam(required = false) Integer zkxmid,
            @RequestParam(required = false) String begDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer days) {
        try {
            return ResponseEntity.ok(qcService.getQcAnalysis(zkpid, zkxmid, begDate, endDate, days));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    @GetMapping("/processing-records")
    public ResponseEntity<List<Map<String, Object>>> listProcessingRecords(
            @RequestParam(required = false) Integer zkxmid,
            @RequestParam(required = false) String month) {
        try {
            return ResponseEntity.ok(qcService.listProcessingRecords(zkxmid, month));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @PostMapping("/processing-records")
    public ResponseEntity<Map<String, Object>> saveProcessingRecord(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> result = qcService.saveProcessingRecord(payload);
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

    @GetMapping("/qc-data")
    public ResponseEntity<List<Map<String, Object>>> getQcData(
            @RequestParam Integer zkxmid,
            @RequestParam(required = false) String begDate,
            @RequestParam(required = false) String endDate) {
        try {
            return ResponseEntity.ok(qcService.getQcData(zkxmid, begDate, endDate));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @PostMapping("/debug-export")
    public ResponseEntity<Map<String, Object>> receiveDebugInfo(@RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            log.info("========== 质控模块诊断信息 ==========");
            log.info("时间: " + payload.get("timestamp"));
            log.info("当前Tab: " + payload.get("activeTab"));
            log.info("质控品数量: " + payload.get("productListCount"));
            log.info("质控项目数量: " + payload.get("productProjectListCount"));
            log.info("日常质控数量: " + payload.get("dailyListCount"));
            log.info("质控评价数量: " + payload.get("evaluationListCount"));
            log.info("质控分析数据量: " + payload.get("analysisChartDataCount"));
            log.info("========================================");
            resp.put("success", true);
            resp.put("message", "诊断信息已接收");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            log.error("操作失败", e);
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping("/products-with-data")
    public ResponseEntity<List<Map<String, Object>>> getProductsWithData() {
        return ResponseEntity.ok(qcService.getProductsWithData());
    }

    @GetMapping("/cv-trend")
    public ResponseEntity<List<Map<String, Object>>> getCvTrend(
            @RequestParam(required = false) Integer zkxmid,
            @RequestParam(required = false) Integer zkpid,
            @RequestParam(required = false) String begDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(qcService.getCvTrend(zkxmid, zkpid, begDate, endDate));
    }

    @GetMapping("/z-score")
    public ResponseEntity<List<Map<String, Object>>> getZScore(
            @RequestParam(required = false) Integer zkxmid,
            @RequestParam(required = false) Integer zkpid,
            @RequestParam(required = false) String begDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(qcService.getZScoreData(zkxmid, zkpid, begDate, endDate));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getQcStats(
            @RequestParam(required = false) Integer zkxmid,
            @RequestParam(required = false) Integer zkpid,
            @RequestParam(required = false) String begDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(qcService.getQcStats(zkxmid, zkpid, begDate, endDate));
    }

    @GetMapping("/z-score-multi")
    public ResponseEntity<List<Map<String, Object>>> getZScoreMulti(
            @RequestParam Integer zkpid,
            @RequestParam(required = false) String begDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(qcService.getZScoreMultiData(zkpid, begDate, endDate));
    }

    @GetMapping("/export-analysis")
    public ResponseEntity<byte[]> exportAnalysis(
            @RequestParam(required = false) Integer zkpid,
            @RequestParam(required = false) Integer zkxmid,
            @RequestParam(required = false) String begDate,
            @RequestParam(required = false) String endDate) {
        String csv = qcService.exportAnalysisCsv(zkpid, zkxmid, begDate, endDate);
        byte[] bytes = ('\uFEFF' + csv).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qc_analysis.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }
}
