package com.lis.service;

import com.lis.mapper.BgxtXmzhMxMapper;
import com.lis.mapper.SysZkpdMapper;
import com.lis.mapper.ZkNyskclMapper;
import com.lis.mapper.ZkNyzkjgMapper;
import com.lis.mapper.ZkNyzkpjMapper;
import com.lis.mapper.ZkNyzkxmMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lis.service.WestgardRuleService.WestgardViolation;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class QcService {

    @Autowired
    private SysZkpdMapper sysZkpdMapper;
    @Autowired
    private ZkNyzkjgMapper zkNyzkjgMapper;
    @Autowired
    private ZkNyzkxmMapper zkNyzkxmMapper;
    @Autowired
    private BgxtXmzhMxMapper bgxtXmzhMxMapper;
    @Autowired
    private ZkNyskclMapper zkNyskclMapper;
    @Autowired
    private ZkNyzkpjMapper zkNyzkpjMapper;
    @Autowired
    private WestgardRuleService westgardRuleService;

    public List<Map<String, Object>> searchProducts(Integer sbDjid, String keyword) {
        if (keyword != null) {
            keyword = keyword.trim();
        }
        List<Map<String, Object>> list = sysZkpdMapper.searchProducts(sbDjid, keyword);
        for (Map<String, Object> row : list) {
            convertToBoolean(row, "zxbz", "sybz");
        }
        return list;
    }

    public List<Map<String, Object>> listAllProducts() {
        return searchProducts(null, null);
    }

    @Transactional
    public Map<String, Object> addProduct(Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        String zwmc = (String) payload.getOrDefault("zwmc", "");
        String ph = (String) payload.getOrDefault("ph", "");
        if (zwmc.isEmpty() || ph.isEmpty()) {
            resp.put("success", false);
            resp.put("message", "名称和批号不能为空");
            return resp;
        }
        String ywmc = (String) payload.getOrDefault("ywmc", "");
        String sccj = (String) payload.getOrDefault("sccj", "");
        String zkpsm = (String) payload.getOrDefault("zkpsm", "");
        boolean sybz = Boolean.TRUE.equals(payload.get("sybz"));
        Integer sbDjid = parseInteger(payload.get("sb_djid"));
        sysZkpdMapper.insertProduct(sbDjid, zwmc, ywmc, zkpsm, ph, sccj, sybz ? 1 : 0);
        resp.put("success", true);
        resp.put("message", "新增成功");
        return resp;
    }

    @Transactional
    public Map<String, Object> updateProduct(Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        Integer zkpid = parseInteger(payload.get("zkpid"));
        if (zkpid == null) {
            resp.put("success", false);
            resp.put("message", "ID不能为空");
            return resp;
        }
        String zwmc = (String) payload.getOrDefault("zwmc", "");
        String ywmc = (String) payload.getOrDefault("ywmc", "");
        String ph = (String) payload.getOrDefault("ph", "");
        String sccj = (String) payload.getOrDefault("sccj", "");
        String zkpsm = (String) payload.getOrDefault("zkpsm", "");
        boolean sybz = Boolean.TRUE.equals(payload.get("sybz"));
        Integer sbDjid = parseInteger(payload.get("sb_djid"));
        sysZkpdMapper.updateProduct(sbDjid, zwmc, ywmc, zkpsm, ph, sccj, sybz ? 1 : 0, zkpid);
        resp.put("success", true);
        resp.put("message", "更新成功");
        return resp;
    }

    @Transactional
    public Map<String, Object> deleteProduct(Integer zkpid) {
        Map<String, Object> resp = new HashMap<>();
        sysZkpdMapper.deleteProduct(zkpid);
        resp.put("success", true);
        resp.put("message", "删除成功");
        return resp;
    }

    public List<Map<String, Object>> listProjects(Integer zkpid) {
        List<Map<String, Object>> list = zkNyzkxmMapper.findProjects(zkpid);
        for (Map<String, Object> row : list) {
            Object xmidObj = row.get("xmid");
            if (xmidObj != null) {
                try {
                    String xmzwmc = bgxtXmzhMxMapper.findXmzwmcById(xmidObj.toString());
                    if (xmzwmc != null) row.put("xmmc", xmzwmc);
                    else row.put("xmmc", "");
                } catch (Exception e) {
                    row.put("xmmc", "");
                }
            }
            ensureInteger(row, "zkxmid");
            ensureInteger(row, "zkpid");
            ensureInteger(row, "xmid");
        }
        return list;
    }

    public List<Map<String, Object>> listAllProjects() {
        List<Map<String, Object>> list = zkNyzkxmMapper.findProjects(null);
        for (Map<String, Object> row : list) {
            Object xmidObj = row.get("xmid");
            if (xmidObj != null) {
                try {
                    Map<String, Object> xmInfo = bgxtXmzhMxMapper.findXmInfoById(xmidObj.toString());
                    if (xmInfo != null && xmInfo.get("xmzwmc") != null) {
                        row.put("xmmc", xmInfo.get("xmzwmc"));
                    }
                } catch (Exception ignored) {}
            }
            ensureInteger(row, "zkxmid");
            ensureInteger(row, "zkpid");
            ensureInteger(row, "xmid");
        }
        return list;
    }

    @Transactional
    public Map<String, Object> addProject(Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        Integer zkpid = parseInteger(payload.get("zkpid"));
        Object xmidObj = payload.get("xmid");
        if (zkpid == null || xmidObj == null) {
            resp.put("success", false);
            resp.put("message", "参数不完整");
            return resp;
        }
        String bz = (String) payload.getOrDefault("bz", "");
        String bzc = (String) payload.getOrDefault("bzc", "");
        String zkdz = (String) payload.getOrDefault("zkdz", "");
        String zkgz = (String) payload.getOrDefault("zkgz", "");
        String dxLx = payload.getOrDefault("dx_lx", "0").toString();
        String fhbz = (String) payload.getOrDefault("fhbz", "");
        zkNyzkxmMapper.insertProject(zkpid, Integer.parseInt(xmidObj.toString()), bz, bzc, zkdz, zkgz, Integer.parseInt(dxLx), parseIntegerDefault(payload.get("fhbz"), 0));
        resp.put("success", true);
        resp.put("message", "添加成功");
        return resp;
    }

    @Transactional
    public Map<String, Object> deleteProject(Integer zkxmid) {
        Map<String, Object> resp = new HashMap<>();
        zkNyzkxmMapper.deleteProject(zkxmid);
        resp.put("success", true);
        resp.put("message", "删除成功");
        return resp;
    }

    public List<Map<String, Object>> listEvaluations(Integer zkpid, String date) {
        return zkNyzkpjMapper.listEvaluations(zkpid, date);
    }

    @Transactional
    public Map<String, Object> addEvaluation(Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        Integer zkpid = parseInteger(payload.get("zkpid"));
        if (zkpid == null) {
            resp.put("success", false);
            resp.put("message", "请选择质控品");
            return resp;
        }
        String pjmd = (String) payload.getOrDefault("pjmd", "");
        String pjjg = (String) payload.getOrDefault("pjjg", "");
        String pjjsyj = (String) payload.getOrDefault("pjjsyj", "");
        String pjczy = (String) payload.getOrDefault("pjczy", "");
        zkNyzkpjMapper.insertEvaluation(zkpid, pjmd, pjjg, pjjsyj, pjczy);
        resp.put("success", true);
        resp.put("message", "新增成功");
        return resp;
    }

    @Transactional
    public Map<String, Object> deleteEvaluation(Integer id) {
        Map<String, Object> resp = new HashMap<>();
        int rows = zkNyzkpjMapper.deleteEvaluation(id);
        if (rows > 0) {
            resp.put("success", true);
            resp.put("message", "删除成功");
        } else {
            resp.put("success", false);
            resp.put("message", "记录不存在");
        }
        return resp;
    }

    public Map<String, Object> getQcAnalysis(Integer zkpid, Integer zkxmid, String begDate, String endDate, Integer days) {
        Map<String, Object> result = new HashMap<>();
        int queryDays = (days != null && days > 0) ? days : 30;
        List<Map<String, Object>> rawData = zkNyzkjgMapper.findAnalysisData(zkpid, zkxmid, begDate, endDate, queryDays);

        for (Map<String, Object> row : rawData) {
            row.put("xmmc", "");
            row.put("xmdw", "");
            Object yhsjObj = row.get("yhsj");
            if (yhsjObj == null || yhsjObj.toString().trim().isEmpty()) {
                row.put("yhsj", row.get("yssj"));
            }
            convertToBoolean(row, "sybz");
            convertToBoolean(row, "skbz");

            Object zkxmidObj = row.get("zkxmid");
            if (zkxmidObj != null) {
                try {
                    Map<String, Object> xmInfo = bgxtXmzhMxMapper.findXmInfoById(zkxmidObj.toString());
                    if (xmInfo != null) {
                        if (xmInfo.get("xmzwmc") != null) row.put("xmmc", xmInfo.get("xmzwmc"));
                        if (xmInfo.get("xmdw") != null) row.put("xmdw", xmInfo.get("xmdw"));
                    }
                } catch (Exception ignored) {
                }
            }
        }

        int total = rawData.size();
        int valid = 0;
        int invalid = 0;
        List<Map<String, Object>> processedData = new ArrayList<>();
        List<Double> cumulativeResults = new ArrayList<>();

        for (Map<String, Object> item : rawData) {
            Object sybzObj = item.get("sybz");
            boolean isValid = Boolean.TRUE.equals(sybzObj);

            if (!isValid) {
                item.put("isOutOfControl", false);
                item.put("status", "无效");
                item.put("westgardRules", "");
                processedData.add(item);
                continue;
            }

            valid++;
            boolean isOutOfControl = false;
            String status = "在控";
            String westgardRules = "";

            Object yhsjObj = item.get("yhsj");
            Object dxLxObj = item.get("dx_lx");
            Object targetBzObj = item.get("target_bz");
            Object targetBzcObj = item.get("target_bzc");
            Object zkgzObj = item.get("zkgz");
            Object zkdzObj = item.get("zkdz");

            if (yhsjObj != null && targetBzObj != null) {
                String yhsj = yhsjObj.toString();
                String targetBz = targetBzObj.toString();
                int dxLx = (dxLxObj != null) ? Integer.parseInt(dxLxObj.toString()) : 0;

                if (dxLx == 1) {
                    if (!yhsj.equals(targetBz)) {
                        isOutOfControl = true;
                        status = "失控";
                    }
                } else {
                    try {
                        double yhsjValue = parseNumericResult(yhsj);
                        double targetValue = parseNumericResult(targetBz);
                        double bzc = 0;
                        if (targetBzcObj != null) {
                            bzc = parseNumericResult(targetBzcObj.toString());
                        }
                        boolean outOfControl = false;
                        double zkdz = 0;
                        double zkgz = 0;
                        if (zkdzObj != null) {
                            zkdz = parseNumericResult(zkdzObj.toString());
                        }
                        if (zkgzObj != null) {
                            zkgz = parseNumericResult(zkgzObj.toString());
                        }
                        if (zkdz > 0 || zkgz > 0) {
                            if (yhsjValue < zkdz || yhsjValue > zkgz) {
                                outOfControl = true;
                            }
                        }
                        if (!outOfControl && bzc > 0) {
                            double diff = Math.abs(yhsjValue - targetValue);
                            double sd = diff / bzc;
                            if (sd > 2) {
                                outOfControl = true;
                            }
                        }

                        cumulativeResults.add(yhsjValue);
                        if (bzc > 0 && cumulativeResults.size() >= 1) {
                            List<WestgardViolation> violations = westgardRuleService.evaluate(
                                    new ArrayList<>(cumulativeResults), targetValue, bzc);
                            if (!violations.isEmpty()) {
                                List<String> ruleNames = new ArrayList<>();
                                List<String> ruleMessages = new ArrayList<>();
                                for (WestgardViolation v : violations) {
                                    ruleNames.add(v.getRuleName());
                                    ruleMessages.add(v.getMessage());
                                }
                                westgardRules = String.join(",", ruleNames);
                                item.put("westgardMessages", String.join("; ", ruleMessages));
                                for (WestgardViolation v : violations) {
                                    if (v.isError()) {
                                        outOfControl = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if (outOfControl) {
                            isOutOfControl = true;
                            status = "失控";
                        }
                    } catch (NumberFormatException e) {
                    }
                }
            } else {
                if (yhsjObj != null) {
                    try {
                        cumulativeResults.add(parseNumericResult(yhsjObj.toString()));
                    } catch (NumberFormatException e) {}
                }
            }

            if (isOutOfControl) {
                invalid++;
            }

            item.put("skbz", isOutOfControl);
            item.put("isOutOfControl", isOutOfControl);
            item.put("status", status);
            item.put("westgardRules", westgardRules);
            processedData.add(item);
        }

        result.put("total", total);
        result.put("valid", valid);
        result.put("invalid", invalid);
        result.put("invalidRate", valid > 0 ? String.format("%.1f", (invalid * 100.0 / valid)) : "0.0");
        result.put("data", processedData);
        return result;
    }

    public List<Map<String, Object>> listProcessingRecords(Integer zkxmid, String month) {
        String monthStart = null;
        if (month != null && !month.isEmpty()) {
            monthStart = month + "-01";
        }
        return zkNyskclMapper.findProcessingRecords(zkxmid, monthStart);
    }

    @Transactional
    public Map<String, Object> saveProcessingRecord(Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        Integer zkxmid = parseInteger(payload.get("zkxmid"));
        if (zkxmid == null) {
            resp.put("success", false);
            resp.put("message", "参数不完整");
            return resp;
        }
        String zkcl = (String) payload.getOrDefault("zkcl", "");
        String czydmClr = (String) payload.getOrDefault("czydm_clr", "");
        String ksrq = (String) payload.getOrDefault("ksrq", "");
        String jsrq = (String) payload.getOrDefault("jsrq", "");
        zkNyskclMapper.insertProcessingRecord(zkxmid, zkcl, czydmClr, ksrq, jsrq);
        resp.put("success", true);
        resp.put("message", "保存成功");
        return resp;
    }

    public List<Map<String, Object>> getQcData(Integer zkxmid, String begDate, String endDate) {
        List<Map<String, Object>> list = zkNyzkjgMapper.findQcData(zkxmid, begDate, endDate);
        for (Map<String, Object> item : list) {
            convertToBoolean(item, "sybz", "skbz");
            Object xmidObj = item.get("xmdm");
            if (xmidObj != null) {
                try {
                    String xmzwmc = bgxtXmzhMxMapper.findXmzwmcById(xmidObj.toString());
                    if (xmzwmc != null) {
                        item.put("xmmc", xmzwmc);
                    }
                } catch (Exception e) {
                    item.put("xmmc", "");
                }
            }
        }
        return list;
    }

    public Map<String, Object> getProduct(Integer zkpid) {
        List<Map<String, Object>> all = sysZkpdMapper.searchProducts(null, null);
        for (Map<String, Object> p : all) {
            convertToBoolean(p, "sybz");
            if (zkpid.equals(parseInteger(p.get("zkpid")))) return p;
        }
        return null;
    }

    public Map<String, Object> updateProduct(Integer zkpid, Map<String, Object> payload) {
        payload.put("zkpid", zkpid);
        return updateProduct(payload);
    }

    public List<Map<String, Object>> listDailyResults(Integer zkpid, String date, Integer days) {
        List<Map<String, Object>> list = zkNyzkjgMapper.findDailyResults(zkpid, date, days);
        for (Map<String, Object> row : list) {
            convertToBoolean(row, "sybz", "skbz");
        }
        return list;
    }

    @Transactional
    public Map<String, Object> addDailyResult(Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        try {
            Integer zkxmid = parseInteger(payload.get("zkxmid"));
            String yssj = (String) payload.getOrDefault("yssj", "");
            String yhsj = (String) payload.getOrDefault("yhsj", "");
            String resultDate = (String) payload.getOrDefault("resultDate", "");
            boolean sybz = Boolean.TRUE.equals(payload.get("sybz"));
            int skbz = calculateSkbz(zkxmid, yhsj);
            if (resultDate != null && !resultDate.isEmpty()) {
                zkNyzkjgMapper.insertResultWithDate(zkxmid, yssj, yhsj, resultDate, sybz ? 1 : 0, skbz);
            } else {
                zkNyzkjgMapper.insertResultWithCurdate(zkxmid, yssj, yhsj, sybz ? 1 : 0, skbz);
            }
            resp.put("success", true);
            resp.put("message", "录入成功");
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "录入失败: " + e.getMessage());
        }
        return resp;
    }

    @Transactional
    public Map<String, Object> deleteDailyResult(Integer id) {
        Map<String, Object> resp = new HashMap<>();
        zkNyzkjgMapper.deleteResult(id);
        resp.put("success", true);
        resp.put("message", "删除成功");
        return resp;
    }

    public List<Map<String, Object>> listQcProjects(Integer zkpid) {
        return listProjects(zkpid);
    }

    public List<Map<String, Object>> listAvailableProjects(Integer zkpid, Integer sbDjid) {
        return bgxtXmzhMxMapper.findAllProjects();
    }

    @Transactional
    public Map<String, Object> addQcProject(Map<String, Object> payload) {
        Integer zkpid = parseInteger(payload.get("zkpid"));
        Object xmidObj = payload.get("xmid");
        if (xmidObj != null) {
            payload.put("xmid", parseInteger(xmidObj));
        }
        return addProject(payload);
    }

    @Transactional
    public Map<String, Object> updateQcProject(Integer zkxmid, Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        String bz = (String) payload.getOrDefault("bz", "");
        String bzc = (String) payload.getOrDefault("bzc", "");
        String zkdz = (String) payload.getOrDefault("zkdz", "");
        String zkgz = (String) payload.getOrDefault("zkgz", "");
        int dxLx = parseIntegerDefault(payload.get("dx_lx"), 0);
        int fhbz = payload.get("fhbz") != null ? parseIntegerDefault(payload.get("fhbz"), 0) : 0;
        String bc = (String) payload.getOrDefault("bc", "");
        zkNyzkxmMapper.updateProject(bz, bzc, zkdz, zkgz, dxLx, fhbz, bc, zkxmid);
        resp.put("success", true);
        resp.put("message", "更新成功");
        return resp;
    }

    @Transactional
    public Map<String, Object> deleteQcProject(Integer id) {
        return deleteProject(id);
    }

    public List<Map<String, Object>> getProductsWithData() {
        return zkNyzkjgMapper.selectProductsWithData();
    }

    public List<Map<String, Object>> getCvTrend(Integer zkxmid, Integer zkpid, String begDate, String endDate) {
        if (begDate == null || begDate.isEmpty()) begDate = LocalDate.now().minusMonths(6).toString();
        if (endDate == null || endDate.isEmpty()) endDate = LocalDate.now().toString();
        return zkNyzkjgMapper.selectCvTrend(zkxmid, zkpid, begDate, endDate);
    }

    public List<Map<String, Object>> getZScoreData(Integer zkxmid, Integer zkpid, String begDate, String endDate) {
        if (begDate == null || begDate.isEmpty()) begDate = LocalDate.now().minusMonths(6).toString();
        if (endDate == null || endDate.isEmpty()) endDate = LocalDate.now().toString();
        return zkNyzkjgMapper.selectZScoreData(zkxmid, zkpid, begDate, endDate);
    }

    public Map<String, Object> getQcStats(Integer zkxmid, Integer zkpid, String begDate, String endDate) {
        if (begDate == null || begDate.isEmpty()) begDate = LocalDate.now().minusMonths(6).toString();
        if (endDate == null || endDate.isEmpty()) endDate = LocalDate.now().toString();
        Map<String, Object> stats = zkNyzkjgMapper.selectQcStats(zkxmid, zkpid, begDate, endDate);
        if (stats == null) stats = new HashMap<>();
        return stats;
    }

    public List<Map<String, Object>> getZScoreMultiData(Integer zkpid, String begDate, String endDate) {
        if (zkpid == null) return new ArrayList<>();
        if (begDate == null || begDate.isEmpty()) begDate = LocalDate.now().minusMonths(6).toString();
        if (endDate == null || endDate.isEmpty()) endDate = LocalDate.now().toString();
        List<Map<String, Object>> projects = zkNyzkxmMapper.findByZkpid(zkpid);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> proj : projects) {
            Integer zkxmid = parseInteger(proj.get("zkxmid"));
            if (zkxmid == null) continue;
            List<Map<String, Object>> zData = zkNyzkjgMapper.selectZScoreData(zkxmid, null, begDate, endDate);
            if (zData == null || zData.isEmpty()) continue;
            Map<String, Object> entry = new HashMap<>();
            String xmmc = proj.get("xmzwmc") != null ? proj.get("xmzwmc").toString() : ("项目" + zkxmid);
            String level = proj.get("zknd") != null ? proj.get("zknd").toString() : "";
            String label = xmmc;
            if ("高".equals(level) || "g".equalsIgnoreCase(level)) label = xmmc + "(高)";
            else if ("中".equals(level) || "z".equalsIgnoreCase(level)) label = xmmc + "(中)";
            else if ("低".equals(level) || "d".equalsIgnoreCase(level)) label = xmmc + "(低)";
            entry.put("label", label);
            entry.put("zkxmid", zkxmid);
            entry.put("data", zData);
            result.add(entry);
        }
        return result;
    }

    public String exportAnalysisCsv(Integer zkpid, Integer zkxmid, String begDate, String endDate) {
        int days = 0;
        if ((begDate == null || begDate.isEmpty()) && (endDate == null || endDate.isEmpty())) {
            days = 365;
        }
        Map<String, Object> analysis = getQcAnalysis(zkpid, zkxmid, begDate, endDate, days);
        List<Map<String, Object>> data = (List<Map<String, Object>>) analysis.getOrDefault("data", new ArrayList<>());
        StringBuilder sb = new StringBuilder();
        sb.append("日期,靶值,实测值,SD偏移,在控状态\n");
        for (Map<String, Object> row : data) {
            String yhsj = row.get("yhsj") != null ? row.get("yhsj").toString() : "";
            String targetBz = row.get("target_bz") != null ? row.get("target_bz").toString() : "";
            String targetBzc = row.get("target_bzc") != null ? row.get("target_bzc").toString() : "0";
            String syrq = row.get("syrq") != null ? row.get("syrq").toString() : "";
            String status = row.get("status") != null ? row.get("status").toString() : "";
            double sd = 0;
            try {
                double val = Double.parseDouble(yhsj);
                double bz = Double.parseDouble(targetBz);
                double bzc = Double.parseDouble(targetBzc);
                if (bzc != 0) sd = (val - bz) / bzc;
            } catch (Exception ignored) {}
            sb.append('"').append(syrq).append('"').append(',')
              .append(targetBz).append(',')
              .append(yhsj).append(',')
              .append(String.format("%.2f", sd)).append(',')
              .append(status).append('\n');
        }
        return sb.toString();
    }

    private int calculateSkbz(Integer zkxmid, String jyjg) {
        try {
            Map<String, Object> qcRule = zkNyzkxmMapper.findRuleByZkxmid(zkxmid);
            if (qcRule == null) return 0;

            Object dxLxObj = qcRule.get("dx_lx");
            Object targetBzObj = qcRule.get("bz");
            Object zkdzObj = qcRule.get("zkdz");
            Object zkgzObj = qcRule.get("zkgz");
            Object bzcObj = qcRule.get("bzc");

            if (jyjg == null || jyjg.trim().isEmpty() || targetBzObj == null) return 0;

            int dxLx = (dxLxObj != null) ? Integer.parseInt(dxLxObj.toString()) : 0;

            if (dxLx == 1) {
                if (!jyjg.equals(targetBzObj.toString())) return 1;
            } else {
                double jyjgValue = parseNumericResult(jyjg);
                double targetValue = parseNumericResult(targetBzObj.toString());
                double bzc = (bzcObj != null) ? parseNumericResult(bzcObj.toString()) : 0;
                double zkdz = (zkdzObj != null) ? parseNumericResult(zkdzObj.toString()) : 0;
                double zkgz = (zkgzObj != null) ? parseNumericResult(zkgzObj.toString()) : 0;

                if (zkdz > 0 || zkgz > 0) {
                    if (jyjgValue < zkdz || jyjgValue > zkgz) return 1;
                } else if (bzc > 0) {
                    double sd = Math.abs(jyjgValue - targetValue) / bzc;
                    if (sd > 2) return 1;
                }
            }
        } catch (Exception e) {
            log.error("规则判断失败: {}", e.getMessage());
        }
        return 0;
    }

    private double parseNumericResult(String value) {
        if (value == null || value.trim().isEmpty()) return 0;
        value = value.trim();
        int plusCount = 0, minusCount = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '+') plusCount++;
            else if (c == '-') minusCount++;
        }
        if (plusCount > 0 || minusCount > 0) {
            if (plusCount > 0 && minusCount == 0) return plusCount;
            if (minusCount > 0 && plusCount == 0) return -minusCount;
            if (plusCount > 0 && minusCount > 0) {
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

    private Integer parseInteger(Object obj) {
        if (obj == null) return null;
        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseIntegerDefault(Object obj, int defaultValue) {
        Integer result = parseInteger(obj);
        return result != null ? result : defaultValue;
    }

    private void convertToBoolean(Map<String, Object> row, String... fields) {
        for (String field : fields) {
            Object val = row.get(field);
            if (val != null) {
                if (val instanceof Boolean) {
                } else if (val instanceof Number) {
                    row.put(field, ((Number) val).intValue() != 0);
                } else {
                    row.put(field, "1".equals(val.toString()) || "true".equalsIgnoreCase(val.toString()));
                }
            }
        }
    }

    private void ensureInteger(Map<String, Object> row, String field) {
        Object val = row.get(field);
        if (val != null && !(val instanceof Integer)) {
            try {
                row.put(field, Integer.parseInt(val.toString()));
            } catch (Exception ignored) {
            }
        }
    }
}
