package com.lis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.enums.SampleStatus;
import com.lis.entity.BgxtBrxx;
import com.lis.entity.BgxtJyjg;
import com.lis.entity.BgxtSampleReject;
import com.lis.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SampleService {

    @Autowired
    private BgxtBrxxMapper brxxMapper;

    @Autowired
    private BgxtJyjgMapper jyjgMapper;

    @Autowired
    private SysJyxmMapper sysJyxmMapper;

    @Autowired
    private SysXmckzMapper sysXmckzMapper;

    @Autowired
    private SysBbzlDictMapper sysBbzlDictMapper;

    @Autowired
    private BgxtXmzhMxMapper bgxtXmzhMxMapper;

    @Autowired
    private BgxtXmzhZbMapper bgxtXmzhZbMapper;

    @Autowired
    private SysCjyszMxMapper sysCjyszMxMapper;

    @Autowired
    private SysCjyszZbMapper sysCjyszZbMapper;

    @Autowired
    private AuditVerificationService auditVerificationService;

    @Autowired
    private BgxtJgdybMapper jgdybMapper;

    @Autowired
    private HISNotificationService hisNotificationService;

    @Autowired
    private CASignatureService caSignatureService;

    @Autowired
    private BgxtHisXmMapper bgxtHisXmMapper;

    @Autowired
    private BgxtSampleRejectMapper sampleRejectMapper;

    @Autowired
    private TemplateRenderService templateRenderService;

    @Autowired
    private com.lis.mapper.InstrumentMapper instrumentMapper;

    @Autowired
    private SystemService systemService;

    @Autowired
    private BgxtHisXmMapper hisXmMapper;

    @Transactional
    public Map<String, Object> saveSample(Map<String, Object> payload) {
        log.debug("=== SAVE DEBUG START ===");
        log.debug("payload keys: {}", payload.keySet());

        @SuppressWarnings("unchecked")
        Map<String, Object> patient = (Map<String, Object>) payload.get("patient");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> results = (List<Map<String, Object>>) payload.get("results");

        log.info("patient: {}", patient);
        log.info("results count: {}", results == null ? "null" : results.size());
        if (results != null && !results.isEmpty()) {
            log.debug("first result: {}", results.get(0));
        }

        if (patient == null) {
            patient = new HashMap<>();
        }

        validateSaveRequest(patient);

        Integer brxxId = resolveBrxxId(patient, payload);
        Integer sbDjid = resolveSbDjid(payload);
        String bgbh = (String) payload.get("bgbh");
        String bgmc = (String) payload.get("bgmc");
        String bgbt = (String) payload.get("bgbt");
        String bgyj = (String) payload.get("bgyj");
        Integer bgjglx = payload.get("bgjglx") != null ? ((Number) payload.get("bgjglx")).intValue() : null;

        checkSampleStatusForUpdate(brxxId);

        Integer bbzlCode = mapSampleTypeToBbzl((String) patient.getOrDefault("sampleType", ""));
        if (bbzlCode == null) {
            bbzlCode = 1;
        }

        Integer brxxIdFinal = savePatientInfo(patient, brxxId, bbzlCode, sbDjid, bgbh, bgmc, bgbt, bgyj, bgjglx);

        if (results != null) {
            saveTestResults(brxxIdFinal, results, sbDjid);
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("brxx_id", brxxIdFinal);
        resp.put("message", "样本信息已保存");

        try {
            hisXmMapper.insertFromCombo(brxxIdFinal);
        } catch (Exception e) {
            log.debug("生成费用记录(主路径)失败，尝试备用路径: {}", e.getMessage());
            try {
                hisXmMapper.insertFromComboFallback(brxxIdFinal);
            } catch (Exception ex) {
                log.debug("生成费用记录(备用路径)也失败: {}", ex.getMessage());
            }
        }

        return resp;
    }

    private void validateSaveRequest(Map<String, Object> patient) {
        String name = (String) patient.getOrDefault("name", "");
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("病人姓名不能为空！");
        }

        String sampleNo = (String) patient.getOrDefault("sampleNo", "");
        if (sampleNo == null || sampleNo.trim().isEmpty()) {
            throw new IllegalArgumentException("样本号不能为空！");
        }

        String sampleType = (String) patient.getOrDefault("sampleType", "");
        if (sampleType == null || sampleType.trim().isEmpty()) {
            throw new IllegalArgumentException("必须要设置标本种类！");
        }

        String type = (String) patient.getOrDefault("type", "");
        String patientId = (String) patient.getOrDefault("patientId", "");
        if (type.contains("住院") && (patientId == null || patientId.trim().isEmpty())) {
            throw new IllegalArgumentException("住院病人必须输入住院号，请输入正确的住院号！");
        }

        Object ageObj = patient.get("age");
        if (ageObj != null) {
            try {
                int age = Integer.parseInt(ageObj.toString());
                if (age < 0) {
                    throw new IllegalArgumentException("年龄不能小于0！");
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private Integer resolveBrxxId(Map<String, Object> patient, Map<String, Object> payload) {
        Object brxxIdObj = patient.get("brxx_id");
        if (brxxIdObj == null) {
            brxxIdObj = payload.get("brxx_id");
        }
        if (brxxIdObj != null) {
            try {
                return Integer.parseInt(brxxIdObj.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private Integer resolveSbDjid(Map<String, Object> payload) {
        Object sbDjidObj = payload.get("sb_djid");
        if (sbDjidObj != null) {
            try {
                return Integer.parseInt(sbDjidObj.toString());
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    private void checkSampleStatusForUpdate(Integer brxxId) {
        if (brxxId == null) {
            return;
        }
        Integer currentStatus = brxxMapper.selectStatusById(brxxId);
        if (currentStatus != null && currentStatus == 2) {
            throw new IllegalArgumentException("样本已经审核，无法对修改的内容进行保存！");
        }
        if (currentStatus != null && currentStatus == 3) {
            throw new IllegalArgumentException("样本已经打印，无法对修改的内容进行保存！");
        }
    }

    private Integer savePatientInfo(Map<String, Object> patient, Integer brxxId, Integer bbzlCode, Integer sbDjid, String bgbh, String bgmc, String bgbt, String bgyj, Integer bgjglx) {
        String name = (String) patient.getOrDefault("name", "");
        String sampleNo = (String) patient.getOrDefault("sampleNo", "");
        String barcode = (String) patient.getOrDefault("barcode", "");
        String patientId = (String) patient.getOrDefault("patientId", "");
        String type = (String) patient.getOrDefault("type", "");
        String ageUnit = (String) patient.getOrDefault("ageUnit", "Y");
        String experimentStatus = (String) patient.getOrDefault("experimentStatus", "");
        String bedNo = (String) patient.getOrDefault("bedNo", "");
        String diagnosis = (String) patient.getOrDefault("diagnosis", "");
        String sjys = (String) patient.getOrDefault("doctor", "");
        String jyys = (String) patient.getOrDefault("inspectingPhysician", "");
        String shys = (String) patient.getOrDefault("reviewingPhysician", "");
        String ksdm = (String) patient.getOrDefault("dept", "");
        String bz = (String) patient.getOrDefault("remarks", "");
        String bz2 = (String) patient.getOrDefault("additionalRemarks", "");
        String tjdw = (String) patient.getOrDefault("physicalExamUnit", "");
        String zjhm = (String) patient.getOrDefault("idNumber", "");
        String lxfs = (String) patient.getOrDefault("contactInfo", "");
        String bbxt = (String) patient.getOrDefault("sampleMorphology", "");
        String czy = (String) patient.getOrDefault("entryPerson", "");

        Integer sex = resolveSex((String) patient.getOrDefault("sex", ""));
        Integer age = resolveAge(patient.get("age"));
        int nllx = resolveNllx(ageUnit);
        Integer brlb = resolveBrlb(type);
        int syqk = resolveSyqk(experimentStatus);

        if (brxxId == null) {
            if (barcode == null || barcode.trim().isEmpty()) {
                barcode = generateBarcode();
            }
            BgxtBrxx entity = new BgxtBrxx();
            entity.setBrxxTmh(barcode);
            entity.setBrbh(patientId);
            entity.setBrxm(name);
            entity.setBrxb(sex);
            entity.setBrnl(age != null ? age.toString() : null);
            entity.setNllx(String.valueOf(nllx));
            entity.setBrlb(brlb);
            entity.setSyh(sampleNo);
            entity.setSyqk(syqk);
            entity.setKsdm(ksdm);
            entity.setBrch(bedNo);
            entity.setBbzl(bbzlCode);
            entity.setSbDjid(sbDjid);
            entity.setBgbh(bgbh);
            entity.setBgmc(bgmc);
            entity.setBgbt(bgbt);
            entity.setBgyj(bgyj);
            entity.setBgjglx(bgjglx);
            entity.setLczd(diagnosis);
            entity.setJyys(jyys);
            entity.setSjys(sjys);
            entity.setShys(shys);
            entity.setBz(bz);
            entity.setBz2(bz2);
            entity.setTjdw(tjdw);
            entity.setZjhm(zjhm);
            entity.setLxfs(lxfs);
            entity.setBbxt(bbxt);
            entity.setCzy(czy);
            entity.setYbzt(0);
            entity.setJyrq(java.time.LocalDateTime.now());
            brxxMapper.insert(entity);
            return entity.getBrxxId();
        } else {
            BgxtBrxx entity = brxxMapper.selectById(brxxId);
            if (entity == null) {
                entity = new BgxtBrxx();
                entity.setBrxxId(brxxId);
            }
            Integer originalYbzt = entity.getYbzt();
            entity.setBrxxTmh(barcode);
            entity.setBrbh(patientId);
            entity.setBrxm(name);
            entity.setBrxb(sex);
            entity.setBrnl(age != null ? age.toString() : null);
            entity.setNllx(String.valueOf(nllx));
            entity.setBrlb(brlb);
            entity.setSyh(sampleNo);
            entity.setSyqk(syqk);
            entity.setKsdm(ksdm);
            entity.setBrch(bedNo);
            entity.setBbzl(bbzlCode);
             entity.setLczd(diagnosis);
             entity.setJyys(jyys);
             entity.setSjys(sjys);
             entity.setShys(shys);
             entity.setBz(bz);
             entity.setBz2(bz2);
             entity.setTjdw(tjdw);
             entity.setZjhm(zjhm);
             entity.setLxfs(lxfs);
             entity.setBbxt(bbxt);
             entity.setCzy(czy);
             entity.setSbDjid(sbDjid);
            entity.setBgbh(bgbh);
            entity.setBgmc(bgmc);
            entity.setBgbt(bgbt);
            entity.setBgyj(bgyj);
            entity.setBgjglx(bgjglx);
            if (originalYbzt != null) {
                entity.setYbzt(originalYbzt);
            }
            if (entity.getBrxxId() != null && brxxMapper.selectById(brxxId) != null) {
                brxxMapper.updateById(entity);
            } else {
                entity.setBrxxId(brxxId);
                brxxMapper.insert(entity);
            }
            return brxxId;
        }
    }

    private void saveTestResults(Integer brxxId, List<Map<String, Object>> results, Integer sbDjid) {
        if (results == null || results.isEmpty()) {
            return;
        }

        jyjgMapper.deleteByBrxxId(brxxId);

        for (Map<String, Object> r : results) {
            log.debug("Processing result: {}", r);

            Integer xmid = resolveXmid(r);
            if (xmid == null) {
                log.debug("SAVE WARNING: xmid is null, skipping result");
                continue;
            }

            Object resultObj = r.get("result");
            String jyjg = resultObj == null ? "" : resultObj.toString();
            log.debug("SAVE SUCCESS: brxxId={}, xmid={}, jyjg={}", brxxId, xmid, jyjg);

            Map<String, Object> ckzRow = findReferenceRange(brxxId, xmid, sbDjid, r);

            String ckz = ckzRow == null ? "" : String.valueOf(ckzRow.getOrDefault("ckz", ""));
            Object ckzgx = ckzRow == null ? null : ckzRow.get("ckzgx");
            Object ckzdx = ckzRow == null ? null : ckzRow.get("ckzdx");
            Object bjzgx = ckzRow == null ? null : ckzRow.get("bjzgx");
            Object bjzdx = ckzRow == null ? null : ckzRow.get("bjzdx");

            String gdbj = computeHighLowFlag(jyjg, ckzgx, ckzdx);

            jyjgMapper.insertResult(brxxId, xmid, jyjg, ckz, ckzgx, ckzdx, bjzgx, bjzdx, gdbj);
        }
    }

    Integer resolveXmid(Map<String, Object> r) {
        Object xmidObj = r.get("xmid");
        if (xmidObj != null) {
            try {
                Integer xmid = Integer.parseInt(xmidObj.toString());
                log.info("Using xmid from frontend: {}", xmid);
                return xmid;
            } catch (NumberFormatException e) {
                log.info("Invalid xmid format: {}", xmidObj);
            }
        }

        Object codeObj = r.get("code");
        if (codeObj != null) {
            String xmdm = codeObj.toString();
            log.debug("Trying to find xmid for xmdm: {}", xmdm);
            Integer xmid = findXmidByCode(xmdm);
            if (xmid != null) {
                return xmid;
            }
        }

        if (xmidObj != null) {
            try {
                return Integer.parseInt(xmidObj.toString());
            } catch (NumberFormatException ignored) {
            }
        }

        return null;
    }

    Integer findXmidByCode(String xmdm) {
        if (xmdm == null || xmdm.trim().isEmpty()) {
            return null;
        }
        try {
            QueryWrapper<com.lis.entity.SysJyxm> wrapper = new QueryWrapper<>();
            wrapper.eq("xmdm", xmdm);
            com.lis.entity.SysJyxm item = sysJyxmMapper.selectOne(wrapper);
            return item != null ? item.getXmid() : null;
        } catch (Exception e) {
            log.info("Not found in sys_jyxm for xmdm: {}", xmdm);
            return null;
        }
    }

    private Map<String, Object> findReferenceRange(Integer brxxId, Integer xmid, Integer sbDjid, Map<String, Object> r) {
        if (xmid != null && xmid > 0) {
            Map<String, Object> result = matchReferenceRange(brxxId, xmid, sbDjid);
            if (result != null) {
                return result;
            }
        }

        Object codeObj = r.get("code");
        if (codeObj != null) {
            String xmdm = codeObj.toString();
            try {
                QueryWrapper<com.lis.entity.SysJyxm> wrapper = new QueryWrapper<>();
                wrapper.eq("xmdm", xmdm).last("LIMIT 1");
                com.lis.entity.SysJyxm item = sysJyxmMapper.selectOne(wrapper);
                Integer realXmid = item != null ? item.getXmid() : null;
                if (realXmid != null) {
                    return matchReferenceRange(brxxId, realXmid, sbDjid);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    Map<String, Object> matchReferenceRange(Integer brxxId, Integer xmid, Integer sbDjid) {
        if (xmid == null) {
            return null;
        }
        return matchReferenceRangeFallback(xmid, sbDjid);
    }

    private Map<String, Object> matchReferenceRangeFallback(Integer xmid, Integer sbDjid) {
        if (xmid == null) {
            return null;
        }
        try {
            if (sbDjid != null) {
                try {
                    QueryWrapper<com.lis.entity.SysXmckz> wrapper = new QueryWrapper<>();
                    wrapper.eq("xmid", xmid).eq("sb_djid", sbDjid)
                        .orderByDesc("bbsgbz", "nlsgbz", "xbsgbz").last("LIMIT 1");
                    com.lis.entity.SysXmckz ckz = sysXmckzMapper.selectOne(wrapper);
                    if (ckz != null) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("ckz", ckz.getCkz());
                        result.put("ckzgx", ckz.getCkzgx());
                        result.put("ckzdx", ckz.getCkzdx());
                        result.put("bjzgx", ckz.getBjzgx());
                        result.put("bjzdx", ckz.getBjzdx());
                        return result;
                    }
                } catch (Exception ignored) {
                }

                try {
                    QueryWrapper<com.lis.entity.SysXmckz> wrapper = new QueryWrapper<>();
                    wrapper.eq("xmid", xmid).isNull("sb_djid")
                        .orderByDesc("bbsgbz", "nlsgbz", "xbsgbz").last("LIMIT 1");
                    com.lis.entity.SysXmckz ckz = sysXmckzMapper.selectOne(wrapper);
                    if (ckz != null) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("ckz", ckz.getCkz());
                        result.put("ckzgx", ckz.getCkzgx());
                        result.put("ckzdx", ckz.getCkzdx());
                        result.put("bjzgx", ckz.getBjzgx());
                        result.put("bjzdx", ckz.getBjzdx());
                        return result;
                    }
                } catch (Exception ignored) {
                }

                try {
                    QueryWrapper<com.lis.entity.SysXmckz> wrapper = new QueryWrapper<>();
                    wrapper.eq("xmid", xmid)
                        .orderByDesc("bbsgbz", "nlsgbz", "xbsgbz").last("LIMIT 1");
                    com.lis.entity.SysXmckz ckz = sysXmckzMapper.selectOne(wrapper);
                    if (ckz != null) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("ckz", ckz.getCkz());
                        result.put("ckzgx", ckz.getCkzgx());
                        result.put("ckzdx", ckz.getCkzdx());
                        result.put("bjzgx", ckz.getBjzgx());
                        result.put("bjzdx", ckz.getBjzdx());
                        return result;
                    }
                } catch (Exception ignored) {
                }
            }

            try {
                QueryWrapper<com.lis.entity.SysXmckz> wrapper = new QueryWrapper<>();
                wrapper.eq("xmid", xmid)
                    .orderByDesc("bbsgbz", "nlsgbz", "xbsgbz").last("LIMIT 1");
                com.lis.entity.SysXmckz ckz = sysXmckzMapper.selectOne(wrapper);
                if (ckz != null) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("ckz", ckz.getCkz());
                    result.put("ckzgx", ckz.getCkzgx());
                    result.put("ckzdx", ckz.getCkzdx());
                    result.put("bjzgx", ckz.getBjzgx());
                    result.put("bjzdx", ckz.getBjzdx());
                    return result;
                }
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private String computeHighLowFlag(String jyjg, Object ckzgx, Object ckzdx) {
        try {
            double v = Double.parseDouble(jyjg.trim());
            if (ckzgx != null) {
                double hi = Double.parseDouble(ckzgx.toString());
                if (hi != 0 && v > hi) return "H";
            }
            if (ckzdx != null) {
                double lo = Double.parseDouble(ckzdx.toString());
                if (lo != 0 && v < lo) return "L";
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    private String generateBarcode() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String prefix = "TM" + dateStr;
        Integer maxSeq = brxxMapper.selectMaxBarcodeSeq(prefix);
        int nextSeq = (maxSeq == null ? 0 : maxSeq) + 1;
        return prefix + String.format("%04d", nextSeq);
    }

    private Integer resolveSex(String sexStr) {
        if ("男".equals(sexStr) || "M".equalsIgnoreCase(sexStr)) {
            return 1;
        } else if ("女".equals(sexStr) || "F".equalsIgnoreCase(sexStr)) {
            return 2;
        }
        return null;
    }

    private Integer resolveAge(Object ageObj) {
        if (ageObj == null) {
            return null;
        }
        try {
            return Integer.parseInt(ageObj.toString());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private int resolveNllx(String ageUnit) {
        if ("M".equalsIgnoreCase(ageUnit)) {
            return 2;
        } else if ("D".equalsIgnoreCase(ageUnit)) {
            return 3;
        }
        return 1;
    }

    private Integer resolveBrlb(String type) {
        if (type.contains("门诊")) {
            return 1;
        } else if (type.contains("住院")) {
            return 2;
        } else if (type.contains("体检")) {
            return 3;
        } else if (type.contains("其他")) {
            return 4;
        } else if (type.contains("科研")) {
            return 5;
        }
        return null;
    }

    private int resolveSyqk(String experimentStatus) {
        if ("紧急".equals(experimentStatus)) {
            return 2;
        } else if ("危急".equals(experimentStatus)) {
            return 3;
        }
        return 1;
    }

    Integer mapSampleTypeToBbzl(String sampleType) {
        if (sampleType == null || sampleType.trim().isEmpty()) {
            return null;
        }
        String trimmed = sampleType.trim();
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            try {
                try {
                    QueryWrapper<com.lis.entity.SysBbzlDict> wrapper = new QueryWrapper<>();
                    wrapper.eq("bmsm", trimmed).last("LIMIT 1");
                    com.lis.entity.SysBbzlDict item = sysBbzlDictMapper.selectOne(wrapper);
                    if (item != null) return item.getBm();
                } catch (Exception ignored) {
                }
                try {
                    QueryWrapper<com.lis.entity.SysBbzlDict> wrapper = new QueryWrapper<>();
                    wrapper.like("bmsm", trimmed).last("LIMIT 1");
                    com.lis.entity.SysBbzlDict item = sysBbzlDictMapper.selectOne(wrapper);
                    if (item != null) return item.getBm();
                } catch (Exception ignored) {
                }
                try {
                    QueryWrapper<com.lis.entity.SysBbzlDict> wrapper = new QueryWrapper<>();
                    wrapper.like("pym", trimmed).last("LIMIT 1");
                    com.lis.entity.SysBbzlDict item = sysBbzlDictMapper.selectOne(wrapper);
                    if (item != null) return item.getBm();
                } catch (Exception ignored) {
                }
                return null;
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public Map<String, Object> nextSampleNo(String date) {
        Map<String, Object> resp = new HashMap<>();
        String targetDate = (date == null || date.isEmpty())
                ? LocalDate.now().toString()
                : date;

        String prefix = LocalDate.parse(targetDate).format(DateTimeFormatter.BASIC_ISO_DATE);

        Integer maxSeq = brxxMapper.selectMaxSampleSeq(prefix + "%", targetDate);
        int nextSeq = (maxSeq == null ? 1 : (maxSeq + 1));
        String sampleNo = prefix + String.format("%04d", nextSeq);

        resp.put("success", true);
        resp.put("date", targetDate);
        resp.put("sampleNo", sampleNo);
        return resp;
    }

    public List<Map<String, Object>> listPatients(String date, String patientType, String sampleNo, String name, String barcode) {
        String targetDate = (date == null || date.isEmpty())
                ? LocalDate.now().toString()
                : date;

        String filterSampleNo = (sampleNo != null && !sampleNo.trim().isEmpty()) ? sampleNo.trim() : null;
        String filterName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
        String filterBarcode = (barcode != null && !barcode.trim().isEmpty()) ? barcode.trim() : null;

        List<Integer> uniqueIds = brxxMapper.selectPatientIdsByFilter(targetDate, filterSampleNo, filterName, filterBarcode);

        if (patientType != null && !patientType.trim().isEmpty() && !"所有".equals(patientType.trim())) {
            uniqueIds = filterByPatientType(uniqueIds, patientType.trim());
        }

        if (uniqueIds.isEmpty()) {
            return new ArrayList<>();
        }

        return brxxMapper.selectPatientListByIds(uniqueIds);
    }

    private List<Integer> filterByPatientType(List<Integer> ids, String patientType) {
        if (ids.isEmpty()) return ids;
        List<Integer> filtered = new ArrayList<>();
        for (Integer id : ids) {
            BgxtBrxx brxx = brxxMapper.selectById(id);
            if (brxx == null) continue;
            boolean match = false;
            switch (patientType) {
                case "未审核":
                    match = brxx.getYbzt() == null || brxx.getYbzt() == 0 || brxx.getYbzt() == 1;
                    break;
                case "已出结果":
                    match = brxx.getYbzt() != null && brxx.getYbzt() >= 1;
                    break;
                case "已打印":
                case "已出报告":
                    match = brxx.getYbzt() != null && brxx.getYbzt() == 3;
                    break;
                case "已检验":
                    match = brxx.getYbzt() != null && brxx.getYbzt() == 1;
                    break;
                case "门诊病人":
                    match = brxx.getBrlb() != null && brxx.getBrlb() == 1;
                    break;
                case "住院病人":
                    match = brxx.getBrlb() != null && brxx.getBrlb() == 2;
                    break;
                case "体检人员":
                    match = brxx.getBrlb() != null && brxx.getBrlb() == 3;
                    break;
                case "其他病人":
                    match = brxx.getBrlb() != null && brxx.getBrlb() == 4;
                    break;
                case "科研人员":
                    match = brxx.getBrlb() != null && brxx.getBrlb() == 5;
                    break;
                default:
                    match = true;
                    break;
            }
            if (match) filtered.add(id);
        }
        return filtered;
    }

    @Transactional
    public Map<String, Object> inspectSample(Integer brxxId, String czydm) {
        Map<String, Object> resp = new HashMap<>();
        if (brxxId == null) {
            resp.put("success", false);
            resp.put("message", "缺少样本ID");
            return resp;
        }

        Integer total = jyjgMapper.countByBrxxId(brxxId);
        if (total == null || total == 0) {
            resp.put("success", false);
            resp.put("message", "请先选择组合项目并录入检验结果后再检验");
            return resp;
        }
        Integer emptyCount = jyjgMapper.countEmptyByBrxxId(brxxId);
        if (emptyCount != null && emptyCount > 0) {
            resp.put("success", false);
            resp.put("message", "存在空结果项目（" + emptyCount + "项），请补齐后再检验");
            return resp;
        }

        String jyys = czydm;
        if (jyys == null || jyys.trim().isEmpty()) {
            jyys = "admin";
        }
        brxxMapper.inspectById(brxxId, jyys);
        resp.put("success", true);
        resp.put("message", "样本已标记为\u201C接收/已检验\u201D");
        return resp;
    }

    @Transactional
    public Map<String, Object> auditSample(Integer brxxId, String czydm) {
        Map<String, Object> resp = new HashMap<>();
        if (brxxId == null) {
            resp.put("success", false);
            resp.put("message", "缺少样本ID");
            return resp;
        }

        List<Map<String, Object>> warnings = auditVerificationService.verify(brxxId, czydm);
        boolean hasError = auditVerificationService.hasErrors(warnings);
        if (hasError) {
            String errorMsg = warnings.stream()
                .filter(w -> "error".equals(w.get("level")))
                .map(w -> String.valueOf(w.get("message")))
                .findFirst().orElse("审核验证失败");
            resp.put("success", false);
            resp.put("message", errorMsg);
            resp.put("warnings", warnings);
            return resp;
        }

        int rowsAffected = brxxMapper.auditById(brxxId, czydm);
        if (rowsAffected == 0) {
            resp.put("success", false);
            resp.put("message", "样本状态已改变，审核失败");
            return resp;
        }

        try {
            // audit log handled by @OperationLog on controller
        } catch (Exception e) {
            log.warn("记录操作日志失败: {}", e.getMessage());
        }

        try {
            String auditContent = "AUDIT:" + brxxId + ":" + czydm + ":" + System.currentTimeMillis();
            String caSignature = caSignatureService.sign(czydm, auditContent);
            resp.put("caSignature", caSignature);
            log.info("CA电子签名已记录: brxxId={}, czydm={}", brxxId, czydm);
        } catch (Exception e) {
            log.warn("CA签名记录失败(不影响审核): brxxId={}, error={}", brxxId, e.getMessage());
        }

        resp.put("success", true);
        resp.put("message", "样本已审核");
        resp.put("warnings", warnings);
        return resp;
    }

    @Transactional
    public Map<String, Object> printSample(Integer brxxId) {
        Map<String, Object> resp = new HashMap<>();
        if (brxxId == null) {
            resp.put("success", false);
            resp.put("message", "缺少样本ID");
            return resp;
        }

        Integer currentStatus = brxxMapper.selectStatusById(brxxId);
        if (currentStatus == null) {
            resp.put("success", false);
            resp.put("message", "样本不存在");
            return resp;
        }
        if (currentStatus < 2) {
            resp.put("success", false);
            resp.put("message", "样本尚未审核，无法打印");
            return resp;
        }

        Integer resultCount = jyjgMapper.countByBrxxId(brxxId);
        if (resultCount == null || resultCount == 0) {
            resp.put("success", false);
            resp.put("message", "当前样本尚未产生任何检验结果，不能打印");
            return resp;
        }

        Integer currentPrintCount = brxxMapper.selectPrintCountById(brxxId);
        int newPrintCount = (currentPrintCount == null ? 0 : currentPrintCount) + 1;

        brxxMapper.updatePrintById(brxxId, newPrintCount);

        try {
            jgdybMapper.insertPrintLog(brxxId, null, null, null);
        } catch (Exception e) {
            log.warn("打印日志记录失败: brxxId={}", brxxId, e);
        }

        try {
            hisNotificationService.notifySampleStatus(brxxId, 3);
        } catch (Exception e) {
            log.warn("HIS状态通知失败: brxxId={}", brxxId, e);
        }

        resp.put("success", true);
        resp.put("message", "样本已打印（第" + newPrintCount + "次）");
        resp.put("printCount", newPrintCount);
        return resp;
    }

    public List<Map<String, Object>> listResults(Integer brxxId) {
        if (brxxId == null) {
            return new ArrayList<>();
        }

        Map<String, Object> brxx = null;
        try {
            brxx = brxxMapper.selectPatientContextById(brxxId);
        } catch (Exception ignored) {
        }

        List<Map<String, Object>> list = jyjgMapper.selectResultsByBrxxId(brxxId);

        Integer sbDjid = null;
        if (brxx != null) {
            Object sbDjidObj = brxx.get("sb_djid");
            if (sbDjidObj instanceof Integer) {
                sbDjid = (Integer) sbDjidObj;
            } else if (sbDjidObj != null) {
                try { sbDjid = Integer.parseInt(sbDjidObj.toString()); } catch (Exception ignored) {}
            }

            for (Map<String, Object> item : list) {
                Object refRangeObj = item.get("refRange");
                String refRange = refRangeObj == null ? "" : String.valueOf(refRangeObj).trim();

                if (refRange.isEmpty()) {
                    Object xmidObj = item.get("xmid");
                    if (xmidObj != null) {
                        try {
                            Integer xmid = Integer.parseInt(xmidObj.toString());
                            Map<String, Object> ckzRow = matchReferenceRange(brxxId, xmid, sbDjid);
                            if (ckzRow != null) {
                                Object ckz = ckzRow.get("ckz");
                                item.put("refRange", ckz == null ? "" : String.valueOf(ckz));
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }

        return list;
    }

    public List<Map<String, Object>> searchSamples(String sampleNo, String name, String barcode, String patientId, String date) {
        return brxxMapper.searchSamples(sampleNo, name, barcode, patientId, date);
    }

    public Map<String, Object> getReportData(Integer brxxId) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> brxx = brxxMapper.selectReportInfoById(brxxId);
        if (brxx != null) {
            data.putAll(brxx);
            Integer sbDjid = brxx.get("sb_djid") != null ? Integer.parseInt(brxx.get("sb_djid").toString()) : null;
            if (sbDjid != null) {
                com.lis.entity.Instrument instrument = instrumentMapper.selectById(sbDjid);
                if (instrument != null) {
                    if (data.get("bgbt") == null || String.valueOf(data.get("bgbt")).isEmpty()) {
                        data.put("bgbt", instrument.getBgbt());
                    }
                    if (data.get("bgyj") == null || String.valueOf(data.get("bgyj")).isEmpty()) {
                        data.put("bgyj", instrument.getBgyj());
                    }
                    if (instrument.getBblb() != null && !instrument.getBblb().isEmpty()) {
                        data.put("bblb", instrument.getBblb());
                    }
                }
            }
            try {
                List<Map<String, Object>> hisItems = bgxtHisXmMapper.selectByBrxxId(brxxId);
                if (hisItems != null && !hisItems.isEmpty()) {
                    for (Map<String, Object> item : hisItems) {
                        Object zhidObj = item.get("zhid");
                        if (zhidObj != null) {
                            Integer zhid = ((Number) zhidObj).intValue();
                            com.lis.entity.BgxtXmzhZb combo = bgxtXmzhZbMapper.selectById(zhid);
                            if (combo != null && combo.getReportType() != null && combo.getReportType() != 0) {
                                data.put("reportType", combo.getReportType());
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("获取组合项目ReportType失败: {}", e.getMessage());
            }
        }
        data.put("results", jyjgMapper.selectReportResultsByBrxxId(brxxId));
        return data;
    }

    @Transactional
    public Map<String, Object> batchAudit(List<Integer> brxxIds, String czydm) {
        Map<String, Object> resp = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();

        for (Integer brxxId : brxxIds) {
            try {
                List<Map<String, Object>> warnings = auditVerificationService.verify(brxxId, czydm);
                if (auditVerificationService.hasErrors(warnings)) {
                    failCount++;
                    String errorMsg = warnings.stream()
                        .filter(w -> "error".equals(w.get("level")))
                        .map(w -> String.valueOf(w.get("message")))
                        .findFirst().orElse("验证失败");
                    errors.add("样本ID " + brxxId + ": " + errorMsg);
                    continue;
                }

                brxxMapper.auditById(brxxId, czydm);

                try {
                    String auditContent = "AUDIT:" + brxxId + ":" + czydm + ":" + System.currentTimeMillis();
                    caSignatureService.sign(czydm, auditContent);
                } catch (Exception ex) {
                    log.warn("批量审核CA签名失败: brxxId={}", brxxId, ex);
                }

                successCount++;
            } catch (Exception e) {
                failCount++;
                errors.add("样本ID " + brxxId + " 审核失败: " + e.getMessage());
            }
        }

        resp.put("success", failCount == 0);
        resp.put("message", "批量审核完成：成功 " + successCount + " 条，失败 " + failCount + " 条");
        resp.put("successCount", successCount);
        resp.put("failCount", failCount);
        resp.put("errors", errors);
        return resp;
    }

    @Transactional
    public Map<String, Object> batchPrint(List<Integer> brxxIds, String czydm, Boolean skipPrinted, Integer templateId) {
        Map<String, Object> resp = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<Map<String, Object>> results = new ArrayList<>();

        for (Integer brxxId : brxxIds) {
            try {
                Integer currentStatus = brxxMapper.selectStatusById(brxxId);
                if (currentStatus == null) {
                    failCount++;
                    Map<String, Object> error = new HashMap<>();
                    error.put("brxxId", brxxId);
                    error.put("success", false);
                    error.put("message", "样本不存在");
                    results.add(error);
                    continue;
                }

                if (currentStatus < 2) {
                    failCount++;
                    Map<String, Object> error = new HashMap<>();
                    error.put("brxxId", brxxId);
                    error.put("success", false);
                    error.put("message", "样本尚未审核，无法打印");
                    results.add(error);
                    continue;
                }

                if (currentStatus == 3 && Boolean.TRUE.equals(skipPrinted)) {
                    Map<String, Object> skipped = new HashMap<>();
                    skipped.put("brxxId", brxxId);
                    skipped.put("success", true);
                    skipped.put("message", "已打印样本已跳过");
                    skipped.put("skipped", true);
                    results.add(skipped);
                    continue;
                }

                Integer resultCount = jyjgMapper.countByBrxxId(brxxId);
                if (resultCount == null || resultCount == 0) {
                    failCount++;
                    Map<String, Object> error = new HashMap<>();
                    error.put("brxxId", brxxId);
                    error.put("success", false);
                    error.put("message", "样本没有检验结果");
                    results.add(error);
                    continue;
                }

                Integer currentPrintCount = brxxMapper.selectPrintCountById(brxxId);
                int newPrintCount = (currentPrintCount == null ? 0 : currentPrintCount) + 1;
                brxxMapper.updatePrintById(brxxId, newPrintCount);

                try {
                    jgdybMapper.insertPrintLog(brxxId, czydm, null, null);
                } catch (Exception e) {
                    log.warn("批量打印日志记录失败: brxxId={}", brxxId, e);
                }

                try {
                    hisNotificationService.notifySampleStatus(brxxId, 3);
                } catch (Exception e) {
                    log.warn("批量打印HIS通知失败: brxxId={}", brxxId, e);
                }

                successCount++;
                Map<String, Object> result = new HashMap<>();
                result.put("brxxId", brxxId);
                result.put("success", true);
                result.put("printCount", newPrintCount);
                result.put("message", currentStatus == 3 ? "重新打印成功（第" + newPrintCount + "次）" : "打印成功（第" + newPrintCount + "次）");

                try {
                    Map<String, Object> reportData = getReportData(brxxId);
                    String reportHtml = templateRenderService.renderReport(templateId, reportData);
                    result.put("reportHtml", reportHtml);
                } catch (Exception e) {
                    log.warn("生成报告HTML失败: brxxId={}", brxxId, e);
                    result.put("reportHtml", null);
                }

                results.add(result);
            } catch (Exception e) {
                failCount++;
                Map<String, Object> error = new HashMap<>();
                error.put("brxxId", brxxId);
                error.put("success", false);
                error.put("message", e.getMessage());
                results.add(error);
            }
        }

        resp.put("success", failCount == 0);
        resp.put("message", "批量打印完成：成功 " + successCount + " 条，失败 " + failCount + " 条");
        resp.put("successCount", successCount);
        resp.put("failCount", failCount);
        resp.put("results", results);
        return resp;
    }

    @Transactional
    public Map<String, Object> batchInvalidate(List<Integer> brxxIds, String reason) {
        Map<String, Object> resp = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();

        for (Integer brxxId : brxxIds) {
            try {
                Integer currentStatus = brxxMapper.selectStatusById(brxxId);
                if (currentStatus != null && currentStatus == 3) {
                    failCount++;
                    errors.add("样本ID " + brxxId + " 已打印，无法作废");
                    continue;
                }

                brxxMapper.invalidateById(brxxId, reason != null ? reason : "");
                successCount++;
            } catch (Exception e) {
                failCount++;
                errors.add("样本ID " + brxxId + " 作废失败: " + e.getMessage());
            }
        }

        resp.put("success", failCount == 0);
        resp.put("message", "批量作废完成：成功 " + successCount + " 条，失败 " + failCount + " 条");
        resp.put("successCount", successCount);
        resp.put("failCount", failCount);
        resp.put("errors", errors);
        return resp;
    }

    @Transactional
    public Map<String, Object> batchUnaudit(List<Integer> brxxIds, String czydm) {
        Map<String, Object> resp = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();

        for (Integer brxxId : brxxIds) {
            try {
                Integer currentStatus = brxxMapper.selectStatusById(brxxId);
                if (currentStatus == null) {
                    failCount++;
                    errors.add("样本ID " + brxxId + " 不存在");
                    continue;
                }
                if (currentStatus == 3) {
                    failCount++;
                    errors.add("样本ID " + brxxId + " 已打印，无法取消审核");
                    continue;
                }
                if (currentStatus != 2) {
                    failCount++;
                    errors.add("样本ID " + brxxId + " 未审核，无法取消");
                    continue;
                }

                brxxMapper.unauditById(brxxId);
                // audit log handled by @OperationLog on controller
                successCount++;
            } catch (Exception e) {
                failCount++;
                errors.add("样本ID " + brxxId + " 取消审核失败: " + e.getMessage());
            }
        }

        resp.put("success", failCount == 0);
        resp.put("message", "批量取消审核完成：成功 " + successCount + " 条，失败 " + failCount + " 条");
        resp.put("successCount", successCount);
        resp.put("failCount", failCount);
        resp.put("errors", errors);
        return resp;
    }

    @Transactional
    public Map<String, Object> cancelSample(Integer brxxId, String czydm, String reason) {
        Map<String, Object> result = new HashMap<>();
        try {
            brxxMapper.insertBrxxLog(brxxId, czydm);
            jyjgMapper.insertJyjgLog(brxxId);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "日志保存失败：" + e.getMessage());
        }
        return result;
    }

    public List<Map<String, Object>> refreshSamples(Integer sbDjid, String date, Integer brlb) {
        return brxxMapper.selectRefreshList(sbDjid, date, brlb);
    }

    @Transactional
     public Map<String, Object> extractFromInstrument(Integer sbDjid, String beginDate, String czydm, Integer bz) {
         return extractFromInstrument(sbDjid, beginDate, czydm, bz, null);
     }

     public Map<String, Object> extractFromInstrument(Integer sbDjid, String beginDate, String czydm, Integer bz, String patientName) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (sbDjid == null) {
                result.put("success", false);
                result.put("message", "仪器ID不能为空");
                return result;
            }

            List<Map<String, Object>> rawList = sysCjyszMxMapper.selectExtractData(sbDjid, beginDate, bz, patientName);
            int updated = 0;
            int skipped = 0;
            for (Map<String, Object> raw : rawList) {
                Integer xmid = toInt(raw.get("xmid"));
                Integer syh = toInt(raw.get("syh"));
                String rawResult = String.valueOf(raw.get("jyjg"));
                java.math.BigDecimal xs = raw.get("xs") != null ? new java.math.BigDecimal(String.valueOf(raw.get("xs"))) : java.math.BigDecimal.ONE;
                Integer xmjd = raw.get("xmjd") != null ? toInt(raw.get("xmjd")) : 3;
                String converted = convertResult(rawResult, xs, xmjd);
                Integer brxxId = null;
                if (patientName != null && !patientName.isEmpty()) {
                    brxxId = brxxMapper.selectIdByBrxm(patientName);
                }
                if (brxxId == null) {
                    brxxId = brxxMapper.selectIdBySyhAndSbdjid(syh, sbDjid);
                }
                if (brxxId == null) {
                    log.warn("提取跳过：未匹配到样本 syh={}, sbDjid={}, patientName={}", syh, sbDjid, patientName);
                    skipped++;
                    continue;
                }

                Map<String, Object> ckzRow = matchReferenceRange(brxxId, xmid, sbDjid);
                String ckz = ckzRow == null ? "" : String.valueOf(ckzRow.getOrDefault("ckz", ""));
                Object ckzgx = ckzRow == null ? null : ckzRow.get("ckzgx");
                Object ckzdx = ckzRow == null ? null : ckzRow.get("ckzdx");
                Object bjzgx = ckzRow == null ? null : ckzRow.get("bjzgx");
                Object bjzdx = ckzRow == null ? null : ckzRow.get("bjzdx");
                String gdbj = computeHighLowFlag(converted, ckzgx, ckzdx);

                Integer existing = jyjgMapper.countByBrxxIdAndXmid(brxxId, xmid);
                if (existing != null && existing > 0) {
                    jyjgMapper.updateExtractResult(brxxId, xmid, converted, ckz, ckzgx, ckzdx, bjzgx, bjzdx, gdbj, raw.get("cjid"));
                } else {
                    jyjgMapper.insertExtractResult(brxxId, xmid, converted, ckz, ckzgx, ckzdx, bjzgx, bjzdx, gdbj, raw.get("cjid"));
                }
                updated++;
            }
            if (updated > 0 && patientName != null && !patientName.isEmpty()) {
                Integer brxxIdForStatus = brxxMapper.selectIdByBrxm(patientName);
                if (brxxIdForStatus != null) {
                    BgxtBrxx current = brxxMapper.selectById(brxxIdForStatus);
                    if (current != null && (current.getYbzt() == null || current.getYbzt() == 0)) {
                        current.setYbzt(1);
                        brxxMapper.updateById(current);
                    }
                }
            }
            result.put("success", true);
            result.put("message", "提取完成，更新 " + updated + " 条结果");
            result.put("count", updated);
            if (skipped > 0) {
                result.put("message", "提取完成，更新 " + updated + " 条结果，跳过 " + skipped + " 条（未匹配到样本）");
            }
            if (updated == 0 && skipped > 0) {
                result.put("success", false);
                result.put("message", "未找到匹配的样本记录，请先在样本录入中录入患者信息");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "提取失败：" + e.getMessage());
        }
        return result;
    }

    private String convertResult(String raw, java.math.BigDecimal xs, int xmjd) {
        try {
            java.math.BigDecimal val = new java.math.BigDecimal(raw);
            val = val.multiply(xs);
            int scale = Math.max(0, xmjd - 1);
            return val.setScale(scale, java.math.BigDecimal.ROUND_HALF_UP).toPlainString();
        } catch (Exception e) {
            return raw;
        }
    }

    private Integer toInt(Object val) {
        if (val == null) return null;
        try { return Integer.parseInt(String.valueOf(val)); } catch (Exception e) { return null; }
    }

    @Transactional
    public Map<String, Object> handleSpecimen(Integer brxxId, Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer yczt = data.get("errorType") != null ? Integer.parseInt(data.get("errorType").toString()) : null;
            String remark = (String) data.get("remark");
            if (yczt == null || yczt <= 0) {
                result.put("success", false);
                result.put("message", "错误类型不能为空");
                return result;
            }
            brxxMapper.updateYcztById(brxxId, yczt);

            BgxtSampleReject reject = new BgxtSampleReject();
            reject.setBrxxId(brxxId);
            reject.setErrorReason((String) data.get("errorReason"));
            reject.setHandlingMeasures((String) data.get("handlingMeasures"));
            reject.setHandlingMeasuresOther((String) data.get("handlingMeasuresOther"));
            reject.setNotes(remark);
            reject.setRecipient((String) data.get("recipient"));
            reject.setOperatorCode((String) data.get("operatorCode"));
            reject.setOperatorName((String) data.get("operatorName"));
            reject.setTestBarcode((String) data.get("testBarcode"));
            reject.setPatientName((String) data.get("patientName"));
            reject.setSex((String) data.get("sex"));
            reject.setAge((String) data.get("age"));
            reject.setPatientType(data.get("patientType") != null ? Integer.parseInt(data.get("patientType").toString()) : null);
            reject.setDepartment((String) data.get("department"));
            reject.setBedNumber((String) data.get("bedNumber"));
            reject.setSampleType((String) data.get("sampleType"));
            reject.setItemName((String) data.get("itemName"));
            reject.setClassGroup((String) data.get("classGroup"));
            reject.setGroupName((String) data.get("groupName"));
            sampleRejectMapper.insert(reject);

            result.put("success", true);
            result.put("message", "标本错误处理成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "处理失败：" + e.getMessage());
        }
        return result;
    }

    @Transactional
    public Map<String, Object> reportIncorrect(Integer brxxId, String reason) {
        Map<String, Object> result = new HashMap<>();
        try {
            brxxMapper.updateReportIncorrect(brxxId, reason != null ? reason : "");
            result.put("success", true);
            result.put("message", "报告不正确标记成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "标记失败：" + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> getProgressStats(String date) {
        Map<String, Object> resp = new HashMap<>();
        String targetDate = (date == null || date.isEmpty())
                ? LocalDate.now().toString()
                : date;

        Map<String, Object> stats = brxxMapper.selectProgressStats(targetDate);

        resp.put("success", true);
        resp.put("date", targetDate);
        resp.put("stats", stats);
        return resp;
    }

    public Map<String, Object> getDailyWorkload(String beginDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        String today = LocalDate.now().toString();
        String start = (beginDate != null && !beginDate.isEmpty()) ? beginDate : today;
        String end = (endDate != null && !endDate.isEmpty()) ? endDate : today;

        result.put("byStatus", brxxMapper.selectStatsByStatusWithDate(start, end));
        result.put("byDepartment", brxxMapper.selectStatsByDepartment(start, end));
        result.put("byPatientType", brxxMapper.selectStatsByPatientTypeWithDate(start, end));
        result.put("total", brxxMapper.selectTotalStats(start, end));
        result.put("success", true);
        result.put("beginDate", start);
        result.put("endDate", end);
        return result;
    }

    public List<Map<String, Object>> getSampleIssues(String date) {
        String targetDate = (date == null || date.isEmpty())
                ? LocalDate.now().toString()
                : date;
        return brxxMapper.selectSampleIssues(targetDate);
    }

    @Transactional
    public Map<String, Object> handleSampleIssue(Map<String, Object> payload) {
        Map<String, Object> resp = new HashMap<>();
        Integer brxxId = null;
        Object brxxIdObj = payload.get("brxxId");
        if (brxxIdObj != null) {
            brxxId = Integer.parseInt(brxxIdObj.toString());
        }

        String action = (String) payload.get("action");
        String remarks = (String) payload.getOrDefault("remarks", "");

        if (brxxId == null) {
            resp.put("success", false);
            resp.put("message", "缺少样本ID");
            return resp;
        }

        if ("cancelInvalid".equals(action)) {
            brxxMapper.cancelInvalidById(brxxId, remarks);
            resp.put("message", "已取消作废");
        } else if ("updateRemarks".equals(action)) {
            brxxMapper.updateRemarksById(brxxId, remarks);
            resp.put("message", "备注已更新");
        } else if ("delete".equals(action)) {
            Integer currentStatus = brxxMapper.selectStatusById(brxxId);
            if (currentStatus != null && currentStatus == 3) {
                resp.put("success", false);
                resp.put("message", "已打印的样本无法删除");
                return resp;
            }
            jyjgMapper.deleteByBrxxId(brxxId);
            brxxMapper.deleteById(brxxId);
            resp.put("message", "样本已删除");
        } else {
            resp.put("success", false);
            resp.put("message", "无效的操作类型");
            return resp;
        }

        resp.put("success", true);
        return resp;
    }

    public List<Map<String, Object>> listCombos(Integer sbDjid, String keyword) {
        Map<String, Object> params = new HashMap<>();
        params.put("sbDjid", sbDjid);
        params.put("keyword", keyword);
        return bgxtXmzhZbMapper.listCombos(params);
    }

    public List<Map<String, Object>> listComboItems(Integer zhid, Integer sbDjid) {
        if (zhid == null) {
            return new ArrayList<>();
        }

        QueryWrapper<com.lis.entity.BgxtXmzhMx> wrapper = new QueryWrapper<>();
        wrapper.eq("zhid", zhid).orderByAsc("xh", "id");
        List<com.lis.entity.BgxtXmzhMx> mxList = bgxtXmzhMxMapper.selectList(wrapper);

        Map<Integer, com.lis.entity.BgxtXmzhMx> dedupMap = new java.util.LinkedHashMap<>();
        List<com.lis.entity.BgxtXmzhMx> noXmidList = new ArrayList<>();

        for (com.lis.entity.BgxtXmzhMx mx : mxList) {
            Integer xmid = mx.getXmid();
            if (xmid != null && xmid > 0) {
                if (!dedupMap.containsKey(xmid)) {
                    dedupMap.put(xmid, mx);
                }
            } else if (mx.getXmdm() != null && !mx.getXmdm().trim().isEmpty()) {
                String xmdm = mx.getXmdm().trim();
                Integer resolvedXmid = findXmidByCode(xmdm);
                if (resolvedXmid != null && !dedupMap.containsKey(resolvedXmid)) {
                    mx.setXmid(resolvedXmid);
                    dedupMap.put(resolvedXmid, mx);
                }
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<Integer, com.lis.entity.BgxtXmzhMx> entry : dedupMap.entrySet()) {
            com.lis.entity.BgxtXmzhMx mx = entry.getValue();
            Integer xmid = entry.getKey();
            Map<String, Object> row = buildComboItemRowFromEntity(mx, xmid, sbDjid);
            result.add(row);
        }

        for (com.lis.entity.BgxtXmzhMx mx : noXmidList) {
            String xmzwmc = mx.getXmzwmc() != null ? mx.getXmzwmc().trim() : "";
            String xmdm = mx.getXmdm() != null ? mx.getXmdm().trim() : "";
            String xmdw = mx.getXmdw() != null ? mx.getXmdw().trim() : "";
            Map<String, Object> row = new HashMap<>();
            row.put("id", mx.getId());
            row.put("zhid", mx.getZhid());
            row.put("code", xmdm);
            row.put("name", xmzwmc.isEmpty() ? xmdm : xmzwmc);
            row.put("unit", xmdw);
            row.put("refRange", "");
            row.put("xmid", null);
            result.add(row);
        }

        return result;
    }

    private Map<String, Object> buildComboItemRowFromEntity(com.lis.entity.BgxtXmzhMx mx, Integer xmid, Integer sbDjid) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", mx.getId());
        row.put("zhid", mx.getZhid());
        row.put("xmid", xmid);

        String xmzwmc = mx.getXmzwmc() != null ? mx.getXmzwmc().trim() : "";
        String xmdm = mx.getXmdm() != null ? mx.getXmdm().trim() : "";
        String xmdw = mx.getXmdw() != null ? mx.getXmdw().trim() : "";

        if (xmzwmc.isEmpty() || xmdm.isEmpty()) {
            try {
                QueryWrapper<com.lis.entity.SysJyxm> w = new QueryWrapper<>();
                w.eq("xmid", xmid).last("LIMIT 1");
                com.lis.entity.SysJyxm jyxm = sysJyxmMapper.selectOne(w);
                if (jyxm != null) {
                    if (xmzwmc.isEmpty() && jyxm.getXmzwmc() != null && !jyxm.getXmzwmc().trim().isEmpty()) {
                        xmzwmc = jyxm.getXmzwmc().trim();
                    }
                    if (xmdm.isEmpty() && jyxm.getXmdm() != null && !jyxm.getXmdm().trim().isEmpty()) {
                        xmdm = jyxm.getXmdm().trim();
                    }
                    if (xmdw.isEmpty() && jyxm.getXmdw() != null && !jyxm.getXmdw().trim().isEmpty()) {
                        xmdw = jyxm.getXmdw().trim();
                    }
                }
            } catch (Exception ignored) {}
        }

        row.put("code", xmdm);
        row.put("name", xmzwmc.isEmpty() ? xmdm : xmzwmc);
        row.put("unit", xmdw);

        String refRange = "";
        try {
            QueryWrapper<com.lis.entity.SysXmckz> ckzWrapper = new QueryWrapper<>();
            ckzWrapper.eq("xmid", xmid);
            if (sbDjid != null) {
                ckzWrapper.eq("sb_djid", sbDjid);
            }
            ckzWrapper.last("LIMIT 1");
            com.lis.entity.SysXmckz ckzEntity = sysXmckzMapper.selectOne(ckzWrapper);
            if (ckzEntity == null && sbDjid != null) {
                QueryWrapper<com.lis.entity.SysXmckz> fallback = new QueryWrapper<>();
                fallback.eq("xmid", xmid).last("LIMIT 1");
                ckzEntity = sysXmckzMapper.selectOne(fallback);
            }
            if (ckzEntity != null) {
                String ckz = ckzEntity.getCkz() != null ? ckzEntity.getCkz().trim() : "";
                String ckzgx = ckzEntity.getCkzgx() != null ? ckzEntity.getCkzgx().toString().trim() : "";
                String ckzdx = ckzEntity.getCkzdx() != null ? ckzEntity.getCkzdx().toString().trim() : "";
                if (!ckz.isEmpty()) {
                    refRange = ckz;
                } else if (!ckzgx.isEmpty() || !ckzdx.isEmpty()) {
                    refRange = ckzdx + "-" + ckzgx;
                }
            }
        } catch (Exception ignored) {}
        row.put("refRange", refRange);

        return row;
    }

    public Integer getExtractPendingCount(Integer sbDjid, String extractDate, String patientName) {
        try {
            return sysCjyszZbMapper.countPending(sbDjid, extractDate);
        } catch (Exception e) {
            return 0;
        }
    }

    public List<Map<String, Object>> getExtractPreviewData(Integer sbDjid, String beginDate, String patientName) {
        try {
            return sysCjyszMxMapper.selectExtractData(sbDjid, null, 1, patientName);
        } catch (Exception e) {
            log.error("获取提取预览数据失败", e);
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> getHistoryRecords(String date) {
        List<Map<String, Object>> records = new ArrayList<>();
        try {
            QueryWrapper<BgxtBrxx> wrapper = new QueryWrapper<>();
            wrapper.ge("jyrq", date + " 00:00:00")
                   .le("jyrq", date + " 23:59:59")
                   .orderByDesc("jyrq")
                   .last("LIMIT 100");

            List<BgxtBrxx> list = brxxMapper.selectList(wrapper);
            for (BgxtBrxx brxx : list) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", brxx.getBrxxId());
                record.put("sampleNo", brxx.getSyh());
                record.put("name", brxx.getBrxm());
                record.put("oldStatus", brxx.getYbzt());
                record.put("newStatus", brxx.getYbzt());
                record.put("operator", brxx.getShys() != null ? brxx.getShys() : brxx.getJyys());
                record.put("operateTime", brxx.getShrq() != null ? brxx.getShrq() : brxx.getCzrq());
                record.put("remarks", brxx.getBz() != null ? brxx.getBz() : "");
                records.add(record);
            }
        } catch (Exception e) {
            log.error("获取历史记录失败", e);
        }
        return records;
    }

    public List<Map<String, Object>> getApplicationInfo(String date) {
        List<Map<String, Object>> records = new ArrayList<>();
        try {
            QueryWrapper<BgxtBrxx> wrapper = new QueryWrapper<>();
            wrapper.ge("jyrq", date + " 00:00:00")
                   .le("jyrq", date + " 23:59:59")
                   .orderByDesc("jyrq")
                   .last("LIMIT 100");

            List<BgxtBrxx> list = brxxMapper.selectList(wrapper);
            for (BgxtBrxx brxx : list) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", brxx.getBrxxId());
                record.put("applyNo", brxx.getBrbh() != null ? brxx.getBrbh() : "");
                record.put("patientName", brxx.getBrxm());
                record.put("dept", brxx.getKsdm() != null ? brxx.getKsdm() : "");
                record.put("doctor", brxx.getJyys() != null ? brxx.getJyys() : "");
                record.put("applyDate", brxx.getJyrq());
                record.put("status", getStatusText(brxx.getYbzt()));
                records.add(record);
            }
        } catch (Exception e) {
            log.error("获取申请信息失败", e);
        }
        return records;
    }

    public List<Map<String, Object>> getWarningInfo(String date) {
        List<Map<String, Object>> records = new ArrayList<>();
        try {
            QueryWrapper<BgxtBrxx> wrapper = new QueryWrapper<>();
            wrapper.ge("jyrq", date + " 00:00:00")
                   .le("jyrq", date + " 23:59:59")
                   .isNotNull("yczt")
                   .ne("yczt", 0)
                   .orderByDesc("jyrq")
                   .last("LIMIT 100");

            List<BgxtBrxx> list = brxxMapper.selectList(wrapper);
            for (BgxtBrxx brxx : list) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", brxx.getBrxxId());
                record.put("sampleNo", brxx.getSyh());
                record.put("patientName", brxx.getBrxm());

                String warningType = getWarningType(brxx.getYczt());
                record.put("type", warningType);
                record.put("message", getWarningMessage(brxx));
                record.put("createTime", brxx.getJyrq());
                records.add(record);
            }
        } catch (Exception e) {
            log.error("获取警示信息失败", e);
        }
        return records;
    }

    private String getStatusText(Integer ybzt) {
        return SampleStatus.getDesc(ybzt);
    }

    private String getWarningType(Integer yczt) {
        if (yczt == null || yczt == 0) return "正常";
        if (yczt == 1) return "异常值";
        if (yczt == 2) return "危急值";
        return "其他异常";
    }

    private String getWarningMessage(BgxtBrxx brxx) {
        if (brxx.getYczt() == null || brxx.getYczt() == 0) {
            return "";
        }
        StringBuilder msg = new StringBuilder();
        msg.append("样本[").append(brxx.getSyh()).append("]");
        if (brxx.getBz() != null && !brxx.getBz().isEmpty()) {
            msg.append(": ").append(brxx.getBz());
        }
        return msg.toString();
    }

    public List<Map<String, Object>> getErrorTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        String[][] items = {{"1","溶血"},{"2","脂血"},{"3","黄疸"},{"4","标本量不足"},{"5","标本凝集"},{"6","标本污染"},{"7","信息不符"},{"8","其他"}};
        for (String[] item : items) {
            Map<String, Object> m = new HashMap<>();
            m.put("code", Integer.parseInt(item[0]));
            m.put("name", item[1]);
            types.add(m);
        }
        return types;
    }

    public List<Map<String, Object>> getHandlingMeasures() {
        List<Map<String, Object>> measures = new ArrayList<>();
        String[][] items = {{"recollect","重采"},{"reject","拒收"},{"concession","让步检验"},{"other","其他"}};
        for (String[] item : items) {
            Map<String, Object> m = new HashMap<>();
            m.put("code", item[0]);
            m.put("name", item[1]);
            measures.add(m);
        }
        return measures;
    }

    public List<Map<String, Object>> queryRejectRecords(String beginDate, String endDate) {
        String start = (beginDate != null && !beginDate.isEmpty()) ? beginDate : LocalDate.now().toString();
        String end = (endDate != null && !endDate.isEmpty()) ? endDate : LocalDate.now().toString();
        return sampleRejectMapper.selectByDateRange(start, end);
    }

    public Map<String, Object> getRejectInfo(Integer brxxId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> reject = sampleRejectMapper.selectByBrxxId(brxxId);
        result.put("reject", reject);
        result.put("success", true);
        return result;
    }

    @Transactional
    public Map<String, Object> invalidateSample(Integer brxxId, String reason, String czydm) {
        Map<String, Object> resp = new HashMap<>();
        if (brxxId == null) {
            resp.put("success", false);
            resp.put("message", "缺少样本ID");
            return resp;
        }

        Integer currentStatus = brxxMapper.selectStatusById(brxxId);
        if (currentStatus != null && currentStatus == 3) {
            resp.put("success", false);
            resp.put("message", "样本已打印，无法作废");
            return resp;
        }

        int rows = brxxMapper.invalidateById(brxxId, reason != null ? reason : "");
        if (rows > 0) {
            resp.put("success", true);
            resp.put("message", "样本已作废");
        } else {
            resp.put("success", false);
            resp.put("message", "样本作废失败");
        }
        return resp;
    }

    @Transactional
    public Map<String, Object> updateSampleTime(Integer brxxId, Map<String, Object> times) {
        Map<String, Object> resp = new HashMap<>();
        if (brxxId == null) {
            resp.put("success", false);
            resp.put("message", "缺少样本ID");
            return resp;
        }

        String jyrq = times.get("jyrq") != null ? times.get("jyrq").toString() : null;
        String shrq = times.get("shrq") != null ? times.get("shrq").toString() : null;

        int rows = brxxMapper.updateSampleTime(brxxId, jyrq, shrq);
        if (rows >= 0) {
            resp.put("success", true);
            resp.put("message", "时间已更新");
        } else {
            resp.put("success", false);
            resp.put("message", "时间更新失败");
        }
        return resp;
    }

    @Transactional
    public Map<String, Object> acceptSample(String barcode, String czydm) {
        Map<String, Object> resp = new HashMap<>();
        if (barcode == null || barcode.trim().isEmpty()) {
            resp.put("success", false);
            resp.put("message", "条码号不能为空");
            return resp;
        }

        Map<String, Object> sample = brxxMapper.selectByBarcode(barcode);
        if (sample == null) {
            resp.put("success", false);
            resp.put("message", "未找到对应样本");
            return resp;
        }

        Integer brxxId = (Integer) sample.get("brxx_id");
        Integer currentStatus = (Integer) sample.get("ybzt");

        if (currentStatus != null && currentStatus >= 1) {
            resp.put("success", false);
            resp.put("message", "样本已核收，无需重复操作");
            return resp;
        }

        int rows = brxxMapper.acceptByBarcode(barcode);
        if (rows > 0) {
            resp.put("success", true);
            resp.put("message", "样本已核收");
            resp.put("brxxId", brxxId);
        } else {
            resp.put("success", false);
            resp.put("message", "核收失败，样本状态可能已改变");
        }
        return resp;
    }

    @Transactional
    public Map<String, Object> rejectSample(Integer brxxId, String reason, String czydm) {
        Map<String, Object> resp = new HashMap<>();
        if (brxxId == null) {
            resp.put("success", false);
            resp.put("message", "缺少样本ID");
            return resp;
        }

        Integer currentStatus = brxxMapper.selectStatusById(brxxId);
        if (currentStatus != null && currentStatus >= 2) {
            resp.put("success", false);
            resp.put("message", "已审核样本无法拒收");
            return resp;
        }

        BgxtSampleReject reject = new BgxtSampleReject();
        reject.setBrxxId(brxxId);
        reject.setErrorReason(reason);
        reject.setOperatorCode(czydm);
        reject.setNotes("拒收");
        sampleRejectMapper.insert(reject);

        brxxMapper.updateYcztById(brxxId, 8);

        resp.put("success", true);
        resp.put("message", "样本已拒收");
        return resp;
    }

    @Transactional
    public Map<String, Object> acceptWithSign(Integer brxxId, String czydm, String signature) {
        Map<String, Object> resp = new HashMap<>();
        if (brxxId == null) {
            resp.put("success", false);
            resp.put("message", "缺少样本ID");
            return resp;
        }

        Map<String, Object> fullInfo = brxxMapper.selectFullById(brxxId);
        if (fullInfo == null) {
            resp.put("success", false);
            resp.put("message", "未找到对应样本");
            return resp;
        }

        Object tmhObj = fullInfo.get("brxx_tmh");
        if (tmhObj == null) {
            resp.put("success", false);
            resp.put("message", "样本条码号为空");
            return resp;
        }

        String jyys = czydm;
        if (jyys == null || jyys.trim().isEmpty()) {
            jyys = "admin";
        }
        brxxMapper.inspectById(brxxId, jyys);

        try {
            String signContent = "ACCEPT:" + brxxId + ":" + czydm + ":" + System.currentTimeMillis();
            String caSignature = caSignatureService.sign(czydm, signContent);
            resp.put("caSignature", caSignature);
        } catch (Exception e) {
            log.warn("CA签名记录失败: brxxId={}, error={}", brxxId, e.getMessage());
        }

        resp.put("success", true);
        resp.put("message", "样本已核收并签名");
        return resp;
    }

    public Map<String, Object> getRawData(Integer brxxId) {
        Map<String, Object> resp = new HashMap<>();
        if (brxxId == null) {
            resp.put("success", false);
            resp.put("message", "缺少样本ID");
            return resp;
        }

        List<Map<String, Object>> rawData = brxxMapper.selectRawDataByBrxxId(brxxId);
        resp.put("success", true);
        resp.put("data", rawData);
        resp.put("count", rawData != null ? rawData.size() : 0);
        return resp;
    }

    @Transactional
    public Map<String, Object> transferSample(Integer brxxId, Map<String, Object> transferInfo) {
        Map<String, Object> resp = new HashMap<>();
        if (brxxId == null) {
            resp.put("success", false);
            resp.put("message", "缺少样本ID");
            return resp;
        }

        String targetDate = (String) transferInfo.get("targetDate");
        Integer targetInstrument = transferInfo.get("targetInstrument") != null
            ? Integer.parseInt(transferInfo.get("targetInstrument").toString()) : null;
        String targetSyh = (String) transferInfo.get("targetSyh");
        String startNo = (String) transferInfo.get("startNo");

        if (targetDate == null || targetInstrument == null || targetSyh == null) {
            resp.put("success", false);
            resp.put("message", "转移信息不完整");
            return resp;
        }

        Integer currentStatus = brxxMapper.selectStatusById(brxxId);
        if (currentStatus != null && currentStatus >= 2) {
            resp.put("success", false);
            resp.put("message", "已审核样本无法转移");
            return resp;
        }

        int rows = brxxMapper.updateTransferInfo(brxxId, targetDate, targetInstrument, targetSyh, startNo);
        if (rows >= 0) {
            resp.put("success", true);
            resp.put("message", "样本已转移");
        } else {
            resp.put("success", false);
            resp.put("message", "样本转移失败");
        }
        return resp;
    }

    @Transactional
    public Map<String, Object> quickExtract(Integer brxxId) {
        Map<String, Object> resp = new HashMap<>();
        if (brxxId == null) {
            resp.put("success", false);
            resp.put("message", "缺少样本ID");
            return resp;
        }

        BgxtBrxx brxx = brxxMapper.selectById(brxxId);
        if (brxx == null) {
            resp.put("success", false);
            resp.put("message", "样本不存在");
            return resp;
        }

        Integer sbDjid = brxx.getSbDjid();
        if (sbDjid == null) {
            resp.put("success", false);
            resp.put("message", "样本未关联仪器");
            return resp;
        }

        String sampleNo = brxx.getSyh();
        if (sampleNo == null || sampleNo.isEmpty()) {
            resp.put("success", false);
            resp.put("message", "样本号为空");
            return resp;
        }

        try {
            List<Map<String, Object>> rawList = sysCjyszMxMapper.selectExtractData(sbDjid, LocalDate.now().toString(), 1, null);
            int updated = 0;
            for (Map<String, Object> raw : rawList) {
                Integer xmid = toInt(raw.get("xmid"));
                Integer syh = toInt(raw.get("syh"));
                String rawResult = String.valueOf(raw.get("jyjg"));
                java.math.BigDecimal xs = raw.get("xs") != null ? new java.math.BigDecimal(String.valueOf(raw.get("xs"))) : java.math.BigDecimal.ONE;
                Integer xmjd = raw.get("xmjd") != null ? toInt(raw.get("xmjd")) : 3;
                String converted = convertResult(rawResult, xs, xmjd);

                Integer rawSyh = brxxMapper.selectIdBySyhAndSbdjid(syh, sbDjid);
                if (rawSyh != null && rawSyh.equals(brxxId)) {
                    Map<String, Object> ckzRow = matchReferenceRange(brxxId, xmid, sbDjid);
                    String ckz = ckzRow == null ? "" : String.valueOf(ckzRow.getOrDefault("ckz", ""));
                    Object ckzgx = ckzRow == null ? null : ckzRow.get("ckzgx");
                    Object ckzdx = ckzRow == null ? null : ckzRow.get("ckzdx");
                    Object bjzgx = ckzRow == null ? null : ckzRow.get("bjzgx");
                    Object bjzdx = ckzRow == null ? null : ckzRow.get("bjzdx");
                    String gdbj = computeHighLowFlag(converted, ckzgx, ckzdx);

                    Integer existing = jyjgMapper.countByBrxxIdAndXmid(brxxId, xmid);
                    if (existing != null && existing > 0) {
                        jyjgMapper.updateExtractResult(brxxId, xmid, converted, ckz, ckzgx, ckzdx, bjzgx, bjzdx, gdbj, raw.get("cjid"));
                    } else {
                        jyjgMapper.insertExtractResult(brxxId, xmid, converted, ckz, ckzgx, ckzdx, bjzgx, bjzdx, gdbj, raw.get("cjid"));
                    }
                    updated++;
                }
            }
            resp.put("success", true);
            resp.put("message", "快捷提取完成，更新 " + updated + " 条结果");
            resp.put("count", updated);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "快捷提取失败：" + e.getMessage());
        }
        return resp;
    }

    @Transactional
    public Map<String, Object> convertToQC(Integer brxxId) {
        Map<String, Object> resp = new HashMap<>();
        if (brxxId == null) {
            resp.put("success", false);
            resp.put("message", "缺少样本ID");
            return resp;
        }

        Integer resultCount = jyjgMapper.countByBrxxId(brxxId);
        if (resultCount == null || resultCount == 0) {
            resp.put("success", false);
            resp.put("message", "样本没有检验结果，无法转为质控");
            return resp;
        }

        try {
            brxxMapper.insertToQc(brxxId);
            resp.put("success", true);
            resp.put("message", "已转为质控数据");
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", "转质控失败：" + e.getMessage());
        }
        return resp;
    }
}
