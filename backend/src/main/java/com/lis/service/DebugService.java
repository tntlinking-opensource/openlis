package com.lis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.BgxtBrxx;
import com.lis.entity.BgxtJyjg;
import com.lis.entity.SysJyxm;
import com.lis.entity.SysXmckz;
import com.lis.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class DebugService {

    @Autowired
    private BgxtBrxxMapper bgxtBrxxMapper;

    @Autowired
    private BgxtJyjgMapper bgxtJyjgMapper;

    @Autowired
    private SysJyxmMapper sysJyxmMapper;

    @Autowired
    private SysXmckzMapper sysXmckzMapper;

    public Map<String, Object> checkResults(Integer brxxId) {
        Map<String, Object> resp = new HashMap<>();
        if (brxxId != null) {
            Integer count = bgxtJyjgMapper.countByBrxxId(brxxId);
            resp.put("resultCount", count);

            QueryWrapper<BgxtJyjg> wrapper = new QueryWrapper<>();
            wrapper.eq("brxx_id", brxxId);
            List<BgxtJyjg> results = bgxtJyjgMapper.selectList(wrapper);
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (BgxtJyjg jg : results) {
                Map<String, Object> row = new HashMap<>();
                row.put("brxx_id", jg.getBrxxId());
                row.put("xmdm", jg.getXmdm());
                row.put("jyjg", jg.getJyjg());
                SysJyxm xm = sysJyxmMapper.selectOne(new QueryWrapper<SysJyxm>().eq("xmdm", jg.getXmdm()));
                if (xm != null) {
                    row.put("xmzwmc", xm.getXmzwmc());
                }
                resultList.add(row);
            }
            resp.put("results", resultList);
        } else {
            List<Map<String, Object>> recent = bgxtJyjgMapper.selectRecentResults();
            resp.put("recentResults", recent);
        }
        return resp;
    }

    public Map<String, Object> checkJyjg(Integer brxxId) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("structure", "Use MySQL DESCRIBE command via database tool");

        if (brxxId != null) {
            QueryWrapper<BgxtJyjg> wrapper = new QueryWrapper<>();
            wrapper.eq("brxx_id", brxxId);
            List<BgxtJyjg> dataList = bgxtJyjgMapper.selectList(wrapper);
            List<Map<String, Object>> data = new ArrayList<>();
            for (BgxtJyjg jg : dataList) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", jg.getId());
                row.put("brxx_id", jg.getBrxxId());
                row.put("jyjg", jg.getJyjg());
                data.add(row);
            }
            resp.put("data", data);
            resp.put("count", data.size());
        }
        return resp;
    }

    public Map<String, Object> checkXm(String xmdm) {
        Map<String, Object> resp = new HashMap<>();
        QueryWrapper<SysJyxm> wrapper = new QueryWrapper<>();
        if (xmdm != null && !xmdm.isEmpty()) {
            wrapper.eq("xmdm", xmdm);
        }
        wrapper.last("LIMIT 10");
        List<SysJyxm> items = sysJyxmMapper.selectList(wrapper);
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (SysJyxm item : items) {
            Map<String, Object> row = new HashMap<>();
            row.put("xmid", item.getXmid());
            row.put("xmzwmc", item.getXmzwmc());
            itemList.add(row);
        }
        resp.put("items", itemList);
        resp.put("count", itemList.size());
        return resp;
    }

    public Map<String, Object> checkMxTable(Integer zhid) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("structure", "Use MySQL DESCRIBE command via database tool");

        if (zhid != null) {
            List<Map<String, Object>> data = bgxtJyjgMapper.selectMxTableData(zhid);
            resp.put("data", data);
        }
        return resp;
    }

    public Map<String, Object> checkTables() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("bgxt_xmzh_mx_columns", "Use MySQL DESCRIBE command via database tool");
        resp.put("sys_xmckz_columns", "Use MySQL DESCRIBE command via database tool");
        resp.put("sys_jyxm_columns", "Use MySQL DESCRIBE command via database tool");
        return resp;
    }

    public Map<String, Object> checkCkzData(Integer xmid) {
        Map<String, Object> resp = new HashMap<>();
        QueryWrapper<SysXmckz> wrapper = new QueryWrapper<>();
        if (xmid != null) {
            wrapper.eq("xmid", xmid);
        }
        wrapper.last("LIMIT 5");
        List<SysXmckz> dataList = sysXmckzMapper.selectList(wrapper);
        List<Map<String, Object>> data = new ArrayList<>();
        for (SysXmckz ckz : dataList) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", ckz.getId());
            row.put("xmid", ckz.getXmid());
            row.put("ckz", ckz.getCkz());
            row.put("ckzgx", ckz.getCkzgx());
            row.put("ckzdx", ckz.getCkzdx());
            data.add(row);
        }
        resp.put("data", data);
        resp.put("count", data.size());
        return resp;
    }

    public Map<String, Object> checkCkz(Integer xmid) {
        return checkCkzData(xmid);
    }

    public Map<String, Object> checkComboItems(Integer zhid) {
        Map<String, Object> resp = new HashMap<>();
        Long count = bgxtJyjgMapper.selectCount(null);
        resp.put("totalItems", count);

        if (zhid != null) {
            Long count2 = bgxtJyjgMapper.selectCountByZhid(zhid);
            resp.put("zhidCount", count2);
            List<Map<String, Object>> sampleData = bgxtJyjgMapper.selectComboItemsSample(zhid);
            resp.put("sampleData", sampleData);
        }
        return resp;
    }

    public Map<String, Object> migrateCkz() {
        Map<String, Object> resp = new HashMap<>();
        Long mySqlCount = bgxtJyjgMapper.selectCount(null);
        resp.put("mySqlBeforeCount", mySqlCount);
        resp.put("message", "需要从SQL Server迁移数据。当前MySQL有 " + mySqlCount + " 条记录，SQL Server有 2464 条记录。");
        resp.put("sqlServerCount", 2464);
        resp.put("needMigration", mySqlCount < 2464);
        return resp;
    }

    @Transactional
    public Map<String, Object> insertTestSamples() {
        Map<String, Object> resp = new HashMap<>();
        insertTestSample("TMH001", "BR001", "测试患者A", 1, 30, 1, 1, "001", "001", "202603170001", 1, 0);
        insertTestSample("TMH002", "BR002", "测试患者B", 2, 25, 1, 1, "001", "002", "202603170002", 2, 0);
        insertTestSample("TMH003", "BR003", "测试患者C", 1, 45, 1, 2, "001", "003", "202603170003", 1, 1);
        insertTestSample("TMH004", "BR004", "测试患者D", 2, 35, 1, 3, "001", "004", "202603170004", 3, 2);
        insertTestSample("TMH005", "BR005", "测试患者E", 1, 50, 1, 1, "001", "005", "202603170005", 1, 3);
        resp.put("success", true);
        resp.put("message", "成功插入5条测试样本数据");
        return resp;
    }

    private void insertTestSample(String tmh, String brh, String brxm, Integer brxb, Integer brnl, Integer nllx, Integer brlb, String ksdm, String brch, String syh, Integer bbzl, Integer ybzt) {
        BgxtBrxx brxx = new BgxtBrxx();
        brxx.setBrxxTmh(tmh);
        brxx.setBrbh(brh);
        brxx.setBrxm(brxm);
        brxx.setBrxb(brxb);
        brxx.setBrnl(String.valueOf(brnl));
        brxx.setNllx(String.valueOf(nllx));
        brxx.setBrlb(brlb);
        brxx.setSyqk(1);
        brxx.setKsdm(ksdm);
        brxx.setBrch(brch);
        brxx.setSyh(syh);
        brxx.setBbzl(bbzl);
        brxx.setYbzt(ybzt);
        brxx.setJyrq(java.time.LocalDateTime.now());
        brxx.setSfbz(0);
        brxx.setCzy("admin");
        bgxtBrxxMapper.insert(brxx);
    }

    @Transactional
    public Map<String, Object> e2eTest() {
        Map<String, Object> resp = new HashMap<>();
        String sampleNo = "20260318" + String.format("%04d", System.currentTimeMillis() % 10000);

        BgxtBrxx brxx = new BgxtBrxx();
        brxx.setBrxxTmh("TMH" + sampleNo);
        brxx.setBrbh("BR" + sampleNo);
        brxx.setBrxm("测试患者E2E");
        brxx.setBrxb(1);
        brxx.setBrnl("30");
        brxx.setNllx("1");
        brxx.setBrlb(1);
        brxx.setSyh(sampleNo);
        brxx.setSyqk(1);
        brxx.setKsdm("001");
        brxx.setBbzl(1);
        brxx.setYbzt(0);
        brxx.setJyrq(java.time.LocalDateTime.now());
        bgxtBrxxMapper.insert(brxx);
        Integer brxxId = brxx.getBrxxId();
        resp.put("step1_sampleCreated", "brxxId=" + brxxId + ", sampleNo=" + sampleNo);

        String[] testCodes = {"50", "51", "52", "53", "54"};
        String[] testResults = {"6.5", "4.8", "140", "250", "45"};

        for (int i = 0; i < testCodes.length; i++) {
            String xmdm = testCodes[i];
            QueryWrapper<SysJyxm> wrapper = new QueryWrapper<>();
            wrapper.eq("xmdm", xmdm);
            SysJyxm xm = sysJyxmMapper.selectOne(wrapper);
            Integer xmid = xm != null ? xm.getXmid() : null;
            if (xmid != null) {
                BgxtJyjg jyjg = new BgxtJyjg();
                jyjg.setBrxxId(brxxId);
                jyjg.setXmdm(xmdm);
                jyjg.setJyjg(testResults[i]);
                jyjg.setCzri(java.time.LocalDateTime.now());
                bgxtJyjgMapper.insert(jyjg);
                resp.put("step2_resultSaved_" + xmdm, "xmid=" + xmid + ", jyjg=" + testResults[i]);
            }
        }

        Integer total = bgxtJyjgMapper.countByBrxxId(brxxId);
        resp.put("step3_checkResults", "total=" + total);

        if (total != null && total > 0) {
            bgxtBrxxMapper.updateStatusById(brxxId, 1);
            resp.put("step4_inspectSuccess", true);
            resp.put("message", "E2E测试成功! brxxId=" + brxxId);
        } else {
            resp.put("step4_inspectSuccess", false);
            resp.put("message", "E2E测试失败: 没有检验结果");
        }
        return resp;
    }

    public void dropTableIfExists() {
    }

    public void executeStatement(String sql) {
    }

    public Integer getCkzCount() {
        return Math.toIntExact(sysXmckzMapper.selectCount(null));
    }

    @Transactional
    public Map<String, Object> addResultsToSample(Integer brxxId, List<Map<String, String>> results) {
        Map<String, Object> resp = new HashMap<>();
        if (brxxId == null) {
            resp.put("success", false);
            resp.put("message", "brxxId不能为空");
            return resp;
        }

        BgxtBrxx brxx = bgxtBrxxMapper.selectById(brxxId);
        if (brxx == null) {
            resp.put("success", false);
            resp.put("message", "样本不存在: brxxId=" + brxxId);
            return resp;
        }

        int addedCount = 0;
        for (Map<String, String> result : results) {
            String xmidStr = result.get("xmid");
            String jyjg = result.get("result");
            if (xmidStr == null || jyjg == null) continue;

            Integer xmid = Integer.parseInt(xmidStr);
            try {
                int affected = bgxtJyjgMapper.insertResult(brxxId, xmid, jyjg, "", null, null, null, null, "");
                if (affected > 0) addedCount++;
            } catch (Exception e) {
                log.warn("Failed to add result for xmid={}: {}", xmid, e.getMessage());
            }
        }

        resp.put("success", true);
        resp.put("addedCount", addedCount);
        resp.put("message", "成功添加 " + addedCount + " 条检验结果");
        return resp;
    }

    public List<Map<String, Object>> findBrxxByDateRange(String startDate, String endDate) {
        return bgxtBrxxMapper.selectListByDateRange(startDate, endDate);
    }

    public int deleteJyjgByBrxxId(Integer brxxId) {
        return bgxtJyjgMapper.deleteByBrxxId(brxxId);
    }

    public int deleteBrxxById(Integer brxxId) {
        return bgxtBrxxMapper.deleteById(brxxId);
    }

    @Autowired
    private SysCjyszMxMapper sysCjyszMxMapper;

    @Autowired
    private SysCjyszZbMapper sysCjyszZbMapper;

    public int deleteCjyszMxByCjid(Integer cjid) {
        return sysCjyszMxMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.lis.entity.SysCjyszMx>().eq("cjid", cjid));
    }

    public int deleteCjyszZbByCjid(Integer cjid) {
        return sysCjyszZbMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.lis.entity.SysCjyszZb>().eq("cjid", cjid));
    }
}
