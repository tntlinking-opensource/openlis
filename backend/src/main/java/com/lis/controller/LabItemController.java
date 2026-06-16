package com.lis.controller;

import com.lis.annotation.OperationLog;
import com.lis.service.InstrumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/labItem")
@Slf4j
public class LabItemController {

    @Autowired
    private InstrumentService instrumentService;

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchItem(@RequestParam String keyword) {
        try {
            return ResponseEntity.ok(instrumentService.searchItems(keyword));
        } catch (Exception e) {
            log.error("搜索项目异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/combo")
    public ResponseEntity<List<Map<String, Object>>> getCombosForItem(@RequestParam Integer xmid) {
        try {
            return ResponseEntity.ok(instrumentService.listCombosForItem(xmid));
        } catch (Exception e) {
            log.error("获取项目组合异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/combo/instruments")
    public ResponseEntity<List<Map<String, Object>>> getComboInstruments(@RequestParam Integer xmid) {
        try {
            return ResponseEntity.ok(instrumentService.listInstrumentsForItem(xmid));
        } catch (Exception e) {
            log.error("获取组合仪器异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/combo/search")
    public ResponseEntity<List<Map<String, Object>>> searchCombos(@RequestParam String keyword) {
        try {
            return ResponseEntity.ok(instrumentService.searchCombos(keyword));
        } catch (Exception e) {
            log.error("搜索组合异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/combo/add")
    @OperationLog(value = "添加组合到项目", module = "仪器设置")
    public ResponseEntity<InstrumentService.SaveResult> addComboToItem(@RequestBody Map<String, Object> data) {
        try {
            Integer xmid = toInt(data.get("xmid"));
            Integer zhid = toInt(data.get("zhid"));
            return ResponseEntity.ok(instrumentService.addComboToItem(xmid, zhid));
        } catch (Exception e) {
            log.error("添加组合异常", e);
            InstrumentService.SaveResult result = new InstrumentService.SaveResult();
            result.setSuccess(false);
            result.setMessage("操作失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @DeleteMapping("/combo/remove")
    @OperationLog(value = "从项目移除组合", module = "仪器设置")
    public ResponseEntity<InstrumentService.SaveResult> removeComboFromItem(@RequestParam Integer xmid, @RequestParam Integer zhid) {
        try {
            return ResponseEntity.ok(instrumentService.removeComboFromItem(xmid, zhid));
        } catch (Exception e) {
            log.error("删除组合异常", e);
            InstrumentService.SaveResult result = new InstrumentService.SaveResult();
            result.setSuccess(false);
            result.setMessage("操作失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/combo/instrument/add")
    @OperationLog(value = "添加仪器到组合", module = "仪器设置")
    public ResponseEntity<InstrumentService.SaveResult> addInstrumentToCombo(@RequestBody Map<String, Object> data) {
        try {
            Integer xmid = toInt(data.get("xmid"));
            Integer sbDjid = toInt(data.get("sbDjid"));
            return ResponseEntity.ok(instrumentService.addInstrumentToCombo(xmid, sbDjid));
        } catch (Exception e) {
            log.error("添加仪器异常", e);
            InstrumentService.SaveResult result = new InstrumentService.SaveResult();
            result.setSuccess(false);
            result.setMessage("操作失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @DeleteMapping("/combo/instrument/remove")
    @OperationLog(value = "从组合移除仪器", module = "仪器设置")
    public ResponseEntity<InstrumentService.SaveResult> removeInstrumentFromCombo(@RequestParam Integer xmid, @RequestParam Integer sbDjid) {
        try {
            return ResponseEntity.ok(instrumentService.removeInstrumentFromCombo(xmid, sbDjid));
        } catch (Exception e) {
            log.error("移除仪器异常", e);
            InstrumentService.SaveResult result = new InstrumentService.SaveResult();
            result.setSuccess(false);
            result.setMessage("操作失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    private Integer toInt(Object val) {
        if (val == null) return null;
        try { return Integer.parseInt(String.valueOf(val)); } catch (Exception e) { return null; }
    }
}
