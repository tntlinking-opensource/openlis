package com.lis.controller;

import com.lis.service.BarcodeService;
import com.lis.service.LabelPrintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.lis.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/barcode")
@Slf4j
public class BarcodeController {

    @Autowired
    private BarcodeService barcodeService;

    @Autowired
    private LabelPrintService labelPrintService;

    @OperationLog(value = "生成条码", module = "条码管理")
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generate() {
        return ResponseEntity.ok(barcodeService.generate());
    }

    @OperationLog(value = "打印标签", module = "条码管理")
    @PostMapping("/print-label")
    public ResponseEntity<Map<String, Object>> printLabel(@RequestBody Map<String, Object> params) {
        List<Integer> brxxIds = (List<Integer>) params.get("brxxIds");
        return ResponseEntity.ok(barcodeService.printLabel(brxxIds));
    }

    @OperationLog(value = "补打标签", module = "条码管理")
    @PostMapping("/reprint")
    public ResponseEntity<Map<String, Object>> reprint(@RequestBody Map<String, Object> params) {
        List<Integer> brxxIds = (List<Integer>) params.get("brxxIds");
        return ResponseEntity.ok(barcodeService.printLabel(brxxIds));
    }

    @GetMapping("/print-pdf/{brxxId}")
    public ResponseEntity<byte[]> printLabelPdf(@PathVariable Integer brxxId) {
        try {
            byte[] pdfBytes = labelPrintService.generateLabelPdf(brxxId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = "label_" + brxxId + ".pdf";
            headers.setContentDispositionFormData("attachment", filename);
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            log.error("生成标签PDF失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        return ResponseEntity.ok(barcodeService.getConfig());
    }

    @GetMapping("/unprinted-samples")
    public ResponseEntity<List<Map<String, Object>>> unprintedSamples(
            @RequestParam(required = false) String brxm,
            @RequestParam(required = false) String syh,
            @RequestParam(required = false) String brxxTmh,
            @RequestParam(required = false) String jyrq) {
        return ResponseEntity.ok(barcodeService.listUnprintedSamples(brxm, syh, brxxTmh, jyrq));
    }
}