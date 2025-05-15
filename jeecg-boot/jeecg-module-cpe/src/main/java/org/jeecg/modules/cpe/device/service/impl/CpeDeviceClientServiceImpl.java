package org.jeecg.modules.cpe.device.service.impl;

import org.jeecg.modules.cpe.device.entity.CpeDeviceClient;
import org.jeecg.modules.cpe.device.mapper.CpeDeviceClientMapper;
import org.jeecg.modules.cpe.device.service.ICpeDeviceClientService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 连接终端
 * @Author: jeecg-boot
 * @Date:   2025-05-15
 * @Version: V1.0
 */
@Service
public class CpeDeviceClientServiceImpl extends ServiceImpl<CpeDeviceClientMapper, CpeDeviceClient> implements ICpeDeviceClientService {
	
	@Autowired
	private CpeDeviceClientMapper cpeDeviceClientMapper;
	
	@Override
	public List<CpeDeviceClient> selectByMainId(String mainId) {
		return cpeDeviceClientMapper.selectByMainId(mainId);
	}

	@Override
	public void deleteByMainId(String mainId) {
		cpeDeviceClientMapper.deleteByMainId(mainId);
	}
}
