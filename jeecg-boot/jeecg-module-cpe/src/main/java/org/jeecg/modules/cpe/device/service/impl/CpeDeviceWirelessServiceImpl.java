package org.jeecg.modules.cpe.device.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.cpe.device.entity.CpeDevice;
import org.jeecg.modules.cpe.device.entity.CpeDeviceWireless;
import org.jeecg.modules.cpe.device.entity.CpeOperLog;
import org.jeecg.modules.cpe.device.mapper.CpeDeviceWirelessMapper;
import org.jeecg.modules.cpe.device.service.ICpeDeviceService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceWirelessService;
import org.jeecg.modules.cpe.device.service.ICpeOperLogService;
import org.jeecg.modules.cpe.device.service.impl.WirelessConfigParser.WirelessConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.text.SimpleDateFormat;

/**
 * @Description: 设备无线网络配置
 * @Author: jeecg-boot
 * @Date:   2025-01-20
 * @Version: V1.0
 */
@Slf4j
@Service
public class CpeDeviceWirelessServiceImpl extends ServiceImpl<CpeDeviceWirelessMapper, CpeDeviceWireless> implements ICpeDeviceWirelessService {

	// 常量定义
	private static final String ADMIN_USER = "admin";
	private static final String SYS_ORG_CODE = "A01";
	private static final int ERROR_CODE = 255;
	private static final int LOCK_TIMEOUT_SECONDS = 5;

	// 依赖注入
	@Autowired
	private CpeDeviceWirelessMapper cpeDeviceWirelessMapper;

	@Autowired
	private ICpeDeviceService cpeDeviceService;

	@Autowired
	private ICpeOperLogService cpeOperLogService;

	/** 并发控制锁 */
	private final ConcurrentHashMap<String, Lock> deviceLocks = new ConcurrentHashMap<>();

	/**
	 * 获取设备锁
	 * @param deviceSn 设备序列号
	 * @return 设备对应的锁对象
	 */
	private Lock getDeviceLock(String deviceSn) {
		return deviceLocks.computeIfAbsent(deviceSn, k -> new ReentrantLock());
	}

	/**
	 * 通过主设备ID查询无线配置记录
	 *
	 * @param mainId 主设备ID
	 * @return 无线配置记录列表
	 */
	@Override
	public List<CpeDeviceWireless> selectByMainId(String mainId) {
		return cpeDeviceWirelessMapper.selectByMainId(mainId);
	}

	/**
	 * 处理设备无线配置报告
	 *
	 * @param deviceSnParam 设备序列号
	 * @param wireless 无线配置内容
	 * @return 处理结果状态码
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int report(String deviceSnParam, String wireless) {
		// 标准化设备序列号
		String standardDeviceSn = standardizeDeviceSn(deviceSnParam);
		// 获取设备对应的锁
		Lock deviceLock = getDeviceLock(standardDeviceSn);

		try {
			// 尝试获取设备锁，防止并发操作
			if (deviceLock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
				try {
					// 参数校验
					if (StringUtils.isBlank(deviceSnParam) || StringUtils.isBlank(wireless)) {
						log.warn("无效的参数：deviceSn={}, wireless={}", deviceSnParam, wireless);
						return ERROR_CODE;
					}

					// 解析无线配置
					WirelessConfig wirelessConfig = WirelessConfigParser.parse(wireless);
					if (wirelessConfig == null) {
						return ERROR_CODE;
					}

					// 获取设备信息
					CpeDevice device = getDevice(deviceSnParam);
					if (device == null) {
						return ERROR_CODE;
					}

					// 处理无线配置
					processWirelessConfig(device, wirelessConfig);

					return 0;
				} catch (Exception e) {
					log.error("处理无线配置失败: {}", e.getMessage(), e);
					return ERROR_CODE;
				} finally {
					// 释放设备锁
					deviceLock.unlock();
				}
			} else {
				// 获取锁超时
				log.warn("Failed to acquire lock for device: {}", standardDeviceSn);
				return ERROR_CODE;
			}
		} catch (InterruptedException e) {
			// 处理线程中断异常
			Thread.currentThread().interrupt();
			log.error("Interrupted while processing device: {}", e.getMessage(), e);
			return ERROR_CODE;
		}
	}

	/**
	 * 标准化设备序列号
	 */
	private String standardizeDeviceSn(String deviceSn) {
		return deviceSn.replace(":", "").toUpperCase();
	}

