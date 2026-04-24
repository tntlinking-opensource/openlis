package com.lis.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.CallableStatement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作员权限设置控制器
 * 对应旧系统 P_czyqxsz.pas
 * 功能：为操作员分配菜单权限和模块权限
 */
@RestController
@RequestMapping("/system/operator-permission")
public class OperatorPermissionController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询操作员列表（用于权限设置）
     * 对应存储过程：sys_se_qxczydm
     */
    @GetMapping("/operators")
    public ResponseEntity<List<Map<String, Object>>> getOperators(
            @RequestParam(required = false, defaultValue = "") String czyxm,
            @RequestParam(required = false, defaultValue = "") String ksmc) {
        
        String sql = "{call sys_se_qxczydm(?, ?)}";
        List<Map<String, Object>> operators = jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setString(1, czyxm);
                    ps.setString(2, ksmc);
                },
                (rs, rowNum) -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("czydm", rs.getString("czydm"));
                    item.put("czyxm", rs.getString("czyxm"));
                    item.put("pym", rs.getString("pym"));
                    item.put("ksdm", rs.getString("ksdm"));
                    item.put("zcdm", rs.getString("zcdm"));
                    item.put("ksmc", rs.getString("ksmc"));
                    item.put("zcmc", rs.getString("zcmc"));
                    return item;
                }
        );

        return ResponseEntity.ok(operators);
    }

    /**
     * 查询子系统列表
     * 对应存储过程：sys_se_zxtmc
     */
    @GetMapping("/subsystems")
    public ResponseEntity<List<Map<String, Object>>> getSubsystems() {
        String sql = "{call sys_se_zxtmc}";
        List<Map<String, Object>> subsystems = jdbcTemplate.query(
                sql,
                ps -> {},
                (rs, rowNum) -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("zxtid", rs.getInt("zxtid"));
                    item.put("zxtjc", rs.getString("zxtjc"));
                    item.put("zxtmc", rs.getString("zxtmc"));
                    item.put("zxtbb", rs.getString("zxtbb"));
                    item.put("bz", rs.getString("bz"));
                    return item;
                }
        );
        return ResponseEntity.ok(subsystems);
    }

    /**
     * 查询模块分类列表
     * 对应存储过程：sys_se_mkfl
     */
    @GetMapping("/module-categories")
    public ResponseEntity<List<Map<String, Object>>> getModuleCategories(
            @RequestParam Integer zxtid) {
        String sql = "{call sys_se_mkfl(?)}";
        List<Map<String, Object>> categories = jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setInt(1, zxtid);
                },
                (rs, rowNum) -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("frm_name", rs.getString("frm_name"));
                    item.put("frm_caption", rs.getString("frm_caption"));
                    return item;
                }
        );
        return ResponseEntity.ok(categories);
    }

    /**
     * 查询模块列表（带权限标志）
     * 对应存储过程：sys_se_mk
     */
    @GetMapping("/modules")
    public ResponseEntity<List<Map<String, Object>>> getModules(
            @RequestParam String frmName,
            @RequestParam String czydm,
            @RequestParam(defaultValue = "false") Boolean showAll) {
        String sql = "{call sys_se_mk(?, ?, ?)}";
        List<Map<String, Object>> modules = jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setString(1, frmName);
                    ps.setString(2, czydm);
                    ps.setBoolean(3, showAll);
                },
                (rs, rowNum) -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("zxtid", rs.getInt("zxtid"));
                    item.put("mkdm", rs.getInt("mkdm"));
                    item.put("frm_name", rs.getString("frm_name"));
                    item.put("frm_caption", rs.getString("frm_caption"));
                    item.put("mkfl", rs.getString("mkfl"));
                    item.put("action_name", rs.getString("action_name"));
                    item.put("caption", rs.getString("caption"));
                    item.put("bz", rs.getBoolean("bz")); // 是否已授权
                    return item;
                }
        );
        return ResponseEntity.ok(modules);
    }

    /**
     * 查询权限大类列表
     * 对应存储过程：sys_se_qxdl
     */
    @GetMapping("/permission-categories")
    public ResponseEntity<List<Map<String, Object>>> getPermissionCategories(
            @RequestParam Integer zxtid) {
        String sql = "{call sys_se_qxdl(?)}";
        List<Map<String, Object>> categories = jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setInt(1, zxtid);
                },
                (rs, rowNum) -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", rs.getInt("id"));
                    item.put("zxtid", rs.getInt("zxtid"));
                    item.put("dldm", rs.getString("dldm"));
                    item.put("dlmc", rs.getString("dlmc"));
                    item.put("kmjs", rs.getInt("kmjs"));
                    return item;
                }
        );
        return ResponseEntity.ok(categories);
    }

    /**
     * 查询权限小类列表（带权限标志）
     * 对应存储过程：sys_se_qxxl
     */
    @GetMapping("/permission-items")
    public ResponseEntity<List<Map<String, Object>>> getPermissionItems(
            @RequestParam String dldm,
            @RequestParam String czydm) {
        String sql = "{call sys_se_qxxl(?, ?)}";
        List<Map<String, Object>> items = jdbcTemplate.query(
                sql,
                ps -> {
                    ps.setString(1, dldm);
                    ps.setString(2, czydm);
                },
                (rs, rowNum) -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("dldm", rs.getString("dldm"));
                    item.put("xldm", rs.getString("xldm"));
                    item.put("xlmc", rs.getString("xlmc"));
                    item.put("bz", rs.getBoolean("bz")); // 是否已授权
                    return item;
                }
        );
        return ResponseEntity.ok(items);
    }

    /**
     * 保存操作员权限（菜单权限）
     * 对应存储过程：sys_in_czyqx / sys_de_czyqx
     */
    @PostMapping("/save-menu-permissions")
    public ResponseEntity<Map<String, Object>> saveMenuPermissions(
            @RequestBody SaveMenuPermissionsRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String czydm = request.getCzydm();
            List<PermissionItem> items = request.getItems();

            for (PermissionItem item : items) {
                if (item.getBz() != null && item.getBz()) {
                    // 授权：调用 sys_in_czyqx
                    String sql = "{call sys_in_czyqx(?, ?, ?)}";
                    jdbcTemplate.execute(sql, (CallableStatement cs) -> {
                        cs.setString(1, czydm);
                        cs.setString(2, item.getXldm());
                        cs.registerOutParameter(3, Types.INTEGER);
                        cs.execute();
                        int errorCode = cs.getInt(3);
                        if (errorCode != -1) {
                            throw new RuntimeException("插入权限失败，错误代码：" + errorCode);
                        }
                        return null;
                    });
                } else {
                    // 取消授权：调用 sys_de_czyqx
                    String sql = "{call sys_de_czyqx(?, ?, ?)}";
                    jdbcTemplate.execute(sql, (CallableStatement cs) -> {
                        cs.setString(1, czydm);
                        cs.setString(2, item.getXldm());
                        cs.registerOutParameter(3, Types.INTEGER);
                        cs.execute();
                        int errorCode = cs.getInt(3);
                        if (errorCode != -1) {
                            throw new RuntimeException("删除权限失败，错误代码：" + errorCode);
                        }
                        return null;
                    });
                }
            }

            result.put("success", true);
            result.put("message", "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "保存失败：" + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @Data
    public static class SaveMenuPermissionsRequest {
        private String czydm;
        private List<PermissionItem> items;
    }

    @Data
    public static class PermissionItem {
        private String xldm; // 权限系列代码
        private Boolean bz;  // 是否授权
    }
}

