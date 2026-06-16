package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bgxt_his_xm")
public class BgxtHisXm {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer brxxId;

    private Integer zhid;

    private Integer sf;

    private LocalDateTime sfrq;

    private String czydmSfr;

    private Integer zfbz;

    private LocalDateTime zfrq;

    private String czydmZfr;
}
