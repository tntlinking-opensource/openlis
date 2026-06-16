package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtJyjg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;

import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtJyjgMapper extends BaseMapper<BgxtJyjg> {

    Integer countByBrxxId(@Param("brxxId") Integer brxxId);

    Integer countEmptyByBrxxId(@Param("brxxId") Integer brxxId);

    List<Map<String, Object>> selectNegativeResults(@Param("brxxId") Integer brxxId);

    List<Map<String, Object>> selectResultsByBrxxId(@Param("brxxId") Integer brxxId);

    List<Map<String, Object>> selectReportResultsByBrxxId(@Param("brxxId") Integer brxxId);

    int deleteByBrxxId(@Param("brxxId") Integer brxxId);

    int insertResult(@Param("brxxId") Integer brxxId, @Param("xmid") Integer xmid, @Param("jyjg") String jyjg,
                     @Param("ckz") String ckz, @Param("ckzgx") Object ckzgx, @Param("ckzdx") Object ckzdx,
                     @Param("bjzgx") Object bjzgx, @Param("bjzdx") Object bjzdx, @Param("gdbj") String gdbj);

    int insertExtractResult(@Param("brxxId") Integer brxxId, @Param("xmid") Integer xmid,
                            @Param("jyjg") String jyjg, @Param("ckz") String ckz,
                            @Param("ckzgx") Object ckzgx, @Param("ckzdx") Object ckzdx,
                            @Param("bjzgx") Object bjzgx, @Param("bjzdx") Object bjzdx,
                            @Param("gdbj") String gdbj, @Param("cjmxid") Object cjmxid);

    int updateExtractResult(@Param("brxxId") Integer brxxId, @Param("xmid") Integer xmid,
                            @Param("jyjg") String jyjg, @Param("ckz") String ckz,
                            @Param("ckzgx") Object ckzgx, @Param("ckzdx") Object ckzdx,
                            @Param("bjzgx") Object bjzgx, @Param("bjzdx") Object bjzdx,
                            @Param("gdbj") String gdbj, @Param("cjmxid") Object cjmxid);

    Integer countByBrxxIdAndXmid(@Param("brxxId") Integer brxxId, @Param("xmid") Integer xmid);

    int insertJyjgLog(@Param("brxxId") Integer brxxId);

    int deleteDuplicateByBrxxIdAndXmid(@Param("brxxId") Integer brxxId, @Param("xmid") Integer xmid);

    @Select("SELECT brxx_id, COUNT(*) as cnt FROM bgxt_jyjg GROUP BY brxx_id ORDER BY brxx_id DESC LIMIT 10")
    List<Map<String, Object>> selectRecentResults();

    @Select("SELECT id, zhid, xmdm, xmzwmc, xmdw, xh, xmid, mrjg, sb_djid FROM bgxt_xmzh_mx WHERE zhid = #{zhid} LIMIT 5")
    List<Map<String, Object>> selectMxTableData(@Param("zhid") Integer zhid);

    @Select("SELECT COUNT(*) FROM bgxt_xmzh_mx")
    Long selectCount();

    @Select("SELECT COUNT(*) FROM bgxt_xmzh_mx WHERE zhid = #{zhid}")
    Long selectCountByZhid(@Param("zhid") Integer zhid);

    @Select("SELECT id, zhid, xmdm, xmzwmc, xmdw, xh, xmid, mrjg, sb_djid FROM bgxt_xmzh_mx WHERE zhid = #{zhid} LIMIT 5")
    List<Map<String, Object>> selectComboItemsSample(@Param("zhid") Integer zhid);

    @Select("SELECT COALESCE(jf.xmzwmc, jg.xmdm, '未知项目') as xmzwmc, COUNT(DISTINCT jg.brxx_id) as cnt FROM bgxt_jyjg jg " +
            "LEFT JOIN sys_jyxm_full jf ON jg.xmdm=jf.xmdm " +
            "LEFT JOIN bgxt_brxx b ON jg.brxx_id=b.brxx_id " +
            "WHERE b.jyrq BETWEEN #{beginDate} AND #{endDate} AND jg.jyjg IS NOT NULL AND jg.jyjg <> '' " +
            "GROUP BY jg.xmdm, jf.xmzwmc ORDER BY cnt DESC LIMIT 50")
    List<Map<String, Object>> selectStatsByItem(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT jf.xmzwmc as itemName, jf.xmdm as itemCode, " +
            "SUM(CASE WHEN b.brlb=1 THEN 1 ELSE 0 END) as mzrs, " +
            "SUM(CASE WHEN b.brlb=2 THEN 1 ELSE 0 END) as zyrs, " +
            "SUM(CASE WHEN b.brlb=3 THEN 1 ELSE 0 END) as tjrs, " +
            "SUM(CASE WHEN b.brlb=4 THEN 1 ELSE 0 END) as qtrs, " +
            "COUNT(DISTINCT b.brxx_id) as zrs " +
            "FROM bgxt_jyjg jg " +
            "JOIN bgxt_brxx b ON jg.brxx_id = b.brxx_id " +
            "LEFT JOIN sys_jyxm_full jf ON jg.xmdm = jf.xmdm " +
            "WHERE b.jyrq BETWEEN #{beginDate} AND #{endDate} AND jg.jyjg IS NOT NULL AND jg.jyjg <> '' " +
            "GROUP BY jg.xmdm, jf.xmzwmc, jf.xmdm ORDER BY zrs DESC")
    List<Map<String, Object>> selectWorkloadByItem(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("brlb") Integer brlb);

    @Select("SELECT b.brxx_id, b.brxm, b.brxb, b.brnl, b.brlb, b.jyrq, b.ybzt, " +
            "jg.jyjg, jg.gdbj, k.ksmc " +
            "FROM bgxt_jyjg jg JOIN bgxt_brxx b ON jg.brxx_id = b.brxx_id " +
            "LEFT JOIN sys_jyxm_full jf ON jg.xmdm = jf.xmdm " +
            "LEFT JOIN sys_kssz k ON b.ksdm = k.ksdm " +
            "WHERE b.jyrq BETWEEN #{beginDate} AND #{endDate} AND jf.xmid = #{xmid} ORDER BY b.jyrq")
    List<Map<String, Object>> selectWorkloadDetail(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("xmid") Integer xmid);

    @Select("SELECT jf.xmzwmc as name, COUNT(DISTINCT jg.brxx_id) as cnt FROM bgxt_jyjg jg " +
            "LEFT JOIN sys_jyxm_full jf ON jg.xmdm=jf.xmdm LEFT JOIN bgxt_brxx b ON jg.brxx_id=b.brxx_id " +
            "WHERE b.jyrq BETWEEN #{beginDate} AND #{endDate} AND jg.jyjg IS NOT NULL GROUP BY jg.xmdm, jf.xmzwmc ORDER BY cnt DESC LIMIT 30")
    List<Map<String, Object>> selectCustomReportByItem(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT jg.xmdm, jf.xmzwmc FROM bgxt_jyjg jg " +
            "LEFT JOIN sys_jyxm_full jf ON jg.xmdm = jf.xmdm " +
            "WHERE jg.brxx_id = #{brxxId} AND jg.jyjg IS NOT NULL AND jg.jyjg <> '' " +
            "AND (jg.ckz IS NULL OR jg.ckz = '')")
    List<Map<String, Object>> findEmptyCkz(@Param("brxxId") Integer brxxId);

    @Select("SELECT jf.xmzwmc FROM bgxt_jyjg jg LEFT JOIN sys_jyxm_full jf ON jg.xmdm = jf.xmdm " +
            "WHERE jg.brxx_id = #{brxxId} AND (jg.jyjg IS NULL OR jg.jyjg = '')")
    List<Map<String, Object>> findEmptyResults(@Param("brxxId") Integer brxxId);

    @Select("SELECT jf.xmzwmc, jg.jyjg FROM bgxt_jyjg jg LEFT JOIN sys_jyxm_full jf ON jg.xmdm = jf.xmdm " +
            "WHERE jg.brxx_id = #{brxxId} AND jg.jyjg REGEXP '^-?[0-9]' AND CAST(jg.jyjg AS DECIMAL) < 0")
    List<Map<String, Object>> findNegativeResults(@Param("brxxId") Integer brxxId);

    @Select("SELECT jf.xmzwmc FROM bgxt_jyjg jg LEFT JOIN sys_jyxm_full jf ON jg.xmdm = jf.xmdm " +
            "WHERE jg.brxx_id = #{brxxId} AND jg.jyjg = '0'")
    List<Map<String, Object>> findZeroResults(@Param("brxxId") Integer brxxId);

    @Select("SELECT jf.xmzwmc, jg.jyjg, jg.gdbj FROM bgxt_jyjg jg " +
            "LEFT JOIN sys_jyxm_full jf ON jg.xmdm = jf.xmdm " +
            "WHERE jg.brxx_id = #{brxxId} AND jg.gdbj IN ('↑↑','↓↓','bjgbh','bjdbh')")
    List<Map<String, Object>> findCriticalResults(@Param("brxxId") Integer brxxId);

    @Select("SELECT jf.xmzwmc, jg.jyjg FROM bgxt_jyjg jg " +
            "LEFT JOIN sys_jyxm_full jf ON jg.xmdm = jf.xmdm " +
            "WHERE jg.brxx_id IN (SELECT b2.brxx_id FROM bgxt_brxx b2 " +
            "WHERE b2.brxm = (SELECT brxm FROM bgxt_brxx WHERE brxx_id = #{brxxId}) " +
            "AND b2.ybzt IN (2,3) AND b2.brxx_id <> #{brxxId} ORDER BY b2.shrq DESC LIMIT 1) " +
            "AND jg.jyjg IS NOT NULL")
    List<Map<String, Object>> findHistoricalResults(@Param("brxxId") Integer brxxId);

    @Select("SELECT COUNT(*) FROM bgxt_jyjg WHERE brxx_id = #{brxxId} AND jyjg IS NOT NULL AND jyjg <> '' AND COALESCE(zfbz,0) = 0")
    Integer countValidResults(@Param("brxxId") Integer brxxId);

    @Update("UPDATE bgxt_jyjg jg JOIN (SELECT MAX(xmdm) AS xmdm FROM sys_jyxm_full WHERE xmid = #{xmid} GROUP BY xmid) jf ON jg.xmdm = jf.xmdm SET jg.jyjg=#{jyjg} WHERE jg.brxx_id=#{brxxId}")
    int updateResultByBrxxIdAndXmid(@Param("brxxId") Integer brxxId, @Param("xmid") Integer xmid, @Param("jyjg") String jyjg);

    @Insert("INSERT INTO bgxt_jyjg (brxx_id, xmdm, jyjg, czri) SELECT #{brxxId}, jf.xmdm, #{jyjg}, NOW() FROM sys_jyxm_full jf WHERE jf.xmid = #{xmid}")
    int insertResultSimple(@Param("brxxId") Integer brxxId, @Param("xmid") Integer xmid, @Param("jyjg") String jyjg);

    @Select("SELECT jf.xmzwmc, jg.jyjg FROM bgxt_jyjg jg " +
            "LEFT JOIN sys_jyxm_full jf ON jg.xmdm = jf.xmdm " +
            "WHERE jg.brxx_id = #{brxxId} AND jg.jyjg IS NOT NULL " +
            "AND jg.jyjg REGEXP '[^0-9.,\\-<>=↑↓]'")
    List<Map<String, Object>> findSpecialResults(@Param("brxxId") Integer brxxId);

    @Select("SELECT j.xmzwmc, j.jyjg, j.ckz, j.gdbj FROM bgxt_jyjg j WHERE j.brxx_id = #{brxxId} ORDER BY j.xmdm")
    List<Map<String, Object>> selectSampleResults(@Param("brxxId") Integer brxxId);

    @Select("SELECT z.zhid, z.zhmc, z.his_xmdm, COALESCE(z.sfbz, 0) AS dj, " +
            "SUM(CASE WHEN b.brlb=1 THEN 1 ELSE 0 END) AS mzrs, " +
            "SUM(CASE WHEN b.brlb=1 THEN COALESCE(z.sfbz, 0) ELSE 0 END) AS mzfy, " +
            "SUM(CASE WHEN b.brlb=2 THEN 1 ELSE 0 END) AS zyrs, " +
            "SUM(CASE WHEN b.brlb=2 THEN COALESCE(z.sfbz, 0) ELSE 0 END) AS zyfy, " +
            "SUM(CASE WHEN b.brlb=3 THEN 1 ELSE 0 END) AS tjrs, " +
            "SUM(CASE WHEN b.brlb=3 THEN COALESCE(z.sfbz, 0) ELSE 0 END) AS tjfy, " +
            "SUM(CASE WHEN COALESCE(b.brlb,0) NOT IN (1,2,3) THEN 1 ELSE 0 END) AS qtrs, " +
            "SUM(CASE WHEN COALESCE(b.brlb,0) NOT IN (1,2,3) THEN COALESCE(z.sfbz, 0) ELSE 0 END) AS qtfy, " +
            "COUNT(DISTINCT b.brxx_id) AS zrs, " +
            "SUM(COALESCE(z.sfbz, 0)) AS zfy " +
            "FROM bgxt_his_xm h " +
            "JOIN bgxt_brxx b ON h.brxx_id = b.brxx_id " +
            "JOIN bgxt_xmzh_zb z ON h.zhid = z.zhid " +
            "WHERE DATE(b.jyrq) BETWEEN #{beginDate} AND #{endDate} " +
            "AND h.zfbz = 0 " +
            "GROUP BY z.zhid, z.zhmc, z.his_xmdm, z.sfbz ORDER BY zrs DESC")
    List<Map<String, Object>> selectWorkloadByItemV2(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT b.brxx_id, b.brxx_tmh AS sjh, b.brxm AS xm, b.brxb AS xb, b.brnl AS nl, COALESCE((SELECT k.ksmc FROM sys_kssz k WHERE k.ksdm = b.ksdm OR k.ksmc = b.ksdm LIMIT 1), b.ksdm) AS ksmc, " +
            "COALESCE(b.sjys, '') AS sjys, COALESCE(b.jyys, '') AS jyys, " +
            "CASE b.brlb WHEN 1 THEN '门诊' WHEN 2 THEN '住院' WHEN 3 THEN '体检' ELSE '其他' END AS brlb, " +
            "DATE_FORMAT(b.jyrq, '%Y-%m-%d %H:%i') AS jysj, COALESCE(z.sfbz, 0) AS fee " +
            "FROM bgxt_his_xm h JOIN bgxt_brxx b ON h.brxx_id = b.brxx_id " +
            "JOIN bgxt_xmzh_zb z ON h.zhid = z.zhid " +
            "WHERE DATE(b.jyrq) BETWEEN #{beginDate} AND #{endDate} AND h.zfbz = 0 AND z.zhid = #{zhid} " +
            "ORDER BY b.jyrq DESC")
    List<Map<String, Object>> selectWorkloadItemDetailV2(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("zhid") Integer zhid);

    @Select("SELECT COALESCE((SELECT k.ksmc FROM sys_kssz k WHERE k.ksdm = b.ksdm OR k.ksmc = b.ksdm LIMIT 1), b.ksdm) AS ksmc, " +
            "COUNT(DISTINCT b.brxx_id) AS sqcs, " +
            "SUM(CASE WHEN b.brlb=1 THEN 1 ELSE 0 END) AS mzrs, SUM(CASE WHEN b.brlb=1 THEN COALESCE(z.sfbz,0) ELSE 0 END) AS mzfy, " +
            "SUM(CASE WHEN b.brlb=2 THEN 1 ELSE 0 END) AS zyrs, SUM(CASE WHEN b.brlb=2 THEN COALESCE(z.sfbz,0) ELSE 0 END) AS zyfy, " +
            "SUM(CASE WHEN b.brlb=3 THEN 1 ELSE 0 END) AS tjrs, SUM(CASE WHEN b.brlb=3 THEN COALESCE(z.sfbz,0) ELSE 0 END) AS tjfy, " +
            "SUM(CASE WHEN COALESCE(b.brlb,0) NOT IN (1,2,3) THEN 1 ELSE 0 END) AS qtrs, SUM(CASE WHEN COALESCE(b.brlb,0) NOT IN (1,2,3) THEN COALESCE(z.sfbz,0) ELSE 0 END) AS qtfy, " +
            "COUNT(DISTINCT b.brxx_id) AS zrs, SUM(COALESCE(z.sfbz,0)) AS zfy " +
            "FROM bgxt_his_xm h JOIN bgxt_brxx b ON h.brxx_id = b.brxx_id " +
            "JOIN bgxt_xmzh_zb z ON h.zhid = z.zhid " +
            "WHERE DATE(b.jyrq) BETWEEN #{beginDate} AND #{endDate} AND h.zfbz = 0 " +
            "GROUP BY b.ksdm ORDER BY zrs DESC")
    List<Map<String, Object>> selectWorkloadByDept(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT b.brxx_id, b.brxx_tmh AS sjh, b.brxm AS xm, b.brxb AS xb, b.brnl AS nl, COALESCE((SELECT k.ksmc FROM sys_kssz k WHERE k.ksdm = b.ksdm OR k.ksmc = b.ksdm LIMIT 1), b.ksdm) AS ksmc, " +
            "COALESCE(b.sjys, '') AS sjys, COALESCE(b.jyys, '') AS jyys, " +
            "CASE b.brlb WHEN 1 THEN '门诊' WHEN 2 THEN '住院' WHEN 3 THEN '体检' ELSE '其他' END AS brlb, " +
            "DATE_FORMAT(b.jyrq, '%Y-%m-%d %H:%i') AS jysj, COALESCE(z.sfbz, 0) AS fee " +
            "FROM bgxt_his_xm h JOIN bgxt_brxx b ON h.brxx_id = b.brxx_id " +
            "JOIN bgxt_xmzh_zb z ON h.zhid = z.zhid " +
            "WHERE DATE(b.jyrq) BETWEEN #{beginDate} AND #{endDate} AND h.zfbz = 0 " +
            "AND (b.ksdm = #{ksmc} OR b.ksdm IN (SELECT k.ksdm FROM sys_kssz k WHERE k.ksmc = #{ksmc})) " +
            "ORDER BY b.jyrq DESC")
    List<Map<String, Object>> selectWorkloadDeptDetail(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("ksmc") String ksmc);

    @Select("SELECT COALESCE(b.sjys, '') AS sjys, " +
            "COUNT(DISTINCT b.brxx_id) AS sqcs, " +
            "SUM(CASE WHEN b.brlb=1 THEN 1 ELSE 0 END) AS mzrs, SUM(CASE WHEN b.brlb=1 THEN COALESCE(z.sfbz,0) ELSE 0 END) AS mzfy, " +
            "SUM(CASE WHEN b.brlb=2 THEN 1 ELSE 0 END) AS zyrs, SUM(CASE WHEN b.brlb=2 THEN COALESCE(z.sfbz,0) ELSE 0 END) AS zyfy, " +
            "SUM(CASE WHEN b.brlb=3 THEN 1 ELSE 0 END) AS tjrs, SUM(CASE WHEN b.brlb=3 THEN COALESCE(z.sfbz,0) ELSE 0 END) AS tjfy, " +
            "SUM(CASE WHEN COALESCE(b.brlb,0) NOT IN (1,2,3) THEN 1 ELSE 0 END) AS qtrs, SUM(CASE WHEN COALESCE(b.brlb,0) NOT IN (1,2,3) THEN COALESCE(z.sfbz,0) ELSE 0 END) AS qtfy, " +
            "COUNT(DISTINCT b.brxx_id) AS zrs, SUM(COALESCE(z.sfbz,0)) AS zfy " +
            "FROM bgxt_his_xm h JOIN bgxt_brxx b ON h.brxx_id = b.brxx_id " +
            "JOIN bgxt_xmzh_zb z ON h.zhid = z.zhid " +
            "WHERE DATE(b.jyrq) BETWEEN #{beginDate} AND #{endDate} AND h.zfbz = 0 " +
            "GROUP BY b.sjys ORDER BY zrs DESC")
    List<Map<String, Object>> selectWorkloadByDoctor(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT b.brxx_id, b.brxx_tmh AS sjh, b.brxm AS xm, b.brxb AS xb, b.brnl AS nl, COALESCE((SELECT k.ksmc FROM sys_kssz k WHERE k.ksdm = b.ksdm OR k.ksmc = b.ksdm LIMIT 1), b.ksdm) AS ksmc, " +
            "COALESCE(b.sjys, '') AS sjys, COALESCE(b.jyys, '') AS jyys, " +
            "CASE b.brlb WHEN 1 THEN '门诊' WHEN 2 THEN '住院' WHEN 3 THEN '体检' ELSE '其他' END AS brlb, " +
            "DATE_FORMAT(b.jyrq, '%Y-%m-%d %H:%i') AS jysj, COALESCE(z.sfbz, 0) AS fee " +
            "FROM bgxt_his_xm h JOIN bgxt_brxx b ON h.brxx_id = b.brxx_id " +
            "JOIN bgxt_xmzh_zb z ON h.zhid = z.zhid " +
            "WHERE DATE(b.jyrq) BETWEEN #{beginDate} AND #{endDate} AND h.zfbz = 0 AND b.sjys = #{sjys} " +
            "ORDER BY b.jyrq DESC")
    List<Map<String, Object>> selectWorkloadDoctorDetail(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("sjys") String sjys);

    @Select("SELECT COALESCE(b.jyys, '') AS jyys, " +
            "COUNT(DISTINCT b.brxx_id) AS sqcs, " +
            "SUM(CASE WHEN b.brlb=1 THEN 1 ELSE 0 END) AS mzrs, SUM(CASE WHEN b.brlb=1 THEN COALESCE(z.sfbz,0) ELSE 0 END) AS mzfy, " +
            "SUM(CASE WHEN b.brlb=2 THEN 1 ELSE 0 END) AS zyrs, SUM(CASE WHEN b.brlb=2 THEN COALESCE(z.sfbz,0) ELSE 0 END) AS zyfy, " +
            "SUM(CASE WHEN b.brlb=3 THEN 1 ELSE 0 END) AS tjrs, SUM(CASE WHEN b.brlb=3 THEN COALESCE(z.sfbz,0) ELSE 0 END) AS tjfy, " +
            "SUM(CASE WHEN COALESCE(b.brlb,0) NOT IN (1,2,3) THEN 1 ELSE 0 END) AS qtrs, SUM(CASE WHEN COALESCE(b.brlb,0) NOT IN (1,2,3) THEN COALESCE(z.sfbz,0) ELSE 0 END) AS qtfy, " +
            "COUNT(DISTINCT b.brxx_id) AS zrs, SUM(COALESCE(z.sfbz,0)) AS zfy " +
            "FROM bgxt_his_xm h JOIN bgxt_brxx b ON h.brxx_id = b.brxx_id " +
            "JOIN bgxt_xmzh_zb z ON h.zhid = z.zhid " +
            "WHERE DATE(b.jyrq) BETWEEN #{beginDate} AND #{endDate} AND h.zfbz = 0 " +
            "GROUP BY b.jyys ORDER BY zrs DESC")
    List<Map<String, Object>> selectWorkloadByExaminer(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT b.brxx_id, b.brxx_tmh AS sjh, b.brxm AS xm, b.brxb AS xb, b.brnl AS nl, COALESCE((SELECT k.ksmc FROM sys_kssz k WHERE k.ksdm = b.ksdm OR k.ksmc = b.ksdm LIMIT 1), b.ksdm) AS ksmc, " +
            "COALESCE(b.sjys, '') AS sjys, COALESCE(b.jyys, '') AS jyys, " +
            "CASE b.brlb WHEN 1 THEN '门诊' WHEN 2 THEN '住院' WHEN 3 THEN '体检' ELSE '其他' END AS brlb, " +
            "DATE_FORMAT(b.jyrq, '%Y-%m-%d %H:%i') AS jysj, COALESCE(z.sfbz, 0) AS fee " +
            "FROM bgxt_his_xm h JOIN bgxt_brxx b ON h.brxx_id = b.brxx_id " +
            "JOIN bgxt_xmzh_zb z ON h.zhid = z.zhid " +
            "WHERE DATE(b.jyrq) BETWEEN #{beginDate} AND #{endDate} AND h.zfbz = 0 AND b.jyys = #{jyys} " +
            "ORDER BY b.jyrq DESC")
    List<Map<String, Object>> selectWorkloadExaminerDetail(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("jyys") String jyys);
}
