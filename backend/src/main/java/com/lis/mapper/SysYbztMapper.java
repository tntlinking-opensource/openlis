package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysYbzt;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysYbztMapper extends BaseMapper<SysYbzt> {

    @Insert("INSERT INTO sys_ybzt (brxx_id, old_ybzt, new_ybzt, czydm, czrq) VALUES (#{brxxId}, #{oldStatus}, #{newStatus}, #{czydm}, NOW())")
    void insertLog(@Param("brxxId") Integer brxxId, @Param("oldStatus") Integer oldStatus, @Param("newStatus") Integer newStatus, @Param("czydm") String czydm);
}
