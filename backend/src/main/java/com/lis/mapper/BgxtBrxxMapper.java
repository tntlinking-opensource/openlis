package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.BgxtBrxx;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface BgxtBrxxMapper extends BaseMapper<BgxtBrxx> {

    Integer selectMaxSampleSeq(String prefix, String date);

    Integer selectStatusById(Integer brxxId);

    @Select("SELECT brxx_id, brxm, brxb, brtmh, ksdm FROM bgxt_brxx WHERE brxx_id=#{id}")
    Map<String, Object> selectPatientPreview(@Param("id") Integer id);

    Integer selectPrintCountById(Integer brxxId);

    Integer countByDate(String date);

    Integer selectIdByTmh(String tmh);

    Map<String, Object> selectPatientContextById(Integer brxxId);

    Map<String, Object> selectReportInfoById(Integer brxxId);

    Map<String, Object> selectFullById(Integer brxxId);

    String selectBbzlNameByBm(Integer bm);

    int updateStatusById(Integer brxxId, Integer ybzt);

    int inspectById(@Param("brxxId") Integer brxxId, @Param("jyys") String jyys);

    int auditById(@Param("brxxId") Integer brxxId, @Param("shys") String shys);

    int unauditById(Integer brxxId);

    int updatePrintById(Integer brxxId, int printCount);

    int invalidateById(Integer brxxId, String reason);

    int cancelInvalidById(Integer brxxId, String remarks);

    int updateRemarksById(Integer brxxId, String remarks);

    int deleteById(Integer brxxId);

    int insertPatient(BgxtBrxx patient);

    List<Integer> selectPatientIdsByFilter(String date, String sampleNo, String name, String barcode);

    List<Map<String, Object>> selectPatientListByIds(@Param("ids") List<Integer> ids);

    List<Map<String, Object>> searchSamples(String sampleNo, String name, String barcode, String patientId, String date);

    Map<String, Object> selectProgressStats(String date);

    List<Map<String, Object>> selectSampleIssues(String date);

    Integer selectIdBySyhAndSbdjid(Integer syh, Integer sbDjid);

    @Select("SELECT brxx_id, brxm, syh, ybzt, jyrq FROM bgxt_brxx WHERE jyrq >= #{startDate} AND jyrq <= CONCAT(#{endDate}, ' 23:59:59') ORDER BY brxx_id")
    List<Map<String, Object>> selectListByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Select("SELECT brxx_id FROM bgxt_brxx WHERE brxm = #{brxm} AND ybzt IN (0, 1, 4) ORDER BY brxx_id DESC LIMIT 1")
    Integer selectIdByBrxm(@Param("brxm") String brxm);

    int updateYcztById(Integer brxxId, Integer yczt);

    int updateReportIncorrect(Integer brxxId, String reason);

    int updatePatientById(BgxtBrxx patient);

    int insertBrxxLog(Integer brxxId, String czydm);

    List<Map<String, Object>> selectRefreshList(Integer sbDjid, String date, Integer brlb);

    List<Map<String, Object>> selectSampleList(String startDate, String endDate, String patientType, String department, String status, String instrument, String examiner, String auditor, int limit, int offset);

    Integer countSampleList(String startDate, String endDate, String patientType, String department, String status, String instrument, String examiner, String auditor);

    List<Map<String, Object>> selectStatsByPatientType();

    List<Map<String, Object>> selectStatsByStatus();

    List<Map<String, Object>> selectStatsByDate();

    List<Map<String, Object>> selectExaminers();

    List<Map<String, Object>> selectAuditors();

    List<Map<String, Object>> selectReportList(String brxm, String brxxTmh, String syh, String ksdm, String shys, String sjys, Integer brlb, List<Integer> ybztList, Integer sbDjid, String beginDate, String endDate);

    List<Map<String, Object>> selectBatchPrintList(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("brlb") Integer brlb, @Param("ksdm") String ksdm, @Param("sbDjid") Integer sbDjid);

    List<Map<String, Object>> selectAllForCleanup();

    Map<String, Object> selectLabelDataById(Integer brxxId);

    Integer selectMaxBarcodeSeq(String prefix);

    int deleteDuplicateByName(String brxm);

    int deleteGarbledNames();

    int deleteDuplicateRecords(String brxm, Integer brxb, String brnl, Integer brlb, Object jyrq);

    int updateBillingStatus(Integer brxxId, Integer sfbz);

    List<Map<String, Object>> listBillingSamples(@Param("syh") String syh, @Param("brxm") String brxm, @Param("brxxTmh") String brxxTmh, @Param("jyrq") String jyrq);

    int updateSampleTime(@Param("brxxId") Integer brxxId, @Param("jyrq") String jyrq, @Param("shrq") String shrq);

    List<Map<String, Object>> selectRawDataByBrxxId(Integer brxxId);

    int updateTransferInfo(@Param("brxxId") Integer brxxId, @Param("targetDate") String targetDate, @Param("targetInstrument") Integer targetInstrument, @Param("targetSyh") String targetSyh, @Param("startNo") String startNo);

    Map<String, Object> selectByBarcode(String barcode);

    int acceptByBarcode(String barcode);

    int insertToQc(Integer brxxId);

    Map<String, Object> selectPatient360View(Integer brxxId);

    Map<String, Object> selectProcessRateStats(String date);

    List<Map<String, Object>> selectBatchPrintListWithFilters(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("brlb") Integer brlb, @Param("ksdm") String ksdm, @Param("tjdw") String tjdw, @Param("sbDjid") Integer sbDjid);

    void updateStatusWithAudit(Integer brxxId, String shys);

    void updateStatusWithPrint(Integer brxxId);

    void updateStatusWithInspect(Integer brxxId, String jyys);

    void updateStatusWithFirstCheck(Integer brxxId, String czydm);

    void updateStatusWithIntermediateCheck(Integer brxxId, String czydm);

    void undoAudit(Integer brxxId);

    void undoInspect(Integer brxxId);

    void undoFirstCheck(Integer brxxId);

    void undoIntermediateCheck(Integer brxxId);

    @Select("SELECT COALESCE((SELECT k.ksmc FROM sys_kssz k WHERE k.ksdm=b.ksdm OR k.ksmc=b.ksdm LIMIT 1), b.ksdm, '未知') AS ksmc, COUNT(*) as cnt FROM bgxt_brxx b " +
            "WHERE b.jyrq BETWEEN #{beginDate} AND #{endDate} GROUP BY b.ksdm ORDER BY cnt DESC")
    List<Map<String, Object>> selectStatsByDepartment(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT COALESCE(c.czyxm, b.shys, '未知') AS czyxm, COUNT(*) as cnt FROM bgxt_brxx b LEFT JOIN sys_czydm c ON b.shys=c.czydm OR b.shys=c.czyxm " +
            "WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') AND b.shys IS NOT NULL AND b.shys <> '' GROUP BY b.shys, c.czyxm ORDER BY cnt DESC")
    List<Map<String, Object>> selectStatsByDoctor(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT CASE ybzt WHEN 0 THEN '登记' WHEN 1 THEN '未审核' WHEN 2 THEN '已审核' WHEN 3 THEN '已打印' WHEN 4 THEN '已检验' WHEN 5 THEN '初审' WHEN 6 THEN '复审' WHEN -1 THEN '已作废' ELSE CONCAT('未知(', ybzt, ')') END AS statusName, ybzt, COUNT(*) as cnt FROM bgxt_brxx WHERE jyrq BETWEEN #{beginDate} AND #{endDate} GROUP BY ybzt ORDER BY ybzt")
    List<Map<String, Object>> selectStatsByStatusWithDate(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT COUNT(*) as total, SUM(CASE WHEN ybzt=2 THEN 1 ELSE 0 END) as audited, " +
            "SUM(CASE WHEN ybzt=3 THEN 1 ELSE 0 END) as printed FROM bgxt_brxx WHERE jyrq BETWEEN #{beginDate} AND #{endDate}")
    Map<String, Object> selectTotalStats(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT COALESCE(k.ksmc, b.ksdm, '未知') as name, b.ksdm as code, COUNT(*) as cnt FROM bgxt_brxx b LEFT JOIN sys_kssz k ON b.ksdm=k.ksdm OR b.ksdm=k.ksmc " +
            "WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') GROUP BY b.ksdm, k.ksmc ORDER BY cnt DESC")
    List<Map<String, Object>> selectCustomReportByDepartment(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT COALESCE(c.czyxm, b.shys, '未知') as name, b.shys as code, COUNT(*) as cnt FROM bgxt_brxx b LEFT JOIN sys_czydm c ON b.shys=c.czydm OR b.shys=c.czyxm " +
            "WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') AND b.shys IS NOT NULL AND b.shys <> '' GROUP BY b.shys, c.czyxm ORDER BY cnt DESC")
    List<Map<String, Object>> selectCustomReportByDoctor(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT CASE ybzt WHEN 0 THEN '登记' WHEN 1 THEN '未审核' WHEN 2 THEN '已审核' WHEN 3 THEN '已打印' WHEN 4 THEN '已检验' WHEN 5 THEN '初审' WHEN 6 THEN '复审' WHEN -1 THEN '已作废' ELSE CONCAT('未知(', ybzt, ')') END as name, " +
            "COUNT(*) as cnt FROM bgxt_brxx WHERE jyrq >= #{beginDate} AND jyrq <= CONCAT(#{endDate}, ' 23:59:59') GROUP BY ybzt ORDER BY ybzt")
    List<Map<String, Object>> selectCustomReportByStatus(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT DATE(jyrq) as name, COUNT(*) as cnt FROM bgxt_brxx WHERE jyrq >= #{beginDate} AND jyrq <= CONCAT(#{endDate}, ' 23:59:59') GROUP BY DATE(jyrq) ORDER BY name")
    List<Map<String, Object>> selectCustomReportByDaily(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT CASE COALESCE(brlb,0) WHEN 1 THEN '门诊' WHEN 2 THEN '住院' WHEN 3 THEN '体检' WHEN 4 THEN '其他' WHEN 5 THEN '科研' WHEN 0 THEN '未知' ELSE CONCAT('其他(', COALESCE(brlb,0), ')') END AS name, COALESCE(brlb,0) AS brlb, COUNT(*) AS cnt " +
            "FROM bgxt_brxx WHERE jyrq >= #{beginDate} AND jyrq <= CONCAT(#{endDate}, ' 23:59:59') GROUP BY brlb ORDER BY cnt DESC")
    List<Map<String, Object>> selectStatsByPatientTypeWithDate(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT CASE COALESCE(brlb,0) WHEN 1 THEN '门诊' WHEN 2 THEN '住院' WHEN 3 THEN '体检' WHEN 4 THEN '其他' WHEN 5 THEN '科研' WHEN 0 THEN '未知' ELSE CONCAT('其他(', COALESCE(brlb,0), ')') END AS name, COUNT(*) AS cnt " +
            "FROM bgxt_brxx WHERE jyrq >= #{beginDate} AND jyrq <= CONCAT(#{endDate}, ' 23:59:59') GROUP BY brlb ORDER BY cnt DESC")
    List<Map<String, Object>> selectCustomReportByPatientType(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT COALESCE(syqk, '未知') as name, COUNT(*) as cnt FROM bgxt_brxx WHERE jyrq >= #{beginDate} AND jyrq <= CONCAT(#{endDate}, ' 23:59:59') GROUP BY syqk ORDER BY cnt DESC")
    List<Map<String, Object>> selectCustomReportByUrgency(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT COALESCE(s.sbmc, '未知仪器') as name, COUNT(*) as cnt FROM bgxt_brxx b LEFT JOIN sys_sbdjb s ON b.sb_djid=s.sb_djid WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') GROUP BY b.sb_djid, s.sbmc ORDER BY cnt DESC LIMIT 30")
    List<Map<String, Object>> selectCustomReportByInstrument(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT z.zhmc as name, z.zhid as code, COUNT(DISTINCT jg.brxx_id) as cnt FROM bgxt_jyjg jg " +
            "LEFT JOIN bgxt_xmzh_zb z ON jg.zhid = z.zhid " +
            "LEFT JOIN bgxt_brxx b ON jg.brxx_id = b.brxx_id " +
            "WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') AND z.zhmc IS NOT NULL AND jg.zhid > 0 " +
            "GROUP BY z.zhid, z.zhmc ORDER BY cnt DESC LIMIT 30")
    List<Map<String, Object>> selectCustomReportByCombo(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT COALESCE(c.czyxm, b.jyys, '未知') as name, b.jyys as code, COUNT(*) as cnt FROM bgxt_brxx b LEFT JOIN sys_czydm c ON b.jyys=c.czydm OR b.jyys=c.czyxm " +
            "WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') AND b.jyys IS NOT NULL AND b.jyys <> '' GROUP BY b.jyys, c.czyxm ORDER BY cnt DESC")
    List<Map<String, Object>> selectCustomReportByExaminer(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Select("SELECT b.brxm, b.brxx_tmh as tmh, CASE b.brxb WHEN 1 THEN '男' WHEN 2 THEN '女' ELSE '' END as brxb, " +
            "CASE COALESCE(b.brlb,0) WHEN 1 THEN '门诊' WHEN 2 THEN '住院' WHEN 3 THEN '体检' WHEN 4 THEN '其他' ELSE '未知' END as brlb, " +
            "COALESCE(k.ksmc, b.ksdm) as ksmc, b.jyrq, b.shrq, b.syh, b.bgmc " +
            "FROM bgxt_brxx b LEFT JOIN sys_kssz k ON b.ksdm=k.ksdm OR b.ksdm=k.ksmc " +
            "WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') " +
            "AND b.ksdm = #{filter} ORDER BY b.jyrq DESC LIMIT 200")
    List<Map<String, Object>> selectDetailByDepartment(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("filter") String filter);

    @Select("SELECT b.brxm, b.brxx_tmh as tmh, CASE b.brxb WHEN 1 THEN '男' WHEN 2 THEN '女' ELSE '' END as brxb, " +
            "CASE COALESCE(b.brlb,0) WHEN 1 THEN '门诊' WHEN 2 THEN '住院' WHEN 3 THEN '体检' WHEN 4 THEN '其他' ELSE '未知' END as brlb, " +
            "COALESCE(k.ksmc, b.ksdm) as ksmc, b.jyrq, b.shrq, b.syh, b.bgmc " +
            "FROM bgxt_brxx b LEFT JOIN sys_kssz k ON b.ksdm=k.ksdm OR b.ksdm=k.ksmc " +
            "WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') " +
            "AND b.shys = #{filter} ORDER BY b.jyrq DESC LIMIT 200")
    List<Map<String, Object>> selectDetailByDoctor(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("filter") String filter);

    @Select("SELECT b.brxm, b.brxx_tmh as tmh, CASE b.brxb WHEN 1 THEN '男' WHEN 2 THEN '女' ELSE '' END as brxb, " +
            "CASE COALESCE(b.brlb,0) WHEN 1 THEN '门诊' WHEN 2 THEN '住院' WHEN 3 THEN '体检' WHEN 4 THEN '其他' ELSE '未知' END as brlb, " +
            "COALESCE(k.ksmc, b.ksdm) as ksmc, b.jyrq, b.shrq, b.syh, b.bgmc " +
            "FROM bgxt_brxx b LEFT JOIN sys_kssz k ON b.ksdm=k.ksdm OR b.ksdm=k.ksmc " +
            "WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') " +
            "AND b.jyys = #{filter} ORDER BY b.jyrq DESC LIMIT 200")
    List<Map<String, Object>> selectDetailByExaminer(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("filter") String filter);

    @Select("SELECT DISTINCT b.brxm, b.brxx_tmh as tmh, CASE b.brxb WHEN 1 THEN '男' WHEN 2 THEN '女' ELSE '' END as brxb, " +
            "CASE COALESCE(b.brlb,0) WHEN 1 THEN '门诊' WHEN 2 THEN '住院' WHEN 3 THEN '体检' WHEN 4 THEN '其他' ELSE '未知' END as brlb, " +
            "COALESCE(k.ksmc, b.ksdm) as ksmc, b.jyrq, b.shrq, b.syh, b.bgmc " +
            "FROM bgxt_brxx b " +
            "JOIN bgxt_jyjg jg ON b.brxx_id = jg.brxx_id " +
            "JOIN bgxt_xmzh_zb z ON jg.zhid = z.zhid " +
            "LEFT JOIN sys_kssz k ON b.ksdm=k.ksdm OR b.ksdm=k.ksmc " +
            "WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') " +
            "AND jg.zhid = #{filter} ORDER BY b.jyrq DESC LIMIT 200")
    List<Map<String, Object>> selectDetailByCombo(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("filter") String filter);

    @Select("SELECT CASE COALESCE(b.brlb,0) WHEN 1 THEN '门诊' WHEN 2 THEN '住院' WHEN 3 THEN '体检' WHEN 4 THEN '其他' ELSE '未知' END as brlb, " +
            "COUNT(*) as cnt " +
            "FROM bgxt_brxx b WHERE b.jyrq >= #{beginDate} AND b.jyrq <= CONCAT(#{endDate}, ' 23:59:59') GROUP BY b.brlb ORDER BY cnt DESC")
    List<Map<String, Object>> selectPatientTypeWithFee(@Param("beginDate") String beginDate, @Param("endDate") String endDate);
}
