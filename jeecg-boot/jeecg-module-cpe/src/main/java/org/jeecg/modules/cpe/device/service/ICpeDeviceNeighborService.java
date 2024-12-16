package org.jeecg.modules.cpe.device.service;

import org.jeecg.modules.cpe.device.entity.CpeDeviceNeighbor;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: CPE设备邻区信息
 * @Author: jeecg-boot
 * @Date:   2024-12-25
 * @Version: V1.0
 */
public interface ICpeDeviceNeighborService extends IService<CpeDeviceNeighbor> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<CpeDeviceNeighbor>
	 */
	public List<CpeDeviceNeighbor> selectByMainId(String mainId);

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return void
	 */
	public void deleteByMainId(String mainId);
}
