package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("gzsz_mx")
public class GzszMx {

    @TableId(type = IdType.AUTO)
    private Integer xlbh;

    private Integer dlid;

    private String xlmc;

    private Integer isuse;

    private Integer yxxh;

    private String tmhgs;

    private String tmfs;

    private BigDecimal sflbz;

    private String cjyq;

    private String zysx;

    private String sgys;

    private String clfdm;

    private String clfmc;
}
