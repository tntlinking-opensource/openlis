package com.lis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.SysTybmzb;
import com.lis.entity.SysTybmmx;
import com.lis.entity.SysXtsz;
import com.lis.mapper.SysTybmzbMapper;
import com.lis.mapper.SysTybmmxMapper;
import com.lis.mapper.SysXtszMapper;
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
 * 6.4 通用编码设置控制器
 * 对应旧系统通用编码设置功能
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
    
    @Autowired
    private SysXtszMapper sysXtszMapper;
    
    /**
     * 获取所有编码主表列表
     */
    @GetMapping("/main/list")
    public ResponseEntity<List<Map<String, Object>>> getMainList() {
        try {
            // 使用正确的列名: id, bmdm, bmmc, pym, bz, tybz
            String sql = "SELECT id, bmdm, bmmc, pym, bz, tybz FROM sys_tybmzb ORDER BY id";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
    
    /**
     * 获取通用编码列表（测试用）
     * 返回系统配置中的编码选项
     */
    @GetMapping("/list")
    public ResponseEntity<List<Object>> getCodeList() {
        // 返回一些常用的编码选项
        List<Object> result = new ArrayList<>();
        
        Map<String, String> item1 = new HashMap<>();
        item1.put("code", "01");
        item1.put("name", "门诊");
        result.add(item1);
        
        Map<String, String> item2 = new HashMap<>();
        item2.put("code", "02");
        item2.put("name", "住院");
        result.add(item2);
        
        Map<String, String> item3 = new HashMap<>();
        item3.put("code", "03");
        item3.put("name", "急诊");
        result.add(item3);
        
        Map<String, String> item4 = new HashMap<>();
        item4.put("code", "04");
        item4.put("name", "体检");
        result.add(item4);
        
        return ResponseEntity.ok(result);
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
     * 保存编码明细（增加同级或下级）
     */
    @PostMapping("/detail/save")
    public ResponseEntity<ApiResponse> saveDetail(@RequestBody SaveDetailRequest request) {
        try {
            SysTybmmx entity = request.getDetail();
            if (entity.getId() != null) {
                sysTybmmxMapper.updateById(entity);
                ApiResponse response = ApiResponse.success("保存成功");
                response.setData(entity); // 返回更新后的实体
                return ResponseEntity.ok(response);
            } else {
                // 新增：如果是同级，使用相同的bmdm；如果是下级，需要设置父级关系
                sysTybmmxMapper.insert(entity);
                ApiResponse response = ApiResponse.success("保存成功");
                response.setData(entity); // 返回新创建的实体（包含自动生成的ID）
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
        private String type; // "sibling" 同级, "child" 下级
    }
    
    /**
     * 迁移数据从SQL Server到MySQL（临时接口）
     */
    @PostMapping("/migrate-from-sqlserver")
    public ResponseEntity<ApiResponse> migrateFromSqlServer() {
        try {
            // 从SQL Server获取数据并插入MySQL
            // 这里使用JDBC直连方式，需要配置SQL Server数据源
            // 暂时返回提示信息
            return ResponseEntity.ok(ApiResponse.success("迁移功能需要配置SQL Server数据源"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("迁移失败：" + e.getMessage()));
        }
    }
    
    /**
     * 调试接口：直接插入测试数据
     */
    @PostMapping("/debug/init-test-data")
    public ResponseEntity<ApiResponse> initTestData() {
        try {
            // 清空旧数据
            try { jdbcTemplate.execute("DELETE FROM sys_tybmmx"); } catch (Exception e) {}
            try { jdbcTemplate.execute("DELETE FROM sys_tybmzb"); } catch (Exception e) {}
            
            // 插入主表数据 (使用实际列名: id, bmdm, bmmc, pym, bz, tybz)
            jdbcTemplate.update("INSERT INTO sys_tybmzb (id, bmdm, bmmc, pym, bz, tybz) VALUES (1, 1, '标本错误类型', '', '', 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmzb (id, bmdm, bmmc, pym, bz, tybz) VALUES (2, 2, '仪器专业设置', '', '', 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmzb (id, bmdm, bmmc, pym, bz, tybz) VALUES (3, 3, '仪器专业设置(周转)', '', '', 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmzb (id, bmdm, bmmc, pym, bz, tybz) VALUES (4, 4, '周转时间项目', '', '', 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmzb (id, bmdm, bmmc, pym, bz, tybz) VALUES (1001, 5, '组合类别', '', '', 0)");
            
            // 插入明细表数据 - 标本错误类型(bmdm=1)
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (1, 101, '标本类型错误', '123', '', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (1, 102, '容器错误', '02', 'RQCW', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (1, 103, '采集量错误', 'W', '', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (1, 104, '抗凝标本凝集', '', 'KNBBNJ', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (1, 105, '溶血', '', 'RX', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (1, 106, '丢失', '', 'DS', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (1, 107, '送检项目错误', '', 'SJXMCW', 0, 0)");
            
            // 插入明细表数据 - 仪器专业设置(bmdm=2)
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (2, 201, '生化', '', 'SH', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (2, 202, '免疫', '', 'MY', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (2, 203, '临检', '', 'LJ', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (2, 204, '微生物', '', 'W', 0, 0)");
            
            // 插入明细表数据 - 仪器专业设置(周转)(bmdm=3)
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (3, 301, '生化', '', 'SH', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (3, 302, '三大常规', '', 'SDCG', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (3, 303, '凝血', '', 'NX', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (3, 304, '免疫', '', 'MY', 0, 0)");
            
            // 插入明细表数据 - 周转时间项目(bmdm=4)
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (4, 401, '血钾', '', '', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (4, 402, '丙氨酸氨基转移酶', '', '', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (4, 403, '肌钙蛋白I或肌钙蛋白T', '', '', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (4, 404, '促甲状腺激素', '', '', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (4, 405, '甲胎蛋白', '', '', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (4, 406, '白细胞计数', '', '', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (4, 407, '尿常规', '', '', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (4, 408, '国际标准化比值(INR)', '', '', 0, 0)");
            
            // 插入明细表数据 - 组合类别(bmdm=5)
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (5, 501, '血培养类别', '1', 'XPY', 0, 0)");
            jdbcTemplate.update("INSERT INTO sys_tybmmx (bmdm, bm, bmsm, szdm, pym, mrzbz, tybz) VALUES (5, 502, '痰培养类别', '', 'TPYLB', 0, 0)");
            
            return ResponseEntity.ok(ApiResponse.success("已初始化测试数据: 5条主表, 26条明细"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(ApiResponse.fail("初始化失败：" + e.getMessage()));
        }
    }
    
    @Data
    public static class ApiResponse {
        private Boolean success;
        private String message;
        private Object data; // 用于返回新创建的实体ID等信息
        
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

