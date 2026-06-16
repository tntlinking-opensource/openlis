package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_rzztsm")
public class SysRzztsm {

    private Integer zxtid;

    private Integer ztid;

    private String ztsm;
}
