package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 模块表 - 对应旧系统 sys_mkb
 */
@Data
@TableName("sys_mkb")
public class SysMkb {
    /**
     * 模块代码（主键）
     */
    @TableId(type = IdType.INPUT)
    private Integer mkdm;
    
    /**
     * 子系统ID
     */
    private Integer zxtid;
    
    /**
     * 窗体名称
     */
    private String frmName;
    
    /**
     * 窗体标题
     */
    private String frmCaption;
    
    /**
     * 模块分类
     */
    private String mkfl;
    
    /**
     * 操作名称
     */
    private String actionName;
    
    /**
     * 标题
     */
    private String caption;
    
    /**
     * 是否已授权（用于查询结果，非数据库字段）
     */
    private Boolean bz;
}

