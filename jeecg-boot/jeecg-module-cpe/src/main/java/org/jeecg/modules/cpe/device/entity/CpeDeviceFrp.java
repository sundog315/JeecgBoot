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
 * @Description: 设备远程控制
 * @Author: jeecg-boot
 * @Date:   2025-01-04
 * @Version: V1.0
 */
@Data
@TableName("cpe_device_frp")
@ApiModel(value="cpe_device_frp对象", description="设备远程控制")
public class CpeDeviceFrp implements Serializable {
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
	/**CPEID*/
    @ApiModelProperty(value = "CPEID")
    private java.lang.String cpeId;
	/**服务器地址*/
	@Excel(name = "服务器地址", width = 15)
    @ApiModelProperty(value = "服务器地址")
    private java.lang.String serverAddr;
	/**服务器端口*/
	@Excel(name = "服务器端口", width = 15)
    @ApiModelProperty(value = "服务器端口")
    private java.lang.Integer serverPort;
	/**令牌*/
	@Excel(name = "令牌", width = 15)
    @ApiModelProperty(value = "令牌")
    private java.lang.String token;
	/**SSH映射端口*/
	@Excel(name = "SSH映射端口", width = 15)
    @ApiModelProperty(value = "SSH映射端口")
    private java.lang.Integer proxySshRemotePort;
	/**HTTP映射端口*/
	@Excel(name = "HTTP映射端口", width = 15)
    @ApiModelProperty(value = "HTTP映射端口")
    private java.lang.Integer proxyHttpRemotePort;
}
