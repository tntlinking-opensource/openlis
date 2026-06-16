package com.lis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.BgxtBrxx;
import com.lis.entity.SysCjdzb;
import com.lis.entity.SysJyxmFull;
import com.lis.mapper.BgxtBrxxMapper;
import com.lis.mapper.BgxtJyjgMapper;
import com.lis.mapper.SysCjdzbMapper;
import com.lis.mapper.SysJyxmFullMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class InstrumentReceiveService {

    @Autowired
    private BgxtBrxxMapper bgxtBrxxMapper;

    @Autowired
    private BgxtJyjgMapper bgxtJyjgMapper;

    @Autowired
    private SysCjdzbMapper sysCjdzbMapper;

    @Autowired
    private SysJyxmFullMapper sysJyxmFullMapper;

    @Transactional
    public Map<String, Object> receiveResult(Integer sbDjid, String sampleNo, Map<String, String> instrumentResults) {
        Map<String, Object> resp = new HashMap<>();
        int saved = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        if (sbDjid == null || sampleNo == null || sampleNo.trim().isEmpty()
                || instrumentResults == null || instrumentResults.isEmpty()) {
            resp.put("success", false);
            resp.put("message", "参数不完整：需要仪器ID、样本号和检验结果");
            return resp;
        }

        QueryWrapper<BgxtBrxx> brxxWrapper = new QueryWrapper<>();
        brxxWrapper.eq("syh", sampleNo.trim())
                .eq("sb_djid", sbDjid)
                .in("ybzt", Arrays.asList(0, 1, 4))
                .last("LIMIT 1");
        BgxtBrxx sample = bgxtBrxxMapper.selectOne(brxxWrapper);
        if (sample == null) {
            resp.put("success", false);
            resp.put("message", "未找到匹配的样本：样本号=" + sampleNo + "，仪器ID=" + sbDjid);
            return resp;
        }
        Integer brxxId = sample.getBrxxId();

        QueryWrapper<SysCjdzb> cjdzbWrapper = new QueryWrapper<>();
        cjdzbWrapper.eq("sb_djid", sbDjid);
        List<SysCjdzb> mappings = sysCjdzbMapper.selectList(cjdzbWrapper);
        Map<String, SysCjdzb> codeToMapping = new HashMap<>();
        for (SysCjdzb m : mappings) {
            if (m.getXmdm() != null) {
                codeToMapping.put(m.getXmdm().trim().toUpperCase(), m);
            }
        }

        for (Map.Entry<String, String> entry : instrumentResults.entrySet()) {
            String instrumentCode = entry.getKey().trim().toUpperCase();
            String rawResult = entry.getValue();

            try {
                Integer xmid = null;
                BigDecimal xs = BigDecimal.ONE;
                int xmjd = 3;

                SysCjdzb mapping = codeToMapping.get(instrumentCode);
                if (mapping != null) {
                    xmid = mapping.getXmid();
                    if (mapping.getXs() != null) {
                        xs = mapping.getXs();
                    }
                }

                if (xmid == null) {
                    QueryWrapper<SysJyxmFull> jyxmWrapper = new QueryWrapper<>();
                    jyxmWrapper.eq("xmdm", instrumentCode).last("LIMIT 1");
                    SysJyxmFull item = sysJyxmFullMapper.selectOne(jyxmWrapper);
                    if (item != null) {
                        xmid = item.getXmid();
                        if (item.getXmjd() != null) {
                            xmjd = item.getXmjd();
                        }
                    }
                }

                if (xmid == null) {
                    skipped++;
                    errors.add("仪器代码[" + instrumentCode + "]未找到对应的LIS项目映射");
                    log.warn("Instrument code [{}] not mapped for instrument [{}]", instrumentCode, sbDjid);
                    continue;
                }

                String convertedResult = convertResult(rawResult, xs, xmjd);

                Integer existing = bgxtJyjgMapper.countByBrxxIdAndXmid(brxxId, xmid);
                if (existing != null && existing > 0) {
                    bgxtJyjgMapper.updateResultByBrxxIdAndXmid(brxxId, xmid, convertedResult);
                } else {
                    bgxtJyjgMapper.insertResultSimple(brxxId, xmid, convertedResult);
                }
                saved++;
                log.info("Auto-receive: brxxId={}, xmid={}, code={}, result={}", brxxId, xmid, instrumentCode, convertedResult);

            } catch (Exception e) {
                skipped++;
                errors.add("处理[" + instrumentCode + "]失败: " + e.getMessage());
                log.error("Error processing instrument result: code={}, instrument={}", instrumentCode, sbDjid, e);
            }
        }

        Integer currentStatus = sample.getYbzt();
        if (currentStatus == null || currentStatus < 4) {
            bgxtBrxxMapper.updateStatusWithInspect(brxxId, "AUTO");
        }

        resp.put("success", saved > 0);
        resp.put("message", "接收完成：保存 " + saved + " 条，跳过 " + skipped + " 条");
        resp.put("saved", saved);
        resp.put("skipped", skipped);
        resp.put("brxxId", brxxId);
        if (!errors.isEmpty()) {
            resp.put("errors", errors);
        }
        return resp;
    }

    private String convertResult(String raw, BigDecimal xs, int xmjd) {
        if (raw == null || raw.trim().isEmpty()) {
            return raw;
        }
        try {
            BigDecimal val = new BigDecimal(raw.trim());
            val = val.multiply(xs);
            int scale = Math.max(0, xmjd - 1);
            return val.setScale(scale, BigDecimal.ROUND_HALF_UP).toPlainString();
        } catch (Exception e) {
            return raw;
        }
    }
}
