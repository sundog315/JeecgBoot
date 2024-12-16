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
 * @Description: 操作记录表
 * @Author: jeecg-boot
 * @Date:   2024-12-30
 * @Version: V1.0
 */
@Data
@TableName("cpe_oper_log")
@ApiModel(value="cpe_oper_log对象", description="操作记录表")
public class CpeOperLog implements Serializable {
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
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTs;
	/**设备主表ID*/
    @ApiModelProperty(value = "设备主表ID")
    private java.lang.String cpeId;
	/**操作类型*/
	@Excel(name = "操作类型", width = 15)
    @ApiModelProperty(value = "操作类型")
    private java.lang.String operType;
	/**操作参数*/
	@Excel(name = "操作参数", width = 15)
    @ApiModelProperty(value = "操作参数")
    private java.lang.String operParam;
	/**执行状态*/
	@Excel(name = "执行状态", width = 15)
    @ApiModelProperty(value = "执行状态")
    private java.lang.String operRetcode;
	/**操作时间*/
	@Excel(name = "操作时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "操作时间")
    private java.util.Date operTs;
	/**操作日志*/
	@Excel(name = "操作日志", width = 15)
    @ApiModelProperty(value = "操作日志")
    private java.lang.String operLog;
}
