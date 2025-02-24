package org.jeecg.modules.demo.cpe.device.mapper;

import java.util.List;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceStatus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: CPE设备状态表
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
public interface CpeDeviceStatusMapper extends BaseMapper<CpeDeviceStatus> {

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
    * @return List<CpeDeviceStatus>
    */
	public List<CpeDeviceStatus> selectByMainId(@Param("mainId") String mainId);

}
