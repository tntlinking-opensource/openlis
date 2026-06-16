package com.lis.controller;

import com.lis.mapper.BgxtBrxxMapper;
import com.lis.mapper.BgxtJyjgMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/patient", "/api/patient"})
@Slf4j
public class PatientController {

    @Autowired
    private BgxtBrxxMapper brxxMapper;

    @Autowired
    private BgxtJyjgMapper jyjgMapper;

    @GetMapping("/360/{brxxId}")
    public ResponseEntity<Map<String, Object>> getPatient360View(@PathVariable("brxxId") Integer brxxId) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (brxxId == null) {
                result.put("success", false);
                result.put("message", "缺少样本ID");
                return ResponseEntity.badRequest().body(result);
            }

            Map<String, Object> patientInfo = brxxMapper.selectPatient360View(brxxId);
            if (patientInfo == null) {
                result.put("success", false);
                result.put("message", "未找到该患者信息");
                return ResponseEntity.ok(result);
            }

            List<Map<String, Object>> results = jyjgMapper.selectResultsByBrxxId(brxxId);

            result.put("success", true);
            result.put("patientInfo", patientInfo);
            result.put("results", results);
            result.put("resultCount", results != null ? results.size() : 0);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取患者360视图失败", e);
            result.put("success", false);
            result.put("message", "获取患者360视图失败：" + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
}
