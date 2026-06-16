package com.lis.controller;

import com.lis.service.ReportVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report-version")
@Slf4j
public class ReportVersionController {

    @Autowired
    private ReportVersionService reportVersionService;

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(reportVersionService.listReportVersions(keyword));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        Map<String, Object> result = reportVersionService.getReportVersionById(id);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse> save(@RequestBody Map<String, Object> data) {
        try {
            reportVersionService.saveReportVersion(data);
            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @PostMapping("/set-default/{id}")
    public ResponseEntity<ApiResponse> setDefault(@PathVariable Integer id) {
        try {
            reportVersionService.setDefaultReport(id);
            return ResponseEntity.ok(ApiResponse.success("设置成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("设置失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        try {
            reportVersionService.deleteReportVersion(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("删除失败：" + e.getMessage()));
        }
    }

    @GetMapping("/{id}/mrt")
    public ResponseEntity<Map<String, Object>> getMrtTemplate(@PathVariable Integer id) {
        try {
            String mrt = reportVersionService.getMrtTemplate(id);
            return ResponseEntity.ok(Map.of("success", true, "mrt", mrt != null ? mrt : ""));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "message", "获取失败：" + e.getMessage()));
        }
    }

    @PostMapping("/{id}/mrt")
    public ResponseEntity<ApiResponse> saveMrtTemplate(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            String mrtJson = body.get("mrt");
            reportVersionService.saveMrtTemplate(id, mrtJson);
            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @lombok.Data
    public static class ApiResponse {
        private Boolean success;
        private String message;
        public static ApiResponse success(String msg) { ApiResponse r = new ApiResponse(); r.setSuccess(true); r.setMessage(msg); return r; }
        public static ApiResponse fail(String msg) { ApiResponse r = new ApiResponse(); r.setSuccess(false); r.setMessage(msg); return r; }
    }
}