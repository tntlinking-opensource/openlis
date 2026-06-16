package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysCjyszZb;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysCjyszZbMapper extends BaseMapper<SysCjyszZb> {

    @Select("<script>SELECT COUNT(*) FROM sys_cjysz_zb z WHERE z.hb_sb_djid = #{sbDjid} AND z.zkbz = 0 <if test='extractDate != null'>AND DATE(z.cjrq) = #{extractDate}</if></script>")
    Integer countPending(@Param("sbDjid") Integer sbDjid, @Param("extractDate") String extractDate);
}
