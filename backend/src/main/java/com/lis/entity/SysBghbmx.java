package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 报告类型明细表 - 对应旧系统 sys_bghbmx
 * 主键为 (hbid, sb_djid) 复合键，实际 CRUD 需使用 QueryWrapper 按两列处理。
 */
@Data
@TableName("sys_bghbmx")
public class SysBghbmx {
    /**
     * 报告ID（关联sys_bghbzb.hbid）
     */
    private Integer hbid;

    /**
     * 设备ID（关联sys_sbdjb.sb_djid）
     */
    private Integer sbDjid;

    /**
     * 使用标志
     */
    private Boolean sybz;
}

