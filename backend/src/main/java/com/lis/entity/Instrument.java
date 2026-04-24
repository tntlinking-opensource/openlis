package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 7.6 仪器设备设置 - 仪器实体
 * 对应旧系统设备登记表 sys_sbdjb
 */
@Data
@TableName("sys_sbdjb")
public class Instrument {

    /**
     * 设备登记ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Integer sbDjid;

    /**
     * 设备代码（sbdm）
     */
    private String sbdm;

    /**
     * 设备名称（sbmc）
     */
    private String sbmc;

    /**
     * 设备别名 / 仪器编码（sbbm）
     */
    private String sbbm;

    /**
     * 科室代码（ksdm）
     */
    private String ksdm;

    /**
     * 工作组代码（gzzdm）
     */
    private String gzzdm;

    /**
     * 拼音码（pym）
     */
    private String pym;

    /**
     * 执行标志（zxbz）
     * 1-执行/启用，0-不执行/停用
     */
    private Boolean zxbz;

    /**
     * 停用标志（tybz）
     */
    private Boolean tybz;
}


