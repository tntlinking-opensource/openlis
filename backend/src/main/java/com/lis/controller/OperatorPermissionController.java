package com.lis.controller;

import com.lis.service.OperatorPermissionService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.lis.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/operator-permission")
@Slf4j
public class OperatorPermissionController {

    @Autowired
    private OperatorPermissionService operatorPermissionService;

    @GetMapping("/operators")
    public ResponseEntity<List<Map<String, Object>>> getOperators(
            @RequestParam(required = false, defaultValue = "") String czyxm,
            @RequestParam(required = false, defaultValue = "") String ksmc) {
        return ResponseEntity.ok(operatorPermissionService.getOperators(czyxm, ksmc));
    }

    @GetMapping("/subsystems")
    public ResponseEntity<List<Map<String, Object>>> getSubsystems() {
        return ResponseEntity.ok(operatorPermissionService.getSubsystems());
    }

    @GetMapping("/module-categories")
    public ResponseEntity<List<Map<String, Object>>> getModuleCategories(
            @RequestParam Integer zxtid) {
        return ResponseEntity.ok(operatorPermissionService.getModuleCategories(zxtid));
    }

    @GetMapping("/modules")
    public ResponseEntity<List<Map<String, Object>>> getModules(
            @RequestParam Integer zxtid,
            @RequestParam(required = false, defaultValue = "") String mkfl,
            @RequestParam String czydm) {
        return ResponseEntity.ok(operatorPermissionService.getModules(zxtid, mkfl, czydm));
    }

    @GetMapping("/permission-categories")
    public ResponseEntity<List<Map<String, Object>>> getPermissionCategories(
            @RequestParam Integer zxtid) {
        return ResponseEntity.ok(operatorPermissionService.getPermissionCategories(zxtid));
    }

    @GetMapping("/permission-items")
    public ResponseEntity<List<Map<String, Object>>> getPermissionItems(
            @RequestParam String dldm,
            @RequestParam String czydm) {
        return ResponseEntity.ok(operatorPermissionService.getPermissionItems(dldm, czydm));
    }

    @OperationLog(value = "保存菜单权限", module = "权限管理")
    @PostMapping("/save-menu-permissions")
    public ResponseEntity<Map<String, Object>> saveMenuPermissions(
            @RequestBody SaveMenuPermissionsRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String czydm = request.getCzydm();
            List<OperatorPermissionService.PermissionItem> items = request.getItems();
            operatorPermissionService.saveMenuPermissions(czydm, items);
            result.put("success", true);
            result.put("message", "保存成功");
        } catch (Exception e) {
            log.error("操作失败", e);
            result.put("success", false);
            result.put("message", "保存失败：" + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-permissions")
    public ResponseEntity<List<String>> myPermissions(@RequestParam String czydm) {
        return ResponseEntity.ok(operatorPermissionService.getMyPermissions(czydm));
    }

    @Data
    public static class SaveMenuPermissionsRequest {
        private String czydm;
        private List<OperatorPermissionService.PermissionItem> items;
    }

    @OperationLog(value = "保存模块权限", module = "权限管理")
    @PostMapping("/save-module-permissions")
    public ResponseEntity<Map<String, Object>> saveModulePermissions(
            @RequestBody SaveModulePermissionsRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String czydm = request.getCzydm();
            List<OperatorPermissionService.ModulePermissionItem> items = request.getItems();
            operatorPermissionService.saveModulePermissions(czydm, items);
            result.put("success", true);
            result.put("message", "保存成功");
        } catch (Exception e) {
            log.error("操作失败", e);
            result.put("success", false);
            result.put("message", "保存失败：" + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @Data
    public static class SaveModulePermissionsRequest {
        private String czydm;
        private List<OperatorPermissionService.ModulePermissionItem> items;
    }
}
