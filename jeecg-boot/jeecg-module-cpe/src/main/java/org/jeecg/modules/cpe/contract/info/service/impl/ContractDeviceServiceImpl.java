package org.jeecg.modules.cpe.contract.info.service.impl;

import org.jeecg.modules.cpe.contract.info.entity.ContractDevice;
import org.jeecg.modules.cpe.contract.info.mapper.ContractDeviceMapper;
import org.jeecg.modules.cpe.contract.info.service.IContractDeviceService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 合同设备表
 * @Author: jeecg-boot
 * @Date:   2025-10-11
 * @Version: V1.0
 */
@Service
public class ContractDeviceServiceImpl extends ServiceImpl<ContractDeviceMapper, ContractDevice> implements IContractDeviceService {
	
	@Autowired
	private ContractDeviceMapper contractDeviceMapper;
	
	@Override
	public List<ContractDevice> selectByMainId(String mainId) {
		return contractDeviceMapper.selectByMainId(mainId);
	}
	
	@Override
	public IPage<ContractDevice> pageByMainId(Page<ContractDevice> page, String mainId) {
		List<ContractDevice> list = contractDeviceMapper.selectByMainId(mainId);
		return new Page<ContractDevice>(page.getCurrent(), page.getSize(), list.size()).setRecords(
			list.stream().skip((page.getCurrent() - 1) * page.getSize()).limit(page.getSize()).collect(Collectors.toList())
		);
	}
}
