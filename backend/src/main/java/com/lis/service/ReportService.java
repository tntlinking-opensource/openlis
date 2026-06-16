package com.lis.service;

import com.lis.dto.QueryRequest;
import com.lis.enums.SampleStatus;
import com.lis.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Base64;

@Service
@Slf4j
public class ReportService {

    @Autowired
    private BgxtBrxxMapper brxxMapper;

    @Autowired
    private BgxtJyjgMapper jyjgMapper;

    @Autowired
    private BgxtBgmbMapper bgmbMapper;

    @Autowired
    private BgxtJgdybMapper jgdybMapper;

    @Autowired
    private InstrumentMapper instrumentMapper;

    @Autowired
    private SysKsszMapper ksszMapper;

    @Autowired
    private SysJyxmMapper jyxmMapper;

    @Autowired
    private SysCzydmMapper czydmMapper;

    @Autowired
    private HISNotificationService hisNotificationService;

    public Map<String, Object> querySampleList(String startDate, String endDate, String patientType,
            String department, String testItem, String status, String instrument,
            String examiner, String auditor, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = brxxMapper.selectSampleList(
                startDate, endDate, patientType, department, status, instrument, examiner, auditor,
                pageSize, offset);
        Integer total = brxxMapper.countSampleList(
                startDate, endDate, patientType, department, status, instrument, examiner, auditor);
        Map<String, Object> result = new HashMap<>();
        result.put("total", total != null ? total : 0);
        result.put("list", list);
        return result;
    }

    public Map<String, Object> querySampleStatistics(String startDate, String endDate, String patientType,
            String department, String testItem, String status, String instrument,
            String examiner, String auditor) {
        Map<String, Object> result = new HashMap<>();

        List<Map<String, Object>> byPatientType = new ArrayList<>();
        try {
            List<Map<String, Object>> brlbStats = brxxMapper.selectStatsByPatientType();
            for (Map<String, Object> stat : brlbStats) {
                Object brlb = stat.get("brlb");
                Object cnt = stat.get("cnt");
                String name = "其他";
                if (brlb != null) {
                    switch (Integer.parseInt(brlb.toString())) {
                        case 1: name = "门诊"; break;
                        case 2: name = "住院"; break;
                        case 3: name = "体检"; break;
                        case 4: name = "其他"; break;
                        case 5: name = "科研"; break;
                    }
                }
                Map<String, Object> item = new HashMap<>();
                item.put("name", name);
                item.put("value", cnt != null ? Integer.parseInt(cnt.toString()) : 0);
                byPatientType.add(item);
            }
        } catch (Exception e) {
            byPatientType.add(createStatItem("门诊", 0));
            byPatientType.add(createStatItem("住院", 0));
            byPatientType.add(createStatItem("体检", 0));
        }
        if (byPatientType.isEmpty()) {
            byPatientType.add(createStatItem("门诊", 0));
            byPatientType.add(createStatItem("住院", 0));
            byPatientType.add(createStatItem("体检", 0));
        }
        result.put("byPatientType", byPatientType);

        List<Map<String, Object>> byStatus = new ArrayList<>();
        try {
            List<Map<String, Object>> ybztStats;
            if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
                ybztStats = brxxMapper.selectStatsByStatusWithDate(startDate, endDate);
            } else {
                ybztStats = brxxMapper.selectStatsByStatus();
            }
            for (Map<String, Object> stat : ybztStats) {
                Object ybzt = stat.get("ybzt");
                Object cnt = stat.get("cnt");
                String name = "新建";
                if (ybzt != null) {
                    switch (Integer.parseInt(ybzt.toString())) {
                        case 0: name = "登记"; break;
                        case 1: name = "未审核"; break;
                        case 2: name = "已审核"; break;
                        case 3: name = "已打印"; break;
                        default: name = SampleStatus.getDesc(Integer.parseInt(ybzt.toString())); break;
                    }
                }
                Map<String, Object> item = new HashMap<>();
                item.put("name", name);
                item.put("value", cnt != null ? Integer.parseInt(cnt.toString()) : 0);
                byStatus.add(item);
            }
        } catch (Exception e) {
            byStatus.add(createStatItem("已审核", 0));
            byStatus.add(createStatItem("已检验", 0));
        }
        if (byStatus.isEmpty()) {
            byStatus.add(createStatItem("已审核", 0));
            byStatus.add(createStatItem("已检验", 0));
        }
        result.put("byStatus", byStatus);

