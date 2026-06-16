package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_gzzd")
public class SysGzzd {

    @TableId(type = IdType.AUTO)
    private Integer gzid;

    private String gzdm;

    private String gzmc;

    private String pym;

    private Integer zxbz;

    private Integer sybz;
}
