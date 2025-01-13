package org.jeecg.modules.cpe.device.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.cpe.device.entity.CpeDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2025-01-02
 * @Version: V1.0
 */
public interface CpeDeviceMapper extends BaseMapper<CpeDevice> {
    /**
     * 根据设备序列号查询设备信息
     * @param deviceSn 设备序列号
     * @return 设备信息列表
     */
    List<CpeDevice> selectByDeviceSn(@Param("deviceSn") String deviceSn);
}
