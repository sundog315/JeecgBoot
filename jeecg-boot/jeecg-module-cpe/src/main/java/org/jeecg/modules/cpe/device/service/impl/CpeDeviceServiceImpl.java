/*
 * @Author: Janelle.Liu sundog315@foxmail.com
 * @Date: 2025-01-02 08:32:19
 * @LastEditors: Janelle.Liu sundog315@foxmail.com
 * @LastEditTime: 2025-02-24 16:13:39
 * @FilePath: /JeecgBoot/jeecg-boot/jeecg-module-cpe/src/main/java/org/jeecg/modules/cpe/device/service/impl/CpeDeviceServiceImpl.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package org.jeecg.modules.cpe.device.service.impl;

import java.util.List;
import java.util.Date;

import org.jeecg.modules.cpe.device.entity.CpeDevice;
import org.jeecg.modules.cpe.device.mapper.CpeDeviceMapper;
import org.jeecg.modules.cpe.device.service.ICpeDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.cpe.card.entity.CardInfo;
import org.jeecg.modules.cpe.card.service.ICardInfoService;

/**
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2025-01-02
 * @Version: V1.0
 */
@Service
public class CpeDeviceServiceImpl extends ServiceImpl<CpeDeviceMapper, CpeDevice> implements ICpeDeviceService {
	@Autowired
	private CpeDeviceMapper cpeDeviceMapper;

	@Autowired
	private ICardInfoService cardInfoService;

	@Override
	public List<CpeDevice> selectByDeviceSn(String deviceSn) {
		return cpeDeviceMapper.selectByDeviceSn(deviceSn);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateById(CpeDevice entity) {
		// 获取更新前的设备信息
		CpeDevice oldDevice = this.getById(entity.getId());
		if (oldDevice == null) {
			return false;
		}

		// 检查组织编码是否发生变化
		if (oldDevice.getSysOrgCode() != null && !oldDevice.getSysOrgCode().equals(entity.getSysOrgCode())) {
			// 更新关联卡片的组织编码
			updateRelatedCardsOrgCode(oldDevice.getCardNo(), entity.getSysOrgCode());
			//updateRelatedCardsOrgCode(oldDevice.getOnlineCardNo(), entity.getSysOrgCode());
		}

		// 更新设备信息
		return super.updateById(entity);
	}

	/**
	 * 更新关联卡片的组织编码
	 * @param cardId 卡片ID
	 * @param newOrgCode 新的组织编码
	 */
	private void updateRelatedCardsOrgCode(String cardId, String newOrgCode) {
		if (StringUtils.isNotBlank(cardId)) {
			CardInfo cardInfo = cardInfoService.getById(cardId);
			cardInfo.setSysOrgCode(newOrgCode);
			cardInfo.setUpdateTime(new Date());
			LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			cardInfo.setUpdateBy(sysUser.getId());
			cardInfoService.updateById(cardInfo);
		}
	}
}