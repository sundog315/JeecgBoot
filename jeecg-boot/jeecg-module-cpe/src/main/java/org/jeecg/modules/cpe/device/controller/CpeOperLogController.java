package org.jeecg.modules.cpe.device.controller;

import java.util.Date;
import java.util.List;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.cpe.device.entity.CpeDevice;
import org.jeecg.modules.cpe.device.entity.CpeOperLog;
import org.jeecg.modules.cpe.device.service.ICpeDeviceService;
import org.jeecg.modules.cpe.device.service.ICpeOperLogService;

import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.config.shiro.IgnoreAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 设备操作
 * @Author: jeecg-boot
 * @Date:   2024-12-22
 * @Version: V1.0
 */
@Api(tags="设备操作表")
@RestController
@RequestMapping("/cpe/device/cpeOperLog")
@Slf4j
public class CpeOperLogController extends JeecgController<CpeOperLog, ICpeOperLogService> {
	@Autowired
	private ICpeDeviceService cpeDeviceService;
	@Autowired
	private ICpeOperLogService cpeOperLogService;

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
								@RequestParam(name="operid",required=true) String operId,
								@RequestParam(name="retCode",required=true) String retCode,
								@RequestParam(name="output",required=true) String outputLog) {

		switch (deviceType) {
			case "X25":
				
				break;

			case "YR500":
				
				break;
		
			default:
				if (operId != null) {
					try{
						CpeOperLog oper = cpeOperLogService.getById(operId);
						if (outputLog != null)
						{
							oper.setOperRetcode(retCode);
							oper.setOperTs(new Date());
							oper.setOperLog(outputLog);
							
							cpeOperLogService.updateById(oper);
						}
					}
					catch (Exception e) {
						return Result.error(e.getMessage());
					}
				}
				break;
		}

		return Result.OK("OK！");
	}

	/**
	 *   信息查询
	 *
	 * @param request
	 * @return
	 */
	@IgnoreAuth
	@AutoLog(value = "设备操作表-查询")
	@ApiOperation(value="设备操作表-查询", notes="设备操作表-查询")
	@RequestMapping(value = "/pull", method = {RequestMethod.GET,RequestMethod.POST})
	public Result<String> push(@RequestParam(name="type",required=true) String deviceType,
								@RequestParam(name="mac",required=true) String deviceSnParam) {

		switch (deviceType) {
			case "X25":
				
				break;

			case "YR500":
				
				break;
		
			default:
				if (deviceSnParam != null) {
					try{
						List<CpeDevice> deviceList = cpeDeviceService.selectByDeviceSn(deviceSnParam.replace(":", ""));
						for (CpeDevice device : deviceList) {
							List<CpeOperLog> operLogList = cpeOperLogService.selectByMainId(device.getId());
							for (CpeOperLog oper : operLogList)
							{
								if (oper.getOperTs() == null)
								{
									if (oper.getOperParam() != null && !oper.getOperParam().isEmpty()) {
										return Result.OK(oper.getId() + '|' + oper.getOperType() + '|' + oper.getOperParam().replace("|", "%7C").replace(",", "%2C"));
									} else {
										return Result.OK(oper.getId() + '|' + oper.getOperType() + '|');
									}
								}
							}

							break;
						}
					} catch (Exception e) {
						return Result.error(e.getMessage());
					}
				}
				break;
		}

		return Result.OK("OK！");
	}
}
