package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysCjyszMx;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysCjyszMxMapper extends BaseMapper<SysCjyszMx> {

    @Select("<script>" +
            "SELECT z.cjid, z.syh, z.sb_djid, m.xmid, m.jyjg, " +
            "dzb.xs, COALESCE(dzb.yqxmdw, jf.xmdw) AS yqxmdw, jf.xmjd, " +
            "COALESCE(jf.xmzwmc, '') AS xmzwmc, " +
            "COALESCE(ckz.ckz, '') AS ckz, " +
            "CASE WHEN ckz.ckzgx IS NOT NULL AND CAST(m.jyjg AS DECIMAL(12,2)) > ckz.ckzgx THEN 'H' " +
            "     WHEN ckz.ckzdx IS NOT NULL AND CAST(m.jyjg AS DECIMAL(12,2)) &lt; ckz.ckzdx THEN 'L' " +
            "     ELSE '' END AS gdbj " +
            "FROM sys_cjysz_mx m " +
            "JOIN sys_cjysz_zb z ON m.cjid = z.cjid " +
            "LEFT JOIN sys_cjdzb dzb ON dzb.xmid = m.xmid AND dzb.sb_djid = z.sb_djid " +
            "LEFT JOIN (SELECT xmid, MAX(xmzwmc) AS xmzwmc, MAX(xmdw) AS xmdw, MAX(xmjd) AS xmjd FROM sys_jyxm_full GROUP BY xmid) jf ON jf.xmid = m.xmid " +
            "LEFT JOIN (SELECT xmid, MAX(ckz) AS ckz, MAX(ckzgx) AS ckzgx, MAX(ckzdx) AS ckzdx FROM sys_xmckz WHERE sb_djid = #{sbDjid} GROUP BY xmid) ckz ON ckz.xmid = m.xmid " +
            "WHERE z.sb_djid = #{sbDjid} " +
            "<if test='patientName != null and patientName != \"\"'>" +
            "AND z.syh IN (SELECT syh FROM bgxt_brxx WHERE brxm = #{patientName} AND sb_djid = #{sbDjid}) " +
            "</if>" +
            "<if test='patientName == null or patientName == \"\"'>" +
            "AND z.cjrq >= IFNULL(#{beginDate}, DATE_SUB(NOW(), INTERVAL 7 DAY)) " +
            "</if>" +
            "</script>")
    List<Map<String, Object>> selectExtractData(@Param("sbDjid") Integer sbDjid, @Param("beginDate") String beginDate, @Param("bz") Integer bz, @Param("patientName") String patientName);
}
