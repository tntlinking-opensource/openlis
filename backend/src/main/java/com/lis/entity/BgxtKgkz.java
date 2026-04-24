package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 流程控制/系统开关控制 - 对应旧系统表 bgxt_kgkz
 * 字段：id, kgmc, kgsm, kgz
 */
@Data
@TableName("bgxt_kgkz")
public class BgxtKgkz {
    @TableId(type = IdType.INPUT)
    private Integer id;

    private String kgmc;
    private String kgsm;

    /**
     * 开关值（旧库为 int，通常 0/1）
     */
    private Integer kgz;
}


