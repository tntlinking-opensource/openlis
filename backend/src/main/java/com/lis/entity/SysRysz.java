package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_rysz")
public class SysRysz {

    @TableId(type = IdType.AUTO)
    private Integer ryid;

    private String rydm;

    private String ryxm;

    private String pym;

    private String ksdm;

    private Integer zxbz;

    private Integer sybz;
}
