package org.jeecg.modules.cpe.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

import org.jeecg.modules.cpe.device.entity.CpeDeviceWireless;

/**
 * @Description: 设备无线网络配置
 * @Author: jeecg-boot
 * @Date:   2025-01-20
 * @Version: V1.0
 */
public interface ICpeDeviceWirelessService extends IService<CpeDeviceWireless> {

  /**
   * 通过主设备ID查询无线配置记录
   *
   * @param mainId 主设备ID
   * @return 无线配置记录列表
   */
	public List<CpeDeviceWireless> selectByMainId(String mainId);

  /**
   * 处理设备无线配置报告
   *
   * @param deviceSnParam 设备序列号
   * @param wireless 无线配置内容
   * @return 处理结果状态码
   */
	int report(String deviceSnParam, String wireless);
}
