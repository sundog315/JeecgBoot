package org.jeecg.modules.demo.cpe.device.service.impl;

import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceNeighbor;
import org.jeecg.modules.demo.cpe.device.mapper.CpeDeviceNeighborMapper;
import org.jeecg.modules.demo.cpe.device.service.ICpeDeviceNeighborService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: CPE设备邻区信息
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
@Service
public class CpeDeviceNeighborServiceImpl extends ServiceImpl<CpeDeviceNeighborMapper, CpeDeviceNeighbor> implements ICpeDeviceNeighborService {
	
	@Autowired
	private CpeDeviceNeighborMapper cpeDeviceNeighborMapper;
	
	@Override
	public List<CpeDeviceNeighbor> selectByMainId(String mainId) {
		return cpeDeviceNeighborMapper.selectByMainId(mainId);
	}
}
