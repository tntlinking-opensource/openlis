package com.lis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HISNotificationServiceImpl implements HISNotificationService {

    @Override
    public void notifySampleStatus(Integer brxxId, int newStatus) {
        log.info("[HIS通知-样本状态] brxxId={}, newStatus={}", brxxId, newStatus);
    }

    @Override
    public void notifyCriticalValue(Integer brxxId, List<Map<String, Object>> criticalItems) {
        log.info("[HIS通知-危急值] brxxId={}, itemCount={}",
                brxxId, criticalItems != null ? criticalItems.size() : 0);
    }
}
