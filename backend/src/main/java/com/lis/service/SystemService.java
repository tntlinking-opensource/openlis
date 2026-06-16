package com.lis.service;

import com.lis.dto.MaterialFeeSyncRequest;
import com.lis.dto.TubeCategoryRequest;
import com.lis.dto.TubeSubcategoryRequest;
import com.lis.entity.GzszMx;
import com.lis.entity.GzszZb;
import com.lis.mapper.*;
import com.lis.entity.SysRz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SystemService {

    @Autowired
    private SysZxtmcMapper zxtmcMapper;
    @Autowired
    private SysRzztsmMapper rzztsmMapper;
    @Autowired
    private SysRyszMapper ryszMapper;
    @Autowired
    private GzszZbMapper gzszZbMapper;
    @Autowired
    private GzszMxMapper gzszMxMapper;
    @Autowired
    private BgxtTsxmtatMapper bgxtTsxmtatMapper;
    @Autowired
    private BgxtXmzhZbMapper bgxtXmzhZbMapper;
    @Autowired
    private BgxtHisXmMapper bgxtHisXmMapper;
    @Autowired
    private GzszZhxmMapper gzszZhxmMapper;
    @Autowired
    private SysRzMapper sysRzMapper;
    @Autowired
    private SysXtszMapper sysXtszMapper;

    public void saveLog(String czydm, String sm, Integer ztid, Integer zxtid) {
        saveLog(czydm, sm, ztid, zxtid, null, null);
    }

    public void saveLog(String czydm, String sm, Integer ztid, Integer zxtid, String czip, String czmk) {
        try {
            SysRz rz = new SysRz();
            rz.setCzrq(java.time.LocalDateTime.now());
            rz.setCzydm(czydm);
            rz.setSm(sm);
            rz.setZtid(ztid);
            rz.setZxtid(zxtid);
            rz.setCzip(czip);
            rz.setCzmk(czmk);
            sysRzMapper.insert(rz);
        } catch (Exception e) {
        }
    }

    public List<Map<String, Object>> listSystems() {
        try {
            return zxtmcMapper.listSystems();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> listOperationTypes(Integer systemId) {
        try {
            return rzztsmMapper.listBySystemId(systemId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Map<String, Object>> listAllOperationTypes() {
        try {
            return rzztsmMapper.listAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Map<String, Object> queryLogs(Integer zxtid, Integer ztid, String czydm, String beginDate, String endDate, Integer includeBak, Integer pageNum, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (beginDate != null && beginDate.trim().isEmpty()) beginDate = null;
            if (endDate != null && endDate.trim().isEmpty()) endDate = null;
            if (czydm != null && czydm.trim().isEmpty()) czydm = null;
            if (endDate != null) endDate = endDate + " 23:59:59";
            if (pageNum == null || pageNum < 1) pageNum = 1;
            if (pageSize == null || pageSize < 1) pageSize = 50;
            int offset = (pageNum - 1) * pageSize;
            Long total = ryszMapper.countLogs(zxtid, ztid, czydm, beginDate, endDate);
            result.put("data", ryszMapper.queryLogs(zxtid, ztid, czydm, beginDate, endDate, offset, pageSize));
            result.put("total", total);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            result.put("success", true);
        } catch (Exception e) {
            result.put("data", new ArrayList<>());
            result.put("total", 0);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    public List<Map<String, Object>> listOperators(String name) {
        if (name != null && !name.isEmpty()) {
            return ryszMapper.searchOperators(name);
        }
        return ryszMapper.listOperators();
    }

    public List<Map<String, Object>> listMaterialFeeItems(String pym) {
        return gzszMxMapper.listMaterialFeeItems(pym);
    }

    @Transactional
    public boolean bindMaterialFee(MaterialFeeSyncRequest data) {
        try {
            gzszMxMapper.bindMaterialFee(data.getXlbh(), data.getClfdm(), data.getClfmc());
            return true;
        } catch (Exception e) {
            throw new RuntimeException("绑定失败：" + e.getMessage());
        }
    }

    @Transactional
    public boolean syncMaterialFee(MaterialFeeSyncRequest data) {
        try {
            gzszMxMapper.syncMaterialFee(data.getSgys(), data.getClfdm(), data.getClfmc());
            return true;
        } catch (Exception e) {
            throw new RuntimeException("同步失败：" + e.getMessage());
        }
    }

    @Transactional
    public void unbindMaterialFee(Integer xlbh) {
        gzszMxMapper.unbindMaterialFee(xlbh);
    }

    public List<Map<String, Object>> listMaterialBindings(Integer dlid, String xlmc) {
        return gzszMxMapper.listMaterialBindings(dlid, xlmc);
    }

    public List<Map<String, Object>> listTubeCategories() {
        return gzszZbMapper.listCategories();
    }

    @Transactional
    public void saveTubeCategory(TubeCategoryRequest data) {
        GzszZb entity = new GzszZb();
        entity.setDlmc(data.getDlmc());
        entity.setIsuse(data.getIsuse() != null ? data.getIsuse() : 1);
        if (data.getDlid() == null || data.getDlid() == 0) {
            gzszZbMapper.insert(entity);
        } else {
            entity.setDlid(data.getDlid());
            gzszZbMapper.updateCategory(entity);
        }
    }

    public List<Map<String, Object>> listTubeSubcategories(Integer dlid) {
        if (dlid != null) {
            return gzszMxMapper.listSubcategoriesByDlid(dlid);
        }
        return gzszMxMapper.listSubcategories();
    }

    @Transactional
    public void saveTubeSubcategory(TubeSubcategoryRequest data) {
        GzszMx entity = new GzszMx();
        entity.setDlid(data.getDlid());
        entity.setXlmc(data.getXlmc());
        entity.setIsuse(data.getIsuse());
        entity.setYxxh(data.getYxxh());
        entity.setTmhgs(data.getTmhgs());
        entity.setTmfs(data.getTmfs());
        entity.setSflbz(data.getSflbz() != null ? new java.math.BigDecimal(data.getSflbz()) : null);
        entity.setCjyq(data.getCjyq());
        entity.setZysx(data.getZysx());
        entity.setSgys(data.getSgys());
        entity.setClfdm(data.getClfdm());
        entity.setClfmc(data.getClfmc());
        if (data.getXlbh() == null || data.getXlbh() == 0) {
            gzszMxMapper.insertSubcategory(entity);
        } else {
            entity.setXlbh(data.getXlbh());
            gzszMxMapper.updateSubcategory(entity);
            bgxtXmzhZbMapper.updateComboItemsFromSubcategory(
                entity.getXlbh(),
                entity.getSgys(),
                entity.getCjyq(),
                entity.getZysx()
            );
        }
    }

    public List<Map<String, Object>> listComboItemsBySubcategory(Integer xlbh) {
        return gzszZhxmMapper.listByXlbh(xlbh);
    }

    public List<Map<String, Object>> listAvailableComboItems(Integer xlbh) {
        return gzszZhxmMapper.listAvailableItems(xlbh);
    }

    @Transactional
    public void saveComboMapping(Integer xlbh, List<Map<String, Object>> items) {
        for (Map<String, Object> item : items) {
            gzszZhxmMapper.insertMapping(
                xlbh,
                (Integer) item.get("zhid"),
                (String) item.get("zhxmmc"),
                item.get("yxxh") != null ? (Integer) item.get("yxxh") : 0
            );
        }
    }

    @Transactional
    public void removeComboMapping(Integer xlbh, Integer zhid) {
        gzszZhxmMapper.deleteByXlbhAndCode(xlbh, zhid);
    }

    @Transactional
    public int batchAddComboByInstrument(Integer xlbh, Integer sbDjid) {
        List<Map<String, Object>> combos = gzszZhxmMapper.listCombosByInstrument(sbDjid);
        int count = 0;
        for (Map<String, Object> combo : combos) {
            try {
                gzszZhxmMapper.insertMapping(
                    xlbh,
                    (Integer) combo.get("zhid"),
                    (String) combo.get("zhmc"),
                    0
                );
                count++;
            } catch (Exception e) {
            }
        }
        return count;
    }

    public List<Map<String, Object>> listTatSettings() {
        return bgxtTsxmtatMapper.listTatSettings();
    }

    @Transactional
    public void saveTatSetting(Map<String, Object> data) {
        bgxtTsxmtatMapper.saveTatSetting(
            (Integer) data.get("sbDjid"),
            (Integer) data.get("brlb"),
            (Integer) data.get("syqk"),
            (Integer) data.get("zhid"),
            (String) data.get("zhmc"),
            (Integer) data.get("tat")
        );
    }

    @Transactional
    public void deleteTatSetting(Integer sbDjid, Integer brlb, Integer syqk, Integer zhid) {
        bgxtTsxmtatMapper.deleteTatSetting(sbDjid, brlb, syqk, zhid);
    }

    public Map<String, Object> autoCalculateTat(Integer sbDjid, Integer buffer) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (sbDjid != null) {
                bgxtXmzhZbMapper.autoCalculateTatForInstrument(buffer, sbDjid);
            } else {
                bgxtXmzhZbMapper.autoCalculateTat(buffer);
            }
            result.put("success", true);
            result.put("message", "自动计算完成");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "计算失败：" + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> getAuxSettings() {
        Map<String, Object> result = new HashMap<>();
        try {
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.lis.entity.SysXtsz> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            wrapper.like("xtsz_key", "aux_");
            java.util.List<com.lis.entity.SysXtsz> settings = sysXtszMapper.selectList(wrapper);

            java.util.Map<String, Boolean> auxSettings = new java.util.HashMap<>();
            auxSettings.put("autoPrint", false);
            auxSettings.put("autoAudit", false);
            auxSettings.put("autoNotify", false);
            auxSettings.put("enableQc", false);
            auxSettings.put("enableCriticalValueAlert", false);

            for (com.lis.entity.SysXtsz setting : settings) {
                String key = setting.getXtszKey();
                if (key != null && key.startsWith("aux_")) {
                    String shortKey = key.substring(4);
                    Boolean value = "1".equals(setting.getXtszValue()) || "true".equalsIgnoreCase(setting.getXtszValue());
                    auxSettings.put(shortKey, value);
                }
            }

            result.put("success", true);
            result.put("settings", auxSettings);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取辅助设置失败：" + e.getMessage());
        }
        return result;
    }

    @Transactional
    public Map<String, Object> saveAuxSettings(java.util.Map<String, Boolean> settings) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (settings == null || settings.isEmpty()) {
                result.put("success", false);
                result.put("message", "设置为空");
                return result;
            }

            for (java.util.Map.Entry<String, Boolean> entry : settings.entrySet()) {
                String key = "aux_" + entry.getKey();
                String value = Boolean.TRUE.equals(entry.getValue()) ? "1" : "0";

                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.lis.entity.SysXtsz> wrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
                wrapper.eq("xtsz_key", key);
                com.lis.entity.SysXtsz existing = sysXtszMapper.selectOne(wrapper);

                if (existing != null) {
                    existing.setXtszValue(value);
                    sysXtszMapper.updateById(existing);
                } else {
                    com.lis.entity.SysXtsz newSetting = new com.lis.entity.SysXtsz();
                    newSetting.setXtszKey(key);
                    newSetting.setXtszValue(value);
                    newSetting.setXtszDesc("辅助功能设置 - " + entry.getKey());
                    sysXtszMapper.insert(newSetting);
                }
            }

            result.put("success", true);
            result.put("message", "辅助设置已保存");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "保存辅助设置失败：" + e.getMessage());
        }
        return result;
    }
}