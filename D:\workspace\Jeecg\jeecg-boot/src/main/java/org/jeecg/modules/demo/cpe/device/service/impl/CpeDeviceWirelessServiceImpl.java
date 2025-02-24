package org.jeecg.modules.demo.cpe.device.service.impl;

import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceWireless;
import org.jeecg.modules.demo.cpe.device.mapper.CpeDeviceWirelessMapper;
import org.jeecg.modules.demo.cpe.device.service.ICpeDeviceWirelessService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 设备无线配置
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
@Service
public class CpeDeviceWirelessServiceImpl extends ServiceImpl<CpeDeviceWirelessMapper, CpeDeviceWireless> implements ICpeDeviceWirelessService {
	
	@Autowired
	private CpeDeviceWirelessMapper cpeDeviceWirelessMapper;
	
	@Override
	public List<CpeDeviceWireless> selectByMainId(String mainId) {
		return cpeDeviceWirelessMapper.selectByMainId(mainId);
	}
}
