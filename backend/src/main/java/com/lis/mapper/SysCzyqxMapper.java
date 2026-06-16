package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysCzyqx;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysCzyqxMapper extends BaseMapper<SysCzyqx> {

    @Select("<script>" +
            "SELECT DISTINCT c.czydm, c.czyxm, c.ksdm, k.ksmc " +
            "FROM sys_czydm c " +
            "LEFT JOIN sys_kssz k ON c.ksdm = k.ksdm " +
            "WHERE 1=1 " +
            "<if test='czyxm != null and czyxm != \"\"'>" +
            " AND (c.czydm LIKE CONCAT('%',#{czyxm},'%') OR c.czyxm LIKE CONCAT('%',#{czyxm},'%'))" +
            "</if>" +
            "<if test='ksmc != null and ksmc != \"\"'>" +
            " AND k.ksmc LIKE CONCAT('%',#{ksmc},'%')" +
            "</if>" +
            " ORDER BY c.czydm" +
            "</script>")
    List<Map<String, Object>> getOperators(@Param("czyxm") String czyxm, @Param("ksmc") String ksmc);

    @Select("SELECT zxtid, zxtjc, zxtmc FROM sys_zxtmc ORDER BY zxtid")
    List<Map<String, Object>> getSubsystems();

    @Select("SELECT DISTINCT mkfl FROM sys_mkb WHERE zxtid = #{zxtid} ORDER BY mkfl")
    List<Map<String, Object>> getModuleCategories(@Param("zxtid") Integer zxtid);

    @Select("<script>" +
            "SELECT m.mkdm, m.frm_name, m.action_name, m.caption, m.mkfl, " +
            "CASE WHEN EXISTS(SELECT 1 FROM sys_czymkqx q WHERE q.czydm = #{czydm} AND q.mkdm = m.mkdm) THEN 1 ELSE 0 END AS bz " +
            "FROM sys_mkb m " +
            "WHERE m.zxtid = #{zxtid} " +
            "<if test='mkfl != null and mkfl != \"\"'>" +
            " AND m.mkfl = #{mkfl}" +
            "</if>" +
            " ORDER BY m.mkdm" +
            "</script>")
    List<Map<String, Object>> getModules(@Param("zxtid") Integer zxtid, @Param("mkfl") String mkfl, @Param("czydm") String czydm);

    @Select("SELECT q.dldm, q.xldm, q.xlmc, " +
            "CASE WHEN EXISTS(SELECT 1 FROM sys_czyqx c WHERE c.czydm = #{czydm} AND c.qxxldm = q.xldm) THEN 1 ELSE 0 END AS bz " +
            "FROM sys_qxxl q WHERE q.dldm = #{dldm} ORDER BY q.xldm")
    List<Map<String, Object>> getPermissionItemsByDldm(@Param("dldm") String dldm, @Param("czydm") String czydm);

    @Select("SELECT id, zxtid, dldm, dlmc, kmjs FROM sys_qxdl WHERE zxtid = #{zxtid} ORDER BY dldm")
    List<Map<String, Object>> getPermissionCategories(@Param("zxtid") Integer zxtid);

    @Insert("INSERT INTO sys_czyqx (czydm, qxxldm) VALUES (#{czydm}, #{xldm})")
    void insertPermission(@Param("czydm") String czydm, @Param("xldm") String xldm);

    @Delete("DELETE FROM sys_czyqx WHERE czydm = #{czydm} AND qxxldm = #{xldm}")
    void deletePermission(@Param("czydm") String czydm, @Param("xldm") String xldm);

    @Insert("INSERT IGNORE INTO sys_czymkqx (czydm, mkdm, frm_name, action_name, caption) VALUES (#{czydm}, #{mkdm}, #{frmName}, #{actionName}, #{caption})")
    void insertModulePermission(@Param("czydm") String czydm, @Param("mkdm") Integer mkdm, @Param("frmName") String frmName, @Param("actionName") String actionName, @Param("caption") String caption);

    @Delete("DELETE FROM sys_czymkqx WHERE czydm = #{czydm} AND mkdm = #{mkdm}")
    void deleteModulePermission(@Param("czydm") String czydm, @Param("mkdm") Integer mkdm);

    @Select("SELECT qxxldm FROM sys_czyqx WHERE czydm = #{czydm}")
    List<String> getMyPermissions(@Param("czydm") String czydm);
}