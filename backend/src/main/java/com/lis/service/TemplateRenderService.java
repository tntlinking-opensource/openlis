package com.lis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lis.mapper.ReportTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Service
@Slf4j
public class TemplateRenderService {

    @Autowired
    private ReportTemplateMapper templateMapper;

    @Autowired
    private SpecimenTypeService specimenTypeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String renderReport(Integer templateId, Map<String, Object> reportData) {
        Map<String, Object> template = null;
        String searchReason = "";

        if (templateId != null) {
            template = templateMapper.getById(templateId);
            if (template == null) {
                return "<div style='padding:40px;text-align:center;color:#e74c3c;font-size:16px;'>错误：指定的模板不存在（templateId=" + templateId + "）</div>";
            }
            searchReason = "指定模板";
        } else {
            Object reportTypeObj = reportData.get("reportType");
            String bbzlCode = (String) reportData.get("bbzlCode");
            Object sbDjidObj = reportData.get("sb_djid");
            Integer sbDjid = null;
            if (sbDjidObj != null) {
                if (sbDjidObj instanceof Number) {
                    sbDjid = ((Number) sbDjidObj).intValue();
                } else {
                    try { sbDjid = Integer.parseInt(sbDjidObj.toString()); } catch (NumberFormatException ignored) {}
                }
            }
            String bgbh = (String) reportData.get("bgbh");
            String bgmc = (String) reportData.get("bgmc");

            // 优先级1: 组合项目ReportType匹配（对应Delphi: isnull(@ReportType,sb.bblb)=bggs.bgid）
            if (template == null && reportTypeObj != null) {
                String reportTypeCode = "RT_" + reportTypeObj;
                template = templateMapper.getTemplateByCode(reportTypeCode);
                if (template != null) {
                    searchReason = "组合项目ReportType匹配(reportType=" + reportTypeObj + ")";
                    log.info("报告模板匹配成功: {}, brxxId={}", searchReason, reportData.get("brxx_id"));
                }
            }

            // 优先级2: 报告编号+名称匹配
            if (template == null && bgbh != null && !bgbh.isEmpty() && bgmc != null && !bgmc.isEmpty()) {
                template = templateMapper.getTemplateByBgbhBgmc(bgbh, bgmc);
                if (template != null) {
                    searchReason = "报告编号+名称匹配(bgbh=" + bgbh + ",bgmc=" + bgmc + ")";
                }
            }

            // 优先级3: 仪器+标本类型匹配
            if (template == null && sbDjid != null && bbzlCode != null) {
                template = templateMapper.getTemplateBySbDjidAndBbzl(sbDjid, bbzlCode);
                if (template != null) {
                    searchReason = "仪器+标本类型匹配(sbDjid=" + sbDjid + ",bbzl=" + bbzlCode + ")";
                }
            }

            // 优先级4: 仪器专用模板（对应Delphi: sb.bblb作为默认报告类型）
            if (template == null && sbDjid != null) {
                template = templateMapper.getTemplateBySbDjid(sbDjid);
                if (template != null) {
                    searchReason = "仪器专用模板匹配(sbDjid=" + sbDjid + ")";
                }
            }

            // 优先级5: 标本类型匹配
            if (template == null && bbzlCode != null) {
                template = templateMapper.getTemplateByBbzl(bbzlCode);
                if (template != null) {
                    searchReason = "标本类型匹配(bbzl=" + bbzlCode + ")";
                }
            }

            // 优先级6: 系统默认模板兜底
            if (template == null) {
                template = templateMapper.getDefaultTemplate();
                if (template != null) {
                    searchReason = "系统默认模板兜底";
                    log.warn("报告模板兜底匹配: 使用系统默认模板, brxxId={}", reportData.get("brxx_id"));
                }
            }
        }

        if (template == null) {
            String bbzlCode = (String) reportData.get("bbzlCode");
            return "<div style='padding:40px;text-align:center;color:#e74c3c;font-size:16px;'>请先在检验报告模板设计器中创建对应标本类型【" + (bbzlCode != null ? bbzlCode : "未知") + "】的模板</div>";
        }

        String htmlContent = (String) template.get("htmlContent");
        String config = (String) template.get("config");
        String templateType = (String) template.get("templateType");
        String templateName = (String) template.get("templateName");

        if (htmlContent != null && !htmlContent.isEmpty()) {
            try {
                return renderFromHtmlContent(htmlContent, reportData);
            } catch (Exception e) {
                e.printStackTrace();
                return "<div style='padding:40px;text-align:center;color:#e74c3c;font-size:16px;'>模板渲染失败：" + templateName + " - " + e.getMessage() + "</div>";
            }
        }

        try {
            JsonNode templateConfig = objectMapper.readTree(config);
            String html = renderByType(templateType, templateConfig, reportData);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
            return "<div style='padding:40px;text-align:center;color:#e74c3c;font-size:16px;'>模板渲染失败：" + templateName + " - " + e.getMessage() + "</div>";
        }
    }

