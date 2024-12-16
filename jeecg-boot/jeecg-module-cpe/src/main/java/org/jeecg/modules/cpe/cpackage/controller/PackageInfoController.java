package org.jeecg.modules.cpe.cpackage.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.cpe.cpackage.entity.PackageInfo;
import org.jeecg.modules.cpe.cpackage.service.IPackageInfoService;

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
 * @Description: 套餐信息表
 * @Author: jeecg-boot
 * @Date:   2025-01-02
 * @Version: V1.0
 */
@Api(tags="套餐信息表")
@RestController
@RequestMapping("/cpe/cpackage/packageInfo")
@Slf4j
public class PackageInfoController extends JeecgController<PackageInfo, IPackageInfoService> {
	@Autowired
	private IPackageInfoService packageInfoService;
	
	/**
	 * 分页列表查询
	 *
	 * @param packageInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "套餐信息表-分页列表查询")
	@ApiOperation(value="套餐信息表-分页列表查询", notes="套餐信息表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PackageInfo>> queryPageList(PackageInfo packageInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<PackageInfo> queryWrapper = QueryGenerator.initQueryWrapper(packageInfo, req.getParameterMap());
		Page<PackageInfo> page = new Page<PackageInfo>(pageNo, pageSize);
		IPage<PackageInfo> pageList = packageInfoService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param packageInfo
	 * @return
	 */
	@AutoLog(value = "套餐信息表-添加")
	@ApiOperation(value="套餐信息表-添加", notes="套餐信息表-添加")
	@RequiresPermissions("cpe.cpackage:package_info:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PackageInfo packageInfo) {
		packageInfoService.save(packageInfo);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param packageInfo
	 * @return
	 */
	@AutoLog(value = "套餐信息表-编辑")
	@ApiOperation(value="套餐信息表-编辑", notes="套餐信息表-编辑")
	@RequiresPermissions("cpe.cpackage:package_info:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PackageInfo packageInfo) {
		packageInfoService.updateById(packageInfo);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "套餐信息表-通过id删除")
	@ApiOperation(value="套餐信息表-通过id删除", notes="套餐信息表-通过id删除")
	@RequiresPermissions("cpe.cpackage:package_info:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		packageInfoService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "套餐信息表-批量删除")
	@ApiOperation(value="套餐信息表-批量删除", notes="套餐信息表-批量删除")
	@RequiresPermissions("cpe.cpackage:package_info:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.packageInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "套餐信息表-通过id查询")
	@ApiOperation(value="套餐信息表-通过id查询", notes="套餐信息表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PackageInfo> queryById(@RequestParam(name="id",required=true) String id) {
		PackageInfo packageInfo = packageInfoService.getById(id);
		if(packageInfo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(packageInfo);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param packageInfo
    */
    @RequiresPermissions("cpe.cpackage:package_info:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PackageInfo packageInfo) {
        return super.exportXls(request, packageInfo, PackageInfo.class, "套餐信息表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("cpe.cpackage:package_info:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, PackageInfo.class);
    }

}
