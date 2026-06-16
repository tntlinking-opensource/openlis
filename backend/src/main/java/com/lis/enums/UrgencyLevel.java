package com.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UrgencyLevel {
    NORMAL(0, "普通"),
    URGENT(1, "紧急"),
    CRITICAL(2, "危急");

    private final int code;
    private final String desc;

    public static UrgencyLevel fromCode(int code) {
        for (UrgencyLevel u : values()) {
            if (u.code == code) return u;
        }
        return null;
    }
}
