package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysBrlb;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysBrlbMapper extends BaseMapper<SysBrlb> {
    List<Map<String, Object>> listCategories(@Param("brlb") Integer brlb, @Param("keyword") String keyword, @Param("tybzVal") Integer tybzVal);
    List<Map<String, Object>> getNextCode();
    List<Map<String, Object>> listActiveForDropdown();
}
