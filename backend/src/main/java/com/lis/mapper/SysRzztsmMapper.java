package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysRzztsm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysRzztsmMapper extends BaseMapper<SysRzztsm> {
    List<Map<String, Object>> listBySystemId(@Param("zxtid") Integer zxtid);
    List<Map<String, Object>> listAll();
}
