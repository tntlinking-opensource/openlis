package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_jyxm_full")
public class SysJyxmFull {
    @TableId(type = IdType.AUTO)
    private Integer xmid;
    private String xmdm;
    private String xmzwmc;
    private String xmywmc;
    private String pym;
    private String qtdm;
    private String xmdw;
    private Integer xmjd;
    private Integer xmlx;
    private Boolean jsbz;
    private java.math.BigDecimal xs;
    private java.math.BigDecimal sjxhl;
    private Boolean tybz;
    private Boolean dybz;
    private Boolean zsbz;
    private java.math.BigDecimal sfbz;
    private java.math.BigDecimal gzl;
    private String hisFydm;
    private String hisJyxmmc;
    private String zskXmdm;
    private String zskXmmc;
    private Integer itemType;
    private String lcyy;
}
