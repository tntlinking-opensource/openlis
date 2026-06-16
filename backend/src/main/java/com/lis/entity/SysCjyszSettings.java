package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_cjysz_settings")
public class SysCjyszSettings {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer sbDjid;

    private Integer xmid;

    private String originalValue;

    private String replaceValue;
}