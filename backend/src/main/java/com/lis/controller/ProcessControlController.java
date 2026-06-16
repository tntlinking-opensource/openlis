package com.lis.controller;

import com.lis.service.SystemSettingService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/system/process-control")
@Slf4j
public class ProcessControlController {

    @Autowired
    private SystemSettingService systemSettingService;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list() {
        return ResponseEntity.ok(systemSettingService.getProcessControlList());
    }

    @PostMapping("/save")
    @Transactional
    public ResponseEntity<Map<String, Object>> save(@RequestBody SaveRequest req) {
        Map<String, Object> reqMap = new java.util.HashMap<>();
        reqMap.put("sqkg", req.getSqkg());
        reqMap.put("mzsjkg", req.getMzsjkg());
        reqMap.put("jmjkk", req.getJmjkk());
        return ResponseEntity.ok(systemSettingService.saveProcessControl(reqMap));
    }

    @Data
    public static class SaveRequest {
        private Boolean sqkg;
        private Boolean mzsjkg;
        private Boolean jmjkk;
    }
}
