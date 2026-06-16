package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysXmckz;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysXmckzMapper extends BaseMapper<SysXmckz> {

    @Select("SELECT id, xmid, bbsgbz, bbzl, xbsgbz, brxb, nlsgbz, nllx, nlsx, nlxx, " +
        "xmdm, ckz, jszgx, jszdx, fczgx, fczdx, zdshbz, zdshgx, zdshdx, zdshcyqj, " +
        "jgfctsbz, sb_djid, ckzgx, ckzdx, bjzgx, bjzdx " +
        "FROM sys_xmckz WHERE xmid = #{xmid} AND (sb_djid IS NULL OR sb_djid = 0 OR sb_djid = #{instId}) ORDER BY id")
    List<Map<String, Object>> getRefRanges(@Param("xmid") Integer xmid, @Param("instId") Integer instId);

    @Insert("INSERT INTO sys_xmckz (xmid, bbsgbz, bbzl, xbsgbz, brxb, nlsgbz, nllx, " +
        "nlsx, nlxx, xmdm, ckz, jszgx, jszdx, fczgx, fczdx, zdshbz, zdshgx, zdshdx, zdshcyqj, " +
        "jgfctsbz, sb_djid, ckzgx, ckzdx, bjzgx, bjzdx) " +
        "VALUES (#{xmid}, #{bbsgbz}, #{bbzl}, #{xbsgbz}, #{brxb}, #{nlsgbz}, #{nllx}, #{nlsx}, #{nlxx}, " +
        "#{xmdm}, #{ckz}, #{jszgx}, #{jszdx}, #{fczgx}, #{fczdx}, #{zdshbz}, #{zdshgx}, #{zdshdx}, #{zdshcyqj}, " +
        "#{jgfctsbz}, #{sbDjid}, #{ckzgx}, #{ckzdx}, #{bjzgx}, #{bjzdx})")
    void insertRefRange(Map<String, Object> data);

    @Update("UPDATE sys_xmckz SET xmid=#{xmid},bbsgbz=#{bbsgbz},bbzl=#{bbzl},xbsgbz=#{xbsgbz}," +
        "brxb=#{brxb},nlsgbz=#{nlsgbz},nllx=#{nllx},nlsx=#{nlsx},nlxx=#{nlxx}," +
        "xmdm=#{xmdm},ckz=#{ckz},jszgx=#{jszgx},jszdx=#{jszdx},fczgx=#{fczgx},fczdx=#{fczdx}," +
        "zdshbz=#{zdshbz},zdshgx=#{zdshgx},zdshdx=#{zdshdx},zdshcyqj=#{zdshcyqj}," +
        "jgfctsbz=#{jgfctsbz},sb_djid=#{sbDjid},ckzgx=#{ckzgx},ckzdx=#{ckzdx},bjzgx=#{bjzgx},bjzdx=#{bjzdx} WHERE id=#{id}")
    void updateRefRange(Map<String, Object> data);

    @Delete("DELETE FROM sys_xmckz WHERE id = #{id}")
    void deleteRefRange(@Param("id") Integer id);
}
