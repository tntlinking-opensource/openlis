package com.lis.service;

import com.lis.mapper.BgxtCriticalValueMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CriticalValueService {

    @Autowired
    private BgxtCriticalValueMapper criticalValueMapper;

    public Map<String, Object> list(String beginDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", criticalValueMapper.selectList(beginDate, endDate));
        return result;
    }

    @Transactional
    public Map<String, Object> add(com.lis.dto.CriticalValueRequest data) {
        Map<String, Object> result = new HashMap<>();
        try {
            criticalValueMapper.insert(data.getReportId(), data.getCriticalValue(),
                    data.getAddOperCode(), data.getAddOperName(), data.getXmid());
            result.put("success", true);
            result.put("message", "新增成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "新增失败：" + e.getMessage());
        }
        return result;
    }

    @Transactional
    public Map<String, Object> softDelete(Integer id, String cancelOperCode) {
        Map<String, Object> result = new HashMap<>();
        try {
            criticalValueMapper.softDelete(id, cancelOperCode);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> statistics(String beginDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", criticalValueMapper.countByDateRange(beginDate, endDate));
        result.put("byDepartment", criticalValueMapper.selectStatsByDepartment(beginDate, endDate));
        result.put("byItem", criticalValueMapper.selectStatsByItem(beginDate, endDate));
        result.put("byOperator", criticalValueMapper.selectStatsByOperator(beginDate, endDate));
        result.put("byMonth", criticalValueMapper.selectStatsByMonth(beginDate, endDate));
        result.put("byUrgency", criticalValueMapper.selectStatsByUrgency(beginDate, endDate));
        return result;
    }

    public String exportCsv(String beginDate, String endDate) {
        List<Map<String, Object>> list = criticalValueMapper.selectList(beginDate, endDate);
        StringBuilder sb = new StringBuilder();
        sb.append("序号,患者姓名,性别,年龄,科室,检验项目,危急值,上报时间,上报人,紧急程度,处理状态\n");
        int idx = 1;
        for (Map<String, Object> row : list) {
            String processStatus = row.get("process_date") != null ? "已处理" : "未处理";
            sb.append(idx++).append(',')
              .append(val(row, "brxm")).append(',')
              .append(val(row, "brxb")).append(',')
              .append(val(row, "brnl")).append(',')
              .append(val(row, "ksmc")).append(',')
              .append(val(row, "xmzwmc")).append(',')
              .append('"').append(val(row, "critical_value")).append('"').append(',')
              .append(val(row, "add_date")).append(',')
              .append(val(row, "add_oper_name")).append(',')
              .append(val(row, "syqk")).append(',')
              .append(processStatus).append('\n');
        }
        return sb.toString();
    }

    @Transactional
    public Map<String, Object> processBatch(List<Integer> ids, String processOperName) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (ids == null || ids.isEmpty()) {
                result.put("success", false);
                result.put("message", "请选择要处理的记录");
                return result;
            }
            criticalValueMapper.processBatch(ids, processOperName);
            result.put("success", true);
            result.put("message", "批量处理成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "处理失败：" + e.getMessage());
        }
        return result;
    }

    private String val(Map<String, Object> row, String key) {
        Object v = row.get(key);
        return v != null ? v.toString().replace(",", " ") : "";
    }
}