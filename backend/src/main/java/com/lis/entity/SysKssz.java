package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 科室设置表 - 对应旧系统 sys_kssz
 */
@Data
@TableName("sys_kssz")
public class SysKssz {
    @TableId
    private Integer ksid;
    
    private String ksdm;
    private String ksmc;
    private String pym;
    private String ksxz;
    private Integer zxbz;
    private Integer sybz;
}


