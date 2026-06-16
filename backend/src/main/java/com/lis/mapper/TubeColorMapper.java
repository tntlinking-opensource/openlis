package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.TubeColor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TubeColorMapper extends BaseMapper<TubeColor> {
    List<Map<String, Object>> listAll();
    List<Map<String, Object>> search(String keyword);
    int countByPym(String pym);
    void insert(Map<String, Object> data);
    void update(Map<String, Object> data);
    void deleteByPym(String pym);
}