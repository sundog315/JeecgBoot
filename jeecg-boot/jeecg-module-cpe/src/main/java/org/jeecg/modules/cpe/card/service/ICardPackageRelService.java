package org.jeecg.modules.cpe.card.service;

import org.jeecg.modules.cpe.card.entity.CardPackageRel;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 卡片套餐
 * @Author: jeecg-boot
 * @Date:   2025-02-28
 * @Version: V1.0
 */
public interface ICardPackageRelService extends IService<CardPackageRel> {

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId
   * @return List<CardPackageRel>
   */
	public List<CardPackageRel> selectByMainId(String mainId);
}
