package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bgxt_tsbbsz")
public class BgxtTybbsz {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer mkid;
    private Integer xmid;
    private String mksm;
    private LocalDateTime gxrq;
}
