package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.ReportTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReportTemplateMapper extends BaseMapper<ReportTemplate> {

    List<Map<String, Object>> listAll();

    List<Map<String, Object>> search(@Param("keyword") String keyword);

    Map<String, Object> getById(@Param("templateId") Integer templateId);

    void insert(Map<String, Object> data);

    void update(Map<String, Object> data);

    void deleteById(@Param("templateId") Integer templateId);

    void clearOtherMr(@Param("templateId") Integer templateId);

    void updateMr(@Param("templateId") Integer templateId, @Param("mr") Integer mr);

    Map<String, Object> getDefaultTemplate();

    Map<String, Object> getTemplateByBbzl(@Param("bbzl") String bbzl);

    Map<String, Object> getTemplateBySbDjid(@Param("sbDjid") Integer sbDjid);

    Map<String, Object> getTemplateBySbDjidAndBbzl(@Param("sbDjid") Integer sbDjid, @Param("bbzl") String bbzl);

    Map<String, Object> getTemplateByBgbhBgmc(@Param("bgbh") String bgbh, @Param("bgmc") String bgmc);

    Map<String, Object> getTemplateByCode(@Param("templateCode") String templateCode);
}