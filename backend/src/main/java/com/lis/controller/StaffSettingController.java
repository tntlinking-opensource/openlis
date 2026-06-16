package com.lis.controller;

import com.lis.annotation.OperationLog;
import com.lis.service.BaseSettingService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/basic/staff")
@Slf4j
public class StaffSettingController {

    @Autowired
    private BaseSettingService baseSettingService;

    @GetMapping("/group-list")
    public ResponseEntity<List<Map<String, Object>>> listGroup(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String ksdm,
            @RequestParam(required = false) String gzzdm) {
        List<Map<String, Object>> list = baseSettingService.listStaffWithGroup(keyword, ksdm, gzzdm);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(@RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) Boolean sybz) {
        List<Map<String, Object>> result = baseSettingService.listStaff(keyword, sybz);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/save")
    @OperationLog(value = "保存人员", module = "基本设置")
    public ResponseEntity<Map<String, Object>> save(@RequestBody StaffSaveRequest req) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> staffData = new HashMap<>();
            staffData.put("czydm", req.getCzydm());
            staffData.put("czyxm", req.getCzyxm());
            staffData.put("pym", req.getPym());
            staffData.put("ksdm", req.getKsdm());
            staffData.put("zcdm", req.getZcdm());
            staffData.put("sybz", req.getSybz());
            staffData.put("glybz", req.getGlybz());
            staffData.put("gzzdm", req.getGzzdm());

            baseSettingService.saveStaff(staffData);
            result.put("success", true);
            result.put("message", "保存成功!");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("操作失败", e);
            result.put("success", false);
            result.put("message", "保存失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @Data
    public static class StaffDto {
        private String czydm;
        private String czyxm;
        private String pym;
        private String ksdm;
        private String ksmc;
        private String zcdm;
        private String zcmc;
        private String hisCzydm;
        private Boolean ysbz;
        private Boolean czybz;
        private Boolean glybz;
        private Boolean sybz;
        private String gzzdm;
        private String gzzmc;
        private String czysfzhm;
    }

    @Data
    public static class StaffSaveRequest {
        private String czydm;
        private String czyxm;
        private String pym;
        private String ksdm;
        private String zcdm;
        private String hisCzydm;
        private Boolean ysbz;
        private Boolean czybz;
        private Boolean glybz;
        private Boolean sybz;
        private String gzzdm;
        private Boolean xgbz;
        private Boolean qkmm;
        private String czysfzhm;
        private Boolean qkdzqm;
    }
}
