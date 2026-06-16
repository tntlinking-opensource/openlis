package com.lis.service;

import com.lis.dto.QueryRequest;
import com.lis.mapper.BgxtBrxxMapper;
import com.lis.mapper.BgxtJyjgMapper;
import com.lis.mapper.BgxtTsxmtatMapper;
import com.lis.mapper.BgxtXmzhZbMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class StatisticsService {

    @Autowired
    private BgxtBrxxMapper bgxtBrxxMapper;

    @Autowired
    private BgxtJyjgMapper bgxtJyjgMapper;

    @Autowired
    private BgxtTsxmtatMapper bgxtTsxmtatMapper;

    @Autowired
    private BgxtXmzhZbMapper bgxtXmzhZbMapper;

    public Map<String, Object> comprehensive(QueryRequest params) {
        Map<String, Object> result = new HashMap<>();
        String beginDate = params.getBeginDate();
        String endDate = params.getEndDate();
        if (beginDate == null || beginDate.trim().isEmpty()) beginDate = "2020-01-01";
        if (endDate == null || endDate.trim().isEmpty()) endDate = "2099-12-31";
        result.put("byStatus", bgxtBrxxMapper.selectStatsByStatusWithDate(beginDate, endDate));
        result.put("byDepartment", bgxtBrxxMapper.selectStatsByDepartment(beginDate, endDate));
        result.put("byPatientType", bgxtBrxxMapper.selectStatsByPatientTypeWithDate(beginDate, endDate));
        result.put("total", bgxtBrxxMapper.selectTotalStats(beginDate, endDate));
        return result;
    }

    private String safeDate(String d, String fallback) {
        return (d == null || d.trim().isEmpty()) ? fallback : d;
    }

    public List<Map<String, Object>> byDepartment(String beginDate, String endDate) {
        return bgxtBrxxMapper.selectStatsByDepartment(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"));
    }

    public List<Map<String, Object>> byDoctor(String beginDate, String endDate) {
        return bgxtBrxxMapper.selectStatsByDoctor(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"));
    }

    public List<Map<String, Object>> byItem(String beginDate, String endDate) {
        return bgxtJyjgMapper.selectStatsByItem(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"));
    }

    public List<Map<String, Object>> byStatus(String beginDate, String endDate) {
        return bgxtBrxxMapper.selectStatsByStatusWithDate(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"));
    }

    public Map<String, Object> workloadByItem(QueryRequest params) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", bgxtJyjgMapper.selectWorkloadByItem(
            safeDate(params.getBeginDate(),"2020-01-01"), safeDate(params.getEndDate(),"2099-12-31"), params.getBrlb()));
        return result;
    }

    public List<Map<String, Object>> workloadByItemV2(String beginDate, String endDate) {
        return bgxtJyjgMapper.selectWorkloadByItemV2(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"));
    }

    public List<Map<String, Object>> workloadItemDetailV2(String beginDate, String endDate, Integer zhid) {
        return bgxtJyjgMapper.selectWorkloadItemDetailV2(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"), zhid);
    }

    public List<Map<String, Object>> workloadByDept(String beginDate, String endDate) {
        return bgxtJyjgMapper.selectWorkloadByDept(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"));
    }

    public List<Map<String, Object>> workloadDeptDetail(String beginDate, String endDate, String ksmc) {
        return bgxtJyjgMapper.selectWorkloadDeptDetail(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"), ksmc);
    }

    public List<Map<String, Object>> workloadByDoctor(String beginDate, String endDate) {
        return bgxtJyjgMapper.selectWorkloadByDoctor(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"));
    }

    public List<Map<String, Object>> workloadDoctorDetail(String beginDate, String endDate, String sjys) {
        return bgxtJyjgMapper.selectWorkloadDoctorDetail(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"), sjys);
    }

    public List<Map<String, Object>> workloadByExaminer(String beginDate, String endDate) {
        return bgxtJyjgMapper.selectWorkloadByExaminer(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"));
    }

    public List<Map<String, Object>> workloadExaminerDetail(String beginDate, String endDate, String jyys) {
        return bgxtJyjgMapper.selectWorkloadExaminerDetail(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"), jyys);
    }

    public List<Map<String, Object>> workloadDetail(String beginDate, String endDate, Integer xmid) {
        return bgxtJyjgMapper.selectWorkloadDetail(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"), xmid);
    }

    public Map<String, Object> customReportQuery(QueryRequest params) {
        Map<String, Object> result = new HashMap<>();
        String dimension = params.getDimension() != null ? params.getDimension() : "department";
        String bd = safeDate(params.getBeginDate(),"2020-01-01");
        String ed = safeDate(params.getEndDate(),"2099-12-31");
        List<Map<String, Object>> data;
        switch (dimension) {
            case "department":
                data = bgxtBrxxMapper.selectCustomReportByDepartment(bd, ed);
                break;
            case "doctor":
                data = bgxtBrxxMapper.selectCustomReportByDoctor(bd, ed);
                break;
            case "item":
                data = bgxtJyjgMapper.selectCustomReportByItem(bd, ed);
                break;
            case "status":
                data = bgxtBrxxMapper.selectCustomReportByStatus(bd, ed);
                break;
            case "daily":
                data = bgxtBrxxMapper.selectCustomReportByDaily(bd, ed);
                break;
            case "patientType":
                data = bgxtBrxxMapper.selectCustomReportByPatientType(bd, ed);
                break;
            case "urgency":
                data = bgxtBrxxMapper.selectCustomReportByUrgency(bd, ed);
                break;
            case "instrument":
                data = bgxtBrxxMapper.selectCustomReportByInstrument(bd, ed);
                break;
            case "combo":
                data = bgxtBrxxMapper.selectCustomReportByCombo(bd, ed);
                break;
            case "examiner":
                data = bgxtBrxxMapper.selectCustomReportByExaminer(bd, ed);
                break;
            default:
                data = bgxtBrxxMapper.selectCustomReportByDepartment(bd, ed);
        }
        result.put("data", data);
        result.put("success", true);
        return result;
    }

    public List<Map<String, Object>> customReportDetail(String beginDate, String endDate, String dimension, String filter) {
        String bd = safeDate(beginDate, "2020-01-01");
        String ed = safeDate(endDate, "2099-12-31");
        switch (dimension) {
            case "department": return bgxtBrxxMapper.selectDetailByDepartment(bd, ed, filter);
            case "doctor": return bgxtBrxxMapper.selectDetailByDoctor(bd, ed, filter);
            case "examiner": return bgxtBrxxMapper.selectDetailByExaminer(bd, ed, filter);
            case "combo": return bgxtBrxxMapper.selectDetailByCombo(bd, ed, filter);
            default: return bgxtBrxxMapper.selectDetailByDepartment(bd, ed, filter);
        }
    }

    public List<Map<String, Object>> patientTypeWithFee(String beginDate, String endDate) {
        return bgxtBrxxMapper.selectPatientTypeWithFee(safeDate(beginDate, "2020-01-01"), safeDate(endDate, "2099-12-31"));
    }

    public Map<String, Object> tatStatistics(String beginDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        String bd = safeDate(beginDate, "2020-01-01");
        String ed = safeDate(endDate, "2099-12-31");
        result.put("avgTat", bgxtTsxmtatMapper.selectAvgTat(bd, ed));
        result.put("byUrgency", bgxtTsxmtatMapper.selectTatByUrgency(bd, ed));
        result.put("percentiles", bgxtTsxmtatMapper.selectTatPercentiles(bd, ed));
        return result;
    }

    public Map<String, Object> tatOvertime(String beginDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", bgxtTsxmtatMapper.selectTatOvertime(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31")));
        return result;
    }

    public List<Map<String, Object>> tatTrend(String beginDate, String endDate) {
        return bgxtTsxmtatMapper.selectTatTrend(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"));
    }

    public List<Map<String, Object>> tatTrendWithTarget(String beginDate, String endDate) {
        return bgxtTsxmtatMapper.selectTatTrendWithTarget(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"));
    }

    public List<Map<String, Object>> tatPhaseStats(String beginDate, String endDate) {
        return bgxtTsxmtatMapper.selectTatPhaseStats(safeDate(beginDate,"2020-01-01"), safeDate(endDate,"2099-12-31"));
    }

    public String exportCustomReportCsv(String dimension, String beginDate, String endDate) {
        String bd = safeDate(beginDate, "2020-01-01");
        String ed = safeDate(endDate, "2099-12-31");
        List<Map<String, Object>> data;
        switch (dimension != null ? dimension : "department") {
            case "doctor": data = bgxtBrxxMapper.selectCustomReportByDoctor(bd, ed); break;
            case "item": data = bgxtJyjgMapper.selectCustomReportByItem(bd, ed); break;
            case "status": data = bgxtBrxxMapper.selectCustomReportByStatus(bd, ed); break;
            case "daily": data = bgxtBrxxMapper.selectCustomReportByDaily(bd, ed); break;
            case "patientType": data = bgxtBrxxMapper.selectCustomReportByPatientType(bd, ed); break;
            case "urgency": data = bgxtBrxxMapper.selectCustomReportByUrgency(bd, ed); break;
            case "instrument": data = bgxtBrxxMapper.selectCustomReportByInstrument(bd, ed); break;
            case "combo": data = bgxtBrxxMapper.selectCustomReportByCombo(bd, ed); break;
            case "examiner": data = bgxtBrxxMapper.selectCustomReportByExaminer(bd, ed); break;
            default: data = bgxtBrxxMapper.selectCustomReportByDepartment(bd, ed);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("名称,数量\n");
        for (Map<String, Object> row : data) {
            sb.append('"').append(row.getOrDefault("name", "").toString().replace("\"", "\"\"")).append('"').append(',')
              .append(row.getOrDefault("cnt", 0)).append('\n');
        }
        return sb.toString();
    }

    public String exportTatCsv(String type, String beginDate, String endDate) {
        String bd = safeDate(beginDate, "2020-01-01");
        String ed = safeDate(endDate, "2099-12-31");
        StringBuilder sb = new StringBuilder();
        switch (type != null ? type : "statistics") {
            case "overtime":
                sb.append("患者姓名,样本号,组合名称,实际TAT(分钟),目标TAT(分钟),是否超时\n");
                for (Map<String, Object> row : bgxtTsxmtatMapper.selectTatOvertime(bd, ed)) {
                    sb.append(val(row,"brxm")).append(',').append(val(row,"syh")).append(',')
                      .append(val(row,"zhmc")).append(',').append(val(row,"actualMin")).append(',')
                      .append(val(row,"TAT")).append(',').append("1".equals(val(row,"isOver")) ? "是" : "否").append('\n');
                }
                break;
            case "trend":
                sb.append("日期,平均TAT(分钟),样本数\n");
                for (Map<String, Object> row : tatTrendWithTarget(beginDate, endDate)) {
                    sb.append(val(row,"date")).append(',').append(val(row,"avgMin")).append(',').append(val(row,"cnt")).append('\n');
                }
                break;
            default:
                sb.append("组合名称,平均TAT(分钟),样本数\n");
                for (Map<String, Object> row : bgxtTsxmtatMapper.selectAvgTat(bd, ed)) {
                    sb.append(val(row,"zhmc")).append(',').append(val(row,"avgMin")).append(',').append(val(row,"cnt")).append('\n');
                }
                break;
        }
        return sb.toString();
    }

    private String val(Map<String, Object> row, String key) {
        Object v = row.get(key);
        return v != null ? v.toString() : "";
    }

    public Map<String, Object> getProcessRate(String date) {
        Map<String, Object> result = new HashMap<>();
        String targetDate = (date == null || date.trim().isEmpty())
                ? LocalDate.now().toString()
                : date;

        Map<String, Object> stats = bgxtBrxxMapper.selectProcessRateStats(targetDate);

        result.put("success", true);
        result.put("date", targetDate);
        result.put("pending", stats != null && stats.get("pending") != null ? stats.get("pending") : 0);
        result.put("audited", stats != null && stats.get("audited") != null ? stats.get("audited") : 0);
        result.put("printed", stats != null && stats.get("printed") != null ? stats.get("printed") : 0);
        return result;
    }
}
