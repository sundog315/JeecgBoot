package org.jeecg.modules.cpe.card.service.impl;

import org.jeecg.modules.cpe.card.entity.CardPackageRel;
import org.jeecg.modules.cpe.card.mapper.CardPackageRelMapper;
import org.jeecg.modules.cpe.card.service.ICardPackageRelService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 卡片套餐
 * @Author: jeecg-boot
 * @Date:   2025-02-28
 * @Version: V1.0
 */
@Service
public class CardPackageRelServiceImpl extends ServiceImpl<CardPackageRelMapper, CardPackageRel> implements ICardPackageRelService {
	
	@Autowired
	private CardPackageRelMapper cardPackageRelMapper;
	
	@Override
	public List<CardPackageRel> selectByMainId(String mainId) {
		return cardPackageRelMapper.selectByMainId(mainId);
	}
}
