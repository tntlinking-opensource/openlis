package com.lis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.SysBrlb;
import com.lis.entity.SysCzydm;
import com.lis.entity.SysGzzd;
import com.lis.entity.SysKssz;
import com.lis.entity.SysTybmzb;
import com.lis.entity.SysTybmmx;
import com.lis.mapper.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BaseSettingService {

    @Autowired
    private SysKsszMapper sysKsszMapper;

    @Autowired
    private SysRyszMapper sysRyszMapper;

    @Autowired
    private SysGzzdMapper sysGzzdMapper;

    @Autowired
    private SysBrlbMapper sysBrlbMapper;

    @Autowired
    private SysTybmzbMapper sysTybmzbMapper;

    @Autowired
    private SysTybmmxMapper sysTybmmxMapper;

    @Autowired
    private SysCzydmMapper sysCzydmMapper;

    @Transactional
    public void cleanupGarbledDepts() {
        sysKsszMapper.cleanupGarbled();
    }

    public List<Map<String, Object>> listDepts(String keyword, Boolean sybz) {
        return sysKsszMapper.findAll();
    }

    @Transactional
    public boolean saveDept(SysKssz dept) {
        if (dept.getKsdm() == null || dept.getKsdm().trim().isEmpty()) {
            throw new IllegalArgumentException("科室代码不能为空");
        }
        if (dept.getKsmc() == null || dept.getKsmc().trim().isEmpty()) {
            throw new IllegalArgumentException("科室名称不能为空");
        }

        List<Map<String, Object>> existing = sysKsszMapper.findByKsdm(dept.getKsdm());
        if (existing.isEmpty()) {
            int zxbz = dept.getZxbz() != null && dept.getZxbz() == 1 ? 1 : 1;
            int sybzVal = dept.getSybz() != null && dept.getSybz() == 1 ? 1 : 1;
            dept.setZxbz(zxbz);
            dept.setSybz(sybzVal);
            sysKsszMapper.insertDept(dept);
        } else {
            int zxbz = dept.getZxbz() != null && dept.getZxbz() == 1 ? 1 : 1;
            int sybzVal = dept.getSybz() != null && dept.getSybz() == 1 ? 1 : 1;
            dept.setZxbz(zxbz);
            dept.setSybz(sybzVal);
            sysKsszMapper.updateByKsdm(dept);
        }
        return true;
    }

    public Map<String, Object> getDeptByKsdm(String ksdm) {
        List<Map<String, Object>> result = sysKsszMapper.findOneByKsdm(ksdm);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    @Transactional
    public int cleanupGarbledWorkgroups() {
        return sysGzzdMapper.cleanupGarbled();
    }

    public List<Map<String, Object>> listWorkgroups(String ssksdm, String gzzdm, String keyword, Integer gzzlx, Boolean sybz) {
        Integer sybzVal = sybz != null ? (sybz ? 1 : 0) : null;
        return sysGzzdMapper.listWorkgroups(gzzdm, keyword, sybzVal);
    }

    @Transactional
    public boolean saveWorkgroup(Map<String, Object> req) {
        String gzzdm = (String) req.get("gzzdm");
        String gzzmc = (String) req.get("gzzmc");
        String pym = (String) req.get("pym");
        Integer gzzlx = req.get("gzzlx") != null ? Integer.parseInt(String.valueOf(req.get("gzzlx"))) : null;
        Integer xh = req.get("xh") != null ? Integer.parseInt(String.valueOf(req.get("xh"))) : 1;
        Boolean sybz = (Boolean) req.get("sybz");
        int sybzVal = sybz == null || sybz ? 1 : 0;

        if (gzzdm == null || gzzdm.trim().isEmpty()) {
            throw new IllegalArgumentException("工作组代码不能为空");
        }
        if (gzzmc == null || gzzmc.trim().isEmpty()) {
            throw new IllegalArgumentException("工作组名称不能为空");
        }
        if (gzzlx == null) {
            throw new IllegalArgumentException("工作组类型不能为空");
        }

        List<Map<String, Object>> existing = sysGzzdMapper.findByGzdm(gzzdm.trim());
        if (existing.isEmpty()) {
            sysGzzdMapper.insertWorkgroup(gzzdm.trim(), gzzmc.trim(), pym != null ? pym.trim() : "", gzzlx, xh, sybzVal);
        } else {
            sysGzzdMapper.updateByGzdm(gzzmc.trim(), pym != null ? pym.trim() : "", gzzlx, xh, sybzVal, gzzdm.trim());
        }
        return true;
    }

    public List<Map<String, Object>> listStaff(String keyword, Boolean sybz) {
        String k = (keyword == null || keyword.trim().isEmpty()) ? "" : keyword.trim();
        QueryWrapper<SysCzydm> wrapper = new QueryWrapper<>();
        if (!k.isEmpty()) {
            wrapper.and(w -> w.like("czyxm", k).or().like("pym", k));
        }
        if (sybz != null) {
            wrapper.eq("sybz", sybz ? 1 : 0);
        }
        wrapper.orderByAsc("czydm");
        List<SysCzydm> list = sysCzydmMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysCzydm c : list) {
            Map<String, Object> row = new HashMap<>();
            row.put("czydm", c.getCzydm());
            row.put("czyxm", c.getCzyxm());
            row.put("pym", c.getPym());
            row.put("ksdm", c.getKsdm());
            row.put("zcdm", c.getZcdm());
            row.put("sybz", c.getSybz());
            row.put("glybz", c.getGlybz());
            result.add(row);
        }
        return result;
    }

    public List<Map<String, Object>> listStaffWithGroup(String keyword, String ksdm, String gzzdm) {
        return sysCzydmMapper.findGroupList(keyword, ksdm, gzzdm);
    }

    @Transactional
    public boolean saveStaff(Map<String, Object> req) {
        String czydm = (String) req.get("czydm");
        String czyxm = (String) req.get("czyxm");
        String pym = (String) req.get("pym");
        String ksdm = (String) req.get("ksdm");
        String zcdm = (String) req.get("zcdm");
        Boolean sybz = (Boolean) req.get("sybz");
        Boolean glybz = (Boolean) req.get("glybz");
        String gzzdm = (String) req.get("gzzdm");

        if (czydm == null || czydm.trim().isEmpty()) {
            throw new IllegalArgumentException("人员代码不能为空");
        }
        if (czyxm == null || czyxm.trim().isEmpty()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (ksdm == null || ksdm.trim().isEmpty()) {
            throw new IllegalArgumentException("科室代码不能为空");
        }

        QueryWrapper<SysCzydm> wrapper = new QueryWrapper<>();
        wrapper.eq("czydm", czydm.trim());
        SysCzydm existing = sysCzydmMapper.selectOne(wrapper);

        Boolean sybzVal = sybz == null || sybz;
        Boolean glybzVal = glybz != null && glybz;

        if (existing == null) {
            SysCzydm newStaff = new SysCzydm();
            newStaff.setCzydm(czydm.trim());
            newStaff.setCzyxm(czyxm.trim());
            newStaff.setPym(pym != null ? pym.trim() : "");
            newStaff.setKsdm(ksdm.trim());
            newStaff.setZcdm(zcdm != null ? zcdm.trim() : "");
            newStaff.setSybz(sybzVal);
            newStaff.setGlybz(glybzVal);
            newStaff.setGzzdm(gzzdm != null ? gzzdm.trim() : "");
            sysCzydmMapper.insert(newStaff);
        } else {
            existing.setCzyxm(czyxm.trim());
            existing.setPym(pym != null ? pym.trim() : "");
            existing.setKsdm(ksdm.trim());
            existing.setZcdm(zcdm != null ? zcdm.trim() : "");
            existing.setSybz(sybzVal);
            existing.setGlybz(glybzVal);
            existing.setGzzdm(gzzdm != null ? gzzdm.trim() : "");
            sysCzydmMapper.updateById(existing);
        }
        return true;
    }

    public List<Map<String, Object>> listPatientCategories(Integer brlb, String keyword, Boolean tybz) {
        Integer tybzVal = tybz != null ? (tybz ? 1 : 0) : null;
        return sysBrlbMapper.listCategories(brlb, keyword, tybzVal);
    }

    public Map<String, Object> getNextPatientCategoryCode() {
        List<Map<String, Object>> result = sysBrlbMapper.getNextCode();
        Integer maxBm = result.get(0).get("maxbm") != null ?
            Integer.parseInt(String.valueOf(result.get(0).get("maxbm"))) : 0;
        Map<String, Object> m = new HashMap<>();
        m.put("bm", maxBm + 1);
        return m;
    }

    @Transactional
    public boolean savePatientCategory(Map<String, Object> req) {
        Integer bm = req.get("bm") != null ? Integer.parseInt(String.valueOf(req.get("bm"))) : null;
        String bmsm = (String) req.get("bmsm");
        String pym = (String) req.get("pym");
        String qtdm = (String) req.get("qtdm");
        Integer sjlyfs = req.get("sjlyfs") != null ? Integer.parseInt(String.valueOf(req.get("sjlyfs"))) : 0;
        String sjlyfsms = (String) req.get("sjlyfsms");
        Boolean mrksbz = (Boolean) req.get("mrksbz");
        String mrksdm = (String) req.get("mrksdm");
        String mrksmc = (String) req.get("mrksmc");
        Boolean mrysbz = (Boolean) req.get("mrysbz");
        String mrysdm = (String) req.get("mrysdm");
        String mrysmc = (String) req.get("mrysmc");
        Integer xh = req.get("xh") != null ? Integer.parseInt(String.valueOf(req.get("xh"))) : 1;
        Boolean tybz = (Boolean) req.get("tybz");
        Boolean jkbz = (Boolean) req.get("jkbz");
        Boolean jgxxBz = (Boolean) req.get("jgxxBz");
        String jgxx = (String) req.get("jgxx");
        Boolean qxkz = (Boolean) req.get("qxkz");
        Integer brlbys = req.get("brlbys") != null ? Integer.parseInt(String.valueOf(req.get("brlbys"))) : 16777201;

        if (bmsm == null || bmsm.trim().isEmpty()) {
            throw new IllegalArgumentException("类别名称不能为空");
        }
        if (bm == null) {
            throw new IllegalArgumentException("类别代码不能为空");
        }

        SysBrlb existing = sysBrlbMapper.selectById(bm);
        if (existing == null) {
            SysBrlb newBrlb = new SysBrlb();
            newBrlb.setBm(bm);
            newBrlb.setBmsm(bmsm.trim());
            newBrlb.setPym(pym != null ? pym.trim() : "");
            newBrlb.setQtdm(qtdm != null ? qtdm.trim() : "");
            newBrlb.setSjlyfs(sjlyfs);
            newBrlb.setSjlyfsms(sjlyfsms != null ? sjlyfsms : "");
            newBrlb.setMrksbz(mrksbz != null ? mrksbz : false);
            newBrlb.setMrksdm(mrksdm != null ? mrksdm : "");
            newBrlb.setMrksmc(mrksmc != null ? mrksmc : "");
            newBrlb.setMrysbz(mrysbz != null ? mrysbz : false);
            newBrlb.setMrysdm(mrysdm != null ? mrysdm : "");
            newBrlb.setMrysmc(mrysmc != null ? mrysmc : "");
            newBrlb.setXh(xh);
            newBrlb.setTybz(tybz != null ? tybz : false);
            newBrlb.setJkbz(jkbz != null ? jkbz : false);
            newBrlb.setJgxxBz(jgxxBz != null ? jgxxBz : false);
            newBrlb.setJgxx(jgxx != null ? jgxx : "");
            newBrlb.setQxkz(qxkz != null ? qxkz : false);
            newBrlb.setQxmc("Action_lb" + bm);
            newBrlb.setBrlbys(brlbys);
            sysBrlbMapper.insert(newBrlb);
        } else {
            existing.setBmsm(bmsm.trim());
            existing.setPym(pym != null ? pym.trim() : "");
            existing.setQtdm(qtdm != null ? qtdm.trim() : "");
            existing.setSjlyfs(sjlyfs);
            existing.setSjlyfsms(sjlyfsms != null ? sjlyfsms : "");
            existing.setMrksbz(mrksbz != null ? mrksbz : false);
            existing.setMrksdm(mrksdm != null ? mrksdm : "");
            existing.setMrksmc(mrksmc != null ? mrksmc : "");
            existing.setMrysbz(mrysbz != null ? mrysbz : false);
            existing.setMrysdm(mrysdm != null ? mrysdm : "");
            existing.setMrysmc(mrysmc != null ? mrysmc : "");
            existing.setXh(xh);
            existing.setTybz(tybz != null ? tybz : false);
            existing.setJkbz(jkbz != null ? jkbz : false);
            existing.setJgxxBz(jgxxBz != null ? jgxxBz : false);
            existing.setJgxx(jgxx != null ? jgxx : "");
            existing.setQxkz(qxkz != null ? qxkz : false);
            existing.setQxmc("Action_lb" + bm);
            if (brlbys != null) {
                existing.setBrlbys(brlbys);
            }
            sysBrlbMapper.updateById(existing);
        }
        return true;
    }

    public List<Map<String, Object>> getCommonCodeMainList() {
        QueryWrapper<SysTybmzb> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("id");
        List<SysTybmzb> list = sysTybmzbMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysTybmzb t : list) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", t.getId());
            row.put("bmdm", t.getBmdm());
            row.put("bmmc", t.getBmmc());
            row.put("tybz", t.getTybz());
            result.add(row);
        }
        return result;
    }

    public List<Object> getCommonCodeList() {
        List<Object> result = new ArrayList<>();

        Map<String, String> item1 = new HashMap<>();
        item1.put("code", "01");
        item1.put("name", "门诊");
        result.add(item1);

        Map<String, String> item2 = new HashMap<>();
        item2.put("code", "02");
        item2.put("name", "住院");
        result.add(item2);

        Map<String, String> item3 = new HashMap<>();
        item3.put("code", "03");
        item3.put("name", "急诊");
        result.add(item3);

        Map<String, String> item4 = new HashMap<>();
        item4.put("code", "04");
        item4.put("name", "体检");
        result.add(item4);

        return result;
    }

    public List<SysTybmmx> getCommonCodeDetailList(Integer bmdm) {
        QueryWrapper<SysTybmmx> wrapper = new QueryWrapper<>();
        wrapper.eq("bmdm", bmdm);
        wrapper.orderByAsc("bm");
        return sysTybmmxMapper.selectList(wrapper);
    }

    @Transactional
    public boolean saveCommonCodeMain(SysTybmzb entity) {
        if (entity.getId() != null) {
            sysTybmzbMapper.updateById(entity);
        } else {
            sysTybmzbMapper.insert(entity);
        }
        return true;
    }

    @Transactional
    public SysTybmmx saveCommonCodeDetail(SysTybmmx entity) {
        if (entity.getId() != null) {
            sysTybmmxMapper.updateById(entity);
        } else {
            sysTybmmxMapper.insert(entity);
        }
        return entity;
    }

    @Transactional
    public boolean deleteCommonCodeDetail(Integer id) {
        sysTybmmxMapper.deleteById(id);
        return true;
    }

    @Data
    public static class ApiResponse {
        private Boolean success;
        private String message;
        private Object data;

        public static ApiResponse success(String msg) {
            ApiResponse r = new ApiResponse();
            r.setSuccess(true);
            r.setMessage(msg);
            return r;
        }

        public static ApiResponse fail(String msg) {
            ApiResponse r = new ApiResponse();
            r.setSuccess(false);
            r.setMessage(msg);
            return r;
        }
    }
}
