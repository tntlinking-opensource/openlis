package com.lis.controller;

import com.lis.entity.SysBrlb;
import com.lis.mapper.SysBrlbMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
 * 7.5 病人类别设置
 * 参考旧系统 p_brlbsz + 存储过程 sys_se_brlbxx（直接操作表 sys_brlb，无 sys_in_brlbxx）
 */
@RestController
@RequestMapping("/basic/patient-category")
public class PatientCategorySettingController {

    @Autowired
    private SysBrlbMapper sysBrlbMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 列表查询（MySQL直接查询）
     */
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(
            @RequestParam(required = false) Integer brlb,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean tybz) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT bm, bmsm, pym, xh, tybz ");
            sql.append("FROM sys_brlb WHERE 1=1 ");
            
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
            return ResponseEntity.ok(Collections.<Map<String, Object>>emptyList());
        }
    }

    /**
     * 获取下一个类别代码（MySQL查询）
     */
    @GetMapping("/next-code")
    public ResponseEntity<Map<String, Object>> getNextCode() {
        try {
            // 获取最大bm然后+1
            List<Map<String, Object>> result = jdbcTemplate.queryForList(
                "SELECT MAX(bm) as maxbm FROM sys_brlb");
            Integer maxBm = result.get(0).get("maxbm") != null ? 
                Integer.parseInt(String.valueOf(result.get(0).get("maxbm"))) : 0;
            
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("bm", maxBm + 1);
            return ResponseEntity.ok(m);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> defaultMap = new HashMap<String, Object>();
            defaultMap.put("bm", 1);
            return ResponseEntity.ok(defaultMap);
        }
    }

    /**
     * 保存（直接操作表 sys_brlb，参考旧系统逻辑）
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> save(@RequestBody SavePatientCategoryRequest req) {
        try {
            if (req.getBmsm() == null || req.getBmsm().trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.fail("类别名称不能为空"));
            }
            if (req.getBm() == null) {
                return ResponseEntity.ok(ApiResponse.fail("类别代码不能为空"));
            }

            SysBrlb existing = sysBrlbMapper.selectById(req.getBm());
            if (existing == null) {
                // 新增
                SysBrlb newBrlb = new SysBrlb();
                newBrlb.setBm(req.getBm());
                newBrlb.setBmsm(req.getBmsm().trim());
                newBrlb.setPym(req.getPym() != null ? req.getPym().trim() : "");
                newBrlb.setQtdm(req.getQtdm() != null ? req.getQtdm().trim() : "");
                newBrlb.setSjlyfs(req.getSjlyfs() != null ? req.getSjlyfs() : 0);
                newBrlb.setSjlyfsms(req.getSjlyfsms() != null ? req.getSjlyfsms() : "");
                newBrlb.setMrksbz(req.getMrksbz() != null ? req.getMrksbz() : false);
                newBrlb.setMrksdm(req.getMrksdm() != null ? req.getMrksdm() : "");
                newBrlb.setMrksmc(req.getMrksmc() != null ? req.getMrksmc() : "");
                newBrlb.setMrysbz(req.getMrysbz() != null ? req.getMrysbz() : false);
                newBrlb.setMrysdm(req.getMrysdm() != null ? req.getMrysdm() : "");
                newBrlb.setMrysmc(req.getMrysmc() != null ? req.getMrysmc() : "");
                newBrlb.setXh(req.getXh() != null ? req.getXh() : 1);
                newBrlb.setTybz(req.getTybz() != null ? req.getTybz() : false);
                newBrlb.setJkbz(req.getJkbz() != null ? req.getJkbz() : false);
                newBrlb.setJgxxBz(req.getJgxxBz() != null ? req.getJgxxBz() : false);
                newBrlb.setJgxx(req.getJgxx() != null ? req.getJgxx() : "");
                newBrlb.setQxkz(req.getQxkz() != null ? req.getQxkz() : false);
                newBrlb.setQxmc("Action_lb" + req.getBm());
                newBrlb.setBrlbys(req.getBrlbys() != null ? req.getBrlbys() : 16777201);

                sysBrlbMapper.insert(newBrlb);
            } else {
                // 修改
                existing.setBmsm(req.getBmsm().trim());
                existing.setPym(req.getPym() != null ? req.getPym().trim() : "");
                existing.setQtdm(req.getQtdm() != null ? req.getQtdm().trim() : "");
                existing.setSjlyfs(req.getSjlyfs() != null ? req.getSjlyfs() : 0);
                existing.setSjlyfsms(req.getSjlyfsms() != null ? req.getSjlyfsms() : "");
                existing.setMrksbz(req.getMrksbz() != null ? req.getMrksbz() : false);
                existing.setMrksdm(req.getMrksdm() != null ? req.getMrksdm() : "");
                existing.setMrksmc(req.getMrksmc() != null ? req.getMrksmc() : "");
                existing.setMrysbz(req.getMrysbz() != null ? req.getMrysbz() : false);
                existing.setMrysdm(req.getMrysdm() != null ? req.getMrysdm() : "");
                existing.setMrysmc(req.getMrysmc() != null ? req.getMrysmc() : "");
                existing.setXh(req.getXh() != null ? req.getXh() : 1);
                existing.setTybz(req.getTybz() != null ? req.getTybz() : false);
                existing.setJkbz(req.getJkbz() != null ? req.getJkbz() : false);
                existing.setJgxxBz(req.getJgxxBz() != null ? req.getJgxxBz() : false);
                existing.setJgxx(req.getJgxx() != null ? req.getJgxx() : "");
                existing.setQxkz(req.getQxkz() != null ? req.getQxkz() : false);
                existing.setQxmc("Action_lb" + req.getBm());
                if (req.getBrlbys() != null) {
                    existing.setBrlbys(req.getBrlbys());
                }

                sysBrlbMapper.updateById(existing);
            }

            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @Data
    public static class SavePatientCategoryRequest {
        private Integer bm;         // 类别代码
        private String bmsm;        // 类别名称
        private String pym;         // 拼音码
        private String qtdm;        // 其他代码
        private Integer sjlyfs;     // 数据来源方式
        private String sjlyfsms;    // 数据来源方式说明
        private Boolean mrksbz;     // 默认科室标志
        private String mrksdm;      // 默认科室代码
        private String mrksmc;      // 默认科室名称
        private Boolean mrysbz;     // 默认医生标志
        private String mrysdm;      // 默认医生代码
        private String mrysmc;      // 默认医生名称
        private Integer xh;         // 序号
        private Boolean tybz;       // 停用标志
        private Boolean jkbz;       // 接口标志
        private Boolean jgxxBz;     // 机构信息标志
        private String jgxx;        // 机构信息
        private Boolean qxkz;       // 权限控制
        private Integer brlbys;     // 病人类别颜色
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

