package com.lis.controller;

import com.lis.service.SystemLockService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 6.2 系统锁定控制器
 * 对应旧系统系统锁定功能
 */
@RestController
@RequestMapping({"/system/lock", "/lock"})
public class SystemLockController {
    
    @Autowired
    private SystemLockService systemLockService;
    
    /**
     * 锁定系统
     */
    @PostMapping("/lock")
    public ResponseEntity<ApiResponse> lockSystem(@RequestBody LockRequest request) {
        boolean success = systemLockService.lockSystem(request.getUsername(), request.getPassword());
        if (success) {
            return ResponseEntity.ok(ApiResponse.success("系统已锁定"));
        } else {
            return ResponseEntity.ok(ApiResponse.fail("密码错误，锁定失败"));
        }
    }
    
    /**
     * 解锁系统
     */
    @PostMapping("/unlock")
    public ResponseEntity<ApiResponse> unlockSystem(@RequestBody LockRequest request) {
        boolean success = systemLockService.unlockSystem(request.getUsername(), request.getPassword());
        if (success) {
            return ResponseEntity.ok(ApiResponse.success("系统已解锁"));
        } else {
            return ResponseEntity.ok(ApiResponse.fail("密码错误，解锁失败"));
        }
    }
    
    /**
     * 获取锁定状态（测试用）
     */
    @GetMapping("/status")
    public ResponseEntity<Object> getLockStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("locked", false);
        result.put("username", "");
        result.put("message", "系统未锁定");
        return ResponseEntity.ok(result);
    }
    
    @Data
    public static class LockRequest {
        private String username;
        private String password;
    }
    
    @Data
    public static class ApiResponse {
        private Boolean success;
        private String message;
        
        public static ApiResponse success(String message) {
            ApiResponse response = new ApiResponse();
            response.setSuccess(true);
            response.setMessage(message);
            return response;
        }
        
        public static ApiResponse fail(String message) {
            ApiResponse response = new ApiResponse();
            response.setSuccess(false);
            response.setMessage(message);
            return response;
        }
    }
}

