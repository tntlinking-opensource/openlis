package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_jsgs")
public class SysJsgs {
    private Integer sbDjid;
    private Integer xmid;
    private String bds;
    private String bdssm;
}