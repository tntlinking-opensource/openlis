package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("sys_sbdjb")
public class Instrument {

    @TableId(type = IdType.AUTO)
    private Integer sbDjid;

    private String sbdm;
    private String sbmc;
    private String sbbm;
    private String ksdm;
    private String gzzdm;
    private String pym;
    private Boolean zxbz;
    private Boolean tybz;

    private String comsm;
    private Integer btl;
    private String jyw;
    private Integer sjw;
    private Integer tzw;

    private String xmxsfs;
    private String bgbt;
    private String bgyj;
    private Integer mrzhid;
    private String tx;
    private String dyfs;
    private String shzfs;
    private Integer sxpl;
    private Boolean ycxwc;

    private String xsfs;
    private String bblb;
    private String bgbh;
    private String bgmc;
    private String xslb;
    private String zklb;
    private String yqzd;
    private Integer zjjgts;
    private String zkjh;
    private String jzjh;
    private String cjcx;
    private String szdm;

    private Boolean kztsbz;
    private Boolean jkxmxz;
    private Boolean fsztsbz;
    private Boolean zerotsbz;

    private String ip;
    private String dk;
    private String sjklj;
    private String wjdz;
    private String bfdz;
    private String wjyhm;
    private String wjmm;

    private String yszcz;
    private String yspgz;
    private String yspdz;
    private String ysbjgz;
    private String ysbjdz;
    private String yswsh;
    private String ysysh;
    private String ysycy;
    private String ysydy;
    private String ysyjy;
    private String ysycz;
    private String yswjz;
    private String ysjgwc;
}
