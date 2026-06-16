package com.lis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.Instrument;
import com.lis.entity.SysBghbzb;
import com.lis.entity.SysBghbmx;
import com.lis.entity.SysGzzd;
import com.lis.entity.SysJyxm;
import com.lis.entity.SysJyxmFull;
import com.lis.entity.SysKssz;
import com.lis.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class InstrumentService {

    @Autowired
    private InstrumentMapper instrumentMapper;
    @Autowired
    private BgxtYqxmzhMapper bgxtYqxmzhMapper;
    @Autowired
    private SysCjdzbMapper sysCjdzbMapper;
    @Autowired
    private BgxtXmzhMxMapper bgxtXmzhMxMapper;
    @Autowired
    private SysXmckzMapper sysXmckzMapper;
    @Autowired
    private BgxtXmmrzMapper bgxtXmmrzMapper;
    @Autowired
    private BgxtXmzhZbMapper bgxtXmzhZbMapper;

    @Autowired
    private SysKsszMapper sysKsszMapper;
    @Autowired
    private SysBghbzbMapper sysBghbzbMapper;
    @Autowired
    private SysBghbmxMapper sysBghbmxMapper;
    @Autowired
    private SysGzzdMapper sysGzzdMapper;
    @Autowired
    private SysCjyszSettingsMapper sysCjyszSettingsMapper;
    @Autowired
    private SysJsgsMapper sysJsgsMapper;
    @Autowired
    private SysJyxmMapper sysJyxmMapper;
    @Autowired
    private SysJyxmFullMapper sysJyxmFullMapper;
    @Autowired
    private BgxtKsyqszMapper bgxtKsyqszMapper;
    @Autowired
    private BgxtTybbszMapper bgxtTybbszMapper;

    public List<Map<String, Object>> listInstruments(String ksdm) {
        List<Instrument> insList = instrumentMapper.selectList(new QueryWrapper<>());
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Instrument ins : insList) {
            if (ksdm == null || ksdm.trim().isEmpty() || ksdm.equals("0000") || ksdm.equals(ins.getKsdm())) {
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
                map.put("bgbh", ins.getBgbh());
                map.put("bgmc", ins.getBgmc());
                mapList.add(map);
            }
        }
        return mapList;
    }

    public Instrument getInstrumentFull(Integer sbDjid) {
        return instrumentMapper.selectById(sbDjid);
    }

    @Transactional
    public SaveResult saveInstrument(Map<String, Object> data) {
        SaveResult result = new SaveResult();
        try {
            String sbmc = (String) data.get("sbmc");
            if (sbmc == null || sbmc.trim().isEmpty()) {
                result.success = false;
                result.message = "设备名称不能为空";
                return result;
            }

            Integer sbDjid = toInt(data.get("sbDjid"));
            Instrument instrument = new Instrument();
            instrument.setSbDjid(sbDjid == null || sbDjid == 0 ? null : sbDjid);
            instrument.setSbdm((String) data.get("sbdm"));
            instrument.setSbmc(sbmc);
            instrument.setSbbm((String) data.get("sbbm"));
            instrument.setKsdm((String) data.get("ksdm"));
            instrument.setGzzdm((String) data.get("gzzdm"));
            instrument.setPym((String) data.get("pym"));
            instrument.setZxbz(toBool(data.get("zxbz"), true));
            instrument.setTybz(toBool(data.get("tybz"), false));
            instrument.setComsm((String) data.get("comsm"));
            Integer btlVal = toInt(data.get("btl"));
            instrument.setBtl(btlVal != null && btlVal != 0 ? btlVal : 9600);
            instrument.setJyw((String) data.get("jyw"));
            Integer sjwVal = toInt(data.get("sjw"));
            instrument.setSjw(sjwVal != null && sjwVal != 0 ? sjwVal : 8);
            Integer tzwVal = toInt(data.get("tzw"));
            instrument.setTzw(tzwVal != null && tzwVal != 0 ? tzwVal : 1);
            instrument.setXmxsfs((String) data.get("xmxsfs"));
            instrument.setBgbt((String) data.get("bgbt"));
            instrument.setBgyj((String) data.get("bgyj"));
            Integer mrzhidVal = toInt(data.get("mrzhid"));
            instrument.setMrzhid(mrzhidVal != null && mrzhidVal != 0 ? mrzhidVal : null);
            instrument.setTx((String) data.get("tx"));
            instrument.setDyfs((String) data.get("dyfs"));
            instrument.setShzfs((String) data.get("shzfs"));
            Integer sxplVal = toInt(data.get("sxpl"));
            instrument.setSxpl(sxplVal != null ? sxplVal : 0);
            instrument.setYcxwc(toBool(data.get("ycxwc"), false));
            instrument.setXsfs((String) data.get("xsfs"));
            instrument.setBblb((String) data.get("bblb"));
            instrument.setBgbh((String) data.get("bgbh"));
            instrument.setBgmc((String) data.get("bgmc"));
            instrument.setXslb((String) data.get("xslb"));
            instrument.setZklb((String) data.get("zklb"));
            instrument.setYqzd((String) data.get("yqzd"));
            Integer zjjgtsVal = toInt(data.get("zjjgts"));
            instrument.setZjjgts(zjjgtsVal != null ? zjjgtsVal : 7);
            instrument.setZkjh((String) data.get("zkjh"));
            instrument.setJzjh((String) data.get("jzjh"));
            instrument.setCjcx((String) data.get("cjcx"));
            instrument.setSzdm((String) data.get("szdm"));
            instrument.setKztsbz(toBool(data.get("kztsbz"), false));
            instrument.setJkxmxz(toBool(data.get("jkxmxz"), false));
            instrument.setFsztsbz(toBool(data.get("fsztsbz"), false));
            instrument.setZerotsbz(toBool(data.get("zerotsbz"), false));
            instrument.setIp((String) data.get("ip"));
            instrument.setDk((String) data.get("dk"));
            instrument.setSjklj((String) data.get("sjklj"));
            instrument.setWjdz((String) data.get("wjdz"));
            instrument.setBfdz((String) data.get("bfdz"));
            instrument.setWjyhm((String) data.get("wjyhm"));
            instrument.setWjmm((String) data.get("wjmm"));
            instrument.setYszcz((String) data.get("yszcz"));
            instrument.setYspgz((String) data.get("yspgz"));
            instrument.setYspdz((String) data.get("yspdz"));
            instrument.setYsbjgz((String) data.get("ysbjgz"));
            instrument.setYsbjdz((String) data.get("ysbjdz"));
            instrument.setYswsh((String) data.get("yswsh"));
            instrument.setYsysh((String) data.get("ysysh"));
            instrument.setYsycy((String) data.get("ysycy"));
            instrument.setYsydy((String) data.get("ysydy"));
            instrument.setYsyjy((String) data.get("ysyjy"));
            instrument.setYsycz((String) data.get("ysycz"));
            instrument.setYswjz((String) data.get("yswjz"));
            instrument.setYsjgwc((String) data.get("ysjgwc"));

            if (sbDjid == null || sbDjid == 0) {
                instrumentMapper.insert(instrument);
            } else {
                instrumentMapper.updateById(instrument);
            }
            result.success = true;
            result.message = "保存成功";
        } catch (Exception e) {
            log.error("操作失败", e);
            result.success = false;
            result.message = "保存失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public SaveResult deleteInstrument(Integer sbDjid) {
        SaveResult result = new SaveResult();
        try {
            if (sbDjid == null || sbDjid <= 0) {
                result.success = false;
                result.message = "无效的仪器ID";
                return result;
            }
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
        } catch (Exception e) {
            log.error("操作失败", e);
            result.success = false;
            result.message = "删除失败：" + e.getMessage();
        }
        return result;
    }

    public List<Map<String, Object>> getInstrumentTree() {
        List<Instrument> instruments = instrumentMapper.selectList(new QueryWrapper<>());
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Instrument inst : instruments) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", "inst_" + inst.getSbDjid());
            node.put("label", inst.getSbmc());
            node.put("type", "instrument");
            node.put("sbDjid", inst.getSbDjid());
            List<Map<String, Object>> combos = bgxtYqxmzhMapper.findCombosByInstrument(inst.getSbDjid());
            List<Map<String, Object>> children = new ArrayList<>();
            for (Map<String, Object> combo : combos) {
                Map<String, Object> child = new HashMap<>();
                child.put("id", "combo_" + inst.getSbDjid() + "_" + combo.get("zhid"));
                child.put("label", combo.get("zhmc"));
                child.put("type", "combo");
                child.put("sbDjid", inst.getSbDjid());
                child.put("zhid", combo.get("zhid"));
                child.put("zhsx", combo.get("zhsx"));
                List<Map<String, Object>> comboItems = bgxtXmzhMxMapper.getComboItems((Integer) combo.get("zhid"));
                List<Map<String, Object>> itemChildren = new ArrayList<>();
                for (Map<String, Object> item : comboItems) {
                    Map<String, Object> itemNode = new HashMap<>();
                    itemNode.put("id", "item_" + item.get("xmid"));
                    itemNode.put("label", item.get("xmzwmc"));
                    itemNode.put("type", "item");
                    itemNode.put("xmid", item.get("xmid"));
                    itemNode.put("xmdm", item.get("xmdm"));
                    itemNode.put("xmdw", item.get("xmdw"));
                    itemChildren.add(itemNode);
                }
                child.put("children", itemChildren);
                children.add(child);
            }
            node.put("children", children);
            tree.add(node);
        }
        return tree;
    }

    @Transactional
    public ApiResult assignCombo(Integer sbDjid, Integer zhid) {
        ApiResult result = new ApiResult();
        try {
            if (sbDjid == null || zhid == null) {
                result.success = false;
                result.message = "仪器ID和组合ID不能为空";
                return result;
            }
            List<Map<String, Object>> existing = bgxtYqxmzhMapper.existsBySbDjidAndZhid(sbDjid, zhid);
            if (!existing.isEmpty()) {
                result.success = false;
                result.message = "该组合已分配到此仪器";
                return result;
            }
            Integer maxSx = bgxtYqxmzhMapper.getMaxZhsxBySbDjid(sbDjid);
            bgxtYqxmzhMapper.assignCombo(sbDjid, zhid, (maxSx != null ? maxSx : 0) + 1);
            result.success = true;
            result.message = "分配成功";
        } catch (Exception e) {
            result.success = false;
            result.message = "分配失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public ApiResult removeCombo(Integer instId, Integer comboId) {
        ApiResult result = new ApiResult();
        try {
            bgxtYqxmzhMapper.removeCombo(instId, comboId);
            result.success = true;
            result.message = "移除成功";
        } catch (Exception e) {
            result.success = false;
            result.message = "移除失败：" + e.getMessage();
        }
        return result;
    }

    public List<Map<String, Object>> getUnassignedCombos(Integer sbDjid) {
        return bgxtXmzhZbMapper.findUnassignedCombos(sbDjid);
    }

    public List<Map<String, Object>> getItemsByInstrument(Integer sbDjid) {
        return sysCjdzbMapper.findItemsByInstrument(sbDjid);
    }

    @Transactional
    public ApiResult addItemToInstrument(Integer sbDjid, Integer xmid) {
        ApiResult result = new ApiResult();
        try {
            if (sbDjid == null || xmid == null) {
                result.success = false;
                result.message = "仪器ID和项目ID不能为空";
                return result;
            }
            List<Map<String, Object>> existing = sysCjdzbMapper.existsBySbDjidAndXmid(sbDjid, xmid);
            if (!existing.isEmpty()) {
                result.success = false;
                result.message = "该项目已分配到此仪器";
                return result;
            }
            Integer maxDyxh = sysCjdzbMapper.getMaxDyxhBySbDjid(sbDjid);
            sysCjdzbMapper.insertCoeff(sbDjid, xmid, null, (maxDyxh != null ? maxDyxh : 0) + 1);
            result.success = true;
            result.message = "添加成功";
        } catch (Exception e) {
            result.success = false;
            result.message = "添加失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public ApiResult removeItemFromInstrument(Integer sbDjid, Integer xmid) {
        ApiResult result = new ApiResult();
        try {
            sysCjdzbMapper.deleteByInstrumentAndItem(sbDjid, xmid);
            result.success = true;
            result.message = "移除成功";
        } catch (Exception e) {
            result.success = false;
            result.message = "移除失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public ApiResult saveInstItem(Map<String, Object> data) {
        ApiResult result = new ApiResult();
        try {
            Integer sbDjid = toInt(data.get("sbDjid"));
            Integer xmid = toInt(data.get("xmid"));
            if (sbDjid == null || xmid == null) {
                result.success = false;
                result.message = "仪器ID和项目ID不能为空";
                return result;
            }
            String xmbm = String.valueOf(data.get("xmbm"));
            Object xs = data.get("xs");
            String yqxmdw = String.valueOf(data.get("yqxmdw"));
            String xmjc = String.valueOf(data.get("xmjc"));
            if ("null".equalsIgnoreCase(xmbm)) xmbm = "";
            if ("null".equalsIgnoreCase(yqxmdw)) yqxmdw = "";
            if ("null".equalsIgnoreCase(xmjc)) xmjc = "";
            sysCjdzbMapper.updateInstItem(sbDjid, xmid, xmbm, xs, yqxmdw, xmjc);
            result.success = true;
            result.message = "保存成功";
        } catch (Exception e) {
            log.error("保存仪器项目设置失败", e);
            result.success = false;
            result.message = "保存失败：" + e.getMessage();
        }
        return result;
    }

    public List<Map<String, Object>> getItemTree() {
        List<Instrument> instruments = instrumentMapper.selectList(new QueryWrapper<>());
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Instrument inst : instruments) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", "inst_" + inst.getSbDjid());
            node.put("label", inst.getSbmc());
            node.put("type", "instrument");
            node.put("sbDjid", inst.getSbDjid());
            List<Map<String, Object>> items = sysCjdzbMapper.findItemsByInstrument(inst.getSbDjid());
            List<Map<String, Object>> children = new ArrayList<>();
            for (Map<String, Object> item : items) {
                Object xmzwmc = item.get("xmzwmc");
                if (xmzwmc == null || "null".equalsIgnoreCase(String.valueOf(xmzwmc))) continue;
                Map<String, Object> child = new HashMap<>(item);
                child.put("id", "item_" + inst.getSbDjid() + "_" + item.get("xmid"));
                Object xmdm = item.get("xmdm");
                String xmdmStr = String.valueOf(xmdm);
                if (xmdm == null || "null".equalsIgnoreCase(xmdmStr) || xmdmStr.trim().isEmpty()) {
                    xmdmStr = "";
                }
                child.put("label", xmzwmc + (xmdmStr.isEmpty() ? "" : " (" + xmdmStr + ")"));
                child.put("type", "item");
                child.put("sbDjid", inst.getSbDjid());
                children.add(child);
            }
            node.put("children", children);
            tree.add(node);
        }
        return tree;
    }

    public List<Map<String, Object>> getRefRanges(Integer instId, Integer xmid) {
        return sysXmckzMapper.getRefRanges(xmid, instId);
    }

    @Transactional
    public ApiResult saveRefRange(Map<String, Object> data) {
        ApiResult result = new ApiResult();
        try {
            Object id = data.get("id");
            if (id != null && !"0".equals(String.valueOf(id)) && !"".equals(String.valueOf(id))) {
                sysXmckzMapper.updateRefRange(data);
            } else {
                sysXmckzMapper.insertRefRange(data);
            }
            result.success = true;
            result.message = "保存成功";
        } catch (Exception e) {
            result.success = false;
            result.message = "保存失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public ApiResult deleteRefRange(Integer id) {
        ApiResult result = new ApiResult();
        try {
            sysXmckzMapper.deleteRefRange(id);
            result.success = true;
            result.message = "删除成功";
        } catch (Exception e) {
            result.success = false;
            result.message = "删除失败：" + e.getMessage();
        }
        return result;
    }

    public Map<String, Object> getDefaultValue(Integer instId, Integer xmid) {
        List<Map<String, Object>> result = bgxtXmmrzMapper.findByXmidAndSbDjid(xmid, instId);
        if (result.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("xmid", xmid);
            empty.put("sbDjid", instId);
            empty.put("mrz", "");
            empty.put("mr", false);
            return empty;
        }
        return result.get(0);
    }

    @Transactional
    public ApiResult saveDefault(Map<String, Object> data) {
        ApiResult result = new ApiResult();
        try {
            Integer xmid = toInt(data.get("xmid"));
            Integer sbDjid = toInt(data.get("sbDjid"));
            List<Map<String, Object>> existing = bgxtXmmrzMapper.findByXmidAndSbDjid(xmid, sbDjid);
            if (existing.isEmpty()) {
                bgxtXmmrzMapper.insertDefault(xmid, sbDjid, data.get("mrz"), data.get("mr") != null ? data.get("mr") : 0);
            } else {
                bgxtXmmrzMapper.updateDefault(data.get("mrz"), data.get("mr"), xmid, sbDjid);
            }
            result.success = true;
            result.message = "保存成功";
        } catch (Exception e) {
            result.success = false;
            result.message = "保存失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public ApiResult batchCoeff(Integer sbDjid, List<Map<String, Object>> items) {
        ApiResult result = new ApiResult();
        try {
            if (items == null || items.isEmpty()) {
                result.success = false;
                result.message = "项目列表不能为空";
                return result;
            }
            sysCjdzbMapper.batchUpsertCoefficients(sbDjid, items);
            result.success = true;
            result.message = "批量保存成功";
        } catch (Exception e) {
            result.success = false;
            result.message = "批量保存失败：" + e.getMessage();
        }
        return result;
    }

    public List<Map<String, Object>> getCoefficientsByInstrument(Integer sbDjid) {
        if (sbDjid == null) {
            return Collections.emptyList();
        }
        return sysCjdzbMapper.findCoefficientsByInstrument(sbDjid);
    }

    public List<Map<String, Object>> listDepartments(Boolean sybz) {
        QueryWrapper<com.lis.entity.SysKssz> wrapper = new QueryWrapper<>();
        if (sybz != null) {
            wrapper.eq("sybz", sybz ? 1 : 0);
        }
        wrapper.orderByAsc("ksdm");
        List<com.lis.entity.SysKssz> list = sysKsszMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        for (com.lis.entity.SysKssz k : list) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", k.getKsid());
            row.put("ksdm", k.getKsdm());
            row.put("ksmc", k.getKsmc());
            row.put("pym", k.getPym());
            row.put("ksxz", k.getKsxz());
            row.put("zxbz", k.getZxbz());
            row.put("sybz", k.getSybz());
            result.add(row);
        }
        return result;
    }

    public List<Map<String, Object>> listDevices(String category, String ksdm, String gzzdm) {
        List<Instrument> instruments = instrumentMapper.selectList(new QueryWrapper<>());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Instrument ins : instruments) {
            Map<String, Object> row = new HashMap<>();
            row.put("sb_djid", ins.getSbDjid());
            row.put("sbmc", ins.getSbmc());
            result.add(row);
        }
        return result;
    }

    public List<Map<String, Object>> listMergeGroups() {
        List<SysBghbzb> groups = sysBghbzbMapper.selectList(new QueryWrapper<>());
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysBghbzb g : groups) {
            Map<String, Object> row = new HashMap<>();
            row.put("hbid", g.getHbid());
            row.put("hbmc", g.getHbmc());
            int count = sysBghbmxMapper.selectCount(new QueryWrapper<SysBghbmx>().eq("hbid", g.getHbid())).intValue();
            row.put("count", count);
            result.add(row);
        }
        return result;
    }

    public List<Map<String, Object>> getMergeGroupDetail(Integer hbid) {
        if (hbid == null) return new ArrayList<>();
        List<SysBghbmx> details = sysBghbmxMapper.selectList(new QueryWrapper<SysBghbmx>().eq("hbid", hbid));
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysBghbmx d : details) {
            Map<String, Object> row = new HashMap<>();
            row.put("hbid", d.getHbid());
            row.put("sbDjid", d.getSbDjid());
            row.put("sybz", d.getSybz());
            Instrument ins = instrumentMapper.selectById(d.getSbDjid());
            if (ins != null) {
                row.put("sbmc", ins.getSbmc());
                row.put("sbdm", ins.getSbdm());
            }
            result.add(row);
        }
        return result;
    }

    @Transactional
    public SaveResult saveMergeGroup(Map<String, Object> data) {
        SaveResult result = new SaveResult();
        try {
            String hbmc = (String) data.get("hbmc");
            if (hbmc == null || hbmc.trim().isEmpty()) {
                result.success = false;
                result.message = "合并组名称不能为空";
                return result;
            }
            Integer hbid = toInt(data.get("hbid"));
            if (hbid == null || hbid == 0) {
                SysBghbzb zb = new SysBghbzb();
                zb.setHbmc(hbmc);
                sysBghbzbMapper.insert(zb);
                result.message = "新增成功";
            } else {
                SysBghbzb zb = new SysBghbzb();
                zb.setHbid(hbid);
                zb.setHbmc(hbmc);
                sysBghbzbMapper.updateById(zb);
                result.message = "更新成功";
            }
            result.success = true;
        } catch (Exception e) {
            log.error("操作失败", e);
            result.success = false;
            result.message = "保存失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public SaveResult deleteMergeGroup(Integer hbid) {
        SaveResult result = new SaveResult();
        try {
            if (hbid == null || hbid <= 0) {
                result.success = false;
                result.message = "无效的合并组ID";
                return result;
            }
            sysBghbmxMapper.delete(new QueryWrapper<SysBghbmx>().eq("hbid", hbid));
            sysBghbzbMapper.deleteById(hbid);
            result.success = true;
            result.message = "删除成功";
        } catch (Exception e) {
            log.error("操作失败", e);
            result.success = false;
            result.message = "删除失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public SaveResult addDeviceToMergeGroup(Map<String, Object> payload) {
        SaveResult result = new SaveResult();
        try {
            Integer hbid = toInt(payload.get("hbid"));
            Integer sbDjid = toInt(payload.get("sbDjid"));
            if (hbid == null || sbDjid == null) {
                result.success = false;
                result.message = "合并组ID和设备ID不能为空";
                return result;
            }
            long count = sysBghbmxMapper.selectCount(new QueryWrapper<SysBghbmx>()
                    .eq("hbid", hbid).eq("sb_djid", sbDjid)).longValue();
            if (count > 0) {
                result.success = false;
                result.message = "该设备已在合并组中";
                return result;
            }
            SysBghbmx mx = new SysBghbmx();
            mx.setHbid(hbid);
            mx.setSbDjid(sbDjid);
            mx.setSybz(true);
            sysBghbmxMapper.insert(mx);
            result.success = true;
            result.message = "添加成功";
        } catch (Exception e) {
            log.error("操作失败", e);
            result.success = false;
            result.message = "添加失败：" + e.getMessage();
        }
        return result;
    }

    public List<Map<String, Object>> getWorkgroupTree() {
        List<SysGzzd> workgroups = sysGzzdMapper.selectList(new QueryWrapper<>());
        List<Map<String, Object>> tree = new ArrayList<>();
        for (SysGzzd gzz : workgroups) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", "gzz_" + gzz.getGzid());
            node.put("pid", 0);
            node.put("dsp", gzz.getGzmc() + " (" + gzz.getGzdm() + ")");
            node.put("type", "workgroup");
            node.put("gzid", gzz.getGzid());
            node.put("gzdm", gzz.getGzdm());
            node.put("gzmc", gzz.getGzmc());

            List<Map<String, Object>> children = new ArrayList<>();
            List<Instrument> devices = instrumentMapper.selectList(
                new QueryWrapper<Instrument>().eq("gzzdm", gzz.getGzdm()).orderByAsc("sbmc"));
            for (Instrument dev : devices) {
                Map<String, Object> child = new HashMap<>();
                child.put("id", "dev_" + dev.getSbDjid());
                child.put("pid", "gzz_" + gzz.getGzid());
                child.put("dsp", dev.getSbmc() + " (" + dev.getSbdm() + ")");
                child.put("type", "device");
                child.put("sbDjid", dev.getSbDjid());
                child.put("sbmc", dev.getSbmc());
                children.add(child);
            }
            node.put("children", children);
            tree.add(node);
        }
        return tree;
    }

    public List<Map<String, Object>> getUnassignedWorkgroupDevices() {
        List<Instrument> allDevices = instrumentMapper.selectList(new QueryWrapper<Instrument>());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Instrument dev : allDevices) {
            if (dev.getGzzdm() == null || dev.getGzzdm().trim().isEmpty()) {
                Map<String, Object> row = new HashMap<>();
                row.put("sb_djid", dev.getSbDjid());
                row.put("sbdm", dev.getSbdm());
                row.put("sbmc", dev.getSbmc());
                result.add(row);
            }
        }
        return result;
    }

    @Transactional
    public SaveResult assignToWorkgroup(Map<String, Object> payload) {
        SaveResult result = new SaveResult();
        try {
            Integer sbDjid = toInt(payload.get("sbDjid"));
            String gzzdm = (String) payload.get("gzzdm");
            if (sbDjid == null) {
                result.success = false;
                result.message = "设备ID不能为空";
                return result;
            }
            if (gzzdm == null || gzzdm.trim().isEmpty()) {
                result.success = false;
                result.message = "工作组代码不能为空";
                return result;
            }
            Instrument ins = instrumentMapper.selectById(sbDjid);
            if (ins == null) {
                result.success = false;
                result.message = "设备不存在";
                return result;
            }
            ins.setGzzdm(gzzdm);
            instrumentMapper.updateById(ins);
            result.success = true;
            result.message = "分配成功";
        } catch (Exception e) {
            log.error("操作失败", e);
            result.success = false;
            result.message = "分配失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public SaveResult unassignFromWorkgroup(Integer sbDjid) {
        SaveResult result = new SaveResult();
        try {
            if (sbDjid == null) {
                result.success = false;
                result.message = "设备ID不能为空";
                return result;
            }
            Instrument ins = instrumentMapper.selectById(sbDjid);
            if (ins == null) {
                result.success = false;
                result.message = "设备不存在";
                return result;
            }
            ins.setGzzdm(null);
            instrumentMapper.updateById(ins);
            result.success = true;
            result.message = "取消分配成功";
        } catch (Exception e) {
            log.error("操作失败", e);
            result.success = false;
            result.message = "取消分配失败：" + e.getMessage();
        }
        return result;
    }

    public List<Map<String, Object>> getSiteTree() {
        List<SysKssz> depts = sysKsszMapper.selectList(new QueryWrapper<>());
        List<Map<String, Object>> tree = new ArrayList<>();
        for (SysKssz dept : depts) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", "dept_" + dept.getKsid());
            node.put("pid", 0);
            node.put("dsp", dept.getKsmc() + " (" + dept.getKsdm() + ")");
            node.put("type", "site");
            node.put("ksid", dept.getKsid());
            node.put("ksdm", dept.getKsdm());
            node.put("ksmc", dept.getKsmc());

            List<Map<String, Object>> children = new ArrayList<>();
            List<Instrument> devices = instrumentMapper.selectList(
                new QueryWrapper<Instrument>().eq("ksdm", dept.getKsdm()).orderByAsc("sbmc"));
            for (Instrument dev : devices) {
                Map<String, Object> child = new HashMap<>();
                child.put("id", "dev_" + dev.getSbDjid());
                child.put("pid", "dept_" + dept.getKsid());
                child.put("dsp", dev.getSbmc() + " (" + dev.getSbdm() + ")");
                child.put("type", "device");
                child.put("sbDjid", dev.getSbDjid());
                child.put("sbmc", dev.getSbmc());
                children.add(child);
            }
            node.put("children", children);
            tree.add(node);
        }
        return tree;
    }

    public List<Map<String, Object>> getUnassignedSiteDevices() {
        List<Instrument> allDevices = instrumentMapper.selectList(new QueryWrapper<Instrument>());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Instrument dev : allDevices) {
            if (dev.getKsdm() == null || dev.getKsdm().trim().isEmpty()) {
                Map<String, Object> row = new HashMap<>();
                row.put("sb_djid", dev.getSbDjid());
                row.put("sbdm", dev.getSbdm());
                row.put("sbmc", dev.getSbmc());
                result.add(row);
            }
        }
        return result;
    }

    @Transactional
    public SaveResult assignToSite(Map<String, Object> payload) {
        SaveResult result = new SaveResult();
        try {
            Integer sbDjid = toInt(payload.get("sbDjid"));
            String ksdm = (String) payload.get("ksdm");
            if (sbDjid == null) {
                result.success = false;
                result.message = "设备ID不能为空";
                return result;
            }
            if (ksdm == null || ksdm.trim().isEmpty()) {
                result.success = false;
                result.message = "站点代码不能为空";
                return result;
            }
            Instrument ins = instrumentMapper.selectById(sbDjid);
            if (ins == null) {
                result.success = false;
                result.message = "设备不存在";
                return result;
            }
            ins.setKsdm(ksdm);
            instrumentMapper.updateById(ins);
            result.success = true;
            result.message = "分配成功";
        } catch (Exception e) {
            log.error("操作失败", e);
            result.success = false;
            result.message = "分配失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public SaveResult unassignFromSite(Integer sbDjid) {
        SaveResult result = new SaveResult();
        try {
            if (sbDjid == null) {
                result.success = false;
                result.message = "设备ID不能为空";
                return result;
            }
            Instrument ins = instrumentMapper.selectById(sbDjid);
            if (ins == null) {
                result.success = false;
                result.message = "设备不存在";
                return result;
            }
            ins.setKsdm(null);
            instrumentMapper.updateById(ins);
            result.success = true;
            result.message = "取消分配成功";
        } catch (Exception e) {
            log.error("操作失败", e);
            result.success = false;
            result.message = "取消分配失败：" + e.getMessage();
        }
        return result;
    }

    private Integer toInt(Object val) {
        if (val == null) return null;
        try { return Integer.parseInt(String.valueOf(val)); } catch (Exception e) { return null; }
    }

    private Boolean toBool(Object val, Boolean defaultVal) {
        if (val == null) return defaultVal;
        return "true".equals(String.valueOf(val)) || "1".equals(String.valueOf(val));
    }

    public List<Map<String, Object>> getDataReplaceSettings(Integer sbDjid, Integer xmid) {
        if (sbDjid == null || xmid == null) {
            return new ArrayList<>();
        }
        return sysCjyszSettingsMapper.findBySbDjidAndXmid(sbDjid, xmid);
    }

    @Transactional
    public ApiResult saveDataReplaceSetting(Map<String, Object> data) {
        ApiResult result = new ApiResult();
        try {
            Integer id = toInt(data.get("id"));
            Integer sbDjid = toInt(data.get("sbDjid"));
            Integer xmid = toInt(data.get("xmid"));
            String originalValue = String.valueOf(data.get("originalValue"));
            String replaceValue = String.valueOf(data.get("replaceValue"));

            if (sbDjid == null || xmid == null || originalValue == null || originalValue.isEmpty()) {
                result.setSuccess(false);
                result.setMessage("参数不完整");
                return result;
            }

            if (id != null && id > 0) {
                sysCjyszSettingsMapper.update(data);
            } else {
                sysCjyszSettingsMapper.insert(data);
            }
            result.setSuccess(true);
            result.setMessage("保存成功");
        } catch (Exception e) {
            log.error("保存数据替换设置失败", e);
            result.setSuccess(false);
            result.setMessage("保存失败：" + e.getMessage());
        }
        return result;
    }

    @Transactional
    public ApiResult deleteDataReplaceSetting(Integer id) {
        ApiResult result = new ApiResult();
        try {
            if (id == null) {
                result.setSuccess(false);
                result.setMessage("ID不能为空");
                return result;
            }
            sysCjyszSettingsMapper.deleteById(id);
            result.setSuccess(true);
            result.setMessage("删除成功");
        } catch (Exception e) {
            log.error("删除数据替换设置失败", e);
            result.setSuccess(false);
            result.setMessage("删除失败：" + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> getFormula(Integer sbDjid, Integer xmid) {
        if (sbDjid == null || xmid == null) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("sbDjid", sbDjid);
            empty.put("xmid", xmid);
            empty.put("bds", "");
            empty.put("bdssm", "");
            return empty;
        }
        List<Map<String, Object>> list = sysJsgsMapper.findBySbDjidAndXmid(sbDjid, xmid);
        if (list.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("sbDjid", sbDjid);
            empty.put("xmid", xmid);
            empty.put("bds", "");
            empty.put("bdssm", "");
            return empty;
        }
        return list.get(0);
    }

    @Transactional
    public ApiResult saveFormula(Map<String, Object> data) {
        ApiResult result = new ApiResult();
        try {
            Integer sbDjid = toInt(data.get("sbDjid"));
            Integer xmid = toInt(data.get("xmid"));
            String bds = String.valueOf(data.get("bds"));
            String bdssm = String.valueOf(data.get("bdssm"));
            if ("null".equals(bds)) bds = "";
            if ("null".equals(bdssm)) bdssm = "";
            if (sbDjid == null || xmid == null) {
                result.setSuccess(false);
                result.setMessage("仪器ID和项目ID不能为空");
                return result;
            }
            if (bds == null || bds.trim().isEmpty()) {
                sysJsgsMapper.deleteBySbDjidAndXmid(sbDjid, xmid);
            } else {
                sysJsgsMapper.saveOrUpdate(sbDjid, xmid, bds, bdssm);
            }
            result.setSuccess(true);
            result.setMessage("保存成功");
        } catch (Exception e) {
            log.error("保存公式失败", e);
            result.setSuccess(false);
            result.setMessage("保存失败：" + e.getMessage());
        }
        return result;
    }

    public List<Map<String, Object>> getFormulaList(Integer sbDjid) {
        if (sbDjid == null) {
            return new ArrayList<>();
        }
        return sysJsgsMapper.findBySbDjid(sbDjid);
    }

    public List<Map<String, Object>> getProjectsByInstrument(Integer sbDjid) {
        if (sbDjid == null) {
            return new ArrayList<>();
        }
        return sysCjdzbMapper.findItemsByInstrument(sbDjid);
    }

    @lombok.Data
    public static class SaveResult {
        private boolean success;
        private String message;
    }

    @lombok.Data
    public static class ApiResult {
        private Boolean success;
        private String message;
        public static ApiResult ok(String msg) { ApiResult r = new ApiResult(); r.setSuccess(true); r.setMessage(msg); return r; }
        public static ApiResult fail(String msg) { ApiResult r = new ApiResult(); r.setSuccess(false); r.setMessage(msg); return r; }
    }

    // ==================== 科室仪器分配相关方法 ====================

    public List<Map<String, Object>> listDepartments() {
        List<SysKssz> ksszList = sysKsszMapper.selectList(new QueryWrapper<>());
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysKssz ks : ksszList) {
            Map<String, Object> map = new HashMap<>();
            map.put("ksdm", ks.getKsdm());
            map.put("ksmc", ks.getKsmc());
            result.add(map);
        }
        return result;
    }

    public List<Map<String, Object>> listInstrumentsWithAssignmentStatus(String ksdm) {
        if (ksdm == null || ksdm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return bgxtKsyqszMapper.selectAllWithAssignmentStatus(ksdm);
    }

    @Transactional
    public SaveResult assignInstrumentToDepartment(String ksdm, Integer sbDjid, boolean assign) {
        SaveResult result = new SaveResult();
        try {
            if (assign) {
                bgxtKsyqszMapper.insertAssignment(ksdm, sbDjid);
            } else {
                bgxtKsyqszMapper.deleteAssignment(ksdm, sbDjid);
            }
            result.success = true;
            result.message = assign ? "分配成功" : "取消分配成功";
        } catch (Exception e) {
            log.error("分配仪器失败", e);
            result.success = false;
            result.message = "操作失败：" + e.getMessage();
        }
        return result;
    }

    // ==================== 特殊标本设置相关方法 ====================

    public List<Map<String, Object>> listSpecialSampleCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        categories.add(createCategory(0, "溶血"));
        categories.add(createCategory(1, "乳糜"));
        categories.add(createCategory(2, "黄疸"));
        categories.add(createCategory(3, "其他"));
        return categories;
    }

    private Map<String, Object> createCategory(int id, String name) {
        Map<String, Object> cat = new HashMap<>();
        cat.put("mkid", id);
        cat.put("mksm", name);
        return cat;
    }

    public List<Map<String, Object>> listSpecialSampleRules(Integer mkid) {
        if (mkid != null) {
            return bgxtTybbszMapper.selectByMkid(mkid);
        }
        return bgxtTybbszMapper.selectAllRules();
    }

    @Transactional
    public SaveResult addSpecialSampleRule(Integer mkid, Integer xmid, String mksm) {
        SaveResult result = new SaveResult();
        try {
            if (bgxtTybbszMapper.countByMkidAndXmid(mkid, xmid) > 0) {
                result.success = false;
                result.message = "该规则已存在";
                return result;
            }
            bgxtTybbszMapper.insertRule(mkid, xmid, mksm);
            result.success = true;
            result.message = "添加成功";
        } catch (Exception e) {
            log.error("添加特殊标本规则失败", e);
            result.success = false;
            result.message = "添加失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public SaveResult removeSpecialSampleRule(Integer mkid, Integer xmid) {
        SaveResult result = new SaveResult();
        try {
            bgxtTybbszMapper.deleteByMkidAndXmid(mkid, xmid);
            result.success = true;
            result.message = "删除成功";
        } catch (Exception e) {
            log.error("删除特殊标本规则失败", e);
            result.success = false;
            result.message = "删除失败：" + e.getMessage();
        }
        return result;
    }

    // ==================== 项目仪器关联相关方法 ====================
    // 使用 sys_cjdzb 表 (采集对照表)

    public List<Map<String, Object>> listInstrumentsForItem(Integer xmid) {
        if (xmid == null) {
            return new ArrayList<>();
        }
        return sysCjdzbMapper.findInstrumentsByItem(xmid);
    }

    public List<Map<String, Object>> listItemsForInstrument(Integer sbDjid) {
        if (sbDjid == null) {
            return new ArrayList<>();
        }
        return sysCjdzbMapper.findItemsByInstrument(sbDjid);
    }

    @Transactional
    public SaveResult addItemInstrumentRelation(Integer xmid, Integer sbDjid, Integer zhid) {
        SaveResult result = new SaveResult();
        try {
            SysJyxm xm = sysJyxmMapper.selectById(xmid);
            if (xm == null) {
                result.success = false;
                result.message = "项目不存在";
                return result;
            }
            List<Map<String, Object>> existing = sysCjdzbMapper.existsBySbDjidAndXmid(sbDjid, xmid);
            if (existing != null && !existing.isEmpty()) {
                result.success = false;
                result.message = "该关联已存在";
                return result;
            }
            sysCjdzbMapper.insertCoeff(sbDjid, xmid, xm.getXmdm(), 1.0);
            result.success = true;
            result.message = "添加成功";
        } catch (Exception e) {
            log.error("添加项目仪器关联失败", e);
            result.success = false;
            result.message = "添加失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public SaveResult removeItemInstrumentRelation(Integer xmid, Integer sbDjid) {
        SaveResult result = new SaveResult();
        try {
            sysCjdzbMapper.deleteByInstrumentAndItem(sbDjid, xmid);
            result.success = true;
            result.message = "删除成功";
        } catch (Exception e) {
            log.error("删除项目仪器关联失败", e);
            result.success = false;
            result.message = "删除失败：" + e.getMessage();
        }
        return result;
    }

    // ==================== 项目组合相关方法 ====================
    // 使用 bgxt_xmzh_mx, bgxt_xmzh_zb 表

    public List<Map<String, Object>> listCombosForItem(Integer xmid) {
        if (xmid == null) {
            return new ArrayList<>();
        }
        return bgxtXmzhMxMapper.selectByXmid(xmid);
    }

    @Transactional
    public SaveResult addComboToItem(Integer xmid, Integer zhid) {
        SaveResult result = new SaveResult();
        try {
            bgxtXmzhMxMapper.insertXmidZhid(xmid, zhid);

            List<Map<String, Object>> comboInstruments = bgxtYqxmzhMapper.selectByZhid(zhid);
            for (Map<String, Object> inst : comboInstruments) {
                Integer sbDjid = (Integer) inst.get("sb_djid");
                if (sbDjid != null) {
                    try {
                        sysCjdzbMapper.insertCoeff(sbDjid, xmid, null, 1.0);
                    } catch (Exception ignored) {}
                }
            }

            result.success = true;
            result.message = "添加组合成功";
        } catch (Exception e) {
            log.error("添加项目组合失败", e);
            result.success = false;
            result.message = "添加失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public SaveResult removeComboFromItem(Integer xmid, Integer zhid) {
        SaveResult result = new SaveResult();
        try {
            bgxtXmzhMxMapper.deleteByXmidAndZhid(xmid, zhid);
            result.success = true;
            result.message = "删除组合成功";
        } catch (Exception e) {
            log.error("删除项目组合失败", e);
            result.success = false;
            result.message = "删除失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public SaveResult addInstrumentToCombo(Integer xmid, Integer sbDjid) {
        SaveResult result = new SaveResult();
        try {
            List<Map<String, Object>> existing = sysCjdzbMapper.existsBySbDjidAndXmid(sbDjid, xmid);
            if (existing != null && !existing.isEmpty()) {
                result.success = false;
                result.message = "该仪器已关联到此项目";
                return result;
            }
            Integer maxDyxh = sysCjdzbMapper.getMaxDyxhBySbDjid(sbDjid);
            sysCjdzbMapper.insertCoeff(sbDjid, xmid, null, (maxDyxh != null ? maxDyxh : 0) + 1);
            result.success = true;
            result.message = "添加仪器成功";
        } catch (Exception e) {
            log.error("添加组合仪器失败", e);
            result.success = false;
            result.message = "添加失败：" + e.getMessage();
        }
        return result;
    }

    @Transactional
    public SaveResult removeInstrumentFromCombo(Integer xmid, Integer sbDjid) {
        SaveResult result = new SaveResult();
        try {
            sysCjdzbMapper.deleteByInstrumentAndItem(sbDjid, xmid);
            result.success = true;
            result.message = "删除仪器成功";
        } catch (Exception e) {
            log.error("删除组合仪器失败", e);
            result.success = false;
            result.message = "删除失败：" + e.getMessage();
        }
        return result;
    }

    public List<Map<String, Object>> searchItems(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return sysJyxmFullMapper.searchTestItems(keyword);
    }

    public List<Map<String, Object>> searchCombos(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bgxtXmzhZbMapper.listCombos(null);
        }
        return bgxtXmzhZbMapper.searchCombos(keyword);
    }
}
