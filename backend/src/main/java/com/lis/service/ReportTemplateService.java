package com.lis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportTemplateService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Map<String, Object>> listTemplates(String keyword, String bgbh, String bgmc) {
        String sql = "SELECT template_id, template_name, template_type, template_code, description, mr, sycx, bgbh, bgmc, sb_djid, czyxm, gxrq FROM sys_report_template WHERE status = 1";
        if (bgbh != null && !bgbh.isEmpty()) {
            sql += " AND bgbh = '" + bgbh.replace("'", "''") + "'";
            if (bgmc != null && !bgmc.isEmpty()) {
                sql += " AND bgmc = '" + bgmc.replace("'", "''") + "'";
            }
        } else if (bgmc != null && !bgmc.isEmpty()) {
            sql += " AND bgmc = '" + bgmc.replace("'", "''") + "'";
        }
        if ((keyword != null && !keyword.trim().isEmpty()) && (bgbh == null || bgbh.isEmpty()) && (bgmc == null || bgmc.isEmpty())) {
            sql += " AND (template_name LIKE '%" + keyword.replace("'", "''") + "%' OR template_code LIKE '%" + keyword.replace("'", "''") + "%')";
        }
        sql += " ORDER BY template_id";
        return jdbcTemplate.queryForList(sql);
    }

    public Map<String, Object> getTemplateById(Integer templateId) {
        return jdbcTemplate.queryForMap(
            "SELECT * FROM sys_report_template WHERE template_id = ?", templateId);
    }

    public Map<String, Object> getTemplateByCode(String templateCode) {
        try {
            return jdbcTemplate.queryForMap(
                "SELECT * FROM sys_report_template WHERE template_code = ?", templateCode);
        } catch (Exception e) {
            log.warn("模板不存在: {}", templateCode);
            return null;
        }
    }

    public Integer saveTemplate(Map<String, Object> data) {
        Integer templateId = data.get("templateId") != null ?
            ((Number) data.get("templateId")).intValue() : null;

        String templateName = (String) data.get("templateName");
        String templateType = (String) data.getOrDefault("templateType", "custom");
        String templateCode = (String) data.getOrDefault("templateCode", "CUSTOM_" + System.currentTimeMillis());
        String description = (String) data.getOrDefault("description", "");
        String htmlContent = (String) data.getOrDefault("htmlContent", "");
        String bbzl = (String) data.getOrDefault("bbzl", "[]");
        Object sbDjidObj = data.get("sbDjid");
        final Integer sbDjid;
        if (sbDjidObj instanceof Number) {
            sbDjid = ((Number) sbDjidObj).intValue();
        } else if (sbDjidObj != null) {
            Integer parsed = null;
            try { parsed = Integer.parseInt(sbDjidObj.toString()); } catch (NumberFormatException ignored) {}
            sbDjid = parsed;
        } else {
            sbDjid = null;
        }
        String bgbh = (String) data.getOrDefault("bgbh", "");
        String bgmc = (String) data.getOrDefault("bgmc", "");

        if (templateId != null && templateName == null && data.containsKey("sbDjid")) {
            jdbcTemplate.update("UPDATE sys_report_template SET sb_djid = ?, gxrq = NOW() WHERE template_id = ?", sbDjid, templateId);
            return templateId;
        }

        if (templateId != null) {
            String sql = "UPDATE sys_report_template SET template_name = ?, template_type = ?, description = ?, html_content = ?, bbzl = ?, sb_djid = ?, bgbh = ?, bgmc = ?, gxrq = NOW() WHERE template_id = ?";
            jdbcTemplate.update(sql, templateName, templateType, description, htmlContent, bbzl, sbDjid, bgbh, bgmc, templateId);
            return templateId;
        } else {
            String sql = "INSERT INTO sys_report_template (template_name, template_type, template_code, description, html_content, bbzl, sb_djid, bgbh, bgmc, config, status, sycx) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, '{}', 1, 101)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, templateName);
                ps.setString(2, templateType);
                ps.setString(3, templateCode);
                ps.setString(4, description);
                ps.setString(5, htmlContent);
                ps.setString(6, bbzl);
                ps.setObject(7, sbDjid);
                ps.setString(8, bgbh);
                ps.setString(9, bgmc);
                return ps;
            }, keyHolder);
            return keyHolder.getKey().intValue();
        }
    }

    public void deleteTemplate(Integer templateId) {
        jdbcTemplate.update("UPDATE sys_report_template SET status = 0 WHERE template_id = ?", templateId);
    }

    public void setDefaultTemplate(Integer templateId) {
        jdbcTemplate.update("UPDATE sys_report_template SET mr = 0 WHERE mr = 1");
        jdbcTemplate.update("UPDATE sys_report_template SET mr = 1 WHERE template_id = ?", templateId);
    }

    public String getHtmlContent(Integer templateId) {
        try {
            Map<String, Object> template = getTemplateById(templateId);
            if (template != null && template.get("html_content") != null) {
                return (String) template.get("html_content");
            }
        } catch (Exception e) {
            log.error("获取HTML内容失败", e);
        }
        return null;
    }

    public void saveHtmlContent(Integer templateId, String htmlContent) {
        jdbcTemplate.update("UPDATE sys_report_template SET html_content = ?, gxrq = NOW() WHERE template_id = ?", htmlContent, templateId);
    }
}
