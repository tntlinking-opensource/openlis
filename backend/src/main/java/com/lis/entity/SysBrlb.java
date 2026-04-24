package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 病人类别表 - 对应旧系统 sys_brlb
 */
@Data
@TableName("sys_brlb")
public class SysBrlb {
    /**
     * 类别代码（主键）
     */
    @TableId(type = IdType.INPUT)
    private Integer bm;
    
    /**
     * 类别名称
     */
    private String bmsm;
    
    /**
     * 拼音码
     */
    private String pym;
    
    /**
     * 其他代码
     */
    private String qtdm;
    
    /**
     * 数据来源方式
     */
    private Integer sjlyfs;
    
    /**
     * 数据来源方式说明
     */
    private String sjlyfsms;
    
    /**
     * 默认科室标志
     */
    private Boolean mrksbz;
    
    /**
     * 默认科室代码
     */
    private String mrksdm;
    
    /**
     * 默认科室名称
     */
    private String mrksmc;
    
    /**
     * 默认医生标志
     */
    private Boolean mrysbz;
    
    /**
     * 默认医生代码
     */
    private String mrysdm;
    
    /**
     * 默认医生名称
     */
    private String mrysmc;
    
    /**
     * 序号
     */
    private Integer xh;
    
    /**
     * 停用标志
     */
    private Boolean tybz;
    
    /**
     * 接口标志
     */
    private Boolean jkbz;
    
    /**
     * 机构信息标志
     */
    private Boolean jgxxBz;
    
    /**
     * 机构信息
     */
    private String jgxx;
    
    /**
     * 权限控制
     */
    private Boolean qxkz;
    
    /**
     * 权限名称
     */
    private String qxmc;
    
    /**
     * 病人类别颜色
     */
    private Integer brlbys;
}

