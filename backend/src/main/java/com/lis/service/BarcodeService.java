package com.lis.service;

import com.lis.mapper.BgxtBrxxMapper;
import com.lis.mapper.SysXtszMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class BarcodeService {

    @Autowired
    private BgxtBrxxMapper brxxMapper;

    @Autowired
    private SysXtszMapper xtszMapper;

    public Map<String, Object> generate() {
        Map<String, Object> result = new HashMap<>();
        try {
            String prefix = getBarcodePrefix();
            String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
            Integer seq = brxxMapper.selectMaxBarcodeSeq(prefix);
            int nextSeq = (seq != null ? seq : 0) + 1;
            String barcode = prefix + datePart + String.format("%04d", nextSeq);
            result.put("success", true);
            result.put("barcode", barcode);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成失败：" + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> printLabelSingle(Integer brxxId) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (brxxId == null) {
                result.put("success", false);
                result.put("message", "样本ID不能为空");
                return result;
            }
            Map<String, Object> label = brxxMapper.selectLabelDataById(brxxId);
            if (label == null) {
                result.put("success", false);
                result.put("message", "样本不存在");
                return result;
            }
            result.put("success", true);
            result.put("label", label);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取标签数据失败：" + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> printLabel(List<Integer> brxxIds) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (brxxIds == null || brxxIds.isEmpty()) {
                result.put("success", false);
                result.put("message", "没有选择样本");
                return result;
            }
            List<Map<String, Object>> labels = new ArrayList<>();
            Set<Integer> uniqueIds = new LinkedHashSet<>(brxxIds);
            for (Integer id : uniqueIds) {
                Map<String, Object> sample = brxxMapper.selectLabelDataById(id);
                if (sample != null) {
                    labels.add(sample);
                }
            }
            result.put("success", true);
            result.put("labels", labels);
            result.put("count", labels.size());
            result.put("message", "标签数据已生成，共 " + labels.size() + " 张");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "打印失败：" + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("prefix", getBarcodePrefix());
        config.put("dateFormat", "yyyyMMdd");
        config.put("seqLength", 4);
        return config;
    }

    private String getBarcodePrefix() {
        try {
            String prefix = xtszMapper.selectBarcodePrefix();
            return prefix != null ? prefix : "LIS";
        } catch (Exception e) {
            return "LIS";
        }
    }

    public List<Map<String, Object>> listUnprintedSamples(String brxm, String syh, String brxxTmh, String jyrq) {
        return brxxMapper.listBillingSamples(syh, brxm, brxxTmh, jyrq);
    }
}