package org.jeecg.modules.cpe.card.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 卡片信息表
 * @Author: jeecg-boot
 * @Date:   2025-02-28
 * @Version: V1.0
 */
@Data
@TableName("card_info")
@ApiModel(value="card_info对象", description="卡片信息表")
public class CardInfo implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属客户*/
	@Excel(name = "所属客户", width = 15, dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    @ApiModelProperty(value = "所属客户")
    private java.lang.String sysOrgCode;
	/**卡号*/
    @Excel(name = "卡号", width = 15)
    @ApiModelProperty(value = "卡号")
    private java.lang.String cardNo;
	/**短号*/
    @Excel(name = "短号", width = 15)
    @ApiModelProperty(value = "短号")
    private java.lang.String shortNo;
	/**接入号*/
    @Excel(name = "接入号", width = 15)
    @ApiModelProperty(value = "接入号")
    private java.lang.String joinNo;
	/**卡片运营商*/
    @Excel(name = "卡片运营商", width = 15, dicCode = "cpe_network")
    @Dict(dicCode = "cpe_network")
    @ApiModelProperty(value = "卡片运营商")
    private java.lang.String netCorps;
	/**是否实名*/
    @Excel(name = "是否实名", width = 15, dicCode = "card_isnamed")
    @Dict(dicCode = "card_isnamed")
    @ApiModelProperty(value = "是否实名")
    private java.lang.Integer named;
	/**实名人*/
    @Excel(name = "实名人", width = 15)
    @ApiModelProperty(value = "实名人")
    private java.lang.String namedPerson;
	/**本周期上传量*/
    @Excel(name = "本周期上传量", width = 15)
    @ApiModelProperty(value = "本周期上传量")
    private java.lang.Double upBytes;
	/**本周期下载量*/
    @Excel(name = "本周期下载量", width = 15)
    @ApiModelProperty(value = "本周期下载量")
    private java.lang.Double downBytes;
	/**期初上传量*/
    @Excel(name = "期初上传量", width = 15)
    @ApiModelProperty(value = "期初上传量")
    private java.lang.Double beginUpBytes;
	/**期初下载量*/
    @Excel(name = "期初下载量", width = 15)
    @ApiModelProperty(value = "期初下载量")
    private java.lang.Double beginDownBytes;
}
