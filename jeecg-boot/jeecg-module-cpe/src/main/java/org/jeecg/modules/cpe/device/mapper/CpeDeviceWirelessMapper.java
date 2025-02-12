package org.jeecg.modules.cpe.device.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.cpe.device.entity.CpeDeviceWireless;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 设备无线网络配置
 * @Author: jeecg-boot
 * @Date:   2025-01-20
 * @Version: V1.0
 */
public interface CpeDeviceWirelessMapper extends BaseMapper<CpeDeviceWireless> {

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return boolean
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);

	/**
	 * 通过主设备ID查询无线配置记录
	 *
	 * @param mainId 主设备ID
	 * @return 无线配置记录列表
	 */
	public List<CpeDeviceWireless> selectByMainId(@Param("mainId") String mainId);

}
