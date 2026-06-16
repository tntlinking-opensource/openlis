package com.lis.controller;

import com.lis.entity.Instrument;
import com.lis.service.InstrumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.lis.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/basic/instrument")
@Slf4j
public class InstrumentSettingController {

    @Autowired
    private InstrumentService instrumentService;

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> list(@RequestParam(required = false, defaultValue = "") String ksdm) {
        try {
            log.info("=== 查询仪器列表 ===");
            log.info("科室代码: " + ksdm);
            List<Map<String, Object>> result = instrumentService.listInstruments(ksdm);
            log.info("查询结果：找到 " + result.size() + " 条仪器记录");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询仪器列表异常: " + e.getClass().getName() + ": " + e.getMessage());
            log.error("操作失败", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable("id") Integer id) {
        try {
            Instrument ins = instrumentService.getInstrumentFull(id);
            if (ins == null) return ResponseEntity.notFound().build();
            Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("sb_djid", ins.getSbDjid());
            map.put("sbdm", ins.getSbdm());
            map.put("sbmc", ins.getSbmc());
            map.put("sbbm", ins.getSbbm());
            map.put("ksdm", ins.getKsdm());
            map.put("gzzdm", ins.getGzzdm());
            map.put("pym", ins.getPym());
            map.put("zxbz", ins.getZxbz());
            map.put("tybz", ins.getTybz());
            map.put("comsm", ins.getComsm());
            map.put("btl", ins.getBtl());
            map.put("jyw", ins.getJyw());
            map.put("sjw", ins.getSjw());
            map.put("tzw", ins.getTzw());
            map.put("xmxsfs", ins.getXmxsfs());
            map.put("bgbt", ins.getBgbt());
            map.put("bgyj", ins.getBgyj());
            map.put("mrzhid", ins.getMrzhid());
            map.put("tx", ins.getTx());
            map.put("dyfs", ins.getDyfs());
            map.put("shzfs", ins.getShzfs());
            map.put("sxpl", ins.getSxpl());
            map.put("ycxwc", ins.getYcxwc());
            map.put("xsfs", ins.getXsfs());
            map.put("bblb", ins.getBblb());
            map.put("bgbh", ins.getBgbh());
            map.put("bgmc", ins.getBgmc());
            map.put("xslb", ins.getXslb());
            map.put("zklb", ins.getZklb());
            map.put("yqzd", ins.getYqzd());
            map.put("zjjgts", ins.getZjjgts());
            map.put("zkjh", ins.getZkjh());
            map.put("jzjh", ins.getJzjh());
            map.put("cjcx", ins.getCjcx());
            map.put("szdm", ins.getSzdm());
            map.put("kztsbz", ins.getKztsbz());
            map.put("jkxmxz", ins.getJkxmxz());
            map.put("fsztsbz", ins.getFsztsbz());
            map.put("zerotsbz", ins.getZerotsbz());
            map.put("ip", ins.getIp());
            map.put("dk", ins.getDk());
            map.put("sjklj", ins.getSjklj());
            map.put("wjdz", ins.getWjdz());
            map.put("bfdz", ins.getBfdz());
            map.put("wjyhm", ins.getWjyhm());
            map.put("wjmm", ins.getWjmm());
            map.put("yszcz", ins.getYszcz());
            map.put("yspgz", ins.getYspgz());
            map.put("yspdz", ins.getYspdz());
            map.put("ysbjgz", ins.getYsbjgz());
            map.put("ysbjdz", ins.getYsbjdz());
            map.put("yswsh", ins.getYswsh());
            map.put("ysysh", ins.getYsysh());
            map.put("ysycy", ins.getYsycy());
            map.put("ysydy", ins.getYsydy());
            map.put("ysyjy", ins.getYsyjy());
            map.put("ysycz", ins.getYsycz());
            map.put("yswjz", ins.getYswjz());
            map.put("ysjgwc", ins.getYsjgwc());
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            log.error("查询仪器详情异常", e);
            return ResponseEntity.notFound().build();
        }
    }

    @OperationLog(value = "保存仪器设置", module = "仪器设置")
    @PostMapping("/save")
    public ResponseEntity<InstrumentService.SaveResult> save(@RequestBody InstrumentSaveRequest req) {
        try {
            if (req.getSbmc() == null || req.getSbmc().trim().isEmpty()) {
                InstrumentService.SaveResult result = new InstrumentService.SaveResult();
                result.setSuccess(false);
                result.setMessage("设备名称不能为空");
                return ResponseEntity.ok(result);
            }
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("sbDjid", req.getSbDjid());
            data.put("sbdm", req.getSbdm());
            data.put("sbmc", req.getSbmc());
            data.put("sbbm", req.getSbbm());
            data.put("ksdm", req.getKsdm());
            data.put("gzzdm", req.getGzzdm());
            data.put("pym", req.getPym());
            data.put("zxbz", req.getZxbz());
            data.put("tybz", req.getTybz());
            data.put("comsm", req.getComsm());
            data.put("btl", req.getBtl());
            data.put("jyw", req.getJyw());
            data.put("sjw", req.getSjw());
            data.put("tzw", req.getTzw());
            data.put("xmxsfs", req.getXmxsfs());
            data.put("bgbt", req.getBgbt());
            data.put("bgyj", req.getBgyj());
            data.put("mrzhid", req.getMrzhid());
            data.put("tx", req.getTx());
            data.put("dyfs", req.getDyfs());
            data.put("shzfs", req.getShzfs());
            data.put("sxpl", req.getSxpl());
            data.put("ycxwc", req.getYcxwc());
            data.put("xsfs", req.getXsfs());
            data.put("bblb", req.getBblb());
            data.put("bgbh", req.getBgbh());
            data.put("bgmc", req.getBgmc());
            data.put("xslb", req.getXslb());
            data.put("zklb", req.getZklb());
            data.put("yqzd", req.getYqzd());
            data.put("zjjgts", req.getZjjgts());
            data.put("zkjh", req.getZkjh());
            data.put("jzjh", req.getJzjh());
            data.put("cjcx", req.getCjcx());
            data.put("szdm", req.getSzdm());
            data.put("kztsbz", req.getKztsbz());
            data.put("jkxmxz", req.getJkxmxz());
            data.put("fsztsbz", req.getFsztsbz());
            data.put("zerotsbz", req.getZerotsbz());
            data.put("ip", req.getIp());
            data.put("dk", req.getDk());
            data.put("sjklj", req.getSjklj());
            data.put("wjdz", req.getWjdz());
            data.put("bfdz", req.getBfdz());
            data.put("wjyhm", req.getWjyhm());
            data.put("wjmm", req.getWjmm());
            data.put("yszcz", req.getYszcz());
            data.put("yspgz", req.getYspgz());
            data.put("yspdz", req.getYspdz());
            data.put("ysbjgz", req.getYsbjgz());
            data.put("ysbjdz", req.getYsbjdz());
            data.put("yswsh", req.getYswsh());
            data.put("ysysh", req.getYsysh());
            data.put("ysycy", req.getYsycy());
            data.put("ysydy", req.getYsydy());
            data.put("ysyjy", req.getYsyjy());
            data.put("ysycz", req.getYsycz());
            data.put("yswjz", req.getYswjz());
            data.put("ysjgwc", req.getYsjgwc());
            return ResponseEntity.ok(instrumentService.saveInstrument(data));
        } catch (Exception e) {
            log.error("操作失败", e);
            InstrumentService.SaveResult result = new InstrumentService.SaveResult();
            result.setSuccess(false);
            result.setMessage("保存失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @OperationLog(value = "删除仪器", module = "仪器设置")
    @PostMapping("/delete")
    public ResponseEntity<InstrumentService.SaveResult> delete(@RequestParam Integer sbDjid) {
        return ResponseEntity.ok(instrumentService.deleteInstrument(sbDjid));
    }

    @GetMapping("/merge-group/list")
    public ResponseEntity<List<Map<String, Object>>> listMergeGroups() {
        try {
            return ResponseEntity.ok(instrumentService.listMergeGroups());
        } catch (Exception e) {
            log.error("查询合并组列表异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/merge-group/detail")
    public ResponseEntity<List<Map<String, Object>>> getMergeGroupDetail(@RequestParam Integer hbid) {
        try {
            return ResponseEntity.ok(instrumentService.getMergeGroupDetail(hbid));
        } catch (Exception e) {
            log.error("查询合并组明细异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/merge-group/save")
    public ResponseEntity<InstrumentService.SaveResult> saveMergeGroup(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(instrumentService.saveMergeGroup(payload));
    }

    @PostMapping("/merge-group/delete")
    public ResponseEntity<InstrumentService.SaveResult> deleteMergeGroup(@RequestParam Integer hbid) {
        return ResponseEntity.ok(instrumentService.deleteMergeGroup(hbid));
    }

    @PostMapping("/merge-group/device")
    public ResponseEntity<InstrumentService.SaveResult> addDeviceToMergeGroup(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(instrumentService.addDeviceToMergeGroup(payload));
    }

    @GetMapping("/workgroup/tree")
    public ResponseEntity<List<Map<String, Object>>> getWorkgroupTree() {
        try {
            return ResponseEntity.ok(instrumentService.getWorkgroupTree());
        } catch (Exception e) {
            log.error("获取工作组树异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/workgroup/unassigned")
    public ResponseEntity<List<Map<String, Object>>> getUnassignedWorkgroupDevices() {
        try {
            return ResponseEntity.ok(instrumentService.getUnassignedWorkgroupDevices());
        } catch (Exception e) {
            log.error("获取未分配工作组设备异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/workgroup/unassigned-devices")
    public ResponseEntity<List<Map<String, Object>>> getUnassignedWorkgroupDevicesFull() {
        return getUnassignedWorkgroupDevices();
    }

    @PostMapping("/workgroup/assign")
    public ResponseEntity<InstrumentService.SaveResult> assignToWorkgroup(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(instrumentService.assignToWorkgroup(payload));
    }

    @PostMapping("/workgroup/unassign")
    public ResponseEntity<InstrumentService.SaveResult> unassignFromWorkgroup(@RequestParam Integer sbDjid) {
        return ResponseEntity.ok(instrumentService.unassignFromWorkgroup(sbDjid));
    }

    @GetMapping("/site/tree")
    public ResponseEntity<List<Map<String, Object>>> getSiteTree() {
        try {
            return ResponseEntity.ok(instrumentService.getSiteTree());
        } catch (Exception e) {
            log.error("获取站点树异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/site/unassigned")
    public ResponseEntity<List<Map<String, Object>>> getUnassignedSiteDevicesCompat() {
        return getUnassignedSiteDevices();
    }

    @GetMapping("/site/unassigned-devices")
    public ResponseEntity<List<Map<String, Object>>> getUnassignedSiteDevices() {
        try {
            return ResponseEntity.ok(instrumentService.getUnassignedSiteDevices());
        } catch (Exception e) {
            log.error("获取未分配站点设备异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/site/assign")
    public ResponseEntity<InstrumentService.SaveResult> assignToSite(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(instrumentService.assignToSite(payload));
    }

    @PostMapping("/site/unassign")
    public ResponseEntity<InstrumentService.SaveResult> unassignFromSite(@RequestParam Integer sbDjid) {
        return ResponseEntity.ok(instrumentService.unassignFromSite(sbDjid));
    }

    // ==================== 科室仪器分配 API ====================

    @GetMapping("/department/list")
    public ResponseEntity<List<Map<String, Object>>> listDepartments() {
        try {
            return ResponseEntity.ok(instrumentService.listDepartments());
        } catch (Exception e) {
            log.error("获取科室列表异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/department/instruments")
    public ResponseEntity<List<Map<String, Object>>> getInstrumentsWithAssignmentStatus(@RequestParam String ksdm) {
        try {
            return ResponseEntity.ok(instrumentService.listInstrumentsWithAssignmentStatus(ksdm));
        } catch (Exception e) {
            log.error("获取仪器分配状态异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/department/assign")
    public ResponseEntity<InstrumentService.SaveResult> assignInstrument(@RequestBody Map<String, Object> payload) {
        try {
            String ksdm = (String) payload.get("ksdm");
            Integer sbDjid = toInt(payload.get("sbDjid"));
            Boolean assign = (Boolean) payload.get("assign");
            return ResponseEntity.ok(instrumentService.assignInstrumentToDepartment(ksdm, sbDjid, assign != null && assign));
        } catch (Exception e) {
            log.error("分配仪器异常", e);
            InstrumentService.SaveResult result = new InstrumentService.SaveResult();
            result.setSuccess(false);
            result.setMessage("操作失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    // ==================== 特殊标本设置 API ====================

    @GetMapping("/special-sample/categories")
    public ResponseEntity<List<Map<String, Object>>> listSpecialSampleCategories() {
        try {
            return ResponseEntity.ok(instrumentService.listSpecialSampleCategories());
        } catch (Exception e) {
            log.error("获取特殊标本分类异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/special-sample/rules")
    public ResponseEntity<List<Map<String, Object>>> listSpecialSampleRules(@RequestParam(required = false) Integer mkid) {
        try {
            return ResponseEntity.ok(instrumentService.listSpecialSampleRules(mkid));
        } catch (Exception e) {
            log.error("获取特殊标本规则异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/special-sample/rules")
    public ResponseEntity<InstrumentService.SaveResult> addSpecialSampleRule(@RequestBody Map<String, Object> payload) {
        try {
            Integer mkid = toInt(payload.get("mkid"));
            Integer xmid = toInt(payload.get("xmid"));
            String mksm = (String) payload.get("mksm");
            return ResponseEntity.ok(instrumentService.addSpecialSampleRule(mkid, xmid, mksm));
        } catch (Exception e) {
            log.error("添加特殊标本规则异常", e);
            InstrumentService.SaveResult result = new InstrumentService.SaveResult();
            result.setSuccess(false);
            result.setMessage("操作失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @DeleteMapping("/special-sample/rules")
    public ResponseEntity<InstrumentService.SaveResult> removeSpecialSampleRule(@RequestParam Integer mkid, @RequestParam Integer xmid) {
        try {
            return ResponseEntity.ok(instrumentService.removeSpecialSampleRule(mkid, xmid));
        } catch (Exception e) {
            log.error("删除特殊标本规则异常", e);
            InstrumentService.SaveResult result = new InstrumentService.SaveResult();
            result.setSuccess(false);
            result.setMessage("操作失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    // ==================== 项目仪器关联 API ====================

    @GetMapping("/item-instrument")
    public ResponseEntity<List<Map<String, Object>>> listInstrumentsForItem(@RequestParam Integer xmid) {
        try {
            return ResponseEntity.ok(instrumentService.listInstrumentsForItem(xmid));
        } catch (Exception e) {
            log.error("获取项目对应仪器异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/instrument-item")
    public ResponseEntity<List<Map<String, Object>>> listItemsForInstrument(@RequestParam Integer sbDjid) {
        try {
            return ResponseEntity.ok(instrumentService.listItemsForInstrument(sbDjid));
        } catch (Exception e) {
            log.error("获取仪器对应项目异常", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @PostMapping("/item-instrument")
    public ResponseEntity<InstrumentService.SaveResult> addItemInstrumentRelation(@RequestBody Map<String, Object> payload) {
        try {
            Integer xmid = toInt(payload.get("xmid"));
            Integer sbDjid = toInt(payload.get("sbDjid"));
            Integer zhid = toInt(payload.get("zhid"));
            return ResponseEntity.ok(instrumentService.addItemInstrumentRelation(xmid, sbDjid, zhid));
        } catch (Exception e) {
            log.error("添加项目仪器关联异常", e);
            InstrumentService.SaveResult result = new InstrumentService.SaveResult();
            result.setSuccess(false);
            result.setMessage("操作失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @DeleteMapping("/item-instrument")
    public ResponseEntity<InstrumentService.SaveResult> removeItemInstrumentRelation(@RequestParam Integer xmid, @RequestParam Integer sbDjid) {
        try {
            return ResponseEntity.ok(instrumentService.removeItemInstrumentRelation(xmid, sbDjid));
        } catch (Exception e) {
            log.error("删除项目仪器关联异常", e);
            InstrumentService.SaveResult result = new InstrumentService.SaveResult();
            result.setSuccess(false);
            result.setMessage("操作失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    private Integer toInt(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @lombok.Data
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
        private String comsm;
        private Integer btl;
        private String jyw;
        private Integer sjw;
        private Integer tzw;
        private String xmxsfs;
        private String bgbt;
        private String bgyj;
        private Integer mrzhid;
        private String tx;
        private String dyfs;
        private String shzfs;
        private Integer sxpl;
        private Boolean ycxwc;
        private String xsfs;
        private String bblb;
        private String bgbh;
        private String bgmc;
        private String xslb;
        private String zklb;
        private String yqzd;
        private Integer zjjgts;
        private String zkjh;
        private String jzjh;
        private String cjcx;
        private String szdm;
        private Boolean kztsbz;
        private Boolean jkxmxz;
        private Boolean fsztsbz;
        private Boolean zerotsbz;
        private String ip;
        private String dk;
        private String sjklj;
        private String wjdz;
        private String bfdz;
        private String wjyhm;
        private String wjmm;
        private String yszcz;
        private String yspgz;
        private String yspdz;
        private String ysbjgz;
        private String ysbjdz;
        private String yswsh;
        private String ysysh;
        private String ysycy;
        private String ysydy;
        private String ysyjy;
        private String ysycz;
        private String yswjz;
        private String ysjgwc;
    }
}
