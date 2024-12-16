package org.jeecg.modules.cpe.card.service;

import org.jeecg.modules.cpe.card.entity.CardInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 卡片信息表
 * @Author: jeecg-boot
 * @Date:   2025-01-02
 * @Version: V1.0
 */
public interface ICardInfoService extends IService<CardInfo> {

	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);

	/**
	 * 通过卡ICCID查询cardInfo
	 *
	 * @param cardNo 卡ICCID号
	 * @return List<CardInfo>
	 */
	public List<CardInfo> selectByCardNo(String cardNo);
}
