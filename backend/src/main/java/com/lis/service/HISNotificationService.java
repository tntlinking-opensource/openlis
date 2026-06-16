package com.lis.service;

import java.util.List;
import java.util.Map;

public interface HISNotificationService {

    void notifySampleStatus(Integer brxxId, int newStatus);

    void notifyCriticalValue(Integer brxxId, List<Map<String, Object>> criticalItems);
}
