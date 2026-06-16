package com.lis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.SysQxxl;
import com.lis.mapper.SysCzyqxMapper;
import com.lis.mapper.SysQxdlMapper;
import com.lis.mapper.SysQxxlMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OperatorPermissionService {

    @Autowired
    private SysCzyqxMapper sysCzyqxMapper;

    @Autowired
    private SysQxdlMapper sysQxdlMapper;

    @Autowired
    private SysQxxlMapper sysQxxlMapper;

    public List<Map<String, Object>> getOperators(String czyxm, String ksmc) {
        return sysCzyqxMapper.getOperators(czyxm, ksmc);
    }

    public List<Map<String, Object>> getSubsystems() {
        return sysCzyqxMapper.getSubsystems();
    }

    public List<Map<String, Object>> getModuleCategories(Integer zxtid) {
        return sysCzyqxMapper.getModuleCategories(zxtid);
    }

    public List<Map<String, Object>> getModules(Integer zxtid, String mkfl, String czydm) {
        return sysCzyqxMapper.getModules(zxtid, mkfl, czydm);
    }

    public List<Map<String, Object>> getPermissionCategories(Integer zxtid) {
        return sysCzyqxMapper.getPermissionCategories(zxtid);
    }

    public List<Map<String, Object>> getPermissionItems(String dldm, String czydm) {
        return sysCzyqxMapper.getPermissionItemsByDldm(dldm, czydm);
    }

    @Data
    public static class PermissionItem {
        private String xldm;
        private Boolean bz;
    }

    @Data
    public static class ModulePermissionItem {
        private Integer mkdm;
        private String frmName;
        private String actionName;
        private String caption;
        private Boolean bz;
    }

    public void saveMenuPermissions(String czydm, List<PermissionItem> items) {
        for (PermissionItem item : items) {
            if (item.getBz() != null && item.getBz()) {
                sysCzyqxMapper.insertPermission(czydm, item.getXldm());
            } else {
                sysCzyqxMapper.deletePermission(czydm, item.getXldm());
            }
        }
    }

    public void saveModulePermissions(String czydm, List<ModulePermissionItem> items) {
        for (ModulePermissionItem item : items) {
            if (item.getBz() != null && item.getBz()) {
                sysCzyqxMapper.insertModulePermission(czydm, item.getMkdm(), item.getFrmName(), item.getActionName(), item.getCaption());
            } else {
                sysCzyqxMapper.deleteModulePermission(czydm, item.getMkdm());
            }
        }
    }

    public List<String> getMyPermissions(String czydm) {
        return sysCzyqxMapper.getMyPermissions(czydm);
    }
}
