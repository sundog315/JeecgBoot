package org.jeecg.modules.cpe.device.mapper;

import java.util.Date;
import java.util.List;
import org.jeecg.modules.cpe.device.entity.CpeDeviceStatus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: CPE设备状态表
 * @Author: jeecg-boot
 * @Date:   2024-12-25
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

	/**
	 * 通过时间戳删除子表数据
	 *
	 * @param deleteBeforTime 删除此时间前的历史数据
	 * @return boolean
	 */
	public boolean deleteByTs(@Param("deleteBeforTime") Date deleteBeforTime);

  	/**
   * 通过主表id查询最新的状态报告时间
   *
   * @param mainId 主表id
   * @return Date
   */
  	public Date selectNewtestTsByMainId(@Param("mainId") String mainId);
}
