package com.lis.controller;

import com.lis.annotation.OperationLog;
import com.lis.service.QcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/qc", "/api/qc"})
@Slf4j
public class QcProductController {

    @Autowired
    private QcService qcService;

    @GetMapping("/products")
    public ResponseEntity<List<Map<String, Object>>> listQcProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer sbDjid) {
        try {
            return ResponseEntity.ok(qcService.searchProducts(sbDjid, keyword));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @GetMapping("/products/{zkpid}")
    public ResponseEntity<Map<String, Object>> getQcProduct(@PathVariable Integer zkpid) {
        try {
            Map<String, Object> result = qcService.getProduct(zkpid);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/products")
    @OperationLog(value = "添加质控品", module = "质控管理")
    public ResponseEntity<Map<String, Object>> addQcProduct(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> resp = qcService.addProduct(payload);
            if (Boolean.TRUE.equals(resp.get("success"))) {
                return ResponseEntity.ok(resp);
            }
            return ResponseEntity.badRequest().body(resp);
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PutMapping("/products/{zkpid}")
    @OperationLog(value = "更新质控品", module = "质控管理")
    public ResponseEntity<Map<String, Object>> updateQcProduct(@PathVariable Integer zkpid, @RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(qcService.updateProduct(zkpid, payload));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @DeleteMapping("/products/{zkpid}")
    @OperationLog(value = "删除质控品", module = "质控管理")
    public ResponseEntity<Map<String, Object>> deleteQcProduct(@PathVariable Integer zkpid) {
        try {
            return ResponseEntity.ok(qcService.deleteProduct(zkpid));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}
