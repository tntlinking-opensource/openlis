package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtXmzhZb;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtXmzhZbMapper extends BaseMapper<BgxtXmzhZb> {

    List<Map<String, Object>> listCombos(Map<String, Object> params);

    List<Map<String, Object>> searchCombos(@Param("name") String name);

    List<Map<String, Object>> findUnassignedCombos(@Param("sbDjid") Integer sbDjid);

    @Update("UPDATE bgxt_xmzh_zb SET ddsj = (SELECT AVG(TIMESTAMPDIFF(MINUTE, b.srrq, b.shrq)) + #{buffer} " +
            "FROM bgxt_brxx b JOIN bgxt_his_xm hx ON b.brxx_id = hx.brxx_id " +
            "WHERE hx.zhid = bgxt_xmzh_zb.zhid AND b.ybzt IN (2,3) AND b.shrq IS NOT NULL " +
            "AND b.jyrq >= DATE_SUB(NOW(), INTERVAL 30 DAY))")
    void autoCalculateTat(@Param("buffer") Integer buffer);

    @Update("UPDATE bgxt_xmzh_zb SET ddsj = (SELECT AVG(TIMESTAMPDIFF(MINUTE, b.srrq, b.shrq)) + #{buffer} " +
            "FROM bgxt_brxx b JOIN bgxt_his_xm hx ON b.brxx_id = hx.brxx_id " +
            "WHERE hx.zhid = bgxt_xmzh_zb.zhid AND b.ybzt IN (2,3) AND b.shrq IS NOT NULL " +
            "AND b.jyrq >= DATE_SUB(NOW(), INTERVAL 30 DAY)) WHERE bgxt_xmzh_zb.zhid IN (SELECT zhid FROM bgxt_yqxmzh WHERE sb_djid = #{sbDjid})")
    void autoCalculateTatForInstrument(@Param("buffer") Integer buffer, @Param("sbDjid") Integer sbDjid);

    @Update("UPDATE bgxt_xmzh_zb zb " +
            "INNER JOIN gzsz_zhxm gz ON zb.zhid = gz.zhid " +
            "SET zb.yssm = #{sgys}, zb.zh_cjyq = #{cjyq}, zb.zh_zy = #{zysx} " +
            "WHERE gz.xlbh = #{xlbh}")
    void updateComboItemsFromSubcategory(@Param("xlbh") Integer xlbh, @Param("sgys") String sgys, @Param("cjyq") String cjyq, @Param("zysx") String zysx);
}
