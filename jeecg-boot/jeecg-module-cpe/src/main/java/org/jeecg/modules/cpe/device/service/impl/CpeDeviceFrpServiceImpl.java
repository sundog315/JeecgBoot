package org.jeecg.modules.cpe.device.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.cpe.device.entity.CpeDevice;
import org.jeecg.modules.cpe.device.entity.CpeDeviceFrp;
import org.jeecg.modules.cpe.device.entity.CpeOperLog;
import org.jeecg.modules.cpe.device.mapper.CpeDeviceFrpMapper;
import org.jeecg.modules.cpe.device.service.ICpeDeviceFrpService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceService;
import org.jeecg.modules.cpe.device.service.ICpeOperLogService;
import org.jeecg.modules.cpe.device.service.impl.FrpConfigParser.FrpConfig;
import org.jeecg.modules.cpe.device.service.impl.FrpConfigParser.ServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * FRP设备服务实现类
 * 处理设备的FRP配置、状态更新和日志记录
 *
 * @author jeecg-boot
 * @version V1.0
 * @date 2024-01-04
 */
@Slf4j
@Service
public class CpeDeviceFrpServiceImpl extends ServiceImpl<CpeDeviceFrpMapper, CpeDeviceFrp> implements ICpeDeviceFrpService {
    
    // 常量定义
    private static final String ADMIN_USER = "admin";
    private static final String SYS_ORG_CODE = "A01";
    private static final int ERROR_CODE = 255;
    private static final String SSH_SERVICE_TYPE = "ssh";
    private static final String HTTP_SERVICE_TYPE = "http";
	private static final int LOCK_TIMEOUT_SECONDS = 5;
    
    // 依赖注入
    @Autowired
    private CpeDeviceFrpMapper cpeDeviceFrpMapper;
    
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
     * 根据主ID查询FRP配置列表
     *
     * @param mainId 主设备ID
     * @return FRP配置列表
     */
    @Override
    public List<CpeDeviceFrp> selectByMainId(String mainId) {
        return cpeDeviceFrpMapper.selectByMainId(mainId);
    }

