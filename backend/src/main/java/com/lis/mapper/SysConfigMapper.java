package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysConfig;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    @Select("SELECT * FROM sys_config LIMIT 1")
    SysConfig selectConfig();

    @Update("UPDATE sys_config SET w_yydm=#{wYydm}, yymc=#{yymc}, jykksdm=#{jykksdm}, " +
            "his_connectbz=#{hisConnectbz}, his_connectlevel=#{hisConnectlevel}, " +
            "tj_connectbz=#{tjConnectbz}, tj_jghcbz=#{tjJghcbz}, ysz_jghcbz=#{yszJghcbz}, " +
            "ysz_connectbz=#{yszConnectbz}, qtxt_jghcbz=#{qtxtJghcbz}, websc=#{websc}, " +
            "gdsj=#{gdsj}, his_connect_ybzx=#{hisConnectYbzx}")
    void updateConfig(SysConfig config);

    @Insert("INSERT INTO sys_config (w_yydm, yymc, jykksdm, his_connectbz, his_connectlevel, " +
            "tj_connectbz, tj_jghcbz, ysz_jghcbz, ysz_connectbz, qtxt_jghcbz, websc, gdsj, his_connect_ybzx) " +
            "VALUES (#{wYydm}, #{yymc}, #{jykksdm}, #{hisConnectbz}, #{hisConnectlevel}, " +
            "#{tjConnectbz}, #{tjJghcbz}, #{yszJghcbz}, #{yszConnectbz}, #{qtxtJghcbz}, #{websc}, " +
            "#{gdsj}, #{hisConnectYbzx})")
    void insertConfig(SysConfig config);
}
