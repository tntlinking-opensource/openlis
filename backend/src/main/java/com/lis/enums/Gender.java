package com.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE(1, "男"),
    FEMALE(2, "女"),
    UNKNOWN(0, "未知");

    private final int code;
    private final String desc;

    public static Gender fromCode(int code) {
        for (Gender g : values()) {
            if (g.code == code) return g;
        }
        return UNKNOWN;
    }

    public static Gender fromString(String str) {
        if (str == null) return UNKNOWN;
        if ("男".equals(str) || "1".equals(str) || "M".equalsIgnoreCase(str)) return MALE;
        if ("女".equals(str) || "2".equals(str) || "F".equalsIgnoreCase(str)) return FEMALE;
        return UNKNOWN;
    }
}
