package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtYqxmzh;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtYqxmzhMapper extends BaseMapper<BgxtYqxmzh> {

    List<Map<String, Object>> findCombosByInstrument(@Param("sbDjid") Object sbDjid);

    List<Map<String, Object>> existsBySbDjidAndZhid(@Param("sbDjid") Integer sbDjid, @Param("zhid") Integer zhid);

    Integer getMaxZhsxBySbDjid(@Param("sbDjid") Integer sbDjid);

    void assignCombo(@Param("sbDjid") Integer sbDjid, @Param("zhid") Integer zhid, @Param("zhsx") int zhsx);

    void removeCombo(@Param("instId") Integer instId, @Param("comboId") Integer comboId);

    void deleteByZhid(@Param("zhid") Integer zhid);

    List<Map<String, Object>> selectByZhid(@Param("zhid") Integer zhid);

    void insertZhidSbDjid(@Param("zhid") Integer zhid, @Param("sbDjid") Integer sbDjid);

    void deleteByZhidAndSbDjid(@Param("zhid") Integer zhid, @Param("sbDjid") Integer sbDjid);
}
