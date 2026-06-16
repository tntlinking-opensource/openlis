package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("zk_nyzkxm")
public class ZkNyzkxm {

    @TableId(type = IdType.AUTO)
    private Integer zkxmid;

    private Integer zkpid;

    private Integer xmid;

    private String bz;

    private String bzc;

    private String zkdz;

    private String zkgz;

    private Integer dxLx;

    private Integer fhbz;

    private String bc;
}
