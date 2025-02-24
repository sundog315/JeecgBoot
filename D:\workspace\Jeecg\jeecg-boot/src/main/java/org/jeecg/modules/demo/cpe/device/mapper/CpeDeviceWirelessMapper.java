package org.jeecg.modules.demo.cpe.device.mapper;

import java.util.List;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceWireless;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 设备无线配置
 * @Author: jeecg-boot
 * @Date:   2025-02-24
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
    * 通过主表id查询子表数据
    *
    * @param mainId 主表id
    * @return List<CpeDeviceWireless>
    */
	public List<CpeDeviceWireless> selectByMainId(@Param("mainId") String mainId);

}
