package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.ZkNyzkjg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ZkNyzkjgMapper extends BaseMapper<ZkNyzkjg> {
    List<Map<String, Object>> findDailyResults(@Param("zkpid") Integer zkpid, @Param("date") String date,
                                                @Param("days") Integer days);
    int insertResultWithDate(@Param("zkxmid") Integer zkxmid, @Param("yssj") String yssj, @Param("yhsj") String yhsj,
                             @Param("resultDate") String resultDate, @Param("sybz") int sybz, @Param("skbz") int skbz);
    int insertResultWithCurdate(@Param("zkxmid") Integer zkxmid, @Param("yssj") String yssj, @Param("yhsj") String yhsj,
                                @Param("sybz") int sybz, @Param("skbz") int skbz);
    int deleteResult(@Param("id") Integer id);
    List<Map<String, Object>> findAnalysisData(@Param("zkpid") Integer zkpid, @Param("zkxmid") Integer zkxmid,
                                               @Param("begDate") String begDate, @Param("endDate") String endDate,
                                               @Param("days") int days);
    List<Map<String, Object>> findQcData(@Param("zkxmid") Integer zkxmid,
                                         @Param("begDate") String begDate, @Param("endDate") String endDate);

    List<Map<String, Object>> findRecentResults(@Param("zkxmid") Integer zkxmid, @Param("limit") int limit);

    List<Map<String, Object>> selectCvTrend(@Param("zkxmid") Integer zkxmid, @Param("zkpid") Integer zkpid,
                                             @Param("begDate") String begDate, @Param("endDate") String endDate);

    List<Map<String, Object>> selectZScoreData(@Param("zkxmid") Integer zkxmid, @Param("zkpid") Integer zkpid,
                                                @Param("begDate") String begDate, @Param("endDate") String endDate);

    Map<String, Object> selectQcStats(@Param("zkxmid") Integer zkxmid, @Param("zkpid") Integer zkpid,
                                       @Param("begDate") String begDate, @Param("endDate") String endDate);

    List<Map<String, Object>> selectProductsWithData();
}
