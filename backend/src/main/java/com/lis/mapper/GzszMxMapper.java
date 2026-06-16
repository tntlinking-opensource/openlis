package com.lis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lis.entity.GzszMx;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface GzszMxMapper extends BaseMapper<GzszMx> {
    List<Map<String, Object>> listSubcategories();
    List<Map<String, Object>> listSubcategoriesByDlid(@Param("dlid") Integer dlid);
    void insertSubcategory(GzszMx entity);
    void updateSubcategory(GzszMx entity);
    void bindMaterialFee(@Param("xlbh") String xlbh, @Param("clfdm") String clfdm, @Param("clfmc") String clfmc);
    void unbindMaterialFee(@Param("xlbh") Integer xlbh);
    void syncMaterialFee(@Param("sgys") String sgys, @Param("clfdm") String clfdm, @Param("clfmc") String clfmc);
    List<Map<String, Object>> listMaterialBindings(@Param("dlid") Integer dlid, @Param("xlmc") String xlmc);

    @Select("SELECT xmdm AS code, xmzwmc AS name, pym, xmdw AS dan_jia FROM sys_jyxm_full WHERE 1=1 " +
            "AND (xmdm LIKE CONCAT('%', #{pym}, '%') OR xmzwmc LIKE CONCAT('%', #{pym}, '%') OR pym LIKE CONCAT('%', #{pym}, '%')) " +
            "LIMIT 50")
    List<Map<String, Object>> listMaterialFeeItems(@Param("pym") String pym);
}