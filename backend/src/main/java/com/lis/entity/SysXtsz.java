package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统设置表 - 对应旧系统 sys_xtsz
 */
@Data
@TableName("sys_xtsz")
public class SysXtsz {
    /**
     * 联网标志
     */
    private Boolean lwbz;
    
    /**
     * 补写条
     */
    private Boolean bxt;
    
    /**
     * 住院费用控制
     */
    private Integer zyfykz;
}

