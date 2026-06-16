package com.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgeType {
    YEAR(1, "岁"),
    MONTH(2, "月"),
    DAY(3, "天"),
    HOUR(4, "时");

    private final int code;
    private final String desc;

    public static AgeType fromCode(int code) {
        for (AgeType a : values()) {
            if (a.code == code) return a;
        }
        return YEAR;
    }

    public static AgeType fromString(String str) {
        if (str == null) return YEAR;
        for (AgeType a : values()) {
            if (a.desc.equals(str)) return a;
        }
        return YEAR;
    }
}
