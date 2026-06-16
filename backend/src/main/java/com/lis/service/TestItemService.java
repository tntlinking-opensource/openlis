package com.lis.service;

import com.lis.entity.SysJyxmFull;
import com.lis.mapper.SysJyxmFullMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TestItemService {

    @Autowired
    private SysJyxmFullMapper testItemMapper;

    public List<Map<String, Object>> list(String keyword, Integer sbDjid) {
        return testItemMapper.listTestItems(keyword, sbDjid);
    }

    public List<Map<String, Object>> search(String pym) {
        return testItemMapper.searchTestItems(pym);
    }

    public List<Map<String, Object>> searchByPym(String pym, Integer sbDjid) {
        return testItemMapper.searchTestItemsByPymAndInstrument(pym, sbDjid);
    }

    @Transactional
    public void save(Map<String, Object> data) {
        String xmzwmc = (String) data.get("xmzwmc");
        String pym = (String) data.get("pym");
        if (xmzwmc == null || xmzwmc.trim().isEmpty()) {
            throw new IllegalArgumentException("项目中文名称不能为空");
        }
        if (pym == null || pym.trim().isEmpty()) {
            throw new IllegalArgumentException("拼音码不能为空");
        }
        Object xmidObj = data.get("xmid");
        SysJyxmFull entity = new SysJyxmFull();
        entity.setXmdm((String) data.get("xmdm"));
        entity.setXmzwmc(xmzwmc);
        entity.setXmywmc((String) data.get("xmywmc"));
        entity.setPym(pym);
        entity.setQtdm((String) data.get("qtdm"));
        entity.setXmdw((String) data.get("xmdw"));
        entity.setXmjd(toInt(data.get("xmjd"), 3));
        entity.setXmlx(toInt(data.get("xmlx"), 0));
        entity.setJsbz(toBool(data.get("jsbz")));
        entity.setXs(toDecimal(data.get("xs"), "1"));
        entity.setSjxhl(toDecimal(data.get("sjxhl"), "0"));
        entity.setTybz(toBool(data.get("tybz")));
        entity.setDybz(toBool(data.get("dybz"), true));
        entity.setZsbz(toBool(data.get("zsbz")));
        entity.setSfbz(toDecimal(data.get("sfbz"), "0"));
        entity.setGzl(toDecimal(data.get("gzl"), "0"));
        entity.setHisFydm((String) data.get("hisFydm"));
        entity.setHisJyxmmc((String) data.get("hisJyxmmc"));
        entity.setZskXmdm((String) data.get("zskXmdm"));
        entity.setZskXmmc((String) data.get("zskXmmc"));
        entity.setItemType(toInt(data.get("itemType"), 0));
        entity.setLcyy((String) data.get("lcyy"));
        if (xmidObj == null || "0".equals(String.valueOf(xmidObj))) {
            testItemMapper.insert(entity);
        } else {
            entity.setXmid(Integer.parseInt(String.valueOf(xmidObj)));
            testItemMapper.updateById(entity);
        }
    }

    @Transactional
    public void delete(Integer xmid) {
        int used = testItemMapper.countComboUsage(xmid);
        if (used > 0) {
            throw new IllegalArgumentException("该项目正在组合中使用，无法删除");
        }
        testItemMapper.deleteById(xmid);
    }

    public List<Map<String, Object>> getTypes() {
        try {
            return testItemMapper.getItemTypes();
        } catch (Exception e) {
            List<Map<String, Object>> defaults = new ArrayList<>();
            Object[][] types = {{0, "常规"}, {1, "生化"}, {2, "免疫"}, {3, "血液"}, {4, "微生物"}};
            for (Object[] t : types) {
                Map<String, Object> m = new HashMap<>();
                m.put("dm", t[0]);
                m.put("name", t[1]);
                defaults.add(m);
            }
            return defaults;
        }
    }

    public List<Map<String, Object>> getPrecisions() {
        String[] labels = {"整数(0位小数)", "1位小数", "2位小数", "3位小数", "4位小数"};
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            Map<String, Object> m = new HashMap<>();
            m.put("value", i + 1);
            m.put("label", labels[i]);
            list.add(m);
        }
        return list;
    }

    private int toInt(Object val, int def) {
        if (val == null) return def;
        try { return Integer.parseInt(String.valueOf(val)); } catch (Exception e) { return def; }
    }

    private boolean toBool(Object val) { return toBool(val, false); }
    private boolean toBool(Object val, boolean def) {
        if (val == null) return def;
        return "true".equals(String.valueOf(val)) || "1".equals(String.valueOf(val));
    }

    private BigDecimal toDecimal(Object val, String def) {
        if (val == null) return new BigDecimal(def);
        try { return new BigDecimal(String.valueOf(val)); } catch (Exception e) { return new BigDecimal(def); }
    }
}
