package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtSampleReject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtSampleRejectMapper extends BaseMapper<BgxtSampleReject> {

    List<Map<String, Object>> selectByDateRange(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    Map<String, Object> selectByBrxxId(@Param("brxxId") Integer brxxId);
}
