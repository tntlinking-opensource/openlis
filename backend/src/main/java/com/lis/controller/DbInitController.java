package com.lis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库初始化 - 通过API执行
 */
@RestController
@RequestMapping("/api/debug")
public class DbInitController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/init-all")
    public ResponseEntity<Map<String, Object>> initAll() {
        Map<String, Object> resp = new HashMap<>();
        try {
            // 1. sys_config
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS sys_config (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, w_yydm VARCHAR(50), yymc VARCHAR(200), " +
                "jykksdm VARCHAR(50), his_connectbz TINYINT DEFAULT 0, his_connectlevel INT DEFAULT 0, " +
                "tj_connectbz TINYINT DEFAULT 0, tj_jghcbz TINYINT DEFAULT 0, ysz_jghcbz TINYINT DEFAULT 0, " +
                "ysz_connectbz TINYINT DEFAULT 0, qtxt_jghcbz TINYINT DEFAULT 0, websc TINYINT DEFAULT 0, " +
                "gdsj INT DEFAULT 10, his_connect_ybzx TINYINT DEFAULT 0)");
            jdbcTemplate.execute("INSERT IGNORE INTO sys_config (yymc) VALUES ('Test Hospital')");

            // 2. sys_kssz
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS sys_kssz (" +
                "ksid INT PRIMARY KEY AUTO_INCREMENT, ksdm VARCHAR(50), ksmc VARCHAR(100), " +
                "pym VARCHAR(50), ksxz VARCHAR(50), zxbz TINYINT DEFAULT 1, sybz TINYINT DEFAULT 1)");
            jdbcTemplate.execute("INSERT IGNORE INTO sys_kssz (ksdm, ksmc, pym) VALUES ('001', 'Lab Dept', 'jy')");
            jdbcTemplate.execute("INSERT IGNORE INTO sys_kssz (ksdm, ksmc, pym) VALUES ('002', 'Internal Med', 'nk')");

            // 3. sys_gzzd
            jdbcTemplate.execute("DROP TABLE IF EXISTS sys_gzzd");
            jdbcTemplate.execute("CREATE TABLE sys_gzzd (" +
                "gzid INT PRIMARY KEY AUTO_INCREMENT, gzdm VARCHAR(50), gzmc VARCHAR(100), " +
                "pym VARCHAR(50), gzzlx INT DEFAULT 1, xh INT DEFAULT 1, sybz TINYINT DEFAULT 1, zxbz TINYINT DEFAULT 1)");
            jdbcTemplate.execute("INSERT IGNORE INTO sys_gzzd (gzdm, gzmc, pym) VALUES ('01', 'Biochem Group', 'shz')");

            // 4. sys_brlb  
            jdbcTemplate.execute("DROP TABLE IF EXISTS sys_brlb");
            jdbcTemplate.execute("CREATE TABLE sys_brlb (" +
                "bm INT PRIMARY KEY, bmsm VARCHAR(100), pym VARCHAR(50), xh INT DEFAULT 1, tybz TINYINT DEFAULT 0)");
            jdbcTemplate.execute("INSERT IGNORE INTO sys_brlb (bm, bmsm, pym, xh) VALUES (1, 'Outpatient', 'mzbr', 1)");
            jdbcTemplate.execute("INSERT IGNORE INTO sys_brlb (bm, bmsm, pym, xh) VALUES (2, 'Inpatient', 'zybr', 2)");
            jdbcTemplate.execute("INSERT IGNORE INTO sys_brlb (bm, bmsm, pym, xh) VALUES (3, 'Health Check', 'tjry', 3)");

            // 5. sys_czydm
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS sys_czydm (" +
                "czydm VARCHAR(50) PRIMARY KEY, czyxm VARCHAR(100), pym VARCHAR(50), ksdm VARCHAR(50), " +
                "czymm VARCHAR(50), sybz TINYINT DEFAULT 1, glybz TINYINT DEFAULT 0, gzzdm VARCHAR(50))");
            jdbcTemplate.execute("INSERT IGNORE INTO sys_czydm (czydm, czyxm, pym, ksdm, czymm, glybz) VALUES ('admin', 'Admin', 'gly', '001', '1', 1)");

            // 6. sys_bbzl
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS sys_bbzl (" +
                "bmid INT PRIMARY KEY AUTO_INCREMENT, bmsm VARCHAR(100), bm VARCHAR(20), pym VARCHAR(50))");
            jdbcTemplate.execute("INSERT IGNORE INTO sys_bbzl (bmsm, bm, pym) VALUES ('Serum', '1', 'xq')");

            // 7. sys_xmckz
            jdbcTemplate.execute("DROP TABLE IF EXISTS sys_xmckz");
            jdbcTemplate.execute("CREATE TABLE sys_xmckz (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, xmid INT, xmdm VARCHAR(50), ckz VARCHAR(100), " +
                "ckzgx VARCHAR(50), ckzdx VARCHAR(50), bjzgx VARCHAR(50), bjzdx VARCHAR(50))");
            jdbcTemplate.execute("INSERT INTO sys_xmckz (xmid, xmdm, ckz, ckzgx, ckzdx) VALUES (1, 'GLU', '4.1-5.9', '5.9', '4.1')");
            jdbcTemplate.execute("INSERT INTO sys_xmckz (xmid, xmdm, ckz, ckzgx, ckzdx) VALUES (2, 'BUN', '2.9-8.2', '8.2', '2.9')");
            jdbcTemplate.execute("INSERT INTO sys_xmckz (xmid, xmdm, ckz, ckzgx, ckzdx) VALUES (7, 'ALT', '0-40', '40', '0')");

            // 8. bgxt_xmzh_zb
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS bgxt_xmzh_zb (" +
                "zhid INT PRIMARY KEY AUTO_INCREMENT, zhmc VARCHAR(100), pym VARCHAR(50), " +
                "zhlx VARCHAR(20), zxbz TINYINT DEFAULT 1, sybz TINYINT DEFAULT 1)");
            jdbcTemplate.execute("INSERT IGNORE INTO bgxt_xmzh_zb (zhmc, pym, zhlx) VALUES ('Liver Function', 'ggd', 'Biochem')");

            // 9. bgxt_xmzh_mx
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS bgxt_xmzh_mx (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, zhid INT, xmdm VARCHAR(50), " +
                "xmzwmc VARCHAR(100), xmdw VARCHAR(20), xh INT DEFAULT 0)");
            jdbcTemplate.execute("INSERT INTO bgxt_xmzh_mx (zhid, xmdm, xmzwmc, xmdw, xh) VALUES (1, 'ALT', 'ALT', 'U/L', 1)");
            jdbcTemplate.execute("INSERT INTO bgxt_xmzh_mx (zhid, xmdm, xmzwmc, xmdw, xh) VALUES (1, 'AST', 'AST', 'U/L', 2)");

            // 10. sys_zkpd
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS sys_zkpd (" +
                "zkpid INT PRIMARY KEY AUTO_INCREMENT, zkpmc VARCHAR(100), pym VARCHAR(50), " +
                "zkplx VARCHAR(20), bjzl VARCHAR(20), bjzh VARCHAR(20), sccj VARCHAR(100))");
            jdbcTemplate.execute("INSERT IGNORE INTO sys_zkpd (zkpmc, pym, zkplx, bjzl, bjzh) VALUES ('Glucose QC', 'xtzkp', 'Biochem', '5.5', '6.5')");

            // 11. zk_nyzkxm
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS zk_nyzkxm (" +
                "zkxmid INT PRIMARY KEY AUTO_INCREMENT, zkpid INT, xmid INT, bz VARCHAR(50), " +
                "bzc VARCHAR(50), zkdz VARCHAR(50), zkgz VARCHAR(50), dx_lx INT DEFAULT 0)");
            jdbcTemplate.execute("INSERT IGNORE INTO zk_nyzkxm (zkpid, xmid, bz, bzc, zkdz, zkgz) VALUES (1, 1, '5.2', '0.5', '4.2', '6.2')");

            // 12. zk_nyzkjg
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS zk_nyzkjg (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, zkxmid INT, yssj VARCHAR(100), yhsj VARCHAR(100), " +
                "jssj DATE, sybz TINYINT DEFAULT 1, skbz TINYINT DEFAULT 0)");
            jdbcTemplate.execute("INSERT IGNORE INTO zk_nyzkjg (zkxmid, yssj, yhsj, jssj, sybz, skbz) VALUES (1, '5.1', '5.1', '2026-03-15', 1, 0)");
            jdbcTemplate.execute("INSERT IGNORE INTO zk_nyzkjg (zkxmid, yssj, yhsj, jssj, sybz, skbz) VALUES (1, '5.3', '5.3', '2026-03-16', 1, 0)");
            jdbcTemplate.execute("INSERT IGNORE INTO zk_nyzkjg (zkxmid, yssj, yhsj, jssj, sybz, skbz) VALUES (1, '6.8', '6.8', '2026-03-18', 1, 1)");

            // 13. bgxt_tsbbsz
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS bgxt_tsbbsz (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, mkid INT, xmid INT, mksm VARCHAR(200))");

            resp.put("success", true);
            resp.put("message", "Database init complete!");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}
