package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.ZkNyzkxm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ZkNyzkxmMapper extends BaseMapper<ZkNyzkxm> {
    List<Map<String, Object>> findProjects(@Param("zkpid") Integer zkpid);
    Map<String, Object> findXmidByZkxmid(@Param("zkxmid") Integer zkxmid);
    Map<String, Object> findRuleByZkxmid(@Param("zkxmid") Integer zkxmid);
    int insertProject(@Param("zkpid") Integer zkpid, @Param("xmid") Integer xmid, @Param("bz") String bz,
                      @Param("bzc") String bzc, @Param("zkdz") String zkdz, @Param("zkgz") String zkgz,
                      @Param("dxLx") Integer dxLx, @Param("fhbz") Integer fhbz);
    int updateProject(@Param("bz") String bz, @Param("bzc") String bzc, @Param("zkdz") String zkdz,
                      @Param("zkgz") String zkgz, @Param("dxLx") Integer dxLx, @Param("fhbz") Integer fhbz,
                      @Param("bc") String bc, @Param("zkxmid") Integer zkxmid);
    int deleteProject(@Param("zkxmid") Integer zkxmid);
    List<Map<String, Object>> findByZkpid(@Param("zkpid") Integer zkpid);
}
