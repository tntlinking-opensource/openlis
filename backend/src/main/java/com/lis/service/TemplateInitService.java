package com.lis.service;

import com.lis.mapper.ReportTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TemplateInitService implements CommandLineRunner {

    @Autowired
    private ReportTemplateMapper templateMapper;

    @Override
    public void run(String... args) {
        // 暂时禁用，等待手动创建表
        // initDefaultTemplates();
    }

    private void initDefaultTemplates() {
        if (templateMapper.listAll().isEmpty()) {
            insertDefaultTemplates();
        }
    }

    private void insertDefaultTemplates() {
        Map<String, Object> singleColTemplate = new HashMap<>();
        singleColTemplate.put("templateName", "标准单列报告");
        singleColTemplate.put("templateType", "single_col");
        singleColTemplate.put("templateCode", "STD_SINGLE_COL");
        singleColTemplate.put("description", "标准单列报告模板，适用于大多数检验项目");
        singleColTemplate.put("config", "{\"hospitalName\": \"医院名称\", \"reportTitle\": \"检验报告单\", \"showLogo\": true, \"pageSize\": \"A4\"}");
        singleColTemplate.put("mr", 1);
        singleColTemplate.put("sycx", 101);
        singleColTemplate.put("czyxm", "系统");

        Map<String, Object> doubleColTemplate = new HashMap<>();
        doubleColTemplate.put("templateName", "标准双列报告");
        doubleColTemplate.put("templateType", "double_col");
        doubleColTemplate.put("templateCode", "STD_DOUBLE_COL");
        doubleColTemplate.put("description", "标准双列报告模板，适用于项目较多的检验组合");
        doubleColTemplate.put("config", "{\"hospitalName\": \"医院名称\", \"reportTitle\": \"检验报告单\", \"showLogo\": true, \"pageSize\": \"A4\"}");
        doubleColTemplate.put("mr", 0);
        doubleColTemplate.put("sycx", 101);
        doubleColTemplate.put("czyxm", "系统");

        Map<String, Object> chartTemplate = new HashMap<>();
        chartTemplate.put("templateName", "标准图表报告");
        chartTemplate.put("templateType", "chart");
        chartTemplate.put("templateCode", "STD_CHART");
        chartTemplate.put("description", "标准图表报告模板，适用于血常规等需要显示直方图的检验项目");
        chartTemplate.put("config", "{\"hospitalName\": \"医院名称\", \"reportTitle\": \"检验报告单\", \"showLogo\": true, \"pageSize\": \"A4\", \"showCharts\": true}");
        chartTemplate.put("mr", 0);
        chartTemplate.put("sycx", 101);
        chartTemplate.put("czyxm", "系统");

        templateMapper.insert(singleColTemplate);
        templateMapper.insert(doubleColTemplate);
        templateMapper.insert(chartTemplate);
    }
}