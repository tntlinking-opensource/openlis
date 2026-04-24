package com.lis.service;

import com.lis.entity.SysCzydm;
import com.lis.mapper.SysCzydmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统锁定服务
 * 对应旧系统6.2系统锁定功能
 */
@Service
public class SystemLockService {
    
    @Autowired
    private SysCzydmMapper sysCzydmMapper;
    
    /**
     * 验证密码并锁定系统
     * @param username 用户名（操作员代码）
     * @param password 密码
     * @return 是否锁定成功
     */
    public boolean lockSystem(String username, String password) {
        SysCzydm user = sysCzydmMapper.selectById(username);
        if (user == null) {
            System.out.println("系统锁定：用户不存在 - " + username);
            return false;
        }
        
        // 验证密码：优先使用czymm（与登录逻辑一致），如果czymm为空则使用kl
        String czymm = user.getCzymm();
        String kl = user.getKl();
        
        System.out.println("系统锁定 - 用户名: " + username);
        System.out.println("系统锁定 - 输入密码: " + password);
        System.out.println("系统锁定 - 数据库czymm: " + (czymm != null ? czymm : "(null)"));
        System.out.println("系统锁定 - 数据库kl: " + (kl != null ? kl : "(null)"));
        
        // 优先使用czymm（与登录逻辑一致）
        String dbPassword = null;
        if (czymm != null && !czymm.isEmpty()) {
            dbPassword = czymm;
            System.out.println("系统锁定 - 使用czymm字段验证");
        } else if (kl != null && !kl.isEmpty()) {
            dbPassword = kl;
            System.out.println("系统锁定 - czymm为空，使用kl字段验证");
        }
        
        if (dbPassword == null) {
            System.out.println("系统锁定 - 密码字段为空");
            return false;
        }
        
        boolean passwordMatch = dbPassword.equals(password);
        if (passwordMatch) {
            System.out.println("系统锁定 - 密码匹配成功");
        } else {
            System.out.println("系统锁定 - 密码不匹配（期望: " + dbPassword + ", 实际: " + password + "）");
        }
        
        return passwordMatch;
    }
    
    /**
     * 解锁系统
     * @param username 用户名
     * @param password 密码
     * @return 是否解锁成功
     */
    public boolean unlockSystem(String username, String password) {
        return lockSystem(username, password);
    }
}

