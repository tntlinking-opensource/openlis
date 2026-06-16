package com.lis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.BgxtKgkz;
import com.lis.entity.BgxtTsbbsz;
import com.lis.entity.BgxtXmzhMx;
import com.lis.entity.SysConfig;
import com.lis.mapper.BgxtKgkzMapper;
import com.lis.mapper.BgxtTsbbszMapper;
import com.lis.mapper.BgxtXmzhMxMapper;
import com.lis.mapper.SysConfigMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SystemSettingService {

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private BgxtKgkzMapper bgxtKgkzMapper;

    @Autowired
    private BgxtTsbbszMapper bgxtTsbbszMapper;

    @Autowired
    private BgxtXmzhMxMapper bgxtXmzhMxMapper;

    public Map<String, Object> getEngineerConfig() {
        Map<String, Object> result = new HashMap<>();
        try {
            SysConfig config = sysConfigMapper.selectConfig();
            if (config == null) {
                result.put("success", true);
                result.put("data", getDefaultConfig());
                return result;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("wYydm", config.getWYydm());
            data.put("yymc", config.getYymc());
            data.put("jykksdm", config.getJykksdm());
            data.put("hisConnectbz", config.getHisConnectbz() != null && config.getHisConnectbz() == 1);
            data.put("hisConnectLevel", config.getHisConnectlevel() != null ? config.getHisConnectlevel() : 0);
            data.put("tjConnectbz", config.getTjConnectbz() != null && config.getTjConnectbz() == 1);
            data.put("tjJghcbz", config.getTjJghcbz() != null && config.getTjJghcbz() == 1);
            data.put("yszJghcbz", config.getYszJghcbz() != null && config.getYszJghcbz() == 1);
            data.put("yszConnectbz", config.getYszConnectbz() != null && config.getYszConnectbz() == 1);
            data.put("qtxtJghcbz", config.getQtxtJghcbz() != null && config.getQtxtJghcbz() == 1);
            data.put("websc", config.getWebsc() != null && config.getWebsc() == 1);
            data.put("gdsj", config.getGdsj() != null ? config.getGdsj() : 10);
            data.put("hisConnectYbzx", config.getHisConnectYbzx() != null && config.getHisConnectYbzx() == 1);

            result.put("success", true);
            result.put("data", data);
            return result;
        } catch (Exception e) {
            result.put("success", true);
            result.put("data", getDefaultConfig());
            return result;
        }
    }

    private Map<String, Object> getDefaultConfig() {
        Map<String, Object> def = new HashMap<>();
        def.put("wYydm", "");
        def.put("yymc", "");
        def.put("hisConnectbz", false);
        def.put("hisConnectLevel", 0);
        def.put("tjConnectbz", false);
        def.put("tjJghcbz", false);
        def.put("yszJghcbz", false);
        def.put("yszConnectbz", false);
        def.put("qtxtJghcbz", false);
        def.put("websc", false);
        def.put("gdsj", 10);
        def.put("hisConnectYbzx", false);
        return def;
    }

    @Transactional
    public Map<String, Object> saveEngineerConfig(Map<String, Object> req) {
        Map<String, Object> result = new HashMap<>();

        String yymc = (String) req.get("yymc");
        Integer gdsj = req.get("gdsj") != null ? Integer.parseInt(String.valueOf(req.get("gdsj"))) : null;
        Boolean hisConnectbz = (Boolean) req.get("hisConnectbz");
        Integer hisConnectLevel = req.get("hisConnectLevel") != null ? Integer.parseInt(String.valueOf(req.get("hisConnectLevel"))) : null;

        if (yymc == null || yymc.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "医院名称不能为空!");
            return result;
        }
        if (gdsj == null) {
            result.put("success", false);
            result.put("message", "归档时间不能为空!");
            return result;
        }
        if (Boolean.TRUE.equals(hisConnectbz)) {
            if (hisConnectLevel == null || hisConnectLevel <= 0) {
                result.put("success", false);
                result.put("message", "请选择HIS连接级别!");
                return result;
            }
        }

        Integer hisLevel = Boolean.TRUE.equals(hisConnectbz)
                ? (hisConnectLevel == null ? 0 : hisConnectLevel)
                : 0;

        SysConfig config = sysConfigMapper.selectConfig();
        if (config != null) {
            config.setWYydm(nvl((String) req.get("wYydm")));
            config.setYymc(yymc.trim());
            config.setJykksdm(nvl((String) req.get("jykksdm")));
            config.setHisConnectbz(Boolean.TRUE.equals(hisConnectbz) ? 1 : 0);
            config.setHisConnectlevel(hisLevel);
            config.setTjConnectbz(Boolean.TRUE.equals(req.get("tjConnectbz")) ? 1 : 0);
            config.setTjJghcbz(Boolean.TRUE.equals(req.get("tjJghcbz")) ? 1 : 0);
            config.setYszJghcbz(Boolean.TRUE.equals(req.get("yszJghcbz")) ? 1 : 0);
            config.setYszConnectbz(Boolean.TRUE.equals(req.get("yszConnectbz")) ? 1 : 0);
            config.setQtxtJghcbz(Boolean.TRUE.equals(req.get("qtxtJghcbz")) ? 1 : 0);
            config.setWebsc(Boolean.TRUE.equals(req.get("websc")) ? 1 : 0);
            config.setGdsj(gdsj);
            config.setHisConnectYbzx(Boolean.TRUE.equals(req.get("hisConnectYbzx")) ? 1 : 0);
            sysConfigMapper.updateConfig(config);
        } else {
            config = new SysConfig();
            config.setWYydm(nvl((String) req.get("wYydm")));
            config.setYymc(yymc.trim());
            config.setJykksdm(nvl((String) req.get("jykksdm")));
            config.setHisConnectbz(Boolean.TRUE.equals(hisConnectbz) ? 1 : 0);
            config.setHisConnectlevel(hisLevel);
            config.setTjConnectbz(Boolean.TRUE.equals(req.get("tjConnectbz")) ? 1 : 0);
            config.setTjJghcbz(Boolean.TRUE.equals(req.get("tjJghcbz")) ? 1 : 0);
            config.setYszJghcbz(Boolean.TRUE.equals(req.get("yszJghcbz")) ? 1 : 0);
            config.setYszConnectbz(Boolean.TRUE.equals(req.get("yszConnectbz")) ? 1 : 0);
            config.setQtxtJghcbz(Boolean.TRUE.equals(req.get("qtxtJghcbz")) ? 1 : 0);
            config.setWebsc(Boolean.TRUE.equals(req.get("websc")) ? 1 : 0);
            config.setGdsj(gdsj);
            config.setHisConnectYbzx(Boolean.TRUE.equals(req.get("hisConnectYbzx")) ? 1 : 0);
            sysConfigMapper.insertConfig(config);
        }

        result.put("success", true);
        result.put("message", "设置成功!");
        return result;
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }

    public Map<String, Object> getSpecialReportModuleList() {
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("data", new Object[]{
            module(0, "精液分析报告"),
            module(1, "亚健康报告")
        });
        return res;
    }

    private Map<String, Object> module(int mkid, String mksm) {
        Map<String, Object> m = new HashMap<>();
        m.put("mkid", mkid);
        m.put("mksm", mksm);
        return m;
    }

    public Map<String, Object> getSpecialReportLinkedList(Integer mkid) {
        Map<String, Object> res = new HashMap<>();
        try {
            List<BgxtTsbbsz> list = bgxtTsbbszMapper.selectByMkid(mkid);
            List<Map<String, Object>> rows = new ArrayList<>();
            for (BgxtTsbbsz t : list) {
                Map<String, Object> row = new HashMap<>();
                row.put("mkid", t.getMkid());
                row.put("xmid", t.getXmid());
                row.put("mksm", t.getMksm());
                BgxtXmzhMx mx = bgxtXmzhMxMapper.selectById(t.getXmid());
                if (mx != null) {
                    row.put("xmzwmc", mx.getXmzwmc());
                    row.put("xmywmc", mx.getXmdm());
                }
                rows.add(row);
            }
            res.put("success", true);
            res.put("data", rows);
            return res;
        } catch (Exception e) {
            res.put("success", true);
            res.put("data", new ArrayList<>());
            return res;
        }
    }

    public Map<String, Object> searchSpecialReportItem(String mc) {
        Map<String, Object> res = new HashMap<>();
        try {
            QueryWrapper<BgxtXmzhMx> wrapper = new QueryWrapper<>();
            if (mc != null && !mc.trim().isEmpty()) {
                wrapper.and(w -> w.like("xmzwmc", mc.trim()).or().like("xmdm", mc.trim()));
            }
            wrapper.last("LIMIT 50");
            List<BgxtXmzhMx> list = bgxtXmzhMxMapper.selectList(wrapper);
            List<Map<String, Object>> rows = new ArrayList<>();
            for (BgxtXmzhMx mx : list) {
                Map<String, Object> row = new HashMap<>();
                row.put("xmid", mx.getId());
                row.put("xmzwmc", mx.getXmzwmc());
                row.put("xmdw", mx.getXmdw());
                row.put("qtdm", mx.getXmdm());
                rows.add(row);
            }
            res.put("success", true);
            res.put("data", rows);
            return res;
        } catch (Exception e) {
            res.put("success", true);
            res.put("data", new ArrayList<>());
            return res;
        }
    }

    @Transactional
    public Map<String, Object> linkSpecialReport(Map<String, Object> req) {
        Map<String, Object> res = new HashMap<>();
        Integer mkid = req.get("mkid") != null ? Integer.parseInt(String.valueOf(req.get("mkid"))) : null;
        Integer xmid = req.get("xmid") != null ? Integer.parseInt(String.valueOf(req.get("xmid"))) : null;
        String mksm = (String) req.get("mksm");

        if (mkid == null || xmid == null) {
            res.put("success", false);
            res.put("message", "参数不完整！");
            return res;
        }

        try {
            QueryWrapper<BgxtTsbbsz> wrapper = new QueryWrapper<>();
            wrapper.eq("mkid", mkid).eq("xmid", xmid);
            Long count = bgxtTsbbszMapper.selectCount(wrapper);

            if (count > 0) {
                res.put("success", false);
                res.put("message", "此关联已经存在，不能添加！");
                return res;
            }

            BgxtTsbbsz entity = new BgxtTsbbsz();
            entity.setMkid(mkid);
            entity.setXmid(xmid);
            entity.setMksm(mksm);
            bgxtTsbbszMapper.insert(entity);

            res.put("success", true);
            res.put("message", "添加关联成功！");
            return res;
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "添加关联失败：" + e.getMessage());
            return res;
        }
    }

    @Transactional
    public Map<String, Object> unlinkSpecialReport(Integer mkid, Integer xmid) {
        Map<String, Object> res = new HashMap<>();
        try {
            QueryWrapper<BgxtTsbbsz> wrapper = new QueryWrapper<>();
            wrapper.eq("mkid", mkid).eq("xmid", xmid);
            bgxtTsbbszMapper.delete(wrapper);
            res.put("success", true);
            res.put("message", "删除关联成功！");
            return res;
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "删除关联失败：" + e.getMessage());
            return res;
        }
    }

    public Map<String, Object> getProcessControlList() {
        List<BgxtKgkz> list = bgxtKgkzMapper.selectList(new QueryWrapper<>());
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("data", list);
        return res;
    }

    @Transactional
    public Map<String, Object> saveProcessControl(Map<String, Object> req) {
        Map<String, Object> res = new HashMap<>();
        try {
            upsert(1, req.get("sqkg"));
            upsert(2, req.get("mzsjkg"));
            upsert(3, req.get("jmjkk"));

            res.put("success", true);
            res.put("message", "设置成功！（需重启程序后生效）");
            return res;
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "设置失败：" + e.getMessage());
            return res;
        }
    }

    private void upsert(int id, Object enabled) {
        BgxtKgkz row = bgxtKgkzMapper.selectById(id);
        int kgz = Boolean.TRUE.equals(enabled) ? 1 : 0;
        if (row == null) {
            BgxtKgkz n = new BgxtKgkz();
            n.setId(id);
            n.setKgz(kgz);
            bgxtKgkzMapper.insert(n);
        } else {
            row.setKgz(kgz);
            bgxtKgkzMapper.updateById(row);
        }
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
