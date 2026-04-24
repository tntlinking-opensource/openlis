package com.lis.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.CallableStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 7.2 人员信息设置
 * 对应旧系统 p_rysz.pas + 存储过程 sys_se_ryxx / sys_in_ryxx
 * 这里直接调用存储过程，保持与旧逻辑一致。
 */
@RestController
@RequestMapping("/basic/staff")
public class StaffSettingController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 7.4 人员工作组设置用 - 人员 × 工作组 视图列表
     * 说明：修正 JOIN 表名为 sys_gzzd，使用CAST解决字符集不匹配问题
     */
    @GetMapping("/group-list")
    public ResponseEntity<List<Map<String, Object>>> listGroup(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String ksdm,
            @RequestParam(required = false) String gzzdm) {

        String k = (keyword == null || keyword.trim().isEmpty()) ? "" : keyword.trim();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.czydm, c.czyxm, c.pym, c.ksdm, k.ksmc, ");
        sql.append("       c.gzzdm, IFNULL(g.gzmc, '') AS gzzmc, IFNULL(g.gzzlx, 0) AS gzzlx, IFNULL(g.sybz, 1) AS gzz_sybz ");
        sql.append("  FROM sys_czydm c ");
        sql.append("  LEFT JOIN sys_kssz k ON CAST(c.ksdm AS CHAR) = CAST(k.ksdm AS CHAR) ");
        sql.append("  LEFT JOIN sys_gzzd g ON CAST(c.gzzdm AS CHAR) = CAST(g.gzdm AS CHAR) ");
        sql.append(" WHERE 1 = 1 ");

        List<Object> params = new ArrayList<Object>();
        if (!k.isEmpty()) {
            sql.append("   AND (c.czydm LIKE ? OR c.czyxm LIKE ? OR c.pym LIKE ?) ");
            String like = "%" + k + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (ksdm != null && !ksdm.trim().isEmpty()) {
            sql.append("   AND c.ksdm = ? ");
            params.add(ksdm.trim());
        }
        if (gzzdm != null && !gzzdm.trim().isEmpty()) {
            sql.append("   AND c.gzzdm = ? ");
            params.add(gzzdm.trim());
        }
        sql.append(" ORDER BY c.czydm ");

        List<Map<String, Object>> list = jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> {
                    Map<String, Object> row = new HashMap<String, Object>();
                    row.put("czydm", rs.getString("czydm"));
                    row.put("czyxm", rs.getString("czyxm"));
                    row.put("pym", rs.getString("pym"));
                    row.put("ksdm", rs.getString("ksdm"));
                    row.put("ksmc", rs.getString("ksmc"));
                    row.put("gzzdm", rs.getString("gzzdm"));
                    row.put("gzzmc", rs.getString("gzzmc"));
                    row.put("gzzlx", rs.getObject("gzzlx"));
                    row.put("gzzSybz", rs.getObject("gzz_sybz"));
                    return row;
                }
        );

        return ResponseEntity.ok(list);
    }

    /**
     * 人员列表查询（直接查询 sys_czydm 表，替代原存储过程 sys_se_ryxx）
     * - keyword 作为姓名/拼音模糊
     * - sybz 对应使用标志过滤
     */
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(@RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) Boolean sybz) {
        String k = (keyword == null || keyword.trim().isEmpty()) ? "" : keyword.trim();

        // 直接查询 sys_czydm 表（替代原存储过程 dbo.sys_se_ryxx）
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.czydm, c.czyxm, c.pym, c.ksdm, c.zcdm, c.sybz, c.glybz ");
        sql.append("FROM sys_czydm c WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (!k.isEmpty()) {
            sql.append(" AND (c.czyxm LIKE ? OR c.pym LIKE ?) ");
            params.add("%" + k + "%");
            params.add("%" + k + "%");
        }
        
        if (sybz != null) {
            sql.append(" AND c.sybz = ? ");
            params.add(sybz ? 1 : 0);
        }
        
        sql.append("ORDER BY c.czydm");
        
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        return ResponseEntity.ok(result);
    }

    /**
     * 保存人员信息（新增 / 修改）
     * 改为MySQL直接操作
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> save(@RequestBody StaffSaveRequest req) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (req.getCzydm() == null || req.getCzydm().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "人员代码不能为空");
                return ResponseEntity.ok(result);
            }
            if (req.getCzyxm() == null || req.getCzyxm().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "姓名不能为空");
                return ResponseEntity.ok(result);
            }
            if (req.getKsdm() == null || req.getKsdm().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "科室代码不能为空");
                return ResponseEntity.ok(result);
            }

            // 检查是否已存在
            List<Map<String, Object>> existing = jdbcTemplate.queryForList(
                "SELECT czydm FROM sys_czydm WHERE czydm = ?", req.getCzydm().trim());
            
            int sybzVal = (req.getSybz() == null || req.getSybz()) ? 1 : 0;
            int glybzVal = (req.getGlybz() != null && req.getGlybz()) ? 1 : 0;
            
            if (existing.isEmpty()) {
                // 新增
                jdbcTemplate.update(
                    "INSERT INTO sys_czydm (czydm, czyxm, pym, ksdm, zcdm, sybz, glybz, gzzdm) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    req.getCzydm().trim(),
                    req.getCzyxm().trim(),
                    req.getPym() != null ? req.getPym().trim() : "",
                    req.getKsdm().trim(),
                    req.getZcdm() != null ? req.getZcdm().trim() : "",
                    sybzVal,
                    glybzVal,
                    req.getGzzdm() != null ? req.getGzzdm().trim() : ""
                );
            } else {
                // 修改
                jdbcTemplate.update(
                    "UPDATE sys_czydm SET czyxm=?, pym=?, ksdm=?, zcdm=?, sybz=?, glybz=?, gzzdm=? WHERE czydm=?",
                    req.getCzyxm().trim(),
                    req.getPym() != null ? req.getPym().trim() : "",
                    req.getKsdm().trim(),
                    req.getZcdm() != null ? req.getZcdm().trim() : "",
                    sybzVal,
                    glybzVal,
                    req.getGzzdm() != null ? req.getGzzdm().trim() : "",
                    req.getCzydm().trim()
                );
            }

            result.put("success", true);
            result.put("message", "保存成功!");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "保存失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
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


