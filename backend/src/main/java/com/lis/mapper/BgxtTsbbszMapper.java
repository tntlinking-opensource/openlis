package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtTsbbsz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BgxtTsbbszMapper extends BaseMapper<BgxtTsbbsz> {

    @Select("SELECT * FROM bgxt_tsbbsz WHERE mkid = #{mkid}")
    List<BgxtTsbbsz> selectByMkid(@Param("mkid") Integer mkid);
}
