/*
 * @Author: Janelle.Liu sundog315@foxmail.com
 * @Date: 2025-01-13 23:27:24
 * @LastEditors: Janelle.Liu sundog315@foxmail.com
 * @LastEditTime: 2025-03-13 10:58:37
 * @FilePath: /JeecgBoot/jeecg-boot/jeecg-module-cpe/src/main/java/org/jeecg/modules/cpe/device/controller/CpeDeviceApi.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package org.jeecg.modules.cpe.device.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.cpe.device.entity.CpeDevice;
import org.jeecg.modules.cpe.device.service.ICpeDeviceAutorebootService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceFrpService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceStatusService;
import org.jeecg.modules.cpe.device.service.ICpeSpeedLimitService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceNetworkService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceWirelessService;

import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.config.shiro.IgnoreAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

@Api(tags = "设备上下行API")
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
	@Autowired
	private ICpeDeviceWirelessService cpeDeviceWirelessService;

	/**
	 * 信息上报
	 *
	 * @param request
	 * @return
	 */
	@IgnoreAuth
	@AutoLog(value = "设备状态表-上报")
	@ApiOperation(value = "设备状态表-上报", notes = "设备状态表-上报")
	@RequestMapping(value = "/push", method = { RequestMethod.GET, RequestMethod.POST })
	public Result<String> push(@RequestParam(name = "type", required = true) String deviceType,
			@RequestParam(name = "mac", required = true) String deviceSnParam,
			@RequestParam(name = "ubus_call", required = false) String ubusOutputParam,
			@RequestParam(name = "ip_addr", required = true) String ipAddrParam,
			@RequestParam(name = "lte_status", required = false) String lteStatus,
			@RequestParam(name = "frp", required = false) String frp,
			@RequestParam(name = "auto_reboot", required = false) String autoReboot,
			@RequestParam(name = "network", required = false) String network,
			@RequestParam(name = "speed_limit", required = false) String speedLimitParam,
			@RequestParam(name = "wireless", required = false) String wireless,
			@RequestParam(name = "version", required = false) String version,
			@RequestParam(name = "uptime", required = false) String uptime,
			@RequestParam(name = "clients", required = false) String clients,
			@RequestParam(name = "cpu_temp", required = false) String cpuTemp) {

		switch (deviceType) {
			case "X25":

				break;

			case "YR500":

				break;

			default:
				try {
					cpeDeviceStatusService.push(deviceSnParam, ubusOutputParam, ipAddrParam, lteStatus, version, uptime,
							clients, cpuTemp);
					if ((frp != null) && (!frp.isEmpty()))
						cpeDeviceFrpService.report(deviceSnParam, frp);
					if ((autoReboot != null) && (!autoReboot.isEmpty()))
						cpeDeviceAutorebootService.report(deviceSnParam, autoReboot);
					if ((network != null) && (!network.isEmpty()))
						cpeDeviceNetworkService.report(deviceSnParam, network);
					if ((speedLimitParam != null) && (!speedLimitParam.isEmpty()))
						cpeSpeedLimitService.report(deviceSnParam, speedLimitParam);
					if ((wireless != null) && (!wireless.isEmpty()))
						cpeDeviceWirelessService.report(deviceSnParam, wireless);

					log.info("{}设备状态上报成功", deviceSnParam);
				} catch (Exception e) {
					log.info("{}设备状态上报失败", deviceSnParam);
					return Result.error(e.getMessage());
				}

				break;
		}

		return Result.OK("OK！");
	}

	/**
	 * Entry point for CPE Inform messages.
	 * TR-069 uses HTTP POST for SOAP message exchange.
	 */
	@IgnoreAuth
	@ApiOperation(value = "CWMP SOAP 入口", notes = "接收 CPE 的 Inform 请求并返回 InformResponse")
	@PostMapping(value = "/acs")
	public void handleCwmpRequest(@RequestBody(required = false) String soapBody, HttpServletResponse response)
			throws IOException {
		if (soapBody == null || soapBody.trim().isEmpty()) {
			log.info("Received empty POST - Ending CWMP session");
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;
		}

		log.debug("Received CWMP Message: {}", soapBody);

		String cwmpId = extractTag(soapBody, "cwmp:ID");
		if (cwmpId.isEmpty())
			cwmpId = extractTag(soapBody, "ID");

		if (soapBody.contains("Inform")) {
			handleInform(soapBody, cwmpId, response);
		} else if (soapBody.contains("GetRPCMethods")) {
			handleGetRPCMethods(cwmpId, response);
		} else {
			log.info("Received unhandled CWMP message, ending session with 204");
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
	}

	private void handleGetRPCMethods(String cwmpId, HttpServletResponse response) throws IOException {
		log.info("CPE requested GetRPCMethods");
		String rpcResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">\n"
				+
				"  <soap:Header>\n" +
				"    <cwmp:ID soap:mustUnderstand=\"1\">" + (cwmpId.isEmpty() ? "1" : cwmpId) + "</cwmp:ID>\n" +
				"  </soap:Header>\n" +
				"  <soap:Body>\n" +
				"    <cwmp:GetRPCMethodsResponse>\n" +
				"      <MethodList>\n" +
				"        <string>Inform</string>\n" +
				"        <string>GetRPCMethods</string>\n" +
				"      </MethodList>\n" +
				"    </cwmp:GetRPCMethodsResponse>\n" +
				"  </soap:Body>\n" +
				"</soap:Envelope>";

		response.setContentType("text/xml;charset=UTF-8");
		response.getWriter().write(rpcResponse);
		response.getWriter().flush();
	}

	private void handleInform(String xml, String cwmpId, HttpServletResponse response) throws IOException {
		// Simple regex-based parsing for prototype purposes
		String deviceSn = extractTag(xml, "SerialNumber");
		log.info("CPE Inform received from Device SN: {}", deviceSn);

		// Extract all parameters from ParameterList
		Map<String, String> params = extractAllParameters(xml);
		params.forEach((key, value) -> log.info("[CWMP] {} = {}", key, value));

		// Push data to our new specialized service method
		try {
			cpeDeviceStatusService.pushCwmp(deviceSn, params);
			log.info("TR-069 Data synced (Step 3) for SN: {}", deviceSn);
		} catch (Exception e) {
			log.error("Failed to process TR-069 pushCwmp for SN: {}", deviceSn, e);
		}

		// Return InformResponse
		String informResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:cwmp=\"urn:dslforum-org:cwmp-1-0\">\n"
				+
				"  <soap:Header>\n" +
				"    <cwmp:ID soap:mustUnderstand=\"1\">" + (cwmpId.isEmpty() ? "1" : cwmpId) + "</cwmp:ID>\n" +
				"  </soap:Header>\n" +
				"  <soap:Body>\n" +
				"    <cwmp:InformResponse>\n" +
				"      <MaxEnvelopes>1</MaxEnvelopes>\n" +
				"    </cwmp:InformResponse>\n" +
				"  </soap:Body>\n" +
				"</soap:Envelope>";

		response.setContentType("text/xml;charset=UTF-8");
		response.getWriter().write(informResponse);
		response.getWriter().flush();
	}

	private Map<String, String> extractAllParameters(String xml) {
		Map<String, String> params = new HashMap<>();
		// Match <ParameterValueStruct> ... <Name>...</Name> ... <Value>...</Value> ...
		// </ParameterValueStruct>
		Pattern structPattern = Pattern.compile("<ParameterValueStruct>(.*?)</ParameterValueStruct>", Pattern.DOTALL);
		Matcher structMatcher = structPattern.matcher(xml);
		while (structMatcher.find()) {
			String struct = structMatcher.group(1);
			String name = extractTag(struct, "Name");
			String value = extractTag(struct, "Value");
			if (!name.isEmpty()) {
				params.put(name, value);
			}
		}
		return params;
	}

	private String extractTag(String xml, String tagName) {
		// Handle tags with or without attributes
		Pattern p = Pattern.compile("<" + tagName + "[^>]*>(.*?)</" + tagName + ">", Pattern.DOTALL);
		Matcher m = p.matcher(xml);
		return m.find() ? m.group(1).trim() : "";
	}
}
