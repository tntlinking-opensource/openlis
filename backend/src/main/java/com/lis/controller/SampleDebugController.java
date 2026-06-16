package com.lis.controller;

import com.lis.service.DebugService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/sample/debug", "/api/sample/debug"})
@Slf4j
public class SampleDebugController {

    @Autowired
    private DebugService debugService;

    @GetMapping("/check-results")
    public ResponseEntity<Map<String, Object>> checkResults(@RequestParam(required = false) Integer brxxId) {
        try {
            return ResponseEntity.ok(debugService.checkResults(brxxId));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check-jyjg")
    public ResponseEntity<Map<String, Object>> checkJyjg(@RequestParam(required = false) Integer brxxId) {
        try {
            return ResponseEntity.ok(debugService.checkJyjg(brxxId));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check-xm")
    public ResponseEntity<Map<String, Object>> checkXm(@RequestParam(required = false) String xmdm) {
        try {
            return ResponseEntity.ok(debugService.checkXm(xmdm));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check-mx-table")
    public ResponseEntity<Map<String, Object>> checkMxTable(@RequestParam(required = false) Integer zhid) {
        try {
            return ResponseEntity.ok(debugService.checkMxTable(zhid));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check-tables")
    public ResponseEntity<Map<String, Object>> checkTables() {
        try {
            return ResponseEntity.ok(debugService.checkTables());
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check-ckz-data")
    public ResponseEntity<Map<String, Object>> checkCkzData(@RequestParam(required = false) Integer xmid) {
        try {
            return ResponseEntity.ok(debugService.checkCkzData(xmid));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check-ckz")
    public ResponseEntity<Map<String, Object>> checkCkz(@RequestParam(required = false) Integer xmid) {
        try {
            return ResponseEntity.ok(debugService.checkCkz(xmid));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check-combo-items")
    public ResponseEntity<Map<String, Object>> checkComboItems(@RequestParam(required = false) Integer zhid) {
        try {
            return ResponseEntity.ok(debugService.checkComboItems(zhid));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/migrate-ckz")
    public ResponseEntity<Map<String, Object>> migrateCkzFromSqlServer() {
        try {
            return ResponseEntity.ok(debugService.migrateCkz());
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/import-ckz-from-file")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> importCkzFromFile() {
        Map<String, Object> resp = new HashMap<>();
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("D:\\LIS02\\ckz_mysql_insert.sql");
            String content = new String(java.nio.file.Files.readAllBytes(path), "UTF-8");

            if (content.charAt(0) == '\uFEFF') {
                content = content.substring(1);
            }

            try {
                debugService.dropTableIfExists();
            } catch (Exception e) {}

            content = content.replaceAll("\\r?\\n", " ");
            content = content.replaceAll("\\s+", " ");

            String[] statements = content.split(";");
            int successCount = 0;
            int errorCount = 0;

            for (String stmt : statements) {
                String trimmed = stmt.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("/*")) {
                    continue;
                }
                try {
                    debugService.executeStatement(trimmed);
                    successCount++;
                } catch (Exception e) {
                    errorCount++;
                    if (errorCount < 5) {
                        log.error("SQL Error: " + trimmed.substring(0, Math.min(100, trimmed.length())) + " -> " + e.getMessage());
                    }
                }
            }

            resp.put("successCount", successCount);
            resp.put("errorCount", errorCount);

            try {
                resp.put("afterCount", debugService.getCkzCount());
            } catch (Exception e) {
                resp.put("afterCount", 0);
                resp.put("tableError", e.getMessage());
            }

            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            log.error("操作失败", e);
            resp.put("error", e.getMessage());
            return ResponseEntity.ok(resp);
        }
    }

    @PostMapping("/insert-test-samples")
    @Transactional
    public ResponseEntity<Map<String, Object>> insertTestSamples() {
        try {
            return ResponseEntity.ok(debugService.insertTestSamples());
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping("/e2e-test")
    @Transactional
    public ResponseEntity<Map<String, Object>> e2eTest() {
        try {
            return ResponseEntity.ok(debugService.e2eTest());
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("error", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping("/cleanup-by-date")
    @Transactional
    public ResponseEntity<Map<String, Object>> cleanupByDate(
            @RequestParam String startDate, @RequestParam String endDate,
            @RequestParam(required = false) List<Integer> excludeIds) {
        try {
            Map<String, Object> resp = new HashMap<>();
            List<Map<String, Object>> all = debugService.findBrxxByDateRange(startDate, endDate);
            int deletedJyjg = 0;
            int deletedBrxx = 0;
            for (Map<String, Object> row : all) {
                Integer brxxId = (Integer) row.get("brxx_id");
                if (excludeIds != null && excludeIds.contains(brxxId)) continue;
                deletedJyjg += debugService.deleteJyjgByBrxxId(brxxId);
                deletedBrxx += debugService.deleteBrxxById(brxxId);
            }
            resp.put("found", all.size());
            resp.put("deletedJyjg", deletedJyjg);
            resp.put("deletedBrxx", deletedBrxx);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("error", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping("/add-results")
    @Transactional
    public ResponseEntity<Map<String, Object>> addResultsToSample(@RequestParam Integer brxxId, @RequestBody List<Map<String, String>> results) {
        try {
            return ResponseEntity.ok(debugService.addResultsToSample(brxxId, results));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("error", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PostMapping("/clean-cjysz")
    public ResponseEntity<Map<String, Object>> cleanCjysz(@RequestParam Integer cjid) {
        try {
            int mx = debugService.deleteCjyszMxByCjid(cjid);
            int zb = debugService.deleteCjyszZbByCjid(cjid);
            Map<String, Object> resp = new HashMap<>();
            resp.put("deletedMx", mx);
            resp.put("deletedZb", zb);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("error", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}
