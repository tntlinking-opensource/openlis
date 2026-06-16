package com.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuditAction {
    CANCEL_AUDIT(-2, "取消审核"),
    CANCEL_TEST(-4, "取消检验"),
    CANCEL_PRINT(-6, "取消打印");

    private final int code;
    private final String desc;

    public static AuditAction fromCode(int code) {
        for (AuditAction a : values()) {
            if (a.code == code) return a;
        }
        return null;
    }
}
