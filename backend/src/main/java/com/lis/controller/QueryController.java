package com.lis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 5.1 标本明细查询
 * 对应SRS_Addendum需求 ADD-QRY-001
 */
@RestController
@RequestMapping("/query")
public class QueryController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 标本明细查询
     * 对应SRS_Addendum需求 ADD-QRY-001.1 - ADD-QRY-001.8
     */
    @GetMapping("/sample/list")
    public ResponseEntity<Map<String, Object>> querySampleList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String patientType,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String testItem,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String instrument,
            @RequestParam(required = false) String examiner,
            @RequestParam(required = false) String auditor,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        try {
            StringBuilder sql = new StringBuilder();
            List<Object> params = new ArrayList<>();
            
            // MySQL分页语法
            int offset = (page - 1) * pageSize;
            
            // 构建查询SQL - 使用MySQL语法，包含用户要求的所有字段
            // 条码号、核收时间、设备、组合名称、标本种类、病人姓名、性别、年龄、科室、床号、病人类别、工作组、核收人
            sql.append("SELECT ");
            sql.append("b.brxx_id AS id, ");              // 添加ID用于调试
            sql.append("b.brxx_tmh AS barcode, ");          // 条码号
            sql.append("b.jyrq AS receiveTime, ");         // 核收时间
            sql.append("IFNULL(s.sbmc, '') AS instrument, ");  // 设备
            sql.append("'' AS comboName, ");               // 组合名称（暂不关联）
            sql.append("IFNULL(bb.bmsm, '') AS sampleTypeName, ");  // 标本种类
            sql.append("b.brxm AS patientName, ");         // 病人姓名
            sql.append("CASE b.brxb WHEN 1 THEN '男' WHEN 2 THEN '女' ELSE '' END AS sex, ");  // 性别
            sql.append("b.brnl AS age, ");                  // 年龄
            sql.append("b.ksdm AS department, ");          // 科室
            sql.append("IFNULL(b.brch, '') AS bedNo, ");   // 床号
            sql.append("CASE b.brlb WHEN 1 THEN '门诊' WHEN 2 THEN '住院' WHEN 3 THEN '体检' WHEN 4 THEN '其他' WHEN 5 THEN '科研' ELSE '' END AS patientCategory, ");  // 病人类别
            sql.append("'' AS workGroup, ");               // 工作组（暂不关联）
            sql.append("IFNULL(b.jyys, '') AS receiver "); // 核收人
            sql.append("FROM bgxt_brxx b ");
            sql.append("LEFT JOIN sys_sbdjb s ON b.sb_djid = s.sb_djid ");
            sql.append("LEFT JOIN sys_bbzl bb ON b.bbzl = bb.bm ");
            sql.append("WHERE 1=1 ");
            
            // 日期范围过滤
            if (startDate != null && !startDate.isEmpty()) {
                sql.append("AND DATE(b.jyrq) >= DATE(?) ");
                params.add(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                sql.append("AND DATE(b.jyrq) <= DATE(?) ");
                params.add(endDate);
            }
            
            // 病人类型过滤
            if (patientType != null && !patientType.isEmpty()) {
                switch (patientType) {
                    case "门诊病人": sql.append("AND b.brlb = 1 "); break;
                    case "住院病人": sql.append("AND b.brlb = 2 "); break;
                    case "体检人员": sql.append("AND b.brlb = 3 "); break;
                    case "其他病人": sql.append("AND b.brlb = 4 "); break;
                    case "科研人员": sql.append("AND b.brlb = 5 "); break;
                }
            }
            
            // 科室过滤
            if (department != null && !department.isEmpty()) {
                sql.append("AND b.ksdm = ? ");
                params.add(department);
            }
            
            // 样本状态过滤
            if (status != null && !status.isEmpty()) {
                switch (status) {
                    case "新建": sql.append("AND b.ybzt = 0 "); break;
                    case "已保存": sql.append("AND b.ybzt = 0 "); break;
                    case "已检验": sql.append("AND b.ybzt = 1 "); break;
                    case "已审核": sql.append("AND b.ybzt = 2 "); break;
                    case "已打印": sql.append("AND b.ybzt = 3 "); break;
                }
            }
            
            // 仪器过滤
            if (instrument != null && !instrument.isEmpty()) {
                try {
                    int instId = Integer.parseInt(instrument.toString());
                    sql.append("AND b.sb_djid = ? ");
                    params.add(instId);
                } catch (NumberFormatException e) {
                    // 如果解析失败，使用字符串比较
                    sql.append("AND b.sb_djid = ? ");
                    params.add(instrument);
                }
            }
            
            // 检验医生过滤
            if (examiner != null && !examiner.isEmpty()) {
                sql.append("AND b.jyys = ? ");
                params.add(examiner);
            }
            
            // 审核医生过滤
            if (auditor != null && !auditor.isEmpty()) {
                sql.append("AND b.shys = ? ");
                params.add(auditor);
            }
            
            // 排序和分页
            sql.append("ORDER BY b.jyrq DESC ");
            sql.append("LIMIT ? OFFSET ? ");
            params.add(pageSize);
            params.add(offset);
            
            // 执行查询
            List<Map<String, Object>> list = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                try { row.put("id", rs.getInt("id")); } catch (Exception e) { row.put("id", 0); }
                try { row.put("barcode", rs.getString("barcode")); } catch (Exception e) { row.put("barcode", ""); }
                try { row.put("receiveTime", rs.getTimestamp("receiveTime")); } catch (Exception e) { row.put("receiveTime", null); }
                try { row.put("instrument", rs.getString("instrument")); } catch (Exception e) { row.put("instrument", ""); }
                try { row.put("comboName", rs.getString("comboName")); } catch (Exception e) { row.put("comboName", ""); }
                try { row.put("sampleTypeName", rs.getString("sampleTypeName")); } catch (Exception e) { row.put("sampleTypeName", ""); }
                try { row.put("patientName", rs.getString("patientName")); } catch (Exception e) { row.put("patientName", ""); }
                try { row.put("sex", rs.getString("sex")); } catch (Exception e) { row.put("sex", ""); }
                try { row.put("age", rs.getObject("age")); } catch (Exception e) { row.put("age", null); }
                try { row.put("department", rs.getString("department")); } catch (Exception e) { row.put("department", ""); }
                try { row.put("bedNo", rs.getString("bedNo")); } catch (Exception e) { row.put("bedNo", ""); }
                try { row.put("patientCategory", rs.getString("patientCategory")); } catch (Exception e) { row.put("patientCategory", ""); }
                try { row.put("workGroup", rs.getString("workGroup")); } catch (Exception e) { row.put("workGroup", ""); }
                try { row.put("receiver", rs.getString("receiver")); } catch (Exception e) { row.put("receiver", ""); }
                return row;
            });
            
            // 查询总数
            String countSql = "SELECT COUNT(*) FROM bgxt_brxx b WHERE 1=1 ";
            List<Object> countParams = new ArrayList<>();
            
            if (startDate != null && !startDate.isEmpty()) {
                countSql += "AND DATE(b.jyrq) >= DATE(?) ";
                countParams.add(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                countSql += "AND DATE(b.jyrq) <= DATE(?) ";
                countParams.add(endDate);
            }
            if (patientType != null && !patientType.isEmpty()) {
                switch (patientType) {
                    case "门诊病人": countSql += "AND b.brlb = 1 "; break;
                    case "住院病人": countSql += "AND b.brlb = 2 "; break;
                    case "体检人员": countSql += "AND b.brlb = 3 "; break;
                    case "其他病人": countSql += "AND b.brlb = 4 "; break;
                    case "科研人员": countSql += "AND b.brlb = 5 "; break;
                }
            }
            if (department != null && !department.isEmpty()) {
                countSql += "AND b.ksdm = ? ";
                countParams.add(department);
            }
            if (status != null && !status.isEmpty()) {
                switch (status) {
                    case "新建": countSql += "AND b.ybzt = 0 "; break;
                    case "已保存": countSql += "AND b.ybzt = 0 "; break;
                    case "已检验": countSql += "AND b.ybzt = 1 "; break;
                    case "已审核": countSql += "AND b.ybzt = 2 "; break;
                    case "已打印": countSql += "AND b.ybzt = 3 "; break;
                }
            }
            if (instrument != null && !instrument.isEmpty()) {
                countSql += "AND b.sb_djid = ? ";
                countParams.add(instrument);
            }
            if (examiner != null && !examiner.isEmpty()) {
                countSql += "AND b.jyys = ? ";
                countParams.add(examiner);
            }
            if (auditor != null && !auditor.isEmpty()) {
                countSql += "AND b.shys = ? ";
                countParams.add(auditor);
            }
            
            Integer total = jdbcTemplate.queryForObject(countSql, countParams.toArray(), Integer.class);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", total != null ? total : 0);
            result.put("list", list);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("total", 0);
            error.put("list", new ArrayList<>());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 标本统计
     * 对应SRS_Addendum需求 ADD-QRY-004
     */
    @GetMapping("/sample/statistics")
    public ResponseEntity<Map<String, Object>> querySampleStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String patientType,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String testItem,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String instrument,
            @RequestParam(required = false) String examiner,
            @RequestParam(required = false) String auditor) {
        
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 按病人类别统计 - 从数据库查询
            List<Map<String, Object>> byPatientType = new ArrayList<>();
            try {
                List<Map<String, Object>> brlbStats = jdbcTemplate.queryForList(
                    "SELECT brlb, COUNT(*) as cnt FROM bgxt_brxx GROUP BY brlb"
                );
                for (Map<String, Object> stat : brlbStats) {
                    Object brlb = stat.get("brlb");
                    Object cnt = stat.get("cnt");
                    String name = "其他";
                    if (brlb != null) {
                        switch (Integer.parseInt(brlb.toString())) {
                            case 1: name = "门诊"; break;
                            case 2: name = "住院"; break;
                            case 3: name = "体检"; break;
                            case 4: name = "其他"; break;
                            case 5: name = "科研"; break;
                        }
                    }
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", name);
                    item.put("value", cnt != null ? Integer.parseInt(cnt.toString()) : 0);
                    byPatientType.add(item);
                }
            } catch (Exception e) {
                byPatientType.add(createStatItem("门诊", 0));
                byPatientType.add(createStatItem("住院", 0));
                byPatientType.add(createStatItem("体检", 0));
            }
            if (byPatientType.isEmpty()) {
                byPatientType.add(createStatItem("门诊", 0));
                byPatientType.add(createStatItem("住院", 0));
                byPatientType.add(createStatItem("体检", 0));
            }
            result.put("byPatientType", byPatientType);
            
            // 按样本状态统计 - 从数据库查询
            List<Map<String, Object>> byStatus = new ArrayList<>();
            try {
                List<Map<String, Object>> ybztStats = jdbcTemplate.queryForList(
                    "SELECT ybzt, COUNT(*) as cnt FROM bgxt_brxx GROUP BY ybzt"
                );
                for (Map<String, Object> stat : ybztStats) {
                    Object ybzt = stat.get("ybzt");
                    Object cnt = stat.get("cnt");
                    String name = "新建";
                    if (ybzt != null) {
                        switch (Integer.parseInt(ybzt.toString())) {
                            case 0: name = "新建"; break;
                            case 1: name = "已检验"; break;
                            case 2: name = "已审核"; break;
                            case 3: name = "已打印"; break;
                        }
                    }
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", name);
                    item.put("value", cnt != null ? Integer.parseInt(cnt.toString()) : 0);
                    byStatus.add(item);
                }
            } catch (Exception e) {
                byStatus.add(createStatItem("已审核", 0));
                byStatus.add(createStatItem("已检验", 0));
            }
            if (byStatus.isEmpty()) {
                byStatus.add(createStatItem("已审核", 0));
                byStatus.add(createStatItem("已检验", 0));
            }
            result.put("byStatus", byStatus);
            
            // 按日期统计 - 从数据库查询最近7天
            List<Map<String, Object>> byDate = new ArrayList<>();
            try {
                List<Map<String, Object>> dateStats = jdbcTemplate.queryForList(
                    "SELECT DATE(jyrq) as stat_date, COUNT(*) as cnt FROM bgxt_brxx " +
                    "WHERE jyrq >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                    "GROUP BY DATE(jyrq) ORDER BY stat_date"
                );
                for (Map<String, Object> stat : dateStats) {
                    Object date = stat.get("stat_date");
                    Object cnt = stat.get("cnt");
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", date != null ? date.toString() : "");
                    item.put("count", cnt != null ? Integer.parseInt(cnt.toString()) : 0);
                    byDate.add(item);
                }
            } catch (Exception e) {
                // 忽略
            }
            if (byDate.isEmpty()) {
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                for (int i = 6; i >= 0; i--) {
                    LocalDate date = today.minusDays(i);
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", date.format(formatter));
                    item.put("count", 0);
                    byDate.add(item);
                }
            }
            result.put("byDate", byDate);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("byPatientType", new ArrayList<>());
            error.put("byStatus", new ArrayList<>());
            error.put("byDate", new ArrayList<>());
            return ResponseEntity.ok(error);
        }
    }

    /**
     * 导出Excel
     * 对应SRS_Addendum需求 ADD-QRY-003
     */
    @GetMapping("/sample/export")
    public ResponseEntity<Map<String, Object>> exportSample(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String patientType,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String testItem,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String instrument,
            @RequestParam(required = false) String examiner,
            @RequestParam(required = false) String auditor) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "导出功能待实现");
        return ResponseEntity.ok(result);
    }

    /**
     * 获取查询条件下拉选项
     */
    @GetMapping("/options")
    public ResponseEntity<Map<String, Object>> getQueryOptions() {
        try {
            Map<String, Object> options = new HashMap<>();
            
            // 病人类型选项
            List<String> patientTypes = Arrays.asList("门诊病人", "住院病人", "体检人员", "其他病人", "科研人员");
            options.put("patientTypes", patientTypes);
            
            // 样本状态选项
            List<String> statuses = Arrays.asList("新建", "已保存", "已检验", "已审核", "已打印", "作废");
            options.put("statuses", statuses);
            
            // 从数据库加载科室选项
            try {
                List<Map<String, Object>> deptList = jdbcTemplate.queryForList(
                    "SELECT ksdm, ksmc FROM sys_kssz WHERE sybz = 1 ORDER BY ksdm"
                );
                options.put("departments", deptList);
            } catch (Exception e) {
                options.put("departments", new ArrayList<>());
            }
            
            // 从数据库加载仪器选项
            try {
                List<Map<String, Object>> instList = jdbcTemplate.queryForList(
                    "SELECT sb_djid, sbmc FROM sys_sbdjb WHERE tybz = 0 ORDER BY sbmc"
                );
                options.put("instruments", instList);
            } catch (Exception e) {
                options.put("instruments", new ArrayList<>());
            }
            
            // 从数据库加载检验项目选项
            try {
                List<Map<String, Object>> itemList = jdbcTemplate.queryForList(
                    "SELECT xmid, xmzwmc FROM sys_jyxm WHERE sybz = 1 OR sybz IS NULL ORDER BY xmzwmc"
                );
                options.put("testItems", itemList);
            } catch (Exception e) {
                options.put("testItems", new ArrayList<>());
            }
            
            // 从数据库加载医生选项（检验医生和审核医生）
            try {
                List<Map<String, Object>> doctorList = jdbcTemplate.queryForList(
                    "SELECT DISTINCT jyys AS doctorCode, jyys AS doctorName FROM bgxt_brxx WHERE jyys IS NOT NULL AND jyys != '' ORDER BY jyys"
                );
                options.put("examiners", doctorList);
            } catch (Exception e) {
                options.put("examiners", new ArrayList<>());
            }
            
            // 审核医生选项
            try {
                List<Map<String, Object>> auditorList = jdbcTemplate.queryForList(
                    "SELECT DISTINCT shys AS doctorCode, shys AS doctorName FROM bgxt_brxx WHERE shys IS NOT NULL AND shys != '' ORDER BY shys"
                );
                options.put("auditors", auditorList);
            } catch (Exception e) {
                options.put("auditors", new ArrayList<>());
            }
            
            return ResponseEntity.ok(options);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new HashMap<>());
        }
    }

    private Map<String, Object> createStatItem(String name, int value) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("value", value);
        return item;
    }
    
    /**
     * 清理数据库中的重复记录和乱码数据
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupDatabase() {
        Map<String, Object> result = new HashMap<>();
        try {
            int deletedCount = 0;
            
            // 1. 简化处理：找出有重复的名字的记录，只保留最新的
            try {
                // 找出有重复病人姓名的记录
                String findDupSql = "SELECT brxm, COUNT(*) as cnt " +
                    "FROM bgxt_brxx " +
                    "WHERE brxm IS NOT NULL AND brxm != '' " +
                    "GROUP BY brxm " +
                    "HAVING COUNT(*) > 1";
                List<Map<String, Object>> sameRecords = jdbcTemplate.queryForList(findDupSql);
                
                System.out.println("发现 " + sameRecords.size() + " 个重复病人姓名");
                
                for (Map<String, Object> rec : sameRecords) {
                    String brxm = (String) rec.get("brxm");
                    Integer cnt = (Integer) rec.get("cnt");
                    
                    System.out.println("处理: " + brxm + " 数量: " + cnt);
                    
                    if (brxm != null && !brxm.isEmpty()) {
                        // 删除该病人姓名的所有记录，只保留最新的（brxx_id最大的）
                        int deleted = jdbcTemplate.update(
                            "DELETE FROM bgxt_brxx WHERE brxm = ? AND brxx_id NOT IN (SELECT MAX(brxx_id) FROM bgxt_brxx WHERE brxm = ?)",
                            brxm, brxm
                        );
                        deletedCount += deleted;
                        System.out.println("删除了 " + deleted + " 条重复记录");
                    }
                }
            } catch (Exception e) {
                System.out.println("清理重复记录时出错: " + e.getMessage());
                e.printStackTrace();
            }
            
            // 2. 删除所有名字为乱码的记录
            try {
                int garbledDeleted = jdbcTemplate.update(
                    "DELETE FROM bgxt_brxx WHERE brxm LIKE '%�%' OR brxm LIKE '%�%' OR brxm = '' OR brxm IS NULL"
                );
                deletedCount += garbledDeleted;
                System.out.println("删除了 " + garbledDeleted + " 条乱码/空名字记录");
            } catch (Exception e) {
                System.out.println("清理乱码名字时出错: " + e.getMessage());
            }
            
            // 2. 清理完全相同的多条记录（所有字段相同）
            try {
                // 找出所有重复的完整记录
                String findDupSql = "SELECT brxm, brxb, brnl, brlb, jyrq, COUNT(*) as cnt " +
                    "FROM bgxt_brxx " +
                    "GROUP BY brxm, brxb, brnl, brlb, jyrq " +
                    "HAVING COUNT(*) > 1";
                List<Map<String, Object>> sameRecords = jdbcTemplate.queryForList(findDupSql);
                
                for (Map<String, Object> rec : sameRecords) {
                    String brxm = (String) rec.get("brxm");
                    Integer brxb = (Integer) rec.get("brxb");
                    String brnl = (String) rec.get("brnl");
                    Integer brlb = (Integer) rec.get("brlb");
                    Object jyrq = rec.get("jyrq");
                    
                    if (brxm != null && !brxm.isEmpty()) {
                        int deleted = jdbcTemplate.update(
                            "DELETE FROM bgxt_brxx WHERE brxm = ? AND brxb = ? AND brnl = ? AND brlb = ? AND jyrq = ? AND brxx_id NOT IN (SELECT MAX(brxx_id) FROM bgxt_brxx WHERE brxm = ? AND brxb = ? AND brnl = ? AND brlb = ? AND jyrq = ?)",
                            brxm, brxb, brnl, brlb, jyrq, brxm, brxb, brnl, brlb, jyrq
                        );
                        deletedCount += deleted;
                    }
                }
            } catch (Exception e) {
                System.out.println("清理完全相同记录时出错: " + e.getMessage());
            }
            
            // 3. 清理检验结果表中的重复数据
            try {
                List<Map<String, Object>> resultDuplicates = jdbcTemplate.queryForList(
                    "SELECT brxx_id, xmid FROM bgxt_jyjg GROUP BY brxx_id, xmid HAVING COUNT(*) > 1"
                );
                
                for (Map<String, Object> dup : resultDuplicates) {
                    Object brxxId = dup.get("brxx_id");
                    Object xmid = dup.get("xmid");
                    if (brxxId != null && xmid != null) {
                        int deleted = jdbcTemplate.update(
                            "DELETE FROM bgxt_jyjg WHERE brxx_id = ? AND xmid = ? AND id NOT IN (SELECT MAX(id) FROM bgxt_jyjg WHERE brxx_id = ? AND xmid = ?)",
                            brxxId, xmid, brxxId, xmid
                        );
                        deletedCount += deleted;
                    }
                }
            } catch (Exception e) {
                System.out.println("清理检验结果重复时出错: " + e.getMessage());
            }
            
            // 4. 清理sys_bbzl表中的乱码并设置正确的标本种类名称
            try {
                // 先查看当前有哪些
                List<Map<String, Object>> bbzlList = jdbcTemplate.queryForList("SELECT bmid, bm, bmsm FROM sys_bbzl");
                System.out.println("当前标本种类数量: " + bbzlList.size());
                
                for (Map<String, Object> bbzl : bbzlList) {
                    String bmsm = (String) bbzl.get("bmsm");
                    Integer bmid = (Integer) bbzl.get("bmid");
                    String bm = (String) bbzl.get("bm");
                    
                    if (bmsm != null && (bmsm.contains("�") || bmsm.equals("Serum"))) {
                        // 根据bm字段设置正确的中文名称
                        String fixed = bm;
                        switch (bm) {
                            case "1": fixed = "血清"; break;
                            case "2": fixed = "血浆"; break;
                            case "3": fixed = "全血"; break;
                            case "4": fixed = "尿液"; break;
                            case "5": fixed = "粪便"; break;
                            case "6": fixed = "脑脊液"; break;
                            case "7": fixed = "胸腹水"; break;
                            case "8": fixed = "其他"; break;
                            default: fixed = "血清"; break;
                        }
                        jdbcTemplate.update("UPDATE sys_bbzl SET bmsm = ? WHERE bmid = ?", fixed, bmid);
                        System.out.println("修复标本种类: bm=" + bm + " 改为 '" + fixed + "'");
                    }
                }
            } catch (Exception e) {
                System.out.println("修复标本种类时出错: " + e.getMessage());
            }
            
            result.put("success", true);
            result.put("message", "清理完成，共删除 " + deletedCount + " 条重复记录");
            result.put("deletedCount", deletedCount);
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "清理失败: " + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}
