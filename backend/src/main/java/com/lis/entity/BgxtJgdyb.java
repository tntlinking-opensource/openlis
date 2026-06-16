package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bgxt_jgdyb")
public class BgxtJgdyb {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer brxxId;

    private String czydm;

    private LocalDateTime dyrq;

    private String zd;

    private String ip;
}
