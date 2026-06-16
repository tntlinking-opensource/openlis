package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bgxt_bgmb")
public class BgxtBgmb {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String bgmbmc;

    private String bgjglx;

    private Integer sbDjid;

    private Integer zhid;

    private String ksdm;

    private Integer sfbz;

    private String bz;

    private String bgmbnr;

    private LocalDateTime srrq;
}
