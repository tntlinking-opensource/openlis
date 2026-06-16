package com.lis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.dto.LoginRequest;
import com.lis.entity.SysCzydm;
import com.lis.mapper.SysCzydmMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.lis.annotation.OperationLog;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * 登录认证控制器
 * 基于旧系统 sys_czydm 表实现
 */
@RestController
@RequestMapping({"/auth", "/api/auth"})
@Slf4j
public class AuthController {
    
    @Autowired
    private SysCzydmMapper sysCzydmMapper;
    
    /**
     * 登录接口
     * 对应旧系统登录逻辑
     */
    @OperationLog(value = "用户登录", module = "系统登录")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            log.info("=== 登录请求 ===");
            String loginCode = request.getUsername();
            log.info("用户名: " + loginCode);
            QueryWrapper<SysCzydm> wrapper = new QueryWrapper<>();
            wrapper.eq("czydm", loginCode);
            log.info("执行查询: czydm = " + request.getUsername());
            
            SysCzydm user = sysCzydmMapper.selectOne(wrapper);
            
            log.info("查询结果: " + (user == null ? "null" : "找到用户"));
            if (user != null) {            }
            
            if (user == null) {
                return ResponseEntity.ok(LoginResponse.fail("用户不存在"));
            }
            
            // 检查是否启用（sfqy: 1-启用，0-停用）
            // 注意：参考项目使用 sfqy 字段，不是 sybz
            // 但当前表结构可能使用 sybz，先检查 sybz
            
            // 验证密码（使用 czymm 字段，与参考项目一致）
            String dbPassword = user.getCzymm();            if (dbPassword == null || !dbPassword.equals(request.getPassword())) {
                return ResponseEntity.ok(LoginResponse.fail("密码错误"));
            }
            
            // 登录成功 - 清除敏感字段后再返回前端
            user.setCzymm(null);
            user.setKl(null);
            user.setDzqm(null);
            
            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setMessage("登录成功");
            response.setUser(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 捕获异常并返回错误信息
            log.error("操作失败", e); // 打印完整堆栈到控制台
            log.error("登录异常: " + e.getClass().getName() + ": " + e.getMessage());
            Throwable cause = e.getCause();
            if (cause != null) {
                log.error("原因: " + cause.getClass().getName() + ": " + cause.getMessage());
            }
            return ResponseEntity.ok(LoginResponse.fail("登录失败: " + e.getMessage()));
        }
    }
    
    
    @Data
    public static class LoginResponse {
        private Boolean success;
        private String message;
        private SysCzydm user;
        
        public static LoginResponse fail(String message) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage(message);
            return response;
        }
    }
}

