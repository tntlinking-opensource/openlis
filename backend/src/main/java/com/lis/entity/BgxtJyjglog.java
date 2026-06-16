package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bgxt_jyjglog")
public class BgxtJyjglog {

    @TableId(type = IdType.AUTO)
    private Integer logId;

    private Integer brxxId;

    private Integer xmid;

    private String jyjg;

    private String gdbj;

    private String ckz;

    private LocalDateTime czri;
}
