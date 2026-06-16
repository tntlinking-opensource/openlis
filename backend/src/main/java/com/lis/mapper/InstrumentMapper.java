package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.Instrument;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface InstrumentMapper extends BaseMapper<Instrument> {
    List<Map<String, Object>> selectActiveInstruments();
}
