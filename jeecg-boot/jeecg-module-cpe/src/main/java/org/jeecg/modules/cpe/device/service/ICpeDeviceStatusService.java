/*
 * @Author: Janelle.Liu sundog315@foxmail.com
 * @Date: 2025-03-11 11:16:21
 * @LastEditors: Janelle.Liu sundog315@foxmail.com
 * @LastEditTime: 2025-03-13 11:00:29
 * @FilePath: /JeecgBoot/jeecg-boot/jeecg-module-cpe/src/main/java/org/jeecg/modules/cpe/device/service/ICpeDeviceStatusService.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
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
	
	public void push(String deviceSnParam, String ubusOutputParam, String ipAddrParam, String lteStatus, String openwrtVer, String sysUptime, String clients, String cpuTemp) throws Exception;

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