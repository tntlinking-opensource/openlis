package com.lis.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class WestgardRuleService {

    @Data
    @AllArgsConstructor
    public static class WestgardViolation {
        private String ruleName;
        private String severity;
        private String message;

        public boolean isError() {
            return "ERROR".equals(severity);
        }
    }

    public List<WestgardViolation> evaluate(List<Double> recentResults, double mean, double sd) {
        if (recentResults == null || recentResults.isEmpty() || sd <= 0) {
            return Collections.emptyList();
        }

        List<WestgardViolation> violations = new ArrayList<>();
        List<Double> ordered = new ArrayList<>(recentResults);
        Collections.reverse(ordered);

        double currentValue = ordered.get(0);

        violations.addAll(check1_3s(currentValue, mean, sd));
        violations.addAll(check2_2s(ordered, mean, sd));
        violations.addAll(checkR_4s(ordered, mean, sd));
        violations.addAll(check1_2s(currentValue, mean, sd));
        violations.addAll(check4_1s(ordered, mean, sd));
        violations.addAll(check10x(ordered, mean));

        return violations;
    }

    private List<WestgardViolation> check1_3s(double value, double mean, double sd) {
        List<WestgardViolation> violations = new ArrayList<>();
        double diff = Math.abs(value - mean);
        if (diff > 3 * sd) {
            String direction = value > mean ? "+" : "-";
            violations.add(new WestgardViolation(
                    "1-3s",
                    "ERROR",
                    String.format("1-3s规则违反: 结果 %.2f 偏离均值 %s 超过3SD (偏差=%.2f, 3SD=%.2f)",
                            value, direction, diff, 3 * sd)
            ));
        }
        return violations;
    }

    private List<WestgardViolation> check2_2s(List<Double> ordered, double mean, double sd) {
        List<WestgardViolation> violations = new ArrayList<>();
        if (ordered.size() < 2) return violations;

        double current = ordered.get(0);
        double previous = ordered.get(1);

        boolean currentAbove = current > mean + 2 * sd;
        boolean currentBelow = current < mean - 2 * sd;
        boolean previousAbove = previous > mean + 2 * sd;
        boolean previousBelow = previous < mean - 2 * sd;

        if ((currentAbove && previousAbove) || (currentBelow && previousBelow)) {
            String direction = currentAbove ? "正" : "负";
            violations.add(new WestgardViolation(
                    "2-2s",
                    "ERROR",
                    String.format("2-2s规则违反: 连续2个结果在均值%s方向超过2SD (%.2f, %.2f)",
                            direction, current, previous)
            ));
        }
        return violations;
    }

    private List<WestgardViolation> checkR_4s(List<Double> ordered, double mean, double sd) {
        List<WestgardViolation> violations = new ArrayList<>();
        if (ordered.size() < 2) return violations;

        double current = ordered.get(0);
        double previous = ordered.get(1);

        double range = Math.abs(current - previous);
        if (range > 4 * sd) {
            violations.add(new WestgardViolation(
                    "R-4s",
                    "ERROR",
                    String.format("R-4s规则违反: 连续2个结果极差 %.2f 超过4SD (4SD=%.2f)",
                            range, 4 * sd)
            ));
        }
        return violations;
    }

    private List<WestgardViolation> check1_2s(double value, double mean, double sd) {
        List<WestgardViolation> violations = new ArrayList<>();
        double diff = Math.abs(value - mean);
        if (diff > 2 * sd) {
            String direction = value > mean ? "+" : "-";
            violations.add(new WestgardViolation(
                    "1-2s",
                    "WARNING",
                    String.format("1-2s规则警告: 结果 %.2f 偏离均值 %s 超过2SD (偏差=%.2f, 2SD=%.2f)",
                            value, direction, diff, 2 * sd)
            ));
        }
        return violations;
    }

    private List<WestgardViolation> check4_1s(List<Double> ordered, double mean, double sd) {
        List<WestgardViolation> violations = new ArrayList<>();
        if (ordered.size() < 4) return violations;

        boolean allAbove = true;
        boolean allBelow = true;
        for (int i = 0; i < 4; i++) {
            double val = ordered.get(i);
            if (val <= mean + sd) allAbove = false;
            if (val >= mean - sd) allBelow = false;
        }

        if (allAbove || allBelow) {
            String direction = allAbove ? "正" : "负";
            violations.add(new WestgardViolation(
                    "4-1s",
                    "WARNING",
                    String.format("4-1s规则警告: 连续4个结果在均值%s方向超过1SD",
                            direction)
            ));
        }
        return violations;
    }

    private List<WestgardViolation> check10x(List<Double> ordered, double mean) {
        List<WestgardViolation> violations = new ArrayList<>();
        if (ordered.size() < 10) return violations;

        boolean allAbove = true;
        boolean allBelow = true;
        for (int i = 0; i < 10; i++) {
            double val = ordered.get(i);
            if (val <= mean) allAbove = false;
            if (val >= mean) allBelow = false;
        }

        if (allAbove || allBelow) {
            String direction = allAbove ? "上方" : "下方";
            violations.add(new WestgardViolation(
                    "10x",
                    "WARNING",
                    String.format("10x规则警告: 连续10个结果均在均值%s", direction)
            ));
        }
        return violations;
    }
}
