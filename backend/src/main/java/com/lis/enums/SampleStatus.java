package com.lis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SampleStatus {
    REGISTERED(0, "登记"),
    NOT_AUDITED(1, "未审核"),
    AUDITED(2, "已审核"),
    PRINTED(3, "已打印"),
    TESTED(4, "已检验"),
    FIRST_CHECK(5, "初审"),
    INTERMEDIATE_CHECK(6, "复审"),
    VOIDED(-1, "已作废");

    private final int code;
    private final String desc;

    public static SampleStatus fromCode(int code) {
        for (SampleStatus s : values()) {
            if (s.code == code) return s;
        }
        return null;
    }

    public static String getDesc(int code) {
        SampleStatus s = fromCode(code);
        return s != null ? s.desc : "未知";
    }

    public static String getDesc(Integer code) {
        return code != null ? getDesc(code.intValue()) : "未知";
    }
}
