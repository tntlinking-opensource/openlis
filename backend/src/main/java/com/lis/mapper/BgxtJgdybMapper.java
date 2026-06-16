package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtJgdyb;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BgxtJgdybMapper extends BaseMapper<BgxtJgdyb> {

    int insertPrintLog(@Param("brxxId") Integer brxxId, @Param("czydm") String czydm,
                       @Param("zd") String zd, @Param("ip") String ip);
}
