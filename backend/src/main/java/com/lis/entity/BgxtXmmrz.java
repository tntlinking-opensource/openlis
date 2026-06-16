package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("bgxt_xmmrz")
public class BgxtXmmrz {
    private Integer xmid;
    private Integer sbDjid;
    private String mrz;
    private Boolean mr;
}
