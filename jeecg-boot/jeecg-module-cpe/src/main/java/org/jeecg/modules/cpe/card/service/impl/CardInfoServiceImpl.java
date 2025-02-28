/*
 * @Author: Janelle.Liu sundog315@foxmail.com
 * @Date: 2025-02-28 08:20:18
 * @LastEditors: Janelle.Liu sundog315@foxmail.com
 * @LastEditTime: 2025-02-28 08:26:05
 * @FilePath: /JeecgBoot/jeecg-boot/jeecg-module-cpe/src/main/java/org/jeecg/modules/cpe/card/service/impl/CardInfoServiceImpl.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package org.jeecg.modules.cpe.card.service.impl;

import org.jeecg.modules.cpe.card.entity.CardInfo;
import org.jeecg.modules.cpe.card.entity.CardPackageRel;
import org.jeecg.modules.cpe.card.mapper.CardPackageRelMapper;
import org.jeecg.modules.cpe.card.mapper.CardInfoMapper;
import org.jeecg.modules.cpe.card.service.ICardInfoService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 卡片信息表
 * @Author: jeecg-boot
 * @Date:   2025-02-28
 * @Version: V1.0
 */
@Service
public class CardInfoServiceImpl extends ServiceImpl<CardInfoMapper, CardInfo> implements ICardInfoService {

	@Autowired
	private CardInfoMapper cardInfoMapper;
	@Autowired
	private CardPackageRelMapper cardPackageRelMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		cardPackageRelMapper.deleteByMainId(id);
		cardInfoMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			cardPackageRelMapper.deleteByMainId(id.toString());
			cardInfoMapper.deleteById(id);
		}
	}
		/**
	 * 通过卡ICCID查询cardInfo
	 *
	 * @param cardNo 卡ICCID号
	 * @return List<CardInfo>
	 */
	@Override
	public List<CardInfo> selectByCardNo(String cardNo) {
		return cardInfoMapper.selectByCardNo(cardNo);
	};
}
