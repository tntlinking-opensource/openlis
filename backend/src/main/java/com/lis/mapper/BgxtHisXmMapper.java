package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtHisXm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtHisXmMapper extends BaseMapper<BgxtHisXm> {

    List<Map<String, Object>> selectByBrxxId(@Param("brxxId") Integer brxxId);

    int insertFromCombo(@Param("brxxId") Integer brxxId);

    int insertFromComboFallback(@Param("brxxId") Integer brxxId);

    int confirmBilling(@Param("brxxId") Integer brxxId, @Param("czydm") String czydm);

    int cancelBilling(@Param("brxxId") Integer brxxId, @Param("czydm") String czydm);

    int invalidateBilling(@Param("brxxId") Integer brxxId, @Param("czydm") String czydm);
}
