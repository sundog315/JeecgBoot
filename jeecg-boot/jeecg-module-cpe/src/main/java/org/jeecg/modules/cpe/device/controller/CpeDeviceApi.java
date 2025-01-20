/*
 * @Author: Janelle.Liu sundog315@foxmail.com
 * @Date: 2025-01-13 23:27:24
 * @LastEditors: Janelle.Liu sundog315@foxmail.com
 * @LastEditTime: 2025-01-20 13:59:36
 * @FilePath: /JeecgBoot/jeecg-boot/jeecg-module-cpe/src/main/java/org/jeecg/modules/cpe/device/controller/CpeDeviceApi.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package org.jeecg.modules.cpe.device.controller;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.cpe.device.entity.CpeDevice;
import org.jeecg.modules.cpe.device.service.ICpeDeviceAutorebootService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceFrpService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceStatusService;
import org.jeecg.modules.cpe.device.service.ICpeSpeedLimitService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceNetworkService;

import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.config.shiro.IgnoreAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

@Api(tags="设备上下行API")
@RestController
@RequestMapping("/cpe/device/api")
@Slf4j
public class CpeDeviceApi extends JeecgController<CpeDevice, ICpeDeviceService> {
	@Autowired
	private ICpeDeviceStatusService cpeDeviceStatusService;
	@Autowired
	private ICpeDeviceFrpService cpeDeviceFrpService;
	@Autowired
	private ICpeDeviceAutorebootService cpeDeviceAutorebootService;
	@Autowired
	private ICpeDeviceNetworkService cpeDeviceNetworkService;
	@Autowired
	private ICpeSpeedLimitService cpeSpeedLimitService;


	/**
	 *   信息上报
	 *
	 * @param request
	 * @return
	 */
	@IgnoreAuth
	@AutoLog(value = "设备状态表-上报")
	@ApiOperation(value="设备状态表-上报", notes="设备状态表-上报")
	@RequestMapping(value = "/push", method = {RequestMethod.GET,RequestMethod.POST})
	public Result<String> push(@RequestParam(name="type",required=true) String deviceType,
								@RequestParam(name="mac",required=true) String deviceSnParam,
								@RequestParam(name="ubus_call",required=true) String ubusOutputParam,
								@RequestParam(name="ip_addr",required=true) String ipAddrParam,
								@RequestParam(name="lte_status",required=false) String lteStatus,
								@RequestParam(name="frp",required=false) String frp,
								@RequestParam(name="auto_reboot",required=false) String autoReboot,
								@RequestParam(name="network",required=false) String network,
								@RequestParam(name="speed_limit",required=false) String speedLimitParam) {

		switch (deviceType) {
			case "X25":

				break;

			case "YR500":

				break;

			default:
				try{
					cpeDeviceStatusService.push(deviceSnParam, ubusOutputParam, ipAddrParam, lteStatus);
					if ((frp != null) && (!frp.isEmpty()))
						cpeDeviceFrpService.report(deviceSnParam, frp);
					if ((autoReboot != null) && (!autoReboot.isEmpty()))
						cpeDeviceAutorebootService.report(deviceSnParam, autoReboot);
					if ((network != null) && (!network.isEmpty()))
						cpeDeviceNetworkService.report(deviceSnParam, network);
						if ((speedLimitParam != null) && (!speedLimitParam.isEmpty()))
						cpeSpeedLimitService.report(deviceSnParam, speedLimitParam);

					log.info("{}设备状态上报成功",deviceSnParam);
				}
				catch (Exception e) {
					log.info("{}设备状态上报失败",deviceSnParam);
					return Result.error(e.getMessage());
				}

				break;
		}

		return Result.OK("OK！");
	}
}
