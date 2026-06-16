package com.lis.controller;

import com.lis.dto.MaterialFeeSyncRequest;
import com.lis.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import com.lis.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/material-fee")
@Slf4j
public class MaterialFeeController {

    @Autowired
    private SystemService systemService;

    @GetMapping("/fee-items")
    public ResponseEntity<List<Map<String, Object>>> feeItems(@RequestParam(required = false) String pym) {
        return ResponseEntity.ok(systemService.listMaterialFeeItems(pym));
    }

    @OperationLog(value = "绑定收费项目", module = "收费管理")
    @PostMapping("/bind")
    @Transactional
    public ResponseEntity<Map<String, Object>> bind(@RequestBody MaterialFeeSyncRequest data) {
        Map<String, Object> result = new HashMap<>();
        try {
            systemService.bindMaterialFee(data);
            result.put("success", true);
            result.put("message", "绑定成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "绑定失败：" + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @OperationLog(value = "解绑收费项目", module = "收费管理")
    @PostMapping("/unbind")
    @Transactional
    public ResponseEntity<Map<String, Object>> unbind(@RequestBody MaterialFeeSyncRequest data) {
        Map<String, Object> result = new HashMap<>();
        try {
            systemService.unbindMaterialFee(Integer.valueOf(data.getXlbh()));
            result.put("success", true);
            result.put("message", "解除绑定成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "解除绑定失败：" + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/bindings")
    public ResponseEntity<List<Map<String, Object>>> bindings(@RequestParam(required = false) Integer dlid, @RequestParam(required = false) String xlmc) {
        return ResponseEntity.ok(systemService.listMaterialBindings(dlid, xlmc));
    }

    @OperationLog(value = "同步费用数据", module = "收费管理")
    @PostMapping("/sync")
    @Transactional
    public ResponseEntity<Map<String, Object>> sync(@RequestBody MaterialFeeSyncRequest data) {
        Map<String, Object> result = new HashMap<>();
        try {
            systemService.syncMaterialFee(data);
            result.put("success", true);
            result.put("message", "同步完成");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}