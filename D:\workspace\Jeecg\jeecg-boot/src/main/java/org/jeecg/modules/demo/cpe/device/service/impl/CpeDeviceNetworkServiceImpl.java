package org.jeecg.modules.demo.cpe.device.service.impl;

import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceNetwork;
import org.jeecg.modules.demo.cpe.device.mapper.CpeDeviceNetworkMapper;
import org.jeecg.modules.demo.cpe.device.service.ICpeDeviceNetworkService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 设备内网配置
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
@Service
public class CpeDeviceNetworkServiceImpl extends ServiceImpl<CpeDeviceNetworkMapper, CpeDeviceNetwork> implements ICpeDeviceNetworkService {
	
	@Autowired
	private CpeDeviceNetworkMapper cpeDeviceNetworkMapper;
	
	@Override
	public List<CpeDeviceNetwork> selectByMainId(String mainId) {
		return cpeDeviceNetworkMapper.selectByMainId(mainId);
	}
}
