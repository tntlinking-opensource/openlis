package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.ReportVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReportVersionMapper extends BaseMapper<ReportVersion> {
    List<Map<String, Object>> listAll();

    List<Map<String, Object>> search(@Param("keyword") String keyword);

    Map<String, Object> getById(@Param("bbId") Integer bbId);

    int countByBbId(@Param("bbId") Integer bbId);

    int countByMr(@Param("mr") Integer mr);

    void insert(Map<String, Object> data);

    void update(Map<String, Object> data);

    void updateMr(@Param("bbId") Integer bbId, @Param("mr") Integer mr);

    void clearOtherMr(@Param("bbId") Integer bbId);

    void deleteById(@Param("bbId") Integer bbId);

    String getMrtById(@Param("bbId") Integer bbId);

    void updateMrt(@Param("bbId") Integer bbId, @Param("mrt") String mrt);

    void insertWithMrt(Map<String, Object> data);
}