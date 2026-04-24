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
 * 6.1 工程师系统设置（严格对齐旧系统 p_gcsxtsz）
 *
 * 旧系统逻辑：
 * - 打开窗体：执行存储过程 sys_se_config，映射到界面控件
 * - 点击确定：执行存储过程 sys_in_config，包含校验与输出提示
 */
@RestController
@RequestMapping("/engineer/setting")
public class EngineerSystemSettingController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取系统配置（改为MySQL直接查询）
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        try {
            // MySQL兼容查询：直接从 sys_config 表读取
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT w_yydm, yymc, jykksdm, his_connectbz, his_connectlevel, " +
                "tj_connectbz, tj_jghcbz, ysz_jghcbz, ysz_connectbz, qtxt_jghcbz, websc, gdsj, his_connect_ybzx " +
                "FROM sys_config LIMIT 1");
            
            Map<String, Object> result = new HashMap<>();
            if (rows.isEmpty()) {
                // 返回默认值
                Map<String, Object> def = new HashMap<>();
                def.put("wYydm", "");
                def.put("yymc", "");
                def.put("hisConnectbz", false);
                def.put("hisConnectLevel", 0);
                def.put("tjConnectbz", false);
                def.put("tjJghcbz", false);
                def.put("yszJghcbz", false);
                def.put("yszConnectbz", false);
                def.put("qtxtJghcbz", false);
                def.put("websc", false);
                def.put("gdsj", 10);
                def.put("hisConnectYbzx", false);
                result.put("success", true);
                result.put("data", def);
                return ResponseEntity.ok(result);
            }

            Map<String, Object> row = rows.get(0);
            Map<String, Object> data = new HashMap<>();
            data.put("wYydm", row.get("w_yydm"));
            data.put("yymc", row.get("yymc"));
            data.put("jykksdm", row.get("jykksdm"));
            data.put("hisConnectbz", "1".equals(String.valueOf(row.get("his_connectbz"))));
            data.put("hisConnectLevel", row.get("his_connectlevel") != null ? row.get("his_connectlevel") : 0);
            data.put("tjConnectbz", "1".equals(String.valueOf(row.get("tj_connectbz"))));
            data.put("tjJghcbz", "1".equals(String.valueOf(row.get("tj_jghcbz"))));
            data.put("yszJghcbz", "1".equals(String.valueOf(row.get("ysz_jghcbz"))));
            data.put("yszConnectbz", "1".equals(String.valueOf(row.get("ysz_connectbz"))));
            data.put("qtxtJghcbz", "1".equals(String.valueOf(row.get("qtxt_jghcbz"))));
            data.put("websc", "1".equals(String.valueOf(row.get("websc"))));
            data.put("gdsj", row.get("gdsj") != null ? row.get("gdsj") : 10);
            data.put("hisConnectYbzx", "1".equals(String.valueOf(row.get("his_connect_ybzx"))));

            result.put("success", true);
            result.put("data", data);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            // 返回默认值而不是报错
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> def = new HashMap<>();
            def.put("wYydm", "");
            def.put("yymc", "");
            def.put("hisConnectbz", false);
            def.put("hisConnectLevel", 0);
            def.put("tjConnectbz", false);
            def.put("tjJghcbz", false);
            def.put("yszJghcbz", false);
            def.put("yszConnectbz", false);
            def.put("qtxtJghcbz", false);
            def.put("websc", false);
            def.put("gdsj", 10);
            def.put("hisConnectYbzx", false);
            result.put("success", true);
            result.put("data", def);
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 保存系统配置（改为MySQL直接操作）
     */
    @PostMapping("/config")
    public ResponseEntity<Map<String, Object>> saveConfig(@RequestBody SaveConfigRequest req) {
        Map<String, Object> result = new HashMap<>();

        // === 校验 ===
        if (req.getYymc() == null || req.getYymc().trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "医院名称不能为空!");
            return ResponseEntity.ok(result);
        }
        if (req.getGdsj() == null) {
            result.put("success", false);
            result.put("message", "归档时间不能为空!");
            return ResponseEntity.ok(result);
        }
        if (Boolean.TRUE.equals(req.getHisConnectbz())) {
            if (req.getHisConnectLevel() == null || req.getHisConnectLevel() <= 0) {
                result.put("success", false);
                result.put("message", "请选择HIS连接级别!");
                return ResponseEntity.ok(result);
            }
        }

        // 按旧系统：如果未勾选HIS连接，hisConnectLevel 传 0
        Integer hisLevel = Boolean.TRUE.equals(req.getHisConnectbz())
                ? (req.getHisConnectLevel() == null ? 0 : req.getHisConnectLevel())
                : 0;

        try {
            // MySQL兼容：直接UPDATE sys_config表
            int hisConnBz = Boolean.TRUE.equals(req.getHisConnectbz()) ? 1 : 0;
            int tjConnBz = Boolean.TRUE.equals(req.getTjConnectbz()) ? 1 : 0;
            int yszConnBz = Boolean.TRUE.equals(req.getYszConnectbz()) ? 1 : 0;
            int tjJghc = Boolean.TRUE.equals(req.getTjJghcbz()) ? 1 : 0;
            int yszJghc = Boolean.TRUE.equals(req.getYszJghcbz()) ? 1 : 0;
            int qtxtJghc = Boolean.TRUE.equals(req.getQtxtJghcbz()) ? 1 : 0;
            int websc = Boolean.TRUE.equals(req.getWebsc()) ? 1 : 0;
            int hisYbzx = Boolean.TRUE.equals(req.getHisConnectYbzx()) ? 1 : 0;

            // 检查是否存在记录
            List<Map<String, Object>> existing = jdbcTemplate.queryForList("SELECT COUNT(*) as cnt FROM sys_config");
            int count = existing.get(0).get("cnt") != null ? Integer.parseInt(String.valueOf(existing.get(0).get("cnt"))) : 0;
            
            int updated;
            if (count > 0) {
                // 更新
                updated = jdbcTemplate.update(
                    "UPDATE sys_config SET w_yydm=?, yymc=?, jykksdm=?, his_connectbz=?, his_connectlevel=?, " +
                    "tj_connectbz=?, tj_jghcbz=?, ysz_jghcbz=?, ysz_connectbz=?, qtxt_jghcbz=?, websc=?, gdsj=?, his_connect_ybzx=?",
                    nvl(req.getWYydm()), req.getYymc().trim(), nvl(req.getJykksdm()),
                    hisConnBz, hisLevel, tjConnBz, tjJghc, yszJghc, yszConnBz, qtxtJghc, websc, 
                    req.getGdsj(), hisYbzx
                );
            } else {
                // 新增
                updated = jdbcTemplate.update(
                    "INSERT INTO sys_config (w_yydm, yymc, jykksdm, his_connectbz, his_connectlevel, " +
                    "tj_connectbz, tj_jghcbz, ysz_jghcbz, ysz_connectbz, qtxt_jghcbz, websc, gdsj, his_connect_ybzx) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    nvl(req.getWYydm()), req.getYymc().trim(), nvl(req.getJykksdm()),
                    hisConnBz, hisLevel, tjConnBz, tjJghc, yszJghc, yszConnBz, qtxtJghc, websc, 
                    req.getGdsj(), hisYbzx
                );
            }

            boolean ok = updated > 0;
            result.put("success", ok);
            result.put("message", ok ? "设置成功!" : "设置失败!");
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
    public static class SaveConfigRequest {
        // sys_config.w_yydm
        private String wYydm;
        // sys_config.yymc
        private String yymc;
        // sys_config.jykksdm（旧系统传 pub_ksdm）
        private String jykksdm;

        private Boolean hisConnectbz;
        private Integer hisConnectLevel;
        private Boolean tjConnectbz;
        private Boolean yszConnectbz;

        private Boolean tjJghcbz;
        private Boolean yszJghcbz;
        private Boolean qtxtJghcbz;

        private Boolean websc;
        private Integer gdsj;
        private Boolean hisConnectYbzx;
    }
}


