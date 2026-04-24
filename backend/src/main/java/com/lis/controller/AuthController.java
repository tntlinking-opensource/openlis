package com.lis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.SysCzydm;
import com.lis.mapper.SysCzydmMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 登录认证控制器
 * 基于旧系统 sys_czydm 表实现
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private SysCzydmMapper sysCzydmMapper;
    
    /**
     * 登录接口
     * 对应旧系统登录逻辑
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("=== 登录请求 ===");
            System.out.println("用户名: " + request.getUsername());
            System.out.println("密码: " + request.getPassword());
            
            // 使用 QueryWrapper 查询用户（因为主键是字符串类型）
            QueryWrapper<SysCzydm> wrapper = new QueryWrapper<>();
            wrapper.eq("czydm", request.getUsername());
            System.out.println("执行查询: czydm = " + request.getUsername());
            
            SysCzydm user = sysCzydmMapper.selectOne(wrapper);
            
            System.out.println("查询结果: " + (user == null ? "null" : "找到用户"));
            if (user != null) {
                System.out.println("用户信息: czydm=" + user.getCzydm() + ", czyxm=" + user.getCzyxm() + ", czymm=" + user.getCzymm() + ", kl=" + user.getKl());
            }
            
            if (user == null) {
                return ResponseEntity.ok(LoginResponse.fail("用户不存在"));
            }
            
            // 检查是否启用（sfqy: 1-启用，0-停用）
            // 注意：参考项目使用 sfqy 字段，不是 sybz
            // 但当前表结构可能使用 sybz，先检查 sybz
            
            // 验证密码（使用 czymm 字段，与参考项目一致）
            String dbPassword = user.getCzymm();
            System.out.println("数据库密码(czymm): " + dbPassword);
            System.out.println("输入密码: " + request.getPassword());
            if (dbPassword == null || !dbPassword.equals(request.getPassword())) {
                return ResponseEntity.ok(LoginResponse.fail("密码错误"));
            }
            
            // 登录成功
            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setMessage("登录成功");
            response.setUser(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 捕获异常并返回错误信息
            e.printStackTrace(); // 打印完整堆栈到控制台
            System.err.println("登录异常: " + e.getClass().getName() + ": " + e.getMessage());
            Throwable cause = e.getCause();
            if (cause != null) {
                System.err.println("原因: " + cause.getClass().getName() + ": " + cause.getMessage());
            }
            return ResponseEntity.ok(LoginResponse.fail("登录失败: " + e.getMessage()));
        }
    }
    
    @Data
    public static class LoginRequest {
        private String username; // czydm
        private String password; // kl
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

