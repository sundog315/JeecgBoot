package org.jeecg.modules.cpe.device.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.cpe.device.entity.CpeOperLog;

/**
 * @Description: 操作记录表
 * @Author: jeecg-boot
 * @Date:   2024-12-30
 * @Version: V1.0
 */
public interface CpeOperLogMapper extends BaseMapper<CpeOperLog> {

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
    * @return List<CpeOperLog>
    */
	public List<CpeOperLog> selectByMainId(@Param("mainId") String mainId);

}
