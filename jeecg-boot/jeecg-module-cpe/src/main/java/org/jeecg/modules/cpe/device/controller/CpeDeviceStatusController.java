package org.jeecg.modules.cpe.device.controller;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.cpe.device.entity.CpeDeviceStatus;
import org.jeecg.modules.cpe.device.service.ICpeDeviceStatusService;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.config.shiro.IgnoreAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 设备状态表
 * @Author: jeecg-boot
 * @Date:   2024-12-22
 * @Version: V1.0
 */
@Api(tags="设备状态表")
@RestController
@RequestMapping("/cpe/device/cpeDeviceStatus")
@Slf4j
public class CpeDeviceStatusController extends JeecgController<CpeDeviceStatus, ICpeDeviceStatusService> {
	@Autowired
	private ICpeDeviceStatusService cpeDeviceStatusService;

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
								@RequestParam(name="lte_status",required=false) String lteStatus) {

		switch (deviceType) {
			case "X25":
				
				break;

			case "YR500":
				
				break;
		
			default:
				if (deviceSnParam != null) {
					try{
						cpeDeviceStatusService.push(deviceSnParam, ubusOutputParam, ipAddrParam, lteStatus);
					}
					catch (Exception e) {
						return Result.error(e.getMessage());
					}
				}
				break;
		}

		return Result.OK("OK！");
	}
}
