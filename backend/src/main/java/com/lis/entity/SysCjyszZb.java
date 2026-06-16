package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_cjysz_zb")
public class SysCjyszZb {

    @TableId(type = IdType.AUTO)
    private Integer cjid;

    private Integer hbSbDjid;

    private Integer sbDjid;

    private Integer syh;

    private LocalDateTime cjrq;

    private Integer zkbz;
}
