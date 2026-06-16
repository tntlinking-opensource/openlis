package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysBbzlDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysBbzlDictMapper extends BaseMapper<SysBbzlDict> {
    List<Map<String, Object>> listSpecimenTypes(@Param("keyword") String keyword);
    void insertSpecimenType(SysBbzlDict entity);
    void updateSpecimenType(SysBbzlDict entity);
    void deleteSpecimenType(@Param("bm") Integer bm);
}
