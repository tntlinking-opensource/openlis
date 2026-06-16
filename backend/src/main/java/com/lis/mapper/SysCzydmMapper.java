package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysCzydm;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysCzydmMapper extends BaseMapper<SysCzydm> {

    @Select("<script>" +
        "SELECT c.czydm, c.czyxm, c.pym, c.ksdm, k.ksmc, " +
        "c.gzzdm, IFNULL(g.gzmc, '') AS gzzmc, IFNULL(g.sybz, 1) AS gzz_sybz " +
        "FROM sys_czydm c " +
        "LEFT JOIN sys_kssz k ON CAST(c.ksdm AS CHAR) = CAST(k.ksdm AS CHAR) " +
        "LEFT JOIN sys_gzzd g ON CAST(c.gzzdm AS CHAR) = CAST(g.gzdm AS CHAR) " +
        "WHERE 1 = 1 " +
        "<if test='keyword != null and keyword != \"\"'>" +
        " AND (c.czydm LIKE CONCAT('%',#{keyword},'%') OR c.czyxm LIKE CONCAT('%',#{keyword},'%') OR c.pym LIKE CONCAT('%',#{keyword},'%')) " +
        "</if>" +
        "<if test='ksdm != null and ksdm != \"\"'>" +
        " AND c.ksdm = #{ksdm} " +
        "</if>" +
        "<if test='gzzdm != null and gzzdm != \"\"'>" +
        " AND c.gzzdm = #{gzzdm} " +
        "</if>" +
        " ORDER BY c.czydm" +
        "</script>")
    List<Map<String, Object>> findGroupList(@Param("keyword") String keyword, @Param("ksdm") String ksdm, @Param("gzzdm") String gzzdm);

    @Select("SELECT czydm FROM sys_czydm WHERE czydm = #{czydm}")
    List<Map<String, Object>> existsByCzydm(@Param("czydm") String czydm);
}
