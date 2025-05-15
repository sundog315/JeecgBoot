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
 * @Description: 连接终端
 * @Author: jeecg-boot
 * @Date:   2025-05-15
 * @Version: V1.0
 */
@Data
@TableName("cpe_device_client")
@ApiModel(value="cpe_device_client对象", description="连接终端")
public class CpeDeviceClient implements Serializable {
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
	/**客户端IP*/
	@Excel(name = "客户端IP", width = 15)
    @ApiModelProperty(value = "客户端IP")
    private java.lang.String clientIp;
	/**客户端MAC*/
	@Excel(name = "客户端MAC", width = 15)
    @ApiModelProperty(value = "客户端MAC")
    private java.lang.String clientMac;
	/**上线时间*/
	@Excel(name = "上线时间", width = 15)
    @ApiModelProperty(value = "上线时间")
    private java.lang.String attachTs;
	/**刷新时间*/
	@Excel(name = "刷新时间", width = 15)
    @ApiModelProperty(value = "刷新时间")
    private java.lang.String refreshTs;
	/**连接时长*/
	@Excel(name = "连接时长", width = 15)
    @ApiModelProperty(value = "连接时长")
    private java.lang.Integer conntedDuration;
	/**预留1*/
	@Excel(name = "预留1", width = 15)
    @ApiModelProperty(value = "预留1")
    private java.lang.String col1;
	/**上行流量*/
	@Excel(name = "上行流量", width = 15)
    @ApiModelProperty(value = "上行流量")
    private java.lang.Integer upBytes;
	/**预留2*/
	@Excel(name = "预留2", width = 15)
    @ApiModelProperty(value = "预留2")
    private java.lang.String col2;
	/**下行流量*/
	@Excel(name = "下行流量", width = 15)
    @ApiModelProperty(value = "下行流量")
    private java.lang.Integer downBytes;
}
