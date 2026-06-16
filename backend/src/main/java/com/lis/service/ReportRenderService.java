package com.lis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
public class ReportRenderService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String renderReportToPdfBase64(Integer templateId, Map<String, Object> data) {
        try {
            String html = getTemplateHtml(templateId);
            if (html == null || html.isEmpty()) {
                html = getDefaultTemplate();
            }
            String processedHtml = processTemplate(html, data);
            return htmlToPdfBase64(processedHtml);
        } catch (Exception e) {
            log.error("渲染报表失败", e);
            throw new RuntimeException("渲染报表失败: " + e.getMessage());
        }
    }

    public String renderHtmlToPdfBase64(String html, Map<String, Object> data) {
        try {
            String processedHtml = processTemplate(html, data);
            return htmlToPdfBase64(processedHtml);
        } catch (Exception e) {
            log.error("渲染HTML失败", e);
            throw new RuntimeException("渲染HTML失败: " + e.getMessage());
        }
    }

    private String getTemplateHtml(Integer templateId) {
        if (templateId == null) return null;
        try {
            Map<String, Object> template = jdbcTemplate.queryForMap(
                "SELECT html_content FROM sys_report_template WHERE template_id = ?", templateId);
            Object htmlContent = template.get("html_content");
            if (htmlContent == null) return null;
            String content = (String) htmlContent;
            if (content.trim().startsWith("{") || content.trim().startsWith("[")) {
                return jsonToHtml(content);
            }
            return content;
        } catch (Exception e) {
            log.warn("获取模板HTML失败, templateId={}", templateId, e);
            return null;
        }
    }

    private String jsonToHtml(String json) {
        try {
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n<html>\n<head><meta charset=\"UTF-8\"/></head>\n<body>\n");

            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(json);
            com.fasterxml.jackson.databind.JsonNode components = root.has("components") ? root.get("components") : root;

            java.util.List<com.fasterxml.jackson.databind.JsonNode> fieldList = new java.util.ArrayList<>();
            java.util.List<com.fasterxml.jackson.databind.JsonNode> resultTables = new java.util.ArrayList<>();
            java.util.List<com.fasterxml.jackson.databind.JsonNode> headerParts = new java.util.ArrayList<>();
            java.util.List<com.fasterxml.jackson.databind.JsonNode> footerParts = new java.util.ArrayList<>();

            boolean foundResultTable = false;
            if (components != null && components.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode comp : components) {
                    String type = comp.has("type") ? comp.get("type").asText() : "";
                    if (type.equals("resultTable")) {
                        foundResultTable = true;
                        resultTables.add(comp);
                        continue;
                    }
                    if (!foundResultTable) {
                        if (type.equals("field")) {
                            fieldList.add(comp);
                        } else {
                            headerParts.add(comp);
                        }
                    } else {
                        footerParts.add(comp);
                    }
                }
            }

            for (com.fasterxml.jackson.databind.JsonNode comp : headerParts) {
                String type = comp.has("type") ? comp.get("type").asText() : "";
                int fontSize = comp.has("fontSize") ? comp.get("fontSize").asInt() : 12;
                switch (type) {
                    case "title":
                    case "hospitalHeader": {
                        String text = comp.has("text") ? comp.get("text").asText() : "报告标题";
                        html.append(String.format("<h1 style=\"text-align:center;font-size:%dpt;margin:0 0 4pt 0;\">%s</h1>\n",
                            fontSize > 0 ? fontSize : 16, escapeHtml(text)));
                        break;
                    }
                    case "subtitle":
                    case "hospitalInfo": {
                        String text = comp.has("text") ? comp.get("text").asText() : "";
                        html.append(String.format("<p style=\"text-align:center;font-size:%dpt;margin:0 0 4pt 0;color:#666;\">%s</p>\n",
                            fontSize > 0 ? fontSize : 8, escapeHtml(text)));
                        break;
                    }
                    case "divider": {
                        html.append("<hr/>\n");
                        break;
                    }
                    case "space": {
                        int height = comp.has("height") ? comp.get("height").asInt() : 20;
                        html.append(String.format("<div style=\"height:%.1fpt;\"></div>\n", height * 0.75));
                        break;
                    }
                    case "patientInfo": {
                        String label = comp.has("label") ? comp.get("label").asText() : "患者信息";
                        html.append(String.format("<p style=\"font-weight:bold;font-size:10pt;margin:4pt 0 2pt 0;\">%s</p>\n", escapeHtml(label)));
                        break;
                    }
                }
            }

            if (!fieldList.isEmpty()) {
                java.util.Map<Integer, java.util.List<com.fasterxml.jackson.databind.JsonNode>> fieldsByRow = new java.util.TreeMap<>();
                for (com.fasterxml.jackson.databind.JsonNode field : fieldList) {
                    int top = field.has("top") ? field.get("top").asInt() : 0;
                    fieldsByRow.computeIfAbsent(top, k -> new java.util.ArrayList<>()).add(field);
                }
                html.append("<div class=\"patient-info-section\">\n");
                for (java.util.Map.Entry<Integer, java.util.List<com.fasterxml.jackson.databind.JsonNode>> entry : fieldsByRow.entrySet()) {
                    java.util.List<com.fasterxml.jackson.databind.JsonNode> rowFields = entry.getValue();
                    rowFields.sort((a, b) -> {
                        int la = a.has("left") ? a.get("left").asInt() : 0;
                        int lb = b.has("left") ? b.get("left").asInt() : 0;
                        return Integer.compare(la, lb);
                    });
                    html.append("<table style=\"width:100%;\" cellspacing=\"0\"><tr>");
                    for (com.fasterxml.jackson.databind.JsonNode field : rowFields) {
                        String label = field.has("label") ? field.get("label").asText() : "";
                        String fieldName = field.has("field") ? field.get("field").asText() : "";
                        html.append(String.format("<td style=\"font-size:9pt;padding:3pt 5pt;border:0.5pt solid #999;\">%s：{{%s}}</td>",
                            escapeHtml(label), fieldName));
                    }
                    html.append("</tr></table>\n");
                }
                html.append("</div>\n");
            }

            html.append("<hr/>\n");

            for (com.fasterxml.jackson.databind.JsonNode comp : resultTables) {
                String title = comp.has("title") ? comp.get("title").asText() : "检验结果";
                int rows = comp.has("rows") ? comp.get("rows").asInt() : 5;
                html.append(String.format("<p style=\"font-weight:bold;font-size:10pt;margin:4pt 0 2pt 0;\">%s</p>\n", escapeHtml(title)));
                html.append("<table class=\"result-table\" style=\"width:100%;\" cellspacing=\"0\">\n");
                html.append("<thead><tr><th style=\"border:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;background:#f0f0f0;\">项目名称</th><th style=\"border:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;background:#f0f0f0;\">结果</th><th style=\"border:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;background:#f0f0f0;\">参考值</th><th style=\"border:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;background:#f0f0f0;\">单位</th><th style=\"border:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;background:#f0f0f0;\">提示</th></tr></thead>\n");
                html.append("<tbody>\n");
                for (int i = 0; i < rows; i++) {
                    html.append("<tr><td style=\"border:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;\">&nbsp;</td><td style=\"border:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;\">&nbsp;</td><td style=\"border:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;\">&nbsp;</td><td style=\"border:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;\">&nbsp;</td><td style=\"border:0.5pt solid #333;padding:3pt 4pt;font-size:9pt;\">&nbsp;</td></tr>\n");
                }
                html.append("</tbody></table>\n");
            }

            html.append("<hr/>\n");

            java.util.List<com.fasterxml.jackson.databind.JsonNode> footerFields = new java.util.ArrayList<>();
            com.fasterxml.jackson.databind.JsonNode signLineNode = null;
            for (com.fasterxml.jackson.databind.JsonNode comp : footerParts) {
                String type = comp.has("type") ? comp.get("type").asText() : "";
                if (type.equals("field")) {
                    footerFields.add(comp);
                } else if (type.equals("signLine")) {
                    signLineNode = comp;
                }
            }

            if (!footerFields.isEmpty()) {
                html.append("<table style=\"width:100%;\" cellspacing=\"0\"><tr>");
                for (com.fasterxml.jackson.databind.JsonNode comp : footerFields) {
                    String label = "";
                    if (comp.has("showLabel") && comp.get("showLabel").asBoolean()) {
                        label = comp.has("label") ? comp.get("label").asText() : "";
                    }
                    String field = comp.has("field") ? comp.get("field").asText() : "";
                    html.append(String.format("<td style=\"font-size:9pt;padding:2pt 4pt;border:none;\">%s：{{%s}}</td>",
                        escapeHtml(label), field));
                }
                if (signLineNode != null) {
                    String signLabel = signLineNode.has("label") ? signLineNode.get("label").asText() : "医生签名";
                    html.append(String.format("<td style=\"font-size:9pt;padding:2pt 4pt;border:none;text-align:right;\">%s_________________________</td>",
                        escapeHtml(signLabel)));
                }
                html.append("</tr></table>\n");
            } else if (signLineNode != null) {
                String signLabel = signLineNode.has("label") ? signLineNode.get("label").asText() : "医生签名";
                int fontSize = signLineNode.has("fontSize") ? signLineNode.get("fontSize").asInt() : 12;
                html.append(String.format("<p style=\"font-size:%dpt;text-align:right;\">%s_________________________</p>\n",
                    fontSize > 0 ? fontSize : 9, escapeHtml(signLabel)));
            }

            html.append("</body>\n</html>");
            return html.toString();
        } catch (Exception e) {
            log.error("JSON转HTML失败", e);
            return "<html><body><p>模板转换失败: " + escapeHtml(e.getMessage()) + "</p></body></html>";
        }
    }

    private String getDefaultTemplate() {
        return "<!DOCTYPE html>\n" +
               "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
               "<head><meta charset=\"UTF-8\"/><style>\n" +
               "body { font-family: SimSun, serif; font-size: 12px; padding: 20px; }\n" +
               "h1 { text-align: center; font-size: 20px; }\n" +
               "table { width: 100%; border-collapse: collapse; margin-top: 20px; }\n" +
               "td, th { border: 1px solid #333; padding: 8px; }\n" +
               ".header { text-align: center; }\n" +
               "</style></head>\n" +
               "<body><h1>检验报告</h1>\n" +
               "<p>患者: {{patientName}}</p>\n" +
               "<p>标本: {{specimenType}}</p>\n" +
               "<p>结果: {{result}}</p>\n" +
               "</body></html>";
    }

    private String processTemplate(String template, Map<String, Object> data) {
        if (template == null) return "";
        String result = template;

        log.info("开始处理模板, 原始长度: {}, data大小: {}", template.length(), data != null ? data.size() : 0);

        if (data != null) {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                String value = entry.getValue() != null ? escapeHtml(entry.getValue().toString()) : "";
                int count = result.split(java.util.regex.Pattern.quote(placeholder), -1).length - 1;
                result = result.replace(placeholder, value);
                log.info("替换占位符: {}, 出现次数: {}, 替换值: {}", placeholder, count, value);
            }
        }

        log.info("模板处理完成, 结果长度: {}", result.length());
        return result;
    }

    private String htmlToPdfBase64(String html) throws Exception {
        byte[] pdfBytes = htmlToPdf(html);
        return Base64.getEncoder().encodeToString(pdfBytes);
    }

    private byte[] htmlToPdf(String html) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        com.lowagie.text.pdf.BaseFont bfChinese = null;
        try {
            bfChinese = com.lowagie.text.pdf.BaseFont.createFont(
                "STSong-Light", "UniGB-UCS2-H", com.lowagie.text.pdf.BaseFont.NOT_EMBEDDED);
            log.info("STSong-Light字体创建成功");
        } catch (Exception e) {
            log.warn("STSong-Light字体创建失败: {}", e.getMessage());
            try {
                bfChinese = com.lowagie.text.pdf.BaseFont.createFont(
                    com.lowagie.text.pdf.BaseFont.HELVETICA, com.lowagie.text.pdf.BaseFont.CP1252, com.lowagie.text.pdf.BaseFont.NOT_EMBEDDED);
            } catch (Exception e2) {
                log.warn("HELVETICA字体创建也失败: {}", e2.getMessage());
            }
        }

        final com.lowagie.text.pdf.BaseFont baseFont = bfChinese;
        final com.lowagie.text.Font defaultFont = baseFont != null ? new com.lowagie.text.Font(baseFont, 12) : com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA, 12);

        com.lowagie.text.Document document = new com.lowagie.text.Document(
            com.lowagie.text.PageSize.A4, 35, 35, 20, 35);
        com.lowagie.text.pdf.PdfWriter writer = com.lowagie.text.pdf.PdfWriter.getInstance(document, outputStream);
        document.open();

        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(html, "UTF-8");
        org.jsoup.select.Elements body = doc.select("body");
        if (!body.isEmpty()) {
            String bodyHtml = body.first().html();
            if (bodyHtml.contains("position") && bodyHtml.contains("absolute")) {
                com.lowagie.text.pdf.PdfContentByte canvas = writer.getDirectContent();
                float pageHeight = document.getPageSize().getHeight();
                for (org.jsoup.nodes.Element elem : body.first().children()) {
                    processElementDeep(elem, document, canvas, defaultFont, document.leftMargin(), document.getPageSize().getHeight() - document.topMargin(), pageHeight);
                }
            } else {
                for (org.jsoup.nodes.Element elem : body.first().children()) {
                    renderElementToDocument(elem, document, writer, defaultFont);
                }
            }
        }

        document.close();
        return outputStream.toByteArray();
    }

    private void renderElementToDocument(org.jsoup.nodes.Element elem, com.lowagie.text.Document document,
            com.lowagie.text.pdf.PdfWriter writer, com.lowagie.text.Font defaultFont) throws Exception {
        String tagName = elem.tagName().toLowerCase();
        String style = elem.attr("style");

        switch (tagName) {
            case "h1": {
                float fontSize = parseFontSizeFromStyle(style, 16);
                com.lowagie.text.Font font = new com.lowagie.text.Font(defaultFont.getBaseFont(), fontSize, com.lowagie.text.Font.BOLD);
                com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(elem.text(), font);
                p.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                p.setSpacingBefore(0);
                p.setSpacingAfter(2);
                document.add(p);
                break;
            }
            case "h2": {
                float fontSize = parseFontSizeFromStyle(style, 14);
                com.lowagie.text.Font font = new com.lowagie.text.Font(defaultFont.getBaseFont(), fontSize, com.lowagie.text.Font.BOLD);
                com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(elem.text(), font);
                p.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                p.setSpacingAfter(2);
                document.add(p);
                break;
            }
            case "p": {
                float fontSize = parseFontSizeFromStyle(style, 9f);
                boolean bold = style != null && style.contains("font-weight:bold");
                com.lowagie.text.Font pFont = new com.lowagie.text.Font(defaultFont.getBaseFont(), fontSize,
                    bold ? com.lowagie.text.Font.BOLD : com.lowagie.text.Font.NORMAL);
                com.lowagie.text.Paragraph p = new com.lowagie.text.Paragraph(elem.text(), pFont);
                if (style != null && style.contains("text-align:center")) {
                    p.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                }
                p.setSpacingBefore(1);
                p.setSpacingAfter(1);
                document.add(p);
                break;
            }
            case "hr": {
                com.lowagie.text.pdf.PdfContentByte canvas = writer.getDirectContent();
                float y = writer.getVerticalPosition(true);
                canvas.setLineWidth(0.5f);
                canvas.moveTo(document.leftMargin(), y);
                canvas.lineTo(document.getPageSize().getWidth() - document.rightMargin(), y);
                canvas.stroke();
                break;
            }
            case "div": {
                if (elem.hasClass("footer-push")) {
                    float bottomMargin = document.bottomMargin();
                    float left = document.leftMargin();
                    float pageWidth = document.getPageSize().getWidth();
                    float right = pageWidth - document.rightMargin();
                    com.lowagie.text.pdf.PdfContentByte cb = writer.getDirectContent();

                    java.util.List<org.jsoup.nodes.Element> children = elem.children();
                    float[] heights = new float[children.size()];
                    float totalHeight = 0;
                    for (int ci = 0; ci < children.size(); ci++) {
                        org.jsoup.nodes.Element child = children.get(ci);
                        String tag = child.tagName();
                        if ("hr".equals(tag)) {
                            heights[ci] = 10f;
                        } else if ("table".equals(tag)) {
                            com.lowagie.text.pdf.PdfPTable pdfTable = buildPdfTable(child, defaultFont, pageWidth, left, right);
                            pdfTable.setTotalWidth(right - left);
                            heights[ci] = pdfTable.getRowHeight(0) * countTableRows(child);
                        } else if ("p".equals(tag)) {
                            float fontSize = parseFontSizeFromStyle(child.attr("style"), 9f);
                            heights[ci] = fontSize + 4f;
                        }
                        totalHeight += heights[ci];
                    }

                    float drawY = bottomMargin + totalHeight + 5f;

                    for (int ci = 0; ci < children.size(); ci++) {
                        org.jsoup.nodes.Element child = children.get(ci);
                        String tag = child.tagName();
                        if ("hr".equals(tag)) {
                            cb.setLineWidth(0.5f);
                            cb.moveTo(left, drawY);
                            cb.lineTo(right, drawY);
                            cb.stroke();
                            drawY -= heights[ci];
                        } else if ("table".equals(tag)) {
                            com.lowagie.text.pdf.PdfPTable pdfTable = buildPdfTable(child, defaultFont, pageWidth, left, right);
                            pdfTable.setTotalWidth(right - left);
                            pdfTable.writeSelectedRows(0, -1, left, drawY, cb);
                            drawY -= heights[ci];
                        } else if ("p".equals(tag)) {
                            float fontSize = parseFontSizeFromStyle(child.attr("style"), 9f);
                            com.lowagie.text.Font font = new com.lowagie.text.Font(defaultFont.getBaseFont(), fontSize);
                            com.lowagie.text.pdf.ColumnText ct = new com.lowagie.text.pdf.ColumnText(cb);
                            ct.setLeading(fontSize + 4f);
                            ct.setSimpleColumn(left, drawY - fontSize - 4f, right, drawY);
                            ct.addText(new com.lowagie.text.Phrase(child.text(), font));
                            ct.go();
                            drawY -= heights[ci];
                        }
                    }
                } else if (elem.children().isEmpty()) {
                    float height = parseHeightFromStyle(style, 0);
                    if (height > 0) {
                        com.lowagie.text.Paragraph spacer = new com.lowagie.text.Paragraph(" ");
                        spacer.setSpacingBefore(height);
                        spacer.setSpacingAfter(0);
                        document.add(spacer);
                    }
                } else {
                    for (org.jsoup.nodes.Element child : elem.children()) {
                        renderElementToDocument(child, document, writer, defaultFont);
                    }
                }
                break;
            }
            case "table": {
                renderHtmlTable(elem, document, defaultFont);
                break;
            }
            case "span": {
                for (org.jsoup.nodes.Element child : elem.children()) {
                    renderElementToDocument(child, document, writer, defaultFont);
                }
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
        java.util.List<java.util.Set<Integer>> spannedPerRow = new java.util.ArrayList<>();
        for (int ri = 0; ri < rows.size(); ri++) {
            spannedPerRow.add(new java.util.HashSet<>());
        }
        for (int ri = 0; ri < rows.size(); ri++) {
            org.jsoup.select.Elements cells = rows.get(ri).select("th,td");
            java.util.Set<Integer> spanned = spannedPerRow.get(ri);
            int colIdx = 0;
            for (int ci = 0; ci < cells.size(); ci++) {
                while (spanned.contains(colIdx)) colIdx++;
                org.jsoup.nodes.Element cell = cells.get(ci);
                String rowspanAttr = cell.attr("rowspan");
                if (!rowspanAttr.isEmpty()) {
                    int rs = Integer.parseInt(rowspanAttr);
                    for (int r = 1; r < rs && ri + r < rows.size(); r++) {
                        spannedPerRow.get(ri + r).add(colIdx);
                    }
                }
                String colspanAttr = cell.attr("colspan");
                int cs = 1;
                if (!colspanAttr.isEmpty()) cs = Integer.parseInt(colspanAttr);
                colIdx += cs;
            }
            if (colIdx > maxCols) maxCols = colIdx;
        }
        if (maxCols == 0) return;

        com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(maxCols);
        table.setWidthPercentage(100);

        boolean isResultTable = tableElem.hasClass("result-table");
        if (isResultTable) {
            float[] widths = {3f, 1.5f, 1.2f, 2f, 2f};
            if (maxCols == widths.length) {
                table.setWidths(widths);
            }
        }

        for (int ri = 0; ri < rows.size(); ri++) {
            org.jsoup.nodes.Element row = rows.get(ri);
            org.jsoup.select.Elements cells = row.select("th,td");
            for (int ci = 0; ci < cells.size(); ci++) {
                org.jsoup.nodes.Element cell = cells.get(ci);
                boolean isHeader = cell.tagName().equalsIgnoreCase("th");
                String cellStyle = cell.attr("style");
                float fontSize = parseFontSizeFromStyle(cellStyle, 9f);
                com.lowagie.text.Font cellFont = new com.lowagie.text.Font(defaultFont.getBaseFont(), fontSize,
                    isHeader ? com.lowagie.text.Font.BOLD : com.lowagie.text.Font.NORMAL);

                String cellText = cell.text().trim();
                if (cellText.isEmpty() || cellText.equals("\u00a0") || cellText.equals(" ")) {
                    cellText = " ";
                }

                boolean noBorder = cellStyle != null && cellStyle.contains("border:none");

                com.lowagie.text.pdf.PdfPCell pdfCell = new com.lowagie.text.pdf.PdfPCell(
                    new com.lowagie.text.Phrase(cellText, cellFont));
                if (noBorder) {
                    pdfCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_LEFT);
                    if (cellStyle != null && cellStyle.contains("text-align:right")) {
                        pdfCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                    } else if (cellStyle != null && cellStyle.contains("text-align:center")) {
                        pdfCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                    }
                } else {
                    pdfCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                }
                pdfCell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
                pdfCell.setPadding(noBorder ? 1 : 2);
                pdfCell.setBorderWidth(noBorder ? 0f : 0.5f);
                pdfCell.setBackgroundColor(null);

                String colspanAttr = cell.attr("colspan");
                if (!colspanAttr.isEmpty()) {
                    try {
                        pdfCell.setColspan(Integer.parseInt(colspanAttr));
                    } catch (NumberFormatException ignored) {}
                }

                String rowspanAttr = cell.attr("rowspan");
                if (!rowspanAttr.isEmpty()) {
                    try {
                        pdfCell.setRowspan(Integer.parseInt(rowspanAttr));
                    } catch (NumberFormatException ignored) {}
                }

                if (isHeader && isResultTable) {
                    pdfCell.setBackgroundColor(new java.awt.Color(240, 240, 240));
                }

                table.addCell(pdfCell);
            }
        }

        document.add(table);
    }

    private com.lowagie.text.pdf.PdfPTable buildPdfTable(org.jsoup.nodes.Element tableElem,
            com.lowagie.text.Font defaultFont, float pageWidth, float leftMargin, float rightMargin) throws Exception {
        org.jsoup.select.Elements rows = tableElem.select("tr");
        int maxCols = 0;
        for (org.jsoup.nodes.Element row : rows) {
            int cols = row.select("th,td").size();
            if (cols > maxCols) maxCols = cols;
        }
        if (maxCols == 0) maxCols = 1;

        com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(maxCols);
        table.setWidthPercentage(100);
        table.setTotalWidth(pageWidth - leftMargin - rightMargin);

        for (org.jsoup.nodes.Element row : rows) {
            org.jsoup.select.Elements cells = row.select("th,td");
            int cellCount = cells.size();
            for (int ci = 0; ci < cells.size(); ci++) {
                org.jsoup.nodes.Element cell = cells.get(ci);
                String cellStyle = cell.attr("style");
                float fontSize = parseFontSizeFromStyle(cellStyle, 9f);
                boolean hasBorder = cellStyle != null && cellStyle.contains("border:") && !cellStyle.contains("border:none");
                com.lowagie.text.Font cellFont = new com.lowagie.text.Font(defaultFont.getBaseFont(), fontSize);
                String cellText = cell.text().trim();
                if (cellText.isEmpty()) cellText = " ";
                com.lowagie.text.pdf.PdfPCell pdfCell = new com.lowagie.text.pdf.PdfPCell(
                    new com.lowagie.text.Phrase(cellText, cellFont));
                pdfCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_LEFT);
                pdfCell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
                pdfCell.setPadding(3);
                pdfCell.setFixedHeight(15f);
                if (!hasBorder) {
                    pdfCell.setBorderWidth(0);
                }
                String colspanAttr = cell.attr("colspan");
                if (!colspanAttr.isEmpty()) {
                    try { pdfCell.setColspan(Integer.parseInt(colspanAttr)); } catch (NumberFormatException ignored) {}
                }
                String rowspanAttr = cell.attr("rowspan");
                if (!rowspanAttr.isEmpty()) {
                    try { pdfCell.setRowspan(Integer.parseInt(rowspanAttr)); } catch (NumberFormatException ignored) {}
                }
                if (ci == cells.size() - 1 && cellCount < maxCols) {
                    pdfCell.setColspan(maxCols - cellCount + 1);
                }
                table.addCell(pdfCell);
            }
        }
        return table;
    }

    private int countTableRows(org.jsoup.nodes.Element tableElem) {
        return tableElem.select("tr").size();
    }

    private float parseFontSizeFromStyle(String style, float defaultVal) {
        if (style == null) return defaultVal;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("font-size:\\s*([0-9.]+)pt").matcher(style);
        if (m.find()) {
            try { return Float.parseFloat(m.group(1)); } catch (Exception e) { return defaultVal; }
        }
        m = java.util.regex.Pattern.compile("font-size:\\s*([0-9.]+)px").matcher(style);
        if (m.find()) {
            try { return Float.parseFloat(m.group(1)) * 72 / 96; } catch (Exception e) { return defaultVal; }
        }
        return defaultVal;
    }

    private float parseHeightFromStyle(String style, float defaultVal) {
        if (style == null) return defaultVal;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("height:\\s*([0-9.]+)pt").matcher(style);
        if (m.find()) {
            try { return Float.parseFloat(m.group(1)); } catch (Exception e) { return defaultVal; }
        }
        return defaultVal;
    }

    private void processElementDeep(org.jsoup.nodes.Element elem, com.lowagie.text.Document document,
            com.lowagie.text.pdf.PdfContentByte canvas, com.lowagie.text.Font defaultFont,
            float marginLeft, float marginTop, float pageHeight) throws Exception {

        String style = elem.attr("style");
        String tagName = elem.tagName().toLowerCase();
        boolean isAbsolute = style != null && style.contains("position:absolute");

        if (tagName.equals("hr")) {
            if (isAbsolute) {
                float elemTop = parseCssValue(style, "top", marginTop);
                float elemLeft = parseCssValue(style, "left", marginLeft);
                float elemWidth = parseCssValue(style, "width", 500);
                float y = pageHeight - elemTop;
                canvas.setLineWidth(0.5f);
                canvas.moveTo(elemLeft, y);
                canvas.lineTo(elemLeft + elemWidth, y);
                canvas.stroke();
            } else {
                document.add(new com.lowagie.text.Paragraph("_________________________________________"));
            }
            return;
        }

            if (tagName.equals("div") || tagName.equals("span") || tagName.equals("p")) {
            if (isAbsolute) {
                float elemTop = parseCssValue(style, "top", marginTop);
                float elemLeft = parseCssValue(style, "left", marginLeft);
                float elemWidth = parseCssValue(style, "width", 500);
                float fontSize = parseCssValue(style, "font-size", 12);
                float y = pageHeight - elemTop;

                String text = elem.ownText().trim();
                if (!text.isEmpty()) {
                    com.lowagie.text.Font elemFont = new com.lowagie.text.Font(defaultFont.getBaseFont(), fontSize);
                    canvas.beginText();
                    canvas.setFontAndSize(elemFont.getBaseFont(), fontSize);
                    canvas.showTextAligned(com.lowagie.text.Element.ALIGN_LEFT, text, elemLeft, y, 0);
                    canvas.endText();
                }
                if (elem.children().size() > 0) {
                    float currentX = elemLeft;
                    float currentY = y;
                    for (org.jsoup.nodes.Element child : elem.children()) {
                        float childPos = currentX;
                        String childStyle = child.attr("style");
                        if (childStyle != null && childStyle.contains("position:absolute")) {
                            childPos = elemLeft + parseCssValue(childStyle, "left", 0);
                        }
                        String childTag = child.tagName().toLowerCase();
                        float childY = currentY;
                        if (childTag.equals("table")) {
                            childY = currentY - fontSize - 4;
                        }
                        float childHeight = processElementDeepWithPositionAndGetWidth(child, document, canvas, defaultFont, childPos, childY, pageHeight, fontSize);
                        if (childTag.equals("table")) {
                            currentY = childY - childHeight;
                            currentX = elemLeft;
                        } else {
                            float childWidth = getTextWidth(child, defaultFont, fontSize);
                            currentX = childPos + childWidth + 2;
                            if (childTag.equals("p") || childTag.equals("div")) {
                                currentY = childY - fontSize - 2;
                                currentX = elemLeft;
                            }
                        }
                    }
                }
            } else {
                boolean hasAbsoluteChild = false;
                for (org.jsoup.nodes.Element child : elem.children()) {
                    String childStyle = child.attr("style");
                    if (childStyle != null && childStyle.contains("position:absolute")) {
                        hasAbsoluteChild = true;
                        break;
                    }
                }
                if (!hasAbsoluteChild) {
                    String text = elem.text().trim();
                    if (!text.isEmpty()) {
                        document.add(new com.lowagie.text.Paragraph(text, defaultFont));
                    }
                }
                for (org.jsoup.nodes.Element child : elem.children()) {
                    processElementDeep(child, document, canvas, defaultFont, marginLeft, marginTop, pageHeight);
                }
            }
            return;
        }

        if (tagName.equals("h1")) {
            com.lowagie.text.Font h1Font = new com.lowagie.text.Font(defaultFont.getBaseFont(), 20, com.lowagie.text.Font.BOLD);
            if (isAbsolute) {
                float elemTop = parseCssValue(style, "top", marginTop);
                float elemLeft = parseCssValue(style, "left", marginLeft);
                float elemWidth = parseCssValue(style, "width", 500);
                float y = pageHeight - elemTop;
                canvas.beginText();
                canvas.setFontAndSize(h1Font.getBaseFont(), 20);
                canvas.showTextAligned(com.lowagie.text.Element.ALIGN_CENTER, elem.text(), elemLeft + elemWidth / 2, y, 0);
                canvas.endText();
            } else {
                document.add(new com.lowagie.text.Paragraph(elem.text(), h1Font));
            }
            return;
        }

        if (tagName.equals("h2")) {
            com.lowagie.text.Font h2Font = new com.lowagie.text.Font(defaultFont.getBaseFont(), 16, com.lowagie.text.Font.BOLD);
            if (isAbsolute) {
                float elemTop = parseCssValue(style, "top", marginTop);
                float elemLeft = parseCssValue(style, "left", marginLeft);
                float elemWidth = parseCssValue(style, "width", 500);
                float y = pageHeight - elemTop;
                canvas.beginText();
                canvas.setFontAndSize(h2Font.getBaseFont(), 16);
                canvas.showTextAligned(com.lowagie.text.Element.ALIGN_CENTER, elem.text(), elemLeft + elemWidth / 2, y, 0);
                canvas.endText();
            } else {
                document.add(new com.lowagie.text.Paragraph(elem.text(), h2Font));
            }
            return;
        }

        if (tagName.startsWith("h")) {
            com.lowagie.text.Font hFont = new com.lowagie.text.Font(defaultFont.getBaseFont(), 14, com.lowagie.text.Font.BOLD);
            if (isAbsolute) {
                float elemTop = parseCssValue(style, "top", marginTop);
                float elemLeft = parseCssValue(style, "left", marginLeft);
                float elemWidth = parseCssValue(style, "width", 500);
                float y = pageHeight - elemTop;
                canvas.beginText();
                canvas.setFontAndSize(hFont.getBaseFont(), 14);
                canvas.showTextAligned(com.lowagie.text.Element.ALIGN_LEFT, elem.text(), elemLeft, y, 0);
                canvas.endText();
            } else {
                document.add(new com.lowagie.text.Paragraph(elem.text(), hFont));
            }
            return;
        }

        if (tagName.equals("table")) {
            boolean parentIsAbsolute = false;
            org.jsoup.nodes.Element parent = elem.parent();
            while (parent != null && !parent.tagName().toLowerCase().equals("body")) {
                String parentStyle = parent.attr("style");
                if (parentStyle != null && parentStyle.contains("position:absolute")) {
                    parentIsAbsolute = true;
                    style = parentStyle;
                    break;
                }
                parent = parent.parent();
            }

            if (parentIsAbsolute || isAbsolute) {
                float tableTop = parseCssValue(style, "top", marginTop);
                float tableLeft = parseCssValue(style, "left", marginLeft);
                float tableWidth = parseCssValue(style, "width", 515);
                float y = pageHeight - tableTop;

                java.util.List<String> headers = new java.util.ArrayList<>();
                java.util.List<java.util.List<String>> rows = new java.util.ArrayList<>();

                org.jsoup.select.Elements rowElements = elem.select("tr");
                if (rowElements.isEmpty()) {
                    return;
                }

                org.jsoup.select.Elements firstRowCells = rowElements.first().select("th,td");
                int colCount = firstRowCells.size();
                if (colCount == 0) colCount = 5;
                float colWidth = tableWidth / colCount;

                boolean isHeader = true;
                for (org.jsoup.nodes.Element row : rowElements) {
                    java.util.List<String> rowData = new java.util.ArrayList<>();
                    for (org.jsoup.nodes.Element cell : row.select("th,td")) {
                        String text = cell.text().trim();
                        rowData.add(text);
                        if (isHeader) {
                            headers.add(text);
                        }
                    }
                    if (!isHeader) {
                        rows.add(rowData);
                    }
                    isHeader = false;
                }

                float rowHeight = 20f;
                float headerY = y;
                float dataY = y - rowHeight;

                canvas.setLineWidth(0.5f);

                float currentY = headerY;
                float headerHeight = rowHeight * 0.8f;

                for (int col = 0; col < headers.size(); col++) {
                    float cellX = tableLeft + col * colWidth;
                    canvas.rectangle(cellX, currentY - headerHeight, cellX + colWidth, currentY);
                    canvas.stroke();

                    com.lowagie.text.Font headerFont = new com.lowagie.text.Font(defaultFont.getBaseFont(), 10, com.lowagie.text.Font.BOLD);
                    canvas.beginText();
                    canvas.setFontAndSize(headerFont.getBaseFont(), 10);
                    float textWidth = headerFont.getBaseFont().getWidthPoint(headers.get(col), 10);
                    float textX = cellX + (colWidth - textWidth) / 2;
                    canvas.showTextAligned(com.lowagie.text.Element.ALIGN_LEFT, headers.get(col), textX, currentY - headerHeight / 2 - 3, 0);
                    canvas.endText();
                }

                currentY -= headerHeight;

                for (java.util.List<String> rowData : rows) {
                    float cellHeight = rowHeight;
                    for (int col = 0; col < rowData.size(); col++) {
                        float cellX = tableLeft + col * colWidth;
                        canvas.rectangle(cellX, currentY - cellHeight, cellX + colWidth, currentY);
                        canvas.stroke();

                        com.lowagie.text.Font cellFont = new com.lowagie.text.Font(defaultFont.getBaseFont(), 10);
                        canvas.beginText();
                        canvas.setFontAndSize(cellFont.getBaseFont(), 10);
                        float textWidth = cellFont.getBaseFont().getWidthPoint(rowData.get(col), 10);
                        float textX = cellX + (colWidth - textWidth) / 2;
                        if (textX < cellX) textX = cellX + 2;
                        canvas.showTextAligned(com.lowagie.text.Element.ALIGN_LEFT, rowData.get(col), textX, currentY - cellHeight / 2 - 3, 0);
                        canvas.endText();
                    }
                    currentY -= cellHeight;
                }

                return;
            }

            org.jsoup.nodes.Element headerRow = elem.select("thead tr").first();
            if (headerRow == null) {
                headerRow = elem.select("tr").first();
            }
            int columns = headerRow != null ? headerRow.select("th,td").size() : 5;
            if (columns == 0) columns = 5;

            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(columns);
            table.setWidthPercentage(100);

            float[] columnWidths = new float[columns];
            for (int i = 0; i < columns; i++) {
                columnWidths[i] = 1.0f;
            }
            table.setWidths(columnWidths);

            boolean isHeader = true;
            for (org.jsoup.nodes.Element row : elem.select("tr")) {
                for (org.jsoup.nodes.Element cell : row.select("th,td")) {
                    com.lowagie.text.pdf.PdfPCell pdfCell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(cell.text(), defaultFont));
                    pdfCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                    pdfCell.setVerticalAlignment(com.lowagie.text.Element.ALIGN_MIDDLE);
                    if (isHeader) {
                        pdfCell.setBackgroundColor(new java.awt.Color(245, 245, 245));
                    }
                    table.addCell(pdfCell);
                }
                isHeader = false;
            }
            document.add(table);
            return;
        }

        if (tagName.equals("img")) {
            return;
        }

        String text = elem.text().trim();
        if (!text.isEmpty()) {
            if (isAbsolute) {
                float elemTop = parseCssValue(style, "top", marginTop);
                float elemLeft = parseCssValue(style, "left", marginLeft);
                float y = pageHeight - elemTop;
                canvas.beginText();
                canvas.setFontAndSize(defaultFont.getBaseFont(), 12);
                canvas.showTextAligned(com.lowagie.text.Element.ALIGN_LEFT, text, elemLeft, y, 0);
                canvas.endText();
            } else {
                document.add(new com.lowagie.text.Paragraph(text, defaultFont));
            }
        } else if (elem.children().size() > 0) {

        }
    }
    private float getTextWidth(org.jsoup.nodes.Element elem, com.lowagie.text.Font defaultFont, float fontSize) {
        String text = elem.ownText().trim();
        if (text.isEmpty()) {
            text = elem.text().trim();
        }
        if (text.isEmpty()) {
            return 0;
        }
        return defaultFont.getBaseFont().getWidthPoint(text, fontSize);
    }

    private float processElementDeepWithPositionAndGetWidth(org.jsoup.nodes.Element elem, com.lowagie.text.Document document,
            com.lowagie.text.pdf.PdfContentByte canvas, com.lowagie.text.Font defaultFont,
            float x, float y, float pageHeight, float parentFontSize) throws Exception {

        String style = elem.attr("style");
        String tagName = elem.tagName().toLowerCase();
        boolean isAbsolute = style != null && style.contains("position:absolute");

        float totalWidth = 0;

        if (tagName.equals("span") || tagName.equals("div") || tagName.equals("p")) {
            if (isAbsolute) {
                float elemTop = parseCssValue(style, "top", 0);
                float elemLeft = parseCssValue(style, "left", 0);
                float fontSize = parseCssValue(style, "font-size", parentFontSize);
                float actualY = pageHeight - elemTop;
                String text = elem.ownText().trim();
                if (!text.isEmpty()) {
                    com.lowagie.text.Font elemFont = new com.lowagie.text.Font(defaultFont.getBaseFont(), fontSize);
                    canvas.beginText();
                    canvas.setFontAndSize(elemFont.getBaseFont(), fontSize);
                    canvas.showTextAligned(com.lowagie.text.Element.ALIGN_LEFT, text, x + elemLeft, y, 0);
                    canvas.endText();
                    totalWidth += elemFont.getBaseFont().getWidthPoint(text, fontSize);
                }
                float childX = x + elemLeft;
                for (org.jsoup.nodes.Element child : elem.children()) {
                    float childWidth = processElementDeepWithPositionAndGetWidth(child, document, canvas, defaultFont, childX, y, pageHeight, fontSize);
                    childX += childWidth + 2;
                    totalWidth += childWidth + 2;
                }
            } else {
                String text = elem.ownText().trim();
                if (!text.isEmpty()) {
                    com.lowagie.text.Font elemFont = new com.lowagie.text.Font(defaultFont.getBaseFont(), parentFontSize);
                    canvas.beginText();
                    canvas.setFontAndSize(elemFont.getBaseFont(), parentFontSize);
                    canvas.showTextAligned(com.lowagie.text.Element.ALIGN_LEFT, text, x, y, 0);
                    canvas.endText();
                    totalWidth += elemFont.getBaseFont().getWidthPoint(text, parentFontSize);
                }
                for (org.jsoup.nodes.Element child : elem.children()) {
                    float childWidth = processElementDeepWithPositionAndGetWidth(child, document, canvas, defaultFont, x + totalWidth, y, pageHeight, parentFontSize);
                    totalWidth += childWidth + 2;
                }
            }
            return totalWidth;
        }

        if (tagName.equals("table")) {
            return renderTableWithPosition(elem, canvas, defaultFont, x, y, pageHeight);
        }

        String text = elem.text().trim();
        if (!text.isEmpty()) {
            com.lowagie.text.Font elemFont = new com.lowagie.text.Font(defaultFont.getBaseFont(), parentFontSize);
            canvas.beginText();
            canvas.setFontAndSize(elemFont.getBaseFont(), parentFontSize);
            canvas.showTextAligned(com.lowagie.text.Element.ALIGN_LEFT, text, x, y, 0);
            canvas.endText();
            totalWidth = elemFont.getBaseFont().getWidthPoint(text, parentFontSize);
        }
        return totalWidth;
    }

    private float renderTableWithPosition(org.jsoup.nodes.Element elem, com.lowagie.text.pdf.PdfContentByte canvas,
            com.lowagie.text.Font defaultFont, float x, float y, float pageHeight) throws Exception {

        String style = elem.attr("style");
        float tableWidth = 793f;
        float tableLeft = x;
        float tableTop = y;

        if (style == null || style.isEmpty() || !style.contains("position:absolute")) {
            org.jsoup.nodes.Element parent = elem.parent();
            while (parent != null && !parent.tagName().toLowerCase().equals("body")) {
                String parentStyle = parent.attr("style");
                if (parentStyle != null && parentStyle.contains("position:absolute")) {
                    style = parentStyle;
                    tableLeft = parseCssValue(parentStyle, "left", x);
                    tableTop = y;
                    if (parentStyle.contains("width:")) {
                        tableWidth = parseCssValue(parentStyle, "width", 793f);
                    }
                    break;
                }
                parent = parent.parent();
            }
        }

        if (style != null && style.contains("width:")) {
            tableWidth = parseCssValue(style, "width", 793f);
        }
        x = tableLeft;
        y = tableTop;

        org.jsoup.select.Elements rowElements = elem.select("tr");
        if (rowElements.isEmpty()) {
            return 0;
        }

        org.jsoup.select.Elements firstRowCells = rowElements.first().select("th,td");
        int colCount = firstRowCells.size();
        if (colCount == 0) colCount = 5;
        float colWidth = tableWidth / colCount;

        java.util.List<java.util.List<String>> allRows = new java.util.ArrayList<>();
        for (org.jsoup.nodes.Element row : rowElements) {
            java.util.List<String> rowData = new java.util.ArrayList<>();
            for (org.jsoup.nodes.Element cell : row.select("th,td")) {
                rowData.add(cell.text().trim());
            }
            allRows.add(rowData);
        }

        float rowHeight = 18f;
        float headerHeight = 20f;
        float currentY = y;
        float totalHeight = 0;

        canvas.setLineWidth(0.5f);

        for (int rowIdx = 0; rowIdx < allRows.size(); rowIdx++) {
            java.util.List<String> rowData = allRows.get(rowIdx);
            float cellHeight = (rowIdx == 0) ? headerHeight : rowHeight;
            boolean isHeader = (rowIdx == 0);

            for (int col = 0; col < rowData.size(); col++) {
                float cellX = x + col * colWidth;
                canvas.rectangle(cellX, currentY - cellHeight, cellX + colWidth, currentY);
                canvas.stroke();

                com.lowagie.text.Font cellFont = new com.lowagie.text.Font(defaultFont.getBaseFont(), 9, isHeader ? com.lowagie.text.Font.BOLD : com.lowagie.text.Font.NORMAL);
                canvas.beginText();
                canvas.setFontAndSize(cellFont.getBaseFont(), 9);
                String cellText = rowData.get(col);
                float textWidth = cellFont.getBaseFont().getWidthPoint(cellText, 9);
                float textX = cellX + (colWidth - textWidth) / 2;
                if (textX < cellX + 2) textX = cellX + 2;
                canvas.showTextAligned(com.lowagie.text.Element.ALIGN_LEFT, cellText, textX, currentY - cellHeight / 2 - 3, 0);
                canvas.endText();
            }
            currentY -= cellHeight;
            totalHeight += cellHeight;
        }

        return totalHeight;
    }

    private float parseCssValue(String style, String property, float defaultValue) {
        if (style == null || style.isEmpty()) return defaultValue;
        try {
            String lowerStyle = style.toLowerCase();
            String prop = property.toLowerCase();

            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                prop + "\\s*:\\s*([0-9.]+)(px|mm|pt)?");
            java.util.regex.Matcher matcher = pattern.matcher(lowerStyle);
            if (matcher.find()) {
                float value = Float.parseFloat(matcher.group(1));
                String unit = matcher.group(2);
                if (unit != null && unit.equals("px")) {
                    value = value * 72 / 96;
                } else if (unit != null && unit.equals("mm")) {
                    value = value * 72 / 25.4f;
                }
                return value;
            }
        } catch (Exception e) {
            log.warn("解析CSS属性失败: {} = {}", property, e.getMessage());
        }
        return defaultValue;
    }

    private void processElement(org.jsoup.nodes.Element elem, com.lowagie.text.Document document, com.lowagie.text.Font font) throws Exception {
        String tagName = elem.tagName().toLowerCase();

        switch (tagName) {
            case "h1":
                com.lowagie.text.Font h1Font = new com.lowagie.text.Font(font.getBaseFont(), 20, com.lowagie.text.Font.BOLD);
                document.add(new com.lowagie.text.Paragraph(elem.text(), h1Font));
                break;
            case "h2":
                com.lowagie.text.Font h2Font = new com.lowagie.text.Font(font.getBaseFont(), 16, com.lowagie.text.Font.BOLD);
                document.add(new com.lowagie.text.Paragraph(elem.text(), h2Font));
                break;
            case "h3":
            case "h4":
            case "h5":
            case "h6":
                com.lowagie.text.Font hFont = new com.lowagie.text.Font(font.getBaseFont(), 14, com.lowagie.text.Font.BOLD);
                document.add(new com.lowagie.text.Paragraph(elem.text(), hFont));
                break;
            case "p":
                document.add(new com.lowagie.text.Paragraph(elem.text(), font));
                break;
            case "div":
            case "span":
                if (!elem.text().isEmpty()) {
                    document.add(new com.lowagie.text.Paragraph(elem.text(), font));
                }
                for (org.jsoup.nodes.Element child : elem.children()) {
                    processElement(child, document, font);
                }
                break;
            case "hr":
                document.add(new com.lowagie.text.Paragraph("_________________________________________"));
                break;
            case "table":
                org.jsoup.nodes.Element headerRow = elem.select("thead tr").first();
                if (headerRow == null) {
                    headerRow = elem.select("tr").first();
                }
                int columns = headerRow != null ? headerRow.select("th,td").size() : 5;
                if (columns == 0) columns = 5;
                com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(columns);
                for (org.jsoup.nodes.Element row : elem.select("tr")) {
                    for (org.jsoup.nodes.Element cell : row.select("th,td")) {
                        table.addCell(new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(cell.text(), font)));
                    }
                }
                document.add(table);
                break;
            default:
                if (!elem.text().isEmpty()) {
                    document.add(new com.lowagie.text.Paragraph(elem.text(), font));
                }
                for (org.jsoup.nodes.Element child : elem.children()) {
                    processElement(child, document, font);
                }
                break;
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    public Map<String, Object> createSampleData() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("patientName", "张三");
        data.put("gender", "男");
        data.put("age", "45岁");
        data.put("specimenType", "血液");
        data.put("specimen", "血清");
        data.put("medicalRecordNo", "BL20240001");
        data.put("ward", "内科一病区");
        data.put("sampleBarcode", "BC2024011500123");
        data.put("testNo", "JC20240115001");
        data.put("department", "内科");
        data.put("bedNo", "003");
        data.put("inpatientNo", "2024001234");
        data.put("diagnosis", "常规体检");
        data.put("requestDoctor", "王医生");
        data.put("testItems", "血常规");
        data.put("collectTime", "2024-01-15 08:30");
        data.put("receiveTime", "2024-01-15 09:00");
        data.put("testTime", "2024-01-15 10:00");
        data.put("reportTime", "2024-01-15 11:00");
        data.put("sampler", "刘护士");
        data.put("sampleTime", "2024-01-15 08:30");
        data.put("inspector", "李医生");
        data.put("reviewer", "赵医生");
        data.put("reviewTime", "2024-01-15 10:30");
        data.put("result", "各项指标正常");
        data.put("reportDate", "2024-01-15");
        return mapper.convertValue(data, Map.class);
    }
}
