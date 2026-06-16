package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("bgxt_sgyssz")
public class TubeColor {
    private String sgys;
    private String pym;
}