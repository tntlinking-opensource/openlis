package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtTybbsz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtTybbszMapper extends BaseMapper<BgxtTybbsz> {
    List<Map<String, Object>> selectByMkid(@Param("mkid") Integer mkid);
    List<Map<String, Object>> selectAllRules();
    int insertRule(@Param("mkid") Integer mkid, @Param("xmid") Integer xmid, @Param("mksm") String mksm);
    int deleteByMkidAndXmid(@Param("mkid") Integer mkid, @Param("xmid") Integer xmid);
    int countByMkidAndXmid(@Param("mkid") Integer mkid, @Param("xmid") Integer xmid);
}
