package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统设置表 - 对应旧系统 bgxt_xtsz
 */
@Data
@TableName("bgxt_xtsz")
public class BgxtXtsz {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 名称
     */
    private String mc;
    
    /**
     * 标志
     */
    private Integer bz;
    
    /**
     * 说明
     */
    private String sm;
}

