package com.lis.service;

import com.lis.mapper.BgxtBrxxMapper;
import com.lis.mapper.SysXtszMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
@Slf4j
public class LabelPrintService {

    @Autowired
    private BgxtBrxxMapper brxxMapper;

    @Autowired
    private SysXtszMapper xtszMapper;

    public byte[] generateLabelPdf(Integer brxxId) {
        try {
            Map<String, Object> data = brxxMapper.selectLabelDataById(brxxId);
            if (data == null) {
                throw new RuntimeException("样本不存在");
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            com.lowagie.text.Document document = new com.lowagie.text.Document(
                com.lowagie.text.PageSize.A4.rotate(), 30, 30, 30, 30);

            com.lowagie.text.pdf.PdfWriter writer = com.lowagie.text.pdf.PdfWriter.getInstance(document, outputStream);
            document.open();

            String hospitalName = getHospitalName();
            String barcode = (String) data.get("brxx_tmh");

            com.lowagie.text.pdf.BaseFont baseFont = getChineseFont();
            if (baseFont == null) {
                baseFont = com.lowagie.text.pdf.BaseFont.createFont();
            }
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(baseFont, 20, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font headerFont = new com.lowagie.text.Font(baseFont, 14, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font normalFont = new com.lowagie.text.Font(baseFont, 12, com.lowagie.text.Font.NORMAL);

            document.add(new com.lowagie.text.Paragraph(hospitalName, titleFont));
            document.add(new com.lowagie.text.Paragraph("检验标本标签", headerFont));
            document.add(new com.lowagie.text.Paragraph(" "));

            String name = (String) data.get("brxm");
            Integer gender = (Integer) data.get("brxb");
            String genderStr = gender != null ? (gender == 1 ? "男" : "女") : "";
            String age = data.get("brnl") != null ? data.get("brnl").toString() : "";

            String dept = (String) data.get("ksmc");
            String bed = (String) data.get("brch");
            String sampleType = (String) data.get("bbzl");
            String sampleNo = (String) data.get("syh");
            String dateStr = "";
            Object jyrq = data.get("jyrq");
            if (jyrq != null) {
                if (jyrq instanceof Date) {
                    dateStr = new SimpleDateFormat("yyyy-MM-dd").format((Date) jyrq);
                } else {
                    dateStr = jyrq.toString().substring(0, 10);
                }
            }

            document.add(new com.lowagie.text.Paragraph("条码号: " + (barcode != null ? barcode : ""), headerFont));
            document.add(new com.lowagie.text.Paragraph("姓名: " + (name != null ? name : "") + "  性别: " + genderStr + "  年龄: " + age, normalFont));
            document.add(new com.lowagie.text.Paragraph("科室: " + (dept != null ? dept : ""), normalFont));
            document.add(new com.lowagie.text.Paragraph("床号: " + (bed != null ? bed : "") + "  标本: " + (sampleType != null ? sampleType : ""), normalFont));
            document.add(new com.lowagie.text.Paragraph("样本号: " + (sampleNo != null ? sampleNo : "") + "  日期: " + dateStr, normalFont));
            document.add(new com.lowagie.text.Paragraph(" "));

            com.lowagie.text.pdf.PdfContentByte canvas = writer.getDirectContent();

            if (barcode != null && !barcode.isEmpty()) {
                com.lowagie.text.pdf.Barcode128 barcode128 = new com.lowagie.text.pdf.Barcode128();
                barcode128.setCode(barcode);
                barcode128.setFont(null);
                barcode128.setBarHeight(40f);
                barcode128.setX(1.5f);
                com.lowagie.text.Image barcodeImage = barcode128.createImageWithBarcode(canvas, null, null);
                barcodeImage.setAbsolutePosition(100, 100);
                barcodeImage.scaleToFit(200, 50);
                document.add(barcodeImage);
            }

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("生成标签PDF失败", e);
            throw new RuntimeException("生成标签PDF失败：" + e.getMessage());
        }
    }

    private String getHospitalName() {
        try {
            String name = xtszMapper.selectHospitalName();
            return name != null ? name : "医院名称";
        } catch (Exception e) {
            return "医院名称";
        }
    }

    private com.lowagie.text.pdf.BaseFont getChineseFont() {
        try {
            return com.lowagie.text.pdf.BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", com.lowagie.text.pdf.BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            log.warn("获取中文字体失败", e);
            try {
                return com.lowagie.text.pdf.BaseFont.createFont();
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
