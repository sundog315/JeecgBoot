package org.jeecg.quartz.job;

import java.util.Date;
import java.util.List;

import org.jeecg.modules.cpe.device.entity.CpeDevice;
import org.jeecg.modules.cpe.device.service.ICpeDeviceNeighborService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceStatusService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobCleanExpiredData implements Job {
    @Autowired
	private ICpeDeviceService cpeDeviceService;
	@Autowired
	private ICpeDeviceStatusService cpeDeviceStatusService;
	@Autowired
	private ICpeDeviceNeighborService cpeDeviceNeighborService;

    public int doCleanData() throws Exception {
        //清理两天前的设备状态信息
        Date datePoint = new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000); // 2 days ago
        cpeDeviceStatusService.deleteByTs(datePoint);

        List<CpeDevice> cpeDevices = cpeDeviceService.list(); // Correctly call the list method
        for (CpeDevice device : cpeDevices) { // Iterate over the list of devices
            // Add logic to clean expired data for each device
            Date newTs = cpeDeviceStatusService.selectNewtestTsByMainId(device.getId());

            if (newTs != null)
                //超过3分钟未上报的设备清理主表信息并置为离线
                if ((newTs.before(new Date(System.currentTimeMillis() - 3 * 60 * 1000))) && device.getDeviceStatusNo().equals("1"))
                {
                    device.setOnlineBand("");
                    device.setOnlineCardNo("");
                    device.setOnlineNetNo("");
                    device.setOnlineBand("");
                    device.setDeviceStatusNo("2");

                    //修改主表信息
                    cpeDeviceService.updateById(device);
                    //清楚邻居信息
                    cpeDeviceNeighborService.deleteByMainId(device.getId());
                }
        }

        return 0;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long t1 = System.currentTimeMillis(); // 记录开始时间
        log.info("开始数据清理任务"); // 日志记录
        try {
            doCleanData(); // 执行授权操作
        } catch (Exception e) {
            log.error(e.getMessage()); // 记录错误信息
            e.printStackTrace(); // 打印堆栈跟踪
        }
        log.info("数据清理任务执行完毕, 消耗：" + (System.currentTimeMillis() - t1)); // 记录执行时间
    }
    
}
