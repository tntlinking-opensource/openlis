package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 子系统名称表 - 对应旧系统 sys_zxtmc
 */
@Data
@TableName("sys_zxtmc")
public class SysZxtmc {
    /**
     * 子系统ID（主键）
     */
    @TableId(type = IdType.INPUT)
    private Integer zxtid;
    
    /**
     * 子系统简称
     */
    private String zxtjc;
    
    /**
     * 子系统名称
     */
    private String zxtmc;
    
    /**
     * 子系统版本
     */
    private String zxtbb;
    
    /**
     * 备注
     */
    private String bz;
}

