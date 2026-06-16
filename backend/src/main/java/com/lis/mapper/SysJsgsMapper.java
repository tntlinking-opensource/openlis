package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysJsgs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysJsgsMapper extends BaseMapper<SysJsgs> {
    List<Map<String, Object>> findBySbDjidAndXmid(@Param("sbDjid") Integer sbDjid, @Param("xmid") Integer xmid);
    List<Map<String, Object>> findBySbDjid(@Param("sbDjid") Integer sbDjid);
    void saveOrUpdate(@Param("sbDjid") Integer sbDjid, @Param("xmid") Integer xmid,
                       @Param("bds") String bds, @Param("bdssm") String bdssm);
    void deleteBySbDjidAndXmid(@Param("sbDjid") Integer sbDjid, @Param("xmid") Integer xmid);
}