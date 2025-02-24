package org.jeecg.modules.demo.cpe.device.service.impl;

import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceInfo;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceStatus;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceNeighbor;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceFrp;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceAutoreboot;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceNetwork;
import org.jeecg.modules.demo.cpe.device.entity.CpeSpeedLimit;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceWireless;
import org.jeecg.modules.demo.cpe.device.entity.CpeOperLog;
import org.jeecg.modules.demo.cpe.device.mapper.CpeDeviceStatusMapper;
import org.jeecg.modules.demo.cpe.device.mapper.CpeDeviceNeighborMapper;
import org.jeecg.modules.demo.cpe.device.mapper.CpeDeviceFrpMapper;
import org.jeecg.modules.demo.cpe.device.mapper.CpeDeviceAutorebootMapper;
import org.jeecg.modules.demo.cpe.device.mapper.CpeDeviceNetworkMapper;
import org.jeecg.modules.demo.cpe.device.mapper.CpeSpeedLimitMapper;
import org.jeecg.modules.demo.cpe.device.mapper.CpeDeviceWirelessMapper;
import org.jeecg.modules.demo.cpe.device.mapper.CpeOperLogMapper;
import org.jeecg.modules.demo.cpe.device.mapper.CpeDeviceInfoMapper;
import org.jeecg.modules.demo.cpe.device.service.ICpeDeviceInfoService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2025-02-24
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
	@Autowired
	private CpeDeviceFrpMapper cpeDeviceFrpMapper;
	@Autowired
	private CpeDeviceAutorebootMapper cpeDeviceAutorebootMapper;
	@Autowired
	private CpeDeviceNetworkMapper cpeDeviceNetworkMapper;
	@Autowired
	private CpeSpeedLimitMapper cpeSpeedLimitMapper;
	@Autowired
	private CpeDeviceWirelessMapper cpeDeviceWirelessMapper;
	@Autowired
	private CpeOperLogMapper cpeOperLogMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		cpeDeviceStatusMapper.deleteByMainId(id);
		cpeDeviceNeighborMapper.deleteByMainId(id);
		cpeDeviceFrpMapper.deleteByMainId(id);
		cpeDeviceAutorebootMapper.deleteByMainId(id);
		cpeDeviceNetworkMapper.deleteByMainId(id);
		cpeSpeedLimitMapper.deleteByMainId(id);
		cpeDeviceWirelessMapper.deleteByMainId(id);
		cpeOperLogMapper.deleteByMainId(id);
		cpeDeviceInfoMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			cpeDeviceStatusMapper.deleteByMainId(id.toString());
			cpeDeviceNeighborMapper.deleteByMainId(id.toString());
			cpeDeviceFrpMapper.deleteByMainId(id.toString());
			cpeDeviceAutorebootMapper.deleteByMainId(id.toString());
			cpeDeviceNetworkMapper.deleteByMainId(id.toString());
			cpeSpeedLimitMapper.deleteByMainId(id.toString());
			cpeDeviceWirelessMapper.deleteByMainId(id.toString());
			cpeOperLogMapper.deleteByMainId(id.toString());
			cpeDeviceInfoMapper.deleteById(id);
		}
	}
	
}
