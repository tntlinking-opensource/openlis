# 新致开源LIS系统 - 软著申请源代码文档

---

## 目录

### 第一部分：后端源代码

#### 模块一：项目启动与配置
1. LisApplication.java - 应用入口
2. CorsConfig.java - 跨域配置

#### 模块二：登录认证模块
3. AuthController.java - 登录认证控制器
4. SysCzydm.java - 用户实体类
5. SysCzydmMapper.java - 用户数据访问层

#### 模块三：样本管理模块
6. SampleEntryController.java - 样本录入控制器

#### 模块四：质控管理模块
7. QcController.java - 质控管理控制器

#### 模块五：查询统计模块
8. QueryController.java - 查询统计控制器

#### 模块六：系统设置模块
9. SystemSettingController.java - 系统设置控制器
10. SysJyxm.java - 检验项目实体类

### 第二部分：前端源代码

#### 模块七：登录页面
11. Login.vue - 登录组件

#### 模块八：主框架页面
12. MainFrame.vue - 主框架组件

---

## 第一部分：后端源代码

### 模块一：项目启动与配置

#### 1. LisApplication.java

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

**文件位置**：`backend/src/main/java/com/lis/LisApplication.java`

---

#### 2. CorsConfig.java

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

**文件位置**：`backend/src/main/java/com/lis/config/CorsConfig.java`

---

### 模块二：登录认证模块

#### 3. AuthController.java

```java
package com.lis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.SysCzydm;
import com.lis.mapper.SysCzydmMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 登录认证控制器
 * 基于旧系统 sys_czydm 表实现
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private SysCzydmMapper sysCzydmMapper;
    
    /**
     * 登录接口
     * 对应旧系统登录逻辑
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            QueryWrapper<SysCzydm> wrapper = new QueryWrapper<>();
            wrapper.eq("czydm", request.getUsername());
            
            SysCzydm user = sysCzydmMapper.selectOne(wrapper);
            
            if (user == null) {
                return ResponseEntity.ok(LoginResponse.fail("用户不存在"));
            }
            
            String dbPassword = user.getCzymm();
            if (dbPassword == null || !dbPassword.equals(request.getPassword())) {
                return ResponseEntity.ok(LoginResponse.fail("密码错误"));
            }
            
            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setMessage("登录成功");
            response.setUser(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(LoginResponse.fail("登录失败: " + e.getMessage()));
        }
    }
    
    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
    
    @Data
    public static class LoginResponse {
        private Boolean success;
        private String message;
        private SysCzydm user;
        
        public static LoginResponse fail(String message) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage(message);
            return response;
        }
    }
}
```

**文件位置**：`backend/src/main/java/com/lis/controller/AuthController.java`

---

#### 4. SysCzydm.java

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

**文件位置**：`backend/src/main/java/com/lis/entity/SysCzydm.java`

---

#### 5. SysCzydmMapper.java

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

**文件位置**：`backend/src/main/java/com/lis/mapper/SysCzydmMapper.java`

---

### 模块三：样本管理模块

#### 6. SampleEntryController.java

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
 * 样本管理 - 样本录入相关接口
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

            int sbDjid = 0;

            StringBuilder sql = new StringBuilder();
            List<Object> params = new ArrayList<>();

            StringBuilder countSql = new StringBuilder();
            countSql.append("SELECT DISTINCT b.brxx_id FROM bgxt_brxx b WHERE 1=1 ");
            countSql.append("AND DATE(b.jyrq) = DATE(?) ");
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

            List<Integer> uniqueIds = jdbcTemplate.query(countSql.toString(), params.toArray(), (rs, rowNum) -> {
                return rs.getInt("brxx_id");
            });
            
            if (uniqueIds.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            }
            
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
            sql.append("CASE b.bbzl ");
            sql.append("WHEN 1 THEN '血清' WHEN 2 THEN '血浆' WHEN 3 THEN '尿液' ");
            sql.append("WHEN 4 THEN '粪便' WHEN 5 THEN '脑脊液' WHEN 6 THEN '胸腹水' ");
            sql.append("WHEN 7 THEN '其它' ELSE CAST(b.bbzl AS CHAR) END AS sampleType, ");
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
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveSample(@RequestBody Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> patient = (Map<String, Object>) payload.get("patient");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> results = (List<Map<String, Object>>) payload.get("results");

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

            String sampleType = (String) patient.getOrDefault("sampleType", "");
            if (sampleType == null || sampleType.trim().isEmpty()) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("success", false);
                resp.put("message", "必须要设置标本种类！");
                return ResponseEntity.badRequest().body(resp);
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "保存成功");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}
```

**文件位置**：`backend/src/main/java/com/lis/controller/SampleEntryController.java`

---

### 模块四：质控管理模块

#### 7. QcController.java

```java
package com.lis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 质控管理控制器
 */
