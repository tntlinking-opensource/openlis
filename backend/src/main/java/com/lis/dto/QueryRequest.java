package com.lis.dto;

import lombok.Data;
import java.util.List;

@Data
public class QueryRequest {
    private String beginDate;
    private String endDate;
    private Integer brlb;
    private String ksdm;
    private String brxm;
    private String brxxTmh;
    private String syh;
    private Integer sbDjid;
    private List<Integer> ybztList;
    private String shys;
    private String sjys;
    private String dimension;
    private Integer xmid;
}
