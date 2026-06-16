package com.lis.controller;

import com.lis.dto.BatchRequest;
import com.lis.mapper.BgxtBrxxMapper;
import com.lis.mapper.BgxtJyjgMapper;
import com.lis.service.SampleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import com.lis.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/sample", "/api/sample"})
@Slf4j
public class SampleBatchController {

    @Autowired
    private SampleService sampleService;

    @Autowired
    private BgxtBrxxMapper brxxMapper;

    @Autowired
    private BgxtJyjgMapper jyjgMapper;

    @OperationLog(value = "批量审核", module = "样本审核")
    @PostMapping("/batch/audit")
    public ResponseEntity<Map<String, Object>> batchAudit(@RequestBody BatchRequest payload) {
        try {
            if (payload.getBrxxIds() == null || payload.getBrxxIds().isEmpty()) {
                Map<String, Object> resp = new java.util.HashMap<>();
                resp.put("success", false);
                resp.put("message", "请选择要审核的样本");
                return ResponseEntity.badRequest().body(resp);
            }
            return ResponseEntity.ok(sampleService.batchAudit(payload.getBrxxIds(), payload.getCzydm()));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new java.util.HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @OperationLog(value = "批量打印", module = "报告打印")
    @PostMapping({"/batch/print", "/batch-print"})
    public ResponseEntity<Map<String, Object>> batchPrint(@RequestBody BatchRequest payload) {
        try {
            if (payload.getBrxxIds() == null || payload.getBrxxIds().isEmpty()) {
                Map<String, Object> resp = new java.util.HashMap<>();
                resp.put("success", false);
                resp.put("message", "请选择要打印的样本");
                return ResponseEntity.badRequest().body(resp);
            }
            return ResponseEntity.ok(sampleService.batchPrint(payload.getBrxxIds(), payload.getCzydm(), payload.getSkipPrinted(), payload.getTemplateId()));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new java.util.HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @OperationLog(value = "批量作废", module = "样本管理")
    @PostMapping("/batch/invalidate")
    public ResponseEntity<Map<String, Object>> batchInvalidate(@RequestBody BatchRequest payload) {
        try {
            if (payload.getBrxxIds() == null || payload.getBrxxIds().isEmpty()) {
                Map<String, Object> resp = new java.util.HashMap<>();
                resp.put("success", false);
                resp.put("message", "请选择要作废的样本");
                return ResponseEntity.badRequest().body(resp);
            }
            return ResponseEntity.ok(sampleService.batchInvalidate(payload.getBrxxIds(), payload.getReason()));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new java.util.HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @OperationLog(value = "批量取消审核", module = "样本审核")
    @PostMapping("/batch/unaudit")
    public ResponseEntity<Map<String, Object>> batchUnaudit(@RequestBody BatchRequest payload) {
        try {
            if (payload.getBrxxIds() == null || payload.getBrxxIds().isEmpty()) {
                Map<String, Object> resp = new java.util.HashMap<>();
                resp.put("success", false);
                resp.put("message", "请选择要取消审核的样本");
                return ResponseEntity.badRequest().body(resp);
            }
            return ResponseEntity.ok(sampleService.batchUnaudit(payload.getBrxxIds(), payload.getCzydm()));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new java.util.HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @DeleteMapping("/cleanup-duplicates")
    @Transactional
    public ResponseEntity<Map<String, Object>> cleanupDuplicates() {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Map<String, Object>> allSamples = brxxMapper.selectAllForCleanup();
            Map<String, List<Integer>> syhGroups = new LinkedHashMap<>();
            for (Map<String, Object> sample : allSamples) {
                Integer brxxId = (Integer) sample.get("brxx_id");
                String syh = (String) sample.get("syh");
                syhGroups.computeIfAbsent(syh, k -> new ArrayList<>()).add(brxxId);
            }

            int deletedCount = 0;
            for (Map.Entry<String, List<Integer>> entry : syhGroups.entrySet()) {
                List<Integer> ids = entry.getValue();
                if (ids.size() > 1) {
                    ids.sort(Comparator.reverseOrder());
                    List<Integer> toKeep = ids.subList(0, 1);
                    List<Integer> toDelete = ids.subList(1, ids.size());
                    for (Integer id : toDelete) {
                        jyjgMapper.deleteByBrxxId(id);
                        brxxMapper.deleteById(id);
                        deletedCount++;
                    }
                    log.info("Cleaned up {} duplicates for syh={}, kept brxx_id={}", toDelete.size(), entry.getKey(), toKeep.get(0));
                }
            }
            resp.put("success", true);
            resp.put("deletedCount", deletedCount);
            resp.put("message", "清理完成，删除了 " + deletedCount + " 条重复数据");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            log.error("清理失败", e);
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}
