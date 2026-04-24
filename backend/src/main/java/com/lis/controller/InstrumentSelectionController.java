package com.lis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1.2 选择检验仪器
 * 对应旧系统 p_xzyq.pas 中的选择仪器逻辑
 * 已迁移到MySQL
 */
@RestController
@RequestMapping("/instrument/selection")
public class InstrumentSelectionController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取检验科室列表（用于检验科室选择）
     * 使用MySQL表 sys_kssz
     */
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getInstrumentList(
            @RequestParam(required = false) Boolean sybz) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT ksid AS id, ksdm, ksmc, pym, ksxz, zxbz, sybz ");
            sql.append("FROM sys_kssz WHERE 1=1 ");
            
            if (sybz != null) {
                sql.append(" AND sybz = ? ");
            }
            sql.append("ORDER BY ksdm");
            
            List<Map<String, Object>> result;
            if (sybz != null) {
                result = jdbcTemplate.queryForList(sql.toString(), sybz ? 1 : 0);
            } else {
                result = jdbcTemplate.queryForList(sql.toString());
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    /**
     * 获取仪器设备列表
     * 使用 MySQL 表 sys_sbdjb
     */
    @GetMapping("/devices")
    public ResponseEntity<List<Map<String, Object>>> getDevicesByCategory(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String ksdm,
            @RequestParam(required = false, defaultValue = "") String gzzdm) {
        try {
            String sql = "SELECT sb_djid, sbmc FROM sys_sbdjb";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
