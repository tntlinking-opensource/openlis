package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysJyxmFull;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysJyxmFullMapper extends BaseMapper<SysJyxmFull> {
    List<Map<String, Object>> listTestItems(@Param("keyword") String keyword, @Param("sbDjid") Integer sbDjid);
    List<Map<String, Object>> searchTestItems(@Param("pym") String pym);
    List<Map<String, Object>> searchTestItemsByPymAndInstrument(@Param("pym") String pym, @Param("sbDjid") Integer sbDjid);
    List<Map<String, Object>> getItemTypes();
    int countComboUsage(@Param("xmid") Integer xmid);
}
