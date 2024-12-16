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
   * 通过device_sn查询主表数据
   *
   * @param deviceSn 主表设备编码
   * @return List<CpeDevice>
   */
  public List<CpeDevice> selectByDeviceSn(@Param("deviceSn") String deviceSn);
}
