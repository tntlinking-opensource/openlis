package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 权限大类表 - 对应旧系统 sys_qxdl
 */
@Data
@TableName("sys_qxdl")
public class SysQxdl {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 子系统ID
     */
    private Integer zxtid;
    
    /**
     * 大类代码
     */
    private String dldm;
    
    /**
     * 大类名称
     */
    private String dlmc;
    
    /**
     * 科目级数
     */
    private Integer kmjs;
}

