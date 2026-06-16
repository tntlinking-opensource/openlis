package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bgxt_sample_reject")
public class BgxtSampleReject {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer brxxId;
    private String testBarcode;
    private String patientName;
    private String sex;
    private String age;
    private Integer patientType;
    private String department;
    private String bedNumber;
    private String sampleType;
    private String itemName;
    private String classGroup;
    private String errorReason;
    private String groupName;
    private String handlingMeasures;
    private String handlingMeasuresOther;
    private String recipient;
    private String notes;
    private String operatorCode;
    private String operatorName;
    private LocalDateTime createTime;
}
