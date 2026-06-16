package com.lis.controller;

import com.lis.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tat")
@Slf4j
public class TatSettingController {

    @Autowired
    private SystemService systemService;

    @GetMapping("/settings")
    public ResponseEntity<List<Map<String, Object>>> list() {
        return ResponseEntity.ok(systemService.listTatSettings());
    }

    @PostMapping("/settings/save")
    @Transactional
    public ResponseEntity<ApiResponse> save(@RequestBody Map<String, Object> data) {
        try {
            systemService.saveTatSetting(data);
            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/settings/{sbDjid}/{brlb}/{syqk}/{zhid}")
    @Transactional
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer sbDjid, @PathVariable Integer brlb,
            @PathVariable Integer syqk, @PathVariable Integer zhid) {
        try {
            systemService.deleteTatSetting(sbDjid, brlb, syqk, zhid);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("删除失败：" + e.getMessage()));
        }
    }

    @GetMapping("/settings/auto-calculate")
    public ResponseEntity<Map<String, Object>> autoCalculate(
            @RequestParam(required = false) Integer sbDjid,
            @RequestParam(required = false) String zhmc,
            @RequestParam(defaultValue = "0") Integer buffer) {
        return ResponseEntity.ok(systemService.autoCalculateTat(sbDjid, buffer));
    }

    @lombok.Data
    public static class ApiResponse {
        private Boolean success;
        private String message;
        public static ApiResponse success(String msg) { ApiResponse r = new ApiResponse(); r.setSuccess(true); r.setMessage(msg); return r; }
        public static ApiResponse fail(String msg) { ApiResponse r = new ApiResponse(); r.setSuccess(false); r.setMessage(msg); return r; }
    }
}