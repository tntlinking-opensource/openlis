package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_xtsz")
public class SysXtsz {

    @TableId(type = IdType.AUTO)
    private Integer xtszId;

    private String xtszKey;

    private String xtszValue;

    private String xtszDesc;

    private Boolean lwbz;

    private Boolean bxt;

    private Integer zyfykz;
}
