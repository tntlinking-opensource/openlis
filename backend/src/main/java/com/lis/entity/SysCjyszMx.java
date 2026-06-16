package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_cjysz_mx")
public class SysCjyszMx {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer cjid;

    private Integer xmid;

    private String jyjg;

    private String mgd;

    private String od;

    private String cutOff;
}
