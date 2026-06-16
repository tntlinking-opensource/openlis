package com.lis.controller;

import com.lis.annotation.OperationLog;
import com.lis.service.BaseSettingService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/basic/patient-category")
@Slf4j
public class PatientCategorySettingController {

    @Autowired
    private BaseSettingService baseSettingService;

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(
            @RequestParam(required = false) Integer brlb,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean tybz) {
        try {
            List<Map<String, Object>> result = baseSettingService.listPatientCategories(brlb, keyword, tybz);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.ok(Collections.<Map<String, Object>>emptyList());
        }
    }

    @GetMapping("/next-code")
    public ResponseEntity<Map<String, Object>> getNextCode() {
        try {
            return ResponseEntity.ok(baseSettingService.getNextPatientCategoryCode());
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> defaultMap = new HashMap<>();
            defaultMap.put("bm", 1);
            return ResponseEntity.ok(defaultMap);
        }
    }

    @PostMapping("/save")
    @OperationLog(value = "保存患者类别", module = "基本设置")
    public ResponseEntity<ApiResponse> save(@RequestBody SavePatientCategoryRequest req) {
        try {
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("bm", req.getBm());
            categoryData.put("bmsm", req.getBmsm());
            categoryData.put("pym", req.getPym());
            categoryData.put("qtdm", req.getQtdm());
            categoryData.put("sjlyfs", req.getSjlyfs());
            categoryData.put("sjlyfsms", req.getSjlyfsms());
            categoryData.put("mrksbz", req.getMrksbz());
            categoryData.put("mrksdm", req.getMrksdm());
            categoryData.put("mrksmc", req.getMrksmc());
            categoryData.put("mrysbz", req.getMrysbz());
            categoryData.put("mrysdm", req.getMrysdm());
            categoryData.put("mrysmc", req.getMrysmc());
            categoryData.put("xh", req.getXh());
            categoryData.put("tybz", req.getTybz());
            categoryData.put("jkbz", req.getJkbz());
            categoryData.put("jgxxBz", req.getJgxxBz());
            categoryData.put("jgxx", req.getJgxx());
            categoryData.put("qxkz", req.getQxkz());
            categoryData.put("brlbys", req.getBrlbys());

            baseSettingService.savePatientCategory(categoryData);
            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @Data
    public static class SavePatientCategoryRequest {
        private Integer bm;
        private String bmsm;
        private String pym;
        private String qtdm;
        private Integer sjlyfs;
        private String sjlyfsms;
        private Boolean mrksbz;
        private String mrksdm;
        private String mrksmc;
        private Boolean mrysbz;
        private String mrysdm;
        private String mrysmc;
        private Integer xh;
        private Boolean tybz;
        private Boolean jkbz;
        private Boolean jgxxBz;
        private String jgxx;
        private Boolean qxkz;
        private Integer brlbys;
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
