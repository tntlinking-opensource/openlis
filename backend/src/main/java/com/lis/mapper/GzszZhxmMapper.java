package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.GzszZhxm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GzszZhxmMapper extends BaseMapper<GzszZhxm> {
    List<Map<String, Object>> listByXlbh(@Param("xlbh") Integer xlbh);

    void insertMapping(@Param("xlbh") Integer xlbh, @Param("zhid") Integer zhid, @Param("zhxmmc") String zhxmmc, @Param("yxxh") Integer yxxh);

    void deleteByXlbhAndCode(@Param("xlbh") Integer xlbh, @Param("zhid") Integer zhid);

    List<Map<String, Object>> listAvailableItems(@Param("xlbh") Integer xlbh);

    List<Map<String, Object>> listCombosByInstrument(@Param("sbDjid") Integer sbDjid);
}