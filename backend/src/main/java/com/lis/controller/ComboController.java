package com.lis.controller;

import com.lis.service.ComboService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import com.lis.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/combo")
@Slf4j
public class ComboController {

    @Autowired
    private ComboService comboService;

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(comboService.list(keyword));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(comboService.search(name));
    }

    @OperationLog(value = "保存组合项目", module = "项目设置")
    @PostMapping("/save")
    @Transactional
    public ResponseEntity<ApiResponse> save(@RequestBody Map<String, Object> data) {
        try {
            comboService.save(data);
            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @OperationLog(value = "删除组合项目", module = "项目设置")
    @DeleteMapping("/{zhid}")
    @Transactional
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer zhid) {
        try {
            comboService.delete(zhid);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("删除失败：" + e.getMessage()));
        }
    }

    @GetMapping("/{zhid}/items")
    public ResponseEntity<List<Map<String, Object>>> getItems(@PathVariable Integer zhid) {
        return ResponseEntity.ok(comboService.getItems(zhid));
    }

    @PostMapping("/{zhid}/add-item")
    public ResponseEntity<ApiResponse> addItem(@PathVariable Integer zhid, @RequestBody Map<String, Object> data) {
        try {
            comboService.addItem(zhid, data);
            return ResponseEntity.ok(ApiResponse.success("添加成功"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("添加失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/{zhid}/remove-item/{xmid}")
    public ResponseEntity<ApiResponse> removeItem(@PathVariable Integer zhid, @PathVariable Integer xmid) {
        try {
            comboService.removeItem(zhid, xmid);
            return ResponseEntity.ok(ApiResponse.success("移除成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("移除失败：" + e.getMessage()));
        }
    }

    @PutMapping("/{zhid}/reorder")
    public ResponseEntity<ApiResponse> reorder(@PathVariable Integer zhid, @RequestBody List<Integer> xmidOrder) {
        try {
            comboService.reorder(zhid, xmidOrder);
            return ResponseEntity.ok(ApiResponse.success("排序成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("排序失败：" + e.getMessage()));
        }
    }

    @PostMapping("/{zhid}/copy-from/{sourceId}")
    public ResponseEntity<ApiResponse> copyFrom(@PathVariable Integer zhid, @PathVariable Integer sourceId) {
        try {
            comboService.copyFrom(zhid, sourceId);
            return ResponseEntity.ok(ApiResponse.success("复制成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("复制失败：" + e.getMessage()));
        }
    }

    @GetMapping("/completion-settings")
    public ResponseEntity<List<Map<String, Object>>> listCompletionSettings(
            @RequestParam(required = false) Integer zhid,
            @RequestParam(required = false) Integer szlb,
            @RequestParam(required = false) Integer tybz) {
        return ResponseEntity.ok(comboService.listCompletionSettings(zhid, szlb, tybz));
    }

    @PostMapping("/completion-settings/save")
    public ResponseEntity<Map<String, Object>> saveCompletionSetting(@RequestBody Map<String, Object> data) {
        try {
            return ResponseEntity.ok(comboService.saveCompletionSetting(data));
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "保存失败：" + e.getMessage());
            return ResponseEntity.ok(err);
        }
    }

    @DeleteMapping("/completion-settings/{id}")
    public ResponseEntity<Map<String, Object>> deleteCompletionSetting(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(comboService.deleteCompletionSetting(id));
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "删除失败：" + e.getMessage());
            return ResponseEntity.ok(err);
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