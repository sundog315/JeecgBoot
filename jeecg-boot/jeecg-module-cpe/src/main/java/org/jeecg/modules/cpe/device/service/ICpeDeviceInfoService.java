package org.jeecg.modules.cpe.device.service;

import org.jeecg.modules.cpe.device.entity.CpeDeviceInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;

/**
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2024-12-29
 * @Version: V1.0
 */
public interface ICpeDeviceInfoService extends IService<CpeDeviceInfo> {

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
