package org.jeecg.modules.cpe.contract.info.controller;

import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.cpe.contract.info.entity.ContractDevice;
import org.jeecg.modules.cpe.contract.info.entity.ContractInfo;
import org.jeecg.modules.cpe.contract.info.service.IContractInfoService;
import org.jeecg.modules.cpe.contract.info.service.IContractDeviceService;
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
 * @Description: 合同信息表
 * @Author: jeecg-boot
 * @Date:   2025-10-11
 * @Version: V1.0
 */
@Api(tags="合同信息表")
@RestController
@RequestMapping("/contract/info/contractInfo")
@Slf4j
public class ContractInfoController extends JeecgController<ContractInfo, IContractInfoService> {

	@Autowired
	private IContractInfoService contractInfoService;

	@Autowired
	private IContractDeviceService contractDeviceService;


	/*---------------------------------主表处理-begin-------------------------------------*/

	/**
	 * 分页列表查询
	 * @param contractInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "合同信息表-分页列表查询")
	@ApiOperation(value="合同信息表-分页列表查询", notes="合同信息表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ContractInfo>> queryPageList(ContractInfo contractInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
		QueryWrapper<ContractInfo> queryWrapper = QueryGenerator.initQueryWrapper(contractInfo, req.getParameterMap(),customeRuleMap);
		queryWrapper.likeRight("sys_org_code", sysUser.getOrgCode());
		Page<ContractInfo> page = new Page<ContractInfo>(pageNo, pageSize);
		IPage<ContractInfo> pageList = contractInfoService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
     *   添加
     * @param contractInfo
     * @return
     */
    @AutoLog(value = "合同信息表-添加")
    @ApiOperation(value="合同信息表-添加", notes="合同信息表-添加")
    @RequiresPermissions("contract.info:contract_info:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody ContractInfo contractInfo) {
        contractInfoService.save(contractInfo);
        return Result.OK("添加成功！");
    }

    /**
     *  编辑
     * @param contractInfo
     * @return
     */
    @AutoLog(value = "合同信息表-编辑")
    @ApiOperation(value="合同信息表-编辑", notes="合同信息表-编辑")
    @RequiresPermissions("contract.info:contract_info:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<String> edit(@RequestBody ContractInfo contractInfo) {
        contractInfoService.updateById(contractInfo);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     * @param id
     * @return
     */
    @AutoLog(value = "合同信息表-通过id删除")
    @ApiOperation(value="合同信息表-通过id删除", notes="合同信息表-通过id删除")
    @RequiresPermissions("contract.info:contract_info:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name="id",required=true) String id) {
        contractInfoService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @AutoLog(value = "合同信息表-批量删除")
    @ApiOperation(value="合同信息表-批量删除", notes="合同信息表-批量删除")
    @RequiresPermissions("contract.info:contract_info:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        this.contractInfoService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 导出
     * @return
     */
    @RequiresPermissions("contract.info:contract_info:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ContractInfo contractInfo) {
        return super.exportXls(request, contractInfo, ContractInfo.class, "合同信息表");
    }

    /**
     * 导入
     * @return
     */
    @RequiresPermissions("contract.info:contract_info:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ContractInfo.class);
    }
	/*---------------------------------主表处理-end-------------------------------------*/
	

    /*--------------------------------子表处理-合同设备表-begin----------------------------------------------*/
	/**
	 * 通过主表ID查询
	 * @return
	 */
	//@AutoLog(value = "合同设备表-通过主表ID查询")
	@ApiOperation(value="合同设备表-通过主表ID查询", notes="合同设备表-通过主表ID查询")
	@GetMapping(value = "/listContractDeviceByMainId")
    public Result<IPage<ContractDevice>> listContractDeviceByMainId(ContractDevice contractDevice,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        Page<ContractDevice> page = new Page<ContractDevice>(pageNo, pageSize);
        // 使用 mapper 的 selectByMainId 方法获取数据
        IPage<ContractDevice> pageList = contractDeviceService.pageByMainId(page, contractDevice.getContractId());
        return Result.OK(pageList);
    }

	/**
	 * 添加
	 * @param contractDevice
	 * @return
	 */
	@AutoLog(value = "合同设备表-添加")
	@ApiOperation(value="合同设备表-添加", notes="合同设备表-添加")
	@PostMapping(value = "/addContractDevice")
	public Result<String> addContractDevice(@RequestBody ContractDevice contractDevice) {
		String cpeId = contractDevice.getCpeId();
		if(cpeId != null && cpeId.contains(",")) {
			String[] cpes = cpeId.split(",");
			for (String cpe : cpes) {
				ContractDevice device = new ContractDevice();
				device.setContractId(contractDevice.getContractId());
				device.setCpeId(cpe);
				contractDeviceService.saveOrUpdate(device);
			}
		} else {
			contractDeviceService.saveOrUpdate(contractDevice);
		}
		return Result.OK("添加成功！");
	}

    /**
	 * 编辑
	 * @param contractDevice
	 * @return
	 */
	@AutoLog(value = "合同设备表-编辑")
	@ApiOperation(value="合同设备表-编辑", notes="合同设备表-编辑")
	@RequestMapping(value = "/editContractDevice", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> editContractDevice(@RequestBody ContractDevice contractDevice) {
		contractDeviceService.saveOrUpdate(contractDevice);
		return Result.OK("编辑成功!");
	}

	/**
	 * 通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "合同设备表-通过id删除")
	@ApiOperation(value="合同设备表-通过id删除", notes="合同设备表-通过id删除")
	@DeleteMapping(value = "/deleteContractDevice")
	public Result<String> deleteContractDevice(@RequestParam(name="id",required=true) String id) {
		contractDeviceService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "合同设备表-批量删除")
	@ApiOperation(value="合同设备表-批量删除", notes="合同设备表-批量删除")
	@DeleteMapping(value = "/deleteBatchContractDevice")
	public Result<String> deleteBatchContractDevice(@RequestParam(name="ids",required=true) String ids) {
	    this.contractDeviceService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/exportContractDevice")
    public ModelAndView exportContractDevice(HttpServletRequest request, ContractDevice contractDevice) {
		 // Step.1 组装查询条件
		 QueryWrapper<ContractDevice> queryWrapper = QueryGenerator.initQueryWrapper(contractDevice, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 // Step.2 获取导出数据
		 List<ContractDevice> pageList = contractDeviceService.list(queryWrapper);
		 List<ContractDevice> exportList = null;

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
		 mv.addObject(NormalExcelConstants.FILE_NAME, "合同设备表");
		 mv.addObject(NormalExcelConstants.CLASS, ContractDevice.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("合同设备表报表", "导出人:" + sysUser.getRealname(), "合同设备表"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		 return mv;
    }

    /**
     * 导入
     * @return
     */
    @RequestMapping(value = "/importContractDevice/{mainId}")
    public Result<?> importContractDevice(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
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
				 List<ContractDevice> list = ExcelImportUtil.importExcel(file.getInputStream(), ContractDevice.class, params);
				 for (ContractDevice temp : list) {
                    temp.setContractId(mainId);
				 }
				 long start = System.currentTimeMillis();
				 contractDeviceService.saveBatch(list);
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

    /*--------------------------------子表处理-合同设备表-end----------------------------------------------*/




}
