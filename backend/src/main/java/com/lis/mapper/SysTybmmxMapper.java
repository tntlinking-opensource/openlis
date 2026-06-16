package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysTybmmx;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysTybmmxMapper extends BaseMapper<SysTybmmx> {

    @Delete("DELETE FROM sys_tybmmx")
    void deleteAll();

    @Insert("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (#{bmdm}, #{bm}, #{bmsm}, #{szdm}, #{pym}, #{mrzbz}, #{tybz})")
    void insertInit(@Param("bmdm") int bmdm, @Param("bm") int bm, @Param("bmsm") String bmsm,
                    @Param("szdm") String szdm, @Param("pym") String pym, @Param("mrzbz") int mrzbz, @Param("tybz") int tybz);
}
