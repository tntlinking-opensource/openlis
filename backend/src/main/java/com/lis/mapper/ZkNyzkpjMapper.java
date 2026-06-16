package com.lis.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface ZkNyzkpjMapper {

    @Select("<script>SELECT p.id, p.zkpid, p.pjmd, p.pjjg, p.pjjsyj, p.pjczy, DATE_FORMAT(p.pjrq,'%Y-%m-%d') AS pjrq, d.zwmc AS zkpmc FROM zk_nykpj p LEFT JOIN sys_zkpd d ON p.zkpid=d.zkpid WHERE 1=1 <if test='zkpid!=null'> AND p.zkpid=#{zkpid}</if> <if test='date!=null and date!=&quot;&quot;'> AND p.pjrq=#{date}</if> ORDER BY p.pjrq DESC</script>")
    List<Map<String, Object>> listEvaluations(@Param("zkpid") Integer zkpid, @Param("date") String date);

    @Insert("INSERT INTO zk_nykpj (zkpid, pjmd, pjjg, pjjsyj, pjczy, pjrq) VALUES (#{zkpid}, #{pjmd}, #{pjjg}, #{pjjsyj}, #{pjczy}, NOW())")
    int insertEvaluation(@Param("zkpid") Integer zkpid, @Param("pjmd") String pjmd,
                         @Param("pjjg") String pjjg, @Param("pjjsyj") String pjjsyj,
                         @Param("pjczy") String pjczy);

    @Delete("DELETE FROM zk_nykpj WHERE id = #{id}")
    int deleteEvaluation(@Param("id") Integer id);
}
