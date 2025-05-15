package org.jeecg.modules.cpe.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

import org.jeecg.modules.cpe.device.entity.CpeDeviceClient;

/**
 * @Description: 连接终端
 * @Author: jeecg-boot
 * @Date:   2025-05-15
 * @Version: V1.0
 */
public interface ICpeDeviceClientService extends IService<CpeDeviceClient> {

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId
   * @return List<CpeDeviceClient>
   */
	public List<CpeDeviceClient> selectByMainId(String mainId);

  /**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return void
	 */
	public void deleteByMainId(String mainId);
}
