package org.jeecg.modules.cpe.device.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 设备信息表
 * @Author: jeecg-boot
 * @Date:   2025-01-02
 * @Version: V1.0
 */
@Data
@TableName("cpe_device")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cpe_device对象", description="设备信息表")
public class CpeDevice implements Serializable {
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
	// /**所属部门*/
    // @ApiModelProperty(value = "所属部门")
    // private java.lang.String sysOrgCode;
	/**设备标识*/
	@Excel(name = "设备标识", width = 15)
    @ApiModelProperty(value = "设备标识")
    private java.lang.String deviceSn;
	/**设备状态*/
	@Excel(name = "设备状态", width = 15, dicCode = "cpe_device_status")
	@Dict(dicCode = "cpe_device_status")
    @ApiModelProperty(value = "设备状态")
    private java.lang.String deviceStatusNo;
	/**设备型号*/
	@Excel(name = "设备型号", width = 15, dicCode = "cpe_device_module")
	@Dict(dicCode = "cpe_device_module")
    @ApiModelProperty(value = "设备型号")
    private java.lang.String deviceModuleNo;
	/**设备类型*/
	@Excel(name = "设备类型", width = 15, dicCode = "cpe_device_type")
	@Dict(dicCode = "cpe_device_type")
    @ApiModelProperty(value = "设备类型")
    private java.lang.String deviceTypeNo;
    /**模组型号*/
	@Excel(name = "模组型号", width = 15)
    @ApiModelProperty(value = "模组型号")
    private java.lang.String fiveGModule;
	/**关联卡片*/
	@Excel(name = "关联卡片", width = 15, dictTable = "card_info", dicText = "card_no", dicCode = "id")
	@Dict(dictTable = "card_info", dicText = "card_no", dicCode = "id")
    @ApiModelProperty(value = "关联卡片")
    private java.lang.String cardNo;
	/**在线卡片*/
	@Excel(name = "在线卡片", width = 15, dictTable = "card_info", dicText = "card_no", dicCode = "id")
	@Dict(dictTable = "card_info", dicText = "card_no", dicCode = "id")
    @ApiModelProperty(value = "在线卡片")
    private java.lang.String onlineCardNo;
	/**在线网络*/
	@Excel(name = "在线网络", width = 15, dicCode = "cpe_network")
	@Dict(dicCode = "cpe_network")
    @ApiModelProperty(value = "在线网络")
    private java.lang.String onlineNetNo;
	/**在线频段*/
	@Excel(name = "在线频段", width = 15)
    @ApiModelProperty(value = "在线频段")
    private java.lang.String onlineBand;
	/**所属客户*/
	@Excel(name = "所属客户", width = 15, dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    @ApiModelProperty(value = "所属客户")
    private java.lang.String sysOrgCode;
	/**安装位置*/
	@Excel(name = "安装位置", width = 15)
    @ApiModelProperty(value = "安装位置")
    private java.lang.String position;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String memo;
}
