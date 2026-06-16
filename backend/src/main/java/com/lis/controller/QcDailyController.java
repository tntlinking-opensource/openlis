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
public class QcDailyController {

    @Autowired
    private QcService qcService;

    @GetMapping("/daily-results")
    public ResponseEntity<List<Map<String, Object>>> listDailyResults(
            @RequestParam(required = false) Integer zkpid,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer days) {
        try {
            return ResponseEntity.ok(qcService.listDailyResults(zkpid, date, days));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @PostMapping("/daily-results")
    @OperationLog(value = "录入质控结果", module = "质控管理")
    public ResponseEntity<Map<String, Object>> addDailyResult(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> resp = qcService.addDailyResult(payload);
            if (Boolean.TRUE.equals(resp.get("success"))) {
                return ResponseEntity.ok(resp);
            }
            return ResponseEntity.badRequest().body(resp);
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "录入失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @DeleteMapping("/daily-results/{id}")
    @OperationLog(value = "删除质控结果", module = "质控管理")
    public ResponseEntity<Map<String, Object>> deleteDailyResult(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(qcService.deleteDailyResult(id));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping("/projects")
    public ResponseEntity<List<Map<String, Object>>> listQcProjects(@RequestParam(required = false) Integer zkpid) {
        try {
            return ResponseEntity.ok(qcService.listQcProjects(zkpid));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @GetMapping("/available-projects")
    public ResponseEntity<List<Map<String, Object>>> listAvailableProjects(
            @RequestParam(required = false) Integer zkpid,
            @RequestParam(required = false) Integer sbDjid) {
        try {
            return ResponseEntity.ok(qcService.listAvailableProjects(zkpid, sbDjid));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @PostMapping("/projects")
    @OperationLog(value = "添加质控项目", module = "质控管理")
    public ResponseEntity<Map<String, Object>> addQcProject(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> resp = qcService.addQcProject(payload);
            if (Boolean.TRUE.equals(resp.get("success"))) {
                return ResponseEntity.ok(resp);
            }
            return ResponseEntity.badRequest().body(resp);
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "添加失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @PutMapping("/projects/{zkxmid}")
    @OperationLog(value = "更新质控项目", module = "质控管理")
    public ResponseEntity<Map<String, Object>> updateQcProject(@PathVariable Integer zkxmid, @RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(qcService.updateQcProject(zkxmid, payload));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @DeleteMapping("/projects/{id}")
    @OperationLog(value = "删除质控项目", module = "质控管理")
    public ResponseEntity<Map<String, Object>> deleteQcProject(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(qcService.deleteQcProject(id));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping("/all-projects")
    public ResponseEntity<List<Map<String, Object>>> listAllProjects() {
        try {
            return ResponseEntity.ok(qcService.listAllProjects());
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }
}
