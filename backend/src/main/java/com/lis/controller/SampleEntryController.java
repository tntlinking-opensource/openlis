package com.lis.controller;

import com.lis.service.SampleService;
import com.lis.service.ReportVersionService;
import com.lis.mapper.SysBrlbMapper;
import com.lis.mapper.SysBbzlDictMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.lis.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/sample", "/api/sample"})
@Slf4j
public class SampleEntryController {

    @Autowired
    private SampleService sampleService;

    @Autowired
    private ReportVersionService reportVersionService;

    @Autowired
    private SysBrlbMapper sysBrlbMapper;

    @Autowired
    private SysBbzlDictMapper sysBbzlDictMapper;

    @GetMapping("/nextSampleNo")
    public ResponseEntity<Map<String, Object>> nextSampleNo(@RequestParam(required = false) String date) {
        try {
            return ResponseEntity.ok(sampleService.nextSampleNo(date));
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping("/patients")
    public ResponseEntity<List<Map<String, Object>>> listPatients(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String patientType,
            @RequestParam(required = false) String sampleNo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String barcode) {
        try {
            return ResponseEntity.ok(sampleService.listPatients(date, patientType, sampleNo, name, barcode));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @OperationLog(value = "保存样本", module = "样本管理")
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveSample(@RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(sampleService.saveSample(payload));
        } catch (IllegalArgumentException e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        } catch (Exception e) {
            log.error("保存样本失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(resp);
        }
    }

    @OperationLog(value = "检验样本", module = "样本管理")
    @PostMapping("/inspect/{brxxId}")
    public ResponseEntity<Map<String, Object>> inspectSample(@PathVariable("brxxId") Integer brxxId,
                                                              @RequestParam(value = "czydm", required = false) String czydm) {
        try {
            Map<String, Object> result = sampleService.inspectSample(brxxId, czydm);
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @OperationLog(value = "审核样本", module = "样本审核")
    @PostMapping("/audit/{brxxId}")
    public ResponseEntity<Map<String, Object>> auditSample(@PathVariable("brxxId") Integer brxxId,
                                                           @RequestParam(value = "czydm", required = false) String czydm) {
        try {
            Map<String, Object> result = sampleService.auditSample(brxxId, czydm);
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @OperationLog(value = "取消审核", module = "样本审核")
    @PostMapping("/unaudit/{brxxId}")
    public ResponseEntity<Map<String, Object>> unauditSample(@PathVariable("brxxId") Integer brxxId,
                                                              @RequestParam(value = "czydm", required = false) String czydm) {
        try {
            Map<String, Object> result = sampleService.batchUnaudit(java.util.Collections.singletonList(brxxId), czydm);
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            log.error("取消审核失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @OperationLog(value = "打印报告", module = "报告打印")
    @PostMapping("/print/{brxxId}")
    public ResponseEntity<Map<String, Object>> printSample(@PathVariable("brxxId") Integer brxxId) {
        try {
            Map<String, Object> result = sampleService.printSample(brxxId);
            if (Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            log.error("操作失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping("/results/{brxxId}")
    public ResponseEntity<List<Map<String, Object>>> listResults(@PathVariable("brxxId") Integer brxxId) {
        try {
            return ResponseEntity.ok(sampleService.listResults(brxxId));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @GetMapping(value = "/report/{brxxId}", produces = "text/html; charset=UTF-8")
    public ResponseEntity<String> reportHtml(@PathVariable("brxxId") Integer brxxId) {
        try {
            if (brxxId == null) {
                return ResponseEntity.badRequest().body("<html><body>缺少样本ID</body></html>");
            }

            String templateHtml = reportVersionService.renderReport(brxxId);
            if (templateHtml != null) {
                return ResponseEntity.ok(templateHtml);
            }

            Map<String, Object> reportData = sampleService.getReportData(brxxId);
            @SuppressWarnings("unchecked")
            Map<String, Object> brxx = reportData;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> jg = (List<Map<String, Object>>) reportData.get("results");
            String bbzlmc = reportData.get("bbzlmc") != null ? String.valueOf(reportData.get("bbzlmc")) : "";

            if (brxx == null) {
                return ResponseEntity.status(500).body("<html><body>样本不存在</body></html>");
            }

            String sexText = "";
            Object brxbObj = brxx.get("brxb");
            if (brxbObj != null) {
                int brxb = (brxbObj instanceof Number) ? ((Number) brxbObj).intValue() : Integer.parseInt(brxbObj.toString().trim());
                sexText = brxb == 1 ? "男" : (brxb == 2 ? "女" : "");
            }

            String ageText = "";
            Object brnlObj = brxx.get("brnl");
            Object nllxObj = brxx.get("nllx");
            if (brnlObj != null) {
                ageText = brnlObj.toString();
                if (nllxObj != null) {
                    String nllxStr = nllxObj.toString().trim();
                    try {
                        int nllx = Integer.parseInt(nllxStr);
                        if (nllx == 1) ageText += "岁";
                        else if (nllx == 2) ageText += "月";
                        else if (nllx == 3) ageText += "天";
                        else if (nllx == 4) ageText += "小时";
                    } catch (NumberFormatException e) {
                        ageText += nllxStr;
                    }
                }
            }

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
                    + "<div><b>送检医生：</b>" + safe(brxx.get("sjys")) + "</div>"
                    + "</div>"
                    + "<table><thead><tr>"
                    + "<th>项目代码</th><th>项目名称</th><th>结果</th><th>单位</th><th>参考范围</th><th>提示</th>"
                    + "</tr></thead><tbody>";

            if (jg == null || jg.isEmpty()) {
                html += "<tr><td colspan='5' style='text-align:center;'>暂无检验结果</td></tr>";
            } else {
                for (Map<String, Object> r : jg) {
                    String flag = "";
                    Object hl = r.get("gdbj");
                    if (hl != null) {
                        String s = String.valueOf(hl);
                        if ("H".equalsIgnoreCase(s)) flag = "↑";
                        if ("L".equalsIgnoreCase(s)) flag = "↓";
                    }
                    html += "<tr>"
                            + "<td>" + safe(r.get("xmdm")) + "</td>"
                            + "<td>" + safe(r.get("xmzwmc")) + "</td>"
                            + "<td>" + safe(r.get("jyjg")) + "</td>"
                            + "<td>" + safe(r.get("jldw")) + "</td>"
                            + "<td>" + safe(r.get("ckz")) + "</td>"
                            + "<td style='text-align:center;'>" + safe(flag) + "</td>"
                            + "</tr>";
                }
            }

            html += "</tbody></table>"
                    + "<div style='margin-top:10px;color:#444;'>报告单号：" + safe(brxx.get("brxx_id")) + "</div>"
                    + "</body></html>";

            return ResponseEntity.ok(html);
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body("<html><body>生成报告失败：" + e.getMessage() + "</body></html>");
        }
    }

    private static String safe(Object v) {
        if (v == null) return "";
        return String.valueOf(v).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchSamples(
            @RequestParam(required = false) String sampleNo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String barcode,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String date) {
        try {
            return ResponseEntity.ok(sampleService.searchSamples(sampleNo, name, barcode, patientId, date));
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.status(500).body(new ArrayList<>());
        }
    }

    @OperationLog(value = "样本作废", module = "样本管理")
    @PostMapping("/invalid/{brxxId}")
    public ResponseEntity<Map<String, Object>> invalidateSample(
            @PathVariable("brxxId") Integer brxxId,
            @RequestBody Map<String, Object> payload) {
        try {
            String reason = payload.get("reason") != null ? payload.get("reason").toString() : "";
            String czydm = payload.get("czydm") != null ? payload.get("czydm").toString() : "";
            return ResponseEntity.ok(sampleService.invalidateSample(brxxId, reason, czydm));
        } catch (Exception e) {
            log.error("样本作废失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @OperationLog(value = "修改审核时间", module = "样本管理")
    @PutMapping("/updateTime/{brxxId}")
    public ResponseEntity<Map<String, Object>> updateSampleTime(
            @PathVariable("brxxId") Integer brxxId,
            @RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(sampleService.updateSampleTime(brxxId, payload));
        } catch (Exception e) {
            log.error("修改时间失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @OperationLog(value = "样本核收", module = "样本管理")
    @PostMapping("/accept")
    public ResponseEntity<Map<String, Object>> acceptSample(@RequestBody Map<String, Object> payload) {
        try {
            String barcode = payload.get("barcode") != null ? payload.get("barcode").toString() : "";
            String czydm = payload.get("czydm") != null ? payload.get("czydm").toString() : "";
            return ResponseEntity.ok(sampleService.acceptSample(barcode, czydm));
        } catch (Exception e) {
            log.error("样本核收失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @OperationLog(value = "样本拒收", module = "样本管理")
    @PostMapping("/reject")
    public ResponseEntity<Map<String, Object>> rejectSample(@RequestBody Map<String, Object> payload) {
        try {
            Integer brxxId = payload.get("brxxId") != null
                ? Integer.parseInt(payload.get("brxxId").toString()) : null;
            String reason = payload.get("reason") != null ? payload.get("reason").toString() : "";
            String czydm = payload.get("czydm") != null ? payload.get("czydm").toString() : "";
            return ResponseEntity.ok(sampleService.rejectSample(brxxId, reason, czydm));
        } catch (Exception e) {
            log.error("样本拒收失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @OperationLog(value = "核收签名", module = "样本管理")
    @PostMapping("/acceptWithSign")
    public ResponseEntity<Map<String, Object>> acceptWithSign(@RequestBody Map<String, Object> payload) {
        try {
            Integer brxxId = payload.get("brxxId") != null
                ? Integer.parseInt(payload.get("brxxId").toString()) : null;
            String czydm = payload.get("czydm") != null ? payload.get("czydm").toString() : "";
            String signature = payload.get("signature") != null ? payload.get("signature").toString() : "";
            return ResponseEntity.ok(sampleService.acceptWithSign(brxxId, czydm, signature));
        } catch (Exception e) {
            log.error("核收签名失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping("/rawData/{brxxId}")
    public ResponseEntity<Map<String, Object>> getRawData(@PathVariable("brxxId") Integer brxxId) {
        try {
            return ResponseEntity.ok(sampleService.getRawData(brxxId));
        } catch (Exception e) {
            log.error("获取原始数据失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @OperationLog(value = "样本转移", module = "样本管理")
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, Object>> transferSample(@RequestBody Map<String, Object> payload) {
        try {
            Integer brxxId = payload.get("brxxId") != null
                ? Integer.parseInt(payload.get("brxxId").toString()) : null;
            return ResponseEntity.ok(sampleService.transferSample(brxxId, payload));
        } catch (Exception e) {
            log.error("样本转移失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @OperationLog(value = "快捷提取", module = "样本管理")
    @PostMapping("/quickExtract")
    public ResponseEntity<Map<String, Object>> quickExtract(@RequestBody Map<String, Object> payload) {
        try {
            Integer brxxId = payload.get("brxxId") != null
                ? Integer.parseInt(payload.get("brxxId").toString()) : null;
            return ResponseEntity.ok(sampleService.quickExtract(brxxId));
        } catch (Exception e) {
            log.error("快捷提取失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @OperationLog(value = "样本转质控", module = "样本管理")
    @PostMapping("/convertToQC")
    public ResponseEntity<Map<String, Object>> convertToQC(@RequestBody Map<String, Object> payload) {
        try {
            Integer brxxId = payload.get("brxxId") != null
                ? Integer.parseInt(payload.get("brxxId").toString()) : null;
            return ResponseEntity.ok(sampleService.convertToQC(brxxId));
        } catch (Exception e) {
            log.error("样本转质控失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", e.getMessage());
            return ResponseEntity.status(500).body(resp);
        }
    }

    @GetMapping("/dropdown-options")
    public ResponseEntity<Map<String, Object>> getDropdownOptions() {
        try {
            Map<String, Object> result = new HashMap<>();
            result.put("patientTypes", sysBrlbMapper.listActiveForDropdown());
            result.put("sampleTypes", sysBbzlDictMapper.listSpecimenTypes(null));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取下拉选项失败", e);
            Map<String, Object> resp = new HashMap<>();
            resp.put("patientTypes", new ArrayList<>());
            resp.put("sampleTypes", new ArrayList<>());
            return ResponseEntity.ok(resp);
        }
    }
}
