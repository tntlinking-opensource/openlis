package com.lis.service;

public interface CASignatureService {

    String sign(String userId, String content);

    boolean verify(String userId, String content, String signature);
}
