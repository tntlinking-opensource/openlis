package com.lis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.SysXmckz;
import com.lis.mapper.SysXmckzMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class ReferenceRangeService {

    @Autowired
    private SysXmckzMapper sysXmckzMapper;

    public Map<String, Object> calculate(Integer xmid, Integer sbDjid, Integer bbzl, Integer brxb, Integer nllx, BigDecimal brnl) {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<SysXmckz> wrapper = new QueryWrapper<>();
        wrapper.eq("xmid", xmid);
        wrapper.and(w -> w.isNull("sb_djid").or().eq("sb_djid", 0).or().eq("sb_djid", sbDjid));
        List<SysXmckz> ranges = sysXmckzMapper.selectList(wrapper);

        SysXmckz best = null;
        int bestPriority = -1;
        for (SysXmckz range : ranges) {
            int priority = calcPriority(range, bbzl, brxb, nllx, brnl);
            if (priority > bestPriority) {
                bestPriority = priority;
                best = range;
            }
        }
        if (best != null) {
            result.put("ckz", best.getCkz());
            result.put("ckzgx", best.getCkzgx());
            result.put("ckzdx", best.getCkzdx());
            result.put("bjzgx", best.getBjzgx());
            result.put("bjzdx", best.getBjzdx());
            result.put("jszgx", best.getJszgx());
            result.put("jszdx", best.getJszdx());
            result.put("fczgx", best.getFczgx());
            result.put("fczdx", best.getFczdx());
        }
        return result;
    }

    private int calcPriority(SysXmckz range, Integer bbzl, Integer brxb, Integer nllx, BigDecimal brnl) {
        int p = 0;
        boolean bbMatch = matchBbzl(range, bbzl);
        boolean xbMatch = matchBrxb(range, brxb);
        boolean nlMatch = matchNl(range, nllx, brnl);
        if (bbMatch) p += 100;
        if (xbMatch) p += 10;
        if (nlMatch) p += 1;
        if (!bbMatch && isFlagSet(range.getBbsgbz())) return -1;
        if (!xbMatch && isFlagSet(range.getXbsgbz())) return -1;
        if (!nlMatch && isFlagSet(range.getNlsgbz())) return -1;
        return p;
    }

    private boolean matchBbzl(SysXmckz range, Integer bbzl) {
        if (!isFlagSet(range.getBbsgbz())) return true;
        return range.getBbzl() != null && bbzl != null && range.getBbzl().equals(bbzl);
    }

    private boolean matchBrxb(SysXmckz range, Integer brxb) {
        if (!isFlagSet(range.getXbsgbz())) return true;
        return range.getBrxb() != null && brxb != null && range.getBrxb().equals(brxb);
    }

    private boolean matchNl(SysXmckz range, Integer nllx, BigDecimal brnl) {
        if (!isFlagSet(range.getNlsgbz())) return true;
        if (nllx == null || brnl == null) return false;
        if (range.getNllx() == null || !range.getNllx().equals(nllx)) return false;
        try {
            BigDecimal nlxx = range.getNlxx();
            BigDecimal nlsx = range.getNlsx();
            if (nlxx != null && brnl.compareTo(nlxx) < 0) return false;
            if (nlsx != null && brnl.compareTo(nlsx) > 0) return false;
            return true;
        } catch (Exception e) { return false; }
    }

    private boolean isFlagSet(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        if (val instanceof Number) return ((Number) val).intValue() == 1;
        return "1".equals(String.valueOf(val));
    }
}
