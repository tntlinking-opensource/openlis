package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("bgxt_jyjg")
public class BgxtJyjg {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer brxxId;

    private String xmdm;

    private String xmzwmc;

    private String jyjg;

    private String jldw;

    private String bjzl;

    private String bjzh;

    private String ckz;

    private LocalDate jyri;

    private String czy;

    private LocalDateTime czri;
}
