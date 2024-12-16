package org.jeecg.modules.cpe.device.service.impl;

import org.jeecg.modules.cpe.device.entity.CpeDeviceInfo;
import org.jeecg.modules.cpe.device.mapper.CpeDeviceStatusMapper;
import org.jeecg.modules.cpe.device.mapper.CpeDeviceNeighborMapper;
import org.jeecg.modules.cpe.device.mapper.CpeDeviceInfoMapper;
import org.jeecg.modules.cpe.device.service.ICpeDeviceInfoService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.Collection;

/**
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2024-12-29
 * @Version: V1.0
 */
@Service
public class CpeDeviceInfoServiceImpl extends ServiceImpl<CpeDeviceInfoMapper, CpeDeviceInfo> implements ICpeDeviceInfoService {

	@Autowired
	private CpeDeviceInfoMapper cpeDeviceInfoMapper;
	@Autowired
	private CpeDeviceStatusMapper cpeDeviceStatusMapper;
	@Autowired
	private CpeDeviceNeighborMapper cpeDeviceNeighborMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		cpeDeviceStatusMapper.deleteByMainId(id);
		cpeDeviceNeighborMapper.deleteByMainId(id);
		cpeDeviceInfoMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			cpeDeviceStatusMapper.deleteByMainId(id.toString());
			cpeDeviceNeighborMapper.deleteByMainId(id.toString());
			cpeDeviceInfoMapper.deleteById(id);
		}
	}
	
}
