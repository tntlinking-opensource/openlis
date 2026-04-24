package com.lis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.BgxtKgkz;
import com.lis.mapper.BgxtKgkzMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 6.6 流程控制设置（旧系统：系统开关控制）
 * 直接读写表 bgxt_kgkz：
 * - id=1 双签控制开关
 * - id=2 门诊收据使用控制
 * - id=3 居民健康卡使用控制
 */
@RestController
@RequestMapping("/system/process-control")
public class ProcessControlController {

    @Autowired
    private BgxtKgkzMapper bgxtKgkzMapper;

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list() {
        List<BgxtKgkz> list = bgxtKgkzMapper.selectList(new QueryWrapper<>());
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("data", list);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> save(@RequestBody SaveRequest req) {
        Map<String, Object> res = new HashMap<>();
        try {
            upsert(1, req.getSqkg());
            upsert(2, req.getMzsjkg());
            upsert(3, req.getJmjkk());

            res.put("success", true);
            res.put("message", "设置成功！（需重启程序后生效）");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "设置失败：" + e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    private void upsert(int id, Boolean enabled) {
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
    public static class SaveRequest {
        private Boolean sqkg;   // id=1
        private Boolean mzsjkg; // id=2
        private Boolean jmjkk;  // id=3
    }
}


