package org.jeecg.modules.cpe.device.controller;

import org.jeecg.common.system.query.QueryGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.system.query.QueryRuleEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.cpe.device.entity.CpeDeviceStatus;
import org.jeecg.modules.cpe.device.entity.CpeDeviceNeighbor;
import org.jeecg.modules.cpe.device.entity.CpeDeviceFrp;
import org.jeecg.modules.cpe.device.entity.CpeOperLog;
import org.jeecg.modules.cpe.device.entity.CpeDeviceInfo;
import org.jeecg.modules.cpe.device.service.ICpeDeviceInfoService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceStatusService;
import org.jeecg.modules.cpe.device.service.ICpeOperLogService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceFrpService;
import org.jeecg.modules.cpe.device.service.ICpeDeviceNeighborService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.shiro.authz.annotation.RequiresPermissions;
 /**
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2024-12-29
 * @Version: V1.0
 */
@Api(tags="设备信息表")
@RestController
@RequestMapping("/cpe/device/cpeDeviceInfo")
@Slf4j
public class CpeDeviceInfoController extends JeecgController<CpeDeviceInfo, ICpeDeviceInfoService> {

	@Autowired
	private ICpeDeviceInfoService cpeDeviceInfoService;

	@Autowired
	private ICpeDeviceStatusService cpeDeviceStatusService;

	@Autowired
	private ICpeDeviceNeighborService cpeDeviceNeighborService;

	@Autowired
	private ICpeDeviceFrpService cpeDeviceFrpService;

	@Autowired
	private ICpeOperLogService cpeOperLogService;


	/*---------------------------------主表处理-begin-------------------------------------*/

	/**
	 * 分页列表查询
	 * @param cpeDeviceInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "设备信息表-分页列表查询")
	@ApiOperation(value="设备信息表-分页列表查询", notes="设备信息表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CpeDeviceInfo>> queryPageList(CpeDeviceInfo cpeDeviceInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("deviceStatusNo", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("customerName", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<CpeDeviceInfo> queryWrapper = QueryGenerator.initQueryWrapper(cpeDeviceInfo, req.getParameterMap(),customeRuleMap);
		Page<CpeDeviceInfo> page = new Page<CpeDeviceInfo>(pageNo, pageSize);
		IPage<CpeDeviceInfo> pageList = cpeDeviceInfoService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
     *   添加
     * @param cpeDeviceInfo
     * @return
     */
    @AutoLog(value = "设备信息表-添加")
    @ApiOperation(value="设备信息表-添加", notes="设备信息表-添加")
    @RequiresPermissions("cpe.device:cpe_device_info:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody CpeDeviceInfo cpeDeviceInfo) {
        cpeDeviceInfoService.save(cpeDeviceInfo);
        return Result.OK("添加成功！");
    }