    /**
     * 处理设备FRP配置报告
     *
     * @param deviceSnParam 设备序列号
     * @param frp FRP配置内容
     * @return 处理结果状态码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int report(String deviceSnParam, String frp) {
        // 标准化设备序列号
        String standardDeviceSn = standardizeDeviceSn(deviceSnParam);
        // 获取设备对应的锁
        Lock deviceLock = getDeviceLock(standardDeviceSn);

		try {
			// 尝试获取设备锁，防止并发操作
			if (deviceLock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
				try {
					// 参数校验
					if (StringUtils.isBlank(deviceSnParam) || StringUtils.isBlank(frp)) {
						log.warn("无效的参数：deviceSn={}, frp={}", deviceSnParam, frp);
						return ERROR_CODE;
					}

					// 解析FRP配置
					FrpConfig frpConfig = parseFrpConfig(frp);
					if (frpConfig == null) {
						return ERROR_CODE;
					}

					// 获取设备信息
					CpeDevice device = getDevice(deviceSnParam);
					if (device == null) {
						return ERROR_CODE;
					}

					// 处理FRP配置
					processFrpConfig(device, frpConfig);

					return 0;
				} catch (Exception e) {
					log.error("处理FRP配置失败: {}", e.getMessage(), e);
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
     * 解析FRP配置
     */
    private FrpConfig parseFrpConfig(String frp) {
        try {
            FrpConfig frpConfig = FrpConfigParser.parse(frp);
            if (frpConfig == null) {
                log.warn("FRP配置解析为空");
                return null;
            }
            return frpConfig;
        } catch (Exception e) {
            log.error("FRP配置解析失败: {}", e.getMessage());
            return null;
        }
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
     * 标准化设备序列号
     */
    private String standardizeDeviceSn(String deviceSn) {
        return deviceSn.replace(":", "").toUpperCase();
    }

    /**
     * 处理FRP配置
     */
    private void processFrpConfig(CpeDevice device, FrpConfig frpConfig) throws Exception {
        // 克隆配置用于比较
        FrpConfig newFrpConfig = (FrpConfig) frpConfig.clone();

        // 获取端口信息
        PortInfo portInfo = extractPortInfo(frpConfig.getServices());

        // 获取或创建FRP记录
        CpeDeviceFrp frpRecord = getOrCreateFrpRecord(device, frpConfig, portInfo);

		if (!newFrpConfig.getServerAddr().equals(frpRecord.getServerAddr()))
			newFrpConfig.setServerAddr(frpRecord.getServerAddr());

		if (!newFrpConfig.getServerPort().equals(frpRecord.getServerPort()))
			newFrpConfig.setServerPort(frpRecord.getServerPort());
		
		if (!newFrpConfig.getToken().equals(frpRecord.getToken()))
			newFrpConfig.setToken(frpRecord.getToken());

		if (frpRecord.getProxyHttpRemotePort() != portInfo.getHttpPort())
			for (ServiceConfig sc : newFrpConfig.getServices()) {
				if (sc.getName().contains(HTTP_SERVICE_TYPE)) {
					sc.setRemotePort(frpRecord.getProxyHttpRemotePort());
				}
			}

		if (frpRecord.getProxySshRemotePort() != portInfo.getSshPort())
		for (ServiceConfig sc : newFrpConfig.getServices()) {
			if (sc.getName().contains(SSH_SERVICE_TYPE)) {
				sc.setRemotePort(frpRecord.getProxySshRemotePort());
			}
		}

		PortInfo pInfo = extractPortInfo(newFrpConfig.getServices());

        // 检查并更新配置
        if (isConfigChanged(frpConfig, newFrpConfig)) {
            //updateFrpRecord(frpRecord);
            createOperationLog(device.getId(), newFrpConfig, pInfo);
        }
    }

    /**
     * 提取端口信息
     */
    @Data
    private static class PortInfo {
        private int sshPort;
        private int httpPort;
    }

    private PortInfo extractPortInfo(List<ServiceConfig> services) {
        PortInfo portInfo = new PortInfo();
        for (ServiceConfig sc : services) {
            if (sc.getName().contains(SSH_SERVICE_TYPE)) {
                portInfo.setSshPort(sc.getRemotePort());
            } else if (sc.getName().contains(HTTP_SERVICE_TYPE)) {
                portInfo.setHttpPort(sc.getRemotePort());
            }
        }
        return portInfo;
    }

    /**
     * 获取或创建FRP记录
     */
    private CpeDeviceFrp getOrCreateFrpRecord(CpeDevice device, FrpConfig frpConfig, PortInfo portInfo) {
        List<CpeDeviceFrp> frpRecordsList = cpeDeviceFrpMapper.selectByMainId(device.getId());
        CpeDeviceFrp frpRecord = frpRecordsList.size() > 0 ? frpRecordsList.get(0) : null;

        if (frpRecord == null) {
            frpRecord = createNewFrpRecord(device, frpConfig, portInfo);
        }
        return frpRecord;
    }

    /**
     * 创建新的FRP记录
     */
    private CpeDeviceFrp createNewFrpRecord(CpeDevice device, FrpConfig frpConfig, PortInfo portInfo) {
        CpeDeviceFrp frpRecord = new CpeDeviceFrp();
        frpRecord.setCpeId(device.getId());
        frpRecord.setCreateBy(ADMIN_USER);
        frpRecord.setCreateTime(new Date());
        frpRecord.setServerAddr(frpConfig.getServerAddr());
        frpRecord.setServerPort(frpConfig.getServerPort());
        frpRecord.setSysOrgCode(SYS_ORG_CODE);
        frpRecord.setToken(frpConfig.getToken());
        frpRecord.setUpdateBy(ADMIN_USER);
        frpRecord.setUpdateTime(new Date());
        frpRecord.setProxyHttpRemotePort(portInfo.getHttpPort());
        frpRecord.setProxySshRemotePort(portInfo.getSshPort());
        
        save(frpRecord);
        return frpRecord;
    }

    /**
     * 检查配置是否发生变化
     */
    private boolean isConfigChanged(FrpConfig newConfig, FrpConfig oldConfig) {
        return !newConfig.equals(oldConfig);
    }

    /**
     * 创建操作日志
     */
    private void createOperationLog(String deviceId, FrpConfig frpConfig, PortInfo portInfo) {
        CpeOperLog operLog = new CpeOperLog();
        operLog.setCpeId(deviceId);
        operLog.setCreateBy(ADMIN_USER);
        operLog.setCreateTime(new Date());
        operLog.setSysOrgCode(SYS_ORG_CODE);
        operLog.setUpdateBy(ADMIN_USER);
        operLog.setUpdateTime(new Date());
        operLog.setCreateTs(new Date());
        operLog.setOperType("frp");
        
        // 构建操作参数
        String operParam = frpConfig.getServerAddr() + "," +
                          frpConfig.getServerPort() + "," +
                          frpConfig.getToken() + "," +
                          portInfo.getSshPort() + "," +
                          portInfo.getHttpPort();
        
        operLog.setOperParam(operParam);
        
        cpeOperLogService.save(operLog);
    }
}