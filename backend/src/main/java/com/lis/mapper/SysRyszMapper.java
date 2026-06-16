package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysRysz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysRyszMapper extends BaseMapper<SysRysz> {
    List<Map<String, Object>> queryLogs(@Param("zxtid") Integer zxtid, @Param("ztid") Integer ztid,
                                        @Param("czydm") String czydm, @Param("beginDate") String beginDate,
                                        @Param("endDate") String endDate,
                                        @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    Long countLogs(@Param("zxtid") Integer zxtid, @Param("ztid") Integer ztid,
                   @Param("czydm") String czydm, @Param("beginDate") String beginDate,
                   @Param("endDate") String endDate);
    List<Map<String, Object>> searchOperators(@Param("name") String name);
    List<Map<String, Object>> listOperators();
}
