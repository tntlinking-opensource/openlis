package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtXmmrz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtXmmrzMapper extends BaseMapper<BgxtXmmrz> {

    List<Map<String, Object>> findByXmidAndSbDjid(@Param("xmid") Integer xmid, @Param("sbDjid") Integer sbDjid);

    void insertDefault(@Param("xmid") Integer xmid, @Param("sbDjid") Integer sbDjid,
                       @Param("mrz") Object mrz, @Param("mr") Object mr);

    void updateDefault(@Param("mrz") Object mrz, @Param("mr") Object mr,
                       @Param("xmid") Integer xmid, @Param("sbDjid") Integer sbDjid);
}
