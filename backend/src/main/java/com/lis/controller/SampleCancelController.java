package com.lis.controller;

import com.lis.annotation.OperationLog;
import com.lis.service.SampleService;
import com.lis.service.SampleStateMachineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/sample", "/api/sample"})
@Slf4j
public class SampleCancelController {

    @Autowired
    private SampleService sampleService;

    @Autowired
    private SampleStateMachineService stateMachine;

    @PostMapping("/{id}/cancel-audit")
    @OperationLog(value = "取消审核", module = "样本审核")
    public ResponseEntity<Map<String, Object>> cancelAudit(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        String czydm = (String) params.get("czydm");
        String reason = (String) params.get("reason");
        Map<String, Object> result = sampleService.cancelSample(id, czydm, reason);
        if (!Boolean.TRUE.equals(result.get("success"))) return ResponseEntity.ok(result);
        Map<String, Object> r = stateMachine.transition(id, -2, czydm);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/{id}/cancel-test")
    @OperationLog(value = "取消检验", module = "样本管理")
    public ResponseEntity<Map<String, Object>> cancelTest(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        String czydm = (String) params.get("czydm");
        String reason = (String) params.get("reason");
        Map<String, Object> result = sampleService.cancelSample(id, czydm, reason);
        if (!Boolean.TRUE.equals(result.get("success"))) return ResponseEntity.ok(result);
        Map<String, Object> r = stateMachine.transition(id, -4, czydm);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/{id}/cancel-first-check")
    @OperationLog(value = "取消初审核", module = "样本审核")
    public ResponseEntity<Map<String, Object>> cancelFirstCheck(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        String czydm = (String) params.get("czydm");
        Map<String, Object> r = stateMachine.transition(id, -5, czydm);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/{id}/cancel-intermediate-check")
    @OperationLog(value = "取消中审核", module = "样本审核")
    public ResponseEntity<Map<String, Object>> cancelIntermediateCheck(@PathVariable Integer id, @RequestBody Map<String, Object> params) {
        String czydm = (String) params.get("czydm");
        Map<String, Object> r = stateMachine.transition(id, -6, czydm);
        return ResponseEntity.ok(r);
    }
}
