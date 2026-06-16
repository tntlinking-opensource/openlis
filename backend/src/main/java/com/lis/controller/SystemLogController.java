package com.lis.controller;

import com.lis.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/log")
@Slf4j
public class SystemLogController {

    @Autowired
    private SystemService systemService;

    @GetMapping("/systems")
    public ResponseEntity<List<Map<String, Object>>> systems() {
        return ResponseEntity.ok(systemService.listSystems());
    }

    @GetMapping("/operation-types/{systemId}")
    public ResponseEntity<List<Map<String, Object>>> operationTypes(@PathVariable Integer systemId) {
        return ResponseEntity.ok(systemService.listOperationTypes(systemId));
    }

    @GetMapping("/operation-types")
    public ResponseEntity<List<Map<String, Object>>> allOperationTypes() {
        return ResponseEntity.ok(systemService.listAllOperationTypes());
    }

    @GetMapping("/query")
    public ResponseEntity<Map<String, Object>> query(
            @RequestParam(required = false) Integer zxtid,
            @RequestParam(required = false) Integer ztid,
            @RequestParam(required = false) String czydm,
            @RequestParam(required = false) String beginDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") Integer includeBak,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "50") Integer pageSize) {
        return ResponseEntity.ok(systemService.queryLogs(zxtid, ztid, czydm, beginDate, endDate, includeBak, pageNum, pageSize));
    }

    @GetMapping("/operators")
    public ResponseEntity<List<Map<String, Object>>> operators(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(systemService.listOperators(name));
    }

    @PostMapping("/write")
    public ResponseEntity<Map<String, Object>> writeLog(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        String czydm = (String) payload.getOrDefault("czydm", "");
        String sm = (String) payload.getOrDefault("sm", "");
        Integer ztid = payload.get("ztid") != null ? Integer.parseInt(payload.get("ztid").toString()) : null;
        Integer zxtid = payload.get("zxtid") != null ? Integer.parseInt(payload.get("zxtid").toString()) : null;
        String czip = request.getRemoteAddr();
        String czmk = (String) payload.getOrDefault("czmk", "");
        systemService.saveLog(czydm, sm, ztid, zxtid, czip, czmk);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }
}