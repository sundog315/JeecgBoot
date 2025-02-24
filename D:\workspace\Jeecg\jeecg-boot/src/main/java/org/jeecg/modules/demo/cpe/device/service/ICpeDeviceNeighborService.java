package org.jeecg.modules.demo.cpe.device.service;

import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceNeighbor;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: CPE设备邻区信息
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
public interface ICpeDeviceNeighborService extends IService<CpeDeviceNeighbor> {

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId
   * @return List<CpeDeviceNeighbor>
   */
	public List<CpeDeviceNeighbor> selectByMainId(String mainId);
}
