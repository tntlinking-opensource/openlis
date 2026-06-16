package com.lis.dto;

import lombok.Data;
import java.util.List;

@Data
public class BatchRequest {
    private List<Integer> brxxIds;
    private String czydm;
    private String reason;
    private Boolean skipPrinted;
    private Integer templateId;
}
