package org.jeecg.modules.cpe.device.service;

import org.jeecg.modules.cpe.device.entity.CpeDeviceStatus;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * @Description: CPE设备状态表
 * @Author: jeecg-boot
 * @Date:   2024-12-25
 * @Version: V1.0
 */
public interface ICpeDeviceStatusService extends IService<CpeDeviceStatus> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<CpeDeviceStatus>
	 */
	public List<CpeDeviceStatus> selectByMainId(String mainId);

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return void
	 */
	public void deleteByMainId(String mainId);
	
	public void push(String deviceSnParam, String ubusOutputParam, String ipAddrParam, String lteStatus) throws Exception;

	/**
	 * 通过时间戳删除子表数据
	 *
	 * @param deleteBeforTime 删除此时间前的数据
	 * @return void
	 */
	public void deleteByTs(Date deleteBeforTime);

	/**
	 * 通过主表id查询子表最新时间戳
	 *
	 * @param deleteBeforTime 删除此时间前的数据
	 * @return void
	 */
	public Date selectNewtestTsByMainId(String mainId);
}