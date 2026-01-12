package org.jeecg.modules.cpe.device.service.impl;

import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.cpe.card.entity.CardInfo;
import org.jeecg.modules.cpe.card.service.ICardInfoService;
import org.jeecg.modules.cpe.device.entity.CpeDevice;
import org.jeecg.modules.cpe.device.entity.CpeDeviceClient;
import org.jeecg.modules.cpe.device.entity.CpeDeviceNeighbor;
import org.jeecg.modules.cpe.device.entity.CpeDeviceStatus;
import org.jeecg.modules.cpe.device.mapper.CpeDeviceClientMapper;
import org.jeecg.modules.cpe.device.mapper.CpeDeviceStatusMapper;
import org.jeecg.modules.cpe.device.service.ICpeDeviceNeighborService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Collections;
import java.util.HashMap;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * CPE设备状态服务实现类
 * 负责处理设备状态、网络数据、信号强度等信息的更新和存储
 *
 * @author liulei
 * @version V1.0
 * @date 2024-12-25
 */
@Service
public class CpeDeviceStatusServiceImpl extends ServiceImpl<CpeDeviceStatusMapper, CpeDeviceStatus>
		implements ICpeDeviceStatusService {

	@Autowired
	private CpeDeviceStatusMapper cpeDeviceStatusMapper;
	@Autowired
	private ICpeDeviceService cpeDeviceService;
	@Autowired
	private ICpeDeviceNeighborService cpeDeviceNeighborService;
	@Autowired
	private ICardInfoService cardInfoService;
	@Autowired
	private CpeDeviceClientMapper cpeDeviceClientMapper;
	@Autowired
	private RedisUtil redisUtil;

	// 常量定义
	private static final String ADMIN_USER = "admin";
	private static final String SYS_ORG_CODE = "A01";
	private static final String UNKNOWN = "UnKnow";
	private static final int LOCK_TIMEOUT_SECONDS = 5;

	private class CpeBaseData {
		/** 设备状态相关字段 */
		public int sim_slot = 0;
		public String imei = "";
		public String version = "";
		public String iccid = "";
		public String module = "";
		public String lte_5g = "";
		public String lte_4g = "";
		public String lte_cell = "";
		public String lte_cainfo = "";
		public String lte_freq = "";
		public String lte_cops = "";
		public String uptime = "";
		public String ipv4 = "";
		public String ipv6 = "";
		public Double upBytes = 0.0;
		public Double downBytes = 0.0;
		public String deviceSn = "";
		public String dns1 = "";
		public String dns2 = "";
		public String openwrtVersion = "";
		public String sysUptime = "";
		public Integer clientCount = 0;
		public Integer cpuTemp = 0;
	}

	/**
	 * 无线接入技术(RAT)类型映射
	 * 将数字代码映射到对应的网络制式
	 */
	private static final Map<String, String> RAT_MAP;
	static {
		Map<String, String> map = new HashMap<>();
		map.put("1", "GSM");
		map.put("2", "WCDMA");
		map.put("3", "TDSCDMA");
		map.put("4", "LTE");
		map.put("5", "eMTC");
		map.put("6", "NB-IoT");
		map.put("7", "CDMA");
		map.put("8", "EVDO");
		map.put("9", "NR-RAN");
		map.put("NR5G-SA", "NR5G-SA");
		map.put("LTE", "LTE");
		RAT_MAP = Collections.unmodifiableMap(map);
	}

	/**
	 * 信号质量相关映射
	 * SINR: 信噪比
	 * RSRP: 参考信号接收功率
	 * RSRQ: 参考信号接收质量
	 */
	private static final Map<String, String> SINR_MAP;
	private static final Map<String, String> RSRP_MAP;
	private static final Map<String, String> RSRQ_MAP;
	private static final Map<String, String> BANDWIDTH_MAP_NR;
	private static final Map<String, String> BANDWIDTH_MAP_LTE;
	static {
		// SINR映射初始化
		Map<String, String> mapSinr = new HashMap<>();
		mapSinr.put("0", "<-23dB");
		mapSinr.put("127", ">40dB");
		mapSinr.put("255", UNKNOWN);
		SINR_MAP = Collections.unmodifiableMap(mapSinr);

		// RSRP映射初始化
		Map<String, String> mapRsrp = new HashMap<>();
		mapRsrp.put("0", "<-156dBm");
		mapRsrp.put("126", ">-31dBm");
		mapRsrp.put("255", UNKNOWN);
		RSRP_MAP = Collections.unmodifiableMap(mapRsrp);

		// RSRQ映射初始化
		Map<String, String> mapRsrq = new HashMap<>();
		mapRsrq.put("0", "<-43dB");
		mapRsrq.put("255", UNKNOWN);
		RSRQ_MAP = Collections.unmodifiableMap(mapRsrq);

		// 带宽映射初始化
		Map<String, String> mapNr = new HashMap<>();
		mapNr.put("6", "1.4 MHz");
		mapNr.put("15", "15 MHz");
		mapNr.put("25", "25 MHz");
		mapNr.put("50", "50 MHz");
		mapNr.put("75", "15 MHz");
		mapNr.put("100", "100 MHz");
		BANDWIDTH_MAP_NR = Collections.unmodifiableMap(mapNr);

		Map<String, String> mapLte = new HashMap<>();
		mapLte.put("6", "1.4 MHz");
		mapLte.put("15", "3 MHz");
		mapLte.put("25", "5 MHz");
		mapLte.put("50", "10 MHz");
		mapLte.put("75", "15 MHz");
		mapLte.put("100", "20 MHz");
		BANDWIDTH_MAP_LTE = Collections.unmodifiableMap(mapLte);
	}

	// 设备锁管理器
	private final ConcurrentHashMap<String, Lock> deviceLocks = new ConcurrentHashMap<>();

	/**
	 * 获取设备锁
	 * 
	 * @param deviceSn 设备序列号
	 * @return 设备对应的锁对象
	 */
	private Lock getDeviceLock(String deviceSn) {
		return deviceLocks.computeIfAbsent(deviceSn, k -> new ReentrantLock());
	}

	/**
	 * 根据主ID查询设备状态列表
	 * 
	 * @param mainId 主设备ID
	 * @return List<CpeDeviceStatus> 设备状态列表
	 */
	@Override
	public List<CpeDeviceStatus> selectByMainId(String mainId) {
		return cpeDeviceStatusMapper.selectByMainId(mainId);
	}

	@Override
	public void deleteByMainId(String mainId) {
		cpeDeviceStatusMapper.deleteByMainId(mainId);
	}

	/**
	 * 格式化带宽信息
	 * 
	 * @param str 带宽值
	 * @return 格式化后的带宽字符串
	 */
	private String bandwidth(String str, CpeBaseData data) {
		// 空值检查
		if (str.isEmpty())
			return "";

		// 判断网络类型（NR或LTE）
		boolean isNrCell = data.lte_cell.indexOf("NR service cell") > 0;
		// 选择对应的带宽映射表
		Map<String, String> currentMap = isNrCell ? BANDWIDTH_MAP_NR : BANDWIDTH_MAP_LTE;

		// 返回格式化后的带宽值
		return currentMap.getOrDefault(str, str + " MHz");
	}

	/**
	 * 转换RAT类型代码为可读文本
	 * 
	 * @param str RAT类型代码
	 * @return 网络制式名称
	 */
	private String rat(String str) {
		if (str.isEmpty())
			return "";
		return RAT_MAP.getOrDefault(str, UNKNOWN);
	}

	/**
	 * 信号强度转换：SINR（信号与干扰加噪声比）
	 *
	 * @param str 原始SINR值字符串
	 * @return String 转换后的SINR值，格式为"xxxdB"
	 */
	private String ssSinr(String str) {
		if (str.isEmpty())
			return "";
		return SINR_MAP.getOrDefault(str,
				String.valueOf((Integer.parseInt(str) * 0.5 - 23)) + "dB");
	}

	/**
	 * 信号强度转换：RSRP（参考信号接收功率）
	 *
	 * @param str 原始RSRP值字符串
	 * @return String 转换后的RSRP值，格式为"xxxdBm"
	 */
	private String ssRsrp(String str) {
		if (str.isEmpty())
			return "";
		return RSRP_MAP.getOrDefault(str,
				String.valueOf((Integer.parseInt(str) - 156)) + "dBm");
	}

	/**
	 * 信号强度转换：RSRQ（参考信号接收质量）
	 *
	 * @param str 原始RSRQ值字符串
	 * @return String 转换后的RSRQ值，格式为"xxxdB"
	 */
	private String ssRsrq(String str) {
		if (str.isEmpty())
			return "";
		return RSRQ_MAP.getOrDefault(str,
				String.valueOf((Integer.parseInt(str) * 0.5 - 43)) + "dB");
	}

	/**
	 * 处理5G网络服务小区信息
	 * 解析并存储5G网络相关的状态数据
	 *
	 * @param cpeDevice    设备对象
	 * @param newCpeDevice 新的设备状态
	 * @param ipAddrParam  IP地址参数
	 * @param lteStatus    LTE状态信息
	 * @throws Exception 处理异常时抛出
	 */
	private void service_nr(CpeDevice cpeDevice, CpeDevice newCpeDevice, String ipAddrParam, String lteStatus,
			CpeBaseData data) throws Exception {
		// if (data.lte_cell == null || data.lte_cell.length() == 0)
		// return;

		if ((data.lte_cell.indexOf("servingcell") == 0)
				&& (data.lte_cell.indexOf("NR") == 0)
				&& (data.lte_cell.indexOf("LTE") == 0)
				&& (data.lte_cell.indexOf("WCDMA") == 0))
			return;

		if (data.module.startsWith("FM")) {
			// 解析第4行的NR信息（0-based index）
			String nr_info = data.lte_cell.split("\\r\\n")[3];
			String[] items = nr_info.split(",");

			// 解析基本服务小区信息
			String isServiceCell = items[0]; // 服务小区标识
			newCpeDevice.setDeviceStatusNo(isServiceCell);

			// 解析并转换网络参数
			String rat = rat(items[1]); // 无线接入技术类型
			String mcc = items[2]; // 移动国家代码
			String mnc = items[3]; // 移动网络代码
			String tac = items[4]; // 跟踪区代码
			String cellid = items[5]; // 小区ID
			String narfcn = items[6]; // 绝对射频信道号
			String physicalcellId = items[7]; // 物理小区ID
			String band = items[8]; // 频段
			// if (lte_cell.indexOf("NR service cell") == -1)
			// band += "Mhz";
			newCpeDevice.setOnlineBand(band);
			// 格式化带宽信息
			String bandwidth = bandwidth(items[9], data);

			// 解析信号质量参数
			String sinr = ssSinr(items[10]); // 信噪比
			String rxlev = items[11]; // 接收电平
			String rsrp = ssRsrp(items[12]); // 参考信号接收功率
			String rsrq = ssRsrq(items[13]); // 参考信号接收质量

			// 解析运营商信息
			String cops = "";
			if (data.lte_cops != null && data.lte_cops.length() > 0) {
				try {
					// 从COPS响应中提取运营商信息
					String[] cops_items = data.lte_cops.split("\\r\\n");
					cops = cops_items[1].split(",")[2].replace("\\\"", "");

					// 根据运营商名称设置网络代码
					if (cops.indexOf("UNICOM") > 0)
						newCpeDevice.setOnlineNetNo("unicom");
					if (cops.indexOf("TELECOM") > 0)
						newCpeDevice.setOnlineNetNo("telecom");
					if (cops.indexOf("MOBILE") > 0)
						newCpeDevice.setOnlineNetNo("mobile");
					if (cops.indexOf("BROADNET") > 0)
						newCpeDevice.setOnlineNetNo("board");
				} catch (Exception ex) {
					// 运营商信息解析失败，继续处理其他数据
				}
			}

			// 更新设备基本信息
			cpeDeviceService.updateById(newCpeDevice);

			// 解析载波聚合信息
			String caBand = "";
			if (data.lte_cainfo != null && data.lte_cainfo.length() > 0) {
				if (data.lte_cainfo.split("\\r\\n").length > 2) {
					try {
						// 提取主载波(PCC)信息
						String[] cainfo_items = data.lte_cainfo.split("\\r\\n")[2].split(",")[0].split(":");
						if (cainfo_items[0].equals("PCC")) {
							caBand = cainfo_items[1];
						}
					} catch (Exception ex) {
						// 载波聚合信息解析失败，继续处理其他数据
					}
				}
			}

			// 处理IP地址和流量信息
			if (ipAddrParam != null && ipAddrParam.length() > 0) {
				String[] ipaddr = ipAddrParam.split(",");
				data.ipv4 = ipaddr[0]; // IPv4地址
				data.ipv6 = ipaddr[1]; // IPv6地址
				data.upBytes = Double.parseDouble(ipaddr[2]);
				data.downBytes = Double.parseDouble(ipaddr[3]);

				// 处理流量统计数据
				processTrafficData(ipaddr, data.iccid, cpeDevice, data);
			}

			// 处理LTE状态信息
			if (lteStatus != null && !lteStatus.isEmpty()) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					@SuppressWarnings("unchecked")
					Map<String, Object> lteStatusMap = objectMapper.readValue(lteStatus, Map.class);

					// 提取运行时间和DNS服务器信息
					data.uptime = lteStatusMap.containsKey("uptime") ? lteStatusMap.get("uptime").toString()
							: data.uptime;
					if (lteStatusMap.containsKey("dns-server")) {
						@SuppressWarnings("unchecked")
						ArrayList<String> dnsList = (ArrayList<String>) (lteStatusMap.get("dns-server"));

						// 设置DNS服务器地址
						if (!dnsList.isEmpty()) {
							data.dns1 = dnsList.get(0).toString();

							if (dnsList.size() > 1)
								data.dns2 = dnsList.get(1).toString();
						}
					}
				} catch (IOException e) {
					log.error("Error parsing lteStatus JSON", e);
					throw new Exception("JSON解析错误！");
				}
			}

			// 创建设备状态记录对象
			CpeDeviceStatus cpeDeviceStatus = new CpeDeviceStatus();
			cpeDeviceStatus.setIpv4(data.ipv4);
			cpeDeviceStatus.setIpv6(data.ipv6);
			cpeDeviceStatus.setUptime(data.uptime);
			cpeDeviceStatus.setUpBytes(data.upBytes);
			cpeDeviceStatus.setDownBytes(data.downBytes);
			cpeDeviceStatus.setCops(cops);
			cpeDeviceStatus.setOnlineBand(band);
			cpeDeviceStatus.setCaBand(caBand);
			cpeDeviceStatus.setCellId(cellid);
			cpeDeviceStatus.setCpeId(cpeDevice.getId());
			cpeDeviceStatus.setDeviceSn(data.deviceSn);
			cpeDeviceStatus.setIccid(data.iccid);
			cpeDeviceStatus.setImei(data.imei);
			cpeDeviceStatus.setLinkStatus(isServiceCell);
			cpeDeviceStatus.setMcc(mcc);
			cpeDeviceStatus.setMnc(mnc);
			cpeDeviceStatus.setModemVersion(data.version);
			cpeDeviceStatus.setPcid(physicalcellId);
			cpeDeviceStatus.setRsrp(rsrp);
			cpeDeviceStatus.setRsrq(rsrq);
			cpeDeviceStatus.setSimSlot(data.sim_slot);
			cpeDeviceStatus.setSinr(sinr);
			cpeDeviceStatus.setStatus(isServiceCell);
			cpeDeviceStatus.setTac(tac);
			cpeDeviceStatus.setBandwidth(bandwidth);
			cpeDeviceStatus.setArfcn(narfcn);
			cpeDeviceStatus.setRxlev(rxlev);
			cpeDeviceStatus.setRat(rat);
			cpeDeviceStatus.setSysOrgCode(SYS_ORG_CODE);
			cpeDeviceStatus.setTs(new Date());
			cpeDeviceStatus.setCreateBy(ADMIN_USER);
			cpeDeviceStatus.setCreateTime(new Date());
			cpeDeviceStatus.setUpdateBy(ADMIN_USER);
			cpeDeviceStatus.setUpdateTime(new Date());
			cpeDeviceStatus.setDns1(data.dns1);
			cpeDeviceStatus.setDns2(data.dns2);
			cpeDeviceStatus.setOpenwrtVersion(data.openwrtVersion);
			cpeDeviceStatus.setSysUptime(data.sysUptime);
			cpeDeviceStatus.setClientCount(data.clientCount);
			cpeDeviceStatus.setCpuTemp(data.cpuTemp);

			// 保存设备状态记录
			save(cpeDeviceStatus);

			// 处理邻区信息
			String[] ne_info;
			if (data.lte_cell.indexOf("NR neighbor cell:") > 0)
				ne_info = data.lte_cell
						.substring(data.lte_cell.indexOf("NR neighbor cell:") + 2, data.lte_cell.length())
						.split("\\r\\n");
			else
				ne_info = data.lte_cell
						.substring(data.lte_cell.indexOf("LTE neighbor cell:") + 2, data.lte_cell.length())
						.split("\\r\\n");
			cpeDeviceNeighborService.deleteByMainId(cpeDevice.getId());
			if (ne_info.length > 1) {
				Collection<CpeDeviceNeighbor> cpeDeviceNeighborList = new ArrayList<>();
				for (int i = 1; i < ne_info.length - 2; i++) {
					String[] ne_items = ne_info[i].split(",");
					String ne_isServiceCell = ne_items[0];
					String ne_rat = rat(ne_items[1]);
					String ne_mcc = ne_items[2];
					String ne_mnc = ne_items[3];
					String ne_tac = ne_items[4];
					String ne_cellid = ne_items[5];
					String ne_narfcn = ne_items[6];
					String ne_physicalcellId = ne_items[7];
					String ne_sinr = ssSinr(ne_items[8]);
					String ne_rxlev = ne_items[9];
					String ne_rsrp = ssRsrp(ne_items[10]);
					String ne_rsrq = ssRsrq(ne_items[11]);

					CpeDeviceNeighbor cpeDeviceNeighbor = new CpeDeviceNeighbor();
					cpeDeviceNeighbor.setCpeId(cpeDevice.getId());
					cpeDeviceNeighbor.setArfcn(ne_narfcn);
					cpeDeviceNeighbor.setCellid(ne_cellid);
					cpeDeviceNeighbor.setMcc(ne_mcc);
					cpeDeviceNeighbor.setMnc(ne_mnc);
					cpeDeviceNeighbor.setPhysicalcellid(ne_physicalcellId);
					cpeDeviceNeighbor.setRat(ne_rat);
					cpeDeviceNeighbor.setRsrp(ne_rsrp);
					cpeDeviceNeighbor.setRsrq(ne_rsrq);
					cpeDeviceNeighbor.setRxlev(ne_rxlev);
					if (data.lte_cell.indexOf("NR service cell") > 0)
						cpeDeviceNeighbor.setSinr(ne_sinr);
					cpeDeviceNeighbor.setStatus(ne_isServiceCell);
					cpeDeviceNeighbor.setTac(ne_tac);
					cpeDeviceNeighbor.setCreateBy("admin");
					cpeDeviceNeighbor.setCreateTime(new Date());
					cpeDeviceNeighbor.setUpdateBy(ADMIN_USER);
					cpeDeviceNeighbor.setUpdateTime(new Date());
					cpeDeviceNeighbor.setSysOrgCode(ADMIN_USER);

					cpeDeviceNeighborList.add(cpeDeviceNeighbor);
				}
				cpeDeviceNeighborService.saveBatch(cpeDeviceNeighborList);
			}
		} else if (data.module.startsWith("RM")) {
			// 解析第4行的NR信息（0-based index）
			String nr_info = data.lte_cell.split("\\r\\n")[1];
			String[] items = nr_info.split(",");

			// 修改为使用普通for循环来更新数组元素
			for (int i = 0; i < items.length; i++) {
				items[i] = items[i].replace("\"", "");
			}

			// 解析基本服务小区信息
			String isServiceCell = items[1]; // 服务小区标识
			if (isServiceCell.equals("CONNECT")) {
				newCpeDevice.setDeviceStatusNo("1");
			} else {
				newCpeDevice.setDeviceStatusNo("0");
			}

			// 解析并转换网络参数
			String rat = rat(items[2]); // 无线接入技术类型
			String mcc = items[4]; // 移动国家代码
			String mnc = items[5]; // 移动网络代码
			String cellid = items[6]; // 小区ID
			String physicalcellId = items[7]; // 物理小区ID
			String band = items[10]; // 频段
			// if (lte_cell.indexOf("NR service cell") == -1)
			// band += "Mhz";
			newCpeDevice.setOnlineBand(band);
			// 格式化带宽信息
			String bandwidth = items[11];

			String tac = items[8]; // 跟踪区代码
			String narfcn = items[9]; // 绝对射频信道号
			String rsrp = items[12]; // 参考信号接收功率
			String rsrq = items[13]; // 参考信号接收质量
			String sinr = items[14]; // 信噪比
			String rxlev = items[16]; // 接收电平
			Integer scs = Integer.parseInt(items[17]);

			if (rat.equals("LTE")) {
				tac = items[12]; // 跟踪区代码
				narfcn = items[8]; // 绝对射频信道号
				rsrp = items[13]; // 参考信号接收功率
				rsrq = items[14]; // 参考信号接收质量
				sinr = items[16]; // 信噪比
				rxlev = items[19]; // 接收电平
				band = items[9]; // 频段
				// if (lte_cell.indexOf("NR service cell") == -1)
				// band += "Mhz";
				newCpeDevice.setOnlineBand(band);
			}

			// 解析信号质量参数

			// 解析运营商信息
			String cops = "";
			if (data.lte_cops != null && data.lte_cops.length() > 0) {
				try {
					// 从COPS响应中提取运营商信息
					String[] cops_items = data.lte_cops.split("\\r\\n");
					cops = cops_items[1].split(",")[2].replace("\\\"", "");

					// 根据运营商名称设置网络代码
					if (cops.indexOf("UNICOM") > 0)
						newCpeDevice.setOnlineNetNo("unicom");
					if (cops.indexOf("TELECOM") > 0)
						newCpeDevice.setOnlineNetNo("telecom");
					if (cops.indexOf("MOBILE") > 0)
						newCpeDevice.setOnlineNetNo("mobile");
					if (cops.indexOf("BROADNET") > 0)
						newCpeDevice.setOnlineNetNo("board");
				} catch (Exception ex) {
					// 运营商信息解析失败，继续处理其他数据
				}
			}

			// 更新设备基本信息
			cpeDeviceService.updateById(newCpeDevice);

			// 解析载波聚合信息
			String caBand = "";
			if (data.lte_cainfo != null && data.lte_cainfo.length() > 0) {
				if (data.lte_cainfo.split("\\r\\n").length > 2) {
					try {
						// 提取主载波(PCC)信息
						String[] cainfo_items = data.lte_cainfo.split("\\r\\n")[2].split(",")[0].split(":");
						if (cainfo_items[0].equals("PCC")) {
							caBand = cainfo_items[1];
						}
					} catch (Exception ex) {
						// 载波聚合信息解析失败，继续处理其他数据
					}
				}
			}

			// 处理IP地址和流量信息
			if (ipAddrParam != null && ipAddrParam.length() > 0) {
				String[] ipaddr = ipAddrParam.split(",");
				data.ipv4 = ipaddr[0]; // IPv4地址
				data.ipv6 = ipaddr[1]; // IPv6地址
				data.upBytes = Double.parseDouble(ipaddr[2]);
				data.downBytes = Double.parseDouble(ipaddr[3]);

				// 处理流量统计数据
				processTrafficData(ipaddr, data.iccid, cpeDevice, data);
			}

			// 处理LTE状态信息
			if (lteStatus != null && !lteStatus.isEmpty()) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					@SuppressWarnings("unchecked")
					Map<String, Object> lteStatusMap = objectMapper.readValue(lteStatus, Map.class);

					// 提取运行时间和DNS服务器信息
					data.uptime = lteStatusMap.containsKey("uptime") ? lteStatusMap.get("uptime").toString()
							: data.uptime;
					if (lteStatusMap.containsKey("dns-server")) {
						@SuppressWarnings("unchecked")
						ArrayList<String> dnsList = (ArrayList<String>) (lteStatusMap.get("dns-server"));

						// 设置DNS服务器地址
						if (!dnsList.isEmpty()) {
							data.dns1 = dnsList.get(0).toString();

							if (dnsList.size() > 1)
								data.dns2 = dnsList.get(1).toString();
						}
					}
				} catch (IOException e) {
					log.error("Error parsing lteStatus JSON", e);
					throw new Exception("JSON解析错误！");
				}
			}

			// 创建设备状态记录对象
			CpeDeviceStatus cpeDeviceStatus = new CpeDeviceStatus();
			cpeDeviceStatus.setIpv4(data.ipv4);
			cpeDeviceStatus.setIpv6(data.ipv6);
			cpeDeviceStatus.setUptime(data.uptime);
			cpeDeviceStatus.setUpBytes(data.upBytes);
			cpeDeviceStatus.setDownBytes(data.downBytes);
			cpeDeviceStatus.setCops(cops);
			cpeDeviceStatus.setOnlineBand(band);
			cpeDeviceStatus.setCaBand(caBand);
			cpeDeviceStatus.setCellId(cellid);
			cpeDeviceStatus.setCpeId(cpeDevice.getId());
			cpeDeviceStatus.setDeviceSn(data.deviceSn);
			cpeDeviceStatus.setIccid(data.iccid);
			cpeDeviceStatus.setImei(data.imei);
			cpeDeviceStatus.setLinkStatus(isServiceCell);
			cpeDeviceStatus.setMcc(mcc);
			cpeDeviceStatus.setMnc(mnc);
			cpeDeviceStatus.setModemVersion(data.version);
			cpeDeviceStatus.setPcid(physicalcellId);
			cpeDeviceStatus.setRsrp(rsrp);
			cpeDeviceStatus.setRsrq(rsrq);
			cpeDeviceStatus.setSimSlot(data.sim_slot);
			cpeDeviceStatus.setSinr(sinr);
			cpeDeviceStatus.setStatus(isServiceCell);
			cpeDeviceStatus.setTac(tac);
			cpeDeviceStatus.setBandwidth(bandwidth);
			cpeDeviceStatus.setArfcn(narfcn);
			cpeDeviceStatus.setRxlev(rxlev);
			cpeDeviceStatus.setRat(rat);
			cpeDeviceStatus.setSysOrgCode(SYS_ORG_CODE);
			cpeDeviceStatus.setTs(new Date());
			cpeDeviceStatus.setCreateBy(ADMIN_USER);
			cpeDeviceStatus.setCreateTime(new Date());
			cpeDeviceStatus.setUpdateBy(ADMIN_USER);
			cpeDeviceStatus.setUpdateTime(new Date());
			cpeDeviceStatus.setDns1(data.dns1);
			cpeDeviceStatus.setDns2(data.dns2);
			cpeDeviceStatus.setOpenwrtVersion(data.openwrtVersion);
			cpeDeviceStatus.setSysUptime(data.sysUptime);
			cpeDeviceStatus.setClientCount(data.clientCount);
			cpeDeviceStatus.setScs(scs);
			cpeDeviceStatus.setCpuTemp(data.cpuTemp);

			// 保存设备状态记录
			save(cpeDeviceStatus);

			// // 处理邻区信息
			// String[] ne_info;
			// if (lte_cell.indexOf("NR neighbor cell:") > 0)
			// ne_info = lte_cell.substring(lte_cell.indexOf("NR neighbor cell:") + 2,
			// lte_cell.length()).split("\\r\\n");
			// else
			// ne_info = lte_cell.substring(lte_cell.indexOf("LTE neighbor cell:") + 2,
			// lte_cell.length()).split("\\r\\n");
			// cpeDeviceNeighborService.deleteByMainId(cpeDevice.getId());
			// if (ne_info.length > 1)
			// {
			// Collection<CpeDeviceNeighbor> cpeDeviceNeighborList = new ArrayList<>();
			// for (int i = 1; i < ne_info.length - 2; i++)
			// {
			// String[] ne_items = ne_info[i].split(",");
			// String ne_isServiceCell = ne_items[0];
			// String ne_rat = rat(ne_items[1]);
			// String ne_mcc = ne_items[2];
			// String ne_mnc = ne_items[3];
			// String ne_tac = ne_items[4];
			// String ne_cellid = ne_items[5];
			// String ne_narfcn = ne_items[6];
			// String ne_physicalcellId = ne_items[7];
			// String ne_sinr = ssSinr(ne_items[8]);
			// String ne_rxlev = ne_items[9];
			// String ne_rsrp = ssRsrp(ne_items[10]);
			// String ne_rsrq = ssRsrq(ne_items[11]);

			// CpeDeviceNeighbor cpeDeviceNeighbor = new CpeDeviceNeighbor();
			// cpeDeviceNeighbor.setCpeId(cpeDevice.getId());
			// cpeDeviceNeighbor.setArfcn(ne_narfcn);
			// cpeDeviceNeighbor.setCellid(ne_cellid);
			// cpeDeviceNeighbor.setMcc(ne_mcc);
			// cpeDeviceNeighbor.setMnc(ne_mnc);
			// cpeDeviceNeighbor.setPhysicalcellid(ne_physicalcellId);
			// cpeDeviceNeighbor.setRat(ne_rat);
			// cpeDeviceNeighbor.setRsrp(ne_rsrp);
			// cpeDeviceNeighbor.setRsrq(ne_rsrq);
			// cpeDeviceNeighbor.setRxlev(ne_rxlev);
			// if (lte_cell.indexOf("NR service cell") >0)
			// cpeDeviceNeighbor.setSinr(ne_sinr);
			// cpeDeviceNeighbor.setStatus(ne_isServiceCell);
			// cpeDeviceNeighbor.setTac(ne_tac);
			// cpeDeviceNeighbor.setCreateBy("admin");
			// cpeDeviceNeighbor.setCreateTime(new Date());
			// cpeDeviceNeighbor.setUpdateBy(ADMIN_USER);
			// cpeDeviceNeighbor.setUpdateTime(new Date());
			// cpeDeviceNeighbor.setSysOrgCode(ADMIN_USER);

			// cpeDeviceNeighborList.add(cpeDeviceNeighbor);
			// }
			// cpeDeviceNeighborService.saveBatch(cpeDeviceNeighborList);
			// }
		} else if (data.module.startsWith("MT")) {
			// 解析第4行的NR信息（0-based index）
			String nr_info = data.lte_cell.split("\\r\\n")[1].split(":")[1].trim();
			String[] items = nr_info.split(",");

			String freq = data.lte_freq.split("\\r\\n")[1].split(":")[1].trim();
			String[] freq_items = freq.split(",");

			// 修改为使用普通for循环来更新数组元素
			for (int i = 0; i < items.length; i++) {
				items[i] = items[i].replace("\"", "");
			}

			// 解析基本服务小区信息
			String isServiceCell = ""; // 服务小区标识
			if (data.lte_5g.equals("1")) {
				newCpeDevice.setDeviceStatusNo("1");
			} else if (data.lte_4g.equals("1")) {
				newCpeDevice.setDeviceStatusNo("1");
			} else
				newCpeDevice.setDeviceStatusNo("0");

			String mcc = ""; // 移动国家代码
			String mnc = ""; // 移动网络代码
			String cellid = ""; // 小区ID
			String physicalcellId = ""; // 物理小区ID
			String band = ""; // 频段

			String bandwidth = "";
			String tac = ""; // 跟踪区代码
			String narfcn = ""; // 绝对射频信道号
			String rsrp = ""; // 参考信号接收功率
			String rsrq = ""; // 参考信号接收质量
			String sinr = ""; // 信噪比
			String rxlev = ""; // 接收电平
			// 解析并转换网络参数
			String rat = items[0]; // 无线接入技术类型
			if (rat.equals("NR")) {
				mcc = items[1]; // 移动国家代码
				mnc = items[2]; // 移动网络代码
				cellid = items[3]; // 小区ID
				physicalcellId = items[6]; // 物理小区ID
				band = freq_items[2]; // 频段

				newCpeDevice.setOnlineBand(band);

				try {
					bandwidth = (String.valueOf(Integer.parseInt(freq_items[5]) / 1000) + "Mhz");
				} catch (Exception e) {
					bandwidth = "";
				}

				tac = items[7]; // 跟踪区代码
				narfcn = items[3]; // 绝对射频信道号
				rsrp = items[8]; // 参考信号接收功率
				rsrq = items[9]; // 参考信号接收质量
				sinr = items[10]; // 信噪比
				// rxlev = items[16]; // 接收电平
			} else if (rat.equals("LTE")) {
				mcc = items[1]; // 移动国家代码
				mnc = items[2]; // 移动网络代码
				cellid = items[3]; // 小区ID
				physicalcellId = items[5]; // 物理小区ID
				band = freq_items[2]; // 频段

				newCpeDevice.setOnlineBand(band);

				try {
					bandwidth = (String.valueOf(Integer.parseInt(freq_items[5]) / 1000) + "Mhz");
				} catch (Exception e) {
					bandwidth = "";
				}
				tac = items[6]; // 跟踪区代码
				narfcn = items[3]; // 绝对射频信道号
				rsrp = items[7]; // 参考信号接收功率
				rsrq = items[8]; // 参考信号接收质量
				sinr = items[9]; // 信噪比
				// rxlev = items[16]; // 接收电平
			} else if (rat.equals("WCDMA")) {
				mcc = items[1]; // 移动国家代码
				mnc = items[2]; // 移动网络代码
				cellid = items[5]; // 小区ID
				band = freq_items[2]; // 频段

				newCpeDevice.setOnlineBand(band);

				try {
					bandwidth = (String.valueOf(Integer.parseInt(freq_items[5]) / 1000) + "Mhz");
				} catch (Exception e) {
					bandwidth = "";
				}
				rsrp = items[7]; // 参考信号接收功率
			}
			// 解析运营商信息
			String cops = "";
			if (data.lte_cops != null && data.lte_cops.length() > 0) {
				try {
					// 从COPS响应中提取运营商信息
					String[] cops_items = data.lte_cops.split("\\r\\n");
					cops = cops_items[1].split(",")[2].replace("\\\"", "");

					// 根据运营商名称设置网络代码
					if (cops.indexOf("UNICOM") > 0)
						newCpeDevice.setOnlineNetNo("unicom");
					if (cops.indexOf("TELECOM") > 0)
						newCpeDevice.setOnlineNetNo("telecom");
					if (cops.indexOf("MOBILE") > 0)
						newCpeDevice.setOnlineNetNo("mobile");
					if (cops.indexOf("BROADNET") > 0)
						newCpeDevice.setOnlineNetNo("board");
				} catch (Exception ex) {
					// 运营商信息解析失败，继续处理其他数据
				}
			}

			// 更新设备基本信息
			cpeDeviceService.updateById(newCpeDevice);

			// 解析载波聚合信息
			// String caBand = "";
			// if (data.lte_cainfo != null && data.lte_cainfo.length() > 0)
			// {
			// if (data.lte_cainfo.split("\\r\\n").length > 2)
			// {
			// try {
			// // 提取主载波(PCC)信息
			// String[] cainfo_items =
			// data.lte_cainfo.split("\\r\\n")[2].split(",")[0].split(":");
			// if (cainfo_items[0].equals("PCC"))
			// {
			// caBand = cainfo_items[1];
			// }
			// }catch (Exception ex) {
			// // 载波聚合信息解析失败，继续处理其他数据
			// }
			// }
			// }

			// 处理IP地址和流量信息
			if (ipAddrParam != null && ipAddrParam.length() > 0) {
				String[] ipaddr = ipAddrParam.split(",");
				data.ipv4 = ipaddr[0]; // IPv4地址
				data.ipv6 = ipaddr[1]; // IPv6地址
				data.upBytes = Double.parseDouble(ipaddr[2]);
				data.downBytes = Double.parseDouble(ipaddr[3]);

				// 处理流量统计数据
				processTrafficData(ipaddr, data.iccid, cpeDevice, data);
			}

			// 处理LTE状态信息
			if (lteStatus != null && !lteStatus.isEmpty()) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					@SuppressWarnings("unchecked")
					Map<String, Object> lteStatusMap = objectMapper.readValue(lteStatus, Map.class);

					// 提取运行时间和DNS服务器信息
					data.uptime = lteStatusMap.containsKey("uptime") ? lteStatusMap.get("uptime").toString()
							: data.uptime;
					if (lteStatusMap.containsKey("dns-server")) {
						@SuppressWarnings("unchecked")
						ArrayList<String> dnsList = (ArrayList<String>) (lteStatusMap.get("dns-server"));

						// 设置DNS服务器地址
						if (!dnsList.isEmpty()) {
							data.dns1 = dnsList.get(0).toString();

							if (dnsList.size() > 1)
								data.dns2 = dnsList.get(1).toString();
						}
					}
				} catch (IOException e) {
					log.error("Error parsing lteStatus JSON", e);
					throw new Exception("JSON解析错误！");
				}
			}

			// 创建设备状态记录对象
			CpeDeviceStatus cpeDeviceStatus = new CpeDeviceStatus();
			cpeDeviceStatus.setIpv4(data.ipv4);
			cpeDeviceStatus.setIpv6(data.ipv6);
			cpeDeviceStatus.setUptime(data.uptime);
			cpeDeviceStatus.setUpBytes(data.upBytes);
			cpeDeviceStatus.setDownBytes(data.downBytes);
			cpeDeviceStatus.setCops(cops);
			cpeDeviceStatus.setOnlineBand(band);
			// cpeDeviceStatus.setCaBand(caBand);
			cpeDeviceStatus.setCellId(cellid);
			cpeDeviceStatus.setCpeId(cpeDevice.getId());
			cpeDeviceStatus.setDeviceSn(data.deviceSn);
			cpeDeviceStatus.setIccid(data.iccid);
			cpeDeviceStatus.setImei(data.imei);
			cpeDeviceStatus.setLinkStatus(isServiceCell);
			cpeDeviceStatus.setMcc(mcc);
			cpeDeviceStatus.setMnc(mnc);
			cpeDeviceStatus.setModemVersion(data.version);
			cpeDeviceStatus.setPcid(physicalcellId);
			cpeDeviceStatus.setRsrp(rsrp);
			cpeDeviceStatus.setRsrq(rsrq);
			cpeDeviceStatus.setSimSlot(data.sim_slot);
			cpeDeviceStatus.setSinr(sinr);
			cpeDeviceStatus.setStatus(isServiceCell);
			cpeDeviceStatus.setTac(tac);
			cpeDeviceStatus.setBandwidth(bandwidth);
			cpeDeviceStatus.setArfcn(narfcn);
			cpeDeviceStatus.setRxlev(rxlev);
			cpeDeviceStatus.setRat(rat);
			cpeDeviceStatus.setSysOrgCode(SYS_ORG_CODE);
			cpeDeviceStatus.setTs(new Date());
			cpeDeviceStatus.setCreateBy(ADMIN_USER);
			cpeDeviceStatus.setCreateTime(new Date());
			cpeDeviceStatus.setUpdateBy(ADMIN_USER);
			cpeDeviceStatus.setUpdateTime(new Date());
			cpeDeviceStatus.setDns1(data.dns1);
			cpeDeviceStatus.setDns2(data.dns2);
			cpeDeviceStatus.setOpenwrtVersion(data.openwrtVersion);
			cpeDeviceStatus.setSysUptime(data.sysUptime);
			cpeDeviceStatus.setClientCount(data.clientCount);
			cpeDeviceStatus.setCpuTemp(data.cpuTemp);

			// 保存设备状态记录
			save(cpeDeviceStatus);

			// // 处理邻区信息
			// String[] ne_info;
			// if (lte_cell.indexOf("NR neighbor cell:") > 0)
			// ne_info = lte_cell.substring(lte_cell.indexOf("NR neighbor cell:") + 2,
			// lte_cell.length()).split("\\r\\n");
			// else
			// ne_info = lte_cell.substring(lte_cell.indexOf("LTE neighbor cell:") + 2,
			// lte_cell.length()).split("\\r\\n");
			// cpeDeviceNeighborService.deleteByMainId(cpeDevice.getId());
			// if (ne_info.length > 1)
			// {
			// Collection<CpeDeviceNeighbor> cpeDeviceNeighborList = new ArrayList<>();
			// for (int i = 1; i < ne_info.length - 2; i++)
			// {
			// String[] ne_items = ne_info[i].split(",");
			// String ne_isServiceCell = ne_items[0];
			// String ne_rat = rat(ne_items[1]);
			// String ne_mcc = ne_items[2];
			// String ne_mnc = ne_items[3];
			// String ne_tac = ne_items[4];
			// String ne_cellid = ne_items[5];
			// String ne_narfcn = ne_items[6];
			// String ne_physicalcellId = ne_items[7];
			// String ne_sinr = ssSinr(ne_items[8]);
			// String ne_rxlev = ne_items[9];
			// String ne_rsrp = ssRsrp(ne_items[10]);
			// String ne_rsrq = ssRsrq(ne_items[11]);

			// CpeDeviceNeighbor cpeDeviceNeighbor = new CpeDeviceNeighbor();
			// cpeDeviceNeighbor.setCpeId(cpeDevice.getId());
			// cpeDeviceNeighbor.setArfcn(ne_narfcn);
			// cpeDeviceNeighbor.setCellid(ne_cellid);
			// cpeDeviceNeighbor.setMcc(ne_mcc);
			// cpeDeviceNeighbor.setMnc(ne_mnc);
			// cpeDeviceNeighbor.setPhysicalcellid(ne_physicalcellId);
			// cpeDeviceNeighbor.setRat(ne_rat);
			// cpeDeviceNeighbor.setRsrp(ne_rsrp);
			// cpeDeviceNeighbor.setRsrq(ne_rsrq);
			// cpeDeviceNeighbor.setRxlev(ne_rxlev);
			// if (lte_cell.indexOf("NR service cell") >0)
			// cpeDeviceNeighbor.setSinr(ne_sinr);
			// cpeDeviceNeighbor.setStatus(ne_isServiceCell);
			// cpeDeviceNeighbor.setTac(ne_tac);
			// cpeDeviceNeighbor.setCreateBy("admin");
			// cpeDeviceNeighbor.setCreateTime(new Date());
			// cpeDeviceNeighbor.setUpdateBy(ADMIN_USER);
			// cpeDeviceNeighbor.setUpdateTime(new Date());
			// cpeDeviceNeighbor.setSysOrgCode(ADMIN_USER);

			// cpeDeviceNeighborList.add(cpeDeviceNeighbor);
			// }
			// cpeDeviceNeighborService.saveBatch(cpeDeviceNeighborList);
			// }
		} else {
			// 处理IP地址和流量信息
			if (ipAddrParam != null && ipAddrParam.length() > 0) {
				String[] ipaddr = ipAddrParam.split(",");
				data.ipv4 = ipaddr[0]; // IPv4地址
				data.ipv6 = ipaddr[1]; // IPv6地址
				data.upBytes = Double.parseDouble(ipaddr[2]);
				data.downBytes = Double.parseDouble(ipaddr[3]);

				// 处理流量统计数据
				processTrafficData(ipaddr, data.iccid, cpeDevice, data);
			}

			// 处理LTE状态信息
			if (lteStatus != null && !lteStatus.isEmpty()) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					@SuppressWarnings("unchecked")
					Map<String, Object> lteStatusMap = objectMapper.readValue(lteStatus, Map.class);

					// 提取运行时间和DNS服务器信息
					data.uptime = lteStatusMap.containsKey("uptime") ? lteStatusMap.get("uptime").toString()
							: data.uptime;
					if (lteStatusMap.containsKey("dns-server")) {
						@SuppressWarnings("unchecked")
						ArrayList<String> dnsList = (ArrayList<String>) (lteStatusMap.get("dns-server"));

						// 设置DNS服务器地址
						if (!dnsList.isEmpty()) {
							data.dns1 = dnsList.get(0).toString();

							if (dnsList.size() > 1)
								data.dns2 = dnsList.get(1).toString();
						}
					}
				} catch (IOException e) {
					log.error("Error parsing lteStatus JSON", e);
					throw new Exception("JSON解析错误！");
				}
			}

			// 创建设备状态记录对象
			CpeDeviceStatus cpeDeviceStatus = new CpeDeviceStatus();
			cpeDeviceStatus.setIpv4(data.ipv4);
			cpeDeviceStatus.setIpv6(data.ipv6);
			cpeDeviceStatus.setUptime(data.uptime);
			cpeDeviceStatus.setUpBytes(data.upBytes);
			cpeDeviceStatus.setDownBytes(data.downBytes);
			cpeDeviceStatus.setCpeId(cpeDevice.getId());
			cpeDeviceStatus.setDeviceSn(data.deviceSn);
			cpeDeviceStatus.setIccid(data.iccid);
			cpeDeviceStatus.setImei(data.imei);
			cpeDeviceStatus.setModemVersion(data.version);
			cpeDeviceStatus.setSimSlot(data.sim_slot);
			cpeDeviceStatus.setSysOrgCode(SYS_ORG_CODE);
			cpeDeviceStatus.setTs(new Date());
			cpeDeviceStatus.setCreateBy(ADMIN_USER);
			cpeDeviceStatus.setCreateTime(new Date());
			cpeDeviceStatus.setUpdateBy(ADMIN_USER);
			cpeDeviceStatus.setUpdateTime(new Date());
			cpeDeviceStatus.setDns1(data.dns1);
			cpeDeviceStatus.setDns2(data.dns2);
			cpeDeviceStatus.setOpenwrtVersion(data.openwrtVersion);
			cpeDeviceStatus.setSysUptime(data.sysUptime);
			cpeDeviceStatus.setClientCount(data.clientCount);
			cpeDeviceStatus.setCpuTemp(data.cpuTemp);

			// 保存设备状态记录
			save(cpeDeviceStatus);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public List<CpeDeviceClient> parseClientsDetail(String clientsData, String cpeId) {
		List<CpeDeviceClient> clientList = new ArrayList<>();
		if (clientsData == null || clientsData.isEmpty())
			return clientList;
		String[] lines = clientsData.split("\\r?\\n");
		cpeDeviceClientMapper.deleteByMainId(cpeId);
		for (String line : lines) {
			String[] parts = line.trim().split("\\s+");
			if (parts.length < 9)
				continue; // 字段不足跳过
			try {
				CpeDeviceClient client = new CpeDeviceClient();
				client.setCpeId(cpeId);
				client.setClientIp(parts[0]);
				client.setClientMac(parts[1]);
				client.setAttachTs(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date(Long.parseLong(parts[2]) * 1000L)));
				client.setRefreshTs(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.format(new Date(Long.parseLong(parts[3]) * 1000L)));
				client.setConntedDuration(Integer.parseInt(parts[4]));
				client.setCol1(parts[5]);
				client.setUpBytes(Integer.parseInt(parts[6]));
				client.setCol2(parts[7]);
				client.setDownBytes(Integer.parseInt(parts[8]));
				cpeDeviceClientMapper.insert(client);
				clientList.add(client);
			} catch (Exception e) {
				// 日志记录异常行
			}
		}
		return clientList;
	}

	/**
	 * 处理设备推送的状态数据
	 * 更新设备状态、网络数据和信号信息
	 *
	 * @param deviceSnParam   设备序列号
	 * @param ubusOutputParam ubus输出数据
	 * @param ipAddrParam     IP地址参数
	 * @param lteStatus       LTE状态信息
	 * @param openwrtVer      OpenWrt版本
	 * @param sysUptime       系统运行时长
	 * @param clientCount     客户端连接信息
	 * @throws Exception 处理异常时抛出
	 */
	@Transactional(rollbackFor = Exception.class)
	public void push(String deviceSnParam, String ubusOutputParam, String ipAddrParam, String lteStatus,
			String openwrtVer, String sysUptime, String clients, String cpuTemp) throws Exception {
		CpeBaseData data = new CpeBaseData();
		// 标准化设备序列号格式（移除冒号并转换为大写）
		data.deviceSn = deviceSnParam.replace(":", "").toUpperCase();

		// 根据设备序列号查询设备信息
		List<CpeDevice> cpeDeviceList = cpeDeviceService.selectByDeviceSn(data.deviceSn);
		CpeDevice cpeDevice = cpeDeviceList.isEmpty() ? null : cpeDeviceList.get(0);
		if (cpeDevice == null) {
			throw new Exception("设备未找到！");
		}

		// 获取设备对应的锁
		Lock deviceLock = getDeviceLock(data.deviceSn);

		try {
			// 尝试获取设备锁，防止并发操作
			if (deviceLock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
				try {
					// 设置设备在线状态
					cpeDevice.setDeviceStatusNo("1");

					data.openwrtVersion = openwrtVer;
					if (sysUptime != null && sysUptime.length() > 0) {
						data.sysUptime = parseSysUptime(sysUptime);
					} else
						data.sysUptime = "0";
					if (clients != null && clients.length() > 0) {
						List<CpeDeviceClient> clientList = parseClientsDetail(clients, cpeDevice.getId());
						data.clientCount = clientList.size();
					} else
						data.clientCount = 0;
					if (cpuTemp != null && cpuTemp.length() > 0) {
						try {
							data.cpuTemp = Integer.parseInt(cpuTemp);
						} catch (NumberFormatException e) {
							data.cpuTemp = 0;
						}
					} else
						data.cpuTemp = 0;

					log.warn("CPU温度: " + data.cpuTemp.toString());

					if (ubusOutputParam != null) {
						String ubusOutput = ubusOutputParam;
						// 解析设备上报的JSON数据
						ObjectMapper objectMapper = new ObjectMapper();
						@SuppressWarnings("unchecked")
						Map<String, Object> ubusOutputMap = objectMapper.readValue(ubusOutput, Map.class);

						// 提取SIM卡槽位信息
						data.sim_slot = Integer.parseInt(ubusOutputMap.get("LTE_SIMSLOT").toString());
						data.lte_4g = ubusOutputMap.containsKey("LTE_4G") ? ubusOutputMap.get("LTE_4G").toString() : "";
						data.lte_5g = ubusOutputMap.containsKey("LTE_5G") ? ubusOutputMap.get("LTE_5G").toString() : "";

						// 提取设备基本信息
						data.imei = ubusOutputMap.containsKey("LTE_IMEI") ? ubusOutputMap.get("LTE_IMEI").toString()
								: "";
						data.version = ubusOutputMap.containsKey("LTE_VER") ? ubusOutputMap.get("LTE_VER").toString()
								: "";
						data.iccid = ubusOutputMap.containsKey("LTE_ICCID") ? ubusOutputMap.get("LTE_ICCID").toString()
								: "";
						// 关联SIM卡信息
						if (cardInfoService.selectByCardNo(data.iccid).size() > 0) {
							cpeDevice.setCardNo(cardInfoService.selectByCardNo(data.iccid).get(0).getId());
							cpeDevice.setOnlineCardNo(cardInfoService.selectByCardNo(data.iccid).get(0).getId());
						}

						// 提取网络相关信息
						data.module = ubusOutputMap.containsKey("LTE_MODULE")
								? ubusOutputMap.get("LTE_MODULE").toString()
								: "";
						cpeDevice.setFiveGModule(data.module);

						data.lte_cell = ubusOutputMap.containsKey("LTE_CELL") ? ubusOutputMap.get("LTE_CELL").toString()
								: "";
						data.lte_freq = ubusOutputMap.containsKey("LTE_FREQ") ? ubusOutputMap.get("LTE_FREQ").toString()
								: "";
						data.lte_cainfo = ubusOutputMap.containsKey("LTE_CAINFO")
								? ubusOutputMap.get("LTE_CAINFO").toString()
								: "";
						data.lte_cops = ubusOutputMap.containsKey("LTE_COPS") ? ubusOutputMap.get("LTE_COPS").toString()
								: "";
					}

					service_nr(cpeDevice, cpeDevice, ipAddrParam, lteStatus, data);

					// 更新设备信息
					cpeDeviceService.updateById(cpeDevice);
				} finally {
					// 释放设备锁
					deviceLock.unlock();
				}
			} else {
				// 获取锁超时
				log.warn("Failed to acquire lock for device: {}");
				throw new Exception("设备正忙，请稍后重试");
			}
		} catch (InterruptedException e) {
			// 处理线程中断异常
			Thread.currentThread().interrupt();
			log.error("Interrupted while processing device: {}", e);
			throw new Exception("处理设备数据被中断");
		}
	}

	/**
	 * 解析系统运行时长
	 *
	 * @param uptime 系统运行时长字符串
	 * @return 格式化后的运行时长
	 */
	private String parseSysUptime(String uptime) {
		if (uptime == null || uptime.isEmpty()) {
			return "0";
		}

		try {
			long totalSeconds = 0;
			String trimmedUptime = uptime.trim();

			// 检查是否包含"day"，处理"1 day, 8:41"这种格式
			if (trimmedUptime.contains("day")) {
				String[] parts = trimmedUptime.split(",");
				// 提取天数
				String dayPart = parts[0].trim();
				int days = Integer.parseInt(dayPart.split(" ")[0]);
				totalSeconds += days * 24 * 60 * 60; // 天数转换为秒

				// 处理时分部分
				if (parts.length > 1) {
					String timePart = parts[1].trim();
					if (parts.length > 2)
						timePart = parts[parts.length - 1].trim();
					String[] timeComponents = timePart.split(":");
					if (timeComponents.length >= 2) {
						int hours = Integer.parseInt(timeComponents[0]);
						int minutes = Integer.parseInt(timeComponents[1]);
						totalSeconds += hours * 60 * 60; // 小时转换为秒
						totalSeconds += minutes * 60; // 分钟转换为秒
					} else if (timePart.contains("min")) {
						String minutesPart = timePart.split("min")[0].trim();
						int minutes = Integer.parseInt(minutesPart);
						totalSeconds += minutes * 60; // 分钟转换为秒
					}
				}
			}
			// 检查是否仅包含"min"，处理"7 min"这种格式
			else if (trimmedUptime.contains("min")) {
				String minutesPart = trimmedUptime.split("min")[0].trim();
				int minutes = Integer.parseInt(minutesPart);
				totalSeconds = minutes * 60; // 分钟转换为秒
			}
			// 处理"3:03"这种格式
			else {
				String[] timeComponents = trimmedUptime.split(":");
				if (timeComponents.length >= 2) {
					int hours = Integer.parseInt(timeComponents[0]);
					int minutes = Integer.parseInt(timeComponents[1]);
					totalSeconds += hours * 60 * 60; // 小时转换为秒
					totalSeconds += minutes * 60; // 分钟转换为秒
				}
			}

			return String.valueOf(totalSeconds);
		} catch (Exception e) {
			log.error("解析系统运行时长失败: " + uptime, e);
			return "0";
		}
	}

	public void deleteByTs(Date deleteBeforTime) {
		cpeDeviceStatusMapper.deleteByTs(deleteBeforTime);
	}

	/**
	 * 通过主表id查询子表最新时间戳
	 *
	 * @param deleteBeforTime 删除此时间前的数据
	 * @return void
	 */
	public Date selectNewtestTsByMainId(String mainId) {
		return cpeDeviceStatusMapper.selectNewtestTsByMainId(mainId);
	}

	/**
	 * 处理网络流量数据
	 * 计算和更新设备的流量统计信息
	 *
	 * @param ipaddr    IP地址数组
	 * @param iccid     卡片ICCID
	 * @param cpeDevice 设备对象
	 */
	private void processTrafficData(String[] ipaddr, String iccid, CpeDevice cpeDevice, CpeBaseData data) {
		// 获取设备上报的流量数据
		double reportedUpBytes = Double.parseDouble(ipaddr[2]);
		double reportedDownBytes = Double.parseDouble(ipaddr[3]);

		// 查询对应的SIM卡信息
		List<CardInfo> cards = cardInfoService.selectByCardNo(iccid);
		if (cards.isEmpty())
			return;

		CardInfo card = cards.get(0);

		// 计算实际流量数据
		TrafficData trafficData = calculateTrafficData(
				cpeDevice.getId(),
				reportedUpBytes,
				reportedDownBytes,
				card.getUpBytes(),
				card.getDownBytes(),
				card.getBeginUpBytes(),
				card.getBeginDownBytes(),
				data);

		// 检查是否需要月度流量重置
		if (isMonthlyReset(cpeDevice.getId())) {
			// 执行月度流量重置
			trafficData = handleMonthlyReset(trafficData);
		}

		// 更新卡片流量数据
		updateCardTraffic(card, trafficData);
	}

	/**
	 * 数据传输对象：流量数据
	 * 封装流量统计信息
	 */
	@Data
	@AllArgsConstructor
	private static class TrafficData {
		private double upBytes;
		private double downBytes;
		private double beginUpBytes;
		private double beginDownBytes;
	}

	/**
	 * 从Redis获取流量数据，处理空值情况
	 *
	 * @param id   设备ID
	 * @param type 流量类型（"upBytes" 或 "downBytes"）
	 * @return double 缓存的流量数据，如果不存在则返回0
	 */
	private double getRedisTraffic(String id, String type) {
		try {
			// 获取缓存数据
			Object value = redisUtil.get(id + "_" + type);
			if (value == null) {
				return 0.0;
			}
			return Double.parseDouble(value.toString());
		} catch (NumberFormatException e) {
			// 数字格式转换异常，返回默认值
			return 0.0;
		} catch (Exception e) {
			// 其他异常，返回默认值
			return 0.0;
		}
	}

	/**
	 * 将流量数据存入Redis
	 *
	 * @param id   设备ID
	 * @param type 流量类型（"upBytes" 或 "downBytes"）
	 * @return Boolean
	 */
	private Boolean setRedisTraffic(String id, String type, Double val) {
		try {
			redisUtil.set(id + "_" + type, String.valueOf(val));
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	/**
	 * 计算实际流量数据，处理设备重启和期初值的情况
	 *
	 * @param reportedUp   设备报告的上传字节数
	 * @param reportedDown 设备报告的下载字节数
	 * @param accuUp       数据库中累计的上传字节数
	 * @param accuDown     数据库中累计的下载字节数
	 * @param beginUp      期初上传字节数
	 * @param beginDown    期初下载字节数
	 * @return TrafficData 计算后的流量数据对象
	 */
	private TrafficData calculateTrafficData(String id, double reportedUp, double reportedDown,
			double accuUp, double accuDown,
			double beginUp, double beginDown,
			CpeBaseData data) {
		// 获取Redis缓存的上次上报数据
		double redisUpBytes = getRedisTraffic(id, "upBytes");
		double redisDownBytes = getRedisTraffic(id, "downBytes");

		// 计算增量
		double upIncrement;
		double downIncrement;

		// 处理设备重启情况
		if (reportedUp < redisUpBytes) {
			// 设备重启，当前值小于上次值，说明是新的累计值
			upIncrement = reportedUp;
		} else {
			upIncrement = reportedUp - redisUpBytes;
		}

		if (reportedDown < redisDownBytes) {
			// 设备重启，当前值小于上次值，说明是新的累计值
			downIncrement = reportedDown;
		} else {
			downIncrement = reportedDown - redisDownBytes;
		}

		// 更新Redis中的最新值
		setRedisTraffic(id, "upBytes", reportedUp);
		setRedisTraffic(id, "downBytes", reportedDown);

		// 计算当月总流量（累计值 + 本次增量）
		double totalUp = accuUp + upIncrement;
		double totalDown = accuDown + downIncrement;

		// 如果有期初值，需要减去期初值得到当月实际使用量
		if (beginUp > 0 && (totalUp - beginUp) > 0) {
			totalUp = totalUp - beginUp;
			beginUp = 0.0;
		}
		if (beginDown > 0 && (totalDown - beginDown) > 0) {
			totalDown = totalDown - beginDown;
			beginDown = 0.0;
		}

		// 保存本次增量，用于更新状态表
		data.upBytes = upIncrement;
		data.downBytes = downIncrement;

		return new TrafficData(totalUp, totalDown, beginUp, beginDown);
	}

	/**
	 * 检查是否需要月度重置
	 *
	 * @param deviceId 设备ID，用于查询最新记录
	 * @return boolean 如果当前月份大于最新记录的月份，返回true
	 */
	private boolean isMonthlyReset(String deviceId) {
		Date latestRecord = selectNewtestTsByMainId(deviceId);
		if (latestRecord == null)
			return false;

		LocalDate currentDate = LocalDate.now();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(latestRecord);

		int currentYear = currentDate.getYear();
		int currentMonth = currentDate.getMonthValue();
		int recordYear = calendar.get(Calendar.YEAR);
		int recordMonth = calendar.get(Calendar.MONTH) + 1;

		return currentYear > recordYear || currentMonth > recordMonth;
	}

	/**
	 * 处理月度重置，将当前流量设为期初值，当前值清零
	 *
	 * @param data 当前的流量数据
	 * @return TrafficData 重置后的流量数据
	 */
	private TrafficData handleMonthlyReset(TrafficData data) {
		return new TrafficData(
				0.0,
				0.0,
				data.getUpBytes(),
				data.getDownBytes());
	}

	/**
	 * 更新卡片数据，包括流量统计和IP地址信息
	 *
	 * @param card        需要更新的卡片信息对象
	 * @param networkData 网络数据，包含IP地址和流量信息
	 * @param trafficData 流量数据，包含当前和期初流量
	 */
	private void updateCardTraffic(CardInfo card, TrafficData data) {
		card.setUpBytes(data.getUpBytes());
		card.setDownBytes(data.getDownBytes());
		card.setBeginUpBytes(data.getBeginUpBytes());
		card.setBeginDownBytes(data.getBeginDownBytes());
		cardInfoService.updateById(card);
	}
}
