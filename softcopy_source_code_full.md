
# 新致开源LIS系统 - 软件著作权申请源代码文档

## 目录

1. [后端核心代码](#后端核心代码)
   - 1.1 [控制器层 (Controller)](#11-控制器层-controller)
   - 1.2 [实体层 (Entity)](#12-实体层-entity)
   - 1.3 [数据访问层 (Mapper)](#13-数据访问层-mapper)
   - 1.4 [服务层 (Service)](#14-服务层-service)
   - 1.5 [配置类 (Config)](#15-配置类-config)
   - 1.6 [启动类](#16-启动类)

2. [前端核心代码](#前端核心代码)
   - 2.1 [视图组件 (Views)](#21-视图组件-views)
   - 2.2 [对话框组件 (Dialogs)](#22-对话框组件-dialogs)
   - 2.3 [API接口封装](#23-api接口封装)
   - 2.4 [路由配置](#24-路由配置)
   - 2.5 [主入口文件](#25-主入口文件)

---

## 1. 后端核心代码

### 1.1 控制器层 (Controller)

#### 1.1.1 AuthController.java - 登录认证控制器

```java
package com.lis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录认证控制器
 * 处理用户登录、验证等安全相关功能
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 用户登录接口
     * @param request 登录请求，包含操作员代码和密码
     * @return 登录响应，包含用户信息和权限
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> resp = new HashMap<>();
        
        try {
            String czydm = request.getCzydm();
            String password = request.getPassword();
            
            if (czydm == null || czydm.trim().isEmpty()) {
                resp.put("success", false);
                resp.put("message", "操作员代码不能为空");
                return ResponseEntity.badRequest().body(resp);
            }
            
            if (password == null || password.trim().isEmpty()) {
                resp.put("success", false);
                resp.put("message", "密码不能为空");
                return ResponseEntity.badRequest().body(resp);
            }
            
            // 查询操作员信息
            String sql = "SELECT czydm, czyxm, pym, ksdm, zcdm, sybz, glybz FROM sys_czydm WHERE czydm = ?";
            List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, czydm);
            
            if (users.isEmpty()) {
                resp.put("success", false);
                resp.put("message", "操作员不存在");
                return ResponseEntity.ok(resp);
            }
            
            Map<String, Object> user = users.get(0);
            
            // 检查是否停用
            Object sybzObj = user.get("sybz");
            boolean sybz = sybzObj != null && (sybzObj instanceof Boolean ? (Boolean)sybzObj : "1".equals(sybzObj.toString()));
            if (!sybz) {
                resp.put("success", false);
                resp.put("message", "操作员已停用");
                return ResponseEntity.ok(resp);
            }
            
            // 验证密码（优先使用czymm字段，兼容旧系统kl字段）
            String sqlPwd = "SELECT czydm FROM sys_czydm WHERE czydm = ? AND (czymm = ? OR kl = ?)";
            List<Map<String, Object>> validUsers = jdbcTemplate.queryForList(sqlPwd, czydm, password, password);
            
            if (validUsers.isEmpty()) {
                resp.put("success", false);
                resp.put("message", "密码错误");
                return ResponseEntity.ok(resp);
            }
            
            // 登录成功，返回用户信息
            resp.put("success", true);
            resp.put("message", "登录成功");
            resp.put("user", user);
            
            // 获取用户权限信息
            Map<String, Object> permissions = getUserPermissions(czydm);
            resp.put("permissions", permissions);
            
            return ResponseEntity.ok(resp);
            
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", "登录失败：" + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 获取用户权限信息
     */
    private Map<String, Object> getUserPermissions(String czydm) {
        Map<String, Object> permissions = new HashMap<>();
        
        // 查询菜单权限
        try {
            String menuSql = "SELECT xldm, xlmc FROM sys_czyqx cq " +
                           "JOIN sys_qxxl qx ON cq.qxdm = qx.xldm " +
                           "WHERE cq.czydm = ?";
            List<Map<String, Object>> menuList = jdbcTemplate.queryForList(menuSql, czydm);
            permissions.put("menus", menuList);
        } catch (Exception e) {
            permissions.put("menus", List.of());
        }
        
        // 查询模块权限
        try {
            String moduleSql = "SELECT mkdm, frm_caption FROM sys_czyqx cq " +
                             "JOIN sys_mkb mk ON cq.qxdm = mk.action_name " +
                             "WHERE cq.czydm = ?";
            List<Map<String, Object>> moduleList = jdbcTemplate.queryForList(moduleSql, czydm);
            permissions.put("modules", moduleList);
        } catch (Exception e) {
            permissions.put("modules", List.of());
        }
        
        return permissions;
    }

    /**
     * 登录请求类
     */
    public static class LoginRequest {
        private String czydm;
        private String password;
        
        public String getCzydm() { return czydm; }
        public void setCzydm(String czydm) { this.czydm = czydm; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
```

#### 1.1.2 SampleEntryController.java - 样本录入控制器

```java
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

/**
 * 样本管理 - 样本录入控制器
 * 处理样本的新增、修改、查询等核心业务
 */
@RestController
@RequestMapping({"/sample", "/api/sample"})
public class SampleEntryController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取下一个样本号（yyyyMMdd + 4位流水）
     */
    @GetMapping("/nextSampleNo")
    public ResponseEntity<Map<String, Object>> nextSampleNo(@RequestParam(required = false) String date) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String targetDate = (date == null || date.isEmpty())
                    ? LocalDate.now().toString()
                    : date;

            String prefix = LocalDate.parse(targetDate).format(DateTimeFormatter.BASIC_ISO_DATE);

            String sql = "SELECT MAX(CAST(SUBSTRING(syh, -4) AS UNSIGNED)) " +
                       "FROM bgxt_brxx " +
                       "WHERE syh IS NOT NULL AND LENGTH(syh) = 12 " +
                       "AND syh LIKE ? AND CAST(syh AS UNSIGNED) > 0 " +
                       "AND DATE(jyrq) = DATE(?)";

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
     * 获取某天的病人/样本列表
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

            StringBuilder countSql = new StringBuilder();
            countSql.append("SELECT DISTINCT b.brxx_id FROM bgxt_brxx b WHERE 1=1 ");
            countSql.append("AND DATE(b.jyrq) = DATE(?) ");
            
            List<Object> params = new ArrayList<>();
            params.add(targetDate);

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

            List<Integer> uniqueIds = jdbcTemplate.query(countSql.toString(), params.toArray(), (rs, rowNum) -> {
                return rs.getInt("brxx_id");
            });

            if (uniqueIds.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT b.brxx_id AS id, b.brxx_tmh AS barcode, b.brbh AS patientId, ");
            sql.append("b.brxm AS name, CASE b.brxb WHEN 1 THEN '男' WHEN 2 THEN '女' ELSE '' END AS sex, ");
            sql.append("b.brnl AS age, b.nllx AS ageUnit, b.brlb AS category, b.syqk AS urgency, ");
            sql.append("b.ksdm AS ward, b.brch AS bedNo, b.syh AS sampleNo, ");
            sql.append("CASE b.bbzl WHEN 1 THEN '血清' WHEN 2 THEN '血浆' WHEN 3 THEN '尿液' ");
            sql.append("WHEN 4 THEN '粪便' WHEN 5 THEN '脑脊液' WHEN 6 THEN '胸腹水' ");
            sql.append("WHEN 7 THEN '其它' ELSE CAST(b.bbzl AS CHAR) END AS sampleType, ");
            sql.append("b.bbzl AS sampleTypeCode, b.ybzt AS status ");
            sql.append("FROM bgxt_brxx b WHERE b.brxx_id IN (");
            
            for (int i = 0; i < uniqueIds.size(); i++) {
                sql.append("?");
                if (i < uniqueIds.size() - 1) sql.append(",");
            }
            sql.append(") ORDER BY b.syh ");

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
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveSample(@RequestBody Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> patient = (Map<String, Object>) payload.get("patient");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) payload.get("results");

            if (patient == null) {
                patient = new HashMap<>();
            }

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

            int nllx = 1;
            String ageUnit = (String) patient.getOrDefault("ageUnit", "Y");
            if ("M".equalsIgnoreCase(ageUnit)) {
                nllx = 2;
            } else if ("D".equalsIgnoreCase(ageUnit)) {
                nllx = 3;
            }

            Integer brlb = null;
            String type = (String) patient.getOrDefault("type", "");
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

            Integer syqk = 1;
            String experimentStatus = (String) patient.getOrDefault("experimentStatus", "");
            if ("紧急".equals(experimentStatus)) {
                syqk = 2;
            } else if ("危急".equals(experimentStatus)) {
                syqk = 3;
            }

            String patientId = (String) patient.getOrDefault("patientId", "");
            String barcode = (String) patient.getOrDefault("barcode", "");
            if (type.contains("住院") && (patientId == null || patientId.trim().isEmpty())) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", false);
                resp.put("message", "住院病人必须输入住院号！");
                return ResponseEntity.badRequest().body(resp);
            }

            if (brxxId != null) {
                Integer currentStatus = jdbcTemplate.queryForObject(
                        "SELECT ybzt FROM bgxt_brxx WHERE brxx_id = ?", Integer.class, brxxId);
                if (currentStatus != null && currentStatus == 2) {
                    Map<String, Object> resp = new HashMap<>();
                    resp.put("success", false);
                    resp.put("message", "样本已经审核，无法修改！");
                    return ResponseEntity.badRequest().body(resp);
                }
                if (currentStatus != null && currentStatus == 3) {
                    Map<String, Object> resp = new HashMap<>();
                    resp.put("success", false);
                    resp.put("message", "样本已经打印，无法修改！");
                    return ResponseEntity.badRequest().body(resp);
                }
            }

            String sampleType = (String) patient.getOrDefault("sampleType", "");
            Integer bbzlCode = mapSampleTypeToBbzl(sampleType);
            if (bbzlCode == null) {
                bbzlCode = 1;
            }

            if (brxxId == null) {
                String insertSql = "INSERT INTO bgxt_brxx (brxx_id, brxx_tmh, brbh, brxm, brxb, " +
                               "brnl, nllx, brlb, syh, syqk, ksdm, brch, bbzl, lczd, jyys, " +
                               "shys, jyrq, ybzt, sb_djid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, " +
                               "?, ?, ?, ?, ?, ?, ?, ?, NOW(), 0, ?)";

                Integer maxId = jdbcTemplate.queryForObject("SELECT IFNULL(MAX(brxx_id), 0) FROM bgxt_brxx", Integer.class);
                brxxId = (maxId == null ? 1 : maxId + 1);

                jdbcTemplate.update(insertSql, brxxId, barcode, patientId, name, sex, age, nllx,
                        brlb, sampleNo, syqk, "", (String)patient.getOrDefault("bedNo", ""),
                        bbzlCode, (String)patient.getOrDefault("diagnosis", ""), "", "", null);
            } else {
                String updateSql = "UPDATE bgxt_brxx SET brxx_tmh=?, brbh=?, brxm=?, brxb=?, " +
                               "brnl=?, nllx=?, brlb=?, syh=?, syqk=?, ksdm=?, brch=?, " +
                               "bbzl=?, lczd=? WHERE brxx_id=?";
                jdbcTemplate.update(updateSql, barcode, patientId, name, sex, age, nllx,
                        brlb, sampleNo, syqk, "", (String)patient.getOrDefault("bedNo", ""),
                        bbzlCode, (String)patient.getOrDefault("diagnosis", ""), brxxId);
            }

            if (results != null) {
                jdbcTemplate.update("DELETE FROM bgxt_jyjg WHERE brxx_id = ?", brxxId);
                String insertResultSql = "INSERT INTO bgxt_jyjg (brxx_id, xmid, jyjg) VALUES (?, ?, ?)";
                for (Map<String, Object> r : results) {
                    Object xmidObj = r.get("xmid");
                    if (xmidObj != null) {
                        try {
                            Integer xmid = Integer.parseInt(xmidObj.toString());
                            String jyjg = (String) r.get("result");
                            jdbcTemplate.update(insertResultSql, brxxId, xmid, jyjg);
                        } catch (Exception e) {
                            // 跳过无效的检验项目
                        }
                    }
                }
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "保存成功");
            resp.put("brxx_id", brxxId);
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "保存失败：" + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    private Integer mapSampleTypeToBbzl(String sampleType) {
        if (sampleType == null) return null;
        switch (sampleType.trim()) {
            case "血清": return 1;
            case "血浆": return 2;
            case "尿液": return 3;
            case "粪便": return 4;
            case "脑脊液": return 5;
            case "胸腹水": return 6;
            default: return null;
        }
    }
}
```

#### 1.1.3 QcController.java - 质控管理控制器

```java
package com.lis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 质控管理控制器
 * 处理质控品管理、质控结果录入与分析等功能
 */
@RestController
@RequestMapping("/qc")
public class QcController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取质控品列表
     */
    @GetMapping("/products")
    public ResponseEntity<List<Map<String, Object>>> listQcProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer sbDjid) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT qcp.*, qcpt.ptmc FROM bgxt_qcp qcp ");
            sql.append("LEFT JOIN bgxt_qcpt qcpt ON qcp.ptdm = qcpt.ptdm ");
            sql.append("WHERE 1=1 ");

            List<Object> params = new java.util.ArrayList<>();
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append("AND (qcp.qcmc LIKE ? OR qcp.pym LIKE ?) ");
                params.add("%" + keyword.trim() + "%");
                params.add("%" + keyword.trim() + "%");
            }
            
            if (sbDjid != null) {
                sql.append("AND qcp.sb_djid = ? ");
                params.add(sbDjid);
            }
            
            sql.append("ORDER BY qcp.qcmc");

            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(List.of());
        }
    }

    /**
     * 获取质控结果列表
     */
    @GetMapping("/results")
    public ResponseEntity<List<Map<String, Object>>> listQcResults(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer qcpId) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT qcr.*, qcp.qcmc, qcp.ptdm, qcp.gg, qcp.dw ");
            sql.append("FROM bgxt_qcr qcr JOIN bgxt_qcp qcp ON qcr.qcp_id = qcp.id ");
            sql.append("WHERE 1=1 ");

            List<Object> params = new java.util.ArrayList<>();
            
            if (startDate != null && !startDate.isEmpty()) {
                sql.append("AND DATE(qcr.jyrq) >= DATE(?) ");
                params.add(startDate);
            }
            
            if (endDate != null && !endDate.isEmpty()) {
                sql.append("AND DATE(qcr.jyrq) <= DATE(?) ");
                params.add(endDate);
            }
            
            if (qcpId != null) {
                sql.append("AND qcr.qcp_id = ? ");
                params.add(qcpId);
            }
            
            sql.append("ORDER BY qcr.jyrq DESC");

            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(List.of());
        }
    }

    /**
     * 保存质控结果
     */
    @PostMapping("/results/save")
    public ResponseEntity<Map<String, Object>> saveQcResult(@RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Integer qcpId = (Integer) payload.get("qcpId");
            String result = (String) payload.get("result");
            String jyrq = (String) payload.get("jyrq");
            
            if (qcpId == null) {
                resp.put("success", false);
                resp.put("message", "质控品ID不能为空");
                return ResponseEntity.badRequest().body(resp);
            }
            
            if (result == null || result.trim().isEmpty()) {
                resp.put("success", false);
                resp.put("message", "质控结果不能为空");
                return ResponseEntity.badRequest().body(resp);
            }

            String insertSql = "INSERT INTO bgxt_qcr (qcp_id, jyjg, jyrq, czy) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(insertSql, qcpId, result, jyrq, "admin");

            resp.put("success", true);
            resp.put("message", "保存成功");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", "保存失败：" + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}
```

#### 1.1.4 QueryController.java - 查询统计控制器

```java
package com.lis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询统计控制器
 * 提供标本明细查询和统计功能
 */
@RestController
@RequestMapping("/query")
public class QueryController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 标本明细查询
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
        
        Map<String, Object> resp = new HashMap<>();
        try {
            StringBuilder countSql = new StringBuilder();
            countSql.append("SELECT COUNT(*) FROM bgxt_brxx b WHERE 1=1 ");
            
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT b.*, s.xmzwmc as firstItemName ");
            sql.append("FROM bgxt_brxx b LEFT JOIN bgxt_jyjg j ON b.brxx_id = j.brxx_id ");
            sql.append("LEFT JOIN sys_jyxm s ON j.xmid = s.xmid ");
            sql.append("WHERE 1=1 ");

            List<Object> params = new java.util.ArrayList<>();

            if (startDate != null && !startDate.isEmpty()) {
                countSql.append("AND DATE(b.jyrq) >= DATE(?) ");
                sql.append("AND DATE(b.jyrq) >= DATE(?) ");
                params.add(startDate);
            }
            
            if (endDate != null && !endDate.isEmpty()) {
                countSql.append("AND DATE(b.jyrq) <= DATE(?) ");
                sql.append("AND DATE(b.jyrq) <= DATE(?) ");
                params.add(endDate);
            }

            Integer total = jdbcTemplate.queryForObject(countSql.toString(), Integer.class, params.toArray());
            int offset = (page - 1) * pageSize;
            
            sql.append("GROUP BY b.brxx_id ORDER BY b.jyrq DESC LIMIT ? OFFSET ?");
            params.add(pageSize);
            params.add(offset);

            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());

            resp.put("success", true);
            resp.put("data", list);
            resp.put("total", total);
            resp.put("page", page);
            resp.put("pageSize", pageSize);
            
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", "查询失败：" + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 获取检验工作量统计
     */
    @GetMapping("/stats/workload")
    public ResponseEntity<Map<String, Object>> getWorkloadStats(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String sql = "SELECT " +
                       "SUM(CASE WHEN b.ybzt = 1 THEN 1 ELSE 0 END) as inspected, " +
                       "SUM(CASE WHEN b.ybzt = 2 THEN 1 ELSE 0 END) as audited, " +
                       "SUM(CASE WHEN b.ybzt = 3 THEN 1 ELSE 0 END) as printed, " +
                       "COUNT(*) as total " +
                       "FROM bgxt_brxx b WHERE DATE(b.jyrq) BETWEEN DATE(?) AND DATE(?)";
            
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, startDate, endDate);
            
            if (!result.isEmpty()) {
                resp.put("success", true);
                resp.put("data", result.get(0));
            } else {
                resp.put("success", true);
                resp.put("data", Map.of("inspected", 0, "audited", 0, "printed", 0, "total", 0));
            }
            
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", "统计失败：" + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}
```

#### 1.1.5 DeptSettingController.java - 科室设置控制器

```java
package com.lis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.SysKssz;
import com.lis.mapper.SysKsszMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 科室信息设置控制器
 * 处理科室信息的增删改查
 */
@RestController
@RequestMapping("/basic/dept")
public class DeptSettingController {

    @Autowired
    private SysKsszMapper sysKsszMapper;

    /**
     * 获取科室列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<SysKssz>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean sybz) {
        QueryWrapper<SysKssz> wrapper = new QueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("ksmc", keyword.trim()).or().like("pym", keyword.trim());
        }
        
        if (sybz != null) {
            wrapper.eq("sybz", sybz);
        }
        
        wrapper.orderByAsc("ksdm");
        List<SysKssz> list = sysKsszMapper.selectList(wrapper);
        return ResponseEntity.ok(list);
    }

    /**
     * 保存科室信息
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

            SysKssz existing = sysKsszMapper.selectById(dept.getKsdm());
            if (existing == null) {
                sysKsszMapper.insert(dept);
            } else {
                sysKsszMapper.updateById(dept);
            }

            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    /**
     * 删除科室
     */
    @DeleteMapping("/{ksdm}")
    public ResponseEntity<ApiResponse> delete(@PathVariable String ksdm) {
        try {
            sysKsszMapper.deleteById(ksdm);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail("删除失败：" + e.getMessage()));
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
```

#### 1.1.6 StaffSettingController.java - 人员设置控制器

```java
package com.lis.controller;

import com.lis.entity.SysCzydm;
import com.lis.mapper.SysCzydmMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人员信息设置控制器
 * 处理人员信息的管理
 */
@RestController
@RequestMapping("/basic/staff")
public class StaffSettingController {

    @Autowired
    private SysCzydmMapper sysCzydmMapper;

    /**
     * 获取人员列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String ksdm) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT c.*, k.ksmc FROM sys_czydm c LEFT JOIN sys_kssz k ON c.ksdm = k.ksdm WHERE 1=1 ");
            
            List<Object> params = new java.util.ArrayList<>();
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append("AND (c.czyxm LIKE ? OR c.pym LIKE ?) ");
                params.add("%" + keyword.trim() + "%");
                params.add("%" + keyword.trim() + "%");
            }
            
            if (ksdm != null && !ksdm.trim().isEmpty()) {
                sql.append("AND c.ksdm = ? ");
                params.add(ksdm);
            }
            
            sql.append("ORDER BY c.czydm");

            List<Map<String, Object>> list = sysCzydmMapper.getBaseMapper().getJdbcTemplate().queryForList(sql.toString(), params.toArray());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * 保存人员信息
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> save(@RequestBody StaffSaveRequest req) {
        Map<String, Object> resp = new HashMap<>();
        try {
            if (req.getCzydm() == null || req.getCzydm().trim().isEmpty()) {
                resp.put("success", false);
                resp.put("message", "操作员代码不能为空");
                return ResponseEntity.badRequest().body(resp);
            }
            if (req.getCzyxm() == null || req.getCzyxm().trim().isEmpty()) {
                resp.put("success", false);
                resp.put("message", "操作员姓名不能为空");
                return ResponseEntity.badRequest().body(resp);
            }

            SysCzydm user = new SysCzydm();
            user.setCzydm(req.getCzydm());
            user.setCzyxm(req.getCzyxm());
            user.setPym(req.getPym());
            user.setKsdm(req.getKsdm());
            user.setZcdm(req.getZcdm());
            user.setGlybz(req.getGlybz());
            user.setSybz(req.getSybz() != null ? req.getSybz() : true);

            SysCzydm existing = sysCzydmMapper.selectById(req.getCzydm());
            if (existing == null) {
                // 新增时设置初始密码
                user.setKl("123456");
                user.setCzymm("123456");
                sysCzydmMapper.insert(user);
            } else {
                sysCzydmMapper.updateById(user);
            }

            resp.put("success", true);
            resp.put("message", "保存成功");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", "保存失败：" + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @Data
    public static class StaffSaveRequest {
        private String czydm;
        private String czyxm;
        private String pym;
        private String ksdm;
        private String zcdm;
        private Boolean glybz;
        private Boolean sybz;
    }
}
```

#### 1.1.7 WorkgroupSettingController.java - 工作组设置控制器

```java
package com.lis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.SysGzzsz;
import com.lis.mapper.SysGzzszMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作组别设置控制器
 * 管理工作组信息
 */
@RestController
@RequestMapping("/basic/workgroup")
public class WorkgroupSettingController {

    @Autowired
    private SysGzzszMapper sysGzzszMapper;

    /**
     * 获取工作组列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<SysGzzsz>> list(
            @RequestParam(required = false) String keyword) {
        QueryWrapper<SysGzzsz> wrapper = new QueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("gzzmc", keyword.trim()).or().like("pym", keyword.trim());
        }
        
        wrapper.orderByAsc("gzzdm");
        List<SysGzzsz> list = sysGzzszMapper.selectList(wrapper);
        return ResponseEntity.ok(list);
    }

    /**
     * 保存工作组信息
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

            SysGzzsz workgroup = new SysGzzsz();
            workgroup.setGzzdm(req.getGzzdm());
            workgroup.setGzzmc(req.getGzzmc());
            workgroup.setPym(req.getPym());
            workgroup.setSsksdm(req.getSsksdm());
            workgroup.setGzzlx(req.getGzzlx());
            workgroup.setSybz(req.getSybz() != null ? req.getSybz() : true);

            SysGzzsz existing = sysGzzszMapper.selectById(req.getGzzdm());
            if (existing == null) {
                sysGzzszMapper.insert(workgroup);
            } else {
                sysGzzszMapper.updateById(workgroup);
            }

            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @Data
    public static class SaveWorkgroupRequest {
        private String gzzdm;
        private String gzzmc;
        private String pym;
        private String ssksdm;
        private Integer gzzlx;
        private Boolean sybz;
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
```

#### 1.1.8 InstrumentSettingController.java - 仪器设备设置控制器

```java
package com.lis.controller;

import com.lis.entity.Instrument;
import com.lis.mapper.InstrumentMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪器设备设置控制器
 * 管理仪器设备信息
 */
@RestController
@RequestMapping("/system/instrument")
public class InstrumentSettingController {

    @Autowired
    private InstrumentMapper instrumentMapper;

    /**
     * 获取仪器列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<Instrument>> list(
            @RequestParam(required = false) String keyword) {
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Instrument> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like("sbmc", keyword.trim()).or().like("pym", keyword.trim());
        }
        
        wrapper.orderByAsc("sb_djid");
        List<Instrument> list = instrumentMapper.selectList(wrapper);
        return ResponseEntity.ok(list);
    }

    /**
     * 保存仪器信息
     */
    @PostMapping("/save")
    public ResponseEntity<SaveResult> save(@RequestBody InstrumentSaveRequest req) {
        SaveResult result = new SaveResult();
        try {
            if (req.getSbDjid() == null) {
                result.setSuccess(false);
                result.setMessage("仪器ID不能为空");
                return ResponseEntity.badRequest().body(result);
            }
            if (req.getSbmc() == null || req.getSbmc().trim().isEmpty()) {
                result.setSuccess(false);
                result.setMessage("仪器名称不能为空");
                return ResponseEntity.badRequest().body(result);
            }

            Instrument instrument = new Instrument();
            instrument.setSbDjid(req.getSbDjid());
            instrument.setSbmc(req.getSbmc());
            instrument.setPym(req.getPym());
            instrument.setKsdm(req.getKsdm());
            instrument.setSybz(req.getSybz() != null ? req.getSybz() : true);

            Instrument existing = instrumentMapper.selectById(req.getSbDjid());
            if (existing == null) {
                instrumentMapper.insert(instrument);
            } else {
                instrumentMapper.updateById(instrument);
            }

            result.setSuccess(true);
            result.setMessage("保存成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("保存失败：" + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    @Data
    public static class InstrumentSaveRequest {
        private Integer sbDjid;
        private String sbmc;
        private String pym;
        private String ksdm;
        private Boolean sybz;
    }

    @Data
    public static class SaveResult {
        private Boolean success;
        private String message;
    }
}
```

#### 1.1.9 SystemSettingController.java - 系统设置控制器

```java
package com.lis.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统设置控制器
 * 处理系统参数配置
 */
@RestController
@RequestMapping("/system/setting")
public class SystemSettingController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取系统设置列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list() {
        try {
            String sql = "SELECT id, mc, z, bz FROM sys_xtsz ORDER BY id";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * 保存系统设置
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveSetting(@RequestBody SaveSettingRequest request) {
        try {
            if (request.getId() == null) {
                return ResponseEntity.ok(ApiResponse.fail("设置ID不能为空"));
            }
            if (request.getZ() == null) {
                return ResponseEntity.ok(ApiResponse.fail("设置值不能为空"));
            }

            String sql = "UPDATE sys_xtsz SET z = ?, bz = ? WHERE id = ?";
            jdbcTemplate.update(sql, request.getZ(), request.getBz(), request.getId());

            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @Data
    public static class SaveSettingRequest {
        private Integer id;
        private String z;
        private String bz;
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
```

#### 1.1.10 SystemLockController.java - 系统锁定控制器

```java
package com.lis.controller;

import com.lis.service.SystemLockService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统锁定控制器
 * 提供锁定和解锁接口
 */
@RestController
@RequestMapping("/system/lock")
public class SystemLockController {

    @Autowired
    private SystemLockService systemLockService;

    /**
     * 锁定系统
     */
    @PostMapping("/lock")
    public ResponseEntity<ApiResponse> lockSystem(@RequestBody LockRequest request) {
        try {
            boolean success = systemLockService.lockSystem(request.getUsername(), request.getPassword());
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("系统已锁定"));
            } else {
                return ResponseEntity.ok(ApiResponse.fail("锁定失败，密码错误"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail("锁定失败：" + e.getMessage()));
        }
    }

    /**
     * 解锁系统
     */
    @PostMapping("/unlock")
    public ResponseEntity<ApiResponse> unlockSystem(@RequestBody UnlockRequest request) {
        try {
            boolean success = systemLockService.unlockSystem(request.getPassword());
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("系统已解锁"));
            } else {
                return ResponseEntity.ok(ApiResponse.fail("解锁失败，密码错误"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail("解锁失败：" + e.getMessage()));
        }
    }

    /**
     * 检查系统锁定状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkStatus() {
        Map<String, Object> resp = new HashMap<>();
        try {
            resp.put("locked", systemLockService.isSystemLocked());
            resp.put("lockedBy", systemLockService.getLockedBy());
            resp.put("lockedTime", systemLockService.getLockedTime());
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("error", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @Data
    public static class LockRequest {
        private String username;
        private String password;
    }

    @Data
    public static class UnlockRequest {
        private String password;
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
```

#### 1.1.11 PermissionController.java - 权限控制器

```java
package com.lis.controller;

import com.lis.service.PermissionService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 权限控制器
 * 提供权限刷新接口
 */
@RestController
@RequestMapping("/system/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 刷新用户权限
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshPermission(@RequestBody RefreshRequest request) {
        try {
            Map<String, Object> permissions = permissionService.refreshPermissions(request.getUsername());
            
            ApiResponse response = ApiResponse.success("权限刷新成功");
            response.setData(permissions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail("权限刷新失败：" + e.getMessage()));
        }
    }

    @Data
    public static class RefreshRequest {
        private String username;
    }

    @Data
    public static class ApiResponse {
        private Boolean success;
        private String message;
        private Object data;

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
```

#### 1.1.12 ProcessControlController.java - 流程控制控制器

```java
package com.lis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.BgxtKgkz;
import com.lis.mapper.BgxtKgkzMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程控制设置控制器
 * 管理系统开关控制
 */
@RestController
@RequestMapping("/system/process-control")
public class ProcessControlController {

    @Autowired
    private BgxtKgkzMapper bgxtKgkzMapper;

    /**
     * 获取流程控制列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list() {
        List<BgxtKgkz> list = bgxtKgkzMapper.selectList(new QueryWrapper<>());
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("data", list);
        return ResponseEntity.ok(res);
    }

    /**
     * 保存流程控制设置
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> save(@RequestBody SaveRequest req) {
        Map<String, Object> res = new HashMap<>();
        try {
            upsert(1, req.getSqkg());   // 双签控制开关
            upsert(2, req.getMzsjkg()); // 门诊收据使用控制
            upsert(3, req.getJmjkk());  // 居民健康卡使用控制

            res.put("success", true);
            res.put("message", "设置成功！（需重启程序后生效）");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "设置失败：" + e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    private void upsert(int id, Boolean enabled) {
        BgxtKgkz row = bgxtKgkzMapper.selectById(id);
        int kgz = Boolean.TRUE.equals(enabled) ? 1 : 0;
        if (row == null) {
            BgxtKgkz n = new BgxtKgkz();
            n.setId(id);
            n.setKgz(kgz);
            bgxtKgkzMapper.insert(n);
        } else {
            row.setKgz(kgz);
            bgxtKgkzMapper.updateById(row);
        }
    }

    @Data
    public static class SaveRequest {
        private Boolean sqkg;    // 双签控制
        private Boolean mzsjkg;  // 门诊收据控制
        private Boolean jmjkk;   // 居民健康卡控制
    }
}
```

#### 1.1.13 CommonCodeController.java - 通用编码控制器

```java
package com.lis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.SysTybmzb;
import com.lis.entity.SysTybmmx;
import com.lis.mapper.SysTybmzbMapper;
import com.lis.mapper.SysTybmmxMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用编码设置控制器
 * 支持增加同级和下级，支持修改
 */
@RestController
@RequestMapping({"/system/common-code", "/common/code"})
public class CommonCodeController {

    @Autowired
    private SysTybmzbMapper sysTybmzbMapper;

    @Autowired
    private SysTybmmxMapper sysTybmmxMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取所有编码主表列表
     */
    @GetMapping("/main/list")
    public ResponseEntity<List<Map<String, Object>>> getMainList() {
        try {
            String sql = "SELECT id, bmdm, bmmc, pym, bz, tybz FROM sys_tybmzb ORDER BY id";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    /**
     * 根据编码代码获取明细列表
     */
    @GetMapping("/detail/list")
    public ResponseEntity<List<SysTybmmx>> getDetailList(@RequestParam Integer bmdm) {
        QueryWrapper<SysTybmmx> wrapper = new QueryWrapper<>();
        wrapper.eq("bmdm", bmdm);
        wrapper.orderByAsc("bm");
        List<SysTybmmx> list = sysTybmmxMapper.selectList(wrapper);
        return ResponseEntity.ok(list);
    }

    /**
     * 保存编码主表
     */
    @PostMapping("/main/save")
    public ResponseEntity<ApiResponse> saveMain(@RequestBody SysTybmzb entity) {
        try {
            if (entity.getId() != null) {
                sysTybmzbMapper.updateById(entity);
            } else {
                sysTybmzbMapper.insert(entity);
            }
            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    /**
     * 保存编码明细
     */
    @PostMapping("/detail/save")
    public ResponseEntity<ApiResponse> saveDetail(@RequestBody SaveDetailRequest request) {
        try {
            SysTybmmx entity = request.getDetail();
            if (entity.getId() != null) {
                sysTybmmxMapper.updateById(entity);
                ApiResponse response = ApiResponse.success("保存成功");
                response.setData(entity);
                return ResponseEntity.ok(response);
            } else {
                sysTybmmxMapper.insert(entity);
                ApiResponse response = ApiResponse.success("保存成功");
                response.setData(entity);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    /**
     * 删除编码明细
     */
    @DeleteMapping("/detail/{id}")
    public ResponseEntity<ApiResponse> deleteDetail(@PathVariable Integer id) {
        try {
            sysTybmmxMapper.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("删除失败：" + e.getMessage()));
        }
    }

    @Data
    public static class SaveDetailRequest {
        private SysTybmmx detail;
        private String type;
    }

    @Data
    public static class ApiResponse {
        private Boolean success;
        private String message;
        private Object data;

        public static ApiResponse success(String message) {
            ApiResponse response = new ApiResponse();
            response.setSuccess(true);
            response.setMessage(message);
            return response;
        }

        public static ApiResponse fail(String message) {
            ApiResponse response = new ApiResponse();
            response.setSuccess(false);
            response.setMessage(message);
            return response;
        }
    }
}
```

#### 1.1.14 PasswordChangeController.java - 密码修改控制器

```java
package com.lis.controller;

import com.lis.entity.SysCzydm;
import com.lis.mapper.SysCzydmMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 密码修改控制器
 */
@RestController
@RequestMapping("/system/password")
public class PasswordChangeController {

    @Autowired
    private SysCzydmMapper sysCzydmMapper;

    /**
     * 修改密码
     */
    @PostMapping("/change")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            SysCzydm user = sysCzydmMapper.selectById(request.getCzydm());
            if (user == null) {
                return ResponseEntity.ok(ApiResponse.fail("用户不存在"));
            }

            String oldPassword = user.getCzymm() != null && !user.getCzymm().isEmpty() 
                ? user.getCzymm() 
                : user.getKl();

            if (oldPassword == null || !oldPassword.equals(request.getOldPassword())) {
                return ResponseEntity.ok(ApiResponse.fail("原密码错误"));
            }

            user.setKl(request.getNewPassword());
            user.setCzymm(request.getNewPassword());
            sysCzydmMapper.updateById(user);

            return ResponseEntity.ok(ApiResponse.success("密码修改成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail("密码修改失败：" + e.getMessage()));
        }
    }

    @Data
    public static class ChangePasswordRequest {
        private String czydm;
        private String oldPassword;
        private String newPassword;
    }

    @Data
    public static class ApiResponse {
        private Boolean success;
        private String message;

        public static ApiResponse success(String message) {
            ApiResponse response = new ApiResponse();
            response.setSuccess(true);
            response.setMessage(message);
            return response;
        }

        public static ApiResponse fail(String message) {
            ApiResponse response = new ApiResponse();
            response.setSuccess(false);
            response.setMessage(message);
            return response;
        }
    }
}
```

#### 1.1.15 PatientCategorySettingController.java - 病人类别设置控制器

```java
package com.lis.controller;

import com.lis.entity.SysBrlb;
import com.lis.mapper.SysBrlbMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 病人类别设置控制器
 */
@RestController
@RequestMapping("/basic/patient-category")
public class PatientCategorySettingController {

    @Autowired
    private SysBrlbMapper sysBrlbMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 列表查询
     */
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(
            @RequestParam(required = false) Integer brlb,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean tybz) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT bm, bmsm, pym, xh, tybz FROM sys_brlb WHERE 1=1 ");

            List<Object> params = new ArrayList<>();

            if (brlb != null) {
                sql.append(" AND bm = ? ");
                params.add(brlb);
            }

            if (keyword != null && !keyword.isEmpty()) {
                sql.append(" AND (bmsm LIKE ? OR pym LIKE ?) ");
                params.add("%" + keyword + "%");
                params.add("%" + keyword + "%");
            }

            if (tybz != null) {
                sql.append(" AND tybz = ? ");
                params.add(tybz ? 1 : 0);
            }

            sql.append("ORDER BY xh, bm");

            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * 获取下一个类别代码
     */
    @GetMapping("/next-code")
    public ResponseEntity<Map<String, Object>> getNextCode() {
        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(
                "SELECT MAX(bm) as maxbm FROM sys_brlb");
            Integer maxBm = result.get(0).get("maxbm") != null ? 
                Integer.parseInt(String.valueOf(result.get(0).get("maxbm"))) : 0;

            Map<String, Object> m = new HashMap<>();
            m.put("bm", maxBm + 1);
            return ResponseEntity.ok(m);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> defaultMap = new HashMap<>();
            defaultMap.put("bm", 1);
            return ResponseEntity.ok(defaultMap);
        }
    }

    /**
     * 保存病人类别
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> save(@RequestBody SysBrlb entity) {
        Map<String, Object> res = new HashMap<>();
        try {
            if (entity.getBm() == null) {
                res.put("success", false);
                res.put("message", "类别代码不能为空");
                return ResponseEntity.badRequest().body(res);
            }
            if (entity.getBmsm() == null || entity.getBmsm().trim().isEmpty()) {
                res.put("success", false);
                res.put("message", "类别名称不能为空");
                return ResponseEntity.badRequest().body(res);
            }

            SysBrlb existing = sysBrlbMapper.selectById(entity.getBm());
            if (existing == null) {
                sysBrlbMapper.insert(entity);
            } else {
                sysBrlbMapper.updateById(entity);
            }

            res.put("success", true);
            res.put("message", "保存成功");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("success", false);
            res.put("message", "保存失败：" + e.getMessage());
            return ResponseEntity.status(500).body(res);
        }
    }

    /**
     * 删除病人类别
     */
    @DeleteMapping("/{bm}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer bm) {
        Map<String, Object> res = new HashMap<>();
        try {
            sysBrlbMapper.deleteById(bm);
            res.put("success", true);
            res.put("message", "删除成功");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("success", false);
            res.put("message", "删除失败：" + e.getMessage());
            return ResponseEntity.status(500).body(res);
        }
    }
}
```

### 1.2 实体层 (Entity)

#### 1.2.1 SysCzydm.java - 操作员代码实体

```java
package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 操作员代码表 - 对应旧系统 sys_czydm
 * 用于登录和权限管理
 */
@Data
@TableName("sys_czydm")
public class SysCzydm {
    /**
     * 操作员代码（主键）
     */
    @TableId(type = IdType.INPUT)
    private String czydm;
    
    /**
     * 操作员姓名
     */
    private String czyxm;
    
    /**
     * 拼音码
     */
    private String pym;
    
    /**
     * 科室代码
     */
    private String ksdm;
    
    /**
     * 职称代码
     */
    private String zcdm;
    
    /**
     * 口令（密码）- 旧字段
     */
    private String kl;
    
    /**
     * 操作员密码（czymm）- 用于登录验证
     */
    private String czymm;
    
    /**
     * HIS操作员代码
     */
    private String hisCzydm;
    
    /**
     * 默认输入人
     */
    private String mrsrf;
    
    /**
     * 医生标志
     */
    private Boolean ysbz;
    
    /**
     * 操作员标志
     */
    private Boolean czybz;
    
    /**
     * 管理员标志
     */
    private Boolean glybz;
    
    /**
     * 使用标志（0-使用，1-停用）
     */
    private Boolean sybz;
    
    /**
     * 工作组代码
     */
    private String gzzdm;
    
    /**
     * 电子签名（image类型）
     */
    private byte[] dzqm;
    
    /**
     * 操作员身份证号码
     */
    private String czysfzhm;
}
```

#### 1.2.2 SysKssz.java - 科室设置实体

```java
package com.lis.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 科室设置表 - 对应旧系统 sys_kssz
 */
@Data
@TableName("sys_kssz")
public class SysKssz {
    @TableId
    private Integer ksid;
    
    private String ksdm;
    private String ksmc;
    private String pym;
    private String ksxz;
    private Integer zxbz;
    private Integer sybz;
}
```

#### 1.2.3 SysGzzsz.java - 工作组设置实体

```java
package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 工作组设置表 - 对应旧系统 sys_gzzsz
 */
@Data
@TableName("sys_gzzsz")
public class SysGzzsz {
    /**
     * 自增ID
     */
    private Integer id;
    
    /**
     * 所属科室代码
     */
    private String ssksdm;
    
    /**
     * 工作组代码（主键）
     */
    @TableId(type = IdType.INPUT)
    private String gzzdm;
    
    /**
     * 工作组名称
     */
    private String gzzmc;
    
    /**
     * 拼音码
     */
    private String pym;
    
    /**
     * 工作组类型（1-检验类，2-库房类，3-后勤类）
     */
    private Integer gzzlx;
    
    /**
     * HIS科室代码
     */
    private String hisKsdm;
    
    /**
     * 序号
     */
    private Integer xh;
    
    /**
     * 使用标志（0-停用，1-使用）
     */
    private Boolean sybz;
}
```

#### 1.2.4 Instrument.java - 仪器设备实体

```java
package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 仪器设备设置 - 仪器实体
 * 对应旧系统设备登记表 sys_sbdjb
 */
@Data
@TableName("sys_sbdjb")
public class Instrument {
    /**
     * 设备登记ID（主键）
     */
    @TableId(type = IdType.AUTO)
    private Integer sbDjid;

    /**
     * 设备代码（sbdm）
     */
    private String sbdm;

    /**
     * 设备名称（sbmc）
     */
    private String sbmc;

    /**
     * 设备别名 / 仪器编码（sbbm）
     */
    private String sbbm;

    /**
     * 科室代码（ksdm）
     */
    private String ksdm;

    /**
     * 工作组代码（gzzdm）
     */
    private String gzzdm;

    /**
     * 拼音码（pym）
     */
    private String pym;

    /**
     * 执行标志（zxbz）
     * 1-执行/启用，0-不执行/停用
     */
    private Boolean zxbz;

    /**
     * 停用标志（tybz）
     */
    private Boolean tybz;
}
```

#### 1.2.5 SysJyxm.java - 检验项目实体

```java
package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 检验项目表 - 对应旧系统 sys_jyxm
 */
@Data
@TableName("sys_jyxm")
public class SysJyxm {
    /**
     * 项目ID（主键）
     */
    @TableId(type = IdType.INPUT)
    private Integer xmid;
    
    /**
     * 项目中文名称
     */
    private String xmzwmc;
    
    /**
     * 项目英文名称
     */
    private String xmywmc;
    
    /**
     * 项目单位
     */
    private String xmdw;
    
    /**
     * 拼音码
     */
    private String pym;
    
    /**
     * 项目代码
     */
    private String xmdm;
    
    /**
     * 参考值
     */
    private String ckz;
    
    /**
     * 是否启用
     */
    private Boolean sybz;
}
```

#### 1.2.6 BgxtKgkz.java - 流程控制实体

```java
package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 流程控制/系统开关控制 - 对应旧系统表 bgxt_kgkz
 */
@Data
@TableName("bgxt_kgkz")
public class BgxtKgkz {
    @TableId(type = IdType.INPUT)
    private Integer id;

    private String kgmc;
    private String kgsm;

    /**
     * 开关值（0/1）
     */
    private Integer kgz;
}
```

#### 1.2.7 SysTybmzb.java - 通用编码主表实体

```java
package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 通用编码主表 - 对应旧系统 sys_tybmzb
 */
@Data
@TableName("sys_tybmzb")
public class SysTybmzb {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String bmmc;
    private Integer bmdm;
    private String bmbh;
    private Integer syccbm;
    private String syccmc;
    private Boolean tybz;
}
```

#### 1.2.8 SysTybmmx.java - 通用编码明细实体

```java
package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 通用编码明细表 - 对应旧系统 sys_tybmmx
 */
@Data
@TableName("sys_tybmmx")
public class SysTybmmx {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer bmdm;
    private Integer bm;
    private String bmsm;
    private String szdm;
    private String pym;
    private Boolean mrzbz;
    private Boolean tybz;
    private String bz;
}
```

#### 1.2.9 SysBrlb.java - 病人类别实体

```java
package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 病人类别表 - 对应旧系统 sys_brlb
 */
@Data
@TableName("sys_brlb")
public class SysBrlb {
    @TableId(type = IdType.INPUT)
    private Integer bm;
    
    private String bmsm;
    private String pym;
    private String qtdm;
    private Integer sjlyfs;
    private String sjlyfsms;
    private Boolean mrksbz;
    private String mrksdm;
    private String mrksmc;
    private Boolean mrysbz;
    private String mrysdm;
    private String mrysmc;
    private Integer xh;
    private Boolean tybz;
    private Boolean jkbz;
    private Boolean jgxxBz;
    private String jgxx;
    private Boolean qxkz;
    private String qxmc;
    private Integer brlbys;
}
```

#### 1.2.10 BgxtXtsz.java - 系统设置实体

```java
package com.lis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统设置表 - 对应旧系统 bgxt_xtsz
 */
@Data
@TableName("bgxt_xtsz")
public class BgxtXtsz {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String mc;
    private Integer bz;
    private String sm;
}
```

### 1.3 数据访问层 (Mapper)

#### 1.3.1 SysJyxmMapper.java - 检验项目Mapper

```java
package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysJyxm;
import org.apache.ibatis.annotations.Mapper;

/**
 * 检验项目表 Mapper
 */
@Mapper
public interface SysJyxmMapper extends BaseMapper<SysJyxm> {
}
```

#### 1.3.2 SysCzydmMapper.java - 操作员Mapper

```java
package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysCzydm;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作员代码表 Mapper
 */
@Mapper
public interface SysCzydmMapper extends BaseMapper<SysCzydm> {
}
```

#### 1.3.3 SysKsszMapper.java - 科室设置Mapper

```java
package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysKssz;
import org.apache.ibatis.annotations.Mapper;

/**
 * 科室设置表 Mapper
 */
@Mapper
public interface SysKsszMapper extends BaseMapper<SysKssz> {
}
```

#### 1.3.4 SysGzzszMapper.java - 工作组设置Mapper

```java
package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysGzzsz;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工作组设置表 Mapper
 */
@Mapper
public interface SysGzzszMapper extends BaseMapper<SysGzzsz> {
}
```

#### 1.3.5 InstrumentMapper.java - 仪器设备Mapper

```java
package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.Instrument;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仪器设备表 Mapper
 */
@Mapper
public interface InstrumentMapper extends BaseMapper<Instrument> {
}
```

#### 1.3.6 BgxtKgkzMapper.java - 流程控制Mapper

```java
package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtKgkz;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程控制表 Mapper
 */
@Mapper
public interface BgxtKgkzMapper extends BaseMapper<BgxtKgkz> {
}
```

#### 1.3.7 SysTybmzbMapper.java - 通用编码主表Mapper

```java
package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysTybmzb;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通用编码主表 Mapper
 */
@Mapper
public interface SysTybmzbMapper extends BaseMapper<SysTybmzb> {
}
```

#### 1.3.8 SysTybmmxMapper.java - 通用编码明细Mapper

```java
package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysTybmmx;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通用编码明细表 Mapper
 */
@Mapper
public interface SysTybmmxMapper extends BaseMapper<SysTybmmx> {
}
```

#### 1.3.9 SysBrlbMapper.java - 病人类别Mapper

```java
package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.SysBrlb;
import org.apache.ibatis.annotations.Mapper;

/**
 * 病人类别表 Mapper
 */
@Mapper
public interface SysBrlbMapper extends BaseMapper<SysBrlb> {
}
```

### 1.4 服务层 (Service)

#### 1.4.1 SystemLockService.java - 系统锁定服务

```java
package com.lis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 系统锁定服务
 * 处理系统锁定与解锁功能
 */
@Service
public class SystemLockService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 锁定系统
     * @param username 操作员代码
     * @param password 密码
     * @return 是否锁定成功
     */
    public boolean lockSystem(String username, String password) {
        try {
            // 验证密码
            String sql = "SELECT COUNT(*) FROM sys_czydm WHERE czydm = ? AND (czymm = ? OR kl = ?)";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username, password, password);
            
            if (count == null || count == 0) {
                return false;
            }

            // 创建或更新锁定记录
            String lockSql = "INSERT INTO bgxt_xtsz (id, mc, z, bz) VALUES (100, '系统锁定', ?, ?) " +
                           "ON DUPLICATE KEY UPDATE z = VALUES(z), bz = VALUES(bz)";
            
            String lockedBy = username;
            String lockedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            jdbcTemplate.update(lockSql, "1", lockedBy + "|" + lockedTime);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解锁系统
     * @param password 管理员密码
     * @return 是否解锁成功
     */
    public boolean unlockSystem(String password) {
        try {
            // 验证管理员密码
            String sql = "SELECT COUNT(*) FROM sys_czydm WHERE glybz = 1 AND (czymm = ? OR kl = ?)";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, password, password);
            
            if (count == null || count == 0) {
                return false;
            }

            // 更新锁定记录为未锁定状态
            String unlockSql = "UPDATE bgxt_xtsz SET z = '0', bz = '' WHERE id = 100";
            jdbcTemplate.update(unlockSql);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检查系统是否被锁定
     */
    public boolean isSystemLocked() {
        try {
            String sql = "SELECT z FROM bgxt_xtsz WHERE id = 100";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            
            if (!result.isEmpty()) {
                Object z = result.get(0).get("z");
                return z != null && "1".equals(z.toString());
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取锁定人
     */
    public String getLockedBy() {
        try {
            String sql = "SELECT bz FROM bgxt_xtsz WHERE id = 100";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            
            if (!result.isEmpty()) {
                Object bz = result.get(0).get("bz");
                if (bz != null) {
                    String[] parts = bz.toString().split("\\|");
                    if (parts.length > 0) {
                        return parts[0];
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取锁定时间
     */
    public String getLockedTime() {
        try {
            String sql = "SELECT bz FROM bgxt_xtsz WHERE id = 100";
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            
            if (!result.isEmpty()) {
                Object bz = result.get(0).get("bz");
                if (bz != null) {
                    String[] parts = bz.toString().split("\\|");
                    if (parts.length > 1) {
                        return parts[1];
                    }
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
```

#### 1.4.2 PermissionService.java - 权限服务

```java
package com.lis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限服务
 * 处理用户权限刷新
 */
@Service
public class PermissionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 刷新用户权限
     * @param username 操作员代码
     * @return 用户权限信息
     */
    public Map<String, Object> refreshPermissions(String username) {
        Map<String, Object> permissions = new HashMap<>();
        
        try {
            // 获取用户基本信息
            String userSql = "SELECT czydm, czyxm, pym, ksdm, glybz, czybz, ysbz, gzzdm FROM sys_czydm WHERE czydm = ?";
            List<Map<String, Object>> users = jdbcTemplate.queryForList(userSql, username);
            
            if (!users.isEmpty()) {
                permissions.put("user", users.get(0));
            }

            // 获取菜单权限
            try {
                String menuSql = "SELECT DISTINCT qx.xldm, qx.xlmc FROM sys_czyqx cq " +
                               "JOIN sys_qxxl qx ON cq.qxdm = qx.xldm " +
                               "WHERE cq.czydm = ?";
                List<Map<String, Object>> menus = jdbcTemplate.queryForList(menuSql, username);
                permissions.put("menus", menus);
            } catch (Exception e) {
                permissions.put("menus", List.of());
            }

            // 获取模块权限
            try {
                String moduleSql = "SELECT DISTINCT mk.mkdm, mk.frm_caption FROM sys_czyqx cq " +
                                 "JOIN sys_mkb mk ON cq.qxdm = mk.action_name " +
                                 "WHERE cq.czydm = ?";
                List<Map<String, Object>> modules = jdbcTemplate.queryForList(moduleSql, username);
                permissions.put("modules", modules);
            } catch (Exception e) {
                permissions.put("modules", List.of());
            }

            // 获取科室信息
            try {
                String deptSql = "SELECT ksdm, ksmc FROM sys_kssz WHERE ksdm = (SELECT ksdm FROM sys_czydm WHERE czydm = ?)";
                List<Map<String, Object>> dept = jdbcTemplate.queryForList(deptSql, username);
                permissions.put("department", dept.isEmpty() ? null : dept.get(0));
            } catch (Exception e) {
                permissions.put("department", null);
            }

            // 获取工作组信息
            try {
                String workgroupSql = "SELECT gzzdm, gzzmc FROM sys_gzzsz WHERE gzzdm = (SELECT gzzdm FROM sys_czydm WHERE czydm = ?)";
                List<Map<String, Object>> workgroup = jdbcTemplate.queryForList(workgroupSql, username);
                permissions.put("workgroup", workgroup.isEmpty() ? null : workgroup.get(0));
            } catch (Exception e) {
                permissions.put("workgroup", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return permissions;
    }
}
```

### 1.5 配置类 (Config)

#### 1.5.1 CorsConfig.java - 跨域配置

```java
package com.lis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

### 1.6 启动类

#### 1.6.1 LisApplication.java - 应用启动类

```java
package com.lis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lis.mapper")
public class LisApplication {
    public static void main(String[] args) {
        SpringApplication.run(LisApplication.class, args);
    }
}
```

---

## 2. 前端核心代码

### 2.1 视图组件 (Views)

#### 2.1.1 Login.vue - 登录页面

```vue
<template>
  <div class="login-container">
    <div class="login-box">
      <div class="logo">
        <span>LIS 系统</span>
        <span class="subtitle">实验室信息管理系统</span>
      </div>
      
      <el-form :model="loginForm" ref="loginForm" label-width="80px" class="login-form">
        <el-form-item label="操作员" prop="czydm">
          <el-input 
            v-model="loginForm.czydm" 
            placeholder="请输入操作员代码"
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码"
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="handleLogin" :loading="loading">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div v-if="errorMsg" class="error-message">
        <el-icon><Warning /></el-icon>
        {{ errorMsg }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Warning } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const loading = ref(false)
const errorMsg = ref('')

const loginForm = reactive({
  czydm: '',
  password: ''
})

const handleLogin = async () => {
  if (!loginForm.czydm) {
    errorMsg.value = '请输入操作员代码'
    return
  }
  if (!loginForm.password) {
    errorMsg.value = '请输入密码'
    return
  }
  
  errorMsg.value = ''
  loading.value = true
  
  try {
    const response = await axios.post('/auth/login', {
      czydm: loginForm.czydm,
      password: loginForm.password
    })
    
    if (response.data.success) {
      // 保存用户信息到localStorage
      localStorage.setItem('user', JSON.stringify(response.data.user))
      localStorage.setItem('permissions', JSON.stringify(response.data.permissions))
      
      ElMessage.success('登录成功')
      router.push('/main')
    } else {
      errorMsg.value = response.data.message
    }
  } catch (error) {
    errorMsg.value = '登录失败：' + (error.message || '网络异常')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.logo {
  text-align: center;
  margin-bottom: 30px;
}

.logo span:first-child {
  display: block;
  font-size: 28px;
  font-weight: bold;
  color: #667eea;
}

.logo .subtitle {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
  height: 40px;
  font-size: 16px;
}

.error-message {
  margin-top: 15px;
  padding: 10px 15px;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 4px;
  color: #dc2626;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
```

#### 2.1.2 Main.vue - 主页面框架

```vue
<template>
  <div class="main-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="logo">
        <span>LIS系统 - 实验室报告系统</span>
      </div>
      <div class="user-info">
        <span>当前用户：{{ currentUser?.czyxm || currentUser?.czydm || '未知' }}</span>
        <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>

    <el-container class="main-content">
      <!-- 侧边栏菜单 -->
      <el-aside width="200px" class="sidebar">
        <el-menu
          :default-active="activeMenu"
          router
          class="menu"
        >
          <el-menu-item index="/main/sample">
            <el-icon><Document /></el-icon>
            <span>二、样本管理</span>
          </el-menu-item>
          <el-menu-item index="/main/qc">
            <el-icon><DataAnalysis /></el-icon>
            <span>三、质控管理</span>
          </el-menu-item>
          <el-menu-item index="/main/query">
            <el-icon><Search /></el-icon>
            <span>五、查询统计</span>
          </el-menu-item>
          <el-menu-item index="/system/setting">
            <el-icon><Setting /></el-icon>
            <span>6.1 工程师系统设置</span>
          </el-menu-item>
          <el-menu-item index="/system/lock">
            <el-icon><Lock /></el-icon>
            <span>6.2 系统锁定</span>
          </el-menu-item>
          <el-menu-item index="/system/permission">
            <el-icon><Refresh /></el-icon>
            <span>6.3 刷新权限</span>
          </el-menu-item>
          <el-menu-item index="/system/common-code">
            <el-icon><Document /></el-icon>
            <span>6.4 通用编码设置</span>
          </el-menu-item>
          <el-menu-item index="/system/special-report">
            <el-icon><Files /></el-icon>
            <span>6.5 特殊报告设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-main class="content">
        <router-view v-if="$route.path !== '/main'" />
        <div v-else class="welcome">
          <h1>欢迎使用 LIS 系统</h1>
          <p>请从左侧菜单选择功能模块</p>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Setting, Lock, Refresh, Document, Files, DataAnalysis, Search } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const currentUser = ref(null)

const activeMenu = computed(() => {
  return route.path
})

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    currentUser.value = JSON.parse(userStr)
  } else {
    router.push('/login')
  }
})

const handleLogout = () => {
  localStorage.removeItem('user')
  localStorage.removeItem('permissions')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.main-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.main-content {
  flex: 1;
  overflow: hidden;
}

.sidebar {
  background: #f5f5f5;
  border-right: 1px solid #e0e0e0;
}

.menu {
  border-right: none;
  height: 100%;
}

.content {
  padding: 20px;
  background: #fff;
  overflow-y: auto;
}

.welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #666;
}

.welcome h1 {
  margin-bottom: 20px;
  color: #333;
}

.welcome p {
  font-size: 16px;
}
</style>
```

#### 2.1.3 SystemLock.vue - 系统锁定页面

```vue
<template>
  <div class="system-lock-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>6.2 系统锁定</span>
        </div>
      </template>
      
      <el-alert
        title="操作员离开电脑时，可进行系统锁定，避免他人修改信息"
        type="info"
        :closable="false"
        style="margin-bottom: 20px;"
      />
      
      <div style="text-align: center; padding: 40px 0;">
        <el-button type="primary" size="large" @click="showLockDialog = true" :disabled="isLocked">
          锁定系统
        </el-button>
        <el-button type="warning" size="large" @click="showUnlockDialog = true" :disabled="!isLocked" style="margin-left: 20px;">
          解锁系统
        </el-button>
      </div>
      
      <el-dialog v-model="showLockDialog" title="系统锁定" width="400px" :close-on-click-modal="false">
        <el-form :model="lockForm" label-width="100px">
          <el-form-item label="用户名">
            <el-input v-model="lockForm.username" placeholder="请输入操作员代码" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="lockForm.password" type="password" placeholder="请输入密码" @keyup.enter="handleLock" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showLockDialog = false">取消</el-button>
          <el-button type="primary" @click="handleLock" :loading="locking">确认锁定</el-button>
        </template>
      </el-dialog>
      
      <el-dialog v-model="showUnlockDialog" title="系统解锁" width="400px" :close-on-click-modal="false">
        <el-form :model="unlockForm" label-width="100px">
          <el-form-item label="用户名">
            <el-input v-model="unlockForm.username" placeholder="请输入操作员代码" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="unlockForm.password" type="password" placeholder="请输入密码" @keyup.enter="handleUnlock" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showUnlockDialog = false">取消</el-button>
          <el-button type="primary" @click="handleUnlock" :loading="unlocking">确认解锁</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const locking = ref(false)
const unlocking = ref(false)
const isLocked = ref(false)
const showLockDialog = ref(false)
const showUnlockDialog = ref(false)

const lockForm = reactive({ username: '', password: '' })
const unlockForm = reactive({ username: '', password: '' })

const handleLock = async () => {
  if (!lockForm.username || !lockForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  locking.value = true
  try {
    const response = await axios.post('/api/system/lock/lock', lockForm)
    if (response.data.success) {
      ElMessage.success('系统已锁定')
      isLocked.value = true
      showLockDialog.value = false
      lockForm.username = ''
      lockForm.password = ''
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    ElMessage.error('锁定失败：' + error.message)
  } finally {
    locking.value = false
  }
}

const handleUnlock = async () => {
  if (!unlockForm.username || !unlockForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  unlocking.value = true
  try {
    const response = await axios.post('/api/system/lock/unlock', unlockForm)
    if (response.data.success) {
      ElMessage.success('系统已解锁')
      isLocked.value = false
      showUnlockDialog.value = false
      unlockForm.username = ''
      unlockForm.password = ''
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    ElMessage.error('解锁失败：' + error.message)
  } finally {
    unlocking.value = false
  }
}
</script>

<style scoped>
.system-lock-container { padding: 20px; }
.card-header { font-size: 16px; font-weight: bold; }
</style>
```

### 2.2 对话框组件 (Dialogs)

#### 2.2.1 DeptSettingDialog.vue - 科室设置对话框

```vue
<template>
  <el-dialog :title="title" :visible.sync="visible" width="500px">
    <el-form :model="form" label-width="100px">
      <el-form-item label="科室代码" prop="ksdm">
        <el-input v-model="form.ksdm" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="科室名称" prop="ksmc">
        <el-input v-model="form.ksmc" />
      </el-form-item>
      <el-form-item label="拼音码" prop="pym">
        <el-input v-model="form.pym" />
      </el-form-item>
      <el-form-item label="科室性质" prop="ksxz">
        <el-input v-model="form.ksxz" />
      </el-form-item>
      <el-form-item label="执行标志">
        <el-switch v-model="form.zxbz" />
      </el-form-item>
      <el-form-item label="使用标志">
        <el-switch v-model="form.sybz" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const props = defineProps({
  visible: Boolean,
  editData: Object
})

const emit = defineEmits(['update:visible', 'saved'])

const isEdit = computed(() => !!props.editData)

const title = computed(() => isEdit.value ? '编辑科室' : '新增科室')

const form = ref({
  ksid: null,
  ksdm: '',
  ksmc: '',
  pym: '',
  ksxz: '',
  zxbz: true,
  sybz: true
})

watch(() => props.editData, (val) => {
  if (val) {
    form.value = { ...val }
  }
}, { immediate: true })

const handleCancel = () => {
  emit('update:visible', false)
}

const handleSave = async () => {
  if (!form.value.ksdm) {
    ElMessage.warning('请输入科室代码')
    return
  }
  if (!form.value.ksmc) {
    ElMessage.warning('请输入科室名称')
    return
  }
  
  try {
    await axios.post('/basic/dept/save', form.value)
    ElMessage.success('保存成功')
    emit('update:visible', false)
    emit('saved')
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}
</script>
```

#### 2.2.2 StaffSettingDialog.vue - 人员设置对话框

```vue
<template>
  <el-dialog :title="title" :visible.sync="visible" width="500px">
    <el-form :model="form" label-width="100px">
      <el-form-item label="操作员代码" prop="czydm">
        <el-input v-model="form.czydm" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="操作员姓名" prop="czyxm">
        <el-input v-model="form.czyxm" />
      </el-form-item>
      <el-form-item label="拼音码" prop="pym">
        <el-input v-model="form.pym" />
      </el-form-item>
      <el-form-item label="科室代码" prop="ksdm">
        <el-input v-model="form.ksdm" />
      </el-form-item>
      <el-form-item label="职称代码" prop="zcdm">
        <el-input v-model="form.zcdm" />
      </el-form-item>
      <el-form-item label="管理员">
        <el-switch v-model="form.glybz" />
      </el-form-item>
      <el-form-item label="使用标志">
        <el-switch v-model="form.sybz" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const props = defineProps({
  visible: Boolean,
  editData: Object
})

const emit = defineEmits(['update:visible', 'saved'])

const isEdit = computed(() => !!props.editData)

const title = computed(() => isEdit.value ? '编辑人员' : '新增人员')

const form = ref({
  czydm: '',
  czyxm: '',
  pym: '',
  ksdm: '',
  zcdm: '',
  glybz: false,
  sybz: true
})

watch(() => props.editData, (val) => {
  if (val) {
    form.value = { ...val }
  }
}, { immediate: true })

const handleCancel = () => {
  emit('update:visible', false)
}

const handleSave = async () => {
  if (!form.value.czydm) {
    ElMessage.warning('请输入操作员代码')
    return
  }
  if (!form.value.czyxm) {
    ElMessage.warning('请输入操作员姓名')
    return
  }
  
  try {
    await axios.post('/basic/staff/save', form.value)
    ElMessage.success('保存成功')
    emit('update:visible', false)
    emit('saved')
  } catch (error) {
    ElMessage.error('保存失败：' + error.message)
  }
}
</script>
```

### 2.3 API接口封装

#### 2.3.1 sample.js - 样本管理API

```javascript
import axios from 'axios'

export function fetchPatients(params) {
  return axios.get('/api/sample/patients', { params })
}

export function saveSample(data) {
  return axios.post('/api/sample/save', data)
}

export function refreshPatients(params) {
  return axios.get('/api/sample/patients', { params })
}

export function inspectSample(brxxId) {
  return axios.post(`/api/sample/inspect/${brxxId}`)
}

export function auditSample(brxxId) {
  return axios.post(`/api/sample/audit/${brxxId}`)
}

export function printSample(brxxId) {
  return axios.post(`/api/sample/print/${brxxId}`)
}

export function searchSamples(params) {
  return axios.get('/api/sample/search', { params })
}

export function getNextSampleNo(params) {
  return axios.get('/api/sample/nextSampleNo', { params })
}

export function fetchCombos(params) {
  return axios.get('/api/sample/combos', { params })
}

export function fetchComboItems(zhid, params) {
  return axios.get(`/api/sample/combos/${zhid}/items`, { params })
}

export function fetchResults(brxxId) {
  return axios.get(`/api/sample/results/${brxxId}`)
}

export function fetchReportHtml(brxxId) {
  return axios.get(`/api/sample/report/${brxxId}`, { responseType: 'text' })
}

export function batchAudit(brxxIds) {
  return axios.post('/api/sample/batch/audit', { brxxIds })
}

export function batchPrint(brxxIds) {
  return axios.post('/api/sample/batch/print', { brxxIds })
}

export function batchInvalidate(brxxIds, reason) {
  return axios.post('/api/sample/batch/invalidate', { brxxIds, reason })
}

export function batchUnaudit(brxxIds) {
  return axios.post('/api/sample/batch/unaudit', { brxxIds })
}

export function getProgressStats(date) {
  return axios.get('/api/sample/stats/progress', { params: { date } })
}

export function getSampleIssues(date) {
  return axios.get('/api/sample/sample/issues', { params: { date } })
}

export function handleSampleIssue(data) {
  return axios.post('/api/sample/sample/handle', data)
}
```

#### 2.3.2 qc.js - 质控管理API

```javascript
import axios from 'axios'

export function fetchQcProducts(params) {
  return axios.get('/api/qc/products', { params })
}

export function getQcProduct(zkpid) {
  return axios.get(`/api/qc/products/${zkpid}`)
}

export function addQcProduct(data) {
  return axios.post('/api/qc/products', data)
}

export function updateQcProduct(zkpid, data) {
  return axios.put(`/api/qc/products/${zkpid}`, data)
}

export function deleteQcProduct(zkpid) {
  return axios.delete(`/api/qc/products/${zkpid}`)
}

export function fetchDailyResults(params) {
  return axios.get('/api/qc/daily-results', { params })
}

export function addDailyResult(data) {
  return axios.post('/api/qc/daily-results', data)
}

export function deleteDailyResult(id) {
  return axios.delete(`/api/qc/daily-results/${id}`)
}

export function fetchEvaluations(params) {
  return axios.get('/api/qc/evaluations', { params })
}

export function addEvaluation(data) {
  return axios.post('/api/qc/evaluations', data)
}

export function deleteEvaluation(id) {
  return axios.delete(`/api/qc/evaluations/${id}`)
}

export function fetchQcProjects(zkpid) {
  return axios.get('/api/qc/projects', { params: { zkpid } })
}

export function updateQcProject(zkxmid, data) {
  return axios.put(`/api/qc/projects/${zkxmid}`, data)
}

export function addQcProject(data) {
  return axios.post('/api/qc/projects', data)
}

export function deleteQcProject(id) {
  return axios.delete(`/api/qc/projects/${id}`)
}

export function fetchAvailableProjects(zkpid, sbDjid) {
  return axios.get('/api/qc/available-projects', { params: { zkpid, sbDjid } })
}

export function fetchQcAnalysis(params) {
  return axios.get('/api/qc/analysis', { params })
}

export function fetchAllProjects() {
  return axios.get('/api/qc/all-projects')
}
```

### 2.4 路由配置

#### 2.4.1 router/index.js

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import MainFrame from '../views/MainFrame.vue'
import SampleManagement from '../views/SampleManagement.vue'
import QcManagement from '../views/QcManagement.vue'
import QueryStatistics from '../views/QueryStatistics.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/main',
    name: 'MainFrame',
    component: MainFrame,
    children: [
      {
        path: 'sample',
        name: 'SampleManagement',
        component: SampleManagement
      },
      {
        path: 'qc',
        name: 'QcManagement',
        component: QcManagement
      },
      {
        path: 'query',
        name: 'QueryStatistics',
        component: QueryStatistics
      }
    ]
  },
  { path: '/sample', redirect: '/main/sample' },
  { path: '/qc', redirect: '/main/qc' },
  { path: '/query', redirect: '/main/query' },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
```

### 2.5 主入口文件

#### 2.5.1 main.js

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
```
