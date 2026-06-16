package com.lis.controller;

import com.lis.annotation.OperationLog;
import com.lis.service.BaseSettingService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/basic/workgroup")
@Slf4j
public class WorkgroupSettingController {

    @Autowired
    private BaseSettingService baseSettingService;

    @PostConstruct
    public void init() {
        try {
            baseSettingService.cleanupGarbledWorkgroups();
        } catch (Exception e) {
            log.error("[WorkgroupSettingController] 清理乱码失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(
            @RequestParam(required = false) String ssksdm,
            @RequestParam(required = false) String gzzdm,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer gzzlx,
            @RequestParam(required = false) Boolean sybz) {
        try {
            List<Map<String, Object>> result = baseSettingService.listWorkgroups(ssksdm, gzzdm, keyword, gzzlx, sybz);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.ok(Collections.<Map<String, Object>>emptyList());
        }
    }

    @PostMapping("/save")
    @OperationLog(value = "保存工作组", module = "基本设置")
    public ResponseEntity<ApiResponse> save(@RequestBody SaveWorkgroupRequest req) {
        try {
            Map<String, Object> workgroupData = new HashMap<>();
            workgroupData.put("gzzdm", req.getGzzdm());
            workgroupData.put("gzzmc", req.getGzzmc());
            workgroupData.put("pym", req.getPym());
            workgroupData.put("gzzlx", req.getGzzlx());
            workgroupData.put("xh", req.getXh());
            workgroupData.put("sybz", req.getSybz());

            baseSettingService.saveWorkgroup(workgroupData);
            return ResponseEntity.ok(ApiResponse.success("保存成功!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @Data
    public static class SaveWorkgroupRequest {
        private String ksdm;
        private String gzzdm;
        private String gzzmc;
        private String pym;
        private Integer gzzlx;
        private String hisKsdm;
        private Integer xh;
        private Boolean sybz;
        private Boolean xgbz;
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

        public static ApiResponse result(boolean ok, String msg) {
            ApiResponse r = new ApiResponse();
            r.setSuccess(ok);
            r.setMessage(msg);
            return r;
        }
    }
}
