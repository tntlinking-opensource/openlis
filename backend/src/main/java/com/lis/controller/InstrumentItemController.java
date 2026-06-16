package com.lis.controller;

import com.lis.service.InstrumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/instrument-item")
@Slf4j
public class InstrumentItemController {

    @Autowired
    private InstrumentService instrumentService;

    @GetMapping("/tree")
    public ResponseEntity<List<Map<String, Object>>> tree() {
        return ResponseEntity.ok(instrumentService.getItemTree());
    }

    @GetMapping("/{instId}/{itemId}/ref-range")
    public ResponseEntity<List<Map<String, Object>>> getRefRanges(
            @PathVariable Integer instId, @PathVariable Integer itemId) {
        return ResponseEntity.ok(instrumentService.getRefRanges(instId, itemId));
    }

    @PostMapping("/ref-range")
    public ResponseEntity<InstrumentService.ApiResult> saveRefRange(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(instrumentService.saveRefRange(data));
    }

    @DeleteMapping("/ref-range/{id}")
    public ResponseEntity<InstrumentService.ApiResult> deleteRefRange(@PathVariable Integer id) {
        return ResponseEntity.ok(instrumentService.deleteRefRange(id));
    }

    @GetMapping("/{instId}/{itemId}/default")
    public ResponseEntity<Map<String, Object>> getDefault(@PathVariable Integer instId, @PathVariable Integer itemId) {
        return ResponseEntity.ok(instrumentService.getDefaultValue(instId, itemId));
    }

    @PostMapping("/default")
    public ResponseEntity<InstrumentService.ApiResult> saveDefault(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(instrumentService.saveDefault(data));
    }

    @PostMapping("/batch-coeff")
    public ResponseEntity<InstrumentService.ApiResult> batchCoeff(@RequestBody Map<String, Object> data) {
        Integer sbDjid = toInt(data.get("sbDjid"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
        return ResponseEntity.ok(instrumentService.batchCoeff(sbDjid, items));
    }

    @GetMapping("/{sbDjid}/items")
    public ResponseEntity<List<Map<String, Object>>> getItemsByInstrument(@PathVariable Integer sbDjid) {
        return ResponseEntity.ok(instrumentService.getItemsByInstrument(sbDjid));
    }

    @GetMapping("/{sbDjid}/coefficients")
    public ResponseEntity<List<Map<String, Object>>> getCoefficients(@PathVariable Integer sbDjid) {
        return ResponseEntity.ok(instrumentService.getCoefficientsByInstrument(sbDjid));
    }

    @PostMapping("/add-item")
    public ResponseEntity<InstrumentService.ApiResult> addItem(@RequestBody Map<String, Object> data) {
        Integer sbDjid = toInt(data.get("sbDjid"));
        Integer xmid = toInt(data.get("xmid"));
        return ResponseEntity.ok(instrumentService.addItemToInstrument(sbDjid, xmid));
    }

    @DeleteMapping("/{sbDjid}/{xmid}")
    public ResponseEntity<InstrumentService.ApiResult> removeItem(
            @PathVariable Integer sbDjid, @PathVariable Integer xmid) {
        return ResponseEntity.ok(instrumentService.removeItemFromInstrument(sbDjid, xmid));
    }

    @PostMapping("/inst-item")
    public ResponseEntity<InstrumentService.ApiResult> saveInstItem(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(instrumentService.saveInstItem(data));
    }

    @GetMapping("/{sbDjid}/{xmid}/data-replace")
    public ResponseEntity<List<Map<String, Object>>> getDataReplaceSettings(
            @PathVariable Integer sbDjid, @PathVariable Integer xmid) {
        return ResponseEntity.ok(instrumentService.getDataReplaceSettings(sbDjid, xmid));
    }

    @PostMapping("/data-replace")
    public ResponseEntity<InstrumentService.ApiResult> saveDataReplaceSetting(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(instrumentService.saveDataReplaceSetting(data));
    }

    @DeleteMapping("/data-replace/{id}")
    public ResponseEntity<InstrumentService.ApiResult> deleteDataReplaceSetting(@PathVariable Integer id) {
        return ResponseEntity.ok(instrumentService.deleteDataReplaceSetting(id));
    }

    @GetMapping("/{sbDjid}/{xmid}/formula")
    public ResponseEntity<Map<String, Object>> getFormula(
            @PathVariable Integer sbDjid, @PathVariable Integer xmid) {
        return ResponseEntity.ok(instrumentService.getFormula(sbDjid, xmid));
    }

    @PostMapping("/formula")
    public ResponseEntity<InstrumentService.ApiResult> saveFormula(@RequestBody Map<String, Object> data) {
        return ResponseEntity.ok(instrumentService.saveFormula(data));
    }

    @GetMapping("/formula/list")
    public ResponseEntity<List<Map<String, Object>>> getFormulaList(@RequestParam Integer sbDjid) {
        return ResponseEntity.ok(instrumentService.getFormulaList(sbDjid));
    }

    @GetMapping("/projects")
    public ResponseEntity<List<Map<String, Object>>> getProjectsByInstrument(@RequestParam Integer sbDjid) {
        return ResponseEntity.ok(instrumentService.getProjectsByInstrument(sbDjid));
    }

    private Integer toInt(Object val) {
        if (val == null) return null;
        try { return Integer.parseInt(String.valueOf(val)); } catch (Exception e) { return null; }
    }
}
