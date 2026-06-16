package com.lis.service;

import com.lis.entity.BgxtXmzhZb;
import com.lis.entity.BgxtXmzhYgsjsz;
import com.lis.mapper.BgxtXmzhMxMapper;
import com.lis.mapper.BgxtXmzhYgsjszMapper;
import com.lis.mapper.BgxtXmzhZbMapper;
import com.lis.mapper.BgxtYqxmzhMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ComboService {

    @Autowired
    private BgxtXmzhZbMapper comboMapper;
    @Autowired
    private BgxtXmzhMxMapper comboItemMapper;
    @Autowired
    private BgxtYqxmzhMapper yqxmzhMapper;
    @Autowired
    private BgxtXmzhYgsjszMapper ygsjszMapper;

    public List<Map<String, Object>> list(String keyword) {
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        return comboMapper.listCombos(params);
    }

    public List<Map<String, Object>> search(String name) {
        return comboMapper.searchCombos(name);
    }

    @Transactional
    public void save(Map<String, Object> data) {
        String zhmc = (String) data.get("zhmc");
        if (zhmc == null || zhmc.trim().isEmpty()) {
            throw new IllegalArgumentException("组合名称不能为空");
        }
        Object zhidObj = data.get("zhid");
        BgxtXmzhZb entity = new BgxtXmzhZb();
        entity.setZhmc(zhmc);
        entity.setBbzl(toIntObj(data.get("bbzl")));
        entity.setPym((String) data.get("pym"));
        entity.setQtdm((String) data.get("qtdm"));
        entity.setHisXmdm((String) data.get("hisXmdm"));
        entity.setHisZhmc((String) data.get("hisZhmc"));
        entity.setSfbz(toDecObj(data.get("sfbz")));
        entity.setGzl(toIntObj(data.get("gzl")));
        entity.setQybz(data.get("qybz") != null ? toIntObj(data.get("qybz")) : 1);
        entity.setLbid(toIntObj(data.get("lbid")));
        entity.setBqys(toIntObj(data.get("bqys")));
        entity.setYssm((String) data.get("yssm"));
        entity.setYbzxxg(toIntObj(data.get("ybzxxg")));
        entity.setReportType(toIntObj(data.get("reportType")));
        entity.setGroupType((String) data.get("groupType"));
        entity.setGetSampleFromHis(toIntObj(data.get("getSampleFromHIS")));
        entity.setDefaultResult((String) data.get("defaultResult"));
        entity.setProjectLevel(toIntObj(data.get("projectLevel")));
        entity.setDdsj(toIntObj(data.get("ddsj")));
        entity.setZhZy((String) data.get("zh_zy"));
        entity.setZhSyz((String) data.get("zh_syz"));
        entity.setZhCjyq((String) data.get("zh_cjyq"));
        if (zhidObj == null || "0".equals(String.valueOf(zhidObj))) {
            comboMapper.insert(entity);
        } else {
            entity.setZhid(Integer.parseInt(String.valueOf(zhidObj)));
            comboMapper.updateById(entity);
        }
    }

    @Transactional
    public void delete(Integer zhid) {
        comboItemMapper.deleteByZhid(zhid);
        yqxmzhMapper.deleteByZhid(zhid);
        comboMapper.deleteById(zhid);
    }

    public List<Map<String, Object>> getItems(Integer zhid) {
        return comboItemMapper.getComboItems(zhid);
    }

    @Transactional
    public void addItem(Integer zhid, Map<String, Object> data) {
        Object xmid = data.get("xmid");
        if (xmid == null) throw new IllegalArgumentException("项目ID不能为空");
        List<Map<String, Object>> existing = comboItemMapper.existsByZhidAndXmid(zhid, xmid);
        if (!existing.isEmpty()) throw new IllegalArgumentException("该项目已在组合中");
        Integer maxId = comboItemMapper.getGlobalMaxId();
        comboItemMapper.insertComboItem((maxId != null ? maxId : 0) + 1, zhid, xmid,
                data.get("mrjg"), data.get("sbDjid"), data.get("bz"));
    }

    @Transactional
    public void removeItem(Integer zhid, Integer xmid) {
        comboItemMapper.removeComboItem(zhid, xmid);
    }

    @Transactional
    public void reorder(Integer zhid, List<Integer> xmidOrder) {
        for (int i = 0; i < xmidOrder.size(); i++) {
            comboItemMapper.reorderComboItem(i + 1, zhid, xmidOrder.get(i));
        }
    }

    @Transactional
    public void copyFrom(Integer zhid, Integer sourceId) {
        comboItemMapper.copyComboItems(zhid, sourceId);
    }

    private Integer toIntObj(Object val) {
        if (val == null) return null;
        try { return Integer.parseInt(String.valueOf(val)); } catch (Exception e) { return null; }
    }

    private BigDecimal toDecObj(Object val) {
        if (val == null) return null;
        try { return new BigDecimal(String.valueOf(val)); } catch (Exception e) { return null; }
    }

    public List<Map<String, Object>> listCompletionSettings(Integer zhid, Integer szlb, Integer tybz) {
        return ygsjszMapper.selectSettings(zhid, szlb, tybz);
    }

    @Transactional
    public Map<String, Object> saveCompletionSetting(Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        Integer id = toInt(data.get("id"));
        Integer zhid = toInt(data.get("zhid"));
        Integer szlb = toInt(data.get("szlb"));
        String qssj = String.valueOf(data.get("qssj"));
        String jssj = String.valueOf(data.get("jssj"));
        Integer ygrq = toInt(data.get("ygrq"));
        String ygsj = String.valueOf(data.get("ygsj"));
        Integer ddsj = toInt(data.get("ddsj"));
        Integer tybz = toInt(data.get("tybz"));

        if (id == null || id == 0) {
            int overlapCount = ygsjszMapper.countTimeOverlap(zhid, 0, qssj, jssj);
            if (overlapCount > 0) {
                result.put("success", false);
                result.put("message", "同一个组合设置的时间段不能有交叉!");
                return result;
            }
            BgxtXmzhYgsjsz record = new BgxtXmzhYgsjsz();
            record.setZhid(zhid);
            record.setSzlb(szlb);
            record.setQssj(qssj);
            record.setJssj(jssj);
            record.setYgrq(ygrq);
            record.setYgsj(ygsj);
            record.setDdsj(ddsj);
            record.setTybz(tybz);
            ygsjszMapper.insert(record);
        } else {
            int overlapCount = ygsjszMapper.countTimeOverlap(zhid, id, qssj, jssj);
            if (overlapCount > 0) {
                result.put("success", false);
                result.put("message", "同一个组合设置的时间段不能有交叉!");
                return result;
            }
            BgxtXmzhYgsjsz record = new BgxtXmzhYgsjsz();
            record.setId(id);
            record.setZhid(zhid);
            record.setSzlb(szlb);
            record.setQssj(qssj);
            record.setJssj(jssj);
            record.setYgrq(ygrq);
            record.setYgsj(ygsj);
            record.setDdsj(ddsj);
            record.setTybz(tybz);
            ygsjszMapper.updateById(record);
        }
        result.put("success", true);
        result.put("message", "保存成功");
        return result;
    }

    @Transactional
    public Map<String, Object> deleteCompletionSetting(Integer id) {
        Map<String, Object> result = new HashMap<>();
        ygsjszMapper.deleteById(id);
        result.put("success", true);
        result.put("message", "删除成功");
        return result;
    }

    private Integer toInt(Object val) {
        if (val == null) return null;
        try { return Integer.parseInt(String.valueOf(val)); } catch (Exception e) { return null; }
    }
}
