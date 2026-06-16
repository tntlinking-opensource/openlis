package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bgxt_xmssyq")
public class BgxtXmssyq {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer xmid;
    private Integer sbDjid;
    private Integer zhid;
    private LocalDateTime gxrq;
}
