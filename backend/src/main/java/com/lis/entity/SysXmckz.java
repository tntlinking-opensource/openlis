package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_xmckz")
public class SysXmckz {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer xmid;
    private Boolean bbsgbz;
    private Integer bbzl;
    private Boolean xbsgbz;
    private Integer brxb;
    private Boolean nlsgbz;
    private Integer nllx;
    private java.math.BigDecimal nlsx;
    private java.math.BigDecimal nlxx;
    private String ckz;
    private java.math.BigDecimal ckzgx;
    private java.math.BigDecimal ckzdx;
    private java.math.BigDecimal bjzgx;
    private java.math.BigDecimal bjzdx;
    private java.math.BigDecimal jszgx;
    private java.math.BigDecimal jszdx;
    private java.math.BigDecimal fczgx;
    private java.math.BigDecimal fczdx;
    private Boolean zdshbz;
    private java.math.BigDecimal zdshgx;
    private java.math.BigDecimal zdshdx;
    private Integer zdshcyqj;
    private Boolean jgfctsbz;
    private Integer sbDjid;
}
