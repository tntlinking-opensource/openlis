package com.lis.controller;

import com.lis.common.R;
import com.lis.service.InstrumentReceiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/instrument", "/api/instrument"})
@Slf4j
public class InstrumentReceiveController {

    @Autowired
    private InstrumentReceiveService instrumentReceiveService;

    @PostMapping("/receive")
    public R<Map<String, Object>> receiveResult(@RequestBody InstrumentReceiveRequest req) {
        try {
            log.info("Instrument receive: sbDjid={}, sampleNo={}, resultCount={}",
                    req.getSbDjid(), req.getSampleNo(),
                    req.getResults() != null ? req.getResults().size() : 0);

            if (req.getSbDjid() == null) {
                return R.fail("仪器ID不能为空");
            }
            if (req.getSampleNo() == null || req.getSampleNo().trim().isEmpty()) {
                return R.fail("样本号不能为空");
            }
            if (req.getResults() == null || req.getResults().isEmpty()) {
                return R.fail("检验结果不能为空");
            }

            Map<String, Object> result = instrumentReceiveService.receiveResult(
                    req.getSbDjid(), req.getSampleNo(), req.getResults());

            if (Boolean.TRUE.equals(result.get("success"))) {
                return R.ok("仪器结果接收成功", result);
            } else {
                return R.fail(String.valueOf(result.get("message")));
            }
        } catch (Exception e) {
            log.error("Instrument receive error", e);
            return R.fail("接收失败：" + e.getMessage());
        }
    }

    @lombok.Data
    public static class InstrumentReceiveRequest {
        private Integer sbDjid;
        private String sampleNo;
        private Map<String, String> results;
    }
}
