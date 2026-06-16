package com.lis.service;

import com.lis.entity.BgxtBrxx;
import com.lis.mapper.BgxtBrxxMapper;
import com.lis.mapper.BgxtHisXmMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class BillingService {

    @Autowired
    private BgxtBrxxMapper brxxMapper;

    @Autowired
    private BgxtHisXmMapper hisXmMapper;

    public List<Map<String, Object>> listBillingSamples(String syh, String brxm, String brxxTmh, String jyrq) {
        return brxxMapper.listBillingSamples(syh, brxm, brxxTmh, jyrq);
    }

    public List<Map<String, Object>> getBillingDetails(Integer brxxId) {
        return hisXmMapper.selectByBrxxId(brxxId);
    }

    public Map<String, Object> getBillingStatus(Integer sampleId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> fees = hisXmMapper.selectByBrxxId(sampleId);
            result.put("fees", fees);
            BgxtBrxx brxx = brxxMapper.selectById(sampleId);
            result.put("sfbz", brxx != null ? brxx.getSfbz() : null);
        } catch (Exception e) {
            result.put("fees", new ArrayList<>());
        }
        return result;
    }

    @Transactional
    public Map<String, Object> confirmBilling(Integer sampleId, String czydm) {
        Map<String, Object> result = new HashMap<>();
        try {
            brxxMapper.updateBillingStatus(sampleId, 1);
            hisXmMapper.confirmBilling(sampleId, czydm);
            result.put("success", true);
            result.put("message", "计费确认成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "计费确认失败：" + e.getMessage());
        }
        return result;
    }

    @Transactional
    public Map<String, Object> cancelBilling(Integer sampleId, String czydm) {
        Map<String, Object> result = new HashMap<>();
        try {
            brxxMapper.updateBillingStatus(sampleId, 0);
            hisXmMapper.cancelBilling(sampleId, czydm);
            result.put("success", true);
            result.put("message", "取消计费成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "取消计费失败：" + e.getMessage());
        }
        return result;
    }

    @Transactional
    public Map<String, Object> batchConfirmBilling(List<Integer> brxxIds, String czydm) {
        Map<String, Object> result = new HashMap<>();
        int count = 0;
        try {
            for (Integer brxxId : brxxIds) {
                brxxMapper.updateBillingStatus(brxxId, 1);
                hisXmMapper.confirmBilling(brxxId, czydm);
                count++;
            }
            result.put("success", true);
            result.put("count", count);
            result.put("message", "批量计费确认成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量计费确认失败：" + e.getMessage());
        }
        return result;
    }

    @Transactional
    public Map<String, Object> batchCancelBilling(List<Integer> brxxIds, String czydm) {
        Map<String, Object> result = new HashMap<>();
        int count = 0;
        try {
            for (Integer brxxId : brxxIds) {
                brxxMapper.updateBillingStatus(brxxId, 0);
                hisXmMapper.cancelBilling(brxxId, czydm);
                count++;
            }
            result.put("success", true);
            result.put("count", count);
            result.put("message", "批量取消计费成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量取消计费失败：" + e.getMessage());
        }
        return result;
    }

    @Transactional
    public Map<String, Object> batchInvalidateBilling(List<Integer> brxxIds, String czydm) {
        Map<String, Object> result = new HashMap<>();
        int count = 0;
        try {
            for (Integer brxxId : brxxIds) {
                brxxMapper.updateBillingStatus(brxxId, 0);
                hisXmMapper.invalidateBilling(brxxId, czydm);
                count++;
            }
            result.put("success", true);
            result.put("count", count);
            result.put("message", "批量作废成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量作废失败：" + e.getMessage());
        }
        return result;
    }
}