package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtCriticalValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtCriticalValueMapper extends BaseMapper<BgxtCriticalValue> {

    List<Map<String, Object>> selectList(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    int insert(@Param("reportId") Integer reportId, @Param("criticalValue") String criticalValue,
               @Param("addOperCode") String addOperCode, @Param("addOperName") String addOperName,
               @Param("xmid") Integer xmid);

    int softDelete(@Param("id") Integer id, @Param("cancelOperCode") String cancelOperCode);

    int processBatch(@Param("ids") List<Integer> ids, @Param("processOperName") String processOperName);

    Integer countByDateRange(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    List<Map<String, Object>> selectStatsByDepartment(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    List<Map<String, Object>> selectStatsByItem(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    List<Map<String, Object>> selectStatsByOperator(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    List<Map<String, Object>> selectStatsByMonth(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    List<Map<String, Object>> selectStatsByUrgency(@Param("beginDate") String beginDate, @Param("endDate") String endDate);
}
