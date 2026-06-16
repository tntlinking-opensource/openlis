package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysJyxm;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysJyxmMapper extends BaseMapper<SysJyxm> {
    List<Map<String, Object>> selectActiveItems();
}
