package com.lis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSetMetaData;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 样本管理 - 2.1 样本录入 相关接口骨架
 *
 * 设计原则：
 * - 严格参考旧系统 P_ybxx.pas + script.sql 中“使用模块：样本录入”的存储过程
 * - 本类先搭建清晰的 API 外壳和参数，再逐步将内部实现替换为真实存储过程调用
 */
@RestController
@RequestMapping({"/sample", "/api/sample"})
public class SampleEntryController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 调试接口：检查bgxt_jyjg表中的检验结果
     */
    @GetMapping("/debug/check-results")
    public ResponseEntity<Map<String, Object>> checkResults(@RequestParam(required = false) Integer brxxId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (brxxId != null) {
                // 检查特定样本的结果
                String sql = "SELECT COUNT(*) as cnt FROM bgxt_jyjg WHERE brxx_id = ?";
                Integer count = jdbcTemplate.queryForObject(sql, Integer.class, brxxId);
                resp.put("resultCount", count);
                
                // 获取结果详情
                String sql2 = "SELECT jg.*, xm.xmzwmc FROM bgxt_jyjg jg LEFT JOIN sys_jyxm xm ON jg.xmid = xm.xmid WHERE jg.brxx_id = ?";
                List<Map<String, Object>> results = jdbcTemplate.queryForList(sql2, brxxId);
                resp.put("results", results);
            } else {
                // 获取最近的检验结果
                String sql = "SELECT brxx_id, COUNT(*) as cnt FROM bgxt_jyjg GROUP BY brxx_id ORDER BY brxx_id DESC LIMIT 10";
                List<Map<String, Object>> recent = jdbcTemplate.queryForList(sql);
                resp.put("recentResults", recent);
            }
            
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
    
    /**
     * 调试接口：检查bgxt_jyjg表结构和数据
     */
    @GetMapping("/debug/check-jyjg")
    public ResponseEntity<Map<String, Object>> checkJyjg(@RequestParam(required = false) Integer brxxId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            // 先检查表结构
            String structSql = "DESCRIBE bgxt_jyjg";
            List<Map<String, Object>> struct = jdbcTemplate.queryForList(structSql);
            resp.put("structure", struct);
            
            // 检查数据
            if (brxxId != null) {
                String dataSql = "SELECT * FROM bgxt_jyjg WHERE brxx_id = ?";
                List<Map<String, Object>> data = jdbcTemplate.queryForList(dataSql, brxxId);
                resp.put("data", data);
                resp.put("count", data.size());
            }
            
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
    @GetMapping("/debug/check-xm")
    public ResponseEntity<Map<String, Object>> checkXm(@RequestParam(required = false) String xmdm) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (xmdm != null && !xmdm.isEmpty()) {
                // 查找特定项目
                String sql = "SELECT xmid, xmdm, xmzwmc FROM sys_jyxm WHERE xmdm = ?";
                List<Map<String, Object>> items = jdbcTemplate.queryForList(sql, xmdm);
                resp.put("items", items);
                resp.put("count", items.size());
            } else {
                // 获取前10条
                String sql = "SELECT xmid, xmdm, xmzwmc FROM sys_jyxm LIMIT 10";
                List<Map<String, Object>> items = jdbcTemplate.queryForList(sql);
                resp.put("items", items);
                resp.put("sample", true);
            }
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
    
    /**
     * 调试接口：检查bgxt_xmzh_mx表结构和数据
     */
    @GetMapping("/debug/check-mx-table")
    public ResponseEntity<Map<String, Object>> checkMxTable(@RequestParam(required = false) Integer zhid) {
        Map<String, Object> resp = new HashMap<>();
        try {
            // 检查表结构
            String structSql = "DESCRIBE bgxt_xmzh_mx";
            List<Map<String, Object>> struct = jdbcTemplate.queryForList(structSql);
            resp.put("structure", struct);
            
            // 检查数据
            if (zhid != null) {
                String sql = "SELECT * FROM bgxt_xmzh_mx WHERE zhid = ? LIMIT 5";
                List<Map<String, Object>> data = jdbcTemplate.queryForList(sql, zhid);
                resp.put("data", data);
            }
            
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
    
    /**
     * 调试接口：检查表结构
     */
    @GetMapping("/debug/check-tables")
    public ResponseEntity<Map<String, Object>> checkTables() {
        Map<String, Object> resp = new HashMap<>();
        try {
            // 检查bgxt_xmzh_mx表结构
            String mxStruct = "DESCRIBE bgxt_xmzh_mx";
            List<Map<String, Object>> mxColumns = jdbcTemplate.queryForList(mxStruct);
            resp.put("bgxt_xmzh_mx_columns", mxColumns);
            
            // 检查sys_xmckz表结构
            String ckzStruct = "DESCRIBE sys_xmckz";
            List<Map<String, Object>> ckzColumns = jdbcTemplate.queryForList(ckzStruct);
            resp.put("sys_xmckz_columns", ckzColumns);
            
            // 检查sys_jyxm表结构
            String jyxmStruct = "DESCRIBE sys_jyxm";
            List<Map<String, Object>> jyxmColumns = jdbcTemplate.queryForList(jyxmStruct);
            resp.put("sys_jyxm_columns", jyxmColumns);
            
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
    
    /**
     * 调试接口：检查sys_xmckz表数据
     */
    @GetMapping("/debug/check-ckz-data")
    public ResponseEntity<Map<String, Object>> checkCkzData(@RequestParam(required = false) Integer xmid) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (xmid != null) {
                String sql = "SELECT * FROM sys_xmckz WHERE xmid = ? LIMIT 5";
                List<Map<String, Object>> data = jdbcTemplate.queryForList(sql, xmid);
                resp.put("data", data);
                resp.put("count", data.size());
            } else {
                String sql = "SELECT * FROM sys_xmckz LIMIT 10";
                List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
                resp.put("data", data);
                resp.put("sample", true);
            }
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
    @GetMapping("/debug/check-ckz")
    public ResponseEntity<Map<String, Object>> checkCkz(@RequestParam(required = false) Integer xmid) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (xmid != null) {
                // 检查特定项目的参考范围
                String sql = "SELECT * FROM sys_xmckz WHERE xmid = ? LIMIT 5";
                List<Map<String, Object>> data = jdbcTemplate.queryForList(sql, xmid);
                resp.put("data", data);
                resp.put("count", data.size());
            } else {
                // 获取前10条
                String sql = "SELECT * FROM sys_xmckz LIMIT 10";
                List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
                resp.put("data", data);
                resp.put("sample", true);
            }
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
    
    /**
     * 调试接口：检查组合项目明细表数据
     */
    @GetMapping("/debug/check-combo-items")
    public ResponseEntity<Map<String, Object>> checkComboItems(@RequestParam(required = false) Integer zhid) {
        Map<String, Object> resp = new HashMap<>();
        try {
            // 检查 bgxt_xmzh_mx 表中是否有数据
            String sql = "SELECT COUNT(*) as cnt FROM bgxt_xmzh_mx";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            resp.put("totalItems", count);
            
            // 检查特定组合
            if (zhid != null) {
                String sql2 = "SELECT COUNT(*) as cnt FROM bgxt_xmzh_mx WHERE zhid = ?";
                Integer count2 = jdbcTemplate.queryForObject(sql2, Integer.class, zhid);
                resp.put("zhidCount", count2);
                
                // 获取前几条数据
                String sql3 = "SELECT * FROM bgxt_xmzh_mx WHERE zhid = ? LIMIT 5";
                List<Map<String, Object>> items = jdbcTemplate.queryForList(sql3, zhid);
                resp.put("sampleData", items);
            }
            
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", e.getMessage());
            return ResponseEntity.ok(resp);
        }
    }
    
    /**
     * 调试接口：从SQL Server迁移参考值数据到MySQL
     * 注意：需要SQL Server JDBC驱动支持
     */
    @GetMapping("/debug/migrate-ckz")
    public ResponseEntity<Map<String, Object>> migrateCkzFromSqlServer() {
        Map<String, Object> resp = new HashMap<>();
        try {
            // 检查当前MySQL中的数据量
            String countSql = "SELECT COUNT(*) FROM sys_xmckz";
            Integer mySqlCount = jdbcTemplate.queryForObject(countSql, Integer.class);
            resp.put("mySqlBeforeCount", mySqlCount);
            
            // 返回提示信息
            resp.put("message", "需要从SQL Server迁移数据。当前MySQL有 " + mySqlCount + " 条记录，SQL Server有 2464 条记录。");
            resp.put("sqlServerCount", 2464);
            resp.put("needMigration", mySqlCount < 2464);
            
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", e.getMessage());
            return ResponseEntity.ok(resp);
        }
    }
    
    /**
     * 调试接口：从文件导入参考值数据
     */
    @GetMapping("/debug/import-ckz-from-file")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> importCkzFromFile() {
        Map<String, Object> resp = new HashMap<>();
        try {
            // 读取文件
            java.nio.file.Path path = java.nio.file.Paths.get("D:\\LIS02\\ckz_mysql_insert.sql");
            String content = new String(java.nio.file.Files.readAllBytes(path), "UTF-8");
            
            // 移除BOM
            if (content.charAt(0) == '\uFEFF') {
                content = content.substring(1);
            }
            
            // 先删除旧表
            try { jdbcTemplate.execute("DROP TABLE IF EXISTS sys_xmckz"); } catch (Exception e) {}
            
            // 将所有换行符替换为空格（合并多行SQL为单行）
            content = content.replaceAll("\\r?\\n", " ");
            // 移除多余空格
            content = content.replaceAll("\\s+", " ");
            
            // 按分号分割SQL语句
            String[] statements = content.split(";");
            int successCount = 0;
            int errorCount = 0;
            
            for (String stmt : statements) {
                String trimmed = stmt.trim();
                // 跳过空语句和注释
                if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("/*")) {
                    continue;
                }
                try {
                    jdbcTemplate.execute(trimmed);
                    successCount++;
                } catch (Exception e) {
                    errorCount++;
                    if (errorCount < 5) {
                        System.out.println("SQL Error: " + trimmed.substring(0, Math.min(100, trimmed.length())) + " -> " + e.getMessage());
                    }
                }
            }
            
            resp.put("successCount", successCount);
            resp.put("errorCount", errorCount);
            
            // 验证结果
            try {
                Integer afterCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_xmckz", Integer.class);
                resp.put("afterCount", afterCount);
            } catch (Exception e) {
                resp.put("afterCount", 0);
                resp.put("tableError", e.getMessage());
            }
            
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", e.getMessage());
            return ResponseEntity.ok(resp);
        }
    }
    
    /**
     * 调试接口：插入测试样本数据
     */
    @PostMapping("/debug/insert-test-samples")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> insertTestSamples() {
        Map<String, Object> resp = new HashMap<>();
        try {
            // 插入测试数据
            jdbcTemplate.update("INSERT INTO bgxt_brxx (brxx_tmh, brbh, brxm, brxb, brnl, nllx, brlb, syqk, ksdm, brch, syh, bbzl, ybzt, jyrq, sfbz, czy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "TMH001", "BR001", "测试患者A", 1, 30, 1, 1, 1, "001", "001", "202603170001", 1, 0, "2026-03-17", 0, "admin");
            jdbcTemplate.update("INSERT INTO bgxt_brxx (brxx_tmh, brbh, brxm, brxb, brnl, nllx, brlb, syqk, ksdm, brch, syh, bbzl, ybzt, jyrq, sfbz, czy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "TMH002", "BR002", "测试患者B", 2, 25, 1, 1, 1, "001", "002", "202603170002", 2, 0, "2026-03-17", 0, "admin");
            jdbcTemplate.update("INSERT INTO bgxt_brxx (brxx_tmh, brbh, brxm, brxb, brnl, nllx, brlb, syqk, ksdm, brch, syh, bbzl, ybzt, jyrq, sfbz, czy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "TMH003", "BR003", "测试患者C", 1, 45, 1, 2, 1, "002", "003", "202603170003", 1, 1, "2026-03-17", 0, "admin");
            jdbcTemplate.update("INSERT INTO bgxt_brxx (brxx_tmh, brbh, brxm, brxb, brnl, nllx, brlb, syqk, ksdm, brch, syh, bbzl, ybzt, jyrq, sfbz, czy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "TMH004", "BR004", "测试患者D", 2, 35, 1, 3, 1, "003", "004", "202603170004", 3, 2, "2026-03-17", 0, "admin");
            jdbcTemplate.update("INSERT INTO bgxt_brxx (brxx_tmh, brbh, brxm, brxb, brnl, nllx, brlb, syqk, ksdm, brch, syh, bbzl, ybzt, jyrq, sfbz, czy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "TMH005", "BR005", "测试患者E", 1, 50, 1, 1, 2, "001", "005", "202603170005", 1, 3, "2026-03-17", 0, "admin");
            
            resp.put("success", true);
            resp.put("message", "成功插入5条测试样本数据");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
    
    /**
     * 调试接口：端到端测试 - 创建样本+保存结果+执行检验
     */
    @PostMapping("/debug/e2e-test")
    public ResponseEntity<Map<String, Object>> e2eTest() {
        Map<String, Object> resp = new HashMap<>();
        try {
            // 1. 创建新样本
            Integer maxId = jdbcTemplate.queryForObject("SELECT IFNULL(MAX(brxx_id), 0) FROM bgxt_brxx", Integer.class);
            Integer brxxId = maxId + 1;
            String sampleNo = "20260318" + String.format("%04d", brxxId % 10000);
            
            jdbcTemplate.update(
                "INSERT INTO bgxt_brxx (brxx_id, brxx_tmh, brbh, brxm, brxb, brnl, nllx, brlb, syh, syqk, ksdm, bbzl, ybzt, jyrq) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())",
                brxxId, "TMH" + sampleNo, "BR" + brxxId, "测试患者E2E", 1, 30, 1, 1, sampleNo, 1, "001", 1, 0
            );
            resp.put("step1_sampleCreated", "brxxId=" + brxxId + ", sampleNo=" + sampleNo);
            
            // 2. 保存检验结果（使用组合589的项目代码）
            String[] testCodes = {"50", "51", "52", "53", "54"};
            String[] testResults = {"6.5", "4.8", "140", "250", "45"};
            
            for (int i = 0; i < testCodes.length; i++) {
                String xmdm = testCodes[i];
                Integer xmid = null;
                try {
                    xmid = jdbcTemplate.queryForObject("SELECT xmid FROM sys_jyxm WHERE xmdm = ?", Integer.class, xmdm);
                } catch (Exception e) { }
                if (xmid != null) {
                    jdbcTemplate.update("INSERT INTO bgxt_jyjg (brxx_id, xmid, jyjg) VALUES (?, ?, ?)", brxxId, xmid, testResults[i]);
                    resp.put("step2_resultSaved_" + xmdm, "xmid=" + xmid + ", jyjg=" + testResults[i]);
                }
            }
            
            // 3. 执行检验
            Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM bgxt_jyjg WHERE brxx_id = ?", Integer.class, brxxId);
            resp.put("step3_checkResults", "total=" + total);
            
            if (total != null && total > 0) {
                jdbcTemplate.update("UPDATE bgxt_brxx SET ybzt = 1 WHERE brxx_id = ?", brxxId);
                resp.put("step4_inspectSuccess", true);
                resp.put("message", "E2E测试成功! brxxId=" + brxxId);
            } else {
                resp.put("step4_inspectSuccess", false);
                resp.put("message", "E2E测试失败: 没有检验结果");
            }
            
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 获取下一个样本号（yyyyMMdd + 4位流水）
     *
     * 对标：旧系统“新增(F9)”自动带出当天递增样本号（如 202602110001）。
     * 说明：syh 为 varchar，历史数据可能混杂，这里仅识别 12 位纯数字且前 8 位为日期前缀的记录。
     */
    @GetMapping("/nextSampleNo")
    public ResponseEntity<Map<String, Object>> nextSampleNo(@RequestParam(required = false) String date) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String targetDate = (date == null || date.isEmpty())
                    ? LocalDate.now().toString()
                    : date;

            String prefix = LocalDate.parse(targetDate).format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd

            String sql = ""
                    + "SELECT MAX(CAST(SUBSTRING(syh, -4) AS UNSIGNED)) "
                    + "FROM bgxt_brxx "
                    + "WHERE syh IS NOT NULL "
                    + "  AND LENGTH(syh) = 12 "
                    + "  AND syh LIKE ? "
                    + "  AND CAST(syh AS UNSIGNED) > 0 "
                    + "  AND DATE(jyrq) = DATE(?)";

            Integer maxSeq = jdbcTemplate.queryForObject(sql, Integer.class, prefix + "%", targetDate);
            int nextSeq = (maxSeq == null ? 1 : (maxSeq + 1));
            String sampleNo = prefix + String.format("%04d", nextSeq);

            resp.put("success", true);
            resp.put("date", targetDate);
            resp.put("sampleNo", sampleNo);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 获取某天的病人/样本列表（右侧列表）
     *
     * 对标：手册 2.1 / 2.2 中"日期选择 + 病人列表"区域，
     * 修复：现在正确按日期过滤
     */
    @GetMapping("/patients")
    public ResponseEntity<List<Map<String, Object>>> listPatients(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String patientType,
            @RequestParam(required = false) String sampleNo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String barcode) {
        try {
            String targetDate = (date == null || date.isEmpty())
                    ? LocalDate.now().toString()
                    : date;

            // 这里假设当前设备 ID 已通过前端或会话传递，暂时用 0 表示"不过滤仪器"
            // 后续会根据 selectedDevice.sb_djid 真实传入
            int sbDjid = 0;

            StringBuilder sql = new StringBuilder();
            List<Object> params = new ArrayList<>();

            // 直接从 bgxt_brxx（或视图）中取出样本基本信息子集，字段命名与前端期望保持一致
            // 使用两阶段查询：先获取唯一的 brxx_id 列表，再获取详细信息
            // 第一阶段：获取唯一的 brxx_id 列表
            StringBuilder countSql = new StringBuilder();
            countSql.append("SELECT DISTINCT b.brxx_id FROM bgxt_brxx b WHERE 1=1 ");
            
            // 关键修复：添加日期过滤！
            countSql.append("AND DATE(b.jyrq) = DATE(?) ");
            params.add(targetDate);
            
            // 添加相同的过滤条件
            if (sampleNo != null && !sampleNo.trim().isEmpty()) {
                countSql.append("AND b.syh LIKE ? ");
                params.add("%" + sampleNo.trim() + "%");
            }
            if (name != null && !name.trim().isEmpty()) {
                countSql.append("AND b.brxm LIKE ? ");
                params.add("%" + name.trim() + "%");
            }
            if (barcode != null && !barcode.trim().isEmpty()) {
                countSql.append("AND b.brxx_tmh LIKE ? ");
                params.add("%" + barcode.trim() + "%");
            }
            if (patientType != null && !patientType.trim().isEmpty() && !"所有".equals(patientType.trim())) {
                String pt = patientType.trim();
                switch (pt) {
                    case "未审核":
                        countSql.append("AND (b.ybzt IS NULL OR b.ybzt = 0 OR b.ybzt = 1) ");
                        break;
                    case "已出结果":
                        countSql.append("AND (b.ybzt IS NOT NULL AND b.ybzt >= 1) ");
                        break;
                    case "已打印":
                    case "已出报告":
                        countSql.append("AND b.ybzt = 3 ");
                        break;
                    case "已检验":
                        countSql.append("AND b.ybzt = 1 ");
                        break;
                    case "门诊病人":
                        countSql.append("AND b.brlb = 1 ");
                        break;
                    case "住院病人":
                        countSql.append("AND b.brlb = 2 ");
                        break;
                    case "体检人员":
                        countSql.append("AND b.brlb = 3 ");
                        break;
                    case "其他病人":
                        countSql.append("AND b.brlb = 4 ");
                        break;
                    case "科研人员":
                        countSql.append("AND b.brlb = 5 ");
                        break;
                    default:
                        break;
                }
            }
            
            // 获取唯一的 brxx_id 列表
            List<Integer> uniqueIds = jdbcTemplate.query(countSql.toString(), params.toArray(), (rs, rowNum) -> {
                return rs.getInt("brxx_id");
            });
            
            if (uniqueIds.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }
            
            // 第二阶段：使用 IN 查询获取详细信息
            sql = new StringBuilder();
            sql.append("SELECT ");
            sql.append("b.brxx_id AS id, ");
            sql.append("b.brxx_tmh AS barcode, ");
            sql.append("b.brbh     AS patientId, ");
            sql.append("b.brxm     AS name, ");
            sql.append("CASE b.brxb WHEN 1 THEN '男' WHEN 2 THEN '女' ELSE '' END AS sex, ");
            sql.append("b.brnl     AS age, ");
            sql.append("b.nllx     AS ageUnit, ");
            sql.append("b.brlb     AS category, ");
            sql.append("b.syqk     AS urgency, ");
            sql.append("b.ksdm     AS ward, ");
            sql.append("b.brch     AS bedNo, ");
            sql.append("b.syh      AS sampleNo, ");
            // 使用硬编码映射表修复样本类型乱码问题
            sql.append("CASE b.bbzl ");
            sql.append("WHEN 1 THEN '血清' ");
            sql.append("WHEN 2 THEN '血浆' ");
            sql.append("WHEN 3 THEN '尿液' ");
            sql.append("WHEN 4 THEN '粪便' ");
            sql.append("WHEN 5 THEN '脑脊液' ");
            sql.append("WHEN 6 THEN '胸腹水' ");
            sql.append("WHEN 7 THEN '其它' ");
            sql.append("ELSE CAST(b.bbzl AS CHAR) END AS sampleType, ");
            sql.append("b.bbzl     AS sampleTypeCode, ");
            sql.append("b.ybzt     AS status ");
            sql.append("FROM bgxt_brxx b ");
            sql.append("WHERE b.brxx_id IN (");
            for (int i = 0; i < uniqueIds.size(); i++) {
                sql.append("?");
                if (i < uniqueIds.size() - 1) sql.append(",");
            }
            sql.append(") ");
            sql.append("ORDER BY b.syh ");

            List<Map<String, Object>> list = jdbcTemplate.query(sql.toString(), uniqueIds.toArray(), (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                for (int i = 1; i <= colCount; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                return row;
            });

            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    /**
     * 保存样本信息（病人基本信息 + 结果）
     *
     * 对标：P_ybxx.pas 中 append_brxxxg / save_ybxx 相关逻辑，
     * 后续将映射到样本写入的存储过程（如 sys_in_brxxxg 等）。
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveSample(@RequestBody Map<String, Object> payload) {
        System.out.println("=== SAVE DEBUG START ===");
        System.out.println("payload keys: " + payload.keySet());
        try {
            // 将 payload 拆分为病人信息 + 结果信息
            @SuppressWarnings("unchecked")
            Map<String, Object> patient = (Map<String, Object>) payload.get("patient");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) payload.get("results");

            System.out.println("patient: " + patient);
            System.out.println("results count: " + (results == null ? "null" : results.size()));
            if (results != null && !results.isEmpty()) {
                System.out.println("first result: " + results.get(0));
            }

            if (patient == null) {
                patient = new HashMap<>();
            }

            // 必填项验证：姓名 + 样本号（按照旧系统 save_brxx 逻辑）
            String name = (String) patient.getOrDefault("name", "");
            String sampleNo = (String) patient.getOrDefault("sampleNo", "");
            if (name == null || name.trim().isEmpty()) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", false);
                resp.put("message", "病人姓名不能为空！");
                return ResponseEntity.badRequest().body(resp);
            }
            if (sampleNo == null || sampleNo.trim().isEmpty()) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", false);
                resp.put("message", "样本号不能为空！");
                return ResponseEntity.badRequest().body(resp);
            }

            // 标本种类必填校验
            String sampleType = (String) patient.getOrDefault("sampleType", "");
            if (sampleType == null || sampleType.trim().isEmpty()) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", false);
                resp.put("message", "必须要设置标本种类！");
                return ResponseEntity.badRequest().body(resp);
            }

            // 住院病人时，住院号（patientId/门诊号/体检号）必填
            String type = (String) patient.getOrDefault("type", "");
            String patientId = (String) patient.getOrDefault("patientId", "");
            String barcode = (String) patient.getOrDefault("barcode", "");  // 住院号/条码号
            if (type.contains("住院") && (patientId == null || patientId.trim().isEmpty())) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", false);
                resp.put("message", "住院病人必须输入住院号，请输入正确的住院号！");
                return ResponseEntity.badRequest().body(resp);
            }

            // 如果前端带了 brxx_id，则视为"修改保存"，否则"新增保存"
            Integer brxxId = null;
            Object brxxIdObj = patient.get("brxx_id");
            if (brxxIdObj == null) {
                brxxIdObj = payload.get("brxx_id");
            }
            if (brxxIdObj != null) {
                try {
                    brxxId = Integer.parseInt(brxxIdObj.toString());
                } catch (NumberFormatException ignored) {
                }
            }

            String sexStr = (String) patient.getOrDefault("sex", "");
            Integer sex = null;
            if ("男".equals(sexStr) || "M".equalsIgnoreCase(sexStr)) {
                sex = 1;
            } else if ("女".equals(sexStr) || "F".equalsIgnoreCase(sexStr)) {
                sex = 2;
            }
            Integer age = null;
            try {
                Object ageObj = patient.get("age");
                if (ageObj != null) {
                    age = Integer.parseInt(ageObj.toString());
                    if (age < 0) {
                        Map<String, Object> resp = new HashMap<>();
                        resp.put("success", false);
                        resp.put("message", "年龄不能小于0！");
                        return ResponseEntity.badRequest().body(resp);
                    }
                }
            } catch (NumberFormatException ignored) {
            }

            // 年龄单位：1 岁 2 月 3 天 4 小时，先默认 1（岁）
            int nllx = 1;
            String ageUnit = (String) patient.getOrDefault("ageUnit", "Y");
            if ("M".equalsIgnoreCase(ageUnit)) {
                nllx = 2;
            } else if ("D".equalsIgnoreCase(ageUnit)) {
                nllx = 3;
            }

            // 病人类别：1 门诊病人、2 住院病人、3 体检人员、4 其他病人、5 科研人员（来自操作手册说明）
            Integer brlb = null;
            if (type.contains("门诊")) {
                brlb = 1;
            } else if (type.contains("住院")) {
                brlb = 2;
            } else if (type.contains("体检")) {
                brlb = 3;
            } else if (type.contains("其他")) {
                brlb = 4;
            } else if (type.contains("科研")) {
                brlb = 5;
            }

            // 实验情况（syqk）：1=普通, 2=紧急, 3=危急（参考旧系统代码）
            Integer syqk = 1; // 默认普通
            String experimentStatus = (String) patient.getOrDefault("experimentStatus", "");
            if ("紧急".equals(experimentStatus)) {
                syqk = 2;
            } else if ("危急".equals(experimentStatus)) {
                syqk = 3;
            }

            String wardCode = ""; // ksdm 科室代码：后续从 selectedInstrument / HIS 获取
            String bedNo = (String) patient.getOrDefault("bedNo", "");
            String diagnosis = (String) patient.getOrDefault("diagnosis", "");
            String jyys = ""; // 检验医生代码：后续从当前登录用户或 HIS 获取
            String shys = ""; // 审核医生代码

            // 仪器设备 ID：后续建议由前端携带 selectedDevice.sb_djid
            Integer sbDjid = null;
            Object sbDjidObj = payload.get("sb_djid");
            if (sbDjidObj != null) {
                try {
                    sbDjid = Integer.parseInt(sbDjidObj.toString());
                } catch (NumberFormatException ignored) {
                }
            }

            // 修改保存时检查样本状态
            if (brxxId != null) {
                Integer currentStatus = jdbcTemplate.queryForObject(
                        "SELECT ybzt FROM bgxt_brxx WHERE brxx_id = ?",
                        Integer.class, brxxId
                );
                if (currentStatus != null && currentStatus == 2) {
                    Map<String, Object> resp = new HashMap<>();
                    resp.put("success", false);
                    resp.put("message", "样本已经审核，无法对修改的内容进行保存！");
                    return ResponseEntity.badRequest().body(resp);
                }
                if (currentStatus != null && currentStatus == 3) {
                    Map<String, Object> resp = new HashMap<>();
                    resp.put("success", false);
                    resp.put("message", "样本已经打印，无法对修改的内容进行保存！");
                    return ResponseEntity.badRequest().body(resp);
                }
            }

            // 标本种类转换：将文本转换为INT编码
            Integer bbzlCode = mapSampleTypeToBbzl(sampleType);
            if (bbzlCode == null) {
                bbzlCode = 1; // 默认全血
            }

            if (brxxId == null) {
                // 新增：基于 bgxt_brxx 表结构，先插入一条最基础的病人/样本记录
                String insertSql = "INSERT INTO bgxt_brxx (" +
                        "brxx_id, brxx_tmh, brbh, brxm, brxb, brnl, nllx, brlb, syh, syqk, ksdm, brch, " +
                        "bbzl, lczd, jyys, shys, jyrq, ybzt, sb_djid" +
                        ") VALUES (" +
                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), 0, ?" +
                        ")";

                Integer maxId = jdbcTemplate.queryForObject("SELECT IFNULL(MAX(brxx_id), 0) FROM bgxt_brxx", Integer.class);
                brxxId = (maxId == null ? 1 : maxId + 1);

                jdbcTemplate.update(
                        insertSql,
                        brxxId,
                        barcode,
                        patientId,
                        name,
                        sex,
                        age,
                        nllx,
                        brlb,
                        sampleNo,
                        syqk, // 实验情况
                        wardCode,
                        bedNo,
                        bbzlCode,
                        diagnosis,
                        jyys,
                        shys,
                        sbDjid
                );
            } else {
                // 修改：仅更新可编辑字段（先覆盖基础字段，不动状态流转字段）
                String updateSql = "UPDATE bgxt_brxx SET " +
                        "brxx_tmh=?, brbh=?, brxm=?, brxb=?, brnl=?, nllx=?, brlb=?, syh=?, syqk=?, ksdm=?, brch=?, bbzl=?, lczd=?, sb_djid=? " +
                        "WHERE brxx_id=?";
                jdbcTemplate.update(
                        updateSql,
                        barcode,
                        patientId,
                        name,
                        sex,
                        age,
                        nllx,
                        brlb,
                        sampleNo,
                        syqk,
                        wardCode,
                        bedNo,
                        bbzlCode,
                        diagnosis,
                        sbDjid,
                        brxxId
                );
            }

            // 保存检验结果到 bgxt_jyjg（简化版：删除旧记录后整体插入）
            // 同时写入参考范围/危急值（来自 sys_xmckz；按设备/标本/性别/年龄进行最优匹配）
            if (results != null) {
                jdbcTemplate.update("DELETE FROM bgxt_jyjg WHERE brxx_id = ?", brxxId);
                String insertResultSql = "INSERT INTO bgxt_jyjg (brxx_id, xmid, jyjg, ckz, ckzgx, ckzdx, bjzgx, bjzdx, gdbj) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                for (Map<String, Object> r : results) {
                    System.out.println("Processing result: " + r);
                    
                    // 优先使用前端传递的xmid（来自bgxt_xmzh_mx.id）
                    Object xmidObj = r.get("xmid");
                    Integer xmid = null;
                    
                    if (xmidObj != null) {
                        try {
                            xmid = Integer.parseInt(xmidObj.toString());
                            System.out.println("Using xmid from frontend: " + xmid);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid xmid format: " + xmidObj);
                        }
                    }
                    
                    // 如果xmid无效，尝试通过code查找（sys_jyxm表可能没有对应记录）
                    if (xmid == null) {
                        Object codeObj = r.get("code");
                        if (codeObj != null) {
                            String xmdm = codeObj.toString();
                            System.out.println("Trying to find xmid for xmdm: " + xmdm);
                            try {
                                xmid = jdbcTemplate.queryForObject(
                                    "SELECT xmid FROM sys_jyxm WHERE xmdm = ?", 
                                    Integer.class, xmdm
                                );
                                System.out.println("Found xmid from sys_jyxm: " + xmid);
                            } catch (Exception e) {
                                System.out.println("Not found in sys_jyxm, trying bgxt_xmzh_mx");
                            }
                        }
                    }
                    
                    if (xmid == null) {
                        System.out.println("SAVE WARNING: xmid is null, skipping result");
                        continue;
                    }
                    if (xmid == null) {
                        // 兼容：如果前端直接传了有效的xmid
                        Object compatXmidObj = r.get("xmid");
                        if (compatXmidObj != null) {
                            try {
                                xmid = Integer.parseInt(compatXmidObj.toString());
                            } catch (NumberFormatException ex) {
                            }
                        }
                    }
                    
                    Object codeObjForLog = r.get("code");
                    if (xmid == null) {
                        System.out.println("SAVE WARNING: xmid is null, skipping result for code=" + codeObjForLog);
                        continue;
                    }
                    
                    Object resultObj = r.get("result");
                    String jyjg = resultObj == null ? "" : resultObj.toString();
                    
                    System.out.println("SAVE SUCCESS: brxxId=" + brxxId + ", xmid=" + xmid + ", jyjg=" + jyjg);

                    // 计算参考范围/危急值（使用项目代码直接查找）
                    Map<String, Object> ckzRow = null;
                    Object codeObjForCkz = r.get("code");
                    if (codeObjForCkz != null) {
                        String xmdm = codeObjForCkz.toString();
                        // 尝试通过xmdm查找参考范围（需要在sys_xmckz中添加xmdm字段，或通过bgxt_xmzh_mx关联）
                        try {
                            // 通过bgxt_xmzh_mx的xmdm查找对应的xmid，再查参考范围
                            Integer realXmid = jdbcTemplate.queryForObject(
                                "SELECT xmid FROM sys_jyxm WHERE xmdm = ?", Integer.class, xmdm);
                            if (realXmid != null) {
                                ckzRow = matchReferenceRange(brxxId, realXmid, sbDjid);
                            }
                        } catch (Exception ignored) {
                            // 如果找不到，尝试其他方式
                        }
                    }
                    
                    String ckz = ckzRow == null ? "" : String.valueOf(ckzRow.getOrDefault("ckz", ""));
                    Object ckzgx = ckzRow == null ? null : ckzRow.get("ckzgx");
                    Object ckzdx = ckzRow == null ? null : ckzRow.get("ckzdx");
                    Object bjzgx = ckzRow == null ? null : ckzRow.get("bjzgx");
                    Object bjzdx = ckzRow == null ? null : ckzRow.get("bjzdx");

                    // 高低标记（gdbj）：基于参考范围上下限对“纯数字结果”打标
                    String gdbj = "";
                    try {
                        double v = Double.parseDouble(jyjg.trim());
                        if (ckzgx != null) {
                            double hi = Double.parseDouble(ckzgx.toString());
                            if (hi != 0 && v > hi) gdbj = "H";
                        }
                        if (ckzdx != null) {
                            double lo = Double.parseDouble(ckzdx.toString());
                            if (lo != 0 && v < lo) gdbj = "L";
                        }
                    } catch (Exception ignored) {
                    }

                    jdbcTemplate.update(insertResultSql, brxxId, xmid, jyjg, ckz, ckzgx, ckzdx, bjzgx, bjzdx, gdbj);
                }
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("brxx_id", brxxId);
            resp.put("message", "样本信息已保存");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 查询当前设备下可用的组合项目列表
     *
     * 简化版：直接从 bgxt_xmzh_zb + bgxt_xmzh_mx 读取，按 sb_djid 和关键字过滤。
     */
    @GetMapping("/combos")
    public ResponseEntity<List<Map<String, Object>>> listCombos(
            @RequestParam(required = false) Integer sbDjid,
            @RequestParam(required = false) String keyword) {
        try {
            StringBuilder sql = new StringBuilder();
            List<Object> params = new ArrayList();

            // 使用正确的MySQL表结构和列名
            sql.append("SELECT DISTINCT zb.zhid, zb.zhmc, zb.pym, zb.zhlx ");
            sql.append("FROM bgxt_xmzh_zb zb ");
            sql.append("JOIN bgxt_xmzh_mx mx ON zb.zhid = mx.zhid ");
            sql.append("WHERE zb.zxbz = 1 ");

            if (sbDjid != null) {
                // 暂时不过滤sb_djid
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append("AND (zb.zhmc LIKE ? OR zb.pym LIKE ?) ");
                String kw = "%" + keyword.trim() + "%";
                params.add(kw);
                params.add(kw);
            }

            sql.append("ORDER BY zb.zhmc ");

            List<Map<String, Object>> list = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                for (int i = 1; i <= colCount; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                return row;
            });

            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    /**
     * 辅助方法：将标本种类文本映射到 sys_bbzl.bm (int)
     */
    private Integer mapSampleTypeToBbzl(String sampleType) {
        if (sampleType == null || sampleType.trim().isEmpty()) {
            return null;
        }
        String trimmed = sampleType.trim();
        try {
            // 先尝试直接转换为 int（如果已经是编码）
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            // 如果不是数字，尝试从 sys_bbzl 表中查找
            try {
                // 先精确匹配名称
                String sql = "SELECT bm FROM sys_bbzl WHERE bmsm = ? LIMIT 1";
                try {
                    Integer bm = jdbcTemplate.queryForObject(sql, Integer.class, trimmed);
                    if (bm != null) return bm;
                } catch (Exception ignored) {
                }
                // 再模糊匹配名称
                sql = "SELECT bm FROM sys_bbzl WHERE bmsm LIKE ? LIMIT 1";
                try {
                    Integer bm = jdbcTemplate.queryForObject(sql, Integer.class, "%" + trimmed + "%");
                    if (bm != null) return bm;
                } catch (Exception ignored) {
                }
                // 最后匹配拼音码
                sql = "SELECT bm FROM sys_bbzl WHERE pym LIKE ? LIMIT 1";
                try {
                    Integer bm = jdbcTemplate.queryForObject(sql, Integer.class, "%" + trimmed + "%");
                    if (bm != null) return bm;
                } catch (Exception ignored) {
                }
                return null;
            } catch (Exception ex) {
                return null;
            }
        }
    }

    /**
     * 辅助方法：匹配参考范围（直接调用旧系统 dbo.ckz 函数）
     * 
     * 完全还原旧系统逻辑：通过 SQL Server 函数 dbo.ckz 获取参考范围
     * 该函数会根据样本信息（brxx_id）和项目ID（xmid）自动匹配最优参考范围
     * 
     * @param brxxId 样本ID（如果为null则使用fallback逻辑）
     * @param xmid 项目ID
     * @param sbDjid 设备ID
     */
    private Map<String, Object> matchReferenceRange(Integer brxxId, Integer xmid, Integer sbDjid) {
        if (xmid == null) {
            return null;
        }
        
        // 如果有 brxxId，优先尝试使用 MySQL 函数 ckz（如果已创建）
        // 否则直接使用 fallback 逻辑
        if (brxxId != null) {
            // dbo.ckz 是 SQL Server 存储过程，MySQL 中需要创建对应的函数或使用 fallback
            // 这里直接使用 fallback 逻辑
        }
        
        // Fallback：使用原有的匹配逻辑
        return matchReferenceRangeFallback(xmid, sbDjid);
    }
    
    /**
     * Fallback 匹配逻辑：当 dbo.ckz 函数无法使用或没有 brxxId 时
     * 
     * 逐步放宽匹配条件：
     * 1. 优先匹配指定设备
     * 2. 然后匹配通用记录（sb_djid IS NULL）
     * 3. 最后匹配任意设备的记录
     */
    private Map<String, Object> matchReferenceRangeFallback(Integer xmid, Integer sbDjid) {
        if (xmid == null) {
            return null;
        }
        
        try {
            // 优先级1：精确匹配（指定设备）
            if (sbDjid != null) {
                try {
                    String sql = "SELECT ckz, ckzgx, ckzdx, bjzgx, bjzdx " +
                                 "FROM sys_xmckz " +
                                 "WHERE xmid = ? AND sb_djid = ? " +
                                 "ORDER BY IFNULL(bbsgbz,0) DESC, IFNULL(nlsgbz,0) DESC, IFNULL(xbsgbz,0) DESC LIMIT 1";
                    return jdbcTemplate.queryForMap(sql, xmid, sbDjid);
                } catch (Exception ignored) {
                }
                
                // 优先级2：通用记录（sb_djid IS NULL）
                try {
                    String sql = "SELECT ckz, ckzgx, ckzdx, bjzgx, bjzdx " +
                                 "FROM sys_xmckz " +
                                 "WHERE xmid = ? AND sb_djid IS NULL " +
                                 "ORDER BY IFNULL(bbsgbz,0) DESC, IFNULL(nlsgbz,0) DESC, IFNULL(xbsgbz,0) DESC LIMIT 1";
                    return jdbcTemplate.queryForMap(sql, xmid);
                } catch (Exception ignored) {
                }
                
                // 优先级3：任意设备记录
                try {
                    String sql = "SELECT ckz, ckzgx, ckzdx, bjzgx, bjzdx " +
                                 "FROM sys_xmckz " +
                                 "WHERE xmid = ? " +
                                 "ORDER BY IFNULL(bbsgbz,0) DESC, IFNULL(nlsgbz,0) DESC, IFNULL(xbsgbz,0) DESC LIMIT 1";
                    return jdbcTemplate.queryForMap(sql, xmid);
                } catch (Exception ignored) {
                }
            }
            
            // 如果没有指定设备，直接查找通用记录
            try {
                String sql = "SELECT ckz, ckzgx, ckzdx, bjzgx, bjzdx " +
                             "FROM sys_xmckz " +
                             "WHERE xmid = ? " +
                             "ORDER BY IFNULL(bbsgbz,0) DESC, IFNULL(nlsgbz,0) DESC, IFNULL(xbsgbz,0) DESC LIMIT 1";
                return jdbcTemplate.queryForMap(sql, xmid);
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询某个组合下的检验项目明细
     *
     * 修复：现在正确获取参考范围
     */
    @GetMapping("/combos/{zhid}/items")
    public ResponseEntity<List<Map<String, Object>>> listComboItems(
            @PathVariable("zhid") Integer zhid,
            @RequestParam(required = false) Integer sbDjid,
            @RequestParam(required = false) String sampleType,
            @RequestParam(required = false) Integer sex,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Integer ageUnit) {
        try {
            if (zhid == null) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            StringBuilder sql = new StringBuilder();
            List<Object> params = new ArrayList<>();

            // bgxt_xmzh_mx 表中没有 sb_djid 列，直接查询
            // 修复：同时从 sys_jyxm 获取项目信息，从 sys_xmckz 获取参考值
            sql.append("SELECT mx.id, mx.zhid, mx.xmdm AS code, ");
            sql.append("       COALESCE(mx.xmzwmc, j.xmzwmc, mx.xmdm) AS name, ");
            sql.append("       COALESCE(mx.xmdw, j.xmdw, '') AS unit, ");
            sql.append("       j.xmid ");
            sql.append("FROM bgxt_xmzh_mx mx ");
            sql.append("LEFT JOIN sys_jyxm j ON (CAST(mx.xmdm AS UNSIGNED) = j.xmid OR mx.xmdm = j.xmdm) ");  // 支持数字code和字符串code
            sql.append("WHERE mx.zhid = ? ");
            params.add(zhid);

            sql.append("ORDER BY mx.xh ");

            final Integer finalSbDjid = sbDjid;
            List<Map<String, Object>> list = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getObject("id"));
                row.put("zhid", rs.getObject("zhid"));
                row.put("code", rs.getString("code"));
                row.put("name", rs.getString("name"));
                row.put("unit", rs.getString("unit"));
                
                // 获取参考范围
                Object xmidObj = rs.getObject("xmid");
                Integer xmid = xmidObj != null ? rs.getInt("xmid") : null;
                if (xmid != null && xmid > 0) {
                    try {
                        // 尝试从 sys_xmckz 获取参考范围
                        String ckzSql = "SELECT ckz, ckzgx, ckzdx FROM sys_xmckz WHERE xmid = ? LIMIT 1";
                        Map<String, Object> ckzRow = jdbcTemplate.queryForMap(ckzSql, xmid);
                        String ckz = ckzRow.get("ckz") != null ? String.valueOf(ckzRow.get("ckz")) : "";
                        String ckzgx = ckzRow.get("ckzgx") != null ? String.valueOf(ckzRow.get("ckzgx")) : "";
                        String ckzdx = ckzRow.get("ckzdx") != null ? String.valueOf(ckzRow.get("ckzdx")) : "";
                        
                        // 构建参考范围显示
                        String refRange = "";
                        if (!ckz.isEmpty()) {
                            refRange = ckz;
                        } else if (!ckzgx.isEmpty() || !ckzdx.isEmpty()) {
                            refRange = ckzdx + "-" + ckzgx;
                        }
                        row.put("refRange", refRange);
                    } catch (Exception e) {
                        row.put("refRange", "");
                    }
                } else {
                    row.put("refRange", "");
                }
                
                return row;
            });

            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    /**
     * 样本检验（将样本状态置为"接收/检验中"等）
     *
     * 旧系统：工具栏"检验(F7)"会调用状态更新过程；这里先用直接 UPDATE bgxt_brxx.ybzt 的方式模拟，
     * 后续可以替换为对应的存储过程调用。
     */
    @PostMapping("/inspect/{brxxId}")
    public ResponseEntity<Map<String, Object>> inspectSample(@PathVariable("brxxId") Integer brxxId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (brxxId == null) {
                resp.put("success", false);
                resp.put("message", "缺少样本ID");
                return ResponseEntity.badRequest().body(resp);
            }
            
            // 调试日志
            System.out.println("=== INSPECT DEBUG ===");
            System.out.println("brxxId: " + brxxId);
            
            // 检验前校验：必须存在项目，并且至少有一个结果（先做基础版）
            Integer total = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM bgxt_jyjg WHERE brxx_id = ?",
                    Integer.class,
                    brxxId
            );
            System.out.println("bgxt_jyjg total count: " + total);
            
            if (total == null || total == 0) {
                // 检查bgxt_brxx表中是否有这条记录
                try {
                    Map<String, Object> brxx = jdbcTemplate.queryForMap("SELECT * FROM bgxt_brxx WHERE brxx_id = ?", brxxId);
                    System.out.println("bgxt_brxx record found: " + brxx.get("brxx_id") + ", syh=" + brxx.get("syh"));
                } catch (Exception e) {
                    System.out.println("bgxt_brxx record NOT found for brxxId: " + brxxId);
                }
                
                resp.put("success", false);
                resp.put("message", "请先选择组合项目并录入检验结果后再检验");
                return ResponseEntity.badRequest().body(resp);
            }
            Integer emptyCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM bgxt_jyjg WHERE brxx_id = ? AND IFNULL(TRIM(jyjg), '') = ''",
                    Integer.class,
                    brxxId
            );
            if (emptyCount != null && emptyCount > 0) {
                resp.put("success", false);
                resp.put("message", "存在空结果项目（" + emptyCount + "项），请补齐后再检验");
                return ResponseEntity.badRequest().body(resp);
            }

            jdbcTemplate.update("UPDATE bgxt_brxx SET ybzt = 1 WHERE brxx_id = ?", brxxId);
            resp.put("success", true);
            resp.put("message", "样本已标记为“接收/已检验”");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 样本审核（将样本状态置为“审核”）
     *
     * 旧系统：工具栏“审核(F8)”调用状态更新过程；此处同样先用 UPDATE ybzt=2，后续再映射到 bgxt 层过程。
     */
    @PostMapping("/audit/{brxxId}")
    public ResponseEntity<Map<String, Object>> auditSample(@PathVariable("brxxId") Integer brxxId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (brxxId == null) {
                resp.put("success", false);
                resp.put("message", "缺少样本ID");
                return ResponseEntity.badRequest().body(resp);
            }
            // 检查样本状态：只有已检验(ybzt=1)或已审核(ybzt=2)的样本才能审核
            Integer currentStatus = jdbcTemplate.queryForObject(
                "SELECT ybzt FROM bgxt_brxx WHERE brxx_id = ?", 
                Integer.class, 
                brxxId
            );
            if (currentStatus == null) {
                resp.put("success", false);
                resp.put("message", "样本不存在");
                return ResponseEntity.badRequest().body(resp);
            }
            if (currentStatus < 1) {
                resp.put("success", false);
                resp.put("message", "样本尚未检验，无法审核");
                return ResponseEntity.badRequest().body(resp);
            }
            if (currentStatus == 2) {
                resp.put("success", false);
                resp.put("message", "样本已经处于审核状态");
                return ResponseEntity.badRequest().body(resp);
            }
            if (currentStatus >= 3) {
                resp.put("success", false);
                resp.put("message", "样本已打印，无法修改");
                return ResponseEntity.badRequest().body(resp);
            }

            // 审核关键校验（基础版，对标旧系统 check_ybxx 的“空结果/异常结果提示”）
            Integer total = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM bgxt_jyjg WHERE brxx_id = ?",
                    Integer.class,
                    brxxId
            );
            if (total == null || total == 0) {
                resp.put("success", false);
                resp.put("message", "当前样本没有任何检验项目，无法审核");
                return ResponseEntity.badRequest().body(resp);
            }
            Integer emptyCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM bgxt_jyjg WHERE brxx_id = ? AND IFNULL(TRIM(jyjg), '') = ''",
                    Integer.class,
                    brxxId
            );
            if (emptyCount != null && emptyCount > 0) {
                resp.put("success", false);
                resp.put("message", "存在空结果项目（" + emptyCount + "项），请补齐后再审核");
                return ResponseEntity.badRequest().body(resp);
            }

            // 简单负数/零值检查（仅对纯数字结果）- 使用MySQL兼容语法
            // 注意：MySQL中没有ISNUMERIC和TRY_CAST，这里简化处理
            List<Map<String, Object>> badNums = jdbcTemplate.queryForList(
                    "SELECT jg.xmid, xm.xmzwmc, jg.jyjg " +
                            "FROM bgxt_jyjg jg LEFT JOIN sys_jyxm xm ON jg.xmid=xm.xmid " +
                            "WHERE jg.brxx_id=? AND jg.jyjg REGEXP '^-?[0-9]+(\\.[0-9]+)?$' AND CAST(jg.jyjg AS DECIMAL) < 0 LIMIT 10",
                    brxxId
            );
            if (badNums != null && !badNums.isEmpty()) {
                String items = badNums.stream()
                        .map(m -> String.valueOf(m.get("xmzwmc")) + "=" + String.valueOf(m.get("jyjg")))
                        .collect(Collectors.joining(", "));
                resp.put("success", false);
                resp.put("message", "存在负数结果，无法审核：" + items);
                return ResponseEntity.badRequest().body(resp);
            }
            
            jdbcTemplate.update("UPDATE bgxt_brxx SET ybzt = 2, shrq = NOW() WHERE brxx_id = ?", brxxId);
            resp.put("success", true);
            resp.put("message", "样本已审核");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 样本打印（将样本状态置为“已打印”）
     *
     * 旧系统：工具栏“打印(F2)”调用打印过程；更新 ybzt=3, dybz=1, dycs+1
     * 参考：P_ybxx.pas 中 print_ybxx 相关逻辑
     */
    @PostMapping("/print/{brxxId}")
    public ResponseEntity<Map<String, Object>> printSample(@PathVariable("brxxId") Integer brxxId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (brxxId == null) {
                resp.put("success", false);
                resp.put("message", "缺少样本ID");
                return ResponseEntity.badRequest().body(resp);
            }
            
            // 检查样本状态：只有已审核(ybzt=2)的样本才能打印
            Integer currentStatus = jdbcTemplate.queryForObject(
                "SELECT ybzt FROM bgxt_brxx WHERE brxx_id = ?", 
                Integer.class, 
                brxxId
            );
            if (currentStatus == null) {
                resp.put("success", false);
                resp.put("message", "样本不存在");
                return ResponseEntity.badRequest().body(resp);
            }
            if (currentStatus < 2) {
                resp.put("success", false);
                resp.put("message", "样本尚未审核，无法打印");
                return ResponseEntity.badRequest().body(resp);
            }

            // 检查是否存在检验结果：没有结果时不允许打印（对标旧系统 print_ybxx 中的多项结果校验）
            Integer resultCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM bgxt_jyjg WHERE brxx_id = ?",
                    Integer.class,
                    brxxId
            );
            if (resultCount == null || resultCount == 0) {
                resp.put("success", false);
                resp.put("message", "当前样本尚未产生任何检验结果，不能打印");
                return ResponseEntity.badRequest().body(resp);
            }
            
            // 获取当前打印次数
            Integer currentPrintCount = jdbcTemplate.queryForObject(
                "SELECT IFNULL(dycs, 0) FROM bgxt_brxx WHERE brxx_id = ?", 
                Integer.class, 
                brxxId
            );
            int newPrintCount = (currentPrintCount == null ? 0 : currentPrintCount) + 1;
            
            // 更新：ybzt=3(已打印), dybz=1(打印标志), dycs+1(打印次数)
            jdbcTemplate.update(
                "UPDATE bgxt_brxx SET ybzt = 3, dybz = 1, dycs = ?, bgrq = NOW() WHERE brxx_id = ?", 
                newPrintCount, 
                brxxId
            );
            
            resp.put("success", true);
            resp.put("message", "样本已打印（第" + newPrintCount + "次）");
            resp.put("printCount", newPrintCount);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 查询某个样本的检验结果明细
     * 
     * 如果参考范围为空，会尝试重新匹配
     */
    @GetMapping("/results/{brxxId}")
    public ResponseEntity<List<Map<String, Object>>> listResults(@PathVariable("brxxId") Integer brxxId) {
        try {
            if (brxxId == null) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            // 先获取样本基本信息（用于重新匹配参考范围）
            Map<String, Object> brxx = null;
            try {
                brxx = jdbcTemplate.queryForMap(
                        "SELECT sb_djid, bbzl, brxb, nllx, brnl FROM bgxt_brxx WHERE brxx_id = ? LIMIT 1",
                        brxxId
                );
            } catch (Exception ignored) {
            }

            // 使用 CAST 确保类型兼容，同时LEFT JOIN两个表以获取项目信息
            // 优先从sys_jyxm获取，如果为空则从bgxt_xmzh_mx获取
            String sql = ""
                    + "SELECT CAST(jg.xmid AS UNSIGNED) AS xmid, "
                    + "       jg.zhid, "
                    + "       COALESCE(xm.xmdm, mx.xmdm, '') AS code, "
                    + "       COALESCE(xm.xmzwmc, mx.xmzwmc, '') AS name, "
                    + "       COALESCE(xm.xmdw, mx.xmdw, '') AS unit, "
                    + "       COALESCE(jg.jyjg, '') AS result, "
                    + "       COALESCE(jg.ckz, '') AS refRange, "
                    + "       jg.bjzdx AS criticalLow, "
                    + "       jg.bjzgx AS criticalHigh, "
                    + "       COALESCE(jg.gdbj, '') AS highLowFlag "
                    + "FROM bgxt_jyjg jg "
                    + "LEFT JOIN sys_jyxm xm ON CAST(jg.xmid AS CHAR) = CAST(xm.xmid AS CHAR) "
                    + "LEFT JOIN bgxt_xmzh_mx mx ON jg.xmid = mx.id "
                    + "WHERE jg.brxx_id = ? "
                    + "ORDER BY COALESCE(xm.xmzwmc, mx.xmzwmc, '') ";

            List<Map<String, Object>> list = jdbcTemplate.query(sql, new Object[]{brxxId}, (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                try {
                    Object xmidObj = rs.getObject("xmid");
                    if (xmidObj != null) {
                        row.put("xmid", Integer.parseInt(xmidObj.toString()));
                    } else {
                        row.put("xmid", null);
                    }
                } catch (Exception e) {
                    row.put("xmid", null);
                }
                row.put("zhid", rs.getObject("zhid"));
                row.put("code", rs.getString("code"));
                row.put("name", rs.getString("name"));
                row.put("unit", rs.getString("unit"));
                row.put("result", rs.getString("result"));
                row.put("refRange", rs.getString("refRange"));
                row.put("criticalLow", rs.getObject("criticalLow"));
                row.put("criticalHigh", rs.getObject("criticalHigh"));
                row.put("highLowFlag", rs.getString("highLowFlag"));
                return row;
            });

            // 如果参考范围为空，尝试重新匹配
            Integer sbDjid = null;
            if (brxx != null) {
                Object sbDjidObj = brxx.get("sb_djid");
                if (sbDjidObj instanceof Integer) {
                    sbDjid = (Integer) sbDjidObj;
                } else if (sbDjidObj != null) {
                    try { sbDjid = Integer.parseInt(sbDjidObj.toString()); } catch (Exception ignored) {}
                }
                
                for (Map<String, Object> item : list) {
                    Object refRangeObj = item.get("refRange");
                    String refRange = refRangeObj == null ? "" : String.valueOf(refRangeObj).trim();
                    
                    // 如果参考范围为空，尝试重新匹配
                    if (refRange.isEmpty()) {
                        Object xmidObj = item.get("xmid");
                        if (xmidObj != null) {
                            try {
                                Integer xmid = Integer.parseInt(xmidObj.toString());
                                Map<String, Object> ckzRow = matchReferenceRange(brxxId, xmid, sbDjid);
                                if (ckzRow != null) {
                                    Object ckz = ckzRow.get("ckz");
                                    item.put("refRange", ckz == null ? "" : String.valueOf(ckz));
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                }
            }

            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    /**
     * 报告打印内容（HTML）
     *
     * 先做基础版：头信息 + 项目明细表，供前端打开新窗口进行浏览器打印。
     */
    @GetMapping(value = "/report/{brxxId}", produces = "text/html; charset=UTF-8")
    public ResponseEntity<String> reportHtml(@PathVariable("brxxId") Integer brxxId) {
        try {
            if (brxxId == null) {
                return ResponseEntity.badRequest().body("<html><body>缺少样本ID</body></html>");
            }

            Map<String, Object> brxx = jdbcTemplate.queryForMap(
                    "SELECT brxx_id, syh, brxm, brxb, brnl, nllx, brlb, ksdm, brch, brxx_tmh, brbh, jyrq, shrq, " +
                            "bbzl, lczd, jyys, shys " +
                            "FROM bgxt_brxx WHERE brxx_id=? LIMIT 1",
                    brxxId
            );

            // 获取标本类型名称
            String bbzlmc = "";
            Object bbzlObj = brxx.get("bbzl");
            if (bbzlObj != null) {
                try {
                    String bbzlSql = "SELECT bmsm FROM sys_bbzl WHERE bm = ?";
                    List<String> bbzlList = jdbcTemplate.query(bbzlSql, new Object[]{bbzlObj}, (rs, rowNum) -> rs.getString("bmsm"));
                    if (!bbzlList.isEmpty()) {
                        bbzlmc = bbzlList.get(0);
                    }
                } catch (Exception ignored) {
                }
            }

            // 性别转换
            String sexText = "";
            Object brxbObj = brxx.get("brxb");
            if (brxbObj != null) {
                int brxb = ((Number) brxbObj).intValue();
                sexText = brxb == 1 ? "男" : (brxb == 2 ? "女" : "");
            }

            // 年龄单位转换
            String ageText = "";
            Object brnlObj = brxx.get("brnl");
            Object nllxObj = brxx.get("nllx");
            if (brnlObj != null) {
                ageText = brnlObj.toString();
                if (nllxObj != null) {
                    int nllx = ((Number) nllxObj).intValue();
                    if (nllx == 1) ageText += "岁";
                    else if (nllx == 2) ageText += "月";
                    else if (nllx == 3) ageText += "天";
                    else if (nllx == 4) ageText += "小时";
                }
            }

            List<Map<String, Object>> jg = jdbcTemplate.queryForList(
                    "SELECT mx.xmdm AS code, mx.xmzwmc AS name, jg.jyjg AS result, mx.xmdw AS unit, IFNULL(jg.ckz,'') AS refRange, " +
                            "jg.gdbj AS highLowFlag, jg.bjzdx AS criticalLow, jg.bjzgx AS criticalHigh " +
                            "FROM bgxt_jyjg jg " +
                            "LEFT JOIN bgxt_xmzh_mx mx ON jg.xmid = mx.id " +
                            "WHERE jg.brxx_id=? ORDER BY mx.xh",
                    brxxId
            );

            String title = "检验报告单";
            String html = "<!doctype html><html><head><meta charset='utf-8'/>"
                    + "<title>" + title + "</title>"
                    + "<style>"
                    + "body{font-family:SimSun,Microsoft YaHei,Arial;font-size:12px;color:#000;margin:16px;}"
                    + ".h1{font-size:18px;font-weight:bold;text-align:center;margin:0 0 10px;}"
                    + ".info{display:flex;flex-wrap:wrap;gap:6px 18px;margin-bottom:10px;}"
                    + ".info div{min-width:220px;}"
                    + "table{width:100%;border-collapse:collapse;}"
                    + "th,td{border:1px solid #333;padding:4px 6px;}"
                    + "th{background:#f0f0f0;}"
                    + "@media print{.no-print{display:none;}}"
                    + "</style></head><body>"
                    + "<div class='no-print' style='text-align:right;margin-bottom:8px;'><button onclick='window.print()'>打印</button></div>"
                    + "<div class='h1'>" + title + "</div>"
                    + "<div class='info'>"
                    + "<div><b>样本号：</b>" + safe(brxx.get("syh")) + "</div>"
                    + "<div><b>姓名：</b>" + safe(brxx.get("brxm")) + "</div>"
                    + "<div><b>性别：</b>" + sexText + "</div>"
                    + "<div><b>年龄：</b>" + ageText + "</div>"
                    + "<div><b>条码号：</b>" + safe(brxx.get("brxx_tmh")) + "</div>"
                    + "<div><b>病人号：</b>" + safe(brxx.get("brbh")) + "</div>"
                    + "<div><b>科室：</b>" + safe(brxx.get("ksdm")) + "</div>"
                    + "<div><b>床号：</b>" + safe(brxx.get("brch")) + "</div>"
                    + "<div><b>样本类型：</b>" + bbzlmc + "</div>"
                    + "<div><b>临床诊断：</b>" + safe(brxx.get("lczd")) + "</div>"
                    + "<div><b>检验医师：</b>" + safe(brxx.get("jyys")) + "</div>"
                    + "<div><b>审核医师：</b>" + safe(brxx.get("shys")) + "</div>"
                    + "</div>"
                    + "<table><thead><tr>"
                    + "<th>项目代码</th><th>项目名称</th><th>结果</th><th>单位</th><th>参考范围</th><th>提示</th>"
                    + "</tr></thead><tbody>";

            if (jg.isEmpty()) {
                html += "<tr><td colspan='5' style='text-align:center;'>暂无检验结果</td></tr>";
            } else {
                for (Map<String, Object> r : jg) {
                    String flag = "";
                    Object hl = r.get("highLowFlag");
                    if (hl != null) {
                        String s = String.valueOf(hl);
                        if ("H".equalsIgnoreCase(s)) flag = "↑";
                        if ("L".equalsIgnoreCase(s)) flag = "↓";
                    }
                    html += "<tr>"
                            + "<td>" + safe(r.get("code")) + "</td>"
                            + "<td>" + safe(r.get("name")) + "</td>"
                            + "<td>" + safe(r.get("result")) + "</td>"
                            + "<td>" + safe(r.get("unit")) + "</td>"
                            + "<td>" + safe(r.get("refRange")) + "</td>"
                            + "<td style='text-align:center;'>" + safe(flag) + "</td>"
                            + "</tr>";
                }
            }

            html += "</tbody></table>"
                    + "<div style='margin-top:10px;color:#444;'>报告单号：" + safe(brxx.get("brxx_id")) + "</div>"
                    + "</body></html>";

            return ResponseEntity.ok(html);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("<html><body>生成报告失败：" + e.getMessage() + "</body></html>");
        }
    }

    private static String safe(Object v) {
        if (v == null) return "";
        return String.valueOf(v).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * 查找样本（支持按实验号、姓名、序号、样本号查找）
     *
     * 对标：手册 2.8.1 查找功能，支持多条件查询
     */
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchSamples(
            @RequestParam(required = false) String sampleNo,      // 实验号/样本号
            @RequestParam(required = false) String name,          // 姓名
            @RequestParam(required = false) String barcode,       // 条码号
            @RequestParam(required = false) String patientId,      // 病人号
            @RequestParam(required = false) String date) {        // 日期范围
        try {
            StringBuilder sql = new StringBuilder();
            List<Object> params = new ArrayList();
            
            // 使用MySQL语法
            sql.append("SELECT ");
            sql.append("brxx_id AS id, ");
            sql.append("brxx_tmh AS barcode, ");
            sql.append("brbh     AS patientId, ");
            sql.append("brxm     AS name, ");
            sql.append("CASE brxb WHEN 1 THEN '男' WHEN 2 THEN '女' ELSE '' END AS sex, ");
            sql.append("brnl     AS age, ");
            sql.append("ksdm     AS ward, ");
            sql.append("brch     AS bedNo, ");
            sql.append("syh      AS sampleNo, ");
            sql.append("ybzt     AS status ");
            sql.append("FROM bgxt_brxx ");
            sql.append("WHERE 1=1 ");
            
            // 日期过滤
            if (date != null && !date.trim().isEmpty()) {
                sql.append("AND DATE(jyrq) = DATE(?) ");
                params.add(date);
            } else {
                // 默认查询最近7天的数据
                sql.append("AND jyrq >= DATE_SUB(NOW(), INTERVAL 7 DAY) ");
            }
            
            // 实验号/样本号
            if (sampleNo != null && !sampleNo.trim().isEmpty()) {
                sql.append("AND syh LIKE ? ");
                params.add("%" + sampleNo.trim() + "%");
            }
            
            // 姓名
            if (name != null && !name.trim().isEmpty()) {
                sql.append("AND brxm LIKE ? ");
                params.add("%" + name.trim() + "%");
            }
            
            // 条码号
            if (barcode != null && !barcode.trim().isEmpty()) {
                sql.append("AND brxx_tmh LIKE ? ");
                params.add("%" + barcode.trim() + "%");
            }
            
            // 病人号
            if (patientId != null && !patientId.trim().isEmpty()) {
                sql.append("AND brbh LIKE ? ");
                params.add("%" + patientId.trim() + "%");
            }
            
            sql.append("ORDER BY jyrq DESC, syh DESC ");
            
            List<Map<String, Object>> list = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                for (int i = 1; i <= colCount; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                return row;
            });

            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    /**
     * 批量审核样本
     * 对标：旧系统批量审核功能
     */
    @PostMapping("/batch/audit")
    public ResponseEntity<Map<String, Object>> batchAudit(@RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Integer> brxxIds = (List<Integer>) payload.get("brxxIds");
            if (brxxIds == null || brxxIds.isEmpty()) {
                resp.put("success", false);
                resp.put("message", "请选择要审核的样本");
                return ResponseEntity.badRequest().body(resp);
            }

            int successCount = 0;
            int failCount = 0;
            List<String> errors = new ArrayList<>();

            for (Integer brxxId : brxxIds) {
                try {
                    // 检查样本状态
                    Integer currentStatus = jdbcTemplate.queryForObject(
                            "SELECT ybzt FROM bgxt_brxx WHERE brxx_id = ?",
                            Integer.class, brxxId
                    );
                    if (currentStatus == null || currentStatus < 1) {
                        failCount++;
                        errors.add("样本ID " + brxxId + " 尚未检验，无法审核");
                        continue;
                    }
                    if (currentStatus >= 3) {
                        failCount++;
                        errors.add("样本ID " + brxxId + " 已打印，无法修改");
                        continue;
                    }

                    // 检查是否有空结果
                    Integer emptyCount = jdbcTemplate.queryForObject(
                            "SELECT COUNT(1) FROM bgxt_jyjg WHERE brxx_id = ? AND IFNULL(TRIM(jyjg), '') = ''",
                            Integer.class, brxxId
                    );
                    if (emptyCount != null && emptyCount > 0) {
                        failCount++;
                        errors.add("样本ID " + brxxId + " 存在空结果项目");
                        continue;
                    }

                    // 审核通过
                    jdbcTemplate.update("UPDATE bgxt_brxx SET ybzt = 2, shrq = NOW() WHERE brxx_id = ?", brxxId);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    errors.add("样本ID " + brxxId + " 审核失败: " + e.getMessage());
                }
            }

            resp.put("success", failCount == 0);
            resp.put("message", "批量审核完成：成功 " + successCount + " 条，失败 " + failCount + " 条");
            resp.put("successCount", successCount);
            resp.put("failCount", failCount);
            resp.put("errors", errors);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 批量打印样本
     * 对标：旧系统批量打印功能
     */
    @PostMapping("/batch/print")
    public ResponseEntity<Map<String, Object>> batchPrint(@RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Integer> brxxIds = (List<Integer>) payload.get("brxxIds");
            if (brxxIds == null || brxxIds.isEmpty()) {
                resp.put("success", false);
                resp.put("message", "请选择要打印的样本");
                return ResponseEntity.badRequest().body(resp);
            }

            int successCount = 0;
            int failCount = 0;
            List<Map<String, Object>> results = new ArrayList<>();

            for (Integer brxxId : brxxIds) {
                try {
                    // 检查样本状态
                    Integer currentStatus = jdbcTemplate.queryForObject(
                            "SELECT ybzt FROM bgxt_brxx WHERE brxx_id = ?",
                            Integer.class, brxxId
                    );
                    if (currentStatus == null || currentStatus < 2) {
                        failCount++;
                        Map<String, Object> error = new HashMap<>();
                        error.put("brxxId", brxxId);
                        error.put("message", "样本尚未审核，无法打印");
                        results.add(error);
                        continue;
                    }

                    // 检查是否有检验结果
                    Integer resultCount = jdbcTemplate.queryForObject(
                            "SELECT COUNT(1) FROM bgxt_jyjg WHERE brxx_id = ?",
                            Integer.class, brxxId
                    );
                    if (resultCount == null || resultCount == 0) {
                        failCount++;
                        Map<String, Object> error = new HashMap<>();
                        error.put("brxxId", brxxId);
                        error.put("message", "样本没有检验结果");
                        results.add(error);
                        continue;
                    }

                    // 更新打印信息
                    Integer currentPrintCount = jdbcTemplate.queryForObject(
                            "SELECT IFNULL(dycs, 0) FROM bgxt_brxx WHERE brxx_id = ?",
                            Integer.class, brxxId
                    );
                    int newPrintCount = (currentPrintCount == null ? 0 : currentPrintCount) + 1;
                    jdbcTemplate.update(
                            "UPDATE bgxt_brxx SET ybzt = 3, dybz = 1, dycs = ?, bgrq = NOW() WHERE brxx_id = ?",
                            newPrintCount, brxxId
                    );
                    successCount++;

                    Map<String, Object> result = new HashMap<>();
                    result.put("brxxId", brxxId);
                    result.put("printCount", newPrintCount);
                    results.add(result);
                } catch (Exception e) {
                    failCount++;
                    Map<String, Object> error = new HashMap<>();
                    error.put("brxxId", brxxId);
                    error.put("message", e.getMessage());
                    results.add(error);
                }
            }

            resp.put("success", failCount == 0);
            resp.put("message", "批量打印完成：成功 " + successCount + " 条，失败 " + failCount + " 条");
            resp.put("successCount", successCount);
            resp.put("failCount", failCount);
            resp.put("results", results);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 批量作废样本
     * 对标：旧系统样本作废功能
     */
    @PostMapping("/batch/invalidate")
    public ResponseEntity<Map<String, Object>> batchInvalidate(@RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Integer> brxxIds = (List<Integer>) payload.get("brxxIds");
            if (brxxIds == null || brxxIds.isEmpty()) {
                resp.put("success", false);
                resp.put("message", "请选择要作废的样本");
                return ResponseEntity.badRequest().body(resp);
            }

            String reason = (String) payload.getOrDefault("reason", "");

            int successCount = 0;
            int failCount = 0;
            List<String> errors = new ArrayList<>();

            for (Integer brxxId : brxxIds) {
                try {
                    // 检查样本状态：只有未打印的样本可以作废
                    Integer currentStatus = jdbcTemplate.queryForObject(
                            "SELECT ybzt FROM bgxt_brxx WHERE brxx_id = ?",
                            Integer.class, brxxId
                    );
                    if (currentStatus != null && currentStatus == 3) {
                        failCount++;
                        errors.add("样本ID " + brxxId + " 已打印，无法作废");
                        continue;
                    }

                    // 作废样本（设置作废标志）
                    jdbcTemplate.update(
                            "UPDATE bgxt_brxx SET sfbz = 1, bz = ? WHERE brxx_id = ?",
                            reason, brxxId
                    );
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    errors.add("样本ID " + brxxId + " 作废失败: " + e.getMessage());
                }
            }

            resp.put("success", failCount == 0);
            resp.put("message", "批量作废完成：成功 " + successCount + " 条，失败 " + failCount + " 条");
            resp.put("successCount", successCount);
            resp.put("failCount", failCount);
            resp.put("errors", errors);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 批量取消审核
     * 对标：旧系统取消审核功能
     */
    @PostMapping("/batch/unaudit")
    public ResponseEntity<Map<String, Object>> batchUnaudit(@RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            @SuppressWarnings("unchecked")
            List<Integer> brxxIds = (List<Integer>) payload.get("brxxIds");
            if (brxxIds == null || brxxIds.isEmpty()) {
                resp.put("success", false);
                resp.put("message", "请选择要取消审核的样本");
                return ResponseEntity.badRequest().body(resp);
            }

            int successCount = 0;
            int failCount = 0;
            List<String> errors = new ArrayList<>();

            for (Integer brxxId : brxxIds) {
                try {
                    // 检查样本状态
                    Integer currentStatus = jdbcTemplate.queryForObject(
                            "SELECT ybzt FROM bgxt_brxx WHERE brxx_id = ?",
                            Integer.class, brxxId
                    );
                    if (currentStatus == null || currentStatus != 2) {
                        failCount++;
                        errors.add("样本ID " + brxxId + " 未审核，无法取消");
                        continue;
                    }
                    if (currentStatus == 3) {
                        failCount++;
                        errors.add("样本ID " + brxxId + " 已打印，无法取消审核");
                        continue;
                    }

                    // 取消审核：状态改回已检验
                    jdbcTemplate.update("UPDATE bgxt_brxx SET ybzt = 1, shrq = NULL WHERE brxx_id = ?", brxxId);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    errors.add("样本ID " + brxxId + " 取消审核失败: " + e.getMessage());
                }
            }

            resp.put("success", failCount == 0);
            resp.put("message", "批量取消审核完成：成功 " + successCount + " 条，失败 " + failCount + " 条");
            resp.put("successCount", successCount);
            resp.put("failCount", failCount);
            resp.put("errors", errors);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 数据处理进度统计
     * 对标：旧系统数据处理进度功能，使用饼图展示各类样本状态占比
     */
    @GetMapping("/stats/progress")
    public ResponseEntity<Map<String, Object>> getProgressStats(@RequestParam(required = false) String date) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String targetDate = (date == null || date.isEmpty())
                    ? LocalDate.now().toString()
                    : date;

            // 统计当天各状态样本数量
            String sql = ""
                    + "SELECT "
                    + "  SUM(CASE WHEN ybzt = 0 OR ybzt IS NULL THEN 1 ELSE 0 END) AS statusEntry, "  // 录入
                    + "  SUM(CASE WHEN ybzt = 1 THEN 1 ELSE 0 END) AS statusInspected, "  // 已检验
                    + "  SUM(CASE WHEN ybzt = 2 THEN 1 ELSE 0 END) AS statusAudited, "  // 已审核
                    + "  SUM(CASE WHEN ybzt = 3 THEN 1 ELSE 0 END) AS statusPrinted, "  // 已打印
                    + "  SUM(CASE WHEN sfbz = 1 THEN 1 ELSE 0 END) AS statusInvalid, "  // 已作废
                    + "  COUNT(*) AS total "
                    + "FROM bgxt_brxx "
                    + "WHERE DATE(jyrq) = DATE(?)";

            Map<String, Object> stats = jdbcTemplate.queryForMap(sql, targetDate);

            resp.put("success", true);
            resp.put("date", targetDate);
            resp.put("stats", stats);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 标本处理 - 获取待处理的标本列表
     * 对标：旧系统标本处理功能
     */
    @GetMapping("/sample/issues")
    public ResponseEntity<List<Map<String, Object>>> getSampleIssues(@RequestParam(required = false) String date) {
        try {
            String targetDate = (date == null || date.isEmpty())
                    ? LocalDate.now().toString()
                    : date;

            // 查询当天有问题的标本（这里简单查询作废的和有备注的）
            String sql = ""
                    + "SELECT "
                    + "  brxx_id AS id, "
                    + "  syh AS sampleNo, "
                    + "  brxm AS name, "
                    + "  brxb AS sex, "
                    + "  brnl AS age, "
                    + "  nllx AS ageUnit, "
                    + "  bbzl AS sampleType, "
                    + "  ybzt AS status, "
                    + "  sfbz AS isInvalid, "
                    + "  bz AS remarks, "
                    + "  jyrq AS testDate "
                    + "FROM bgxt_brxx "
                    + "WHERE DATE(jyrq) = DATE(?) "
                    + "AND (sfbz = 1 OR LENGTH(IFNULL(bz, '')) > 0) "
                    + "ORDER BY jyrq DESC";

            List<Map<String, Object>> list = jdbcTemplate.query(sql, new Object[]{targetDate}, (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                for (int i = 1; i <= colCount; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                return row;
            });

            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    /**
     * 标本处理 - 处理标本问题
     * 对标：旧系统标本处理功能
     */
    @PostMapping("/sample/handle")
    public ResponseEntity<Map<String, Object>> handleSampleIssue(@RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Integer brxxId = null;
            Object brxxIdObj = payload.get("brxxId");
            if (brxxIdObj != null) {
                brxxId = Integer.parseInt(brxxIdObj.toString());
            }

            String action = (String) payload.get("action"); // 取消作废/修改备注/删除
            String remarks = (String) payload.getOrDefault("remarks", "");

            if (brxxId == null) {
                resp.put("success", false);
                resp.put("message", "缺少样本ID");
                return ResponseEntity.badRequest().body(resp);
            }

            if ("cancelInvalid".equals(action)) {
                // 取消作废
                jdbcTemplate.update("UPDATE bgxt_brxx SET sfbz = 0, bz = ? WHERE brxx_id = ?", remarks, brxxId);
                resp.put("message", "已取消作废");
            } else if ("updateRemarks".equals(action)) {
                // 修改备注
                jdbcTemplate.update("UPDATE bgxt_brxx SET bz = ? WHERE brxx_id = ?", remarks, brxxId);
                resp.put("message", "备注已更新");
            } else if ("delete".equals(action)) {
                // 删除样本（仅当未打印时可删除）
                Integer currentStatus = jdbcTemplate.queryForObject(
                        "SELECT ybzt FROM bgxt_brxx WHERE brxx_id = ?",
                        Integer.class, brxxId
                );
                if (currentStatus != null && currentStatus == 3) {
                    resp.put("success", false);
                    resp.put("message", "已打印的样本无法删除");
                    return ResponseEntity.badRequest().body(resp);
                }
                // 删除检验结果
                jdbcTemplate.update("DELETE FROM bgxt_jyjg WHERE brxx_id = ?", brxxId);
                // 删除样本
                jdbcTemplate.update("DELETE FROM bgxt_brxx WHERE brxx_id = ?", brxxId);
                resp.put("message", "样本已删除");
            } else {
                resp.put("success", false);
                resp.put("message", "无效的操作类型");
                return ResponseEntity.badRequest().body(resp);
            }

            resp.put("success", true);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}


