package com.lis.controller;

import com.lis.annotation.OperationLog;
import com.lis.service.InstrumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/instrument-combo")
@Slf4j
public class InstrumentComboController {

    @Autowired
    private InstrumentService instrumentService;

    @GetMapping("/tree")
    public ResponseEntity<List<Map<String, Object>>> tree() {
        return ResponseEntity.ok(instrumentService.getInstrumentTree());
    }

    @PostMapping("/assign")
    @OperationLog(value = "分配组合到仪器", module = "仪器设置")
    public ResponseEntity<InstrumentService.ApiResult> assign(@RequestBody Map<String, Object> data) {
        Integer sbDjid = toInt(data.get("sbDjid"));
        Integer zhid = toInt(data.get("zhid"));
        return ResponseEntity.ok(instrumentService.assignCombo(sbDjid, zhid));
    }

    @DeleteMapping("/{instId}/{comboId}")
    @OperationLog(value = "移除仪器组合", module = "仪器设置")
    public ResponseEntity<InstrumentService.ApiResult> remove(@PathVariable Integer instId, @PathVariable Integer comboId) {
        return ResponseEntity.ok(instrumentService.removeCombo(instId, comboId));
    }

    @GetMapping("/unassigned-combos")
    public ResponseEntity<List<Map<String, Object>>> unassignedCombos(@RequestParam Integer sbDjid) {
        return ResponseEntity.ok(instrumentService.getUnassignedCombos(sbDjid));
    }

    private Integer toInt(Object val) {
        if (val == null) return null;
        try { return Integer.parseInt(String.valueOf(val)); } catch (Exception e) { return null; }
    }
}
