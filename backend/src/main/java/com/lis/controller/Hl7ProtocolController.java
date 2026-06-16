package com.lis.controller;

import com.lis.dto.ProtocolParseRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/protocol/hl7")
@Slf4j
public class Hl7ProtocolController {

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> send(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "HL7消息已发送（模拟模式）");
        result.put("messageId", UUID.randomUUID().toString());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/parse")
    public ResponseEntity<Map<String, Object>> parse(@RequestBody ProtocolParseRequest params) {
        Map<String, Object> result = new HashMap<>();
        try {
            String message = params.getMessage();
            if (message == null || message.isEmpty()) {
                result.put("success", false);
                result.put("message", "HL7消息不能为空");
                return ResponseEntity.ok(result);
            }
            Map<String, Object> parsed = new HashMap<>();
            String[] segments = message.split("\\r");
            List<Map<String, Object>> patientInfo = new ArrayList<>();
            List<Map<String, Object>> orders = new ArrayList<>();
            List<Map<String, Object>> results = new ArrayList<>();
            for (String seg : segments) {
                String[] fields = seg.split("\\|");
                String segType = fields[0];
                switch (segType) {
                    case "PID":
                        Map<String, Object> pid = new HashMap<>();
                        if (fields.length > 3) pid.put("patientId", fields[3].split("\\^")[0]);
                        if (fields.length > 5) pid.put("patientName", fields[5].replace("^", " ").trim());
                        patientInfo.add(pid);
                        break;
                    case "OBR":
                        Map<String, Object> obr = new HashMap<>();
                        if (fields.length > 4) obr.put("orderCode", fields[4].split("\\^")[0]);
                        if (fields.length > 7) obr.put("requestDate", fields[7]);
                        orders.add(obr);
                        break;
                    case "OBX":
                        Map<String, Object> obx = new HashMap<>();
                        if (fields.length > 3) obx.put("valueType", fields[2]);
                        if (fields.length > 4) obx.put("testCode", fields[3].split("\\^")[0]);
                        if (fields.length > 5) obx.put("resultValue", fields[5]);
                        if (fields.length > 6) obx.put("unit", fields[6].split("\\^")[0]);
                        if (fields.length > 7) obx.put("refRange", fields[7]);
                        results.add(obx);
                        break;
                }
            }
            parsed.put("patientInfo", patientInfo);
            parsed.put("orders", orders);
            parsed.put("results", results);
            result.put("success", true);
            result.put("parsed", parsed);
            result.put("segmentCount", segments.length);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "解析失败：" + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Map<String, Object>>> messages(
            @RequestParam(required = false) String beginDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(new ArrayList<>());
    }
}
