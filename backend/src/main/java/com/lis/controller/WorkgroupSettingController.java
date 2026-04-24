package com.lis.controller;

import com.lis.entity.SysGzzsz;
import com.lis.mapper.SysGzzszMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 7.3 工作组别设置
 * 参考旧系统 p_gzzbsz + 存储过程 sys_se_gzzxx/sys_in_gzzxx
 */
@RestController
@RequestMapping("/basic/workgroup")
public class WorkgroupSettingController {

    @Autowired
    private SysGzzszMapper sysGzzszMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 启动时清理乱码数据
     */
    @PostConstruct
    public void init() {
        try {
            // 清理乱码记录: 保留 id=1,2,3,4 的正确记录，删除其他重复/乱码记录
            // 正确记录: 生化组(id=1), 免疫组(id=2), 临检组(id=3), 微生物组(id=4)
            String sql = "DELETE FROM sys_gzzd WHERE gzid NOT IN (1,2,3,4)";
            int deleted = jdbcTemplate.update(sql);
            if (deleted > 0) {
                System.out.println("[WorkgroupSettingController] 启动清理了 " + deleted + " 条乱码记录");
            }
        } catch (Exception e) {
            System.out.println("[WorkgroupSettingController] 清理乱码失败: " + e.getMessage());
        }
    }

    /**
     * 列表查询（直接查询 sys_gzzd 表，MySQL兼容）
     */
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(
            @RequestParam(required = false) String ssksdm,
            @RequestParam(required = false) String gzzdm,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer gzzlx,
            @RequestParam(required = false) Boolean sybz) {
        try {
            // 直接查询 sys_gzzd 表（MySQL兼容版本）
            // 前端期望字段: gzzdm, gzzmc, pym, gzzlxmc, his_ksdm, xh, sybz
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT ");
            sql.append("  gzid AS id, ");
            sql.append("  gzdm AS gzzdm, ");          // 工作组代码
            sql.append("  gzmc AS gzzmc, ");          // 工作组名称
            sql.append("  pym, ");                     // 拼音码
            sql.append("  gzzlx, ");                   // 工作组类型(数值)
            sql.append("  CASE gzzlx WHEN 1 THEN '检验类' WHEN 2 THEN '库房类' WHEN 3 THEN '后勤类' ELSE '' END AS gzzlxmc, ");  // 工作组类型(文本)
            sql.append("  '' AS his_ksdm, ");          // HIS代码（暂无）
            sql.append("  COALESCE(xh, 1) AS xh, ");  // 序号
            sql.append("  COALESCE(sybz, 1) AS sybz ");  // 使用标志
            sql.append("FROM sys_gzzd WHERE 1=1 ");
            
            List<Object> params = new ArrayList<>();
            
            if (gzzdm != null && !gzzdm.isEmpty()) {
                sql.append(" AND gzdm = ? ");
                params.add(gzzdm);
            }
            
            if (keyword != null && !keyword.isEmpty()) {
                sql.append(" AND (gzmc LIKE ? OR pym LIKE ?) ");
                params.add("%" + keyword + "%");
                params.add("%" + keyword + "%");
            }
            
            if (sybz != null) {
                sql.append(" AND sybz = ? ");
                params.add(sybz ? 1 : 0);
            }
            
            sql.append("ORDER BY gzdm");
            
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Collections.<Map<String, Object>>emptyList());
        }
    }

    /**
     * 保存（MySQL直接操作）
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> save(@RequestBody SaveWorkgroupRequest req) {
        try {
            if (req.getGzzdm() == null || req.getGzzdm().trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.fail("工作组代码不能为空"));
            }
            if (req.getGzzmc() == null || req.getGzzmc().trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.fail("工作组名称不能为空"));
            }
            if (req.getGzzlx() == null) {
                return ResponseEntity.ok(ApiResponse.fail("工作组类型不能为空"));
            }
            if (req.getXh() == null) {
                req.setXh(1); // 默认序号
            }

            // 检查是否已存在
            List<Map<String, Object>> existing = jdbcTemplate.queryForList(
                "SELECT gzid FROM sys_gzzd WHERE gzdm = ?", req.getGzzdm().trim());
            
            int sybzVal = (req.getSybz() == null || req.getSybz()) ? 1 : 0;
            
            if (existing.isEmpty()) {
                // 新增
                jdbcTemplate.update(
                    "INSERT INTO sys_gzzd (gzdm, gzmc, pym, gzzlx, xh, sybz) VALUES (?, ?, ?, ?, ?, ?)",
                    req.getGzzdm().trim(),
                    req.getGzzmc().trim(),
                    req.getPym() != null ? req.getPym().trim() : "",
                    req.getGzzlx(),
                    req.getXh(),
                    sybzVal
                );
                return ResponseEntity.ok(ApiResponse.success("保存成功!"));
            } else {
                // 修改
                jdbcTemplate.update(
                    "UPDATE sys_gzzd SET gzmc=?, pym=?, gzzlx=?, xh=?, sybz=? WHERE gzdm=?",
                    req.getGzzmc().trim(),
                    req.getPym() != null ? req.getPym().trim() : "",
                    req.getGzzlx(),
                    req.getXh(),
                    sybzVal,
                    req.getGzzdm().trim()
                );
                return ResponseEntity.ok(ApiResponse.success("保存成功!"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @Data
    public static class SaveWorkgroupRequest {
        private String ksdm;        // 所属科室代码
        private String gzzdm;       // 工作组代码
        private String gzzmc;      // 工作组名称
        private String pym;         // 拼音码
        private Integer gzzlx;      // 工作组类型（1-检验类，2-库房类，3-后勤类）
        private String hisKsdm;    // HIS科室代码
        private Integer xh;         // 序号
        private Boolean sybz;       // 使用标志
        private Boolean xgbz;       // 修改标志（false=新增，true=修改）
    }

    @Data
    public static class ApiResponse {
        private Boolean success;
        private String message;

        public static ApiResponse success(String msg) {
            ApiResponse r = new ApiResponse();
            r.setSuccess(true);
            r.setMessage(msg);
            return r;
        }

        public static ApiResponse fail(String msg) {
            ApiResponse r = new ApiResponse();
            r.setSuccess(false);
            r.setMessage(msg);
            return r;
        }

        public static ApiResponse result(boolean ok, String msg) {
            ApiResponse r = new ApiResponse();
            r.setSuccess(ok);
            r.setMessage(msg);
            return r;
        }
    }
}

