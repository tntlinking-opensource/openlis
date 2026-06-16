package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_ybzt")
public class SysYbzt {

    private Integer brxxId;

    private Integer oldYbzt;

    private Integer newYbzt;

    private String czydm;

    private LocalDateTime czrq;
}
