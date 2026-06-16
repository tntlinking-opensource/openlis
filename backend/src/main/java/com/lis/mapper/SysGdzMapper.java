package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysGdz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysGdzMapper extends BaseMapper<SysGdz> {
    List<Map<String, Object>> listAll();
    List<Map<String, Object>> listActive();
    void deactivateAll();
    Integer getMaxBhid();
    void insertFlag(@Param("bhid") int bhid, @Param("bh") String bh, @Param("bs") int bs, @Param("sybz") int sybz);
    void updateFlag(@Param("bh") String bh, @Param("sybz") int sybz, @Param("bhid") int bhid, @Param("bs") int bs);
    void deleteByBhidRange(@Param("from") int from, @Param("to") int to);
    int countActiveByBhid(@Param("baseId") int baseId);
}
