package org.jeecg.modules.demo.cpe.device.service;

import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceAutoreboot;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 设备自动重启
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
public interface ICpeDeviceAutorebootService extends IService<CpeDeviceAutoreboot> {

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId
   * @return List<CpeDeviceAutoreboot>
   */
	public List<CpeDeviceAutoreboot> selectByMainId(String mainId);
}
