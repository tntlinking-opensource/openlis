package com.lis.dto;

import lombok.Data;

@Data
public class CriticalValueRequest {
    private Integer reportId;
    private Integer xmid;
    private String criticalValue;
    private String addOperCode;
    private String addOperName;
    private String cancelOperCode;
}
