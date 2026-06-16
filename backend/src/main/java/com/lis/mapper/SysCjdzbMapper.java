package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysCjdzb;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysCjdzbMapper extends BaseMapper<SysCjdzb> {
    List<Map<String, Object>> findItemsByInstrument(@Param("sbDjid") Object sbDjid);
    List<Map<String, Object>> findInstrumentsByItem(@Param("xmid") Integer xmid);
    List<Map<String, Object>> existsBySbDjidAndXmid(@Param("sbDjid") Integer sbDjid, @Param("xmid") Integer xmid);
    void insertCoeff(@Param("sbDjid") Integer sbDjid, @Param("xmid") Integer xmid,
                     @Param("xmdm") Object xmdm, @Param("xs") Object xs);
    void updateCoeff(@Param("xs") Object xs, @Param("sbDjid") Integer sbDjid, @Param("xmid") Integer xmid);
    void updateInstItem(@Param("sbDjid") Integer sbDjid, @Param("xmid") Integer xmid,
                        @Param("xmbm") String xmbm, @Param("xs") Object xs,
                        @Param("yqxmdw") String yqxmdw, @Param("xmjc") String xmjc);
    void deleteByInstrumentAndItem(@Param("sbDjid") Integer sbDjid, @Param("xmid") Integer xmid);
    Integer getMaxDyxhBySbDjid(@Param("sbDjid") Integer sbDjid);

    List<Map<String, Object>> findCoefficientsByInstrument(@Param("sbDjid") Integer sbDjid);

    void batchUpsertCoefficients(@Param("sbDjid") Integer sbDjid, @Param("items") List<Map<String, Object>> items);
}