        List<Map<String, Object>> byDate = new ArrayList<>();
        try {
            List<Map<String, Object>> dateStats = brxxMapper.selectStatsByDate();
            for (Map<String, Object> stat : dateStats) {
                Object date = stat.get("stat_date");
                Object cnt = stat.get("cnt");
                Map<String, Object> item = new HashMap<>();
                item.put("date", date != null ? date.toString() : "");
                item.put("count", cnt != null ? Integer.parseInt(cnt.toString()) : 0);
                byDate.add(item);
            }
        } catch (Exception e) {
        }
        if (byDate.isEmpty()) {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                Map<String, Object> item = new HashMap<>();
                item.put("date", date.format(formatter));
                item.put("count", 0);
                byDate.add(item);
            }
        }
        result.put("byDate", byDate);

        return result;
    }

    private Map<String, Object> createStatItem(String name, int value) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("value", value);
        return item;
    }

    public Map<String, Object> getQueryOptions() {
        Map<String, Object> options = new HashMap<>();
        List<String> patientTypes = Arrays.asList("门诊病人", "住院病人", "体检人员", "其他病人", "科研人员");
        options.put("patientTypes", patientTypes);
        List<String> statuses = Arrays.asList("新建", "已保存", "已检验", "已审核", "已打印", "作废");
        options.put("statuses", statuses);

        try {
            List<Map<String, Object>> deptList = ksszMapper.selectActiveDepartments();
            options.put("departments", deptList);
        } catch (Exception e) {
            options.put("departments", new ArrayList<>());
        }

        try {
            List<Map<String, Object>> instList = instrumentMapper.selectActiveInstruments();
            options.put("instruments", instList);
        } catch (Exception e) {
            options.put("instruments", new ArrayList<>());
        }

        try {
            List<Map<String, Object>> itemList = jyxmMapper.selectActiveItems();
            options.put("testItems", itemList);
        } catch (Exception e) {
            options.put("testItems", new ArrayList<>());
        }

        try {
            List<Map<String, Object>> examinerList = brxxMapper.selectExaminers();
            options.put("examiners", examinerList);
        } catch (Exception e) {
            options.put("examiners", new ArrayList<>());
        }

        try {
            List<Map<String, Object>> auditorList = brxxMapper.selectAuditors();
            options.put("auditors", auditorList);
        } catch (Exception e) {
            options.put("auditors", new ArrayList<>());
        }

        return options;
    }

    @Transactional
    public Map<String, Object> cleanupDatabase() {
        Map<String, Object> result = new HashMap<>();
        int deletedCount = 0;

        try {
            List<Map<String, Object>> sameRecords = brxxMapper.selectStatsByPatientType();
            for (Map<String, Object> rec : sameRecords) {
                deletedCount += brxxMapper.deleteDuplicateByName((String) rec.get("brlb"));
            }
        } catch (Exception e) {
            log.error("清理重复记录时出错: " + e.getMessage());
        }

        try {
            deletedCount += brxxMapper.deleteGarbledNames();
        } catch (Exception e) {
            log.error("清理乱码名字时出错: " + e.getMessage());
        }

        result.put("success", true);
        result.put("message", "清理完成，共删除 " + deletedCount + " 条重复记录");
        result.put("deletedCount", deletedCount);
        return result;
    }

    public Map<String, Object> queryReportList(QueryRequest params) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> data = brxxMapper.selectReportList(
                    params.getBrxm(), params.getBrxxTmh(), params.getSyh(), params.getKsdm(),
                    params.getShys(), params.getSjys(), params.getBrlb(), params.getYbztList(),
                    params.getSbDjid(), params.getBeginDate(), params.getEndDate());
            result.put("success", true);
            result.put("data", data);
            result.put("total", data.size());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败：" + e.getMessage());
        }
        return result;
    }

    public List<Map<String, Object>> getReportResults(Integer id) {
        String sql = "SELECT jg.*, jf.xmzwmc, jf.xmdm, jf.xmdw FROM bgxt_jyjg jg " +
                "LEFT JOIN sys_jyxm_full jf ON jg.xmid = jf.xmid WHERE jg.brxx_id = ? ORDER BY jg.id";
        return jyjgMapper.selectResultsByBrxxId(id);
    }

    @Transactional
    public Map<String, Object> printReport(Integer id, String czydm) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer ybzt = brxxMapper.selectStatusById(id);
            if (ybzt == null) {
                result.put("success", false);
                result.put("message", "样本不存在");
                return result;
            }
            if (ybzt != 2 && ybzt != 3) {
                result.put("success", false);
                result.put("message", "只有已审核或已打印状态的样本可以打印（当前状态：" + getStatusText(ybzt) + "）");
                return result;
            }
            Integer resultCount = jyjgMapper.countByBrxxId(id);
            if (resultCount == null || resultCount == 0) {
                result.put("success", false);
                result.put("message", "样本没有检验结果，不能打印");
                return result;
            }
            Integer printCount = brxxMapper.selectPrintCountById(id);
            brxxMapper.updatePrintById(id, printCount != null ? printCount + 1 : 1);
            jgdybMapper.insertPrintLog(id, czydm, null, null);
            Integer brlb = brxxMapper.selectFullById(id) != null ? (Integer) brxxMapper.selectFullById(id).get("brlb") : null;
            hisNotificationService.notifySampleStatus(id, 3);
            result.put("success", true);
            result.put("message", "打印成功");
        } catch (Exception e) {
            log.error("打印失败", e);
            result.put("success", false);
            result.put("message", "打印失败：" + e.getMessage());
        }
        return result;
    }

    private String getStatusText(Integer ybzt) {
        return SampleStatus.getDesc(ybzt);
    }

    public Map<String, Object> getFilterOptions() {
        Map<String, Object> result = new HashMap<>();
        result.put("instruments", instrumentMapper.selectActiveInstruments());
        result.put("departments", ksszMapper.selectActiveDepartments());
        result.put("doctors", czydmMapper.findGroupList(null, null, null));
        return result;
    }

    public List<Map<String, Object>> listTemplates() {
        return bgmbMapper.selectAll();
    }

    public Map<String, Object> getTemplate(Integer id) {
        return bgmbMapper.selectById(id);
    }

    @Transactional
    public Map<String, Object> saveTemplate(com.lis.dto.ReportTemplateRequest data) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer id = data.getId();
            if (id != null && !"0".equals(String.valueOf(id))) {
                com.lis.entity.BgxtBgmb record = new com.lis.entity.BgxtBgmb();
                record.setId(id);
                record.setBgmbmc(data.getBgmbmc());
                record.setBgjglx(data.getBgjglx());
                record.setSbDjid(data.getSbDjid());
                record.setZhid(data.getZhid());
                record.setKsdm(data.getKsdm());
                record.setBz(data.getBz());
                record.setBgmbnr(data.getBgmbnr());
                bgmbMapper.updateById(record);
            } else {
                com.lis.entity.BgxtBgmb record = new com.lis.entity.BgxtBgmb();
                record.setBgmbmc(data.getBgmbmc());
                record.setBgjglx(data.getBgjglx());
                record.setSbDjid(data.getSbDjid());
                record.setZhid(data.getZhid());
                record.setKsdm(data.getKsdm());
                record.setBz(data.getBz());
                record.setBgmbnr(data.getBgmbnr());
                bgmbMapper.insert(record);
            }
            result.put("success", true);
            result.put("message", "保存成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "保存失败：" + e.getMessage());
        }
        return result;
    }

    @Transactional
    public Map<String, Object> deleteTemplate(Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            bgmbMapper.deleteById(id);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
        }
        return result;
    }

    public List<Map<String, Object>> queryBatchPrintSamples(String beginDate, String endDate, Integer brlb, String ksdm, Integer sbDjid) {
        return brxxMapper.selectBatchPrintList(beginDate, endDate, brlb, ksdm, sbDjid);
    }

    @Transactional
    public Map<String, Object> executeBatchPrint(List<Integer> brxxIds, String czydm, Boolean skipPrinted) {
        Map<String, Object> result = new HashMap<>();
        if (brxxIds == null || brxxIds.isEmpty()) {
            result.put("success", false);
            result.put("message", "没有选择样本");
            return result;
        }
        int printed = 0, skipped = 0;
        for (Integer brxxId : brxxIds) {
            try {
                Integer ybzt = brxxMapper.selectStatusById(brxxId);
                if (ybzt == null) { skipped++; continue; }
                if (Boolean.TRUE.equals(skipPrinted) && ybzt == 3) { skipped++; continue; }
                if (ybzt != 2 && ybzt != 3) { skipped++; continue; }

                Integer currentPrintCount = brxxMapper.selectPrintCountById(brxxId);
                int newPrintCount = (currentPrintCount == null ? 0 : currentPrintCount) + 1;
                brxxMapper.updatePrintById(brxxId, newPrintCount);

                jgdybMapper.insertPrintLog(brxxId, czydm, null, null);

                try {
                    hisNotificationService.notifySampleStatus(brxxId, 3);
                } catch (Exception e) {
                    log.warn("HIS通知失败: brxxId={}", brxxId, e);
                }

                printed++;
            } catch (Exception e) { skipped++; }
        }
        result.put("success", true);
        result.put("message", "批量打印完成：打印" + printed + "条，跳过" + skipped + "条");
        result.put("printed", printed);
        result.put("skipped", skipped);
        return result;
    }

    public List<Map<String, Object>> getDepartments() {
        return ksszMapper.selectActiveDepartments();
    }

    public List<Map<String, Object>> querySampleResults(Integer brxxId) {
        return jyjgMapper.selectSampleResults(brxxId);
    }

    public Map<String, Object> mergePrint(List<Integer> brxxIds) {
        Map<String, Object> result = new HashMap<>();
        if (brxxIds == null || brxxIds.isEmpty()) {
            result.put("success", false);
            result.put("message", "没有选择样本");
            return result;
        }

        try {
            List<byte[]> pdfPages = new ArrayList<>();
            for (Integer brxxId : brxxIds) {
                Map<String, Object> reportData = getReportDataForMerge(brxxId);
                if (reportData != null && !reportData.isEmpty()) {
                    byte[] pdfPage = generatePdfPage(reportData);
                    if (pdfPage != null) {
                        pdfPages.add(pdfPage);
                    }
                }
            }

            if (pdfPages.isEmpty()) {
                result.put("success", false);
                result.put("message", "没有可合并的报告");
                return result;
            }

            byte[] mergedPdf = mergePdfBytes(pdfPages);
            String base64 = Base64.getEncoder().encodeToString(mergedPdf);

            result.put("success", true);
            result.put("message", "合并打印成功");
            result.put("pdf", base64);
            result.put("count", pdfPages.size());
        } catch (Exception e) {
            log.error("合并打印失败", e);
            result.put("success", false);
            result.put("message", "合并打印失败：" + e.getMessage());
        }
        return result;
    }

    private Map<String, Object> getReportDataForMerge(Integer brxxId) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> brxx = brxxMapper.selectReportInfoById(brxxId);
        if (brxx != null) {
            data.putAll(brxx);
            data.put("results", jyjgMapper.selectReportResultsByBrxxId(brxxId));
        }
        return data;
    }

    private byte[] generatePdfPage(Map<String, Object> data) {
        try {
            String html = buildReportHtml(data);
            return htmlToPdfBytes(html);
        } catch (Exception e) {
            log.error("生成PDF页失败", e);
            return null;
        }
    }

    private String buildReportHtml(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'/><style>");
        html.append("body{font-family:SimSun,Microsoft YaHei,Arial;font-size:12px;padding:20px;}");
        html.append("h1{text-align:center;font-size:18px;margin:0 0 10px;}");
        html.append("table{width:100%;border-collapse:collapse;margin-top:10px;}");
        html.append("th,td{border:1px solid #333;padding:4px 6px;text-align:left;}");
        html.append("th{background:#f0f0f0;}</style></head><body>");

        html.append("<h1>检验报告单</h1>");
        html.append("<table><tr><td>姓名：").append(safe(data.get("brxm"))).append("</td>");
        html.append("<td>性别：").append(data.get("brxb") != null ? (data.get("brxb").toString().equals("1") ? "男" : "女") : "").append("</td>");
        html.append("<td>年龄：").append(safe(data.get("brnl"))).append("</td></tr>");
        html.append("<tr><td>样本号：").append(safe(data.get("syh"))).append("</td>");
        html.append("<td>条码号：").append(safe(data.get("brxx_tmh"))).append("</td>");
        html.append("<td>科室：").append(safe(data.get("ksdm"))).append("</td></tr></table>");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> results = (List<Map<String, Object>>) data.get("results");
        if (results != null && !results.isEmpty()) {
            html.append("<table><thead><tr><th>项目名称</th><th>结果</th><th>单位</th><th>参考范围</th><th>提示</th></tr></thead><tbody>");
            for (Map<String, Object> r : results) {
                String flag = "";
                Object gdbj = r.get("gdbj");
                if (gdbj != null) {
                    String s = String.valueOf(gdbj);
                    if ("H".equalsIgnoreCase(s)) flag = "↑";
                    if ("L".equalsIgnoreCase(s)) flag = "↓";
                }
                html.append("<tr><td>").append(safe(r.get("xmdm"))).append("</td>");
                html.append("<td>").append(safe(r.get("jyjg"))).append("</td>");
                html.append("<td>").append(safe(r.get("jldw"))).append("</td>");
                html.append("<td>").append(safe(r.get("ckz"))).append("</td>");
                html.append("<td>").append(flag).append("</td></tr>");
            }
            html.append("</tbody></table>");
        }

        html.append("</body></html>");
        return html.toString();
    }

    private String safe(Object v) {
        if (v == null) return "";
        return String.valueOf(v).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private byte[] htmlToPdfBytes(String html) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4, 35, 35, 20, 35);

        com.lowagie.text.pdf.BaseFont bfChinese = null;
        try {
            bfChinese = com.lowagie.text.pdf.BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", com.lowagie.text.pdf.BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            bfChinese = com.lowagie.text.pdf.BaseFont.createFont();
        }

        com.lowagie.text.pdf.PdfWriter writer = com.lowagie.text.pdf.PdfWriter.getInstance(document, outputStream);
        document.open();

        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html, "UTF-8");
        org.jsoup.select.Elements body = doc.select("body");
        if (!body.isEmpty()) {
            com.lowagie.text.Font defaultFont = bfChinese != null ? new com.lowagie.text.Font(bfChinese, 12) : com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA, 12);
            for (org.jsoup.nodes.Element elem : body.first().children()) {
                renderElementToDocument(elem, document, writer, defaultFont);
            }
        }

        document.close();
        return outputStream.toByteArray();
    }

    private void renderElementToDocument(org.jsoup.nodes.Element elem, com.lowagie.text.Document document,
            com.lowagie.text.pdf.PdfWriter writer, com.lowagie.text.Font defaultFont) throws Exception {
        String tagName = elem.tagName().toLowerCase();

        switch (tagName) {
            case "h1": {
                com.lowagie.text.Font font = new com.lowagie.text.Font(defaultFont.getBaseFont(), 18, com.lowagie.text.Font.BOLD);
                com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(elem.text(), font);
                p.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                document.add(p);
                break;
            }
            case "p": {
                com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(elem.text(), defaultFont);
                p.setSpacingBefore(2);
                p.setSpacingAfter(2);
                document.add(p);
                break;
            }
            case "table": {
                renderHtmlTable(elem, document, defaultFont);
                break;
            }
            default: {
                for (org.jsoup.nodes.Element child : elem.children()) {
                    renderElementToDocument(child, document, writer, defaultFont);
                }
                break;
            }
        }
    }

    private void renderHtmlTable(org.jsoup.nodes.Element tableElem, com.lowagie.text.Document document,
            com.lowagie.text.Font defaultFont) throws Exception {
        org.jsoup.select.Elements rows = tableElem.select("tr");
        if (rows.isEmpty()) return;

        int maxCols = 0;
        for (org.jsoup.nodes.Element row : rows) {
            int cols = row.select("th,td").size();
            if (cols > maxCols) maxCols = cols;
        }
        if (maxCols == 0) return;

        com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(maxCols);
        table.setWidthPercentage(100);

        for (org.jsoup.nodes.Element row : rows) {
            org.jsoup.select.Elements cells = row.select("th,td");
            for (org.jsoup.nodes.Element cell : cells) {
                boolean isHeader = cell.tagName().equalsIgnoreCase("th");
                com.lowagie.text.Font cellFont = new com.lowagie.text.Font(defaultFont.getBaseFont(), 10,
                    isHeader ? com.lowagie.text.Font.BOLD : com.lowagie.text.Font.NORMAL);
                com.lowagie.text.pdf.PdfPCell pdfCell = new com.lowagie.text.pdf.PdfPCell(
                    new com.lowagie.text.Phrase(cell.text().trim(), cellFont));
                pdfCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                pdfCell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
                pdfCell.setPadding(3);
                if (isHeader) {
                    pdfCell.setBackgroundColor(new java.awt.Color(240, 240, 240));
                }
                table.addCell(pdfCell);
            }
        }
        document.add(table);
    }

    private byte[] mergePdfBytes(List<byte[]> pdfPages) throws Exception {
        ByteArrayOutputStream mergedOutput = new ByteArrayOutputStream();
        com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4);
        com.lowagie.text.pdf.PdfCopy copy = new com.lowagie.text.pdf.PdfCopy(document, mergedOutput);
        document.open();

        for (byte[] pdfPage : pdfPages) {
            if (pdfPage != null && pdfPage.length > 0) {
                com.lowagie.text.pdf.PdfReader reader = new com.lowagie.text.pdf.PdfReader(pdfPage);
                for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                    copy.addPage(copy.getImportedPage(reader, i));
                }
                reader.close();
            }
        }

        document.close();
        return mergedOutput.toByteArray();
    }

    public Map<String, Object> mergeToPdf(List<Integer> brxxIds) {
        return mergePrint(brxxIds);
    }

    public Map<String, Object> printCollectionList(List<Integer> brxxIds) {
        Map<String, Object> result = new HashMap<>();
        if (brxxIds == null || brxxIds.isEmpty()) {
            result.put("success", false);
            result.put("message", "没有选择样本");
            return result;
        }

        try {
            List<Map<String, Object>> collectionData = brxxMapper.selectPatientListByIds(brxxIds);

            String html = buildCollectionListHtml(collectionData);
            byte[] pdfBytes = htmlToPdfBytes(html);
            String base64 = Base64.getEncoder().encodeToString(pdfBytes);

            result.put("success", true);
            result.put("message", "采集列表打印成功");
            result.put("pdf", base64);
            result.put("count", collectionData.size());
        } catch (Exception e) {
            log.error("打印采集列表失败", e);
            result.put("success", false);
            result.put("message", "打印采集列表失败：" + e.getMessage());
        }
        return result;
    }

    private String buildCollectionListHtml(List<Map<String, Object>> data) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'/><style>");
        html.append("body{font-family:SimSun,Microsoft YaHei,Arial;font-size:12px;padding:20px;}");
        html.append("h1{text-align:center;font-size:16px;margin:0 0 15px;}");
        html.append("table{width:100%;border-collapse:collapse;}");
        html.append("th,td{border:1px solid #333;padding:4px 6px;text-align:center;}");
        html.append("th{background:#f0f0f0;}</style></head><body>");
        html.append("<h1>样本采集列表</h1>");
        html.append("<table><thead><tr><th>序号</th><th>姓名</th><th>性别</th><th>年龄</th><th>样本号</th><th>条码号</th><th>科室</th><th>床号</th><th>样本类型</th><th>状态</th></tr></thead><tbody>");

        int index = 1;
        for (Map<String, Object> row : data) {
            html.append("<tr><td>").append(index++).append("</td>");
            html.append("<td>").append(safe(row.get("name"))).append("</td>");
            html.append("<td>").append(safe(row.get("sex"))).append("</td>");
            html.append("<td>").append(safe(row.get("age"))).append("</td>");
            html.append("<td>").append(safe(row.get("sampleNo"))).append("</td>");
            html.append("<td>").append(safe(row.get("barcode"))).append("</td>");
            html.append("<td>").append(safe(row.get("ward"))).append("</td>");
            html.append("<td>").append(safe(row.get("bedNo"))).append("</td>");
            html.append("<td>").append(safe(row.get("sampleType"))).append("</td>");
            html.append("<td>").append(getStatusText((Integer) row.get("status"))).append("</td></tr>");
        }

        html.append("</tbody></table></body></html>");
        return html.toString();
    }

    public List<Map<String, Object>> queryBatchPrintSamplesWithFilters(String beginDate, String endDate, Integer brlb, String ksdm, String tjdw, Integer sbDjid) {
        return brxxMapper.selectBatchPrintListWithFilters(beginDate, endDate, brlb, ksdm, tjdw, sbDjid);
    }
}