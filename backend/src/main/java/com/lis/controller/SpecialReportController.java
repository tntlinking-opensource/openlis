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
@RequestMapping("/system/special-report")
@Slf4j
public class SpecialReportController {

    @Autowired
    private SystemSettingService systemSettingService;

    @GetMapping("/module/list")
    public ResponseEntity<Map<String, Object>> moduleList() {
        return ResponseEntity.ok(systemSettingService.getSpecialReportModuleList());
    }

    @GetMapping("/linked/list")
    public ResponseEntity<Map<String, Object>> linkedList(@RequestParam Integer mkid) {
        return ResponseEntity.ok(systemSettingService.getSpecialReportLinkedList(mkid));
    }

    @GetMapping("/item/search")
    public ResponseEntity<Map<String, Object>> searchItem(@RequestParam String mc) {
        return ResponseEntity.ok(systemSettingService.searchSpecialReportItem(mc));
    }

    @PostMapping("/link")
    @Transactional
    public ResponseEntity<Map<String, Object>> link(@RequestBody LinkRequest req) {
        Map<String, Object> reqMap = new java.util.HashMap<>();
        reqMap.put("mkid", req.getMkid());
        reqMap.put("mksm", req.getMksm());
        reqMap.put("xmid", req.getXmid());
        return ResponseEntity.ok(systemSettingService.linkSpecialReport(reqMap));
    }

    @DeleteMapping("/link")
    @Transactional
    public ResponseEntity<Map<String, Object>> unlink(@RequestParam Integer mkid, @RequestParam Integer xmid) {
        return ResponseEntity.ok(systemSettingService.unlinkSpecialReport(mkid, xmid));
    }

    @Data
    public static class LinkRequest {
        private Integer mkid;
        private String mksm;
        private Integer xmid;
    }
}
