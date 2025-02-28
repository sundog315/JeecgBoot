package org.jeecg.modules.cpe.card.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.cpe.card.entity.CardInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 卡片信息表
 * @Author: jeecg-boot
 * @Date:   2025-02-28
 * @Version: V1.0
 */
public interface CardInfoMapper extends BaseMapper<CardInfo> {
	/**
	 * 通过卡ICCID查询cardInfo
	 *
	 * @param cardNo 卡ICCID号
	 * @return List<CardInfo>
	 */
	public List<CardInfo> selectByCardNo(@Param("cardNo")String cardNo);

}
