package org.jeecg.modules.cpe.scripts.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.modules.cpe.scripts.entity.CpeScripts;
import org.jeecg.modules.cpe.scripts.service.ICpeScriptsService;

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
 * @Description: 设备脚本管理
 * @Author: jeecg-boot
 * @Date:   2025-01-14
 * @Version: V1.0
 */
@Api(tags="设备脚本管理")
@RestController
@RequestMapping("/cpe/scripts/cpeScripts")
@Slf4j
public class CpeScriptsController extends JeecgController<CpeScripts, ICpeScriptsService> {
	@Autowired
	private ICpeScriptsService cpeScriptsService;
	
	/**
	 * 分页列表查询
	 *
	 * @param cpeScripts
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "设备脚本管理-分页列表查询")
	@ApiOperation(value="设备脚本管理-分页列表查询", notes="设备脚本管理-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CpeScripts>> queryPageList(CpeScripts cpeScripts,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("enableFlag", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("deviceModuleNo", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<CpeScripts> queryWrapper = QueryGenerator.initQueryWrapper(cpeScripts, req.getParameterMap(),customeRuleMap);
		Page<CpeScripts> page = new Page<CpeScripts>(pageNo, pageSize);
		IPage<CpeScripts> pageList = cpeScriptsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param cpeScripts
	 * @return
	 */
	@AutoLog(value = "设备脚本管理-添加")
	@ApiOperation(value="设备脚本管理-添加", notes="设备脚本管理-添加")
	//@RequiresPermissions("cpe.scripts:cpe_scripts:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CpeScripts cpeScripts) {
		cpeScriptsService.save(cpeScripts);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param cpeScripts
	 * @return
	 */
	@AutoLog(value = "设备脚本管理-编辑")
	@ApiOperation(value="设备脚本管理-编辑", notes="设备脚本管理-编辑")
	//@RequiresPermissions("cpe.scripts:cpe_scripts:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CpeScripts cpeScripts) {
		cpeScriptsService.updateById(cpeScripts);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "设备脚本管理-通过id删除")
	@ApiOperation(value="设备脚本管理-通过id删除", notes="设备脚本管理-通过id删除")
	//@RequiresPermissions("cpe.scripts:cpe_scripts:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		cpeScriptsService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "设备脚本管理-批量删除")
	@ApiOperation(value="设备脚本管理-批量删除", notes="设备脚本管理-批量删除")
	//@RequiresPermissions("cpe.scripts:cpe_scripts:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.cpeScriptsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "设备脚本管理-通过id查询")
	@ApiOperation(value="设备脚本管理-通过id查询", notes="设备脚本管理-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CpeScripts> queryById(@RequestParam(name="id",required=true) String id) {
		CpeScripts cpeScripts = cpeScriptsService.getById(id);
		if(cpeScripts==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(cpeScripts);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param cpeScripts
    */
    @RequiresPermissions("cpe.scripts:cpe_scripts:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CpeScripts cpeScripts) {
        return super.exportXls(request, cpeScripts, CpeScripts.class, "设备脚本管理");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("cpe.scripts:cpe_scripts:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CpeScripts.class);
    }

	@GetMapping("/listEnabledScripts")
	public Result<List<Map<String, Object>>> listEnabledScripts(@RequestParam String deviceModule) {
		// 1. 构建查询条件
		QueryWrapper<CpeScripts> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("enable_flag", "1")
				   .like("device_module_no", deviceModule)
				   .select("script_name", "script_path", "version"); 
			
		// 2. 获取所有生效的脚本记录
		List<CpeScripts> scripts = cpeScriptsService.list(queryWrapper);
			
		// 3. 转换为前端所需格式
		List<Map<String, Object>> result = scripts.stream().map(script -> {
			Map<String, Object> map = new HashMap<>();
			// 组合完整的脚本路径
			String fullPath = script.getScriptPath() + "/" + script.getScriptName();
			map.put("fullPath", fullPath);
			map.put("version", script.getVersion());
			map.put("content", script.getContent());
			return map;
		}).collect(Collectors.toList());
		
		return Result.OK(result);
	}
	
	@GetMapping("/getScriptContent")
	public Result<String> getScriptContent(@RequestParam String scriptPath) {
		// 拆分完整路径为目录路径和文件名
		String scriptName = scriptPath.substring(scriptPath.lastIndexOf('/') + 1);
		String path = scriptPath.substring(0, scriptPath.lastIndexOf('/'));
		
		// 构建查询条件
		QueryWrapper<CpeScripts> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("script_path", path)
				.eq("script_name", scriptName)
				.eq("enable_flag", "1")
				.select("content");
			
		// 获取脚本内容
		CpeScripts script = cpeScriptsService.getOne(queryWrapper);
			
		if (script == null || script.getContent() == null) {
			return Result.error("脚本不存在或内容为空");
		}

		// 对脚本内容进行Base64编码
		String encodedContent = java.util.Base64.getEncoder().encodeToString(script.getContent().getBytes());
			
		return Result.OK(encodedContent);
	}
}
