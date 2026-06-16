package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("bgxt_xmzh_ygsjsz")
public class BgxtXmzhYgsjsz implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer zhid;

    private Integer szlb;

    private String qssj;

    private String jssj;

    private Integer ygrq;

    private String ygsj;

    private Integer ddsj;

    private Integer tybz;
}
