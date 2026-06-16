package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sys_zkjl")
public class SysZkjl {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer zkpid;

    private String xmdm;

    private LocalDate jyrq;

    private String jyjg;

    private String pgjg;

    private String czy;

    private LocalDateTime czrq;
}
