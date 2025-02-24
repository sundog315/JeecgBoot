package org.jeecg.modules.demo.cpe.device.service.impl;

import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceFrp;
import org.jeecg.modules.demo.cpe.device.mapper.CpeDeviceFrpMapper;
import org.jeecg.modules.demo.cpe.device.service.ICpeDeviceFrpService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 设备远程控制
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
@Service
public class CpeDeviceFrpServiceImpl extends ServiceImpl<CpeDeviceFrpMapper, CpeDeviceFrp> implements ICpeDeviceFrpService {
	
	@Autowired
	private CpeDeviceFrpMapper cpeDeviceFrpMapper;
	
	@Override
	public List<CpeDeviceFrp> selectByMainId(String mainId) {
		return cpeDeviceFrpMapper.selectByMainId(mainId);
	}
}
