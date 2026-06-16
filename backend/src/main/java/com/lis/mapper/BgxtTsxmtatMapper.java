package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtTsxmtat;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtTsxmtatMapper extends BaseMapper<BgxtTsxmtat> {

    @Select("SELECT t.sb_djid as sbDjid, t.brlb, t.syqk, t.zhid, t.TAT, z.zhmc, s.sbmc FROM bgxt_tsxmtat t LEFT JOIN bgxt_xmzh_zb z ON t.zhid=z.zhid LEFT JOIN sys_sbdjb s ON t.sb_djid=s.sb_djid ORDER BY t.sb_djid, t.syqk")
    List<Map<String, Object>> listTatSettings();

    @Insert("INSERT INTO bgxt_tsxmtat (sb_djid, brlb, syqk, zhid, zhmc, TAT) VALUES (#{sbDjid},#{brlb},#{syqk},#{zhid},#{zhmc},#{tat}) " +
            "ON DUPLICATE KEY UPDATE TAT=#{tat}, zhmc=#{zhmc}")
    void saveTatSetting(@Param("sbDjid") Integer sbDjid, @Param("brlb") Integer brlb, @Param("syqk") Integer syqk,
                        @Param("zhid") Integer zhid, @Param("zhmc") String zhmc, @Param("tat") Integer tat);

    @Delete("DELETE FROM bgxt_tsxmtat WHERE sb_djid=#{sbDjid} AND brlb=#{brlb} AND syqk=#{syqk} AND zhid=#{zhid}")
    void deleteTatSetting(@Param("sbDjid") Integer sbDjid, @Param("brlb") Integer brlb, @Param("syqk") Integer syqk, @Param("zhid") Integer zhid);

    @Select("SELECT z.zhmc, ROUND(AVG(TIMESTAMPDIFF(MINUTE, COALESCE(b.srrq, b.jyrq), b.shrq)),0) as avgMin, " +
            "COUNT(*) as cnt FROM bgxt_brxx b " +
            "LEFT JOIN bgxt_his_xm hx ON b.brxx_id = hx.brxx_id " +
            "LEFT JOIN bgxt_xmzh_zb z ON hx.zhid = z.zhid " +
            "WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') AND b.ybzt IN (2,3) AND b.shrq IS NOT NULL " +
            "GROUP BY hx.zhid, z.zhmc ORDER BY avgMin DESC LIMIT 30")
    List<Map<String, Object>> selectAvgTat(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT b.syqk, ROUND(AVG(TIMESTAMPDIFF(MINUTE, COALESCE(b.srrq, b.jyrq), b.shrq)),0) as avgMin, COUNT(*) as cnt " +
            "FROM bgxt_brxx b WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') AND b.ybzt IN (2,3) AND b.shrq IS NOT NULL " +
            "GROUP BY b.syqk ORDER BY b.syqk")
    List<Map<String, Object>> selectTatByUrgency(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT b.brxx_id, b.brxm, b.syh, z.zhmc, t.TAT, " +
            "TIMESTAMPDIFF(MINUTE, COALESCE(b.srrq, b.jyrq), b.shrq) as actualMin, " +
            "CASE WHEN TIMESTAMPDIFF(MINUTE, COALESCE(b.srrq, b.jyrq), b.shrq) > t.TAT THEN 1 ELSE 0 END as isOver " +
            "FROM bgxt_brxx b " +
            "LEFT JOIN bgxt_his_xm hx ON b.brxx_id = hx.brxx_id " +
            "LEFT JOIN bgxt_xmzh_zb z ON hx.zhid = z.zhid " +
            "LEFT JOIN bgxt_tsxmtat t ON hx.zhid = t.zhid " +
            "WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') AND b.ybzt IN (2,3) AND t.TAT IS NOT NULL " +
            "AND TIMESTAMPDIFF(MINUTE, COALESCE(b.srrq, b.jyrq), b.shrq) > t.TAT " +
            "ORDER BY actualMin DESC LIMIT 100")
    List<Map<String, Object>> selectTatOvertime(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT DATE(b.jyrq) as date, ROUND(AVG(TIMESTAMPDIFF(MINUTE, COALESCE(b.srrq, b.jyrq), b.shrq)),0) as avgMin, COUNT(*) as cnt " +
            "FROM bgxt_brxx b WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') AND b.ybzt IN (2,3) AND b.shrq IS NOT NULL " +
            "GROUP BY DATE(b.jyrq) ORDER BY date")
    List<Map<String, Object>> selectTatTrend(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT " +
            "COALESCE(ROUND(MAX(CASE WHEN rk <= 0.50 THEN elapsed END),0),0) as p50, " +
            "COALESCE(ROUND(MAX(CASE WHEN rk <= 0.90 THEN elapsed END),0),0) as p90, " +
            "COALESCE(ROUND(MAX(CASE WHEN rk <= 0.95 THEN elapsed END),0),0) as p95, " +
            "COALESCE(ROUND(MAX(CASE WHEN rk <= 0.99 THEN elapsed END),0),0) as p99 " +
            "FROM (SELECT TIMESTAMPDIFF(MINUTE, COALESCE(srrq, jyrq), shrq) as elapsed, " +
            "PERCENT_RANK() OVER (ORDER BY TIMESTAMPDIFF(MINUTE, COALESCE(srrq, jyrq), shrq)) as rk " +
            "FROM bgxt_brxx WHERE jyrq >= #{beginDate} AND jyrq <= CONCAT(#{endDate}, ' 23:59:59') AND ybzt IN (2,3) AND shrq IS NOT NULL) t")
    Map<String, Object> selectTatPercentiles(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT '核收→检验' as phase, COALESCE(ROUND(AVG(TIMESTAMPDIFF(MINUTE, COALESCE(b.srrq, b.jyrq), b.jyrq)),0),0) as avgMin, COUNT(*) as cnt " +
            "FROM bgxt_brxx b WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') AND b.ybzt IN (2,3) AND b.jyrq IS NOT NULL " +
            "UNION ALL " +
            "SELECT '检验→审核' as phase, COALESCE(ROUND(AVG(TIMESTAMPDIFF(MINUTE, b.jyrq, b.shrq)),0),0) as avgMin, COUNT(*) as cnt " +
            "FROM bgxt_brxx b WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') AND b.ybzt IN (2,3) AND b.shrq IS NOT NULL AND b.jyrq IS NOT NULL " +
            "UNION ALL " +
            "SELECT '核收→审核' as phase, COALESCE(ROUND(AVG(TIMESTAMPDIFF(MINUTE, COALESCE(b.srrq, b.jyrq), b.shrq)),0),0) as avgMin, COUNT(*) as cnt " +
            "FROM bgxt_brxx b WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') AND b.ybzt IN (2,3) AND b.shrq IS NOT NULL")
    List<Map<String, Object>> selectTatPhaseStats(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT d.date, d.avgMin, d.cnt, ROUND(AVG(t.TAT),0) as targetMin " +
            "FROM (SELECT DATE(b.jyrq) as date, ROUND(AVG(TIMESTAMPDIFF(MINUTE, COALESCE(b.srrq, b.jyrq), b.shrq)),0) as avgMin, COUNT(*) as cnt " +
            "FROM bgxt_brxx b WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') AND b.ybzt IN (2,3) AND b.shrq IS NOT NULL " +
            "GROUP BY DATE(b.jyrq)) d " +
            "CROSS JOIN (SELECT AVG(TAT) as TAT FROM bgxt_tsxmtat WHERE TAT IS NOT NULL AND TAT > 0) t " +
            "GROUP BY d.date, d.avgMin, d.cnt ORDER BY d.date")
    List<Map<String, Object>> selectTatTrendWithTarget(@Param("beginDate") String beginDate, @Param("endDate") String endDate);
}
