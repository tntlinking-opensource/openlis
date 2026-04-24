package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 通用编码主表 - 对应旧系统 sys_tybmzb
 */
@Data
@TableName("sys_tybmzb")
public class SysTybmzb {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 编码名称
     */
    private String bmmc;
    
    /**
     * 编码代码
     */
    private Integer bmdm;
    
    /**
     * 编码编号
     */
    private String bmbh;
    
    /**
     * 使用层次编码
     */
    private Integer syccbm;
    
    /**
     * 使用层次名称
     */
    private String syccmc;
    
    /**
     * 停用标志
     */
    private Boolean tybz;
}

