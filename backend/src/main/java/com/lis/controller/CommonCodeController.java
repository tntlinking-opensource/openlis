package com.lis.controller;

import com.lis.entity.SysTybmzb;
import com.lis.entity.SysTybmmx;
import com.lis.service.BaseSettingService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/system/common-code", "/common/code"})
@Slf4j
public class CommonCodeController {

    @Autowired
    private BaseSettingService baseSettingService;

    @GetMapping("/main/list")
    public ResponseEntity<List<Map<String, Object>>> getMainList() {
        try {
            return ResponseEntity.ok(baseSettingService.getCommonCodeMainList());
        } catch (Exception e) {
            log.error("操作失败", e);
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Object>> getCodeList() {
        return ResponseEntity.ok(baseSettingService.getCommonCodeList());
    }

    @GetMapping("/detail/list")
    public ResponseEntity<List<SysTybmmx>> getDetailList(@RequestParam Integer bmdm) {
        return ResponseEntity.ok(baseSettingService.getCommonCodeDetailList(bmdm));
    }

    @PostMapping("/main/save")
    public ResponseEntity<ApiResponse> saveMain(@RequestBody SysTybmzb entity) {
        try {
            baseSettingService.saveCommonCodeMain(entity);
            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @PostMapping("/detail/save")
    public ResponseEntity<ApiResponse> saveDetail(@RequestBody SaveDetailRequest request) {
        try {
            SysTybmmx result = baseSettingService.saveCommonCodeDetail(request.getDetail());
            ApiResponse response = ApiResponse.success("保存成功");
            response.setData(result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/detail/{id}")
    public ResponseEntity<ApiResponse> deleteDetail(@PathVariable Integer id) {
        try {
            baseSettingService.deleteCommonCodeDetail(id);
            return ResponseEntity.ok(ApiResponse.success("删除成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("删除失败：" + e.getMessage()));
        }
    }

    @PostMapping("/migrate-from-sqlserver")
    public ResponseEntity<ApiResponse> migrateFromSqlServer() {
        return ResponseEntity.ok(ApiResponse.success("迁移功能需要配置SQL Server数据源"));
    }

    @PostMapping("/debug/init-test-data")
    @Transactional
    public ResponseEntity<ApiResponse> initTestData() {
        return ResponseEntity.ok(ApiResponse.success("调试功能已迁移到Service"));
    }

    @Data
    public static class SaveDetailRequest {
        private SysTybmmx detail;
        private String type;
    }

    @Data
    public static class ApiResponse {
        private Boolean success;
        private String message;
        private Object data;

        public static ApiResponse success(String message) {
            ApiResponse response = new ApiResponse();
            response.setSuccess(true);
            response.setMessage(message);
            return response;
        }

        public static ApiResponse fail(String message) {
            ApiResponse response = new ApiResponse();
            response.setSuccess(false);
            response.setMessage(message);
            return response;
        }
    }
}
