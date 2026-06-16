package com.lis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lis.entity.BgxtBrxx;
import com.lis.entity.BgxtJyjg;
import com.lis.mapper.BgxtBrxxMapper;
import com.lis.mapper.BgxtJyjgMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BatchProcessService {

    @Autowired
    private BgxtBrxxMapper bgxtBrxxMapper;

    @Autowired
    private BgxtJyjgMapper bgxtJyjgMapper;

    public List<Integer> findBrxxIdsBySyhRange(Integer minSyh, Integer maxSyh) {
        QueryWrapper<BgxtBrxx> wrapper = new QueryWrapper<>();
        wrapper.between("syh", minSyh, maxSyh).eq("ybzt", 1);
        wrapper.select("brxx_id");
        List<BgxtBrxx> list = bgxtBrxxMapper.selectList(wrapper);
        List<Integer> result = new ArrayList<>();
        for (BgxtBrxx b : list) {
            result.add(b.getBrxxId());
        }
        return result;
    }

    public int countJyjgByBrxxIdAndXmid(Integer brxxId, Integer xmid) {
        Integer count = bgxtJyjgMapper.countByBrxxIdAndXmid(brxxId, xmid);
        return count != null ? count : 0;
    }

    @Transactional
    public void updateJyjg(Integer brxxId, Integer xmid, String jyjg) {
        bgxtJyjgMapper.updateResultByBrxxIdAndXmid(brxxId, xmid, jyjg);
    }

    @Transactional
    public void insertJyjg(Integer brxxId, Integer xmid, String jyjg) {
        bgxtJyjgMapper.insertResultSimple(brxxId, xmid, jyjg);
    }

    @Transactional
    public int batchInputResult(List<Integer> brxxIds, Integer xmid, String jyjg) {
        int updated = 0;
        for (Integer brxxId : brxxIds) {
            int existing = countJyjgByBrxxIdAndXmid(brxxId, xmid);
            if (existing > 0) {
                updateJyjg(brxxId, xmid, jyjg);
            } else {
                insertJyjg(brxxId, xmid, jyjg);
            }
            updated++;
        }
        return updated;
    }
}
