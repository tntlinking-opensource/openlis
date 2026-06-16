package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysKssz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysKsszMapper extends BaseMapper<SysKssz> {
    List<Map<String, Object>> findAll();
    List<Map<String, Object>> findByKsdm(@Param("ksdm") String ksdm);
    List<Map<String, Object>> findOneByKsdm(@Param("ksdm") String ksdm);
    void insertDept(SysKssz dept);
    void updateByKsdm(SysKssz dept);
    int cleanupGarbled();
    List<Map<String, Object>> selectActiveDepartments();
}
