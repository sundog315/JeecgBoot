package org.jeecg.modules.demo.cpe.device.service;

import org.jeecg.modules.demo.cpe.device.entity.CpeSpeedLimit;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 设备速率
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
public interface ICpeSpeedLimitService extends IService<CpeSpeedLimit> {

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId
   * @return List<CpeSpeedLimit>
   */
	public List<CpeSpeedLimit> selectByMainId(String mainId);
}
