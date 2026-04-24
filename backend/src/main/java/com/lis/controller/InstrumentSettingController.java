package com.lis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.Instrument;
import com.lis.mapper.InstrumentMapper;
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
 * 7.6 仪器设备设置
 * 使用MyBatis直接操作数据库，兼容H2/MySQL
 */
@RestController
@RequestMapping("/basic/instrument")
public class InstrumentSettingController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private InstrumentMapper instrumentMapper;

    /**
     * 仪器列表（按科室代码）
     * 如果 ksdm 为空或未提供，则查询所有仪器
     */
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(@RequestParam(required = false, defaultValue = "") String ksdm) {
        try {
            System.out.println("=== 查询仪器列表 ===");
            System.out.println("科室代码: " + ksdm);
            
            List<Map<String, Object>> result;
            
            // 如果 ksdm 为空或未提供，查询所有仪器
            if (ksdm == null || ksdm.trim().isEmpty()) {
                System.out.println("科室代码为空，查询所有仪器");
                List<Instrument> insList = instrumentMapper.selectList(new QueryWrapper<>());
                // 转换为Map格式返回
                List<Map<String, Object>> mapList = new ArrayList<>();
                for (Instrument ins : insList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("sb_djid", ins.getSbDjid());
                    map.put("sbdm", ins.getSbdm());
                    map.put("sbmc", ins.getSbmc());
                    map.put("sbbm", ins.getSbbm());
                    map.put("ksdm", ins.getKsdm());
                    map.put("gzzdm", ins.getGzzdm());
                    map.put("pym", ins.getPym());
                    map.put("zxbz", ins.getZxbz());
                    map.put("tybz", ins.getTybz());
                    mapList.add(map);
                }
                System.out.println("查询结果（所有仪器）：找到 " + mapList.size() + " 条仪器记录");
                return ResponseEntity.ok(mapList);
            } else {
                // 按科室代码查询
                List<Instrument> insList = instrumentMapper.selectList(new QueryWrapper<>());
                List<Map<String, Object>> mapList = new ArrayList<>();
                for (Instrument ins : insList) {
                    // 如果前端传0000或空字符串，显示所有仪器（包括有科室的和无科室的）
                    if (ksdm.equals("0000") || ksdm.trim().isEmpty()) {
                        // 返回所有仪器
                        Map<String, Object> map = new HashMap<>();
                        map.put("sb_djid", ins.getSbDjid());
                        map.put("sbdm", ins.getSbdm());
                        map.put("sbmc", ins.getSbmc());
                        map.put("sbbm", ins.getSbbm());
                        map.put("ksdm", ins.getKsdm());
                        map.put("gzzdm", ins.getGzzdm());
                        map.put("pym", ins.getPym());
                        map.put("zxbz", ins.getZxbz());
                        map.put("tybz", ins.getTybz());
                        mapList.add(map);
                    } else if (ksdm.equals(ins.getKsdm())) {
                        // 精确匹配
                        Map<String, Object> map = new HashMap<>();
                        map.put("sb_djid", ins.getSbDjid());
                        map.put("sbdm", ins.getSbdm());
                        map.put("sbmc", ins.getSbmc());
                        map.put("sbbm", ins.getSbbm());
                        map.put("ksdm", ins.getKsdm());
                        map.put("gzzdm", ins.getGzzdm());
                        map.put("pym", ins.getPym());
                        map.put("zxbz", ins.getZxbz());
                        map.put("tybz", ins.getTybz());
                        mapList.add(map);
                    }
                }
                System.out.println("查询结果（科室 " + ksdm + "）：找到 " + mapList.size() + " 条仪器记录");
                return ResponseEntity.ok(mapList);
            }
        } catch (Exception e) {
            System.err.println("查询仪器列表异常: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    /**
     * 保存仪器（新增/修改）
     */
    @PostMapping("/save")
    public ResponseEntity<SaveResult> save(@RequestBody InstrumentSaveRequest req) {
        SaveResult result = new SaveResult();
        try {
            if (req.getSbmc() == null || req.getSbmc().trim().isEmpty()) {
                result.success = false;
                result.message = "设备名称不能为空";
                return ResponseEntity.ok(result);
            }

            int sbDjid = req.getSbDjid() == null ? 0 : req.getSbDjid();
            
            Instrument instrument = new Instrument();
            instrument.setSbDjid(sbDjid == 0 ? null : sbDjid);
            instrument.setSbdm(req.getSbdm());
            instrument.setSbmc(req.getSbmc());
            instrument.setSbbm(req.getSbbm());
            instrument.setKsdm(req.getKsdm());
            instrument.setGzzdm(req.getGzzdm());
            instrument.setPym(req.getPym());
            instrument.setZxbz(req.getZxbz() != null ? req.getZxbz() : true);
            instrument.setTybz(req.getTybz() != null ? req.getTybz() : false);

            if (sbDjid == 0) {
                // 新增
                instrumentMapper.insert(instrument);
                result.success = true;
                result.message = "保存成功";
            } else {
                // 修改
                instrumentMapper.updateById(instrument);
                result.success = true;
                result.message = "保存成功";
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.success = false;
            result.message = "保存失败：" + e.getMessage();
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 删除仪器（逻辑删除 - 设置tybz=1）
     * 对应SRS_Addendum需求 ADD-INS-003
     */
    @PostMapping("/delete")
    public ResponseEntity<SaveResult> delete(@RequestParam Integer sbDjid) {
        SaveResult result = new SaveResult();
        try {
            if (sbDjid == null || sbDjid <= 0) {
                result.success = false;
                result.message = "无效的仪器ID";
                return ResponseEntity.ok(result);
            }

            // 逻辑删除：设置停用标志为1
            Instrument instrument = new Instrument();
            instrument.setSbDjid(sbDjid);
            instrument.setTybz(true);
            int rows = instrumentMapper.updateById(instrument);
            
            if (rows > 0) {
                result.success = true;
                result.message = "删除成功";
            } else {
                result.success = false;
                result.message = "删除失败，记录不存在";
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.success = false;
            result.message = "删除失败：" + e.getMessage();
            return ResponseEntity.ok(result);
        }
    }

    // ==================== 报告合并设置相关接口（占位实现） ====================
    
    /**
     * 获取合并组列表
     */
    @GetMapping("/merge-group/list")
    public ResponseEntity<List<Map<String, Object>>> listMergeGroups() {
        return ResponseEntity.ok(new ArrayList<>());
    }
    
    /**
     * 获取合并组明细
     */
    @GetMapping("/merge-group/detail")
    public ResponseEntity<List<Map<String, Object>>> getMergeGroupDetail(@RequestParam Integer hbid) {
        return ResponseEntity.ok(new ArrayList<>());
    }
    
    /**
     * 保存合并组
     */
    @PostMapping("/merge-group/save")
    public ResponseEntity<SaveResult> saveMergeGroup(@RequestBody Map<String, Object> payload) {
        SaveResult result = new SaveResult();
        result.success = false;
        result.message = "合并组设置功能待完善";
        return ResponseEntity.ok(result);
    }
    
    /**
     * 删除合并组
     */
    @PostMapping("/merge-group/delete")
    public ResponseEntity<SaveResult> deleteMergeGroup(@RequestParam Integer hbid) {
        SaveResult result = new SaveResult();
        result.success = false;
        result.message = "合并组设置功能待完善";
        return ResponseEntity.ok(result);
    }
    
    /**
     * 添加设备到合并组
     */
    @PostMapping("/merge-group/device")
    public ResponseEntity<SaveResult> addDeviceToMergeGroup(@RequestBody Map<String, Object> payload) {
        SaveResult result = new SaveResult();
        result.success = false;
        result.message = "合并组设置功能待完善";
        return ResponseEntity.ok(result);
    }

    // ==================== 工作组设置相关接口（占位实现） ====================
    
    /**
     * 获取工作组树
     */
    @GetMapping("/workgroup/tree")
    public ResponseEntity<List<Map<String, Object>>> getWorkgroupTree() {
        return ResponseEntity.ok(new ArrayList<>());
    }
    
    /**
     * 获取未分配工作组设备（兼容前端路径）
     */
    @GetMapping("/workgroup/unassigned")
    public ResponseEntity<List<Map<String, Object>>> getUnassignedWorkgroupDevicesCompat() {
        return ResponseEntity.ok(new ArrayList<>());
    }
    
    /**
     * 获取未分配工作组设备
     */
    @GetMapping("/workgroup/unassigned-devices")
    public ResponseEntity<List<Map<String, Object>>> getUnassignedWorkgroupDevicesFull() {
        return ResponseEntity.ok(new ArrayList<>());
    }
    
    /**
     * 分配设备到工作组
     */
    @PostMapping("/workgroup/assign")
    public ResponseEntity<SaveResult> assignToWorkgroup(@RequestBody Map<String, Object> payload) {
        SaveResult result = new SaveResult();
        result.success = false;
        result.message = "工作组设置功能待完善";
        return ResponseEntity.ok(result);
    }
    
    /**
     * 取消设备工作组分配
     */
    @PostMapping("/workgroup/unassign")
    public ResponseEntity<SaveResult> unassignFromWorkgroup(@RequestParam Integer sbDjid) {
        SaveResult result = new SaveResult();
        result.success = false;
        result.message = "工作组设置功能待完善";
        return ResponseEntity.ok(result);
    }

    // ==================== 站点设置相关接口（占位实现） ====================
    
    /**
     * 获取站点树
     */
    @GetMapping("/site/tree")
    public ResponseEntity<List<Map<String, Object>>> getSiteTree() {
        return ResponseEntity.ok(new ArrayList<>());
    }
    
    /**
     * 获取未分配站点设备（兼容前端路径）
     */
    @GetMapping("/site/unassigned")
    public ResponseEntity<List<Map<String, Object>>> getUnassignedSiteDevicesCompat() {
        return getUnassignedSiteDevices();
    }
    
    /**
     * 获取未分配站点设备
     */
    @GetMapping("/site/unassigned-devices")
    public ResponseEntity<List<Map<String, Object>>> getUnassignedSiteDevices() {
        return ResponseEntity.ok(new ArrayList<>());
    }
    
    /**
     * 分配设备到站点
     */
    @PostMapping("/site/assign")
    public ResponseEntity<SaveResult> assignToSite(@RequestBody Map<String, Object> payload) {
        SaveResult result = new SaveResult();
        result.success = false;
        result.message = "站点设置功能待完善";
        return ResponseEntity.ok(result);
    }
    
    /**
     * 取消设备站点分配
     */
    @PostMapping("/site/unassign")
    public ResponseEntity<SaveResult> unassignFromSite(@RequestParam Integer sbDjid) {
        SaveResult result = new SaveResult();
        result.success = false;
        result.message = "站点设置功能待完善";
        return ResponseEntity.ok(result);
    }

    // ==================== 请求类 ====================

    @Data
    public static class InstrumentSaveRequest {
        private Integer sbDjid;
        private String sbdm;
        private String sbmc;
        private String sbbm;
        private String ksdm;
        private String gzzdm;
        private String pym;
        private Boolean zxbz;
        private Boolean tybz;
    }

    @Data
    public static class SaveResult {
        private boolean success;
        private String message;
    }
}
