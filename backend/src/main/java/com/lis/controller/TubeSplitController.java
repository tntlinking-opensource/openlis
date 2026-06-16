package com.lis.controller;

import com.lis.dto.TubeCategoryRequest;
import com.lis.dto.TubeSubcategoryRequest;
import com.lis.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.lis.annotation.OperationLog;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tube-split")
@Slf4j
public class TubeSplitController {

    @Autowired
    private SystemService systemService;

    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, Object>>> categories() {
        return ResponseEntity.ok(systemService.listTubeCategories());
    }

    @OperationLog(value = "保存分管大类", module = "分管设置")
    @PostMapping("/category/save")
    public ResponseEntity<Map<String, Object>> saveCategory(@RequestBody TubeCategoryRequest data) {
        Map<String, Object> result = new HashMap<>();
        try {
            systemService.saveTubeCategory(data);
            result.put("success", true);
            result.put("message", "保存成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "保存失败：" + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/subcategories")
    public ResponseEntity<List<Map<String, Object>>> subcategories(@RequestParam(required = false) Integer dlid) {
        return ResponseEntity.ok(systemService.listTubeSubcategories(dlid));
    }

    @OperationLog(value = "保存分管小类", module = "分管设置")
    @PostMapping("/subcategory/save")
    public ResponseEntity<Map<String, Object>> saveSubcategory(@RequestBody TubeSubcategoryRequest data) {
        Map<String, Object> result = new HashMap<>();
        try {
            systemService.saveTubeSubcategory(data);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/combo-items")
    public ResponseEntity<List<Map<String, Object>>> comboItems(@RequestParam Integer xlbh) {
        return ResponseEntity.ok(systemService.listComboItemsBySubcategory(xlbh));
    }

    @GetMapping("/available-combo-items")
    public ResponseEntity<List<Map<String, Object>>> availableComboItems(@RequestParam Integer xlbh) {
        return ResponseEntity.ok(systemService.listAvailableComboItems(xlbh));
    }

    @OperationLog(value = "保存组合映射", module = "分管设置")
    @PostMapping("/combo-mapping/save")
    public ResponseEntity<Map<String, Object>> saveComboMapping(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer xlbh = (Integer) params.get("xlbh");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
            systemService.saveComboMapping(xlbh, items);
            result.put("success", true);
            result.put("message", "保存成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "保存失败：" + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @OperationLog(value = "删除组合映射", module = "分管设置")
    @DeleteMapping("/combo-mapping")
    public ResponseEntity<Map<String, Object>> removeComboMapping(@RequestParam Integer xlbh, @RequestParam Integer zhid) {
        Map<String, Object> result = new HashMap<>();
        try {
            systemService.removeComboMapping(xlbh, zhid);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/batch-add-by-instrument")
    public ResponseEntity<Map<String, Object>> batchAddByInstrument(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer xlbh = (Integer) params.get("xlbh");
            Integer sbDjid = (Integer) params.get("sbDjid");
            if (xlbh == null || sbDjid == null) {
                result.put("success", false);
                result.put("message", "参数错误");
                return ResponseEntity.ok(result);
            }
            int count = systemService.batchAddComboByInstrument(xlbh, sbDjid);
            result.put("success", true);
            result.put("message", "成功添加 " + count + " 个项目");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量添加失败：" + e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
}