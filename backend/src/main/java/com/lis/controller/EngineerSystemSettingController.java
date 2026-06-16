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
@RequestMapping("/engineer/setting")
@Slf4j
public class EngineerSystemSettingController {

    @Autowired
    private SystemSettingService systemSettingService;

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        return ResponseEntity.ok(systemSettingService.getEngineerConfig());
    }

    @PostMapping("/config")
    @Transactional
    public ResponseEntity<Map<String, Object>> saveConfig(@RequestBody SaveConfigRequest req) {
        Map<String, Object> reqMap = new java.util.HashMap<>();
        reqMap.put("wYydm", req.getWYydm());
        reqMap.put("yymc", req.getYymc());
        reqMap.put("jykksdm", req.getJykksdm());
        reqMap.put("hisConnectbz", req.getHisConnectbz());
        reqMap.put("hisConnectLevel", req.getHisConnectLevel());
        reqMap.put("tjConnectbz", req.getTjConnectbz());
        reqMap.put("yszConnectbz", req.getYszConnectbz());
        reqMap.put("tjJghcbz", req.getTjJghcbz());
        reqMap.put("yszJghcbz", req.getYszJghcbz());
        reqMap.put("qtxtJghcbz", req.getQtxtJghcbz());
        reqMap.put("websc", req.getWebsc());
        reqMap.put("gdsj", req.getGdsj());
        reqMap.put("hisConnectYbzx", req.getHisConnectYbzx());
        return ResponseEntity.ok(systemSettingService.saveEngineerConfig(reqMap));
    }

    @Data
    public static class SaveConfigRequest {
        private String wYydm;
        private String yymc;
        private String jykksdm;
        private Boolean hisConnectbz;
        private Integer hisConnectLevel;
        private Boolean tjConnectbz;
        private Boolean yszConnectbz;
        private Boolean tjJghcbz;
        private Boolean yszJghcbz;
        private Boolean qtxtJghcbz;
        private Boolean websc;
        private Integer gdsj;
        private Boolean hisConnectYbzx;
    }
}
