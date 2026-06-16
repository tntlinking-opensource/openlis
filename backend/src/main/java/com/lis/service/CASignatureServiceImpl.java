package com.lis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
@Slf4j
public class CASignatureServiceImpl implements CASignatureService {

    @Override
    public String sign(String userId, String content) {
        log.info("CA签名请求: userId={}, contentLength={}", userId,
                content != null ? content.length() : 0);
        log.debug("CA签名内容: userId={}, content={}", userId, content);

        String raw = "STUB-" + userId + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
        String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));

        log.info("CA签名完成(桩): userId={}, signature={}", userId, signature);
        return signature;
    }

    @Override
    public boolean verify(String userId, String content, String signature) {
        log.info("CA验签请求: userId={}, signature={}", userId, signature);

        if (userId == null || signature == null || signature.isEmpty()) {
            log.warn("CA验签失败: 参数不完整");
            return false;
        }

        try {
            String decoded = new String(Base64.getUrlDecoder().decode(signature), StandardCharsets.UTF_8);
            boolean valid = decoded.startsWith("STUB-" + userId + "-");
            log.info("CA验签结果(桩): userId={}, valid={}", userId, valid);
            return valid;
        } catch (Exception e) {
            log.warn("CA验签异常: userId={}, error={}", userId, e.getMessage());
            return false;
        }
    }
}
