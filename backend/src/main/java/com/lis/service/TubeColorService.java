package com.lis.service;

import com.lis.mapper.TubeColorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TubeColorService {

    @Autowired
    private TubeColorMapper tubeColorMapper;

    public List<Map<String, Object>> listTubeColors(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return tubeColorMapper.listAll();
        }
        return tubeColorMapper.search(keyword);
    }

    public void saveTubeColor(Map<String, Object> data) {
        String sgys = (String) data.get("sgys");
        String pym = (String) data.get("pym");
        if (sgys == null || sgys.trim().isEmpty()) {
            throw new IllegalArgumentException("试管颜色不能为空");
        }
        if (pym == null || pym.trim().isEmpty()) {
            throw new IllegalArgumentException("拼音码不能为空");
        }
        String oldPym = (String) data.get("oldPym");
        if (oldPym != null && !oldPym.isEmpty()) {
            if (!pym.equals(oldPym) && tubeColorMapper.countByPym(pym) > 0) {
                throw new IllegalArgumentException("该拼音码已存在");
            }
            tubeColorMapper.update(data);
        } else {
            if (tubeColorMapper.countByPym(pym) > 0) {
                throw new IllegalArgumentException("该拼音码已存在");
            }
            tubeColorMapper.insert(data);
        }
    }

    public void deleteTubeColor(String pym) {
        if (pym == null || pym.trim().isEmpty()) {
            throw new IllegalArgumentException("拼音码不能为空");
        }
        tubeColorMapper.deleteByPym(pym);
    }
}