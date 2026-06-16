package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("zk_nyskcl")
public class ZkNyskcl {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer zkxmid;

    private String zkcl;

    private String czydmClr;

    private LocalDateTime ksrq;

    private LocalDateTime jsrq;

    private LocalDateTime clrq;
}
