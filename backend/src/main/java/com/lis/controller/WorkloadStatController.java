package com.lis.controller;

import com.lis.dto.QueryRequest;
import com.lis.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/statistics")
@Slf4j
public class WorkloadStatController {

    @Autowired
    private StatisticsService statisticsService;

    @PostMapping("/workload-by-item")
    public ResponseEntity<Map<String, Object>> workloadByItem(@RequestBody QueryRequest params) {
        return ResponseEntity.ok(statisticsService.workloadByItem(params));
    }

    @GetMapping("/workload-detail")
    public ResponseEntity<List<Map<String, Object>>> workloadDetail(
            @RequestParam String beginDate, @RequestParam String endDate,
            @RequestParam Integer xmid) {
        return ResponseEntity.ok(statisticsService.workloadDetail(beginDate, endDate, xmid));
    }

    @GetMapping("/workload-by-item-v2")
    public ResponseEntity<List<Map<String, Object>>> workloadByItemV2(
            @RequestParam String beginDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticsService.workloadByItemV2(beginDate, endDate));
    }

    @GetMapping("/workload-item-detail-v2")
    public ResponseEntity<List<Map<String, Object>>> workloadItemDetailV2(
            @RequestParam String beginDate, @RequestParam String endDate,
            @RequestParam Integer zhid) {
        return ResponseEntity.ok(statisticsService.workloadItemDetailV2(beginDate, endDate, zhid));
    }

    @GetMapping("/workload-by-dept")
    public ResponseEntity<List<Map<String, Object>>> workloadByDept(
            @RequestParam String beginDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticsService.workloadByDept(beginDate, endDate));
    }

    @GetMapping("/workload-dept-detail")
    public ResponseEntity<List<Map<String, Object>>> workloadDeptDetail(
            @RequestParam String beginDate, @RequestParam String endDate,
            @RequestParam String ksmc) {
        return ResponseEntity.ok(statisticsService.workloadDeptDetail(beginDate, endDate, ksmc));
    }

    @GetMapping("/workload-by-doctor")
    public ResponseEntity<List<Map<String, Object>>> workloadByDoctor(
            @RequestParam String beginDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticsService.workloadByDoctor(beginDate, endDate));
    }

    @GetMapping("/workload-doctor-detail")
    public ResponseEntity<List<Map<String, Object>>> workloadDoctorDetail(
            @RequestParam String beginDate, @RequestParam String endDate,
            @RequestParam String sjys) {
        return ResponseEntity.ok(statisticsService.workloadDoctorDetail(beginDate, endDate, sjys));
    }

    @GetMapping("/workload-by-examiner")
    public ResponseEntity<List<Map<String, Object>>> workloadByExaminer(
            @RequestParam String beginDate, @RequestParam String endDate) {
        return ResponseEntity.ok(statisticsService.workloadByExaminer(beginDate, endDate));
    }

    @GetMapping("/workload-examiner-detail")
    public ResponseEntity<List<Map<String, Object>>> workloadExaminerDetail(
            @RequestParam String beginDate, @RequestParam String endDate,
            @RequestParam String jyys) {
        return ResponseEntity.ok(statisticsService.workloadExaminerDetail(beginDate, endDate, jyys));
    }

    private String[] workloadCols = {"门诊人数","门诊费用","住院人数","住院费用","体检人数","体检费用","其他人数","其他费用","总人数","总费用"};
    private String[] workloadKeys = {"mzrs","mzfy","zyrs","zyfy","tjrs","tjfy","qtrs","qtfy","zrs","zfy"};

    @GetMapping("/export-workload")
    public ResponseEntity<byte[]> exportWorkload(
            @RequestParam String tab,
            @RequestParam String beginDate,
            @RequestParam String endDate) throws Exception {
        List<Map<String, Object>> data;
        String filename;
        switch (tab) {
            case "dept":
                data = statisticsService.workloadByDept(beginDate, endDate);
                filename = "工作量统计_按开单科室";
                break;
            case "doctor":
                data = statisticsService.workloadByDoctor(beginDate, endDate);
                filename = "工作量统计_按开单医生";
                break;
            case "examiner":
                data = statisticsService.workloadByExaminer(beginDate, endDate);
                filename = "工作量统计_按检验医生";
                break;
            default:
                data = statisticsService.workloadByItemV2(beginDate, endDate);
                filename = "工作量统计_按检验项目";
                break;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter w = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
        w.write('\uFEFF');
        switch (tab) {
            case "item":
                w.write("项目代码,项目名称"); break;
            case "dept":
                w.write("科室"); break;
            case "doctor":
                w.write("医生"); break;
            case "examiner":
                w.write("检验医生"); break;
        }
        for (String c : workloadCols) w.write("," + c);
        w.write("\n");
        for (Map<String, Object> row : data) {
            switch (tab) {
                case "item":
                    w.write(csvCell(row.get("zhid")) + "," + csvCell(row.get("zhmc"))); break;
                case "dept":
                    w.write(csvCell(row.get("ksmc"))); break;
                case "doctor":
                    w.write(csvCell(row.get("sjys"))); break;
                case "examiner":
                    w.write(csvCell(row.get("jyys"))); break;
            }
            for (String k : workloadKeys) w.write("," + csvCell(row.get(k)));
            w.write("\n");
        }
        w.flush();
        String fn = filename + ".csv";
        String encodedFn = java.net.URLEncoder.encode(fn, StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"export.csv\"; filename*=UTF-8''" + encodedFn)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.parseMediaType("text/csv;charset=utf-8"))
                .body(baos.toByteArray());
    }

    private String csvCell(Object v) {
        if (v == null) return "";
        String s = String.valueOf(v);
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}