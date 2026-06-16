package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysTybmzb;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysTybmzbMapper extends BaseMapper<SysTybmzb> {

    @Select("SELECT id, bmdm, bmmc, pym, bz, tybz FROM sys_tybmzb ORDER BY id")
    List<Map<String, Object>> listMain();

    @Delete("DELETE FROM sys_tybmzb")
    void deleteAll();

    @Insert("INSERT INTO sys_tybmzb (id, bmdm, bmmc, pym, bz, tybz) VALUES (#{id}, #{bmdm}, #{bmmc}, #{pym}, #{bz}, #{tybz})")
    void insertInit(@Param("id") int id, @Param("bmdm") int bmdm, @Param("bmmc") String bmmc,
                    @Param("pym") String pym, @Param("bz") String bz, @Param("tybz") int tybz);
}
