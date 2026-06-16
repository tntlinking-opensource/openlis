package com.lis.controller;

import com.lis.service.InstrumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/instrument/selection")
@Slf4j
public class InstrumentSelectionController {

    @Autowired
    private InstrumentService instrumentService;

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getInstrumentList(
            @RequestParam(required = false) Boolean sybz) {
        try {
            return ResponseEntity.ok(instrumentService.listDepartments(sybz));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @GetMapping("/devices")
    public ResponseEntity<List<Map<String, Object>>> getDevicesByCategory(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String ksdm,
            @RequestParam(required = false, defaultValue = "") String gzzdm) {
        try {
            return ResponseEntity.ok(instrumentService.listDevices(category, ksdm, gzzdm));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
