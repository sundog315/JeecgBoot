package org.jeecg.modules.demo.cpe.device.service;

import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceWireless;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 设备无线配置
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
public interface ICpeDeviceWirelessService extends IService<CpeDeviceWireless> {

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId
   * @return List<CpeDeviceWireless>
   */
	public List<CpeDeviceWireless> selectByMainId(String mainId);
}
