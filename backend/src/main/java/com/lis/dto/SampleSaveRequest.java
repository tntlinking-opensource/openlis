package com.lis.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class SampleSaveRequest {
    private Map<String, Object> patient;
    private List<Map<String, Object>> results;
}
