package org.jeecg.modules.cpe.card.controller;

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
import java.util.HashMap;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.cpe.card.entity.CardPackageRel;
import org.jeecg.modules.cpe.card.entity.CardInfo;
import org.jeecg.modules.cpe.card.service.ICardInfoService;
import org.jeecg.modules.cpe.card.service.ICardPackageRelService;
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
 * @Description: 卡片信息表
 * @Author: jeecg-boot
 * @Date:   2025-02-28
 * @Version: V1.0
 */
@Api(tags="卡片信息表")
@RestController
@RequestMapping("/cpe/card/cardInfo")
@Slf4j
public class CardInfoController extends JeecgController<CardInfo, ICardInfoService> {

	@Autowired
	private ICardInfoService cardInfoService;

	@Autowired
	private ICardPackageRelService cardPackageRelService;


	/*---------------------------------主表处理-begin-------------------------------------*/

	/**
	 * 分页列表查询
	 * @param cardInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "卡片信息表-分页列表查询")
	@ApiOperation(value="卡片信息表-分页列表查询", notes="卡片信息表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CardInfo>> queryPageList(CardInfo cardInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("netCorps", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<CardInfo> queryWrapper = QueryGenerator.initQueryWrapper(cardInfo, req.getParameterMap(),customeRuleMap);
		Page<CardInfo> page = new Page<CardInfo>(pageNo, pageSize);
		IPage<CardInfo> pageList = cardInfoService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
     *   添加
     * @param cardInfo
     * @return
     */
    @AutoLog(value = "卡片信息表-添加")
    @ApiOperation(value="卡片信息表-添加", notes="卡片信息表-添加")
    @RequiresPermissions("cpe.card:card_info:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody CardInfo cardInfo) {
        cardInfoService.save(cardInfo);
        return Result.OK("添加成功！");
    }

    /**
     *  编辑
     * @param cardInfo
     * @return
     */
    @AutoLog(value = "卡片信息表-编辑")
    @ApiOperation(value="卡片信息表-编辑", notes="卡片信息表-编辑")
    @RequiresPermissions("cpe.card:card_info:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
    public Result<String> edit(@RequestBody CardInfo cardInfo) {
        cardInfoService.updateById(cardInfo);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     * @param id
     * @return
     */
    @AutoLog(value = "卡片信息表-通过id删除")
    @ApiOperation(value="卡片信息表-通过id删除", notes="卡片信息表-通过id删除")
    @RequiresPermissions("cpe.card:card_info:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name="id",required=true) String id) {
        cardInfoService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @AutoLog(value = "卡片信息表-批量删除")
    @ApiOperation(value="卡片信息表-批量删除", notes="卡片信息表-批量删除")
    @RequiresPermissions("cpe.card:card_info:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        this.cardInfoService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 导出
     * @return
     */
    @RequiresPermissions("cpe.card:card_info:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CardInfo cardInfo) {
        return super.exportXls(request, cardInfo, CardInfo.class, "卡片信息表");
    }

    /**
     * 导入
     * @return
     */
    @RequiresPermissions("cpe.card:card_info:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CardInfo.class);
    }
	/*---------------------------------主表处理-end-------------------------------------*/
	

    /*--------------------------------子表处理-卡片套餐-begin----------------------------------------------*/
	/**
	 * 通过主表ID查询
	 * @return
	 */
	//@AutoLog(value = "卡片套餐-通过主表ID查询")
	@ApiOperation(value="卡片套餐-通过主表ID查询", notes="卡片套餐-通过主表ID查询")
	@GetMapping(value = "/listCardPackageRelByMainId")
    public Result<IPage<CardPackageRel>> listCardPackageRelByMainId(CardPackageRel cardPackageRel,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                    HttpServletRequest req) {
        QueryWrapper<CardPackageRel> queryWrapper = QueryGenerator.initQueryWrapper(cardPackageRel, req.getParameterMap());
        Page<CardPackageRel> page = new Page<CardPackageRel>(pageNo, pageSize);
        IPage<CardPackageRel> pageList = cardPackageRelService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

	/**
	 * 添加
	 * @param cardPackageRel
	 * @return
	 */
	@AutoLog(value = "卡片套餐-添加")
	@ApiOperation(value="卡片套餐-添加", notes="卡片套餐-添加")
	@PostMapping(value = "/addCardPackageRel")
	public Result<String> addCardPackageRel(@RequestBody CardPackageRel cardPackageRel) {
		cardPackageRelService.save(cardPackageRel);
		return Result.OK("添加成功！");
	}

    /**
	 * 编辑
	 * @param cardPackageRel
	 * @return
	 */
	@AutoLog(value = "卡片套餐-编辑")
	@ApiOperation(value="卡片套餐-编辑", notes="卡片套餐-编辑")
	@RequestMapping(value = "/editCardPackageRel", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> editCardPackageRel(@RequestBody CardPackageRel cardPackageRel) {
		cardPackageRelService.updateById(cardPackageRel);
		return Result.OK("编辑成功!");
	}

	/**
	 * 通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "卡片套餐-通过id删除")
	@ApiOperation(value="卡片套餐-通过id删除", notes="卡片套餐-通过id删除")
	@DeleteMapping(value = "/deleteCardPackageRel")
	public Result<String> deleteCardPackageRel(@RequestParam(name="id",required=true) String id) {
		cardPackageRelService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "卡片套餐-批量删除")
	@ApiOperation(value="卡片套餐-批量删除", notes="卡片套餐-批量删除")
	@DeleteMapping(value = "/deleteBatchCardPackageRel")
	public Result<String> deleteBatchCardPackageRel(@RequestParam(name="ids",required=true) String ids) {
	    this.cardPackageRelService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

    /**
     * 导出
     * @return
     */
    @RequestMapping(value = "/exportCardPackageRel")
    public ModelAndView exportCardPackageRel(HttpServletRequest request, CardPackageRel cardPackageRel) {
		 // Step.1 组装查询条件
		 QueryWrapper<CardPackageRel> queryWrapper = QueryGenerator.initQueryWrapper(cardPackageRel, request.getParameterMap());
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		 // Step.2 获取导出数据
		 List<CardPackageRel> pageList = cardPackageRelService.list(queryWrapper);
		 List<CardPackageRel> exportList = null;

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
		 mv.addObject(NormalExcelConstants.FILE_NAME, "卡片套餐");
		 mv.addObject(NormalExcelConstants.CLASS, CardPackageRel.class);
		 mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("卡片套餐报表", "导出人:" + sysUser.getRealname(), "卡片套餐"));
		 mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
		 return mv;
    }

    /**
     * 导入
     * @return
     */
    @RequestMapping(value = "/importCardPackageRel/{mainId}")
    public Result<?> importCardPackageRel(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
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
				 List<CardPackageRel> list = ExcelImportUtil.importExcel(file.getInputStream(), CardPackageRel.class, params);
				 for (CardPackageRel temp : list) {
                    temp.setCardId(mainId);
				 }
				 long start = System.currentTimeMillis();
				 cardPackageRelService.saveBatch(list);
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

    /*--------------------------------子表处理-卡片套餐-end----------------------------------------------*/




}
