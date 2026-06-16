package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bgxt_ksyqsz")
public class BgxtKsyqsz {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String ksdm;
    private Integer sbDjid;
    private LocalDateTime gxrq;
}
