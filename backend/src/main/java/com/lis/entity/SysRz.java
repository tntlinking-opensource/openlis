package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_rz")
public class SysRz {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private LocalDateTime czrq;

    private String czydm;

    private String sm;

    private String czip;

    private String czmk;

    private Integer ztid;

    private Integer zxtid;
}
