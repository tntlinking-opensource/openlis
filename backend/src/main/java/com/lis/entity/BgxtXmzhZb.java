package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("bgxt_xmzh_zb")
public class BgxtXmzhZb {

    @TableId(type = IdType.AUTO)
    private Integer zhid;

    private String zhmc;

    private String pym;

    private String zhlx;

    private Integer zxbz;

    private Integer sybz;

    private Integer bbzl;

    private String qtdm;

    private String hisXmdm;

    private String hisZhmc;

    private BigDecimal sfbz;

    private Integer gzl;

    private Integer qybz;

    private Integer lbid;

    private Integer bqys;

    private String yssm;

    private Integer ybzxxg;

    private Integer reportType;

    private String groupType;

    private Integer getSampleFromHis;

    private String defaultResult;

    private Integer projectLevel;

    private Integer ddsj;

    private String zhZy;

    private String zhSyz;

    private String zhCjyq;
}
