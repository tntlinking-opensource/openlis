package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysZkpd;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysZkpdMapper extends BaseMapper<SysZkpd> {
    List<Map<String, Object>> searchProducts(@Param("sbDjid") Integer sbDjid, @Param("keyword") String keyword);
    Map<String, Object> findProductById(@Param("zkpid") Integer zkpid);
    int insertProduct(@Param("sbDjid") Integer sbDjid, @Param("zwmc") String zwmc, @Param("ywmc") String ywmc,
                      @Param("zkpsm") String zkpsm, @Param("ph") String ph, @Param("sccj") String sccj,
                      @Param("sybz") Integer sybz);
    int updateProduct(@Param("sbDjid") Integer sbDjid, @Param("zwmc") String zwmc, @Param("ywmc") String ywmc,
                      @Param("zkpsm") String zkpsm, @Param("ph") String ph, @Param("sccj") String sccj,
                      @Param("sybz") Integer sybz, @Param("zkpid") Integer zkpid);
    int deleteProduct(@Param("zkpid") Integer zkpid);
}
