package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysCjyszSettings;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysCjyszSettingsMapper extends BaseMapper<SysCjyszSettings> {

    @Select("SELECT id, sb_djid, xmid, original_value AS originalValue, replace_value AS replaceValue FROM sys_cjysz_settings WHERE sb_djid = #{sbDjid} AND xmid = #{xmid}")
    List<Map<String, Object>> findBySbDjidAndXmid(@Param("sbDjid") Integer sbDjid, @Param("xmid") Integer xmid);

    @Insert("INSERT INTO sys_cjysz_settings (sb_djid, xmid, original_value, replace_value) VALUES (#{sbDjid}, #{xmid}, #{originalValue}, #{replaceValue})")
    void insert(Map<String, Object> data);

    @Update("UPDATE sys_cjysz_settings SET replace_value = #{replaceValue} WHERE id = #{id}")
    void update(Map<String, Object> data);

    @Delete("DELETE FROM sys_cjysz_settings WHERE id = #{id}")
    void deleteById(@Param("id") Integer id);
}