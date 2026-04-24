package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 工作组实体 - 对应表 sys_gzzsz
 * 说明：旧系统中以“工作组”概念出现，这里提供一个更语义化的别名实体。
 */
@Data
@TableName("sys_gzzsz")
public class SysGzz {

    /**
     * 自增ID（记录用，非主键）
     */
    private Integer id;

    /**
     * 所属科室代码 ssksdm
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
     * HIS 科室代码
     */
    private String hisKsdm;

    /**
     * 序号
     */
    private Integer xh;

    /**
     * 使用标志（1-使用，0-停用）
     */
    private Boolean sybz;
}


