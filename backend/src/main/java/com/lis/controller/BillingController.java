package com.lis.controller;

import com.lis.dto.BillingRequest;
import com.lis.service.BillingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.lis.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/billing")
@Slf4j
public class BillingController {

    @Autowired
    private BillingService billingService;

    @GetMapping("/samples")
    public ResponseEntity<List<Map<String, Object>>> samples(
            @RequestParam(required = false) String syh,
            @RequestParam(required = false) String brxm,
            @RequestParam(required = false) String brxxTmh,
            @RequestParam(required = false) String jyrq) {
        return ResponseEntity.ok(billingService.listBillingSamples(syh, brxm, brxxTmh, jyrq));
    }

    @GetMapping("/details/{brxxId}")
    public ResponseEntity<List<Map<String, Object>>> details(@PathVariable Integer brxxId) {
        return ResponseEntity.ok(billingService.getBillingDetails(brxxId));
    }

    @GetMapping("/status/{sampleId}")
    public ResponseEntity<Map<String, Object>> status(@PathVariable Integer sampleId) {
        return ResponseEntity.ok(billingService.getBillingStatus(sampleId));
    }

    @OperationLog(value = "确认收费", module = "收费管理")
    @PostMapping("/confirm/{sampleId}")
    public ResponseEntity<Map<String, Object>> confirm(@PathVariable Integer sampleId, @RequestBody BillingRequest params) {
        return ResponseEntity.ok(billingService.confirmBilling(sampleId, params.getCzydm()));
    }

    @OperationLog(value = "取消收费", module = "收费管理")
    @PostMapping("/cancel/{sampleId}")
    public ResponseEntity<Map<String, Object>> cancel(@PathVariable Integer sampleId, @RequestBody BillingRequest params) {
        return ResponseEntity.ok(billingService.cancelBilling(sampleId, params.getCzydm()));
    }

    @OperationLog(value = "批量确认收费", module = "收费管理")
    @PostMapping("/batch-confirm")
    public ResponseEntity<Map<String, Object>> batchConfirm(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Integer> brxxIds = (List<Integer>) params.get("brxxIds");
        String czydm = (String) params.get("czydm");
        return ResponseEntity.ok(billingService.batchConfirmBilling(brxxIds, czydm));
    }

    @OperationLog(value = "批量取消收费", module = "收费管理")
    @PostMapping("/batch-cancel")
    public ResponseEntity<Map<String, Object>> batchCancel(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Integer> brxxIds = (List<Integer>) params.get("brxxIds");
        String czydm = (String) params.get("czydm");
        return ResponseEntity.ok(billingService.batchCancelBilling(brxxIds, czydm));
    }

    @OperationLog(value = "批量作废", module = "收费管理")
    @PostMapping("/batch-invalidate")
    public ResponseEntity<Map<String, Object>> batchInvalidate(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Integer> brxxIds = (List<Integer>) params.get("brxxIds");
        String czydm = (String) params.get("czydm");
        return ResponseEntity.ok(billingService.batchInvalidateBilling(brxxIds, czydm));
    }
}