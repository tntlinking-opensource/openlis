package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("bgxt_tsxmtat")
public class BgxtTsxmtat {

    private Integer sbDjid;

    private Integer brlb;

    private Integer syqk;

    private Integer zhid;

    private String zhmc;

    private Integer tat;
}
