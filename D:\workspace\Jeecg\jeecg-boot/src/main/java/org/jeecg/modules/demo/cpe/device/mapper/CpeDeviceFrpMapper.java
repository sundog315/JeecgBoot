package org.jeecg.modules.demo.cpe.device.mapper;

import java.util.List;
import org.jeecg.modules.demo.cpe.device.entity.CpeDeviceFrp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 设备远程控制
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
public interface CpeDeviceFrpMapper extends BaseMapper<CpeDeviceFrp> {

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
    * @return List<CpeDeviceFrp>
    */
	public List<CpeDeviceFrp> selectByMainId(@Param("mainId") String mainId);

}
