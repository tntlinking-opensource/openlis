package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 操作员权限表 - 对应旧系统 sys_czyqx
 * 存储操作员与权限系列代码的关联关系
 */
@Data
@TableName("sys_czyqx")
public class SysCzyqx {
    /**
     * 操作员代码
     */
    private String czydm;
    
    /**
     * 权限系列代码（菜单项名称，如 lis08, lis06_01 等）
     */
    private String qxxldm;
}