    /**
     *  编辑
     * @param cpeDeviceInfo
     * @return
     */
    @AutoLog(value = "设备信息表-编辑")
    @ApiOperation(value="设备信息表-编辑", notes="设备信息表-编辑")
    @RequiresPermissions("cpe.device:cpe_device_info:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<String> edit(@RequestBody CpeDeviceInfo cpeDeviceInfo) {
        cpeDeviceInfoService.updateById(cpeDeviceInfo);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     * @param id
     * @return
     */
    @AutoLog(value = "设备信息表-通过id删除")
    @ApiOperation(value="设备信息表-通过id删除", notes="设备信息表-通过id删除")
    @RequiresPermissions("cpe.device:cpe_device_info:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name="id",required=true) String id) {
        cpeDeviceInfoService.delMain(id);
        return Result.OK("删除成功!");
    }

	/**
     * 重启设备操作
     * @param id
     * @return
     */
    @AutoLog(value = "设备信息表-重启设备操作")
    @ApiOperation(value="设备信息表-重启设备操作", notes="设备信息表-重启设备操作")
    //@RequiresPermissions("cpe.device:cpe_device_info:reboot")
    @PostMapping(value = "/reboot")
    public Result<String> reboot(@RequestParam(name="id",required=true) String id) {
		CpeOperLog oper = new CpeOperLog();
		oper.setCpeId(id);
		oper.setOperType("reboot");
		oper.setCreateTs(new Date());

		cpeOperLogService.save(oper);
        return Result.OK("操作成功!");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @AutoLog(value = "设备信息表-批量删除")
    @ApiOperation(value="设备信息表-批量删除", notes="设备信息表-批量删除")
    @RequiresPermissions("cpe.device:cpe_device_info:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        this.cpeDeviceInfoService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 导出
     * @return
     */
    @RequiresPermissions("cpe.device:cpe_device_info:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CpeDeviceInfo cpeDeviceInfo) {
        return super.exportXls(request, cpeDeviceInfo, CpeDeviceInfo.class, "设备信息表");
    }

    /**
     * 导入
     * @return
     */
    @RequiresPermissions("cpe.device:cpe_device_info:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CpeDeviceInfo.class);
    }
	/*---------------------------------主表处理-end-------------------------------------*/
	

    /*--------------------------------子表处理-CPE设备状态表-begin----------------------------------------------*/
	/**
	 * 通过主表ID查询
	 * @return
	 */
	//@AutoLog(value = "CPE设备状态表-通过主表ID查询")
	@ApiOperation(value="CPE设备状态表-通过主表ID查询", notes="CPE设备状态表-通过主表ID查询")
	@GetMapping(value = "/listCpeDeviceStatusByMainId")
    public Result<IPage<CpeDeviceStatus>> listCpeDeviceStatusByMainId(CpeDeviceStatus cpeDeviceStatus,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        QueryWrapper<CpeDeviceStatus> queryWrapper = QueryGenerator.initQueryWrapper(cpeDeviceStatus, req.getParameterMap());
        Page<CpeDeviceStatus> page = new Page<CpeDeviceStatus>(pageNo, pageSize);
        IPage<CpeDeviceStatus> pageList = cpeDeviceStatusService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

	/**
	 * 添加
	 * @param cpeDeviceStatus
	 * @return
	 */
	@AutoLog(value = "CPE设备状态表-添加")
	@ApiOperation(value="CPE设备状态表-添加", notes="CPE设备状态表-添加")
	@PostMapping(value = "/addCpeDeviceStatus")
	public Result<String> addCpeDeviceStatus(@RequestBody CpeDeviceStatus cpeDeviceStatus) {
		cpeDeviceStatusService.save(cpeDeviceStatus);
		return Result.OK("添加成功！");
	}

    /**
	 * 编辑
	 * @param cpeDeviceStatus
	 * @return
	 */
	@AutoLog(value = "CPE设备状态表-编辑")
	@ApiOperation(value="CPE设备状态表-编辑", notes="CPE设备状态表-编辑")
	@RequestMapping(value = "/editCpeDeviceStatus", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> editCpeDeviceStatus(@RequestBody CpeDeviceStatus cpeDeviceStatus) {
		cpeDeviceStatusService.updateById(cpeDeviceStatus);
		return Result.OK("编辑成功!");
	}

	/**
	 * 通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "CPE设备状态表-通过id删除")
	@ApiOperation(value="CPE设备状态表-通过id删除", notes="CPE设备状态表-通过id删除")
	@DeleteMapping(value = "/deleteCpeDeviceStatus")
	public Result<String> deleteCpeDeviceStatus(@RequestParam(name="id",required=true) String id) {
		cpeDeviceStatusService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "CPE设备状态表-批量删除")
	@ApiOperation(value="CPE设备状态表-批量删除", notes="CPE设备状态表-批量删除")
	@DeleteMapping(value = "/deleteBatchCpeDeviceStatus")
	public Result<String> deleteBatchCpeDeviceStatus(@RequestParam(name="ids",required=true) String ids) {
	    this.cpeDeviceStatusService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/exportCpeDeviceStatus")
    public ModelAndView exportCpeDeviceStatus(HttpServletRequest request, CpeDeviceStatus cpeDeviceStatus) {
		 // Step.1 组装查询条件
		 QueryWrapper<CpeDeviceStatus> queryWrapper = QueryGenerator.initQueryWrapper(cpeDeviceStatus, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 // Step.2 获取导出数据
		 List<CpeDeviceStatus> pageList = cpeDeviceStatusService.list(queryWrapper);
		 List<CpeDeviceStatus> exportList = null;

		 // 过滤选中数据
		 String selections = request.getParameter("selections");
		 if (oConvertUtils.isNotEmpty(selections)) {
			 List<String> selectionList = Arrays.asList(selections.split(","));
			 exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		 } else {
			 exportList = pageList;
		 }

		 // Step.3 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 //此处设置的filename无效,前端会重更新设置一下
		 mv.addObject(NormalExcelConstants.FILE_NAME, "CPE设备状态表");
		 mv.addObject(NormalExcelConstants.CLASS, CpeDeviceStatus.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("CPE设备状态表报表", "导出人:" + sysUser.getRealname(), "CPE设备状态表"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		 return mv;
    }

    /**
     * 导入
     * @return
     */
    @RequestMapping(value = "/importCpeDeviceStatus/{mainId}")
    public Result<?> importCpeDeviceStatus(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
       // 获取上传文件对象
			 MultipartFile file = entity.getValue();
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<CpeDeviceStatus> list = ExcelImportUtil.importExcel(file.getInputStream(), CpeDeviceStatus.class, params);
				 for (CpeDeviceStatus temp : list) {
                    temp.setCpeId(mainId);
				 }
				 long start = System.currentTimeMillis();
				 cpeDeviceStatusService.saveBatch(list);
				 log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
				 return Result.OK("文件导入成功！数据行数：" + list.size());
			 } catch (Exception e) {
				 log.error(e.getMessage(), e);
				 return Result.error("文件导入失败:" + e.getMessage());
			 } finally {
				 try {
					 file.getInputStream().close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 return Result.error("文件导入失败！");
    }

    /*--------------------------------子表处理-CPE设备状态表-end----------------------------------------------*/

    /*--------------------------------子表处理-CPE设备邻区信息-begin----------------------------------------------*/
	/**
	 * 通过主表ID查询
	 * @return
	 */
	//@AutoLog(value = "CPE设备邻区信息-通过主表ID查询")
	@ApiOperation(value="CPE设备邻区信息-通过主表ID查询", notes="CPE设备邻区信息-通过主表ID查询")
	@GetMapping(value = "/listCpeDeviceNeighborByMainId")
    public Result<IPage<CpeDeviceNeighbor>> listCpeDeviceNeighborByMainId(CpeDeviceNeighbor cpeDeviceNeighbor,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        QueryWrapper<CpeDeviceNeighbor> queryWrapper = QueryGenerator.initQueryWrapper(cpeDeviceNeighbor, req.getParameterMap());
        Page<CpeDeviceNeighbor> page = new Page<CpeDeviceNeighbor>(pageNo, pageSize);
        IPage<CpeDeviceNeighbor> pageList = cpeDeviceNeighborService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

	/**
	 * 添加
	 * @param cpeDeviceNeighbor
	 * @return
	 */
	@AutoLog(value = "CPE设备邻区信息-添加")
	@ApiOperation(value="CPE设备邻区信息-添加", notes="CPE设备邻区信息-添加")
	@PostMapping(value = "/addCpeDeviceNeighbor")
	public Result<String> addCpeDeviceNeighbor(@RequestBody CpeDeviceNeighbor cpeDeviceNeighbor) {
		cpeDeviceNeighborService.save(cpeDeviceNeighbor);
		return Result.OK("添加成功！");
	}

    /**
	 * 编辑
	 * @param cpeDeviceNeighbor
	 * @return
	 */
	@AutoLog(value = "CPE设备邻区信息-编辑")
	@ApiOperation(value="CPE设备邻区信息-编辑", notes="CPE设备邻区信息-编辑")
	@RequestMapping(value = "/editCpeDeviceNeighbor", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> editCpeDeviceNeighbor(@RequestBody CpeDeviceNeighbor cpeDeviceNeighbor) {
		cpeDeviceNeighborService.updateById(cpeDeviceNeighbor);
		return Result.OK("编辑成功!");
	}

	/**
	 * 通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "CPE设备邻区信息-通过id删除")
	@ApiOperation(value="CPE设备邻区信息-通过id删除", notes="CPE设备邻区信息-通过id删除")
	@DeleteMapping(value = "/deleteCpeDeviceNeighbor")
	public Result<String> deleteCpeDeviceNeighbor(@RequestParam(name="id",required=true) String id) {
		cpeDeviceNeighborService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "CPE设备邻区信息-批量删除")
	@ApiOperation(value="CPE设备邻区信息-批量删除", notes="CPE设备邻区信息-批量删除")
	@DeleteMapping(value = "/deleteBatchCpeDeviceNeighbor")
	public Result<String> deleteBatchCpeDeviceNeighbor(@RequestParam(name="ids",required=true) String ids) {
	    this.cpeDeviceNeighborService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/exportCpeDeviceNeighbor")
    public ModelAndView exportCpeDeviceNeighbor(HttpServletRequest request, CpeDeviceNeighbor cpeDeviceNeighbor) {
		 // Step.1 组装查询条件
		 QueryWrapper<CpeDeviceNeighbor> queryWrapper = QueryGenerator.initQueryWrapper(cpeDeviceNeighbor, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 // Step.2 获取导出数据
		 List<CpeDeviceNeighbor> pageList = cpeDeviceNeighborService.list(queryWrapper);
		 List<CpeDeviceNeighbor> exportList = null;

		 // 过滤选中数据
		 String selections = request.getParameter("selections");
		 if (oConvertUtils.isNotEmpty(selections)) {
			 List<String> selectionList = Arrays.asList(selections.split(","));
			 exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		 } else {
			 exportList = pageList;
		 }

		 // Step.3 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 //此处设置的filename无效,前端会重更新设置一下
		 mv.addObject(NormalExcelConstants.FILE_NAME, "CPE设备邻区信息");
		 mv.addObject(NormalExcelConstants.CLASS, CpeDeviceNeighbor.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("CPE设备邻区信息报表", "导出人:" + sysUser.getRealname(), "CPE设备邻区信息"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		 return mv;
    }

    /**
     * 导入
     * @return
     */
    @RequestMapping(value = "/importCpeDeviceNeighbor/{mainId}")
    public Result<?> importCpeDeviceNeighbor(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
       // 获取上传文件对象
			 MultipartFile file = entity.getValue();
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<CpeDeviceNeighbor> list = ExcelImportUtil.importExcel(file.getInputStream(), CpeDeviceNeighbor.class, params);
				 for (CpeDeviceNeighbor temp : list) {
                    temp.setCpeId(mainId);
				 }
				 long start = System.currentTimeMillis();
				 cpeDeviceNeighborService.saveBatch(list);
				 log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
				 return Result.OK("文件导入成功！数据行数：" + list.size());
			 } catch (Exception e) {
				 log.error(e.getMessage(), e);
				 return Result.error("文件导入失败:" + e.getMessage());
			 } finally {
				 try {
					 file.getInputStream().close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 return Result.error("文件导入失败！");
    }

    /*--------------------------------子表处理-CPE设备邻区信息-end----------------------------------------------*/

/*--------------------------------子表处理-设备远程控制-begin----------------------------------------------*/
	/**
	 * 通过主表ID查询
	 * @return
	 */
	//@AutoLog(value = "设备远程控制-通过主表ID查询")
	@ApiOperation(value="设备远程控制-通过主表ID查询", notes="设备远程控制-通过主表ID查询")
	@GetMapping(value = "/listCpeDeviceFrpByMainId")
    public Result<IPage<CpeDeviceFrp>> listCpeDeviceFrpByMainId(CpeDeviceFrp cpeDeviceFrp,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        QueryWrapper<CpeDeviceFrp> queryWrapper = QueryGenerator.initQueryWrapper(cpeDeviceFrp, req.getParameterMap());
        Page<CpeDeviceFrp> page = new Page<CpeDeviceFrp>(pageNo, pageSize);
        IPage<CpeDeviceFrp> pageList = cpeDeviceFrpService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

	/**
	 * 添加
	 * @param cpeDeviceFrp
	 * @return
	 */
	@AutoLog(value = "设备远程控制-添加")
	@ApiOperation(value="设备远程控制-添加", notes="设备远程控制-添加")
	@PostMapping(value = "/addCpeDeviceFrp")
	public Result<String> addCpeDeviceFrp(@RequestBody CpeDeviceFrp cpeDeviceFrp) {
		cpeDeviceFrpService.save(cpeDeviceFrp);
		return Result.OK("添加成功！");
	}

    /**
	 * 编辑
	 * @param cpeDeviceFrp
	 * @return
	 */
	@AutoLog(value = "设备远程控制-编辑")
	@ApiOperation(value="设备远程控制-编辑", notes="设备远程控制-编辑")
	@RequestMapping(value = "/editCpeDeviceFrp", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> editCpeDeviceFrp(@RequestBody CpeDeviceFrp cpeDeviceFrp) {
		cpeDeviceFrpService.updateById(cpeDeviceFrp);
		return Result.OK("编辑成功!");
	}

	/**
	 * 通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "设备远程控制-通过id删除")
	@ApiOperation(value="设备远程控制-通过id删除", notes="设备远程控制-通过id删除")
	@DeleteMapping(value = "/deleteCpeDeviceFrp")
	public Result<String> deleteCpeDeviceFrp(@RequestParam(name="id",required=true) String id) {
		cpeDeviceFrpService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "设备远程控制-批量删除")
	@ApiOperation(value="设备远程控制-批量删除", notes="设备远程控制-批量删除")
	@DeleteMapping(value = "/deleteBatchCpeDeviceFrp")
	public Result<String> deleteBatchCpeDeviceFrp(@RequestParam(name="ids",required=true) String ids) {
	    this.cpeDeviceFrpService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/exportCpeDeviceFrp")
    public ModelAndView exportCpeDeviceFrp(HttpServletRequest request, CpeDeviceFrp cpeDeviceFrp) {
		 // Step.1 组装查询条件
		 QueryWrapper<CpeDeviceFrp> queryWrapper = QueryGenerator.initQueryWrapper(cpeDeviceFrp, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 // Step.2 获取导出数据
		 List<CpeDeviceFrp> pageList = cpeDeviceFrpService.list(queryWrapper);
		 List<CpeDeviceFrp> exportList = null;

		 // 过滤选中数据
		 String selections = request.getParameter("selections");
		 if (oConvertUtils.isNotEmpty(selections)) {
			 List<String> selectionList = Arrays.asList(selections.split(","));
			 exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		 } else {
			 exportList = pageList;
		 }

		 // Step.3 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 //此处设置的filename无效,前端会重更新设置一下
		 mv.addObject(NormalExcelConstants.FILE_NAME, "设备远程控制");
		 mv.addObject(NormalExcelConstants.CLASS, CpeDeviceFrp.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("设备远程控制报表", "导出人:" + sysUser.getRealname(), "设备远程控制"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		 return mv;
    }

    /**
     * 导入
     * @return
     */
    @RequestMapping(value = "/importCpeDeviceFrp/{mainId}")
    public Result<?> importCpeDeviceFrp(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
       // 获取上传文件对象
			 MultipartFile file = entity.getValue();
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<CpeDeviceFrp> list = ExcelImportUtil.importExcel(file.getInputStream(), CpeDeviceFrp.class, params);
				 for (CpeDeviceFrp temp : list) {
                    temp.setCpeId(mainId);
				 }
				 long start = System.currentTimeMillis();
				 cpeDeviceFrpService.saveBatch(list);
				 log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
				 return Result.OK("文件导入成功！数据行数：" + list.size());
			 } catch (Exception e) {
				 log.error(e.getMessage(), e);
				 return Result.error("文件导入失败:" + e.getMessage());
			 } finally {
				 try {
					 file.getInputStream().close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 return Result.error("文件导入失败！");
    }

    /*--------------------------------子表处理-设备远程控制-end----------------------------------------------*/

 /*--------------------------------子表处理-操作记录表-begin----------------------------------------------*/
	/**
	 * 通过主表ID查询
	 * @return
	 */
	//@AutoLog(value = "操作记录表-通过主表ID查询")
	@ApiOperation(value="操作记录表-通过主表ID查询", notes="操作记录表-通过主表ID查询")
	@GetMapping(value = "/listCpeOperLogByMainId")
    public Result<IPage<CpeOperLog>> listCpeOperLogByMainId(CpeOperLog cpeOperLog,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        QueryWrapper<CpeOperLog> queryWrapper = QueryGenerator.initQueryWrapper(cpeOperLog, req.getParameterMap());
        Page<CpeOperLog> page = new Page<CpeOperLog>(pageNo, pageSize);
        IPage<CpeOperLog> pageList = cpeOperLogService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

	/**
	 * 添加
	 * @param cpeOperLog
	 * @return
	 */
	@AutoLog(value = "操作记录表-添加")
	@ApiOperation(value="操作记录表-添加", notes="操作记录表-添加")
	@PostMapping(value = "/addCpeOperLog")
	public Result<String> addCpeOperLog(@RequestBody CpeOperLog cpeOperLog) {
		cpeOperLogService.save(cpeOperLog);
		return Result.OK("添加成功！");
	}

    /**
	 * 编辑
	 * @param cpeOperLog
	 * @return
	 */
	@AutoLog(value = "操作记录表-编辑")
	@ApiOperation(value="操作记录表-编辑", notes="操作记录表-编辑")
	@RequestMapping(value = "/editCpeOperLog", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> editCpeOperLog(@RequestBody CpeOperLog cpeOperLog) {
		cpeOperLogService.updateById(cpeOperLog);
		return Result.OK("编辑成功!");
	}

	/**
	 * 通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "操作记录表-通过id删除")
	@ApiOperation(value="操作记录表-通过id删除", notes="操作记录表-通过id删除")
	@DeleteMapping(value = "/deleteCpeOperLog")
	public Result<String> deleteCpeOperLog(@RequestParam(name="id",required=true) String id) {
		cpeOperLogService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "操作记录表-批量删除")
	@ApiOperation(value="操作记录表-批量删除", notes="操作记录表-批量删除")
	@DeleteMapping(value = "/deleteBatchCpeOperLog")
	public Result<String> deleteBatchCpeOperLog(@RequestParam(name="ids",required=true) String ids) {
	    this.cpeOperLogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/exportCpeOperLog")
    public ModelAndView exportCpeOperLog(HttpServletRequest request, CpeOperLog cpeOperLog) {
		 // Step.1 组装查询条件
		 QueryWrapper<CpeOperLog> queryWrapper = QueryGenerator.initQueryWrapper(cpeOperLog, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 // Step.2 获取导出数据
		 List<CpeOperLog> pageList = cpeOperLogService.list(queryWrapper);
		 List<CpeOperLog> exportList = null;

		 // 过滤选中数据
		 String selections = request.getParameter("selections");
		 if (oConvertUtils.isNotEmpty(selections)) {
			 List<String> selectionList = Arrays.asList(selections.split(","));
			 exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
		 } else {
			 exportList = pageList;
		 }

		 // Step.3 AutoPoi 导出Excel
		 ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		 //此处设置的filename无效,前端会重更新设置一下
		 mv.addObject(NormalExcelConstants.FILE_NAME, "操作记录表");
		 mv.addObject(NormalExcelConstants.CLASS, CpeOperLog.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("操作记录表报表", "导出人:" + sysUser.getRealname(), "操作记录表"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		 return mv;
    }

    /**
     * 导入
     * @return
     */
    @RequestMapping(value = "/importCpeOperLog/{mainId}")
    public Result<?> importCpeOperLog(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
       // 获取上传文件对象
			 MultipartFile file = entity.getValue();
			 ImportParams params = new ImportParams();
			 params.setTitleRows(2);
			 params.setHeadRows(1);
			 params.setNeedSave(true);
			 try {
				 List<CpeOperLog> list = ExcelImportUtil.importExcel(file.getInputStream(), CpeOperLog.class, params);
				 for (CpeOperLog temp : list) {
                    temp.setCpeId(mainId);
				 }
				 long start = System.currentTimeMillis();
				 cpeOperLogService.saveBatch(list);
				 log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
				 return Result.OK("文件导入成功！数据行数：" + list.size());
			 } catch (Exception e) {
				 log.error(e.getMessage(), e);
				 return Result.error("文件导入失败:" + e.getMessage());
			 } finally {
				 try {
					 file.getInputStream().close();
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 return Result.error("文件导入失败！");
    }

    /*--------------------------------子表处理-操作记录表-end----------------------------------------------*/

}