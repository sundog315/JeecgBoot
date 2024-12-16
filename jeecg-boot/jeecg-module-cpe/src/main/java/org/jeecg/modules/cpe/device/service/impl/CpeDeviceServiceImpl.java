package org.jeecg.modules.cpe.device.service.impl;

import java.util.List;

import org.jeecg.modules.cpe.device.entity.CpeDevice;
import org.jeecg.modules.cpe.device.mapper.CpeDeviceMapper;
import org.jeecg.modules.cpe.device.service.ICpeDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

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
	
	@Override
	public List<CpeDevice> selectByDeviceSn(String deviceSn) {
		return cpeDeviceMapper.selectByDeviceSn(deviceSn);
	}
}