package org.jeecg.modules.cpe.device.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 设备内网配置
 * @Author: jeecg-boot
 * @Date:   2025-01-12
 * @Version: V1.0
 */
@Data
@TableName("cpe_device_network")
@ApiModel(value="cpe_device_network对象", description="设备内网配置")
public class CpeDeviceNetwork implements Serializable {
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
	/**内网地址*/
	@Excel(name = "内网地址", width = 15)
    @ApiModelProperty(value = "内网地址")
    private java.lang.String ipaddr;
	/**子网掩码*/
	@Excel(name = "子网掩码", width = 15)
    @ApiModelProperty(value = "子网掩码")
    private java.lang.String netmask;
	/**DHCP起始地址*/
	@Excel(name = "DHCP起始地址", width = 15)
    @ApiModelProperty(value = "DHCP起始地址")
    private java.lang.String dhcpStart;
	/**DHCP截至地址*/
	@Excel(name = "DHCP截至地址", width = 15)
    @ApiModelProperty(value = "DHCP截至地址")
    private java.lang.String dhcpEnd;
	/**DHCP租期*/
	@Excel(name = "DHCP租期", width = 15)
    @ApiModelProperty(value = "DHCP租期")
    private java.lang.String dhcpLeasetime;
}
