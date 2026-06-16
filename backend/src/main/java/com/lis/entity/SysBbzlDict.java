package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_bbzl_dict")
public class SysBbzlDict {
    @TableId(type = IdType.AUTO)
    private Integer bm;
    private String bmsm;
    private String pym;
    private String qtdm;
    private Integer xssx;
    private String whonet;
    private String hisBmdm;
    private String rqdm;
    private String rqlx;
    private String cjyq;
}
