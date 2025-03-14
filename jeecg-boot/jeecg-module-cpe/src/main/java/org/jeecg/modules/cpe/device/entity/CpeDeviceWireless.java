package org.jeecg.modules.cpe.device.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 设备无线网络配置
 * @Author: jeecg-boot
 * @Date:   2025-01-20
 * @Version: V1.0
 */
@Data
@TableName("cpe_device_wireless")
@ApiModel(value="cpe_device_wireless对象", description="设备无线网络配置")
public class CpeDeviceWireless implements Serializable {
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
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**设备ID*/
    @ApiModelProperty(value = "设备ID")
    private java.lang.String cpeId;
	/**2.4G启用状态*/
	@Excel(name = "2.4G启用状态", width = 15, dicCode = "wireless_disable")
    @ApiModelProperty(value = "2.4G启用状态")
    @Dict(dicCode = "wireless_disable")
    private java.lang.String radio24Disabled;
	/**5G启用状态*/
	@Excel(name = "5G启用状态", width = 15, dicCode = "wireless_disable")
    @ApiModelProperty(value = "5G启用状态")
    @Dict(dicCode = "wireless_disable")
    private java.lang.String radio5Disabled;
	/**2.4G信道*/
	@Excel(name = "2.4G信道", width = 15, dicCode = "24g_channel")
    @ApiModelProperty(value = "2.4G信道")
    @Dict(dicCode = "24g_channel")
    private java.lang.String radio24Channel;
	/**5G信道*/
	@Excel(name = "5G信道", width = 15, dicCode = "5g_channel")
    @ApiModelProperty(value = "5G信道")
    @Dict(dicCode = "5g_channel")
    private java.lang.String radio5Channel;
	/**2.4G SSID*/
	@Excel(name = "2.4G SSID", width = 15)
    @ApiModelProperty(value = "2.4G SSID")
    private java.lang.String radio24Ssid;
	/**5G SSID*/
	@Excel(name = "5G SSID", width = 15)
    @ApiModelProperty(value = "5G SSID")
    private java.lang.String radio5Ssid;
	/**2.4G加密方式*/
	@Excel(name = "2.4G加密方式", width = 15, dicCode = "wireless_encryption")
    @ApiModelProperty(value = "2.4G加密方式")
    @Dict(dicCode = "wireless_encryption")
    private java.lang.String radio24Encryption;
	/**5G加密方式*/
	@Excel(name = "5G加密方式", width = 15, dicCode = "wireless_encryption")
    @ApiModelProperty(value = "5G加密方式")
    @Dict(dicCode = "wireless_encryption")
    private java.lang.String radio5Encryption;
	/**2.4G密码*/
	@Excel(name = "2.4G密码", width = 15)
    @ApiModelProperty(value = "2.4G密码")
    private java.lang.String radio24Key;
	/**5G密码*/
	@Excel(name = "5G密码", width = 15)
    @ApiModelProperty(value = "5G密码")
    private java.lang.String radio5Key;
	/**2.4G最大终端数*/
	@Excel(name = "2.4G最大终端数", width = 15)
    @ApiModelProperty(value = "2.4G最大终端数")
    private java.lang.Integer radio24MaxSta;
	/**5G最大终端数*/
	@Excel(name = "5G最大终端数", width = 15)
    @ApiModelProperty(value = "5G最大终端数")
    private java.lang.Integer radio5MaxSta;
	/**2.4G功率*/
	@Excel(name = "2.4G功率", width = 15)
    @ApiModelProperty(value = "2.4G功率")
    private java.lang.Integer radio24Power;
	/**5G功率*/
	@Excel(name = "5G功率", width = 15)
    @ApiModelProperty(value = "5G功率")
    private java.lang.Integer radio5Power;

    /**2.4GMAC过滤*/
	@Excel(name = "2.4G MAC过滤", width = 15)
    @ApiModelProperty(value = "2.4G MAC过滤")
    private java.lang.String radio24Macfilter;
	/**5GMAC过滤*/
	@Excel(name = "5G MAC过滤", width = 15)
    @ApiModelProperty(value = "5G MAC过滤")
    private java.lang.String radio5Macfilter;
    /**2.4G不广播*/
	@Excel(name = "2.4G不广播", width = 15)
    @ApiModelProperty(value = "2.4G不广播")
    private java.lang.String radio24Hidden;
	/**5G不广播*/
	@Excel(name = "5G不广播", width = 15)
    @ApiModelProperty(value = "5G不广播")
    private java.lang.String radio5Hidden;
}
