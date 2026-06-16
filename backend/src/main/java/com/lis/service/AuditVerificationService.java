package com.lis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.BgxtBrxx;
import com.lis.entity.SysXtsz;
import com.lis.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuditVerificationService {

    @Autowired
    private BgxtBrxxMapper bgxtBrxxMapper;

    @Autowired
    private BgxtJyjgMapper bgxtJyjgMapper;

    @Autowired
    private SysXtszMapper sysXtszMapper;

    public List<Map<String, Object>> verify(Integer brxxId, String czydm) {
        List<Map<String, Object>> warnings = new ArrayList<>();
        try {
            BgxtBrxx sample = bgxtBrxxMapper.selectById(brxxId);
            if (sample == null) {
                addError(warnings, 1, "样本不存在");
                return warnings;
            }

            // Step 1: Status re-verification
            Integer ybzt = sample.getYbzt();
            if (ybzt == null) ybzt = 0;
            if (ybzt != 1 && ybzt != 4) {
                addError(warnings, 1, "样本状态不是未审核/已检验，当前状态: " + ybzt);
                return warnings;
            }
            Integer recheckedStatus = bgxtBrxxMapper.selectStatusById(brxxId);
            if (recheckedStatus != null && !recheckedStatus.equals(ybzt)) {
                addError(warnings, 1, "样本状态已被其他操作改变(原:" + ybzt + " 现:" + recheckedStatus + ")");
                return warnings;
            }

            // Step 2: Error sample blocking (yczt)
            Integer yczt = sample.getYczt();
            if (yczt == null) yczt = 0;
            if (yczt > 0) {
                addError(warnings, 2, "样本存在错误处理标记(yczt=" + yczt + ")，不允许审核");
                return warnings;
            }

            // Step 3: Password check
            Boolean shmmyz = getSysParam("pub_shmmyz");
            if (shmmyz != null && shmmyz) {
                Map<String, Object> pwFlag = new HashMap<>();
                pwFlag.put("step", 3);
                pwFlag.put("level", "info");
                pwFlag.put("message", "需要审核密码验证");
                pwFlag.put("requirePassword", true);
                warnings.add(pwFlag);
            }

            // Step 4: Empty result check (at least one non-empty result required)
            Integer resultCount = bgxtJyjgMapper.countValidResults(brxxId);
            if (resultCount == null || resultCount == 0) {
                addError(warnings, 4, "样本没有检验结果，不能审核");
                return warnings;
            }

            // Step 5: Empty reference range warning
            Boolean ckzjykg = getSysParam("pub_ckzjykg");
            if (ckzjykg != null && ckzjykg) {
                List<Map<String, Object>> emptyCkz = bgxtJyjgMapper.findEmptyCkz(brxxId);
                if (!emptyCkz.isEmpty()) {
                    addWarning(warnings, 5, "以下项目参考值为空: " +
                        String.join(",", emptyCkz.stream().map(r -> String.valueOf(r.get("xmzwmc"))).toArray(String[]::new)));
                }
            }

            // Step 6: Empty result warning
            Boolean kztsbz = getSysParam("pub_kztsbz");
            if (kztsbz != null && kztsbz) {
                List<Map<String, Object>> emptyResults = bgxtJyjgMapper.findEmptyResults(brxxId);
                if (!emptyResults.isEmpty()) {
                    addWarning(warnings, 6, "以下项目结果为空: " +
                        String.join(",", emptyResults.stream().map(r -> String.valueOf(r.get("xmzwmc"))).toArray(String[]::new)));
                }
            }

            // Step 7: Negative value warning
            Boolean fsztsbz = getSysParam("pub_fsztsbz");
            if (fsztsbz != null && fsztsbz) {
                List<Map<String, Object>> negResults = bgxtJyjgMapper.findNegativeResults(brxxId);
                if (!negResults.isEmpty()) {
                    addWarning(warnings, 7, "存在负值结果: " +
                        String.join(",", negResults.stream().map(r -> r.get("xmzwmc") + "=" + r.get("jyjg")).toArray(String[]::new)));
                }
            }

            // Step 8: Zero value warning
            Boolean zerotsbz = getSysParam("pub_zerotsbz");
            if (zerotsbz != null && zerotsbz) {
                List<Map<String, Object>> zeroResults = bgxtJyjgMapper.findZeroResults(brxxId);
                if (!zeroResults.isEmpty()) {
                    addWarning(warnings, 8, "存在零值结果");
                }
            }

            // Step 9: Critical value notification
            Boolean wjzfsts = getSysParam("pub_wjzfsts");
            if (wjzfsts != null && wjzfsts) {
                List<Map<String, Object>> criticals = bgxtJyjgMapper.findCriticalResults(brxxId);
                if (!criticals.isEmpty()) {
                    addWarning(warnings, 9, "存在危急值结果(" + criticals.size() + "项)，请确认是否发送危急值通知");
                }
            }

            // Step 10: Special result verification
            Boolean specialVerify = getSysParam("pub_pecialresultverification");
            if (specialVerify != null && specialVerify) {
                try {
                    List<Map<String, Object>> specialResults = bgxtJyjgMapper.findSpecialResults(brxxId);
                    if (specialResults != null && !specialResults.isEmpty()) {
                        addWarning(warnings, 10, "存在特殊结果需验证: " +
                            specialResults.stream().map(r -> r.get("xmzwmc") + "=" + r.get("jyjg"))
                            .collect(Collectors.joining(", ")));
                    }
                } catch (Exception e) {
                    log.debug("Step 10 special verification skipped: {}", e.getMessage());
                }
            }

            // Step 11: Historical comparison
            try {
                List<Map<String, Object>> history = bgxtJyjgMapper.findHistoricalResults(brxxId);
                if (history != null && !history.isEmpty()) {
                    addWarning(warnings, 11, "存在历史结果可供对比，共 " + history.size() + " 项");
                }
            } catch (Exception e) {
                log.debug("Step 11 historical comparison skipped: {}", e.getMessage());
            }

        } catch (Exception e) {
            addError(warnings, 0, "验证过程异常: " + e.getMessage());
        }
        return warnings;
    }

    public boolean hasErrors(List<Map<String, Object>> warnings) {
        return warnings.stream().anyMatch(w -> "error".equals(w.get("level")));
    }

    private void addError(List<Map<String, Object>> list, int step, String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("step", step);
        m.put("level", "error");
        m.put("message", msg);
        list.add(m);
    }

    private void addWarning(List<Map<String, Object>> list, int step, String msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("step", step);
        m.put("level", "warning");
        m.put("message", msg);
        list.add(m);
    }

    private Boolean getSysParam(String key) {
        try {
            QueryWrapper<SysXtsz> wrapper = new QueryWrapper<>();
            wrapper.eq("xtsz_key", key);
            SysXtsz setting = sysXtszMapper.selectOne(wrapper);
            if (setting == null) return false;
            String val = setting.getXtszValue();
            if (val == null) {
                return setting.getLwbz() != null && setting.getLwbz();
            }
            return "1".equals(val) || "true".equalsIgnoreCase(val) || "yes".equalsIgnoreCase(val);
        } catch (Exception e) {
            return false;
        }
    }
}
