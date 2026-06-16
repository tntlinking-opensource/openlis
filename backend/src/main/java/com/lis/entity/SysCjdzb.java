package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_cjdzb")
public class SysCjdzb {
    private Integer sbDjid;
    private Integer xmid;
    private String xmdm;
    private java.math.BigDecimal xs;
    private Integer dyxh;
    private String yqxmdw;
}
