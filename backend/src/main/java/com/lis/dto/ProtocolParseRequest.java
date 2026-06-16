package com.lis.dto;

import lombok.Data;

@Data
public class ProtocolParseRequest {
    private String rawData;
    private String message;
    private Integer sbDjid;
}
