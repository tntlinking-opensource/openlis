package com.lis.service;

import com.lis.mapper.BgxtBrxxMapper;
import com.lis.mapper.SysYbztMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class SampleStateMachineService {

    @Autowired
    private BgxtBrxxMapper brxxMapper;

    @Autowired
    private SysYbztMapper sysYbztMapper;

    public Map<String, Object> transition(Integer brxxId, Integer newStatus, String czydm) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer currentStatus = brxxMapper.selectStatusById(brxxId);
            if (currentStatus == null) {
                result.put("success", false);
                result.put("message", "样本不存在");
                return result;
            }
            String error = validateTransition(currentStatus, newStatus);
            if (error != null) {
                result.put("success", false);
                result.put("message", error);
                return result;
            }
            switch (newStatus) {
                case 2:
                    brxxMapper.updateStatusWithAudit(brxxId, czydm);
                    break;
                case 3:
                    brxxMapper.updateStatusWithPrint(brxxId);
                    break;
                case 4:
                    brxxMapper.updateStatusWithInspect(brxxId, czydm);
                    break;
                case 5:
                    brxxMapper.updateStatusWithFirstCheck(brxxId, czydm);
                    break;
                case 6:
                    brxxMapper.updateStatusWithIntermediateCheck(brxxId, czydm);
                    break;
                default:
                    if (newStatus < 0) {
                        return handleUndoTransition(brxxId, newStatus, czydm);
                    }
                    result.put("success", false);
                    result.put("message", "不支持的状态转换: " + newStatus);
                    return result;
            }
            logStatusChange(brxxId, currentStatus, newStatus, czydm);
            result.put("success", true);
            result.put("message", "状态更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "状态更新失败: " + e.getMessage());
        }
        return result;
    }

    private Map<String, Object> handleUndoTransition(Integer brxxId, Integer newStatus, String czydm) {
        Map<String, Object> result = new HashMap<>();
        switch (newStatus) {
            case -2:
                brxxMapper.undoAudit(brxxId);
                break;
            case -4:
                brxxMapper.undoInspect(brxxId);
                break;
            case -5:
                brxxMapper.undoFirstCheck(brxxId);
                break;
            case -6:
                brxxMapper.undoIntermediateCheck(brxxId);
                break;
            default:
                result.put("success", false);
                result.put("message", "不支持的撤销操作");
                return result;
        }
        logStatusChange(brxxId, null, newStatus, czydm);
        result.put("success", true);
        result.put("message", "撤销成功");
        return result;
    }

    private String validateTransition(Integer current, Integer target) {
        switch (target) {
            case 2: if (current != 1 && current != 4 && current != 6) return "当前状态不允许审核(当前:" + current + ")"; break;
            case 3: if (current != 2) return "只有已审核样本可打印(当前:" + current + ")"; break;
            case 4: if (current != 1) return "只有未审核样本可检验(当前:" + current + ")"; break;
            case 5: if (current != 1) return "只有未审核样本可初审(当前:" + current + ")"; break;
            case 6: if (current != 5) return "只有初审样本可复审(当前:" + current + ")"; break;
            case -2: if (current != 2 && current != 3) return "只有已审核/已打印样本可撤销审核(当前:" + current + ")"; break;
            case -4: if (current != 4) return "只有已检验样本可撤销检验(当前:" + current + ")"; break;
            case -5: if (current != 5) return "只有初审样本可撤销初审(当前:" + current + ")"; break;
            case -6: if (current != 6) return "只有复审样本可撤销复审(当前:" + current + ")"; break;
        }
        return null;
    }

    private void logStatusChange(Integer brxxId, Integer oldStatus, Integer newStatus, String czydm) {
        try {
            sysYbztMapper.insertLog(brxxId, oldStatus, newStatus, czydm);
        } catch (Exception e) {
            log.debug("Status change log skipped: {}", e.getMessage());
        }
    }
}
