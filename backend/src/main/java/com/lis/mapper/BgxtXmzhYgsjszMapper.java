package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtXmzhYgsjsz;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtXmzhYgsjszMapper extends BaseMapper<BgxtXmzhYgsjsz> {

    @Select("<script>" +
            "SELECT id, zhid, szlb, qssj, jssj, ygrq, " +
            "CASE WHEN ygrq = 0 THEN '当日' WHEN ygrq = 1 THEN '次日' WHEN ygrq = 2 THEN '第三日' ELSE '' END AS ygrqmc, " +
            "ygsj, ddsj, tybz FROM bgxt_xmzh_ygsjsz WHERE 1=1 " +
            "<if test='zhid != null'> AND zhid = #{zhid} </if>" +
            "<if test='szlb != null'> AND szlb = #{szlb} </if>" +
            "<if test='tybz != null'> AND tybz = #{tybz} </if>" +
            "ORDER BY zhid, qssj" +
            "</script>")
    List<Map<String, Object>> selectSettings(@Param("zhid") Integer zhid,
                                            @Param("szlb") Integer szlb,
                                            @Param("tybz") Integer tybz);

    @Insert("INSERT INTO bgxt_xmzh_ygsjsz (zhid, szlb, qssj, jssj, ygrq, ygsj, ddsj, tybz) " +
            "VALUES (#{zhid}, #{szlb}, #{qssj}, #{jssj}, #{ygrq}, #{ygsj}, #{ddsj}, #{tybz})")
    int insert(BgxtXmzhYgsjsz record);

    @Update("UPDATE bgxt_xmzh_ygsjsz SET szlb=#{szlb}, qssj=#{qssj}, jssj=#{jssj}, " +
            "ygrq=#{ygrq}, ygsj=#{ygsj}, ddsj=#{ddsj}, tybz=#{tybz} WHERE id=#{id}")
    int update(BgxtXmzhYgsjsz record);

    @Delete("DELETE FROM bgxt_xmzh_ygsjsz WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    @Select("SELECT COUNT(*) FROM bgxt_xmzh_ygsjsz WHERE zhid=#{zhid} AND id<>#{id} " +
            "AND TIME(qssj) <= TIME(#{qssj}) AND TIME(jssj) >= TIME(#{qssj})")
    int countTimeOverlap(@Param("zhid") Integer zhid, @Param("id") Integer id, @Param("qssj") String qssj, @Param("jssj") String jssj);
}
