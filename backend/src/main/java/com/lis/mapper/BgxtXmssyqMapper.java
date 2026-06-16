package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtXmssyq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtXmssyqMapper extends BaseMapper<BgxtXmssyq> {
    List<Map<String, Object>> selectByXmid(@Param("xmid") Integer xmid);
    List<Map<String, Object>> selectBySbDjid(@Param("sbDjid") Integer sbDjid);
    int insertRelation(@Param("xmid") Integer xmid, @Param("sbDjid") Integer sbDjid, @Param("zhid") Integer zhid);
    int deleteByXmidAndSbDjid(@Param("xmid") Integer xmid, @Param("sbDjid") Integer sbDjid);
    int countByXmidAndSbDjid(@Param("xmid") Integer xmid, @Param("sbDjid") Integer sbDjid);
}
