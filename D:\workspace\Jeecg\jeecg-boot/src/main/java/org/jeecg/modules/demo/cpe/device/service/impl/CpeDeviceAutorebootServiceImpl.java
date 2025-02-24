package org.jeecg.modules.demo.cpe.device.service.impl;

import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceAutoreboot;
import org.jeecg.modules.demo.cpe.device.mapper.CpeDeviceAutorebootMapper;
import org.jeecg.modules.demo.cpe.device.service.ICpeDeviceAutorebootService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 设备自动重启
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
@Service
public class CpeDeviceAutorebootServiceImpl extends ServiceImpl<CpeDeviceAutorebootMapper, CpeDeviceAutoreboot> implements ICpeDeviceAutorebootService {
	
	@Autowired
	private CpeDeviceAutorebootMapper cpeDeviceAutorebootMapper;
	
	@Override
	public List<CpeDeviceAutoreboot> selectByMainId(String mainId) {
		return cpeDeviceAutorebootMapper.selectByMainId(mainId);
	}
}
