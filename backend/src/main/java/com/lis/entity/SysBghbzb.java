package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

/**
 * 报告类型主表 - 对应旧系统 sys_bghbzb
 */
@Data
@TableName("sys_bghbzb")
public class SysBghbzb {
    /**
     * 报告ID
     */
    @TableId(type = IdType.INPUT)
    private Integer hbid;
    
    /**
     * 报告名称
     */
    private String hbmc;
}

