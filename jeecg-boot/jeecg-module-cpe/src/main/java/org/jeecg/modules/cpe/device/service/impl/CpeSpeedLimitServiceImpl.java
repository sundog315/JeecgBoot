package org.jeecg.modules.cpe.device.service.impl;

import org.jeecg.modules.cpe.device.entity.CpeDevice;
import org.jeecg.modules.cpe.device.entity.CpeOperLog;
import org.jeecg.modules.cpe.device.entity.CpeSpeedLimit;
import org.jeecg.modules.cpe.device.mapper.CpeSpeedLimitMapper;
import org.jeecg.modules.cpe.device.service.ICpeDeviceService;
import org.jeecg.modules.cpe.device.service.ICpeOperLogService;
import org.jeecg.modules.cpe.device.service.ICpeSpeedLimitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 设备速率
 * @Author: jeecg-boot
 * @Date:   2025-01-13
 * @Version: V1.0
 */
@Service
@Slf4j
public class CpeSpeedLimitServiceImpl extends ServiceImpl<CpeSpeedLimitMapper, CpeSpeedLimit> implements ICpeSpeedLimitService {

    @Autowired
    private CpeSpeedLimitMapper cpeSpeedLimitMapper;

    @Autowired
    private ICpeDeviceService cpeDeviceService;

    @Autowired
    private ICpeOperLogService cpeOperLogService;

    @Autowired
    private ObjectMapper objectMapper;

    // 用于并发控制的锁
    private static final ConcurrentHashMap<String, Lock> DEVICE_LOCKS = new ConcurrentHashMap<>();
	private static final String ADMIN_USER = "admin";
    private static final String SYS_ORG_CODE = "A01";
    private static final int LOCK_TIMEOUT_SECONDS = 5;

    private Lock getDeviceLock(String deviceSn) {
        return DEVICE_LOCKS.computeIfAbsent(deviceSn, k -> new ReentrantLock());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void report(String deviceSn, String speedLimitJson) throws Exception {
        Lock lock = getDeviceLock(deviceSn);
        if (!lock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            throw new Exception("获取设备锁超时");
        }

        try {
			// 标准化设备序列号格式（移除冒号并转换为大写）
			deviceSn = deviceSn.replace(":", "").toUpperCase();

            // 查询设备信息
            List<CpeDevice> devices = cpeDeviceService.selectByDeviceSn(deviceSn);
            if (devices == null || devices.isEmpty()) {
                throw new Exception("设备不存在: " + deviceSn);
            }
            CpeDevice device = devices.get(0);

            // 解析速率限制JSON
            JsonNode speedLimitNode = objectMapper.readTree(speedLimitJson);
            String upLimit = speedLimitNode.get("up").asText();
            String downLimit = speedLimitNode.get("down").asText();

            // 查询现有速率限制记录
            QueryWrapper<CpeSpeedLimit> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("cpe_id", device.getId());
            CpeSpeedLimit existingLimit = getOne(queryWrapper);

            // 获取当前时间
            Date now = new Date();

            if (existingLimit == null) {
                // 创建新记录
                CpeSpeedLimit newLimit = new CpeSpeedLimit();
                newLimit.setCpeId(device.getId());
                newLimit.setUpLimit(upLimit);
                newLimit.setDownLimit(downLimit);
                newLimit.setCreateTime(now);
                newLimit.setUpdateTime(now);
				newLimit.setCreateBy(ADMIN_USER);
				newLimit.setUpdateBy(ADMIN_USER);
				newLimit.setSysOrgCode(SYS_ORG_CODE);
                save(newLimit);

                log.info("新增速率限制记录: 设备={}, 上传={}, 下载={}", deviceSn, upLimit, downLimit);
            } else if (!upLimit.equals(existingLimit.getUpLimit()) || 
                    !downLimit.equals(existingLimit.getDownLimit())) {
                // 记录变更前的值
                String oldValue = String.format("%s,%s", 
                    existingLimit.getUpLimit(), existingLimit.getDownLimit());

                // 创建操作日志
                CpeOperLog operLog = new CpeOperLog();
                operLog.setCpeId(device.getId());
                operLog.setOperType("speed_limit");
                operLog.setOperParam(oldValue);
				operLog.setCreateBy(ADMIN_USER);
				operLog.setCreateTime(new Date());
				operLog.setCreateTs(new Date());
				operLog.setSysOrgCode(SYS_ORG_CODE);
                cpeOperLogService.save(operLog);

                log.info("更新速率限制记录: 设备={}, 上传={}, 下载={}, 原值={}", 
                    deviceSn, upLimit, downLimit, oldValue);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<CpeSpeedLimit> selectByMainId(String mainId) {
        return cpeSpeedLimitMapper.selectByMainId(mainId);
    }
}
