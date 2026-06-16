package com.lis.controller;

import com.lis.service.SampleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/sample", "/api/sample"})
@Slf4j
public class SampleComboController {

    @Autowired
    private SampleService sampleService;

    @GetMapping("/combos")
    public ResponseEntity<List<Map<String, Object>>> listCombos(
            @RequestParam(required = false) Integer sbDjid,
            @RequestParam(required = false) String keyword) {
        try {
            return ResponseEntity.ok(sampleService.listCombos(sbDjid, keyword));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @GetMapping("/combos/{zhid}/items")
    public ResponseEntity<List<Map<String, Object>>> listComboItems(
            @PathVariable("zhid") Integer zhid,
            @RequestParam(required = false) Integer sbDjid,
            @RequestParam(required = false) String sampleType,
            @RequestParam(required = false) Integer sex,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Integer ageUnit) {
        try {
            return ResponseEntity.ok(sampleService.listComboItems(zhid, sbDjid));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }
}
