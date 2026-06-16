package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("bgxt_xmzh_mx")
public class BgxtXmzhMx {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer zhid;

    private String xmdm;

    private String xmzwmc;

    private String xmdw;

    private Integer xh;

    private Integer xmid;

    private String mrjg;

    private Integer sbDjid;
}
