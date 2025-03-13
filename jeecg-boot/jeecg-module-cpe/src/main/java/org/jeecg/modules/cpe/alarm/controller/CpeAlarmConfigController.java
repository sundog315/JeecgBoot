package org.jeecg.modules.cpe.alarm.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.cpe.alarm.entity.CpeAlarmConfig;
import org.jeecg.modules.cpe.alarm.service.ICpeAlarmConfigService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 告警策略
 * @Author: jeecg-boot
 * @Date:   2025-03-12
 * @Version: V1.0
 */
@Api(tags="告警策略")
@RestController
@RequestMapping("/cpe/alarm/cpeAlarmConfig")
@Slf4j
public class CpeAlarmConfigController extends JeecgController<CpeAlarmConfig, ICpeAlarmConfigService> {
	@Autowired
	private ICpeAlarmConfigService cpeAlarmConfigService;
	
	/**
	 * 分页列表查询
	 *
	 * @param cpeAlarmConfig
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "告警策略-分页列表查询")
	@ApiOperation(value="告警策略-分页列表查询", notes="告警策略-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CpeAlarmConfig>> queryPageList(CpeAlarmConfig cpeAlarmConfig,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<CpeAlarmConfig> queryWrapper = QueryGenerator.initQueryWrapper(cpeAlarmConfig, req.getParameterMap());
		Page<CpeAlarmConfig> page = new Page<CpeAlarmConfig>(pageNo, pageSize);
		IPage<CpeAlarmConfig> pageList = cpeAlarmConfigService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param cpeAlarmConfig
	 * @return
	 */
	@AutoLog(value = "告警策略-添加")
	@ApiOperation(value="告警策略-添加", notes="告警策略-添加")
	@RequiresPermissions("cpe.alarm:cpe_alarm_config:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CpeAlarmConfig cpeAlarmConfig) {
		cpeAlarmConfigService.save(cpeAlarmConfig);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param cpeAlarmConfig
	 * @return
	 */
	@AutoLog(value = "告警策略-编辑")
	@ApiOperation(value="告警策略-编辑", notes="告警策略-编辑")
	@RequiresPermissions("cpe.alarm:cpe_alarm_config:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CpeAlarmConfig cpeAlarmConfig) {
		cpeAlarmConfigService.updateById(cpeAlarmConfig);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "告警策略-通过id删除")
	@ApiOperation(value="告警策略-通过id删除", notes="告警策略-通过id删除")
	@RequiresPermissions("cpe.alarm:cpe_alarm_config:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		cpeAlarmConfigService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "告警策略-批量删除")
	@ApiOperation(value="告警策略-批量删除", notes="告警策略-批量删除")
	@RequiresPermissions("cpe.alarm:cpe_alarm_config:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.cpeAlarmConfigService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "告警策略-通过id查询")
	@ApiOperation(value="告警策略-通过id查询", notes="告警策略-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CpeAlarmConfig> queryById(@RequestParam(name="id",required=true) String id) {
		CpeAlarmConfig cpeAlarmConfig = cpeAlarmConfigService.getById(id);
		if(cpeAlarmConfig==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(cpeAlarmConfig);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param cpeAlarmConfig
    */
    @RequiresPermissions("cpe.alarm:cpe_alarm_config:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CpeAlarmConfig cpeAlarmConfig) {
        return super.exportXls(request, cpeAlarmConfig, CpeAlarmConfig.class, "告警策略");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("cpe.alarm:cpe_alarm_config:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CpeAlarmConfig.class);
    }

}
