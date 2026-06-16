package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("gzsz_zhxm")
public class GzszZhxm {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer xlbh;

    private Integer zhid;

    private String zhxmmc;

    private Integer yxxh;
}