package com.lis.controller;

import com.lis.dto.ProtocolParseRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/protocol/astm")
@Slf4j
public class AstmProtocolController {

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status(@RequestParam(required = false) Integer sbDjid) {
        Map<String, Object> result = new HashMap<>();
        result.put("connected", false);
        result.put("lastCommunication", null);
        result.put("message", "ASTM通信服务就绪（需要硬件连接）");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> start(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "ASTM通信已启动（模拟模式）");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/stop")
    public ResponseEntity<Map<String, Object>> stop(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "ASTM通信已停止");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/log")
    public ResponseEntity<List<Map<String, Object>>> log(@RequestParam(required = false) Integer sbDjid) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @PostMapping("/parse")
    public ResponseEntity<Map<String, Object>> parse(@RequestBody ProtocolParseRequest params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String rawData = params.getRawData();
            if (rawData == null || rawData.isEmpty()) {
                result.put("success", false);
                result.put("message", "原始数据不能为空");
                return ResponseEntity.ok(result);
            }
            String[] lines = rawData.split("\\r?\\n");
            List<Map<String, Object>> records = new ArrayList<>();
            for (String line : lines) {
                if (line.startsWith("O|")) {
                    Map<String, Object> rec = new HashMap<>();
                    String[] fields = line.split("\\|");
                    if (fields.length > 2) rec.put("sampleId", fields[2].trim());
                    if (fields.length > 3) rec.put("testCode", fields[3].trim());
                    records.add(rec);
                } else if (line.startsWith("R|")) {
                    Map<String, Object> rec = new HashMap<>();
                    String[] fields = line.split("\\|");
                    if (fields.length > 2) rec.put("testCode", fields[2].trim());
                    if (fields.length > 3) rec.put("result", fields[3].trim());
                    if (fields.length > 4) rec.put("unit", fields[4].trim());
                    records.add(rec);
                }
            }
            result.put("success", true);
            result.put("records", records);
            result.put("totalLines", lines.length);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "解析失败：" + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}
