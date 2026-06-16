package com.lis.controller;

import com.lis.annotation.OperationLog;
import com.lis.service.SpecimenTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/specimen-type")
@Slf4j
public class SpecimenTypeController {

    @Autowired
    private SpecimenTypeService specimenTypeService;

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(specimenTypeService.listSpecimenTypes(keyword));
    }

    @PostMapping("/save")
    @OperationLog(value = "保存标本类型", module = "基本设置")
    public ResponseEntity<ApiResponse> save(@RequestBody Map<String, Object> data) {
        try {
            specimenTypeService.saveSpecimenType(data);
            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/{bm}")
    @OperationLog(value = "删除标本类型", module = "基本设置")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer bm) {
        try {
            specimenTypeService.deleteSpecimenType(bm);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
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