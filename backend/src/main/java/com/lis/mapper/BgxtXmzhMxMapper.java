package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtXmzhMx;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtXmzhMxMapper extends BaseMapper<BgxtXmzhMx> {

    String findXmzwmcById(@Param("id") String id);

    Map<String, Object> findXmInfoById(@Param("id") String id);

    List<Map<String, Object>> findAvailableProjects(@Param("zkpid") Integer zkpid);

    List<Map<String, Object>> findAllProjects();

    List<Map<String, Object>> getComboItems(@Param("zhid") Integer zhid);

    List<Map<String, Object>> existsByZhidAndXmid(@Param("zhid") Integer zhid, @Param("xmid") Object xmid);

    Integer getMaxIdByZhid(@Param("zhid") Integer zhid);

    Integer getGlobalMaxId();

    void insertComboItem(@Param("id") int id, @Param("zhid") Integer zhid, @Param("xmid") Object xmid,
                         @Param("mrjg") Object mrjg, @Param("sbDjid") Object sbDjid, @Param("bz") Object bz);

    void removeComboItem(@Param("zhid") Integer zhid, @Param("xmid") Integer xmid);

    void reorderComboItem(@Param("order") int order, @Param("zhid") Integer zhid, @Param("xmid") Integer xmid);

    void copyComboItems(@Param("targetZhid") Integer targetZhid, @Param("sourceZhid") Integer sourceZhid);

    void deleteByZhid(@Param("zhid") Integer zhid);

    List<Map<String, Object>> selectByXmid(@Param("xmid") Integer xmid);

    void insertXmidZhid(@Param("xmid") Integer xmid, @Param("zhid") Integer zhid);

    void deleteByXmidAndZhid(@Param("xmid") Integer xmid, @Param("zhid") Integer zhid);
}
