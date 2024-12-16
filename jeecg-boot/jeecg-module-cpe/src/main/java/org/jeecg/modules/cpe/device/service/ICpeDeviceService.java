package org.jeecg.modules.cpe.device.service;

import java.util.List;

import org.jeecg.modules.cpe.device.entity.CpeDevice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2025-01-02
 * @Version: V1.0
 */
public interface ICpeDeviceService extends IService<CpeDevice> {
	/**
	 * 通过设诶序列号查询cpeDevice
	 *
	 * @param deviceSn 设备序列号
	 * @return List<CpeDevice>
	 */
	public List<CpeDevice> selectByDeviceSn(String deviceSn);
}