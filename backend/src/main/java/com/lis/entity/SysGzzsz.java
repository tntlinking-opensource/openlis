package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 工作组设置表 - 对应旧系统 sys_gzzsz
 */
@Data
@TableName("sys_gzzsz")
public class SysGzzsz {
    /**
     * 自增ID
     */
    private Integer id;
    
    /**
     * 所属科室代码
     */
    private String ssksdm;
    
    /**
     * 工作组代码（主键）
     */
    @TableId(type = IdType.INPUT)
    private String gzzdm;
    
    /**
     * 工作组名称
     */
    private String gzzmc;
    
    /**
     * 拼音码
     */
    private String pym;
    
    /**
     * 工作组类型（1-检验类，2-库房类，3-后勤类）
     */
    private Integer gzzlx;
    
    /**
     * HIS科室代码
     */
    private String hisKsdm;
    
    /**
     * 序号
     */
    private Integer xh;
    
    /**
     * 使用标志（0-停用，1-使用）
     */
    private Boolean sybz;
}

