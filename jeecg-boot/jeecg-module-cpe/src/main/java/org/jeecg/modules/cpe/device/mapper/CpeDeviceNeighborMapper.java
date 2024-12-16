package org.jeecg.modules.cpe.device.mapper;

import java.util.List;
import org.jeecg.modules.cpe.device.entity.CpeDeviceNeighbor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: CPE设备邻区信息
 * @Author: jeecg-boot
 * @Date:   2024-12-25
 * @Version: V1.0
 */
public interface CpeDeviceNeighborMapper extends BaseMapper<CpeDeviceNeighbor> {

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
   * @return List<CpeDeviceNeighbor>
   */
	public List<CpeDeviceNeighbor> selectByMainId(@Param("mainId") String mainId);
}
