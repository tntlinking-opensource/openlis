package com.lis.controller;

import com.lis.service.ReportTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.lis.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report-template")
@Slf4j
public class ReportTemplateController {

    @Autowired
    private ReportTemplateService reportTemplateService;

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String bgbh,
            @RequestParam(required = false) String bgmc) {
        return ResponseEntity.ok(reportTemplateService.listTemplates(keyword, bgbh, bgmc));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Integer id) {
        try {
            Map<String, Object> result = reportTemplateService.getTemplateById(id);
            if (result == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取模板失败", e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/html")
    public ResponseEntity<Map<String, Object>> getHtml(@PathVariable Integer id) {
        try {
            String html = reportTemplateService.getHtmlContent(id);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("html", html != null ? html : "");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取HTML失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @OperationLog(value = "保存报告模板", module = "报告模板")
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> save(@RequestBody Map<String, Object> data) {
        try {
            Integer templateId = reportTemplateService.saveTemplate(data);
            return ResponseEntity.ok(Map.of("success", true, "message", "保存成功", "templateId", templateId));
        } catch (Exception e) {
            log.error("保存模板失败", e);
            return ResponseEntity.ok(Map.of("success", false, "message", "保存失败: " + e.getMessage()));
        }
    }

    @OperationLog(value = "保存模板内容", module = "报告模板")
    @PostMapping("/{id}/html")
    public ResponseEntity<Map<String, Object>> saveHtml(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            String html = body.get("html");
            reportTemplateService.saveHtmlContent(id, html);
            return ResponseEntity.ok(Map.of("success", true, "message", "保存成功"));
        } catch (Exception e) {
            log.error("保存HTML失败", e);
            return ResponseEntity.ok(Map.of("success", false, "message", "保存失败: " + e.getMessage()));
        }
    }

    @OperationLog(value = "设置默认模板", module = "报告模板")
    @PostMapping("/set-default/{id}")
    public ResponseEntity<Map<String, Object>> setDefault(@PathVariable Integer id) {
        try {
            reportTemplateService.setDefaultTemplate(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "设置成功"));
        } catch (Exception e) {
            log.error("设置默认模板失败", e);
            return ResponseEntity.ok(Map.of("success", false, "message", "设置失败: " + e.getMessage()));
        }
    }

    @OperationLog(value = "删除报告模板", module = "报告模板")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id) {
        try {
            reportTemplateService.deleteTemplate(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "删除成功"));
        } catch (Exception e) {
            log.error("删除模板失败", e);
            return ResponseEntity.ok(Map.of("success", false, "message", "删除失败: " + e.getMessage()));
        }
    }
}
