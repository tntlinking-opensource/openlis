package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("bgxt_yqxmzh")
public class BgxtYqxmzh {
    private Integer sbDjid;
    private Integer zhid;
    private Integer zhsx;
}
