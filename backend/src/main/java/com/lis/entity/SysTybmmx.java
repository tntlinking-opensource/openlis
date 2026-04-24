package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 通用编码明细表 - 对应旧系统 sys_tybmmx
 */
@Data
@TableName("sys_tybmmx")
public class SysTybmmx {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 编码代码（关联sys_tybmzb.bmdm）
     */
    private Integer bmdm;
    
    /**
     * 编码
     */
    private Integer bm;
    
    /**
     * 编码说明
     */
    private String bmsm;
    
    /**
     * 数字代码
     */
    private String szdm;
    
    /**
     * 拼音码
     */
    private String pym;
    
    /**
     * 默认值标志
     */
    private Boolean mrzbz;
    
    /**
     * 停用标志
     */
    private Boolean tybz;
    
    /**
     * 备注
     */
    private String bz;
}

