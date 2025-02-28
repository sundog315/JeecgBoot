/*
 * @Author: Janelle.Liu sundog315@foxmail.com
 * @Date: 2025-02-28 08:20:18
 * @LastEditors: Janelle.Liu sundog315@foxmail.com
 * @LastEditTime: 2025-02-28 08:22:39
 * @FilePath: /JeecgBoot/jeecg-boot/jeecg-module-cpe/src/main/java/org/jeecg/modules/cpe/card/mapper/CardPackageRelMapper.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package org.jeecg.modules.cpe.card.mapper;

import java.util.List;
import org.jeecg.modules.cpe.card.entity.CardPackageRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 卡片套餐
 * @Author: jeecg-boot
 * @Date:   2025-02-28
 * @Version: V1.0
 */
public interface CardPackageRelMapper extends BaseMapper<CardPackageRel> {

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return boolean
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);

   /**
    * 通过主表id查询子表数据
    *
    * @param mainId 主表id
    * @return List<CardPackageRel>
    */
	public List<CardPackageRel> selectByMainId(@Param("mainId") String mainId);

}
