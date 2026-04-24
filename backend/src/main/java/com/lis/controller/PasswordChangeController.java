package com.lis.controller;

import com.lis.entity.SysCzydm;
import com.lis.mapper.SysCzydmMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 密码修改控制器
 */
@RestController
@RequestMapping("/system/password")
public class PasswordChangeController {
    
    @Autowired
    private SysCzydmMapper sysCzydmMapper;
    
    /**
     * 修改密码
     */
    @PostMapping("/change")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            // 获取用户信息
            SysCzydm user = sysCzydmMapper.selectById(request.getCzydm());
            if (user == null) {
                return ResponseEntity.ok(ApiResponse.fail("用户不存在"));
            }
            
            // 验证原密码（优先使用czymm，如果没有则使用kl）
            String oldPassword = user.getCzymm() != null && !user.getCzymm().isEmpty() 
                ? user.getCzymm() 
                : user.getKl();
            
            if (oldPassword == null || !oldPassword.equals(request.getOldPassword())) {
                return ResponseEntity.ok(ApiResponse.fail("原密码错误"));
            }
            
            // 更新密码（同时更新kl和czymm字段，保持一致性）
            user.setKl(request.getNewPassword());
            user.setCzymm(request.getNewPassword());
            sysCzydmMapper.updateById(user);
            
            return ResponseEntity.ok(ApiResponse.success("密码修改成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail("密码修改失败：" + e.getMessage()));
        }
    }
    
    @Data
    public static class ChangePasswordRequest {
        private String czydm;
        private String oldPassword;
        private String newPassword;
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

