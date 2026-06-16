package com.lis.controller;

import com.lis.service.ReportRenderService;
import com.lis.service.ReportService;
import com.lis.service.ReportTemplateService;
import com.lis.service.TemplateRenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/report", "/api/report"})
@Slf4j
public class ReportController {

    @Autowired
    private ReportRenderService reportRenderService;

    @Autowired
    private ReportTemplateService reportTemplateService;

    @Autowired
    private TemplateRenderService templateRenderService;

    @Autowired
    private ReportService reportService;

    @GetMapping("/preview/{templateId}")
    public ResponseEntity<Map<String, Object>> previewReport(
            @PathVariable Integer templateId,
            @RequestParam(required = false) Map<String, Object> params) {
        try {
            Map<String, Object> data = reportRenderService.createSampleData();
            String renderedHtml = templateRenderService.renderReport(templateId, data);
            String pdfBase64 = reportRenderService.renderHtmlToPdfBase64(renderedHtml, data);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("pdf", pdfBase64);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("生成报表预览失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/render")
    public ResponseEntity<Map<String, Object>> renderReport(@RequestBody Map<String, Object> request) {
        try {
            Integer templateId = request.get("templateId") != null ?
                ((Number) request.get("templateId")).intValue() : null;
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) request.get("data");

            if (data == null) {
                data = reportRenderService.createSampleData();
            }

            String html = (String) request.get("html");
            String pdfBase64;
            if (html != null && !html.isEmpty()) {
                String trimmed = html.trim();
                if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                    String renderedHtml = templateRenderService.renderFromHtmlContentDirect(html, data);
                    pdfBase64 = reportRenderService.renderHtmlToPdfBase64(renderedHtml, data);
                } else {
                    pdfBase64 = reportRenderService.renderHtmlToPdfBase64(html, data);
                }
            } else {
                String renderedHtml = templateRenderService.renderReport(templateId, data);
                pdfBase64 = reportRenderService.renderHtmlToPdfBase64(renderedHtml, data);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("pdf", pdfBase64);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("渲染报表失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @GetMapping("/sample")
    public ResponseEntity<Map<String, Object>> getSampleData() {
        try {
            Map<String, Object> sampleData = reportRenderService.createSampleData();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", sampleData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取示例数据失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/mergePrint")
    public ResponseEntity<Map<String, Object>> mergePrint(@RequestBody Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> brxxIds = (List<Integer>) payload.get("brxxIds");
            return ResponseEntity.ok(reportService.mergePrint(brxxIds));
        } catch (Exception e) {
            log.error("合并打印失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/mergeToPdf")
    public ResponseEntity<Map<String, Object>> mergeToPdf(@RequestBody Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> brxxIds = (List<Integer>) payload.get("brxxIds");
            return ResponseEntity.ok(reportService.mergeToPdf(brxxIds));
        } catch (Exception e) {
            log.error("合并PDF失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
