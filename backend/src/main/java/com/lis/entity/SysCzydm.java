package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 操作员代码表 - 对应旧系统 sys_czydm
 * 用于登录和权限管理
 */
@Data
@TableName("sys_czydm")
public class SysCzydm {
    /**
     * 操作员代码（主键）
     */
    @TableId(type = IdType.INPUT)
    private String czydm;
    
    /**
     * 操作员姓名
     */
    private String czyxm;
    
    /**
     * 拼音码
     */
    private String pym;
    
    /**
     * 科室代码
     */
    private String ksdm;
    
    /**
     * 职称代码
     */
    private String zcdm;
    
    /**
     * 口令（密码）- 旧字段
     */
    private String kl;
    
    /**
     * 操作员密码（czymm）- 用于登录验证
     */
    private String czymm;
    
    /**
     * HIS操作员代码
     */
    private String hisCzydm;
    
    /**
     * 默认输入人
     */
    private String mrsrf;
    
    /**
     * 医生标志
     */
    private Boolean ysbz;
    
    /**
     * 操作员标志
     */
    private Boolean czybz;
    
    /**
     * 管理员标志
     */
    private Boolean glybz;
    
    /**
     * 使用标志（0-使用，1-停用）
     */
    private Boolean sybz;
    
    /**
     * 工作组代码
     */
    private String gzzdm;
    
    /**
     * 电子签名（image类型）
     */
    private byte[] dzqm;
    
    /**
     * 操作员身份证号码
     */
    private String czysfzhm;
}

