package org.jeecg.modules.cpe.device.service;

import org.jeecg.modules.cpe.device.entity.CpeDeviceNetwork;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 设备内网配置
 * @Author: jeecg-boot
 * @Date:   2025-01-12
 * @Version: V1.0
 */
public interface ICpeDeviceNetworkService extends IService<CpeDeviceNetwork> {
    /**
     * 处理设备上报的网络配置信息
     * @param deviceSn 设备序列号
     * @param networkConfig 网络配置JSON字符串
     * @throws Exception 处理异常时抛出
     */
    void report(String deviceSn, String networkConfig) throws Exception;
}
