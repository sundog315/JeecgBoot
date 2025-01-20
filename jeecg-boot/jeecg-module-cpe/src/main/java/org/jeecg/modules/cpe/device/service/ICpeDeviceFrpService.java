package org.jeecg.modules.cpe.device.service;

import org.jeecg.modules.cpe.device.entity.CpeDeviceFrp;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 设备远程控制
 * @Author: jeecg-boot
 * @Date:   2025-01-04
 * @Version: V1.0
 */
public interface ICpeDeviceFrpService extends IService<CpeDeviceFrp> {

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId
   * @return List<CpeDeviceFrp>
   */
	public List<CpeDeviceFrp> selectByMainId(String mainId);

  public int report(String deviceSnParam, String frp);
}