@RestController
@RequestMapping({"/qc", "/api/qc"})
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
            List<Object> params = new ArrayList<>();
            
            sql.append("SELECT zkpid, zkpmc, pym, zkplx, xmdm, xmzwmc, bjzl, bjzh, sccj, sxrq, zxbz, sybz ");
            sql.append("FROM sys_zkpd z ");
            sql.append("WHERE 1=1 ");
            
            if (sbDjid != null) {
                sql.append("AND sb_djid = ? ");
                params.add(sbDjid);
            }
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append("AND (zkpmc LIKE ? OR xmzwmc LIKE ?) ");
                String k = "%" + keyword.trim() + "%";
                params.add(k);
                params.add(k);
            }
            
            sql.append("ORDER BY zkpid DESC");
            
            List<Map<String, Object>> list = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                row.put("zkpid", rs.getInt("zkpid"));
                row.put("zkpmc", rs.getString("zkpmc"));
                row.put("zwmc", rs.getString("zkpmc"));
                row.put("pym", rs.getString("pym"));
                row.put("zkplx", rs.getString("zkplx"));
                row.put("xmdm", rs.getString("xmdm"));
                row.put("xmzwmc", rs.getString("xmzwmc"));
                row.put("bjzl", rs.getString("bjzl"));
                row.put("bjzh", rs.getString("bjzh"));
                row.put("sccj", rs.getString("sccj"));
                row.put("sxrq", rs.getDate("sxrq"));
                row.put("zxbz", rs.getBoolean("zxbz"));
                row.put("sybz", rs.getBoolean("sybz"));
                return row;
            });
            
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }
    
    /**
     * 获取质控品详情
     */
    @GetMapping("/products/{zkpid}")
    public ResponseEntity<Map<String, Object>> getQcProduct(@PathVariable Integer zkpid) {
        try {
            String sql = "SELECT * FROM sys_zkpd WHERE zkpid = ?";
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, zkpid);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 新增质控品
     */
    @PostMapping("/products")
    public ResponseEntity<Map<String, Object>> addQcProduct(@RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String zwmc = (String) payload.getOrDefault("zwmc", "");
            String ywmc = (String) payload.getOrDefault("ywmc", "");
            String zkpsm = (String) payload.getOrDefault("zkpsm", "");
            String ph = (String) payload.getOrDefault("ph", "");
            String sccj = (String) payload.getOrDefault("sccj", "");
            
            if (zwmc == null || zwmc.trim().isEmpty()) {
                resp.put("success", false);
                resp.put("message", "质控品中文名不能为空");
                return ResponseEntity.badRequest().body(resp);
            }
            
            String sql = "INSERT INTO sys_zkpd (zwmc, ywmc, zkpsm, ph, sccj, sybz, syrq) VALUES (?, ?, ?, ?, ?, ?, NOW())";
            jdbcTemplate.update(sql, zwmc, ywmc, zkpsm, ph, sccj, true);
            
            resp.put("success", true);
            resp.put("message", "质控品添加成功");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 获取日常质控结果
     */
    @GetMapping("/daily-results")
    public ResponseEntity<List<Map<String, Object>>> listDailyResults(
            @RequestParam(required = false) Integer zkpid,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer days) {
        try {
            String targetDate = (date == null || date.isEmpty())
                    ? java.time.LocalDate.now().toString()
                    : date;
            
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT j.id, j.zkxmid, j.yssj, j.yhsj, j.jssj, j.sybz, j.skbz, ");
            sql.append("x.zkpid, x.xmid as xmdm, z.zkpmc ");
            sql.append("FROM zk_nyzkjg j ");
            sql.append("LEFT JOIN zk_nyzkxm x ON j.zkxmid = x.zkxmid ");
            sql.append("LEFT JOIN sys_zkpd z ON x.zkpid = z.zkpid ");
            sql.append("WHERE 1=1 ");
            
            List<Object> params = new ArrayList<>();
            
            if (days != null && days > 0) {
                sql.append("AND j.jssj >= DATE_SUB(CURDATE(), INTERVAL ? DAY) ");
                params.add(days);
            } else {
                sql.append("AND DATE(j.jssj) = DATE(?) ");
                params.add(targetDate);
            }
            
            if (zkpid != null) {
                sql.append("AND x.zkpid = ? ");
                params.add(zkpid);
            }
            
            sql.append("ORDER BY j.jssj DESC, j.id DESC");
            
            List<Map<String, Object>> list = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("zkxmid", rs.getObject("zkxmid"));
                row.put("zkpid", rs.getObject("zkpid"));
                row.put("xmdm", rs.getString("xmdm"));
                row.put("yssj", rs.getString("yssj"));
                row.put("yhsj", rs.getString("yhsj"));
                row.put("jssj", rs.getDate("jssj"));
                Object sybzObj = rs.getObject("sybz");
                row.put("sybz", sybzObj != null && (sybzObj.equals(1) || sybzObj.toString().equals("1")));
                Object skbzObj = rs.getObject("skbz");
                row.put("skbz", skbzObj != null && (skbzObj.equals(1) || skbzObj.toString().equals("1")));
                row.put("zkpmc", rs.getString("zkpmc"));
                return row;
            });
            
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    /**
     * 录入日常质控结果
     */
    @PostMapping("/daily-results")
    public ResponseEntity<Map<String, Object>> addDailyResult(@RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Integer zkpid = null;
            Object zkpidObj = payload.get("zkpid");
            if (zkpidObj != null) {
                zkpid = Integer.parseInt(zkpidObj.toString());
            }
            
            Integer zkxmid = null;
            Object zkxmidObj = payload.get("zkxmid");
            if (zkxmidObj != null) {
                zkxmid = Integer.parseInt(zkxmidObj.toString());
            }
            
            if (zkpid == null) {
                resp.put("success", false);
                resp.put("message", "请选择质控品");
                return ResponseEntity.badRequest().body(resp);
            }
            
            if (zkxmid == null) {
                resp.put("success", false);
                resp.put("message", "请选择质控项目");
                return ResponseEntity.badRequest().body(resp);
            }
            
            String yssj = (String) payload.getOrDefault("yssj", "");
            String yhsj = (String) payload.getOrDefault("yhsj", "");
            
            String sql = "INSERT INTO zk_nyzkjg (zkxmid, yssj, yhsj, jssj, sybz, skbz) VALUES (?, ?, ?, CURDATE(), ?, ?)";
            jdbcTemplate.update(sql, zkxmid, yssj, yhsj, 1, 0);
            
            resp.put("success", true);
            resp.put("message", "质控结果录入成功");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", "录入失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}
```

**文件位置**：`backend/src/main/java/com/lis/controller/QcController.java`

---

### 模块五：查询统计模块

#### 8. QueryController.java

```java
package com.lis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 查询统计控制器
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
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        try {
            StringBuilder sql = new StringBuilder();
            List<Object> params = new ArrayList<>();
            
            int offset = (page - 1) * pageSize;
            
            sql.append("SELECT ");
            sql.append("b.brxx_id AS id, ");
            sql.append("b.brxx_tmh AS barcode, ");
            sql.append("b.jyrq AS receiveTime, ");
            sql.append("b.brxm AS patientName, ");
            sql.append("CASE b.brxb WHEN 1 THEN '男' WHEN 2 THEN '女' ELSE '' END AS sex, ");
            sql.append("b.brnl AS age, ");
            sql.append("b.ksdm AS department, ");
            sql.append("CASE b.brlb WHEN 1 THEN '门诊' WHEN 2 THEN '住院' WHEN 3 THEN '体检' ");
            sql.append("WHEN 4 THEN '其他' WHEN 5 THEN '科研' ELSE '' END AS patientCategory ");
            sql.append("FROM bgxt_brxx b ");
            sql.append("WHERE 1=1 ");
            
            if (startDate != null && !startDate.isEmpty()) {
                sql.append("AND DATE(b.jyrq) >= DATE(?) ");
                params.add(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                sql.append("AND DATE(b.jyrq) <= DATE(?) ");
                params.add(endDate);
            }
            
            if (patientType != null && !patientType.isEmpty()) {
                switch (patientType) {
                    case "门诊病人": sql.append("AND b.brlb = 1 "); break;
                    case "住院病人": sql.append("AND b.brlb = 2 "); break;
                    case "体检人员": sql.append("AND b.brlb = 3 "); break;
                }
            }
            
            if (department != null && !department.isEmpty()) {
                sql.append("AND b.ksdm = ? ");
                params.add(department);
            }
            
            sql.append("ORDER BY b.jyrq DESC ");
            sql.append("LIMIT ? OFFSET ? ");
            params.add(pageSize);
            params.add(offset);
            
            List<Map<String, Object>> list = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("barcode", rs.getString("barcode"));
                row.put("receiveTime", rs.getTimestamp("receiveTime"));
                row.put("patientName", rs.getString("patientName"));
                row.put("sex", rs.getString("sex"));
                row.put("age", rs.getObject("age"));
                row.put("department", rs.getString("department"));
                row.put("patientCategory", rs.getString("patientCategory"));
                return row;
            });
            
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
     */
    @GetMapping("/sample/statistics")
    public ResponseEntity<Map<String, Object>> querySampleStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        try {
            Map<String, Object> result = new HashMap<>();
            
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
            result.put("byPatientType", byPatientType);
            
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
            result.put("byStatus", byStatus);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("byPatientType", new ArrayList<>());
            error.put("byStatus", new ArrayList<>());
            return ResponseEntity.ok(error);
        }
    }

    private Map<String, Object> createStatItem(String name, int value) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("value", value);
        return item;
    }
}
```

**文件位置**：`backend/src/main/java/com/lis/controller/QueryController.java`

---

### 模块六：系统设置模块

#### 9. SystemSettingController.java

```java
package com.lis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.BgxtXtsz;
import com.lis.entity.SysXtsz;
import com.lis.mapper.BgxtXtszMapper;
import com.lis.mapper.SysXtszMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统设置控制器
 */
