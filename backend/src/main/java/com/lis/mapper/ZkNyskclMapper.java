package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.ZkNyskcl;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ZkNyskclMapper extends BaseMapper<ZkNyskcl> {
    List<Map<String, Object>> findProcessingRecords(@Param("zkxmid") Integer zkxmid,
                                                     @Param("monthStart") String monthStart);
    int insertProcessingRecord(@Param("zkxmid") Integer zkxmid, @Param("zkcl") String zkcl,
                               @Param("czydmClr") String czydmClr, @Param("ksrq") String ksrq,
                               @Param("jsrq") String jsrq);
}
