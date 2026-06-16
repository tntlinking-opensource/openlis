package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.GzszZb;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface GzszZbMapper extends BaseMapper<GzszZb> {
    List<Map<String, Object>> listCategories();
    void insertCategory(GzszZb entity);
    void updateCategory(GzszZb entity);
}
