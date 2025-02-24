package org.jeecg.modules.demo.cpe.device.service;

import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceStatus;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: CPE设备状态表
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
public interface ICpeDeviceStatusService extends IService<CpeDeviceStatus> {

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId
   * @return List<CpeDeviceStatus>
   */
	public List<CpeDeviceStatus> selectByMainId(String mainId);
}
