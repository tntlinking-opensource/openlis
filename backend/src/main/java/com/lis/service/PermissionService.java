package com.lis.service;

import com.lis.entity.SysCzydm;
import com.lis.mapper.SysCzydmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 权限服务
 * 对应旧系统6.3刷新权限功能
 */
@Service
public class PermissionService {
    
    @Autowired
    private SysCzydmMapper sysCzydmMapper;
    
    /**
     * 刷新用户权限
     * 重新从数据库加载用户权限信息
     * @param username 用户名（操作员代码）
     * @return 用户权限信息
     */
    public Map<String, Object> refreshPermissions(String username) {
        SysCzydm user = sysCzydmMapper.selectById(username);
        if (user == null) {
            return null;
        }
        
        Map<String, Object> permissions = new HashMap<>();
        permissions.put("czyxm", user.getCzyxm());
        permissions.put("czydm", user.getCzydm());
        permissions.put("ksdm", user.getKsdm());
        permissions.put("gzzdm", user.getGzzdm());
        
        // 权限标志
        permissions.put("ysbz", user.getYsbz() != null && user.getYsbz()); // 医生标志
        permissions.put("czybz", user.getCzybz() != null && user.getCzybz()); // 操作员标志
        permissions.put("glybz", user.getGlybz() != null && user.getGlybz()); // 管理员标志
        
        return permissions;
    }
}

