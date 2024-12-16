package org.jeecg.modules.cpe.device.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

import org.jeecg.modules.cpe.device.entity.CpeOperLog;

/**
 * @Description: 操作记录表
 * @Author: jeecg-boot
 * @Date:   2024-12-30
 * @Version: V1.0
 */
public interface ICpeOperLogService extends IService<CpeOperLog> {

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId
   * @return List<CpeOperLog>
   */
	public List<CpeOperLog> selectByMainId(String mainId);
}
