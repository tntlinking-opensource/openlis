package com.lis.service;

import com.lis.mapper.ReportVersionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportVersionService {

    @Autowired
    private ReportVersionMapper reportVersionMapper;

    public List<Map<String, Object>> listReportVersions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return reportVersionMapper.listAll();
        }
        return reportVersionMapper.search(keyword);
    }

    public Map<String, Object> getReportVersionById(Integer bbId) {
        return reportVersionMapper.getById(bbId);
    }

    public void saveReportVersion(Map<String, Object> data) {
        String bbsm = (String) data.get("bbsm");

        if (bbsm == null || bbsm.trim().isEmpty()) {
            throw new IllegalArgumentException("报告说明不能为空");
        }

        Integer bbId = data.get("bbId") != null ? (Integer) data.get("bbId") : null;
        Integer mr = data.get("mr") != null ? (Integer) data.get("mr") : 0;

        if (bbId != null) {
            reportVersionMapper.update(data);
        } else {
            reportVersionMapper.insert(data);
        }
    }

    public void setDefaultReport(Integer bbId) {
        if (bbId == null) {
            throw new IllegalArgumentException("报告ID不能为空");
        }
        reportVersionMapper.clearOtherMr(bbId);
        reportVersionMapper.updateMr(bbId, 1);
    }

    public void deleteReportVersion(Integer bbId) {
        if (bbId == null) {
            throw new IllegalArgumentException("报告ID不能为空");
        }
        reportVersionMapper.deleteById(bbId);
    }

    public String getMrtTemplate(Integer bbId) {
        if (bbId == null) {
            throw new IllegalArgumentException("报告ID不能为空");
        }
        return reportVersionMapper.getMrtById(bbId);
    }

    public void saveMrtTemplate(Integer bbId, String mrtJson) {
        if (bbId == null) {
            throw new IllegalArgumentException("报告ID不能为空");
        }
        if (mrtJson == null || mrtJson.trim().isEmpty()) {
            throw new IllegalArgumentException("模板内容不能为空");
        }
        reportVersionMapper.updateMrt(bbId, mrtJson);
    }

    public String renderReport(Integer brxxId) {
        return null;
    }
}