package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_report_template")
public class ReportTemplate {

    @TableId(type = IdType.AUTO)
    private Integer templateId;

    private String templateName;

    private String templateType;

    private String templateCode;

    private String description;

    private String config;

    private Integer mr;

    private Integer sycx;

    private String czyxm;

    private LocalDateTime gxrq;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}