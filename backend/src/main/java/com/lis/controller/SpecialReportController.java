package com.lis.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 6.5 特殊报告设置（严格对齐旧系统 p_tsbbsz）
 *
 * 旧系统存储过程：
 * - bgxt_se_jyxm_mccx(@mc): 项目查询
 * - bgxt_se_tsbbsz(@mkid): 已有关联项
 * - bgxt_in_tsbbsz(@xmid,@mkid,@mksm,@error_code out): 添加关联
 * - bgxt_de_tsbbsz(@mkid,@xmid): 删除关联
 */
@RestController
@RequestMapping("/system/special-report")
public class SpecialReportController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 模块列表（与旧系统 ComboBox 固定两项一致）
     */
    @GetMapping("/module/list")
    public ResponseEntity<Map<String, Object>> moduleList() {
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("data", new Object[]{
                module(0, "精液分析报告"),
                module(1, "亚健康报告")
        });
        return ResponseEntity.ok(res);
    }

    private Map<String, Object> module(int mkid, String mksm) {
        Map<String, Object> m = new HashMap<>();
        m.put("mkid", mkid);
        m.put("mksm", mksm);
        return m;
    }

    /**
     * 已有关联项（改为MySQL直接查询）
     */
    @GetMapping("/linked/list")
    public ResponseEntity<Map<String, Object>> linkedList(@RequestParam Integer mkid) {
        try {
            // MySQL兼容：直接查询 bgxt_tsbbsz 表，关联 bgxt_xmzh_mx 获取中文名称
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT t.mkid, t.xmid, t.mksm, x.xmzwmc, x.xmdm as xmywmc " +
                "FROM bgxt_tsbbsz t " +
                "LEFT JOIN bgxt_xmzh_mx x ON t.xmid = x.id " +
                "WHERE t.mkid = ? " +
                "ORDER BY x.xmzwmc", mkid);
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("data", rows);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("data", new java.util.ArrayList<>());
            return ResponseEntity.ok(res);
        }
    }

    /**
     * 项目查询（改为MySQL直接查询）
     * 使用 bgxt_xmzh_mx 表获取检验项目列表
     */
    @GetMapping("/item/search")
    public ResponseEntity<Map<String, Object>> searchItem(@RequestParam String mc) {
        try {
            String keyword = (mc == null || mc.trim().isEmpty()) ? "%" : "%" + mc.trim() + "%";
            // MySQL兼容：直接查询 bgxt_xmzh_mx 表（组合项目表有正确的中文名称）
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id as xmid, xmzwmc, xmdw, xmdm as qtdm " +
                "FROM bgxt_xmzh_mx " +
                "WHERE xmzwmc LIKE '" + keyword + "' OR xmdm LIKE '" + keyword + "' " +
                "ORDER BY xmzwmc LIMIT 50");
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("data", rows);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> res = new HashMap<>();
            res.put("success", true);
            res.put("data", new java.util.ArrayList<>());
            return ResponseEntity.ok(res);
        }
    }

    /**
     * 添加关联（改为MySQL直接操作）
     */
    @PostMapping("/link")
    public ResponseEntity<Map<String, Object>> link(@RequestBody LinkRequest req) {
        Map<String, Object> res = new HashMap<>();
        try {
            // 检查是否已存在
            List<Map<String, Object>> existing = jdbcTemplate.queryForList(
                "SELECT COUNT(*) as cnt FROM bgxt_tsbbsz WHERE mkid = ? AND xmid = ?",
                req.getMkid(), req.getXmid());
            int count = existing.get(0).get("cnt") != null ? Integer.parseInt(String.valueOf(existing.get(0).get("cnt"))) : 0;
            
            if (count > 0) {
                res.put("success", false);
                res.put("message", "此关联已经存在，不能添加！");
                return ResponseEntity.ok(res);
            }
            
            // 插入关联
            jdbcTemplate.update(
                "INSERT INTO bgxt_tsbbsz (mkid, xmid, mksm) VALUES (?, ?, ?)",
                req.getMkid(), req.getXmid(), req.getMksm());
            
            res.put("success", true);
            res.put("message", "添加关联成功！");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "添加关联失败：" + e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    /**
     * 删除关联（改为MySQL直接操作）
     */
    @DeleteMapping("/link")
    public ResponseEntity<Map<String, Object>> unlink(@RequestParam Integer mkid, @RequestParam Integer xmid) {
        Map<String, Object> res = new HashMap<>();
        try {
            jdbcTemplate.update("DELETE FROM bgxt_tsbbsz WHERE mkid = ? AND xmid = ?", mkid, xmid);
            res.put("success", true);
            res.put("message", "删除关联成功！");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "删除关联失败：" + e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @Data
    public static class LinkRequest {
        private Integer mkid;
        private String mksm;
        private Integer xmid;
    }
}

