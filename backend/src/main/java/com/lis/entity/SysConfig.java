package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_config")
public class SysConfig implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String wYydm;
    private String yymc;
    private String jykksdm;
    private Integer hisConnectbz;
    private Integer hisConnectlevel;
    private Integer tjConnectbz;
    private Integer tjJghcbz;
    private Integer yszJghcbz;
    private Integer yszConnectbz;
    private Integer qtxtJghcbz;
    private Integer websc;
    private Integer gdsj;
    private Integer hisConnectYbzx;
}
