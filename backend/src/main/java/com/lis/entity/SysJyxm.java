package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 检验项目表 - 对应旧系统 sys_jyxm
 */
@Data
@TableName("sys_jyxm")
public class SysJyxm {
    /**
     * 项目ID（主键）
     */
    @TableId(type = IdType.INPUT)
    private Integer xmid;
    
    /**
     * 项目中文名称
     */
    private String xmzwmc;
    
    /**
     * 项目英文名称
     */
    private String xmywmc;
    
    /**
     * 项目单位
     */
    private String xmdw;
    
    /**
     * 拼音码
     */
    private String pym;
}

