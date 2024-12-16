package org.jeecg.modules.cpe.card.service.impl;

import org.jeecg.modules.cpe.card.entity.CardInfo;
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
 * @Date:   2025-01-02
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
