package org.jeecg.modules.demo.cpe.device.service;

import org.jeecg.modules.demo.cpe.device.entity.CpeOperLog;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 操作记录表
 * @Author: jeecg-boot
 * @Date:   2025-02-24
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
