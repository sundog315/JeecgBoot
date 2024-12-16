package org.jeecg.modules.cpe.device.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.modules.cpe.device.entity.CpeDevice;
import org.jeecg.modules.cpe.device.service.ICpeDeviceService;

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
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2025-01-02
 * @Version: V1.0
 */
@Api(tags="设备信息表")
@RestController
@RequestMapping("/cpe/device/cpeDevice")
@Slf4j
public class CpeDeviceController extends JeecgController<CpeDevice, ICpeDeviceService> {
	@Autowired
	private ICpeDeviceService cpeDeviceService;
	
	/**
	 * 分页列表查询
	 *
	 * @param cpeDevice
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "设备信息表-分页列表查询")
	@ApiOperation(value="设备信息表-分页列表查询", notes="设备信息表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CpeDevice>> queryPageList(CpeDevice cpeDevice,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("deviceStatusNo", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("customerName", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<CpeDevice> queryWrapper = QueryGenerator.initQueryWrapper(cpeDevice, req.getParameterMap(),customeRuleMap);
		Page<CpeDevice> page = new Page<CpeDevice>(pageNo, pageSize);
		IPage<CpeDevice> pageList = cpeDeviceService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param cpeDevice
	 * @return
	 */
	@AutoLog(value = "设备信息表-添加")
	@ApiOperation(value="设备信息表-添加", notes="设备信息表-添加")
	@RequiresPermissions("cpe.device:cpe_device:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CpeDevice cpeDevice) {
		cpeDeviceService.save(cpeDevice);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param cpeDevice
	 * @return
	 */
	@AutoLog(value = "设备信息表-编辑")
	@ApiOperation(value="设备信息表-编辑", notes="设备信息表-编辑")
	@RequiresPermissions("cpe.device:cpe_device:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CpeDevice cpeDevice) {
		cpeDeviceService.updateById(cpeDevice);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "设备信息表-通过id删除")
	@ApiOperation(value="设备信息表-通过id删除", notes="设备信息表-通过id删除")
	@RequiresPermissions("cpe.device:cpe_device:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		cpeDeviceService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "设备信息表-批量删除")
	@ApiOperation(value="设备信息表-批量删除", notes="设备信息表-批量删除")
	@RequiresPermissions("cpe.device:cpe_device:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.cpeDeviceService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "设备信息表-通过id查询")
	@ApiOperation(value="设备信息表-通过id查询", notes="设备信息表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CpeDevice> queryById(@RequestParam(name="id",required=true) String id) {
		CpeDevice cpeDevice = cpeDeviceService.getById(id);
		if(cpeDevice==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(cpeDevice);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param cpeDevice
    */
    @RequiresPermissions("cpe.device:cpe_device:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CpeDevice cpeDevice) {
        return super.exportXls(request, cpeDevice, CpeDevice.class, "设备信息表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("cpe.device:cpe_device:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CpeDevice.class);
    }

}
