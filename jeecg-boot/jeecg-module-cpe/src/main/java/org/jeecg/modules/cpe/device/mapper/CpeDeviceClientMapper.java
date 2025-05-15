package org.jeecg.modules.cpe.device.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.cpe.device.entity.CpeDeviceClient;

/**
 * @Description: 连接终端
 * @Author: jeecg-boot
 * @Date:   2025-05-15
 * @Version: V1.0
 */
public interface CpeDeviceClientMapper extends BaseMapper<CpeDeviceClient> {

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
    * @return List<CpeDeviceClient>
    */
	public List<CpeDeviceClient> selectByMainId(@Param("mainId") String mainId);

}
