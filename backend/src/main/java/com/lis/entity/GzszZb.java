package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("gzsz_zb")
public class GzszZb {

    @TableId(type = IdType.AUTO)
    private Integer dlid;

    private String dlmc;

    private Integer isuse;
}
