package org.jeecg.modules.cpe.contract.info.mapper;

import java.util.List;
import org.jeecg.modules.cpe.contract.info.entity.ContractDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 合同设备表
 * @Author: jeecg-boot
 * @Date:   2025-10-11
 * @Version: V1.0
 */
public interface ContractDeviceMapper extends BaseMapper<ContractDevice> {

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
    * @return List<ContractDevice>
    */
	public List<ContractDevice> selectByMainId(@Param("mainId") String mainId);

}
