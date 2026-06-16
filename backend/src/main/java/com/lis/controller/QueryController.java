package com.lis.controller;

import com.lis.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/query")
@Slf4j
public class QueryController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/sample/list")
    public ResponseEntity<Map<String, Object>> querySampleList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String patientType,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String testItem,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String instrument,
            @RequestParam(required = false) String examiner,
            @RequestParam(required = false) String auditor,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return ResponseEntity.ok(reportService.querySampleList(
                startDate, endDate, patientType, department, testItem, status, instrument,
                examiner, auditor, page, pageSize));
    }

    @GetMapping("/sample/statistics")
    public ResponseEntity<Map<String, Object>> querySampleStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String patientType,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String testItem,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String instrument,
            @RequestParam(required = false) String examiner,
            @RequestParam(required = false) String auditor) {
        return ResponseEntity.ok(reportService.querySampleStatistics(
                startDate, endDate, patientType, department, testItem, status, instrument,
                examiner, auditor));
    }

    @GetMapping("/sample/export")
    public ResponseEntity<byte[]> exportSample(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String patientType,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String testItem,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String instrument,
            @RequestParam(required = false) String examiner,
            @RequestParam(required = false) String auditor) {
        Map<String, Object> queryResult = reportService.querySampleList(
                startDate, endDate, patientType, department, testItem, status, instrument,
                examiner, auditor, 1, 100000);
        StringBuilder sb = new StringBuilder();
        sb.append("样本号,条码号,患者姓名,性别,年龄,患者类别,科室,送检医生,检验日期,审核日期,状态\n");
        List<Map<String, Object>> records = (List<Map<String, Object>>) queryResult.get("records");
        if (records == null) records = (List<Map<String, Object>>) queryResult.get("data");
        if (records == null) records = List.of();
        for (Map<String, Object> r : records) {
            sb.append(csvVal(r.get("syh"))).append(',');
            sb.append(csvVal(r.get("brxx_tmh"))).append(',');
            sb.append(csvVal(r.get("brxm"))).append(',');
            sb.append(csvVal(r.get("brxb"))).append(',');
            sb.append(csvVal(r.get("brnl"))).append(',');
            sb.append(csvVal(r.get("brlb"))).append(',');
            sb.append(csvVal(r.get("ksdm"))).append(',');
            sb.append(csvVal(r.get("sjys"))).append(',');
            sb.append(csvVal(r.get("jyrq"))).append(',');
            sb.append(csvVal(r.get("shrq"))).append(',');
            int ybzt = r.get("ybzt") != null ? ((Number) r.get("ybzt")).intValue() : 0;
            String[] statusNames = {"登记", "未审核", "已审核", "已打印", "已检验"};
            sb.append(ybzt >= 0 && ybzt < statusNames.length ? statusNames[ybzt] : "未知");
            sb.append('\n');
        }
        byte[] bytes = ('\uFEFF' + sb.toString()).getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample_export.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }

    private String csvVal(Object val) {
        if (val == null) return "";
        String s = val.toString().replace("\"", "\"\"");
        return s.contains(",") || s.contains("\"") || s.contains("\n") ? "\"" + s + "\"" : s;
    }

    @GetMapping("/options")
    public ResponseEntity<Map<String, Object>> getQueryOptions() {
        return ResponseEntity.ok(reportService.getQueryOptions());
    }

    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupDatabase() {
        return ResponseEntity.ok(reportService.cleanupDatabase());
    }

    @GetMapping("/sample/results/{brxxId}")
    public ResponseEntity<List<Map<String, Object>>> querySampleResults(@PathVariable Integer brxxId) {
        return ResponseEntity.ok(reportService.querySampleResults(brxxId));
    }
}