package com.lis.controller;

import com.lis.annotation.OperationLog;
import com.lis.entity.SysKssz;
import com.lis.service.BaseSettingService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/basic/dept")
@Slf4j
public class DeptSettingController {

    @Autowired
    private BaseSettingService baseSettingService;



    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "[{\"ksid\":1,\"ksdm\":\"0083\",\"ksmc\":\"检验科\"}]";
    }

    @GetMapping("/debug-sql")
    @ResponseBody
    public String debugSql() {
        return "DEBUG endpoint - not migrated";
    }

    @GetMapping("/list")
    @ResponseBody
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Boolean sybz) {
        try {
            List<Map<String, Object>> result = baseSettingService.listDepts(keyword, sybz);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < result.size(); i++) {
                if (i > 0) sb.append(",");
                Map<String, Object> row = result.get(i);
                sb.append("{");
                sb.append("\"ksid\":").append(row.get("ksid") != null ? row.get("ksid") : "null");
                sb.append(",\"ksdm\":\"").append(row.get("ksdm") != null ? row.get("ksdm") : "").append("\"");
                sb.append(",\"ksmc\":\"").append(row.get("ksmc") != null ? row.get("ksmc") : "").append("\"");
                sb.append(",\"pym\":\"").append(row.get("pym") != null ? row.get("pym") : "").append("\"");
                sb.append(",\"ksxz\":\"").append(row.get("ksxz") != null ? row.get("ksxz") : "").append("\"");
                sb.append(",\"zxbz\":").append(row.get("zxbz") != null ? row.get("zxbz") : "1");
                sb.append(",\"sybz\":").append(row.get("sybz") != null ? row.get("sybz") : "1");
                sb.append("}");
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception e) {
            log.error("操作失败", e);
            return "ERROR: " + e.getMessage();
        }
    }

    @PostMapping("/save")
    @OperationLog(value = "保存科室", module = "基本设置")
    @Transactional
    public ResponseEntity<ApiResponse> save(@RequestBody SysKssz dept) {
        try {
            baseSettingService.saveDept(dept);
            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @GetMapping("/{ksdm}")
    public ResponseEntity<Map<String, Object>> getOne(@PathVariable String ksdm) {
        try {
            Map<String, Object> result = baseSettingService.getDeptByKsdm(ksdm);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(null);
        }
    }

    @DeleteMapping("/cleanup-garbled")
    @OperationLog(value = "清理乱码科室", module = "基本设置")
    @Transactional
    public ResponseEntity<ApiResponse> cleanupGarbled() {
        try {
            baseSettingService.cleanupGarbledDepts();
            return ResponseEntity.ok(ApiResponse.success("已清理乱码记录"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("清理失败：" + e.getMessage()));
        }
    }

    @Data
    public static class ApiResponse {
        private Boolean success;
        private String message;

        public static ApiResponse success(String msg) {
            ApiResponse r = new ApiResponse();
            r.setSuccess(true);
            r.setMessage(msg);
            return r;
        }

        public static ApiResponse fail(String msg) {
            ApiResponse r = new ApiResponse();
            r.setSuccess(false);
            r.setMessage(msg);
            return r;
        }
    }
}
