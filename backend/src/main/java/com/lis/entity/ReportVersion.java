package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_bbsz")
public class ReportVersion {
    @TableId(value = "bb_id", type = IdType.AUTO)
    private Integer bbId;

    private Integer sycx;

    private String bbsm;

    private String czydm;

    private String czyxm;

    private Date gxrq;

    @TableField("bb")
    private byte[] bb;

    private Integer mr;
}