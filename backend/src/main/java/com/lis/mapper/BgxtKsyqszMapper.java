package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtKsyqsz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtKsyqszMapper extends BaseMapper<BgxtKsyqsz> {
    List<Map<String, Object>> selectByKsdm(@Param("ksdm") String ksdm);
    List<Map<String, Object>> selectAllWithAssignmentStatus(@Param("ksdm") String ksdm);
    int insertAssignment(@Param("ksdm") String ksdm, @Param("sbDjid") Integer sbDjid);
    int deleteAssignment(@Param("ksdm") String ksdm, @Param("sbDjid") Integer sbDjid);
}
