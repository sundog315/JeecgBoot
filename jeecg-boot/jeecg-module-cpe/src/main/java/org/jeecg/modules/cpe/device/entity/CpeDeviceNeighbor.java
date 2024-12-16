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
 * @Description: CPE设备邻区信息
 * @Author: jeecg-boot
 * @Date:   2024-12-25
 * @Version: V1.0
 */
@ApiModel(value="cpe_device_neighbor对象", description="CPE设备邻区信息")
@Data
@TableName("cpe_device_neighbor")
public class CpeDeviceNeighbor implements Serializable {
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
	/**cpe设备ID*/
    @ApiModelProperty(value = "cpe设备ID")
    private java.lang.String cpeId;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private java.lang.String status;
	/**RAT*/
	@Excel(name = "RAT", width = 15)
    @ApiModelProperty(value = "RAT")
    private java.lang.String rat;
	/**MCC*/
	@Excel(name = "MCC", width = 15)
    @ApiModelProperty(value = "MCC")
    private java.lang.String mcc;
	/**MNC*/
	@Excel(name = "MNC", width = 15)
    @ApiModelProperty(value = "MNC")
    private java.lang.String mnc;
	/**TAC*/
	@Excel(name = "TAC", width = 15)
    @ApiModelProperty(value = "TAC")
    private java.lang.String tac;
	/**CELLID*/
	@Excel(name = "CELLID", width = 15)
    @ApiModelProperty(value = "CELLID")
    private java.lang.String cellid;
	/**ARFCN*/
	@Excel(name = "ARFCN", width = 15)
    @ApiModelProperty(value = "ARFCN")
    private java.lang.String arfcn;
	/**CELLID*/
	@Excel(name = "CELLID", width = 15)
    @ApiModelProperty(value = "CELLID")
    private java.lang.String physicalcellid;
	/**SINR*/
	@Excel(name = "SINR", width = 15)
    @ApiModelProperty(value = "SINR")
    private java.lang.String sinr;
	/**RXLEV*/
	@Excel(name = "RXLEV", width = 15)
    @ApiModelProperty(value = "RXLEV")
    private java.lang.String rxlev;
	/**RSRP*/
	@Excel(name = "RSRP", width = 15)
    @ApiModelProperty(value = "RSRP")
    private java.lang.String rsrp;
	/**RSRQ*/
	@Excel(name = "RSRQ", width = 15)
    @ApiModelProperty(value = "RSRQ")
    private java.lang.String rsrq;
}
