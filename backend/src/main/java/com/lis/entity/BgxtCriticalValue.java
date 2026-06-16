package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bgxt_CriticalValue")
public class BgxtCriticalValue {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer reportId;

    private String criticalValue;

    private LocalDateTime addDate;

    private String addOperCode;

    private String addOperName;

    private Integer xmid;

    private LocalDateTime cancelDate;

    private String cancelOperCode;
}
