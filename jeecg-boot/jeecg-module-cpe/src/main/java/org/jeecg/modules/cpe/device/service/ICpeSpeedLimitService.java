package org.jeecg.modules.cpe.device.service;

import org.jeecg.modules.cpe.device.entity.CpeSpeedLimit;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 设备速率
 * @Author: jeecg-boot
 * @Date:   2025-01-13
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

  /**
   * 处理设备上报的速率限制信息
   * @param deviceSn 设备序列号
   * @param speedLimitJson 速率限制JSON字符串
   * @throws Exception
   */
  void report(String deviceSn, String speedLimitJson) throws Exception;
}
