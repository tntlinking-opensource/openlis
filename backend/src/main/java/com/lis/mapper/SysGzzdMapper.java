package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysGzzd;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysGzzdMapper extends BaseMapper<SysGzzd> {
    List<Map<String, Object>> listWorkgroups(@Param("gzzdm") String gzzdm, @Param("keyword") String keyword, @Param("sybzVal") Integer sybzVal);
    List<Map<String, Object>> findByGzdm(@Param("gzdm") String gzdm);
    void insertWorkgroup(@Param("gzdm") String gzdm, @Param("gzmc") String gzmc, @Param("pym") String pym,
                         @Param("gzzlx") Integer gzzlx, @Param("xh") Integer xh, @Param("sybz") Integer sybz);
    void updateByGzdm(@Param("gzmc") String gzmc, @Param("pym") String pym, @Param("gzzlx") Integer gzzlx,
                      @Param("xh") Integer xh, @Param("sybz") Integer sybz, @Param("gzdm") String gzdm);
    int cleanupGarbled();
}