    public String renderFromHtmlContentDirect(String htmlContent, Map<String, Object> reportData) {
        try {
            return renderFromHtmlContent(htmlContent, reportData);
        } catch (Exception e) {
            log.error("renderFromHtmlContentDirect失败", e);
            return "<div style='padding:40px;text-align:center;color:#e74c3c;font-size:16px;'>模板渲染失败：" + e.getMessage() + "</div>";
        }
    }

    private String renderFromHtmlContent(String htmlContent, Map<String, Object> reportData) throws Exception {
        Map<String, Object> parsed = objectMapper.readValue(htmlContent, Map.class);
        Object componentsObj = parsed.get("components");
        if (componentsObj == null) {
            return "<div style='padding:40px;text-align:center;color:#e74c3c;font-size:16px;'>模板格式错误：缺少components</div>";
        }

        List<Map<String, Object>> components = (List<Map<String, Object>>) componentsObj;
        if (components.isEmpty()) {
            return "<div style='padding:40px;text-align:center;color:#e74c3c;font-size:16px;'>模板为空</div>";
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><style>");
        html.append(getCompReportCss());
        html.append("</style></head><body>");
        html.append("<div style='position:relative;width:793px;min-height:1123px;margin:0 auto;'>");

        List<Map<String, Object>> sortedComponents = new ArrayList<>(components);
        sortedComponents.sort((a, b) -> {
            int ta = a.get("top") != null ? ((Number) a.get("top")).intValue() : 0;
            int tb = b.get("top") != null ? ((Number) b.get("top")).intValue() : 0;
            return Integer.compare(ta, tb);
        });

        for (Map<String, Object> comp : sortedComponents) {
            String type = (String) comp.get("type");
            if (type == null) continue;

            int top = comp.get("top") != null ? ((Number) comp.get("top")).intValue() : 0;
            int left = comp.get("left") != null ? ((Number) comp.get("left")).intValue() : 0;
            int width = comp.get("width") != null ? ((Number) comp.get("width")).intValue() : 793;
            int fontSize = comp.get("fontSize") != null ? ((Number) comp.get("fontSize")).intValue() : 12;
            String align = (String) comp.getOrDefault("align", "left");

            switch (type) {
                case "hospitalHeader":
                    String text = (String) comp.get("text");
                    html.append("<div style='position:absolute;top:").append(top).append("px;left:").append(left).append("px;width:").append(width).append("px;font-size:").append(fontSize).append("px;text-align:").append(align).append(";font-weight:bold;'>").append(text != null ? text : "").append("</div>");
                    break;
                case "hospitalInfo":
                    String info = (String) comp.get("text");
                    html.append("<div style='position:absolute;top:").append(top).append("px;left:").append(left).append("px;width:").append(width).append("px;font-size:").append(fontSize).append("px;text-align:").append(align).append(";color:#888;'>").append(info != null ? info : "").append("</div>");
                    break;
                case "divider":
                    html.append("<div style='position:absolute;top:").append(top).append("px;left:").append(left).append("px;width:").append(width).append("px;'><hr style='border:none;border-top:1px solid #333;margin:0;'/></div>");
                    break;
                case "field":
                    String label = (String) comp.get("label");
                    String field = (String) comp.get("field");
                    Boolean showLabel = (Boolean) comp.getOrDefault("showLabel", true);
                    String value = getFieldValue(reportData, field);
                    html.append("<div style='position:absolute;top:").append(top).append("px;left:").append(left).append("px;width:").append(width).append("px;font-size:").append(fontSize).append("px;white-space:nowrap;overflow:hidden;'>");
                    if (showLabel != null && showLabel && label != null) {
                        html.append("<span style='color:#333;'>").append(label).append("：</span>");
                    }
                    html.append("<span style='font-weight:500;'>").append(value).append("</span></div>");
                    break;
                case "resultTable":
                    html.append("<div style='position:absolute;top:").append(top).append("px;left:").append(left).append("px;width:").append(width).append("px;'>");
                    html.append(renderResultTableFromComponents(reportData));
                    html.append("</div>");
                    break;
                case "signLinePair":
                    String leftLabel = (String) comp.get("leftLabel");
                    String rightLabel = (String) comp.get("rightLabel");
                    html.append("<div style='position:absolute;top:").append(top).append("px;left:").append(left).append("px;width:").append(width).append("px;display:flex;justify-content:space-between;'>");
                    html.append("<div style='text-align:center;'><div style='width:120px;border-bottom:1px solid #333;margin-bottom:2px;'></div><span style='font-size:10px;color:#666;'>").append(leftLabel != null ? leftLabel : "").append("</span></div>");
                    html.append("<div style='text-align:center;'><div style='width:120px;border-bottom:1px solid #333;margin-bottom:2px;'></div><span style='font-size:10px;color:#666;'>").append(rightLabel != null ? rightLabel : "").append("</span></div>");
                    html.append("</div>");
                    break;
                case "footerNote":
                    String note = (String) comp.get("text");
                    html.append("<div style='position:absolute;top:").append(top).append("px;left:").append(left).append("px;width:").append(width).append("px;font-size:11px;color:#333;'>").append(note != null ? note : "").append("</div>");
                    break;
                case "footerInfo":
                    String footerInfo = (String) comp.get("text");
                    html.append("<div style='position:absolute;top:").append(top).append("px;left:").append(left).append("px;width:").append(width).append("px;font-size:10px;color:#666;text-align:center;'>").append(footerInfo != null ? footerInfo : "").append("</div>");
                    break;
            }
        }

        html.append("</div></body></html>");
        return html.toString();
    }

     private String renderResultTableFromComponents(Map<String, Object> reportData) {
         StringBuilder html = new StringBuilder();
         html.append("<table style='width:100%;border-collapse:collapse;font-size:12px;'>");
         html.append("<thead><tr style='background:#f0f0f0;'>");
         html.append("<th style='border:none;border-bottom:1px solid #333;padding:4px 6px;text-align:center;width:40px;'>序号</th>");
         html.append("<th style='border:none;border-bottom:1px solid #333;padding:4px 6px;text-align:left;'>项目名称</th>");
         html.append("<th style='border:none;border-bottom:1px solid #333;padding:4px 6px;text-align:center;'>结果</th>");
         html.append("<th style='border:none;border-bottom:1px solid #333;padding:4px 6px;text-align:center;'>单位</th>");
         html.append("<th style='border:none;border-bottom:1px solid #333;padding:4px 6px;text-align:center;'>参考区间</th>");
         html.append("<th style='border:none;border-bottom:1px solid #333;padding:4px 6px;text-align:center;width:40px;'>标志</th>");
         html.append("</tr></thead><tbody>");

         List<Map<String, Object>> results = getResults(reportData);
         int idx = 0;
         for (Map<String, Object> item : results) {
             idx++;
             String name = getItemValue(item, "xmzwmc");
              String result = getItemValue(item, "jyjg");
              String unit = getItemValue(item, "jldw");
              String ref = getItemValue(item, "ckz");
              String[] flagInfo = getResultFlagWithDir(item, result, ref);
              String flagDir = flagInfo[0], flagSym = flagInfo[1];
              String flagColor = "";
              if ("H".equals(flagDir)) flagColor = "color:#d32f2f;";
              else if ("L".equals(flagDir)) flagColor = "color:#1976d2;";

              html.append("<tr>");
              html.append("<td style='border:none;padding:3px 6px;text-align:center;'>").append(idx).append("</td>");
              html.append("<td style='border:none;padding:3px 6px;'>").append(name).append("</td>");
              html.append("<td style='border:none;padding:3px 6px;text-align:center;font-weight:bold;").append(flagColor).append("'>").append(result).append("</td>");
              html.append("<td style='border:none;padding:3px 6px;text-align:center;'>").append(unit).append("</td>");
              html.append("<td style='border:none;padding:3px 6px;text-align:center;'>").append(ref).append("</td>");
              html.append("<td style='border:none;padding:3px 6px;text-align:center;font-weight:bold;").append(flagColor).append("'>").append(flagSym.isEmpty() ? "" : flagSym).append("</td>");
              html.append("</tr>");
         }
         html.append("</tbody></table>");
         return html.toString();
     }

    private String getFieldValue(Map<String, Object> data, String field) {
        if (field == null || field.isEmpty()) return "";
        String value = null;
        switch (field) {
            case "patientName": value = getPatientValue(data, "brxm"); break;
            case "gender": value = getPatientValue(data, "brxb"); break;
            case "age": value = getPatientValue(data, "brnl"); break;
            case "department": value = getPatientValue(data, "ksdm"); break;
            case "bedNo": value = getPatientValue(data, "brch"); break;
            case "sampleBarcode": value = getPatientValue(data, "brxx_tmh"); break;
            case "specimen": value = getPatientValue(data, "bbzl"); break;
            case "collectTime": value = getPatientValue(data, "jyrq"); break;
            case "inspector": value = getPatientValue(data, "jyys"); break;
            case "reviewer": value = getPatientValue(data, "shys"); break;
            case "testNo": value = getPatientValue(data, "syh"); break;
            case "medicalRecordNo": value = getPatientValue(data, "brbh"); break;
            case "ward": value = getPatientValue(data, "brch"); break;
            case "diagnosis": value = getPatientValue(data, "lczd"); break;
            case "testItems": value = getPatientValue(data, "bgbt"); break;
            case "requestDoctor": value = getPatientValue(data, "jyys"); break;
            case "sampleTime": value = getPatientValue(data, "jyrq"); break;
            case "receiveTime": value = getPatientValue(data, "jyrq"); break;
            case "reportTime": value = getPatientValue(data, "shrq"); break;
            case "reportDate": value = getPatientValue(data, "shrq"); break;
            case "result": value = getPatientValue(data, "result"); break;
            default: break;
        }
        if ((value == null || value.isEmpty()) && data.containsKey(field)) {
            Object v = data.get(field);
            value = v != null ? v.toString() : "";
        }
        return value != null ? value : "";
    }

    private String renderByType(String type, JsonNode config, Map<String, Object> data) {
        switch (type) {
            case "single_col":
                return renderSingleColReport(config, data);
            case "double_col":
                return renderDoubleColReport(config, data);
            case "chart":
                return renderChartReport(config, data);
            default:
                return renderSingleColReport(config, data);
        }
    }

    private String renderSingleColReport(JsonNode config, Map<String, Object> data) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><style>");
        html.append(getReportCss());
        html.append("</style></head><body>");

        html.append("<div class='report-container'>");
        html.append("<div class='report-header'>");
        html.append("<div class='hospital-name'>").append(getConfigValue(config, "hospitalName", "医院名称")).append("</div>");
        String title1 = data.get("bgbt") != null ? data.get("bgbt").toString() : getConfigValue(config, "reportTitle", "检验报告单");
        html.append("<div class='report-title'>").append(title1.isEmpty() ? getConfigValue(config, "reportTitle", "检验报告单") : title1).append("</div>");
        html.append("</div>");

        html.append("<table class='patient-info'>");
        html.append("<tr><td class='label'>姓名:</td><td>").append(getPatientValue(data, "brxm")).append("</td>");
        html.append("<td class='label'>性别:</td><td>").append(getPatientValue(data, "brxb")).append("</td>");
        html.append("<td class='label'>年龄:</td><td>").append(getPatientValue(data, "brnl")).append("</td>");
        html.append("<td class='label'>科室:</td><td>").append(getPatientValue(data, "ksdm")).append("</td></tr>");
        html.append("<tr><td class='label'>样本号:</td><td>").append(getPatientValue(data, "syh")).append("</td>");
        html.append("<td class='label'>标本:</td><td>").append(getPatientValue(data, "bbzl")).append("</td>");
        html.append("<td class='label'>床号:</td><td>").append(getPatientValue(data, "brch")).append("</td>");
        html.append("<td class='label'>送检医生:</td><td>").append(getPatientValue(data, "sjys")).append("</td></tr>");
        html.append("<tr><td class='label'>检验日期:</td><td>").append(getPatientValue(data, "jyrq")).append("</td>");
        html.append("<td class='label'>审核日期:</td><td>").append(getPatientValue(data, "shrq")).append("</td>");
        html.append("<td class='label'>审核医生:</td><td>").append(getPatientValue(data, "shys")).append("</td>");
        html.append("<td></td><td></td></tr>");
        html.append("</table>");

        html.append("<table class='result-table'>");
        html.append("<thead><tr>");
        html.append("<th>项目名称</th><th>结果</th><th>参考值</th><th>单位</th><th>标志</th>");
        html.append("</tr></thead><tbody>");

        List<Map<String, Object>> results = getResults(data);
         for (Map<String, Object> item : results) {
             String result = getItemValue(item, "jyjg");
             String ref = getItemValue(item, "ckz");
             String[] flagInfo = getResultFlagWithDir(item, result, ref);
             String flagDir = flagInfo[0], flagSym = flagInfo[1];

             html.append("<tr>");
             html.append("<td>").append(getItemValue(item, "xmzwmc")).append("</td>");
             html.append("<td class='result-value").append(flagDir.isEmpty() ? "" : " flag-" + flagDir.toLowerCase()).append("'>").append(result).append("</td>");
             html.append("<td>").append(ref).append("</td>");
             html.append("<td>").append(getItemValue(item, "jldw")).append("</td>");
             html.append("<td class='flag'>").append(flagSym).append("</td>");
             html.append("</tr>");
         }

         html.append("</tbody></table>");

         html.append("<div class='report-footer'>");
         html.append("<div class='footer-info'>");
         String footer1 = data.get("bgyj") != null ? data.get("bgyj").toString() : "";
        if (!footer1.isEmpty()) {
            html.append("<span>").append(footer1).append("</span>");
        }
        html.append("<span>打印时间: ").append(new Date()).append("</span>");
        html.append("</div>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }

    private String renderDoubleColReport(JsonNode config, Map<String, Object> data) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><style>");
        html.append(getReportCss());
        html.append("</style></head><body>");

        html.append("<div class='report-container'>");
        html.append("<div class='report-header'>");
        html.append("<div class='hospital-name'>").append(getConfigValue(config, "hospitalName", "医院名称")).append("</div>");
        String title2 = data.get("bgbt") != null ? data.get("bgbt").toString() : getConfigValue(config, "reportTitle", "检验报告单");
        html.append("<div class='report-title'>").append(title2.isEmpty() ? getConfigValue(config, "reportTitle", "检验报告单") : title2).append("</div>");
        html.append("</div>");

        html.append("<table class='patient-info'>");
        html.append("<tr><td class='label'>姓名:</td><td>").append(getPatientValue(data, "brxm")).append("</td>");
        html.append("<td class='label'>性别:</td><td>").append(getPatientValue(data, "brxb")).append("</td>");
        html.append("<td class='label'>年龄:</td><td>").append(getPatientValue(data, "brnl")).append("</td>");
        html.append("<td class='label'>科室:</td><td>").append(getPatientValue(data, "ksdm")).append("</td></tr>");
        html.append("<tr><td class='label'>样本号:</td><td>").append(getPatientValue(data, "syh")).append("</td>");
        html.append("<td class='label'>标本:</td><td>").append(getPatientValue(data, "bbzl")).append("</td>");
        html.append("<td class='label'>床号:</td><td>").append(getPatientValue(data, "brch")).append("</td>");
        html.append("<td class='label'>送检医生:</td><td>").append(getPatientValue(data, "sjys")).append("</td></tr>");
        html.append("</table>");

        html.append("<table class='result-table'>");
        html.append("<thead><tr>");
        html.append("<th>项目名称</th><th>结果</th><th>参考值</th><th>单位</th><th>标志</th>");
        html.append("</tr></thead><tbody>");

         List<Map<String, Object>> results = getResults(data);
         for (Map<String, Object> item : results) {
             String result = getItemValue(item, "jyjg");
             String ref = getItemValue(item, "ckz");
             String[] flagInfo = getResultFlagWithDir(item, result, ref);
             String flagDir = flagInfo[0], flagSym = flagInfo[1];

             html.append("<tr>");
             html.append("<td>").append(getItemValue(item, "xmzwmc")).append("</td>");
             html.append("<td class='result-value").append(flagDir.isEmpty() ? "" : " flag-" + flagDir.toLowerCase()).append("'>").append(result).append("</td>");
             html.append("<td>").append(ref).append("</td>");
             html.append("<td>").append(getItemValue(item, "jldw")).append("</td>");
             html.append("<td class='flag'>").append(flagSym).append("</td>");
             html.append("</tr>");
         }

         html.append("</tbody></table>");

         html.append("<div class='report-footer'>");
        html.append("<div class='footer-info'>");
        String footer2 = data.get("bgyj") != null ? data.get("bgyj").toString() : "";
        if (!footer2.isEmpty()) {
            html.append("<span>").append(footer2).append("</span>");
        }
        html.append("<span>打印时间: ").append(new Date()).append("</span>");
        html.append("</div>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }

    private String renderChartReport(JsonNode config, Map<String, Object> data) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>");
        html.append(getReportCss());
        html.append(".chart-container { display: flex; justify-content: space-around; margin: 20px 0; }");
        html.append(".chart-box { text-align: center; }");
        html.append(".chart-box canvas { max-width: 200px; }");
        html.append("</style>");
        html.append("</head><body>");

        html.append("<div class='report-container'>");
        html.append(renderSingleColReport(config, data));

        html.append("<div class='chart-container'>");
        html.append("<div class='chart-box'><canvas id='wbcChart'></canvas><div>WBC直方图</div></div>");
        html.append("<div class='chart-box'><canvas id='rbcChart'></canvas><div>RBC直方图</div></div>");
        html.append("<div class='chart-box'><canvas id='pltChart'></canvas><div>PLT直方图</div></div>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }

    private String renderDefaultReport(Map<String, Object> data) {
        return renderSingleColReport(null, data);
    }

    private String getCompReportCss() {
        return """
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { font-family: 'SimSun', '宋体', serif; font-size: 14px; }
            """;
    }

    private String getReportCss() {
        return """
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { font-family: 'SimSun', '宋体', serif; font-size: 14px; }
            .report-container { max-width: 800px; margin: 0 auto; padding: 20px; }
            .report-header { text-align: center; margin-bottom: 20px; border-bottom: 2px solid #333; padding-bottom: 15px; }
            .hospital-name { font-size: 20px; font-weight: bold; margin-bottom: 10px; }
            .report-title { font-size: 18px; font-weight: bold; }
            .patient-info { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
            .patient-info td { padding: 5px 10px; border: 1px solid #ddd; }
            .patient-info .label { font-weight: bold; background-color: #f5f5f5; width: 80px; }
            .result-table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }
            .result-table th, .result-table td { border: 1px solid #333; padding: 8px; text-align: center; }
            .result-table th { background-color: #f0f0f0; font-weight: bold; }
            .result-value { font-weight: bold; }
            .flag { font-weight: bold; }
            .flag-H { color: #d32f2f; }
            .flag-L { color: #1976d2; }
            .flag-P { color: #388e3c; }
            .report-footer { margin-top: 30px; border-top: 1px solid #ddd; padding-top: 15px; }
            .footer-info { display: flex; justify-content: space-between; }
            """;
    }

    private String getConfigValue(JsonNode config, String key, String defaultValue) {
        if (config == null || !config.has(key)) {
            return defaultValue;
        }
        return config.get(key).asText(defaultValue);
    }

    private String getPatientValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value == null) return "";
        String strValue = value.toString();
        if ("brxb".equals(key)) {
            return "1".equals(strValue) ? "男" : "女";
        }
        if ("jyrq".equals(key) || "shrq".equals(key)) {
            if (strValue.length() > 19) {
                return strValue.substring(0, 19);
            }
        }
        return strValue;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getResults(Map<String, Object> data) {
        Object results = data.get("results");
        if (results instanceof List) {
            return (List<Map<String, Object>>) results;
        }
        return new ArrayList<>();
    }

    private String getItemValue(Map<String, Object> item, String key) {
        Object value = item.get(key);
        return value == null ? "" : value.toString();
    }

    private Map<String, Object> getHighLowFlagConfig() {
        try {
            return specimenTypeService.getActiveHighLowFlag();
        } catch (Exception e) {
            return null;
        }
    }

    private String[] getResultFlagWithDir(Map<String, Object> item, String result, String ref) {
        if (result == null || result.isEmpty() || ref == null || ref.isEmpty()) {
            return new String[]{"", ""};
        }
        try {
            double resultVal = Double.parseDouble(result);
            String[] refParts = ref.split("-");
            if (refParts.length == 2) {
                double low = Double.parseDouble(refParts[0].trim());
                double high = Double.parseDouble(refParts[1].trim());
                Map<String, Object> flagConfig = getHighLowFlagConfig();
                String highSym = "H", lowSym = "L";
                if (flagConfig != null) {
                    if (flagConfig.get("high") != null) highSym = flagConfig.get("high").toString();
                    if (flagConfig.get("low") != null) lowSym = flagConfig.get("low").toString();
                }
                if (resultVal > high) return new String[]{"H", highSym};
                if (resultVal < low) return new String[]{"L", lowSym};
            }
        } catch (NumberFormatException e) {
            return new String[]{"", ""};
        }
        return new String[]{"", ""};
    }
}