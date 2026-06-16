package com.lis.controller;

import lombok.extern.slf4j.Slf4j;
import com.lis.service.BatchProcessService;
import com.lis.service.SampleStateMachineService;
import com.lis.service.AuditVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/batch-process")
@Slf4j
public class BatchProcessController {

    @Autowired
    private BatchProcessService batchProcessService;
    @Autowired
    private SampleStateMachineService stateMachine;
    @Autowired
    private AuditVerificationService auditVerify;

    @PostMapping("/audit")
    public ResponseEntity<Map<String, Object>> batchAudit(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String czydm = (String) params.get("czydm");
        List<Integer> brxxIds = (List<Integer>) params.get("brxxIds");
        if (brxxIds == null || brxxIds.isEmpty()) {
            Object minSyh = params.get("minSyh");
            Object maxSyh = params.get("maxSyh");
            if (minSyh != null && maxSyh != null) {
                brxxIds = batchProcessService.findBrxxIdsBySyhRange(
                    Integer.parseInt(minSyh.toString()), Integer.parseInt(maxSyh.toString()));
            }
        }
        if (brxxIds == null || brxxIds.isEmpty()) {
            result.put("success", false);
            result.put("message", "没有可审核的样本");
            return ResponseEntity.ok(result);
        }
        int success = 0, fail = 0;
        List<String> errors = new ArrayList<>();
        for (Integer brxxId : brxxIds) {
            try {
                List<Map<String, Object>> warnings = auditVerify.verify(brxxId, czydm);
                boolean hasError = warnings.stream().anyMatch(w -> "error".equals(w.get("level")));
                if (hasError) {
                    fail++;
                    String msg = warnings.stream().filter(w -> "error".equals(w.get("level")))
                        .map(w -> String.valueOf(w.get("message"))).findFirst().orElse("未知错误");
                    errors.add("样本" + brxxId + ": " + msg);
                    continue;
                }
                Map<String, Object> r = stateMachine.transition(brxxId, 2, czydm);
                if (Boolean.TRUE.equals(r.get("success"))) success++;
                else { fail++; errors.add("样本" + brxxId + ": " + r.get("message")); }
            } catch (Exception e) {
                fail++;
                errors.add("样本" + brxxId + ": " + e.getMessage());
            }
        }
        result.put("success", true);
        result.put("message", "批量审核完成：成功" + success + "条，失败" + fail + "条");
        result.put("successCount", success);
        result.put("failCount", fail);
        result.put("errors", errors);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/input-result")
    @Transactional
    public ResponseEntity<Map<String, Object>> batchInputResult(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Integer> brxxIds = (List<Integer>) params.get("brxxIds");
            Integer xmid = (Integer) params.get("xmid");
            String jyjg = (String) params.get("jyjg");
            if (brxxIds == null || xmid == null || jyjg == null) {
                result.put("success", false);
                result.put("message", "参数不完整");
                return ResponseEntity.ok(result);
            }
            int updated = batchProcessService.batchInputResult(brxxIds, xmid, jyjg);
            result.put("success", true);
            result.put("message", "批量录入完成，更新" + updated + "条");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量录入失败：" + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}
