package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bgxt_brxx")
public class BgxtBrxx {

    @TableId(type = IdType.AUTO)
    private Integer brxxId;

    private String brxxTmh;

    private String brbh;

    private String brxm;

    private Integer brxb;

    private String brnl;

    private String nllx;

    private Integer brlb;

    private Integer syqk;

    private String ksdm;

    private String brch;

    private String syh;

    private Integer bbzl;

    private Integer ybzt;

    private LocalDateTime jyrq;

    private LocalDateTime shrq;

    private Integer sfbz;

    private String bz;

    @TableField("bz2")
    private String bz2;

    @TableField("tjdw")
    private String tjdw;

    @TableField("zjhm")
    private String zjhm;

    @TableField("lxfs")
    private String lxfs;

    @TableField("bbxt")
    private String bbxt;

    private String czy;

    private LocalDateTime czrq;

    @TableField("lczd")
    private String lczd;

    @TableField("jyys")
    private String jyys;

    @TableField("sjys")
    private String sjys;

    @TableField("shys")
    private String shys;

    @TableField("sb_djid")
    private Integer sbDjid;

    @TableField("bgbh")
    private String bgbh;

    @TableField("bgmc")
    private String bgmc;

    @TableField("bgbt")
    private String bgbt;

    @TableField("bgyj")
    private String bgyj;

    @TableField("bgjglx")
    private Integer bgjglx;

    @TableField("yczt")
    private Integer yczt;

    @TableField("dycs")
    private Integer dycs;

    @TableField("dybz")
    private Integer dybz;

    @TableField("bgrq")
    private LocalDateTime bgrq;
}
