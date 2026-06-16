package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysZxtmc;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysZxtmcMapper extends BaseMapper<SysZxtmc> {
    List<Map<String, Object>> listSystems();
}
