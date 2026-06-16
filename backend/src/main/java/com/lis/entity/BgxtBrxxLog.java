package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bgxt_brxx_log")
public class BgxtBrxxLog {

    @TableId(type = IdType.AUTO)
    private Integer logId;

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

    private String bbzl;

    private Integer ybzt;

    private LocalDateTime jyrq;

    private Integer sfbz;

    private String bz;

    private String qxshczy;

    private LocalDateTime qxshrq;
}
