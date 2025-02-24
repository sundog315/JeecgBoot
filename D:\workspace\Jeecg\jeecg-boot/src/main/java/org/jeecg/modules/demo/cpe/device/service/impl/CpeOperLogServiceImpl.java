package org.jeecg.modules.demo.cpe.device.service.impl;

import org.jeecg.modules.demo.cpe.device.entity.CpeOperLog;
import org.jeecg.modules.demo.cpe.device.mapper.CpeOperLogMapper;
import org.jeecg.modules.demo.cpe.device.service.ICpeOperLogService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 操作记录表
 * @Author: jeecg-boot
 * @Date:   2025-02-24
 * @Version: V1.0
 */
@Service
public class CpeOperLogServiceImpl extends ServiceImpl<CpeOperLogMapper, CpeOperLog> implements ICpeOperLogService {
	
	@Autowired
	private CpeOperLogMapper cpeOperLogMapper;
	
	@Override
	public List<CpeOperLog> selectByMainId(String mainId) {
		return cpeOperLogMapper.selectByMainId(mainId);
	}
}