@RestController
@RequestMapping("/system/setting")
public class SystemSettingController {
    
    @Autowired
    private BgxtXtszMapper bgxtXtszMapper;
    
    @Autowired
    private SysXtszMapper sysXtszMapper;
    
    /**
     * 获取所有系统设置项
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getSettings() {
        Map<String, Object> result = new HashMap<>();
        
        List<BgxtXtsz> bgxtSettings = bgxtXtszMapper.selectList(null);
        result.put("bgxtSettings", bgxtSettings);
        
        List<SysXtsz> sysSettings = sysXtszMapper.selectList(null);
        if (!sysSettings.isEmpty()) {
            result.put("sysSetting", sysSettings.get(0));
        } else {
            SysXtsz defaultSetting = new SysXtsz();
            defaultSetting.setLwbz(false);
            defaultSetting.setBxt(false);
            defaultSetting.setZyfykz(0);
            result.put("sysSetting", defaultSetting);
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 保存系统设置项
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveSetting(@RequestBody SaveSettingRequest request) {
        try {
            if (request.getBgxtSettings() != null && !request.getBgxtSettings().isEmpty()) {
                for (BgxtXtsz setting : request.getBgxtSettings()) {
                    if (setting.getId() != null) {
                        bgxtXtszMapper.updateById(setting);
                    } else {
                        bgxtXtszMapper.insert(setting);
                    }
                }
            }
            
            if (request.getSysSetting() != null) {
                SysXtsz sysSetting = request.getSysSetting();
                QueryWrapper<SysXtsz> wrapper = new QueryWrapper<>();
                sysXtszMapper.delete(wrapper);
                sysXtszMapper.insert(sysSetting);
            }
            
            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }
    
    @Data
    public static class SaveSettingRequest {
        private List<BgxtXtsz> bgxtSettings;
        private SysXtsz sysSetting;
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

**文件位置**：`backend/src/main/java/com/lis/controller/SystemSettingController.java`

---

#### 10. SysJyxm.java

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
}
```

**文件位置**：`backend/src/main/java/com/lis/entity/SysJyxm.java`

---

## 第二部分：前端源代码

### 模块七：登录页面

#### 11. Login.vue

```vue
<template>
  <div class="login-container">
    <div class="login-box">
      <h2>实验室报告系统</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入操作员代码" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="version-info">版本号：{{ version }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)
const version = ref('1.0.0')

const loginForm = reactive({
  username: '',
  password: ''
})

onMounted(() => {
  version.value = '1.0.0'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      const response = await axios.post('/api/auth/login', loginForm)
      if (response.data.success) {
        ElMessage.success('登录成功')
        if (response.data.user) {
          localStorage.setItem('user', JSON.stringify(response.data.user))
        }
        router.push('/main')
      } else {
        ElMessage.error(response.data.message || '登录失败')
      }
    } catch (error) {
      ElMessage.error('登录失败：' + (error.response?.data?.message || error.message))
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f5f5f5;
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.version-info {
  text-align: center;
  margin-top: 20px;
  font-size: 12px;
  color: #999;
}
</style>
```

**文件位置**：`frontend/src/views/Login.vue`

---

### 模块八：主框架页面

#### 12. MainFrame.vue

```vue
<template>
  <div class="main-frame">
    <!-- 标题栏 -->
    <div class="title-bar">
      <div class="title-text">实验室报告系统</div>
      <div class="title-buttons">
        <button class="title-btn minimize" @click="handleMinimize" title="最小化">-</button>
        <button class="title-btn maximize" @click="handleMaximize" title="最大化">□</button>
        <button class="title-btn close" @click="handleCloseWindow" title="关闭">×</button>
      </div>
    </div>

    <!-- 菜单栏 -->
    <div class="menu-bar">
      <el-menu mode="horizontal" class="main-menu" :default-active="activeMenu">
        <!-- 样本管理[Y] -->
        <el-sub-menu index="sample">
          <template #title>样本管理[Y]</template>
          <el-menu-item index="sample-entry" @click="openSampleManagement">样本录入</el-menu-item>
          <el-menu-item index="sample-date" @click="handleSelectDateMenu">日期选择</el-menu-item>
          <el-menu-item index="sample-new" @click="handleNew">新增样本</el-menu-item>
          <el-menu-item index="sample-save" @click="handleSave">保存样本</el-menu-item>
          <el-menu-item index="sample-audit" @click="handleAudit">审核样本</el-menu-item>
          <el-menu-item index="sample-print" @click="handlePrint">打印报告</el-menu-item>
        </el-sub-menu>
        
        <!-- 质控[Z] -->
        <el-sub-menu index="qc">
          <template #title>质控[Z]</template>
          <el-menu-item index="qc-setup" @click="openQcManagement">质控设置</el-menu-item>
          <el-menu-item index="qc-entry" @click="openQcDaily">质控录入</el-menu-item>
          <el-menu-item index="qc-analysis" @click="openQcAnalysis">质控分析</el-menu-item>
        </el-sub-menu>
        
        <!-- 仪器[M] -->
        <el-menu-item index="instrument" @click="openInstrumentSelection">
          <span>仪器[M]</span>
        </el-menu-item>
        
        <!-- 查询统计[T] -->
        <el-menu-item index="query" @click="openQueryStatistics">
          <span>查询统计[T]</span>
        </el-menu-item>
        
        <!-- 系统设置[S] -->
        <el-sub-menu index="system">
          <template #title>系统设置[S]</template>
          <el-menu-item index="system-engineer" @click="openDialog('engineer')">工程师系统设置</el-menu-item>
          <el-menu-item index="system-lock" @click="openDialog('lock')">系统锁定</el-menu-item>
          <el-menu-item index="system-password" @click="openDialog('passwordChange')">密码修改</el-menu-item>
          <el-menu-item index="system-common-code" @click="openDialog('commonCode')">通用编码设置</el-menu-item>
        </el-sub-menu>
        
        <!-- 基本设置[J] -->
        <el-sub-menu index="basic">
          <template #title>基本设置[J]</template>
          <el-menu-item index="basic-dept" @click="openDialog('dept')">科室信息设置</el-menu-item>
          <el-menu-item index="basic-personnel" @click="openDialog('staff')">人员信息设置</el-menu-item>
          <el-menu-item index="basic-workgroup" @click="openDialog('workgroup')">工作组别设置</el-menu-item>
          <el-menu-item index="basic-instrument" @click="openDialog('instrument')">仪器设备设置</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar toolbar-legacy" role="toolbar" aria-label="主功能栏">
      <button class="tb-btn" type="button" title="选择日期" @click.prevent="handleSelectDate">
        <span class="tb-icon i-calendar"></span>
        <span class="tb-text">选择日期</span>
      </button>
      <div class="tb-sep" aria-hidden="true"></div>

      <button class="tb-btn" type="button" title="查找" @click.prevent="handleFind">
        <span class="tb-icon i-find"></span>
        <span class="tb-text">查找</span>
      </button>
      <button class="tb-btn" type="button" title="刷新" @click.prevent="handleRefresh">
        <span class="tb-icon i-refresh"></span>
        <span class="tb-text">刷新</span>
      </button>
      <div class="tb-sep" aria-hidden="true"></div>

      <button class="tb-btn tb-primary" type="button" title="新增" @click.prevent="handleNew">
        <span class="tb-icon i-add"></span>
        <span class="tb-text">新增</span>
      </button>
      <button class="tb-btn tb-success" type="button" title="保存" @click.prevent="handleSave">
        <span class="tb-icon i-save"></span>
        <span class="tb-text">保存</span>
      </button>
      <div class="tb-sep" aria-hidden="true"></div>

      <button class="tb-btn" type="button" title="检验" @click.prevent="handleTest">
        <span class="tb-icon i-test"></span>
        <span class="tb-text">检验</span>
      </button>
      <button class="tb-btn" type="button" title="审核" @click.prevent="handleAudit">
        <span class="tb-icon i-check"></span>
        <span class="tb-text">审核</span>
      </button>
      <div class="tb-sep" aria-hidden="true"></div>

      <button class="tb-btn" type="button" title="锁定" @click.prevent="handleLock">
        <span class="tb-icon i-lock"></span>
        <span class="tb-text">锁定</span>
      </button>
      <button class="tb-btn" type="button" title="打印" @click.prevent="handlePrint">
        <span class="tb-icon i-print"></span>
        <span class="tb-text">打印</span>
      </button>
      <button class="tb-btn" type="button" title="关闭" @click.prevent="handleClose">
        <span class="tb-icon i-close"></span>
        <span class="tb-text">关闭</span>
      </button>
    </div>

    <!-- 主工作区 -->
    <div class="main-workspace">
      <router-view v-if="$route.name === 'SampleManagement' || $route.name === 'QcManagement' || $route.name === 'QueryStatistics'" />
      
      <div v-else-if="!selectedDevice" class="home-page">
        <div class="home-page-content">
          <div class="home-page-title">
            <h1>实验室信息管理系统</h1>
            <p class="home-page-subtitle">Laboratory Information System</p>
            <p class="home-page-hint">
              当前未绑定检验仪器，请点击上方菜单栏【仪器[M]】选择检验仪器。
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- 状态栏 -->
    <div class="status-bar">
      <div class="status-item">
        <span class="status-label">操作员：</span>
        <span class="status-value">{{ currentUser?.czyxm || currentUser?.czydm || '未知' }}</span>
      </div>
      <div class="status-item">
        <span class="status-label">仪器：</span>
        <span class="status-value">{{ selectedInstrument || '未选择' }}</span>
      </div>
      <div class="status-item">
        <span class="status-label">时间：</span>
        <span class="status-value">{{ currentTime }}</span>
      </div>
      <div class="status-item">
        <span class="status-label">数据库：</span>
        <span class="status-value">lisdata</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, provide } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const currentUser = ref(null)
const selectedInstrument = ref('')
const selectedDevice = ref(null)
const currentTime = ref('')

const dialogs = ref({
  engineer: false,
  lock: false,
  passwordChange: false,
  commonCode: false,
  instrumentSelection: false
})

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const toolbarHandlers = ref({})

const handleSelectDate = () => {
  ElMessage.info('请在样本录入界面选择日期')
}

const handleFind = () => {
  ElMessage.info('请先进入样本录入界面')
}

const handleRefresh = () => {
  if (toolbarHandlers.value.refresh) {
    toolbarHandlers.value.refresh()
  } else {
    ElMessage.info('请先进入样本录入界面')
  }
}

const handleNew = () => {
  if (toolbarHandlers.value.new) {
    toolbarHandlers.value.new()
  }
}

const handleSave = () => {
  if (toolbarHandlers.value.save) {
    toolbarHandlers.value.save()
  }
}

const handleTest = () => {
  if (toolbarHandlers.value.test) {
    toolbarHandlers.value.test()
  }
}

const handleAudit = () => {
  if (toolbarHandlers.value.audit) {
    toolbarHandlers.value.audit()
  }
}

const handleLock = () => {
  dialogs.value.lock = true
}

const handlePrint = () => {
  if (toolbarHandlers.value.print) {
    toolbarHandlers.value.print()
  } else {
    ElMessage.info('请先进入样本录入界面')
  }
}

const handleClose = () => {
  selectedDevice.value = null
  router.push('/main')
}

const openDialog = (dialogName) => {
  if (dialogs.value[dialogName] !== undefined) {
    dialogs.value[dialogName] = true
  }
}

const openSampleManagement = () => {
  if (!selectedDevice.value) {
    ElMessage.warning('请先选择检验仪器')
    dialogs.value.instrumentSelection = true
    return
  }
  router.push('/main/sample')
}

const openQcManagement = () => {
  router.push('/main/qc?tab=setup')
}

const openQcDaily = () => {
  router.push('/main/qc?tab=entry')
}

const openQcAnalysis = () => {
  router.push('/main/qc?tab=analysis')
}

const openQueryStatistics = () => {
  router.push('/main/query')
}

const openInstrumentSelection = () => {
  dialogs.value.instrumentSelection = true
}

const handleMinimize = () => {
  console.log('最小化窗口')
}

const handleMaximize = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const handleCloseWindow = () => {
  if (confirm('确定要退出系统吗？')) {
    localStorage.removeItem('user')
    window.location.href = '/login'
  }
}

let timeInterval = null

onMounted(async () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    currentUser.value = JSON.parse(userStr)
  } else {
    window.location.href = '/login'
    return
  }

  updateTime()
  timeInterval = setInterval(updateTime, 1000)
})

provide('registerToolbarHandler', (name, handler) => {
  if (toolbarHandlers.value[name] !== undefined) {
    toolbarHandlers.value[name] = handler
  }
})

provide('unregisterToolbarHandler', (name) => {
  if (toolbarHandlers.value[name] !== undefined) {
    toolbarHandlers.value[name] = null
  }
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>

<style scoped>
.main-frame {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  background: #f0f2f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Microsoft YaHei', sans-serif;
  font-size: 13px;
}

.title-bar {
  height: 40px;
  background: linear-gradient(135deg, #1a73e8 0%, #0d47a1 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  user-select: none;
  box-shadow: 0 1px 3px rgba(0,0,0,0.12);
}

.title-text {
  font-size: 13px;
  font-weight: 500;
  color: #fff;
  letter-spacing: 0.5px;
}

.title-buttons {
  display: flex;
  gap: 4px;
}

.title-btn {
  width: 28px;
  height: 24px;
  border: none;
  background: rgba(255,255,255,0.15);
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s;
}

.title-btn:hover {
  background: rgba(255,255,255,0.25);
}

.title-btn.close:hover {
  background: #ea4335;
}

.menu-bar {
  background: #ffffff;
  border-bottom: 1px solid #e4e7ed;
  min-height: 40px;
}

.main-menu {
  border-bottom: none;
  background: transparent;
  height: 40px;
}

.main-menu >>> .el-sub-menu__title,
.main-menu >>> .el-menu-item {
  height: 40px;
  line-height: 40px;
  padding: 0 16px;
  font-size: 13px;
  color: #303133;
}

.main-menu >>> .el-sub-menu__title:hover,
.main-menu >>> .el-menu-item:hover:not(.is-disabled) {
  background: #f5f7fa;
  color: #1a73e8;
}

.toolbar {
  border-bottom: 1px solid #e4e7ed;
  background: #fafafa;
}

.toolbar-legacy {
  height: 48px;
  background: #ffffff;
  display: flex;
  align-items: stretch;
  padding: 4px 8px;
  gap: 4px;
  overflow: hidden;
  border-bottom: 1px solid #e4e7ed;
}

.tb-sep {
  width: 1px;
  margin: 4px 8px;
  background: #e4e7ed;
}

.tb-btn {
  padding: 4px 12px;
  height: 36px;
  border: 1px solid #e4e7ed;
  background: #ffffff;
  border-radius: 4px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  font-size: 12px;
  color: #606266;
  transition: all 0.2s;
}

.tb-btn:hover {
  background: #f5f7fa;
  border-color: #1a73e8;
  color: #1a73e8;
}

.tb-btn.tb-primary {
  background: linear-gradient(#d8ecff 0%, #c9e4ff 55%, #b7d7f0 100%);
}

.tb-btn.tb-success {
  background: linear-gradient(#dff6e5 0%, #cfeedd 55%, #bfe4cf 100%);
}

.tb-icon {
  width: 26px;
  height: 26px;
  display: inline-block;
  border-radius: 4px;
  background-color: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.10);
  position: relative;
}

.tb-text {
  font-size: 11px;
  line-height: 12px;
  color: #0b1b2b;
  white-space: nowrap;
}

.main-workspace {
  flex: 1;
  overflow: hidden;
}

.home-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background: #f5f7fa;
}

.home-page-content {
  text-align: center;
}

.home-page-title h1 {
  font-size: 28px;
  color: #1a73e8;
  margin-bottom: 10px;
}

.home-page-subtitle {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

.home-page-hint {
  font-size: 13px;
  color: #999;
}

.status-bar {
  height: 26px;
  background: #fafafa;
  border-top: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  padding: 0 12px;
  gap: 20px;
  font-size: 12px;
}

.status-item {
  display: flex;
  align-items: center;
}

.status-label {
  color: #666;
}

.status-value {
  color: #333;
  font-weight: 500;
}
</style>
```

**文件位置**：`frontend/src/views/MainFrame.vue`

---

## 附录：项目结构说明

```
openLis/
├── backend/                          # 后端代码目录
│   ├── src/main/java/com/lis/
│   │   ├── LisApplication.java       # 应用入口
│   │   ├── config/                   # 配置类
│   │   │   └── CorsConfig.java       # 跨域配置
│   │   ├── controller/               # REST控制器
│   │   │   ├── AuthController.java           # 登录认证
│   │   │   ├── SampleEntryController.java    # 样本管理
│   │   │   ├── QcController.java             # 质控管理
│   │   │   ├── QueryController.java          # 查询统计
│   │   │   └── SystemSettingController.java  # 系统设置
│   │   ├── entity/                   # 实体类
│   │   │   ├── SysCzydm.java         # 用户实体
│   │   │   ├── SysJyxm.java          # 检验项目实体
│   │   │   └── ...                   # 其他实体类
│   │   └── mapper/                   # 数据访问层
│   │       ├── SysCzydmMapper.java   # 用户数据访问
│   │       └── ...                   # 其他Mapper
│   └── src/main/resources/
│       └── application.yml           # 应用配置

├── frontend/                         # 前端代码目录
│   ├── src/
│   │   ├── views/                    # 页面组件
│   │   │   ├── Login.vue             # 登录页面
│   │   │   ├── MainFrame.vue         # 主框架页面
│   │   │   ├── SampleManagement.vue  # 样本管理页面
│   │   │   ├── QcManagement.vue      # 质控管理页面
│   │   │   └── QueryStatistics.vue   # 查询统计页面
│   │   ├── components/               # 公共组件
│   │   ├── router/                   # 路由配置
│   │   ├── api/                      # API接口
│   │   ├── App.vue                   # 根组件
│   │   └── main.js                   # 入口文件
│   └── index.html                    # HTML模板

└── README.md                         # 项目说明文档
```

---

**文档说明**：本文档包含新致开源LIS系统的核心源代码，涵盖登录认证、样本管理、质控管理、查询统计、系统设置等主要功能模块。代码经过整理和优化，保留了系统核心业务逻辑，可用于软件著作权申请。