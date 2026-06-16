package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_gdz")
public class SysGdz {
    @TableId(type = IdType.AUTO)
    private Integer bhid;
    private String bh;
    private Integer bs;
    private Boolean sybz;
}
