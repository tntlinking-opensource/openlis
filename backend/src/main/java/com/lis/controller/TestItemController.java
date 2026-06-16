package com.lis.controller;

import com.lis.service.TestItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.lis.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/test-item")
@Slf4j
public class TestItemController {

    @Autowired
    private TestItemService testItemService;

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer sbDjid) {
        return ResponseEntity.ok(testItemService.list(keyword, sbDjid));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(@RequestParam String pym) {
        return ResponseEntity.ok(testItemService.search(pym));
    }

    @GetMapping("/search-by-pym")
    public ResponseEntity<List<Map<String, Object>>> searchByPym(
            @RequestParam String pym,
            @RequestParam(required = false) Integer sbDjid) {
        return ResponseEntity.ok(testItemService.searchByPym(pym, sbDjid));
    }

    @OperationLog(value = "保存检验项目", module = "项目设置")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> save(@RequestBody Map<String, Object> data) {
        try {
            testItemService.save(data);
            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @OperationLog(value = "删除检验项目", module = "项目设置")
    @DeleteMapping("/{xmid}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer xmid) {
        try {
            testItemService.delete(xmid);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("删除失败：" + e.getMessage()));
        }
    }

    @GetMapping("/types")
    public ResponseEntity<List<Map<String, Object>>> getTypes() {
        return ResponseEntity.ok(testItemService.getTypes());
    }

    @GetMapping("/precisions")
    public ResponseEntity<List<Map<String, Object>>> getPrecisions() {
        return ResponseEntity.ok(testItemService.getPrecisions());
    }

    @lombok.Data
    public static class ApiResponse {
        private Boolean success;
        private String message;
        public static ApiResponse success(String msg) { ApiResponse r = new ApiResponse(); r.setSuccess(true); r.setMessage(msg); return r; }
        public static ApiResponse fail(String msg) { ApiResponse r = new ApiResponse(); r.setSuccess(false); r.setMessage(msg); return r; }
    }
}