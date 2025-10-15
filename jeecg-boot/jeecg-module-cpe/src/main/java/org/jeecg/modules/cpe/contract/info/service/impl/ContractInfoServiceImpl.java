package org.jeecg.modules.cpe.contract.info.service.impl;

import org.jeecg.modules.cpe.contract.info.entity.ContractInfo;
import org.jeecg.modules.cpe.contract.info.mapper.ContractDeviceMapper;
import org.jeecg.modules.cpe.contract.info.mapper.ContractInfoMapper;
import org.jeecg.modules.cpe.contract.info.service.IContractInfoService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.Collection;

/**
 * @Description: 合同信息表
 * @Author: jeecg-boot
 * @Date:   2025-10-11
 * @Version: V1.0
 */
@Service
public class ContractInfoServiceImpl extends ServiceImpl<ContractInfoMapper, ContractInfo> implements IContractInfoService {

	@Autowired
	private ContractInfoMapper contractInfoMapper;
	@Autowired
	private ContractDeviceMapper contractDeviceMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		contractDeviceMapper.deleteByMainId(id);
		contractInfoMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			contractDeviceMapper.deleteByMainId(id.toString());
			contractInfoMapper.deleteById(id);
		}
	}
	
}
