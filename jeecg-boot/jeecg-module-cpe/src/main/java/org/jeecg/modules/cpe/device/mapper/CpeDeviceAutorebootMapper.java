/*
 * @Author: Janelle.Liu sundog315@foxmail.com
 * @Date: 2025-01-05 15:41:47
 * @LastEditors: Janelle.Liu sundog315@foxmail.com
 * @LastEditTime: 2025-01-20 14:01:05
 * @FilePath: /JeecgBoot/jeecg-boot/jeecg-module-cpe/src/main/java/org/jeecg/modules/cpe/device/mapper/CpeDeviceAutorebootMapper.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package org.jeecg.modules.cpe.device.mapper;

import java.util.List;
import org.jeecg.modules.cpe.device.entity.CpeDeviceAutoreboot;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 设备自动重启
 * @Author: jeecg-boot
 * @Date:   2025-01-05
 * @Version: V1.0
 */
public interface CpeDeviceAutorebootMapper extends BaseMapper<CpeDeviceAutoreboot> {

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
    * @return List<CpeDeviceAutoreboot>
    */
	public List<CpeDeviceAutoreboot> selectByMainId(@Param("mainId") String mainId);

}
