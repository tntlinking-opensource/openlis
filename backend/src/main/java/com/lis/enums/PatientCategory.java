package com.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PatientCategory {
    OUTPATIENT(1, "门诊"),
    INPATIENT(2, "住院"),
    HEALTH_CHECK(3, "体检"),
    OTHER(4, "其他"),
    RESEARCH(5, "科研");

    private final int code;
    private final String desc;

    public static PatientCategory fromCode(int code) {
        for (PatientCategory c : values()) {
            if (c.code == code) return c;
        }
        return null;
    }

    public static String getDesc(int code) {
        PatientCategory c = fromCode(code);
        return c != null ? c.desc : "未知";
    }
}
