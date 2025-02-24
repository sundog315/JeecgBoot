package org.jeecg.modules.demo.cpe.device.service.impl;

import org.jeecg.modules.demo.cpe.device.entity.CpeSpeedLimit;
import org.jeecg.modules.demo.cpe.device.mapper.CpeSpeedLimitMapper;
import org.jeecg.modules.demo.cpe.device.service.ICpeSpeedLimitService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 设备速率
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
@Service
public class CpeSpeedLimitServiceImpl extends ServiceImpl<CpeSpeedLimitMapper, CpeSpeedLimit> implements ICpeSpeedLimitService {
	
	@Autowired
	private CpeSpeedLimitMapper cpeSpeedLimitMapper;
	
	@Override
	public List<CpeSpeedLimit> selectByMainId(String mainId) {
		return cpeSpeedLimitMapper.selectByMainId(mainId);
	}
}
