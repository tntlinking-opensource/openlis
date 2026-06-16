package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysXtsz;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysXtszMapper extends BaseMapper<SysXtsz> {
    String selectBarcodePrefix();
    String selectHospitalName();
}