	/**
	 * 获取设备信息
	 */
	private CpeDevice getDevice(String deviceSnParam) {
		// 标准化设备序列号
		String deviceSn = standardizeDeviceSn(deviceSnParam);

		// 查询设备
		List<CpeDevice> deviceList = cpeDeviceService.selectByDeviceSn(deviceSn);
		if (deviceList.isEmpty()) {
			log.warn("找不到设备，设备ID为：{}", deviceSn);
			return null;
		}

		return deviceList.get(0);
	}

	/**
	 * 处理无线配置
	 */
	private void processWirelessConfig(CpeDevice device, WirelessConfig wirelessConfig) {
		// 获取或创建无线配置记录
		List<CpeDeviceWireless> wirelessRecordsList = cpeDeviceWirelessMapper.selectByMainId(device.getId());
		CpeDeviceWireless wirelessRecord = wirelessRecordsList.isEmpty() ? null : wirelessRecordsList.get(0);

		if (wirelessRecord == null) {
			wirelessRecord = createNewWirelessRecord(device, wirelessConfig);
		}

		Date recorUpdateDate = wirelessRecord.getUpdateTime();

		// 检查并更新配置
		boolean isConfigChanged = checkWirelessRecord(wirelessRecord, wirelessConfig);
		if (isConfigChanged) {
			// 将时间戳转换为格式化日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date fileDate = sdf.parse(wirelessConfig.getFileDate().getModify_date());
				if (fileDate.getTime() > recorUpdateDate.getTime() + 240000) {
					updateById(wirelessRecord);
				} else {
					createOperationLog(device.getId());
				}
			} catch (Exception ex) {
				log.info("无线配置文件修改日期转换失败，可能因版本不同导致，可以忽略");
			}
		}
	}

	/**
	 * 创建新的无线配置记录
	 */
	private CpeDeviceWireless createNewWirelessRecord(CpeDevice device, WirelessConfig wirelessConfig) {
		CpeDeviceWireless wirelessRecord = new CpeDeviceWireless();
		wirelessRecord.setCpeId(device.getId());
		wirelessRecord.setCreateBy(ADMIN_USER);
		wirelessRecord.setCreateTime(new Date());
		wirelessRecord.setSysOrgCode(SYS_ORG_CODE);
		wirelessRecord.setUpdateBy(ADMIN_USER);
		wirelessRecord.setUpdateTime(new Date());

		// 设置2.4G配置
		wirelessRecord.setRadio24Channel(wirelessConfig.getRadio0().getChannel());
		wirelessRecord.setRadio24Power(wirelessConfig.getRadio0().getPower());
		wirelessRecord.setRadio24Disabled(wirelessConfig.getWlan0().getDisabled());
		wirelessRecord.setRadio24Ssid(wirelessConfig.getWlan0().getSsid());
		wirelessRecord.setRadio24Encryption(wirelessConfig.getWlan0().getEncryption());
		wirelessRecord.setRadio24Key(wirelessConfig.getWlan0().getKey());
		wirelessRecord.setRadio24MaxSta(wirelessConfig.getWlan0().getMaxsta());
		wirelessRecord.setRadio24Macfilter(wirelessConfig.getWlan0().getMacfilter());
		wirelessRecord.setRadio24Hidden(wirelessConfig.getWlan0().getHidden());

		// 设置5G配置
		wirelessRecord.setRadio5Channel(wirelessConfig.getRadio1().getChannel());
		wirelessRecord.setRadio5Power(wirelessConfig.getRadio1().getPower());
		wirelessRecord.setRadio5Disabled(wirelessConfig.getWlan1().getDisabled());
		wirelessRecord.setRadio5Ssid(wirelessConfig.getWlan1().getSsid());
		wirelessRecord.setRadio5Encryption(wirelessConfig.getWlan1().getEncryption());
		wirelessRecord.setRadio5Key(wirelessConfig.getWlan1().getKey());
		wirelessRecord.setRadio5MaxSta(wirelessConfig.getWlan1().getMaxsta());
		wirelessRecord.setRadio5Macfilter(wirelessConfig.getWlan1().getMacfilter());
		wirelessRecord.setRadio5Hidden(wirelessConfig.getWlan1().getHidden());

		save(wirelessRecord);
		return wirelessRecord;
	}

	/**
	 * 检查无线配置记录
	 * @return 配置是否发生变化
	 */
	private boolean checkWirelessRecord(CpeDeviceWireless record, WirelessConfig config) {
		boolean changed = false;
		boolean newfile = false;

		// 将时间戳转换为格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date fileDate = sdf.parse(config.getFileDate().getModify_date());
			if (fileDate.getTime() > record.getUpdateTime().getTime() + 240000) {
				newfile = true;
			}
		} catch (Exception ex) {
			log.info("无线配置文件修改日期转换失败，可能因版本不同导致，可以忽略");
		}

		// 检查并更新2.4G配置
		if (!StringUtils.equals(record.getRadio24Channel(), config.getRadio0().getChannel())) {
			if (newfile) record.setRadio24Channel(config.getRadio0().getChannel());
			changed = true;
		}
		if (!Objects.equals(record.getRadio24Power(), config.getRadio0().getPower())) {
			if (newfile) record.setRadio24Power(config.getRadio0().getPower());
			changed = true;
		}
		if (!Objects.equals(record.getRadio24Disabled(), config.getWlan0().getDisabled())) {
			if (newfile) record.setRadio24Disabled(config.getWlan0().getDisabled());
			changed = true;
		}
		if (!StringUtils.equals(record.getRadio24Ssid(), config.getWlan0().getSsid())) {
			if (newfile) record.setRadio24Ssid(config.getWlan0().getSsid());
			changed = true;
		}
		if (!StringUtils.equals(record.getRadio24Encryption(), config.getWlan0().getEncryption())) {
			if (newfile) record.setRadio24Encryption(config.getWlan0().getEncryption());
			changed = true;
		}
		if (!StringUtils.equals(record.getRadio24Key(), config.getWlan0().getKey())) {
			if (newfile) record.setRadio24Key(config.getWlan0().getKey());
			changed = true;
		}
		if (!Objects.equals(record.getRadio24MaxSta(), config.getWlan0().getMaxsta())) {
			if (newfile) record.setRadio24MaxSta(config.getWlan0().getMaxsta());
			changed = true;
		}
		if (!Objects.equals(record.getRadio24Macfilter(), config.getWlan0().getMacfilter())) {
			if (newfile) record.setRadio24Macfilter(config.getWlan0().getMacfilter());
			changed = true;
		}
		if (!Objects.equals(record.getRadio24Hidden(), config.getWlan0().getHidden())) {
			if (newfile) record.setRadio24Hidden(config.getWlan0().getHidden());
			changed = true;
		}

		// 检查并更新5G配置
		if (!StringUtils.equals(record.getRadio5Channel(), config.getRadio1().getChannel())) {
			if (newfile) record.setRadio5Channel(config.getRadio1().getChannel());
			changed = true;
		}
		if (!Objects.equals(record.getRadio5Power(), config.getRadio1().getPower())) {
			if (newfile) record.setRadio5Power(config.getRadio1().getPower());
			changed = true;
		}
		if (!Objects.equals(record.getRadio5Disabled(), config.getWlan1().getDisabled())) {
			if (newfile) record.setRadio5Disabled(config.getWlan1().getDisabled());
			changed = true;
		}
		if (!StringUtils.equals(record.getRadio5Ssid(), config.getWlan1().getSsid())) {
			if (newfile) record.setRadio5Ssid(config.getWlan1().getSsid());
			changed = true;
		}
		if (!StringUtils.equals(record.getRadio5Encryption(), config.getWlan1().getEncryption())) {
			if (newfile) record.setRadio5Encryption(config.getWlan1().getEncryption());
			changed = true;
		}
		if (!StringUtils.equals(record.getRadio5Key(), config.getWlan1().getKey())) {
			if (newfile) record.setRadio5Key(config.getWlan1().getKey());
			changed = true;
		}
		if (!Objects.equals(record.getRadio5MaxSta(), config.getWlan1().getMaxsta())) {
			if (newfile) record.setRadio5MaxSta(config.getWlan1().getMaxsta());
			changed = true;
		}
		if (!Objects.equals(record.getRadio5Macfilter(), config.getWlan1().getMacfilter())) {
			if (newfile) record.setRadio5Macfilter(config.getWlan1().getMacfilter());
			changed = true;
		}
		if (!Objects.equals(record.getRadio5Hidden(), config.getWlan1().getHidden())) {
			if (newfile) record.setRadio5Hidden(config.getWlan1().getHidden());
			changed = true;
		}

		if (changed && newfile) {
			record.setUpdateBy(ADMIN_USER);
			record.setUpdateTime(new Date());
		}

		return changed;
	}

	/**
	 * 创建操作日志
	 */
	private void createOperationLog(String deviceId) {
		CpeOperLog operLog = new CpeOperLog();
		operLog.setCpeId(deviceId);
		operLog.setCreateBy(ADMIN_USER);
		operLog.setCreateTime(new Date());
		operLog.setSysOrgCode(SYS_ORG_CODE);
		operLog.setUpdateBy(ADMIN_USER);
		operLog.setUpdateTime(new Date());
		operLog.setCreateTs(new Date());
		operLog.setOperType("wireless");

		// 从数据库获取最新的无线配置记录
		List<CpeDeviceWireless> wirelessRecords = cpeDeviceWirelessMapper.selectByMainId(deviceId);
		if (wirelessRecords.isEmpty()) {
			log.error("无法找到设备{}的无线配置记录", deviceId);
			return;
		}
		CpeDeviceWireless record = wirelessRecords.get(0);

		// 使用数据库记录构建操作参数
		String operParam = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
			// wirelessConfig.getWlan0().getDisabled(),
			// wirelessConfig.getRadio0().getChannel(),
			// wirelessConfig.getWlan0().getSsid(),
			// wirelessConfig.getWlan0().getEncryption(),
			// wirelessConfig.getWlan0().getKey(),
			// wirelessConfig.getWlan0().getMaxsta(),
			// wirelessConfig.getRadio0().getPower(),
			// wirelessConfig.getWlan0().getMacfilter(),
			// wirelessConfig.getWlan0().getHidden(),
			// wirelessConfig.getWlan1().getDisabled(),
			// wirelessConfig.getRadio1().getChannel(),
			// wirelessConfig.getWlan1().getSsid(),
			// wirelessConfig.getWlan1().getEncryption(),
			// wirelessConfig.getWlan1().getKey(),
			// wirelessConfig.getWlan1().getMaxsta(),
			// wirelessConfig.getRadio0().getPower(),
			// wirelessConfig.getWlan1().getMacfilter(),
			// wirelessConfig.getWlan1().getHidden()
			record.getRadio24Disabled(),
			record.getRadio24Channel(),
			record.getRadio24Ssid(),
			record.getRadio24Encryption(),
			record.getRadio24Key(),
			record.getRadio24MaxSta(),
			record.getRadio24Power(),
			record.getRadio24Macfilter(),
			record.getRadio24Hidden(),
			record.getRadio5Disabled(),
			record.getRadio5Channel(),
			record.getRadio5Ssid(),
			record.getRadio5Encryption(),
			record.getRadio5Key(),
			record.getRadio5MaxSta(),
			record.getRadio5Power(),
			record.getRadio5Macfilter(),
			record.getRadio5Hidden()
		);

		operLog.setOperParam(operParam);
		cpeOperLogService.save(operLog);
	}
}