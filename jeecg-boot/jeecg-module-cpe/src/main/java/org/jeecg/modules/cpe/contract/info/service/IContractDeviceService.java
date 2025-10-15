package org.jeecg.modules.cpe.contract.info.service;

import org.jeecg.modules.cpe.contract.info.entity.ContractDevice;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 合同设备表
 * @Author: jeecg-boot
 * @Date:   2025-10-11
 * @Version: V1.0
 */
public interface IContractDeviceService extends IService<ContractDevice> {

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId
   * @return List<ContractDevice>
   */
	public List<ContractDevice> selectByMainId(String mainId);
	
  /**
   * 通过主表id分页查询子表数据
   *
   * @param page 分页参数
   * @param mainId 主表id
   * @return IPage<ContractDevice>
   */
	public IPage<ContractDevice> pageByMainId(Page<ContractDevice> page, String mainId);
}
