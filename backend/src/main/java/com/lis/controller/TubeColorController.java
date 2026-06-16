package com.lis.controller;

import com.lis.annotation.OperationLog;
import com.lis.service.TubeColorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tube-color")
@Slf4j
public class TubeColorController {

    @Autowired
    private TubeColorService tubeColorService;

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(tubeColorService.listTubeColors(keyword));
    }

    @PostMapping("/save")
    @OperationLog(value = "保存试管颜色", module = "基本设置")
    public ResponseEntity<ApiResponse> save(@RequestBody Map<String, Object> data) {
        try {
            tubeColorService.saveTubeColor(data);
            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("")
    @OperationLog(value = "删除试管颜色", module = "基本设置")
    public ResponseEntity<ApiResponse> delete(@RequestParam String pym) {
        try {
            tubeColorService.deleteTubeColor(pym);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("删除失败：" + e.getMessage()));
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