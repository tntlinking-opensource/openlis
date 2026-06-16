package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sys_zkpd")
public class SysZkpd {

    @TableId(type = IdType.AUTO)
    private Integer zkpid;

    private String zkpmc;

    private String pym;

    private String zkplx;

    private String xmdm;

    private String xmzwmc;

    private String bjzl;

    private String bjzh;

    private String sccj;

    private LocalDate sxrq;

    private Integer zxbz;

    private Integer sybz;

    private Integer sbDjid;

    private String zwmc;

    private String ywmc;

    private String zkpsm;

    private String ph;

    private LocalDateTime syrq;
}
