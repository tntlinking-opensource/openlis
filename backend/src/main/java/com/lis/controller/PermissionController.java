package com.lis.controller;

import com.lis.service.PermissionService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 6.3 刷新权限控制器
 * 对应旧系统刷新权限功能
 */
@RestController
@RequestMapping({"/system/permission", "/permission"})
public class PermissionController {
    
    @Autowired
    private PermissionService permissionService;
    
    /**
     * 刷新权限
     * 重新加载当前用户的权限信息，无需退出程序重新登录
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshPermission(@RequestBody RefreshRequest request) {
        Map<String, Object> permissions = permissionService.refreshPermissions(request.getUsername());
        if (permissions != null) {
            ApiResponse response = new ApiResponse();
            response.setSuccess(true);
            response.setMessage("权限刷新成功");
            response.setPermissions(permissions);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(ApiResponse.fail("用户不存在"));
        }
    }
    
    /**
     * 获取权限列表（测试用）
     */
    @GetMapping("/list")
    public ResponseEntity<Object> getPermissionList() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> item1 = new HashMap<>();
        item1.put("code", "admin");
        item1.put("name", "管理员");
        data.add(item1);
        
        Map<String, String> item2 = new HashMap<>();
        item2.put("code", "user");
        item2.put("name", "普通用户");
        data.add(item2);
        
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
    
    @Data
    public static class RefreshRequest {
        private String username;
    }
    
    @Data
    public static class ApiResponse {
        private Boolean success;
        private String message;
        private Map<String, Object> permissions;
        
        public static ApiResponse fail(String message) {
            ApiResponse response = new ApiResponse();
            response.setSuccess(false);
            response.setMessage(message);
            return response;
        }
    }
}

