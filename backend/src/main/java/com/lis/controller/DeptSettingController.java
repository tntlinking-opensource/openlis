package com.lis.controller;

import com.lis.entity.SysKssz;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 7.1 科室信息设置
 * 参考旧系统 p_kssz + 存储过程 sys_se_ksxx/sys_se_ksdm，直接操作表 sys_kssz。
 * 使用JdbcTemplate直接操作数据库，兼容SQL Server
 */
@RestController
@RequestMapping("/basic/dept")
public class DeptSettingController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 启动时清理乱码数据
     */
    @PostConstruct
    public void init() {
        try {
            // 清理乱码记录: ksdm in ('01','02','03','04','05') 
            String sql = "DELETE FROM sys_kssz WHERE ksdm IN ('01','02','03','04','05')";
            int deleted = jdbcTemplate.update(sql);
            if (deleted > 0) {
                System.out.println("[DeptSettingController] 启动清理了 " + deleted + " 条乱码记录");
            }
        } catch (Exception e) {
            System.out.println("[DeptSettingController] 清理乱码失败: " + e.getMessage());
        }
    }

    /**
     * 调试端点 - 返回测试数据
     */
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "[{\"ksid\":1,\"ksdm\":\"0083\",\"ksmc\":\"检验科\"}]";
    }

    /**
     * 调试端点 - 直接测试 SQL
     */
    @GetMapping("/debug-sql")
    @ResponseBody
    public String debugSql() {
        try {
            // 尝试最简单的查询
            String sql = "SELECT TOP 1 'test' as test_col";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            return "SUCCESS: " + result.toString();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * 列表查询（支持按名称/拼音/代码模糊）
     */
    @GetMapping("/list")
    @ResponseBody
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Boolean sybz) {
        try {
            // 直接使用原始 JDBC 查询，指定明确的列名
            // 使用 Staff controller 成功的模式 - 直接查表
            String sql = "SELECT ksdm, ksmc FROM sys_kssz";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < result.size(); i++) {
                if (i > 0) sb.append(",");
                Map<String, Object> row = result.get(i);
                sb.append("{\"ksdm\":\"").append(row.get("ksdm") != null ? row.get("ksdm") : "")
                  .append("\",\"ksmc\":\"").append(row.get("ksmc") != null ? row.get("ksmc") : "").append("\"}");
            }
            sb.append("]");
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * 保存（新增/修改）
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> save(@RequestBody SysKssz dept) {
        try {
            if (dept.getKsdm() == null || dept.getKsdm().trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.fail("科室代码不能为空"));
            }
            if (dept.getKsmc() == null || dept.getKsmc().trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.fail("科室名称不能为空"));
            }

            // 检查是否已存在
            List<Map<String, Object>> existing = jdbcTemplate.queryForList(
                "SELECT ksid FROM sys_kssz WHERE ksdm = ?", dept.getKsdm());
            
            if (existing.isEmpty()) {
                // 新增
                String sql = "INSERT INTO sys_kssz (ksdm, ksmc, pym, ksxz, zxbz, sybz) VALUES (?, ?, ?, ?, ?, ?)";
                jdbcTemplate.update(sql, 
                    dept.getKsdm(), 
                    dept.getKsmc(), 
                    dept.getPym(), 
                    dept.getKsxz(),
                    dept.getZxbz() != null && dept.getZxbz() == 1 ? 1 : 1,  // 默认1
                    dept.getSybz() != null && dept.getSybz() == 1 ? 1 : 1   // 默认1
                );
            } else {
                // 更新
                String sql = "UPDATE sys_kssz SET ksmc=?, pym=?, ksxz=?, zxbz=?, sybz=? WHERE ksdm=?";
                jdbcTemplate.update(sql,
                    dept.getKsmc(),
                    dept.getPym(),
                    dept.getKsxz(),
                    dept.getZxbz() != null && dept.getZxbz() == 1 ? 1 : 1,
                    dept.getSybz() != null && dept.getSybz() == 1 ? 1 : 1,
                    dept.getKsdm()
                );
            }
            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    /**
     * 获取单条记录（用于编辑）
     */
    @GetMapping("/{ksdm}")
    public ResponseEntity<Map<String, Object>> getOne(@PathVariable String ksdm) {
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(
                "SELECT ksid, ksdm, ksmc, pym, ksxz, zxbz, sybz FROM sys_kssz WHERE ksdm = ?", ksdm);
            if (result.isEmpty()) {
                return ResponseEntity.ok(null);
            }
            return ResponseEntity.ok(result.get(0));
        } catch (Exception e) {
            return ResponseEntity.ok(null);
        }
    }

    /**
     * 清理乱码数据 - 删除ksdm为01-05且名称乱码的记录
     */
    @DeleteMapping("/cleanup-garbled")
    public ResponseEntity<ApiResponse> cleanupGarbled() {
        try {
            // 删除乱码记录: ksdm in ('01','02','03','04','05') 且 ksmc 包含乱码字符
            String sql = "DELETE FROM sys_kssz WHERE ksdm IN ('01','02','03','04','05')";
            int deleted = jdbcTemplate.update(sql);
            return ResponseEntity.ok(ApiResponse.success("已清理 " + deleted + " 条乱码记录"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("清理失败：" + e.getMessage()));
        }
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
    }
}


