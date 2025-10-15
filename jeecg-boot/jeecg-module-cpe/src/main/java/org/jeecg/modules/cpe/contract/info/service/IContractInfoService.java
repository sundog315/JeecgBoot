package org.jeecg.modules.cpe.contract.info.service;

import org.jeecg.modules.cpe.contract.info.entity.ContractInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;

/**
 * @Description: 合同信息表
 * @Author: jeecg-boot
 * @Date:   2025-10-11
 * @Version: V1.0
 */
public interface IContractInfoService extends IService<ContractInfo> {

	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);


}
