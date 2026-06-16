package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtBgmb;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtBgmbMapper extends BaseMapper<BgxtBgmb> {

    List<Map<String, Object>> selectAll();

    Map<String, Object> selectById(@Param("id") Integer id);

    Map<String, Object> selectByInstrumentAndCombo(@Param("sbDjid") Integer sbDjid, @Param("zhid") Integer zhid);

    Map<String, Object> selectByInstrument(@Param("sbDjid") Integer sbDjid);

    Map<String, Object> selectByDepartment(@Param("ksdm") String ksdm);

    Map<String, Object> selectDefault();

    int insert(BgxtBgmb record);

    int updateById(BgxtBgmb record);

    int deleteById(@Param("id") Integer id);
}
