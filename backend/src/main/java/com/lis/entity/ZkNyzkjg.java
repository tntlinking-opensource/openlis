package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("zk_nyzkjg")
public class ZkNyzkjg {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer zkxmid;

    private String yssj;

    private String yhsj;

    private String jssj;

    private LocalDate syrq;

    private Integer sybz;

    private Integer skbz;

    private LocalDate jssjDate;
}
