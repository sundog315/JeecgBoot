package org.jeecg.modules.demo.cpe.device.service;

import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceStatus;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceNeighbor;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceFrp;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceAutoreboot;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceNetwork;
import org.jeecg.modules.demo.cpe.device.entity.CpeSpeedLimit;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceWireless;
import org.jeecg.modules.demo.cpe.device.entity.CpeOperLog;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2025-02-24
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
