package com.lis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping({"/qc", "/api/qc"})
public class QcController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取质控品列表（使用MySQL表 sys_zkpd）
     */
    @GetMapping("/products")
    public ResponseEntity<List<Map<String, Object>>> listQcProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer sbDjid) {
        try {
            StringBuilder sql = new StringBuilder();
            List<Object> params = new ArrayList<>();
            
            // 使用 MySQL 表 sys_zkpd（替代原 SQL Server 表 zk_nyzkp）
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
                row.put("zwmc", rs.getString("zkpmc")); // 别名，兼容前端
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
     * 获取质控品详情（使用MySQL表 sys_zkpd）
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
            Integer sbDjid = null;
            Object sbDjidObj = payload.get("sb_djid");
            if (sbDjidObj != null) {
                sbDjid = Integer.parseInt(sbDjidObj.toString());
            }
            
            String zwmc = (String) payload.getOrDefault("zwmc", "");
            String ywmc = (String) payload.getOrDefault("ywmc", "");
            String zkpsm = (String) payload.getOrDefault("zkpsm", "");
            String ph = (String) payload.getOrDefault("ph", "");
            String sccj = (String) payload.getOrDefault("sccj", "");
            Boolean sybz = true;
            Object sybzObj = payload.get("sybz");
            if (sybzObj != null) {
                sybz = Boolean.TRUE.equals(sybzObj);
            }
            
            if (zwmc == null || zwmc.trim().isEmpty()) {
                resp.put("success", false);
                resp.put("message", "质控品中文名不能为空");
                return ResponseEntity.badRequest().body(resp);
            }
            
            String sql = "INSERT INTO sys_zkpd (sb_djid, zwmc, ywmc, zkpsm, ph, sccj, sybz, syrq) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
            jdbcTemplate.update(sql, sbDjid, zwmc, ywmc, zkpsm, ph, sccj, sybz);
            
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
     * 修改质控品
     */
    @PutMapping("/products/{zkpid}")
    public ResponseEntity<Map<String, Object>> updateQcProduct(@PathVariable Integer zkpid, @RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Integer sbDjid = null;
            Object sbDjidObj = payload.get("sb_djid");
            if (sbDjidObj != null) {
                sbDjid = Integer.parseInt(sbDjidObj.toString());
            }
            
            String zwmc = (String) payload.getOrDefault("zwmc", "");
            String ywmc = (String) payload.getOrDefault("ywmc", "");
            String zkpsm = (String) payload.getOrDefault("zkpsm", "");
            String ph = (String) payload.getOrDefault("ph", "");
            String sccj = (String) payload.getOrDefault("sccj", "");
            Boolean sybz = true;
            Object sybzObj = payload.get("sybz");
            if (sybzObj != null) {
                sybz = Boolean.TRUE.equals(sybzObj);
            }
            
            String sql = "UPDATE sys_zkpd SET sb_djid=?, zwmc=?, ywmc=?, zkpsm=?, ph=?, sccj=?, sybz=? WHERE zkpid=?";
            int rows = jdbcTemplate.update(sql, sbDjid, zwmc, ywmc, zkpsm, ph, sccj, sybz, zkpid);
            
            if (rows > 0) {
                resp.put("success", true);
                resp.put("message", "质控品更新成功");
            } else {
                resp.put("success", false);
                resp.put("message", "质控品不存在");
            }
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 删除质控品
     */
    @DeleteMapping("/products/{zkpid}")
    public ResponseEntity<Map<String, Object>> deleteQcProduct(@PathVariable Integer zkpid) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String sql = "DELETE FROM sys_zkpd WHERE zkpid = ?";
            int rows = jdbcTemplate.update(sql, zkpid);
            
            if (rows > 0) {
                resp.put("success", true);
                resp.put("message", "质控品删除成功");
            } else {
                resp.put("success", false);
                resp.put("message", "质控品不存在");
            }
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

     /**
     * 获取日常质控结果（使用MySQL表 zk_nyzkjg）
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
            
            // 从 zk_nyzkjg 表查询，包含 sybz（有效/无效）和 skbz（失控/在控）
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT j.id, j.zkxmid, j.yssj, j.yhsj, j.jssj, j.sybz, j.skbz, j.jssj as syrq, ");
            sql.append("x.zkpid, x.xmid as xmdm, ");
            sql.append("z.zkpmc ");
            sql.append("FROM zk_nyzkjg j ");
            sql.append("LEFT JOIN zk_nyzkxm x ON j.zkxmid = x.zkxmid ");
            sql.append("LEFT JOIN sys_zkpd z ON x.zkpid = z.zkpid ");
            sql.append("WHERE 1=1 ");
            
            List<Object> params = new ArrayList<>();
            
            if (days != null && days > 0) {
                sql.append("AND j.jssj >= DATE_SUB(CURDATE(), INTERVAL ? DAY) ");
                params.add(days);
                if (zkpid != null) {
                    sql.append("AND x.zkpid = ? ");
                    params.add(zkpid);
                }
            } else {
                sql.append("AND DATE(j.jssj) = DATE(?) ");
                params.add(targetDate);
                if (zkpid != null) {
                    sql.append("AND x.zkpid = ? ");
                    params.add(zkpid);
                }
            }
            
            sql.append("ORDER BY j.jssj DESC, j.id DESC");
            
            final List<Map<String, Object>> list = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("zkxmid", rs.getObject("zkxmid"));
                row.put("zkpid", rs.getObject("zkpid"));
                row.put("xmdm", rs.getString("xmdm"));
                row.put("yssj", rs.getString("yssj"));
                row.put("yhsj", rs.getString("yhsj"));
                row.put("jssj", rs.getDate("jssj"));
                row.put("syrq", rs.getDate("syrq"));
                // sybz: 1=有效, 0=无效
                Object sybzObj = rs.getObject("sybz");
                boolean sybz = sybzObj != null && (sybzObj.equals(1) || sybzObj.toString().equals("1"));
                row.put("sybz", sybz);
                // skbz: 1=失控, 0=在控
                Object skbzObj = rs.getObject("skbz");
                boolean skbz = skbzObj != null && (skbzObj.equals(1) || skbzObj.toString().equals("1"));
                row.put("skbz", skbz);
                row.put("zkpmc", rs.getString("zkpmc"));
                row.put("xmmc", ""); // 项目名称稍后通过xmdm查找
                return row;
            });
            
            // 在Java中查找项目名称
            for (Map<String, Object> item : list) {
                Object xmidObj = item.get("xmdm");
                if (xmidObj != null) {
                    try {
                        String xmidStr = xmidObj.toString();
                        // 尝试从bgxt_xmzh_mx查找
                        String findSql = "SELECT xmzwmc FROM bgxt_xmzh_mx WHERE CAST(id AS CHAR) = ? COLLATE utf8mb4_unicode_ci LIMIT 1";
                        String xmzwmc = jdbcTemplate.queryForObject(findSql, String.class, xmidStr);
                        if (xmzwmc != null) {
                            item.put("xmmc", xmzwmc);
                        }
                    } catch (Exception e) {
                        // 忽略，继续使用空名称
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
     * 录入日常质控结果
     */
    @PostMapping("/daily-results")
    public ResponseEntity<Map<String, Object>> addDailyResult(@RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            System.out.println("=== 质控录入收到的数据 ===");
            System.out.println("payload: " + payload);
            
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
            
            // 从zkxmid获取xmdm（项目代码）
            String xmdm = "";
            if (zkxmid != null) {
                try {
                    Map<String, Object> qcProject = jdbcTemplate.queryForMap(
                        "SELECT xmid FROM zk_nyzkxm WHERE zkxmid = ?", zkxmid);
                    if (qcProject.get("xmid") != null) {
                        xmdm = qcProject.get("xmid").toString();
                    }
                } catch (Exception e) {
                    System.out.println("获取xmdm失败: " + e.getMessage());
                }
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
            
            // 使用jyjg作为检验结果（原始值）
            String yssj = (String) payload.getOrDefault("yssj", "");
            String yhsj = (String) payload.getOrDefault("yhsj", "");
            String resultDate = (String) payload.getOrDefault("resultDate", "");
            
            // 如果有yhsj（核算结果），使用它；否则使用yssj
            String jyjg = yhsj.isEmpty() ? yssj : yhsj;
            
            // sybz默认为true
            boolean sybz = true;
            if (payload.containsKey("sybz")) {
                sybz = Boolean.TRUE.equals(payload.get("sybz"));
            }
            
            // 失控判断逻辑（简化版：基于范围判断）
            int skbz = 0;
            System.out.println("=== 失控判断计算 ===");
            System.out.println("jyjg: " + jyjg);
            try {
                String qcRuleSql = "SELECT bz, bzc, zkdz, zkgz, dx_lx FROM zk_nyzkxm WHERE zkxmid = ?";
                Map<String, Object> qcRule = jdbcTemplate.queryForMap(qcRuleSql, zkxmid);
                
                Object dx_lxObj = qcRule.get("dx_lx");
                Object targetBzObj = qcRule.get("bz");
                Object zkdzObj = qcRule.get("zkdz");
                Object zkgzObj = qcRule.get("zkgz");
                Object bzcObj = qcRule.get("bzc");
                
                System.out.println("dx_lx: " + dx_lxObj + ", bz: " + targetBzObj + ", bzc: " + bzcObj);
                System.out.println("zkdz: " + zkdzObj + ", zkgz: " + zkgzObj);
                
                if (jyjg != null && !jyjg.trim().isEmpty() && targetBzObj != null) {
                    int dx_lx = (dx_lxObj != null) ? Integer.parseInt(dx_lxObj.toString()) : 0;
                    
                    if (dx_lx == 1) {
                        // 定性
                        String targetBz = targetBzObj.toString();
                        if (!jyjg.equals(targetBz)) {
                            skbz = 1;
                        }
                    } else {
                        // 定量
                        double jyjgValue = parseNumericResult(jyjg);
                        double targetValue = parseNumericResult(targetBzObj.toString());
                        double bzc = 0;
                        if (bzcObj != null) {
                            bzc = parseNumericResult(bzcObj.toString());
                        }
                        
                        double zkdz = 0;
                        double zkgz = 0;
                        if (zkdzObj != null) {
                            zkdz = parseNumericResult(zkdzObj.toString());
                        }
                        if (zkgzObj != null) {
                            zkgz = parseNumericResult(zkgzObj.toString());
                        }
                        
                        System.out.println("Parsed: jyjgValue=" + jyjgValue + ", targetValue=" + targetValue + ", bzc=" + bzc + ", zkdz=" + zkdz + ", zkgz=" + zkgz);
                        
                        // 优先使用范围规则判断
                        if (zkdz > 0 || zkgz > 0) {
                            if (jyjgValue < zkdz || jyjgValue > zkgz) {
                                skbz = 1;
                                System.out.println("Range rule: Out of range, 失控!");
                            }
                        } else if (bzc > 0) {
                            // 如果没有范围规则，使用SD规则
                            double diff = Math.abs(jyjgValue - targetValue);
                            double sd = diff / bzc;
                            System.out.println("Using SD rule: diff=" + diff + ", sd=" + sd);
                            if (sd > 2) {
                                skbz = 1;
                                System.out.println("SD > 2, 失控!");
                            }
                        } else {
                            System.out.println("No valid rule found - bzc=0 and zkdz/zkgz=0");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("规则判断失败: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println("Final skbz: " + skbz);
            
            // 保存到 zk_nyzkjg 表（质控结果表）
            // 该表有：id, zkxmid, yssj, yhsj, jssj, sybz, skbz
            // yssj=原始数据, yhsj=核算数据, jssj=检验日期
            String sql;
            Object[] params;
            
            if (resultDate != null && !resultDate.isEmpty()) {
                sql = "INSERT INTO zk_nyzkjg (zkxmid, yssj, yhsj, jssj, sybz, skbz) VALUES (?, ?, ?, ?, ?, ?)";
                params = new Object[]{zkxmid, yssj, yhsj, resultDate, sybz ? 1 : 0, skbz};
            } else {
                sql = "INSERT INTO zk_nyzkjg (zkxmid, yssj, yhsj, jssj, sybz, skbz) VALUES (?, ?, ?, CURDATE(), ?, ?)";
                params = new Object[]{zkxmid, yssj, yhsj, sybz ? 1 : 0, skbz};
            }
            
            jdbcTemplate.update(sql, params);
            
            String message = "质控结果录入成功";
            if (skbz == 1) {
                message = "质控结果录入成功，但结果失控！";
            }
            resp.put("success", true);
            resp.put("message", message);
            resp.put("skbz", skbz);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", "录入失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 删除日常质控结果
     */
    @DeleteMapping("/daily-results/{id}")
    public ResponseEntity<Map<String, Object>> deleteDailyResult(@PathVariable Integer id) {
        Map<String, Object> resp = new HashMap<>();
        try {
            // 从 zk_nyzkjg 表删除
            String sql = "DELETE FROM zk_nyzkjg WHERE id = ?";
            int rows = jdbcTemplate.update(sql, id);
            
            if (rows > 0) {
                resp.put("success", true);
                resp.put("message", "删除成功");
            } else {
                resp.put("success", false);
                resp.put("message", "记录不存在");
            }
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 获取质控评价列表（返回空列表，质控评估功能待完善）
     */
    @GetMapping("/evaluations")
    public ResponseEntity<List<Map<String, Object>>> listEvaluations(
            @RequestParam(required = false) Integer zkpid,
            @RequestParam(required = false) String date) {
        try {
            // 暂时返回空列表，质控评估功能需要根据实际业务需求完善
            return ResponseEntity.ok(new ArrayList<>());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }
    
    /**
     * 获取质控项目列表（使用MySQL表）
     * 如果没有传入zkpid，返回所有项目
     */
    @GetMapping("/projects")
    public ResponseEntity<List<Map<String, Object>>> listQcProjects(@RequestParam(required = false) Integer zkpid) {
        try {
            System.out.println("=== /projects API called with zkpid: " + zkpid);
            
            // 简化查询：先查zk_nyzkxm，再在Java中查找项目名称
            StringBuilder sql = new StringBuilder();
            List<Object> params = new ArrayList<>();
            
            sql.append("SELECT x.zkxmid, x.zkpid, CAST(x.xmid AS UNSIGNED) AS xmid, x.bz, x.bzc, x.zkdz, x.zkgz, x.dx_lx, ");
            sql.append("z.zkpmc ");
            sql.append("FROM zk_nyzkxm x ");
            sql.append("LEFT JOIN sys_zkpd z ON x.zkpid = z.zkpid ");
            sql.append("WHERE 1=1 ");
            
            if (zkpid != null && zkpid > 0) {
                sql.append("AND x.zkpid = ? ");
                params.add(zkpid);
            }
            
            sql.append("ORDER BY x.zkxmid");
            
            System.out.println("Executing SQL: " + sql.toString());
            System.out.println("Params: " + params);
            
            final List<Map<String, Object>> list = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                row.put("zkxmid", rs.getObject("zkxmid"));
                row.put("zkpid", rs.getObject("zkpid"));
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
                row.put("bz", rs.getString("bz"));
                row.put("bzc", rs.getString("bzc"));
                row.put("zkdz", rs.getString("zkdz"));
                row.put("zkgz", rs.getString("zkgz"));
                row.put("dx_lx", rs.getObject("dx_lx"));
                row.put("zkpmc", rs.getString("zkpmc"));
                row.put("xmzwmc", "");
                row.put("xmdw", "");
                return row;
            });
            
            // 在Java中查找项目名称
            for (final Map<String, Object> row : list) {
                Object xmidObj = row.get("xmid");
                if (xmidObj != null) {
                    try {
                        String xmidStr = xmidObj.toString();
                        String findSql = "SELECT xmzwmc, xmdw FROM bgxt_xmzh_mx WHERE CAST(id AS CHAR) = ? COLLATE utf8mb4_unicode_ci LIMIT 1";
                        Map<String, Object> xmInfo = jdbcTemplate.queryForMap(findSql, xmidStr);
                        if (xmInfo.get("xmzwmc") != null) {
                            row.put("xmzwmc", xmInfo.get("xmzwmc"));
                        }
                        if (xmInfo.get("xmdw") != null) {
                            row.put("xmdw", xmInfo.get("xmdw"));
                        }
                        row.put("xmmc", xmInfo.get("xmzwmc"));
                    } catch (Exception e) {
                        // 忽略
                    }
                }
            }
            
            System.out.println("Found " + list.size() + " projects");
            
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }
    
    /**
     * 获取可用项目列表（返回未绑定到该质控品的项目）
     * 优先使用 bgxt_xmzh_mx 表获取项目信息
     */
    @GetMapping("/available-projects")
    public ResponseEntity<List<Map<String, Object>>> listAvailableProjects(@RequestParam(required = false) Integer zkpid, @RequestParam(required = false) Integer sbDjid) {
        try {
            // 如果没有zkpid，返回所有未绑定的项目
            if (zkpid == null || zkpid <= 0) {
                // 返回空列表，要求先选择质控品
                return ResponseEntity.ok(new ArrayList<>());
            }
            
            // 简化查询，直接从bgxt_xmzh_mx获取
            String sql = "SELECT mx.id AS xmid, mx.xmdm, mx.xmzwmc, mx.xmdw " +
                         "FROM bgxt_xmzh_mx mx " +
                         "WHERE mx.id NOT IN (SELECT CAST(xmid AS CHAR) FROM zk_nyzkxm WHERE zkpid = ?) " +
                         "GROUP BY mx.id, mx.xmdm, mx.xmzwmc, mx.xmdw " +
                         "ORDER BY mx.xmzwmc, mx.xmdm LIMIT 200";
            
            List<Map<String, Object>> list = jdbcTemplate.query(sql, new Object[]{zkpid}, (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                Object xmidObj = rs.getObject("xmid");
                row.put("xmid", xmidObj != null ? Integer.parseInt(xmidObj.toString()) : null);
                row.put("xmdm", rs.getString("xmdm"));
                row.put("xmzwmc", rs.getString("xmzwmc"));
                row.put("xmdw", rs.getString("xmdw"));
                // 确保字段不为null
                if (row.get("xmzwmc") == null) row.put("xmzwmc", "");
                if (row.get("xmdw") == null) row.put("xmdw", "");
                if (row.get("xmdm") == null) row.put("xmdm", "");
                return row;
            });
            
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    /**
     * 添加质控项目
     */
    @PostMapping("/projects")
    public ResponseEntity<Map<String, Object>> addQcProject(@RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            System.out.println("=== 添加质控项目收到的数据 ===");
            System.out.println("payload: " + payload);
            
            Integer zkpid = null;
            Object zkpidObj = payload.get("zkpid");
            if (zkpidObj != null) {
                zkpid = Integer.parseInt(zkpidObj.toString());
            }
            
            Integer xmid = null;
            Object xmidObj = payload.get("xmid");
            if (xmidObj != null) {
                xmid = Integer.parseInt(xmidObj.toString());
            }
            
            System.out.println("zkpid: " + zkpid + ", xmid: " + xmid);
            
            if (zkpid == null || xmid == null) {
                resp.put("success", false);
                resp.put("message", "参数不完整: zkpid=" + zkpid + ", xmid=" + xmid);
                return ResponseEntity.badRequest().body(resp);
            }
            
            String bz = (String) payload.getOrDefault("bz", "");
            String bzc = (String) payload.getOrDefault("bzc", "");
            String zkdz = (String) payload.getOrDefault("zkdz", "");
            String zkgz = (String) payload.getOrDefault("zkgz", "");
            Integer dx_lx = 0;
            if (payload.get("dx_lx") != null) {
                try {
                    dx_lx = Integer.parseInt(payload.get("dx_lx").toString());
                } catch (Exception e) {
                    dx_lx = 0;
                }
            }
            Integer fhbz = 0;
            if (payload.get("fhbz") != null) {
                try {
                    fhbz = Integer.parseInt(payload.get("fhbz").toString());
                } catch (Exception e) {
                    fhbz = 0;
                }
            }
            
            System.out.println("准备插入: bz=" + bz + ", bzc=" + bzc + ", zkdz=" + zkdz + ", zkgz=" + zkgz);
            
            String sql = "INSERT INTO zk_nyzkxm (zkpid, xmid, bz, bzc, zkdz, zkgz, dx_lx, fhbz) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, zkpid, xmid, bz, bzc, zkdz, zkgz, dx_lx, fhbz);
            
            resp.put("success", true);
            resp.put("message", "添加成功");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", "添加失败: " + e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 更新质控项目设置（靶值、规则等）
     */
    @PutMapping("/projects/{zkxmid}")
    public ResponseEntity<Map<String, Object>> updateQcProject(@PathVariable Integer zkxmid, @RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String bz = (String) payload.getOrDefault("bz", "");
            String bzc = (String) payload.getOrDefault("bzc", "");
            String zkdz = (String) payload.getOrDefault("zkdz", "");
            String zkgz = (String) payload.getOrDefault("zkgz", "");
            String bc = (String) payload.getOrDefault("bc", "");
            Integer dx_lx = payload.get("dx_lx") != null ? Integer.parseInt(payload.get("dx_lx").toString()) : 0;
            Integer fhbz = payload.get("fhbz") != null ? Integer.parseInt(payload.get("fhbz").toString()) : 0;
            
            String sql = "UPDATE zk_nyzkxm SET bz=?, bzc=?, zkdz=?, zkgz=?, dx_lx=?, fhbz=?, bc=? WHERE zkxmid=?";
            int rows = jdbcTemplate.update(sql, bz, bzc, zkdz, zkgz, dx_lx, fhbz, bc, zkxmid);
            
            if (rows > 0) {
                resp.put("success", true);
                resp.put("message", "更新成功");
            } else {
                resp.put("success", false);
                resp.put("message", "记录不存在");
            }
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 删除质控项目
     */
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Map<String, Object>> deleteQcProject(@PathVariable Integer id) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String sql = "DELETE FROM zk_nyzkxm WHERE zkxmid = ?";
            int rows = jdbcTemplate.update(sql, id);
            
            if (rows > 0) {
                resp.put("success", true);
                resp.put("message", "删除成功");
            } else {
                resp.put("success", false);
                resp.put("message", "记录不存在");
            }
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 获取所有检验项目（用于质控项目选择）
     * 优先使用 bgxt_xmzh_mx 表获取项目信息，因为 sys_jyxm 数据可能不完整
     */
    @GetMapping("/all-projects")
    public ResponseEntity<List<Map<String, Object>>> listAllProjects() {
        try {
            // 优先从 bgxt_xmzh_mx 获取项目信息
            String sql = "SELECT mx.id AS xmid, mx.xmdm, mx.xmzwmc, mx.xmdw " +
                         "FROM bgxt_xmzh_mx mx " +
                         "GROUP BY mx.id, mx.xmdm, mx.xmzwmc, mx.xmdw " +
                         "ORDER BY mx.xmzwmc, mx.xmdm LIMIT 200";
            List<Map<String, Object>> list = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                Object xmidObj = rs.getObject("xmid");
                row.put("xmid", xmidObj != null ? Integer.parseInt(xmidObj.toString()) : null);
                row.put("xmdm", rs.getString("xmdm"));
                row.put("xmzwmc", rs.getString("xmzwmc"));
                row.put("xmdw", rs.getString("xmdw"));
                // 确保字段不为null
                if (row.get("xmzwmc") == null) row.put("xmzwmc", "");
                if (row.get("xmdw") == null) row.put("xmdw", "");
                if (row.get("xmdm") == null) row.put("xmdm", "");
                return row;
            });
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    /**
     * 新增质控评价
     */
    @PostMapping("/evaluations")
    public ResponseEntity<Map<String, Object>> addEvaluation(@RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Integer zkpid = null;
            Object zkpidObj = payload.get("zkpid");
            if (zkpidObj != null) {
                zkpid = Integer.parseInt(zkpidObj.toString());
            }
            
            if (zkpid == null) {
                resp.put("success", false);
                resp.put("message", "请选择质控品");
                return ResponseEntity.badRequest().body(resp);
            }
            
            String pjmd = (String) payload.getOrDefault("pjmd", "");
            String pjjg = (String) payload.getOrDefault("pjjg", "");
            String pjjsyj = (String) payload.getOrDefault("pjjsyj", "");
            String pjczy = (String) payload.getOrDefault("pjczy", "");
            
            String sql = "INSERT INTO zk_nykpj (zkpid, pjmd, pjjg, pjjsyj, pjczy, pjrq) VALUES (?, ?, ?, ?, ?, NOW())";
            jdbcTemplate.update(sql, zkpid, pjmd, pjjg, pjjsyj, pjczy);
            
            resp.put("success", true);
            resp.put("message", "评价成功");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 删除质控评价
     */
    @DeleteMapping("/evaluations/{id}")
    public ResponseEntity<Map<String, Object>> deleteEvaluation(@PathVariable Integer id) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String sql = "DELETE FROM zk_nyzkpj WHERE id = ?";
            int rows = jdbcTemplate.update(sql, id);
            
            if (rows > 0) {
                resp.put("success", true);
                resp.put("message", "删除成功");
            } else {
                resp.put("success", false);
                resp.put("message", "记录不存在");
            }
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 质控分析 - 获取带有失控判断的结果
     */
    @GetMapping("/analysis")
    public ResponseEntity<Map<String, Object>> getQcAnalysis(
            @RequestParam(required = false) Integer zkpid,
            @RequestParam(required = false) Integer zkxmid,
            @RequestParam(required = false) String begDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer days) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 简化查询，避免collation问题
            String sql = ""
                    + "SELECT jg.id, jg.zkxmid, jg.yssj, jg.yhsj, jg.jssj, jg.sybz, jg.skbz, jg.jssj as syrq, "
                    + "x.zkpid, "
                    + "z.zkpmc, "
                    + "x.bz as target_bz, x.bzc as target_bzc, x.zkdz, x.zkgz, x.dx_lx, x.fhbz "
                    + "FROM zk_nyzkjg jg "
                    + "LEFT JOIN zk_nyzkxm x ON jg.zkxmid = x.zkxmid "
                    + "LEFT JOIN sys_zkpd z ON x.zkpid = z.zkpid "
                    + "WHERE 1=1 ";
            
            List<Object> params = new ArrayList<>();
            
            // 日期范围处理
            if (begDate != null && !begDate.isEmpty()) {
                sql += " AND DATEDIFF(jg.jssj, ?) >= 0 ";
                params.add(begDate);
            }
            
            if (endDate != null && !endDate.isEmpty()) {
                sql += " AND DATEDIFF(jg.jssj, ?) <= 0 ";
                params.add(endDate);
            }
            
            // 如果没有指定日期范围，使用天数参数
            if ((begDate == null || begDate.isEmpty()) && (endDate == null || endDate.isEmpty())) {
                int queryDays = (days != null && days > 0) ? days : 30;
                sql += " AND jg.jssj >= DATE_SUB(NOW(), INTERVAL ? DAY) ";
                params.add(queryDays);
            }
            
            if (zkpid != null) {
                sql += " AND x.zkpid = ? ";
                params.add(zkpid);
            }
            
            if (zkxmid != null) {
                sql += " AND jg.zkxmid = ? ";
                params.add(zkxmid);
            }
            
            sql += " ORDER BY jg.jssj DESC";
            
            final List<Map<String, Object>> rawData = jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("zkxmid", rs.getObject("zkxmid"));
                row.put("zkpid", rs.getObject("zkpid"));
                row.put("xmmc", ""); // 稍后在Java中查找
                row.put("xmdw", "");
                row.put("zkpmc", rs.getString("zkpmc"));
                row.put("yssj", rs.getObject("yssj"));
                // yhsj优先，否则使用yssj作为备选（确保趋势图有数据可显示）
                Object yhsjObj = rs.getObject("yhsj");
                if (yhsjObj == null || yhsjObj.toString().trim().isEmpty()) {
                    row.put("yhsj", rs.getObject("yssj"));
                } else {
                    row.put("yhsj", yhsjObj);
                }
                row.put("jssj", rs.getObject("jssj"));
                // sybz: 1=有效, 0=无效
                Object sybzObj = rs.getObject("sybz");
                row.put("sybz", sybzObj != null && (sybzObj.equals(1) || sybzObj.toString().equals("1")));
                // skbz: 1=失控, 0=在控
                Object skbzObj = rs.getObject("skbz");
                row.put("skbz", skbzObj != null && (skbzObj.equals(1) || skbzObj.toString().equals("1")));
                row.put("syrq", rs.getDate("syrq"));
                row.put("target_bz", rs.getObject("target_bz"));
                row.put("target_bzc", rs.getObject("target_bzc"));
                row.put("zkdz", rs.getObject("zkdz"));
                row.put("zkgz", rs.getObject("zkgz"));
                row.put("dx_lx", rs.getObject("dx_lx"));
                row.put("fhbz", rs.getObject("fhbz"));
                return row;
            });
            
            // 在Java中查找项目名称
            for (Map<String, Object> row : rawData) {
                Object zkxmidObj = row.get("zkxmid");
                if (zkxmidObj != null) {
                    try {
                        String findXmSql = "SELECT xmzwmc, xmdw FROM bgxt_xmzh_mx WHERE CAST(id AS CHAR) = ? COLLATE utf8mb4_unicode_ci LIMIT 1";
                        Map<String, Object> xmInfo = jdbcTemplate.queryForMap(findXmSql, zkxmidObj.toString());
                        if (xmInfo.get("xmzwmc") != null) {
                            row.put("xmmc", xmInfo.get("xmzwmc"));
                        }
                        if (xmInfo.get("xmdw") != null) {
                            row.put("xmdw", xmInfo.get("xmdw"));
                        }
                    } catch (Exception e) {
                        // 忽略
                    }
                }
            }
            
            int total = rawData.size();
            int valid = 0;
            int invalid = 0;
            List<Map<String, Object>> processedData = new ArrayList<>();
            
            for (Map<String, Object> item : rawData) {
                // 首先检查是否有效（sybz=1为有效）
                Object sybzObj = item.get("sybz");
                boolean isValid = sybzObj != null && (Boolean.TRUE.equals(sybzObj) || "1".equals(String.valueOf(sybzObj)));
                
                // 只有有效的记录才参与失控统计
                if (!isValid) {
                    // 无效记录不计入失控统计
                    item.put("isOutOfControl", false);
                    item.put("status", "无效");
                    processedData.add(item);
                    continue;
                }
                
                valid++; // 有效记录计数
                
                // 检查失控
                boolean isOutOfControl = false;
                String status = "在控";
                
                Object yhsjObj = item.get("yhsj");
                Object dx_lxObj = item.get("dx_lx");
                Object targetBzObj = item.get("target_bz");
                Object targetBzcObj = item.get("target_bzc");
                Object zkgzObj = item.get("zkgz");
                Object zkdzObj = item.get("zkdz");
                
                if (yhsjObj != null && targetBzObj != null) {
                    String yhsj = yhsjObj.toString();
                    String targetBz = targetBzObj.toString();
                    
                    int dx_lx = (dx_lxObj != null) ? Integer.parseInt(dx_lxObj.toString()) : 0;
                    
                    if (dx_lx == 1) {
                        if (!yhsj.equals(targetBz)) {
                            isOutOfControl = true;
                            status = "失控";
                        }
                    } else {
                        // 定量判断
                        try {
                            double yhsjValue = parseNumericResult(yhsj);
                            double targetValue = parseNumericResult(targetBz);
                            double bzc = 0;
                            if (targetBzcObj != null) {
                                bzc = parseNumericResult(targetBzcObj.toString());
                            }
                            
                            boolean outOfControl = false;
                            
                            // 获取范围值
                            double zkdz = 0;
                            double zkgz = 0;
                            if (zkdzObj != null) {
                                zkdz = parseNumericResult(zkdzObj.toString());
                            }
                            if (zkgzObj != null) {
                                zkgz = parseNumericResult(zkgzObj.toString());
                            }
                            
                            // 优先使用范围规则判断（更准确）
                            if (zkdz > 0 || zkgz > 0) {
                                if (yhsjValue < zkdz || yhsjValue > zkgz) {
                                    outOfControl = true;
                                    System.out.println("Range rule: " + yhsjValue + " < " + zkdz + " or " + yhsjValue + " > " + zkgz + " → 失控");
                                }
                            }
                            
                            // 如果范围规则没判断失控，使用SD规则
                            if (!outOfControl && bzc > 0) {
                                double diff = Math.abs(yhsjValue - targetValue);
                                double sd = diff / bzc;
                                System.out.println("SD rule: diff=" + diff + ", sd=" + sd + ", bzc=" + bzc);
                                // |SD| > 2 则失控（超出±2SD范围）
                                if (sd > 2) {
                                    outOfControl = true;
                                    System.out.println("SD > 2 → 失控");
                                }
                            }
                            
                            if (outOfControl) {
                                isOutOfControl = true;
                                status = "失控";
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                }
                
                if (isOutOfControl) {
                    invalid++;
                    System.out.println(">>> 失控统计: yhsj=" + yhsjObj + ", zkdz=" + zkdzObj + ", zkgz=" + zkgzObj + ", isOutOfControl=" + isOutOfControl);
                }
                
                // 同步skbz字段与isOutOfControl计算结果，确保前端显示一致
                item.put("skbz", isOutOfControl);
                item.put("isOutOfControl", isOutOfControl);
                item.put("status", status);
                processedData.add(item);
            }
            
            // 只使用有效记录数量作为分母计算失控率
            result.put("total", total);
            result.put("valid", valid);
            result.put("invalid", invalid);
            result.put("invalidRate", valid > 0 ? String.format("%.1f", (invalid * 100.0 / valid)) : "0.0");
            result.put("data", processedData);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 获取质控处理记录
     */
    @GetMapping("/processing-records")
    public ResponseEntity<List<Map<String, Object>>> listProcessingRecords(
            @RequestParam(required = false) Integer zkxmid,
            @RequestParam(required = false) String month) {
        try {
            String sql = "SELECT * FROM zk_nyskcl WHERE 1=1 ";
            List<Object> params = new ArrayList<>();
            
            if (zkxmid != null) {
                sql += " AND zkxmid = ?";
                params.add(zkxmid);
            }
            
            if (month != null && !month.isEmpty()) {
                sql += " AND DATEDIFF(?, ksrq) >= 0 AND DATEDIFF(?, ksrq) < 30";
                params.add(month + "-01");
                params.add(month + "-01");
            }
            
            sql += " ORDER BY clrq DESC";
            
            List<Map<String, Object>> list = jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("zkxmid", rs.getObject("zkxmid"));
                row.put("zkcl", rs.getString("zkcl"));
                row.put("czydm_clr", rs.getString("czydm_clr"));
                row.put("ksrq", rs.getTimestamp("ksrq"));
                row.put("jsrq", rs.getTimestamp("jsrq"));
                row.put("clrq", rs.getTimestamp("clrq"));
                return row;
            });
            
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    /**
     * 保存质控处理记录
     */
    @PostMapping("/processing-records")
    public ResponseEntity<Map<String, Object>> saveProcessingRecord(@RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Integer zkxmid = null;
            Object zkxmidObj = payload.get("zkxmid");
            if (zkxmidObj != null) {
                zkxmid = Integer.parseInt(zkxmidObj.toString());
            }
            
            if (zkxmid == null) {
                resp.put("success", false);
                resp.put("message", "参数不完整");
                return ResponseEntity.badRequest().body(resp);
            }
            
            String zkcl = (String) payload.getOrDefault("zkcl", "");
            String czydmClr = (String) payload.getOrDefault("czydm_clr", "");
            String ksrq = (String) payload.getOrDefault("ksrq", "");
            String jsrq = (String) payload.getOrDefault("jsrq", "");
            
            String sql = "INSERT INTO zk_nyskcl (zkxmid, zkcl, czydm_clr, ksrq, jsrq, clrq) VALUES (?, ?, ?, ?, ?, NOW())";
            jdbcTemplate.update(sql, zkxmid, zkcl, czydmClr, ksrq, jsrq);
            
            resp.put("success", true);
            resp.put("message", "保存成功");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    /**
     * 获取质控数据(按项目)
     */
    @GetMapping("/qc-data")
    public ResponseEntity<List<Map<String, Object>>> getQcData(
            @RequestParam Integer zkxmid,
            @RequestParam(required = false) String begDate,
            @RequestParam(required = false) String endDate) {
        try {
            // 简化查询：不直接JOIN bgxt_xmzh_mx，先查数据再在Java中获取项目名称
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT j.id, j.zkxmid, j.yssj, j.yhsj, j.jssj, j.sybz, j.skbz, ");
            sql.append("x.xmid as xmdm ");
            sql.append("FROM zk_nyzkjg j ");
            sql.append("LEFT JOIN zk_nyzkxm x ON j.zkxmid = x.zkxmid ");
            sql.append("WHERE 1=1 ");
            
            List<Object> params = new ArrayList<>();
            
            if (zkxmid != null) {
                sql.append("AND j.zkxmid = ? ");
                params.add(zkxmid);
            }
            
            if (begDate != null && !begDate.isEmpty()) {
                sql.append("AND DATEDIFF(j.jssj, ?) >= 0 ");
                params.add(begDate);
            }
            
            if (endDate != null && !endDate.isEmpty()) {
                sql.append("AND DATEDIFF(j.jssj, ?) <= 0 ");
                params.add(endDate);
            }
            
            sql.append("AND j.sybz = 1 ");  // 只查询有效记录
            sql.append("ORDER BY j.jssj DESC");
            
            System.out.println("=== qc-data SQL ===" + sql);
            System.out.println("Params: " + params);
            
            final List<Map<String, Object>> list = jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("zkxmid", rs.getObject("zkxmid"));
                row.put("yssj", rs.getObject("yssj"));
                row.put("yhsj", rs.getObject("yhsj"));
                row.put("jssj", rs.getObject("jssj"));
                row.put("xmdm", rs.getString("xmdm"));
                // sybz 和 skbz 是 INT 类型（1/0）
                Object sybzObj = rs.getObject("sybz");
                boolean sybz = sybzObj != null && (sybzObj.equals(1) || sybzObj.toString().equals("1"));
                row.put("sybz", sybz);
                Object skbzObj = rs.getObject("skbz");
                boolean skbz = skbzObj != null && (skbzObj.equals(1) || skbzObj.toString().equals("1"));
                row.put("skbz", skbz);
                row.put("syrq", rs.getDate("jssj"));
                return row;
            });
            
            // 在Java中查找项目名称（与 daily-results 保持一致）
            for (Map<String, Object> item : list) {
                Object xmidObj = item.get("xmdm");
                if (xmidObj != null) {
                    try {
                        String xmidStr = xmidObj.toString();
                        String findSql = "SELECT xmzwmc FROM bgxt_xmzh_mx WHERE CAST(id AS CHAR) = ? COLLATE utf8mb4_unicode_ci LIMIT 1";
                        String xmzwmc = jdbcTemplate.queryForObject(findSql, String.class, xmidStr);
                        if (xmzwmc != null) {
                            item.put("xmmc", xmzwmc);
                        }
                    } catch (Exception e) {
                        item.put("xmmc", "");
                    }
                }
            }
            
            System.out.println("qc-data 返回记录数: " + list.size());
            
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }
    
    private double parseNumericResult(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        value = value.trim();
        
        int plusCount = 0;
        int minusCount = 0;
        
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '+') plusCount++;
            else if (c == '-') minusCount++;
        }
        
        if (plusCount > 0 || minusCount > 0) {
            if (plusCount > 0 && minusCount == 0) {
                return plusCount;
            } else if (minusCount > 0 && plusCount == 0) {
                return -minusCount;
            } else if (plusCount > 0 && minusCount > 0) {
                String numericPart = value.replaceAll("[^0-9]", "");
                if (!numericPart.isEmpty()) {
                    return Double.parseDouble(numericPart) * (plusCount > minusCount ? 1 : -1);
                }
            }
        }
        
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * 诊断工具：接收前端导出的调试信息
     */
    @PostMapping("/debug-export")
    public ResponseEntity<Map<String, Object>> receiveDebugInfo(@RequestBody Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            System.out.println("========== 质控模块诊断信息 ==========");
            System.out.println("时间: " + payload.get("timestamp"));
            System.out.println("当前Tab: " + payload.get("activeTab"));
            System.out.println("质控品数量: " + payload.get("productListCount"));
            System.out.println("质控项目数量: " + payload.get("productProjectListCount"));
            System.out.println("日常质控数量: " + payload.get("dailyListCount"));
            System.out.println("质控评价数量: " + payload.get("evaluationListCount"));
            System.out.println("质控分析数据量: " + payload.get("analysisChartDataCount"));
            System.out.println("========================================");
            
            resp.put("success", true);
            resp.put("message", "诊断信息已接收");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }
}
