package com.lis.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.BgxtXtsz;
import com.lis.entity.SysXtsz;
import com.lis.mapper.BgxtXtszMapper;
import com.lis.mapper.SysXtszMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 6.1 工程师系统设置控制器
 * 对应旧系统工程师系统设置功能
 */
@RestController
@RequestMapping("/system/setting")
public class SystemSettingController {
    
    @Autowired
    private BgxtXtszMapper bgxtXtszMapper;
    
    @Autowired
    private SysXtszMapper sysXtszMapper;
    
    /**
     * 获取所有系统设置项
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getSettings() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取 bgxt_xtsz 设置项
        List<BgxtXtsz> bgxtSettings = bgxtXtszMapper.selectList(null);
        result.put("bgxtSettings", bgxtSettings);
        
        // 获取 sys_xtsz 系统设置（通常只有一条记录）
        List<SysXtsz> sysSettings = sysXtszMapper.selectList(null);
        if (!sysSettings.isEmpty()) {
            result.put("sysSetting", sysSettings.get(0));
        } else {
            // 如果没有记录，创建一个默认的
            SysXtsz defaultSetting = new SysXtsz();
            defaultSetting.setLwbz(false);
            defaultSetting.setBxt(false);
            defaultSetting.setZyfykz(0);
            result.put("sysSetting", defaultSetting);
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 保存系统设置项
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveSetting(@RequestBody SaveSettingRequest request) {
        try {
            // 保存 bgxt_xtsz 设置项列表
            if (request.getBgxtSettings() != null && !request.getBgxtSettings().isEmpty()) {
                for (BgxtXtsz setting : request.getBgxtSettings()) {
                    if (setting.getId() != null) {
                        bgxtXtszMapper.updateById(setting);
                    } else {
                        bgxtXtszMapper.insert(setting);
                    }
                }
            }
            
            if (request.getSysSetting() != null) {
                SysXtsz sysSetting = request.getSysSetting();
                // sys_xtsz 通常只有一条记录，先删除再插入
                QueryWrapper<SysXtsz> wrapper = new QueryWrapper<>();
                sysXtszMapper.delete(wrapper);
                sysXtszMapper.insert(sysSetting);
            }
            
            return ResponseEntity.ok(ApiResponse.success("保存成功"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("保存失败：" + e.getMessage()));
        }
    }
    
    @Data
    public static class SaveSettingRequest {
        private List<BgxtXtsz> bgxtSettings;
        private SysXtsz sysSetting;
    }
    
    @Data
    public static class ApiResponse {
        private Boolean success;
        private String message;
        
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

