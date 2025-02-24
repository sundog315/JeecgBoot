package org.jeecg.modules.demo.cpe.device.service.impl;

import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceStatus;
import org.jeecg.modules.demo.cpe.device.mapper.CpeDeviceStatusMapper;
import org.jeecg.modules.demo.cpe.device.service.ICpeDeviceStatusService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: CPE设备状态表
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
@Service
public class CpeDeviceStatusServiceImpl extends ServiceImpl<CpeDeviceStatusMapper, CpeDeviceStatus> implements ICpeDeviceStatusService {
	
	@Autowired
	private CpeDeviceStatusMapper cpeDeviceStatusMapper;
	
	@Override
	public List<CpeDeviceStatus> selectByMainId(String mainId) {
		return cpeDeviceStatusMapper.selectByMainId(mainId);
	}
}
