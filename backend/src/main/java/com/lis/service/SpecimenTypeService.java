package com.lis.service;

import com.lis.entity.SysBbzlDict;
import com.lis.entity.SysGdz;
import com.lis.mapper.SysBbzlDictMapper;
import com.lis.mapper.SysGdzMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SpecimenTypeService {

    @Autowired
    private SysBbzlDictMapper specimenTypeMapper;
    @Autowired
    private SysGdzMapper highLowFlagMapper;

    public List<Map<String, Object>> listSpecimenTypes(String keyword) {
        return specimenTypeMapper.listSpecimenTypes(keyword);
    }

    @Transactional
    public void saveSpecimenType(Map<String, Object> data) {
        String bmsm = (String) data.get("bmsm");
        String pym = (String) data.get("pym");
        if (bmsm == null || bmsm.trim().isEmpty()) {
            throw new IllegalArgumentException("名称不能为空");
        }
        if (pym == null || pym.trim().isEmpty()) {
            throw new IllegalArgumentException("拼音码不能为空");
        }
        SysBbzlDict entity = new SysBbzlDict();
        entity.setBmsm(bmsm);
        entity.setPym(pym);
        entity.setQtdm((String) data.get("qtdm"));
        entity.setXssx(toInt(data.get("xssx")));
        entity.setWhonet((String) data.get("whonet"));
        entity.setHisBmdm((String) data.get("hisBmdm"));
        entity.setRqdm((String) data.get("rqdm"));
        entity.setRqlx((String) data.get("rqlx"));
        entity.setCjyq((String) data.get("cjyq"));
        Object bmObj = data.get("bm");
        if (bmObj == null || "0".equals(String.valueOf(bmObj))) {
            specimenTypeMapper.insertSpecimenType(entity);
        } else {
            entity.setBm(Integer.parseInt(String.valueOf(bmObj)));
            specimenTypeMapper.updateSpecimenType(entity);
        }
    }

    @Transactional
    public void deleteSpecimenType(Integer bm) {
        specimenTypeMapper.deleteSpecimenType(bm);
    }

    public List<Map<String, Object>> listHighLowFlags() {
        return highLowFlagMapper.listAll();
    }

    public Map<String, Object> getActiveHighLowFlag() {
        List<Map<String, Object>> rows = highLowFlagMapper.listActive();
        Map<String, Object> result = new HashMap<>();
        for (Map<String, Object> row : rows) {
            int bs = ((Number) row.get("bs")).intValue();
            switch (bs) {
                case 1: result.put("high", row.get("bh")); break;
                case 0: result.put("low", row.get("bh")); break;
                case 3: result.put("alarmHigh", row.get("bh")); break;
                case 2: result.put("alarmLow", row.get("bh")); break;
            }
        }
        result.put("bhid", rows.isEmpty() ? 0 : rows.get(0).get("bhid"));
        return result;
    }

    @Transactional
    public void saveHighLowFlag(Map<String, Object> data) {
        String high = (String) data.get("high");
        String low = (String) data.get("low");
        if (high == null || high.trim().isEmpty() || low == null || low.trim().isEmpty()) {
            throw new IllegalArgumentException("高标志和低标志不能为空");
        }
        Boolean activate = data.get("activate") != null &&
            ("true".equals(String.valueOf(data.get("activate"))) || "1".equals(String.valueOf(data.get("activate"))));

        if (activate) {
            highLowFlagMapper.deactivateAll();
        }

        Object bhidObj = data.get("bhid");
        if (bhidObj == null || "0".equals(String.valueOf(bhidObj))) {
            String alarmHigh = data.get("alarmHigh") != null ? (String) data.get("alarmHigh") : "";
            String alarmLow = data.get("alarmLow") != null ? (String) data.get("alarmLow") : "";
            Integer maxId = highLowFlagMapper.getMaxBhid();
            int newId = (maxId != null ? maxId : 0) / 4 * 4 + 4;
            int sybz = activate ? 1 : 0;
            highLowFlagMapper.insertFlag(newId, high, 1, sybz);
            highLowFlagMapper.insertFlag(newId + 1, low, 0, sybz);
            highLowFlagMapper.insertFlag(newId + 2, alarmHigh, 3, sybz);
            highLowFlagMapper.insertFlag(newId + 3, alarmLow, 2, sybz);
        } else {
            int bhid = Integer.parseInt(String.valueOf(bhidObj));
            int baseId = (bhid - 1) / 4 * 4 + 1;
            int sybz = activate ? 1 : 0;
            highLowFlagMapper.updateFlag(high, sybz, baseId, 1);
            highLowFlagMapper.updateFlag(low, sybz, baseId + 1, 0);
            String alarmHigh = data.get("alarmHigh") != null ? (String) data.get("alarmHigh") : "";
            String alarmLow = data.get("alarmLow") != null ? (String) data.get("alarmLow") : "";
            highLowFlagMapper.updateFlag(alarmHigh, sybz, baseId + 2, 3);
            highLowFlagMapper.updateFlag(alarmLow, sybz, baseId + 3, 2);
        }
    }

    @Transactional
    public void deleteHighLowFlag(Integer bhid) {
        int baseId = (bhid - 1) / 4 * 4 + 1;
        int active = highLowFlagMapper.countActiveByBhid(baseId);
        if (active > 0) {
            throw new IllegalArgumentException("正在使用的标志组不能删除，请先停用");
        }
        highLowFlagMapper.deleteByBhidRange(baseId, baseId + 3);
    }

    private int toInt(Object val) {
        if (val == null) return 0;
        try { return Integer.parseInt(String.valueOf(val)); } catch (Exception e) { return 0; }
    }
}