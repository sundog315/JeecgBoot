package org.jeecg.modules.cpe.device.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.cpe.device.entity.CpeDevice;
import org.jeecg.modules.cpe.device.entity.CpeDeviceAutoreboot;
import org.jeecg.modules.cpe.device.entity.CpeOperLog;
import org.jeecg.modules.cpe.device.mapper.CpeDeviceAutorebootMapper;
import org.jeecg.modules.cpe.device.service.ICpeDeviceAutorebootService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceService;
import org.jeecg.modules.cpe.device.service.ICpeOperLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 设备自动重启
 * @Author: jeecg-boot
 * @Date:   2025-01-05
 * @Version: V1.0
 */
@Slf4j
@Service
public class CpeDeviceAutorebootServiceImpl extends ServiceImpl<CpeDeviceAutorebootMapper, CpeDeviceAutoreboot> implements ICpeDeviceAutorebootService {
	
	@Autowired
	private CpeDeviceAutorebootMapper cpeDeviceAutorebootMapper;

	@Autowired
	private ICpeDeviceService cpeDeviceService;

	@Autowired
	private ICpeOperLogService cpeOperLogService;

    // 常量定义
    private static final String ADMIN_USER = "admin";
    private static final String SYS_ORG_CODE = "A01";
    private static final int ERROR_CODE = 255;
	private static final int LOCK_TIMEOUT_SECONDS = 5;

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
	
	@Override
	public List<CpeDeviceAutoreboot> selectByMainId(String mainId) {
		return cpeDeviceAutorebootMapper.selectByMainId(mainId);
	}

	/**
     * 处理设备自动重启配置报告
     *
     * @param deviceSnParam 设备序列号
     * @param autoReboot 自动重启配置内容
     * @return 处理结果状态码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int report(String deviceSnParam, String autoReboot) {
        // 标准化设备序列号
        String standardDeviceSn = standardizeDeviceSn(deviceSnParam);
        // 获取设备对应的锁
        Lock deviceLock = getDeviceLock(standardDeviceSn);

		try {
			// 尝试获取设备锁，防止并发操作
			if (deviceLock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
				try {
					// 参数校验
					if (StringUtils.isBlank(deviceSnParam) || StringUtils.isBlank(autoReboot)) {
						log.warn("无效的参数：deviceSn={}, autoReboot={}", deviceSnParam, autoReboot);
						return ERROR_CODE;
					}

					String[] params = autoReboot.split(" ");
					String schedule = "";
					String cmd = "";

					for (int i = 0; i < params.length; i++)
					{
						if (i<5)
							schedule += params[i] + " ";
						else
							cmd += params[i] + " ";
					}
					schedule = schedule.trim();
					cmd = cmd.trim();

					if ((schedule == null || cmd == null) || (schedule.isEmpty() || cmd.isEmpty())) {
						log.warn("无效的参数：autoReboot={}", autoReboot);
						return ERROR_CODE;
					}

					// 获取设备信息
					CpeDevice device = getDevice(deviceSnParam);
					if (device == null) {
						return ERROR_CODE;
					}

					// 处理FRP配置
					processAutoRebootConfig(device, schedule, cmd);

					return 0;
				} catch (Exception e) {
					log.error("处理定时重启配置失败: {}", e.getMessage(), e);
					return ERROR_CODE;
				} finally {
					// 释放设备锁
					deviceLock.unlock();
				}
			} else {
				// 获取锁超时
				log.warn("Failed to acquire lock for device: {}");
				return ERROR_CODE;
			}
		} catch (InterruptedException e) {
			// 处理线程中断异常
			Thread.currentThread().interrupt();
			log.error("Interrupted while processing device: {}", e);
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
     * 获取或创建定时重启记录
     */
    private CpeDeviceAutoreboot getOrCreateAutoRebootRecord(CpeDevice device, String schedule, String cmd) {
        List<CpeDeviceAutoreboot> autoRebootRecordsList = cpeDeviceAutorebootMapper.selectByMainId(device.getId());
        CpeDeviceAutoreboot autoRebootRecord = autoRebootRecordsList.size() > 0 ? autoRebootRecordsList.get(0) : null;

        if (autoRebootRecord == null) {
            autoRebootRecord = createNewAutoRebootRecord(device, schedule, cmd);
        }
        return autoRebootRecord;
    }

    /**
     * 处理定时重启配置
     */
    private void processAutoRebootConfig(CpeDevice device, String schedule, String cmd) throws Exception {
        // 获取或创建定时重启记录
        CpeDeviceAutoreboot autoRebootRecord = getOrCreateAutoRebootRecord(device, schedule, cmd);

		String newSchedule = autoRebootRecord.getSchedule();
		String newCmd = autoRebootRecord.getCmd();

		if (!newSchedule.equals(schedule))
			newSchedule = schedule;

		if (!newCmd.equals(cmd))
			newCmd = cmd;

        // 检查并更新配置
        if (!newSchedule.equals(schedule) || !newCmd.equals(cmd)) {
            createOperationLog(device.getId(), newSchedule, newCmd);
        }
    }

	/**
     * 创建新的定时重启记录
     */
    private CpeDeviceAutoreboot createNewAutoRebootRecord(CpeDevice device, String schedule, String cmd) {
        CpeDeviceAutoreboot autoRebootRecord = new CpeDeviceAutoreboot();
        autoRebootRecord.setCpeId(device.getId());
        autoRebootRecord.setCreateBy(ADMIN_USER);
        autoRebootRecord.setCreateTime(new Date());
        autoRebootRecord.setSysOrgCode(SYS_ORG_CODE);
        autoRebootRecord.setUpdateBy(ADMIN_USER);
        autoRebootRecord.setUpdateTime(new Date());
		autoRebootRecord.setSchedule(schedule);
		autoRebootRecord.setCmd(cmd);
        
        save(autoRebootRecord);
        return autoRebootRecord;
    }

    /**
     * 创建操作日志
     */
    private void createOperationLog(String deviceId, String schedule, String cmd) {
        CpeOperLog operLog = new CpeOperLog();
        operLog.setCpeId(deviceId);
        operLog.setCreateBy(ADMIN_USER);
        operLog.setCreateTime(new Date());
        operLog.setSysOrgCode(SYS_ORG_CODE);
        operLog.setUpdateBy(ADMIN_USER);
        operLog.setUpdateTime(new Date());
        operLog.setCreateTs(new Date());
        operLog.setOperType("autoreboot");
		operLog.setOperParam(schedule + " " + cmd);
        
        cpeOperLogService.save(operLog);
    }
}
