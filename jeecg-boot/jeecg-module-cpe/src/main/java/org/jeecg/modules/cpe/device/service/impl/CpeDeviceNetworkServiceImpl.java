package org.jeecg.modules.cpe.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.cpe.device.entity.CpeDevice;
import org.jeecg.modules.cpe.device.entity.CpeDeviceNetwork;
import org.jeecg.modules.cpe.device.entity.CpeOperLog;
import org.jeecg.modules.cpe.device.mapper.CpeDeviceNetworkMapper;
import org.jeecg.modules.cpe.device.service.ICpeDeviceNetworkService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceService;
import org.jeecg.modules.cpe.device.service.ICpeOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description: 设备内网配置
 * @Author: jeecg-boot
 * @Date:   2025-01-12
 * @Version: V1.0
 */
@Slf4j
@Service
public class CpeDeviceNetworkServiceImpl extends ServiceImpl<CpeDeviceNetworkMapper, CpeDeviceNetwork> implements ICpeDeviceNetworkService {
	@Autowired
	private ICpeDeviceService cpeDeviceService;

	@Autowired
	private ICpeOperLogService cpeOperLogService;

	private static final String ADMIN_USER = "admin";
    private static final String SYS_ORG_CODE = "A01";
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
	@Transactional(rollbackFor = Exception.class)
	public void report(String deviceSn, String networkConfig) throws Exception {
		// 标准化设备序列号格式（移除冒号并转换为大写）
		deviceSn = deviceSn.replace(":", "").toUpperCase();

		// 获取设备对应的锁
		Lock deviceLock = getDeviceLock(deviceSn);

		// 尝试获取锁
		if (!deviceLock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
			throw new Exception("获取设备锁超时！");
		}

		try {
			// 根据设备序列号查询设备信息
			List<CpeDevice> cpeDeviceList = cpeDeviceService.selectByDeviceSn(deviceSn);
			CpeDevice cpeDevice = cpeDeviceList.isEmpty() ? null : cpeDeviceList.get(0);
			if (cpeDevice == null) {
				throw new Exception("设备未找到！");
			}

			// 解析设备上报的JSON数据
			ObjectMapper objectMapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> networkConfigMap = objectMapper.readValue(networkConfig, Map.class);

			// 查询设备现有的网络配置
			QueryWrapper<CpeDeviceNetwork> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("cpe_id", cpeDevice.getId());
			CpeDeviceNetwork deviceNetwork = this.getOne(queryWrapper);

			// 如果存在现有配置，检查是否有变化
			if (deviceNetwork != null) {
				String oldConfig = String.format("%s,%s,%s,%s,%s",
					deviceNetwork.getIpaddr(),
					deviceNetwork.getNetmask(),
					deviceNetwork.getDhcpStart(),
					deviceNetwork.getDhcpEnd(),
					deviceNetwork.getDhcpLeasetime()
				);

				String newIpaddr = (String) networkConfigMap.get("lan_ip");
				String newNetmask = (String) networkConfigMap.get("lan_netmask");
				String newDhcpStart = (String) networkConfigMap.get("dhcp_start");
				String newDhcpEnd = (String) networkConfigMap.get("dhcp_end");
				String newDhcpLease = (String) networkConfigMap.get("dhcp_lease");
				String lastModified = (String) networkConfigMap.get("last_modified");

				// 检查配置是否有变化
				if (!deviceNetwork.getIpaddr().equals(newIpaddr) ||
					!deviceNetwork.getNetmask().equals(newNetmask) ||
					!deviceNetwork.getDhcpStart().equals(newDhcpStart) ||
					!deviceNetwork.getDhcpEnd().equals(newDhcpEnd) ||
					!deviceNetwork.getDhcpLeasetime().equals(newDhcpLease)) {
					log.info("设备网络配置发生变化，旧配置：{}，新配置：{}", oldConfig, newIpaddr+","+newNetmask+","+newDhcpStart+","+newDhcpEnd+","+newDhcpLease);

					try {
						// 将时间戳转换为格式化日期
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date fileDate = sdf.parse(lastModified);
						if (fileDate.getTime() > deviceNetwork.getUpdateTime().getTime() + 240000) {
							// 更新网络配置信息
							deviceNetwork.setIpaddr((String) networkConfigMap.get("lan_ip"));
							deviceNetwork.setNetmask((String) networkConfigMap.get("lan_netmask"));
							deviceNetwork.setDhcpStart((String) networkConfigMap.get("dhcp_start"));
							deviceNetwork.setDhcpEnd((String) networkConfigMap.get("dhcp_limit")); // 从dhcp_range中提取结束地址
							deviceNetwork.setDhcpLeasetime((String) networkConfigMap.get("dhcp_lease"));
							deviceNetwork.setUpdateTime(new Date());
							deviceNetwork.setUpdateBy(ADMIN_USER);
							// 保存或更新配置
							this.saveOrUpdate(deviceNetwork);
						}else{
							// 创建操作日志
							CpeOperLog operLog = new CpeOperLog();
							operLog.setCpeId(cpeDevice.getId());
							operLog.setOperType("network");
							operLog.setOperParam(oldConfig);
							operLog.setCreateBy(ADMIN_USER);
							operLog.setCreateTime(new Date());
							operLog.setCreateTs(new Date());
							operLog.setSysOrgCode(SYS_ORG_CODE);

							// 保存操作日志
							cpeOperLogService.save(operLog);
						}
					} catch (Exception ex) {
						log.info("网络配置文件修改日期转换失败，可能因版本不同导致，可以忽略");
					}
				}
			} else {
				deviceNetwork = new CpeDeviceNetwork();
				deviceNetwork.setCpeId(cpeDevice.getId());
				deviceNetwork.setCreateTime(new Date());
				deviceNetwork.setCreateBy(ADMIN_USER);
				deviceNetwork.setSysOrgCode(SYS_ORG_CODE);
				// 更新网络配置信息
				deviceNetwork.setIpaddr((String) networkConfigMap.get("lan_ip"));
				deviceNetwork.setNetmask((String) networkConfigMap.get("lan_netmask"));
				deviceNetwork.setDhcpStart((String) networkConfigMap.get("dhcp_start"));
				deviceNetwork.setDhcpEnd((String) networkConfigMap.get("dhcp_end")); // 从dhcp_range中提取结束地址
				deviceNetwork.setDhcpLeasetime((String) networkConfigMap.get("dhcp_lease"));
				deviceNetwork.setUpdateTime(new Date());
				deviceNetwork.setUpdateBy(ADMIN_USER);
				// 保存或更新配置
				this.saveOrUpdate(deviceNetwork);
			}
		} catch (Exception e) {
			throw new Exception("处理网络配置失败：" + e.getMessage());
		} finally {
			// 释放锁
			deviceLock.unlock();
		}
	}
}
