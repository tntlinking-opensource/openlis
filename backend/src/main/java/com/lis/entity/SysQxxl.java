package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 权限小类表 - 对应旧系统 sys_qxxl
 */
@Data
@TableName("sys_qxxl")
public class SysQxxl {
    /**
     * 大类代码
     */
    private String dldm;
    
    /**
     * 小类代码（权限系列代码，对应菜单项名称）
     */
    private String xldm;
    
    /**
     * 小类名称
     */
    private String xlmc;
    
    /**
     * 是否已授权（用于查询结果，非数据库字段）
     */
    private Boolean bz;
}

