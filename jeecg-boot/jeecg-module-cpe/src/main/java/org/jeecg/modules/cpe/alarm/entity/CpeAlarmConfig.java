package org.jeecg.modules.cpe.alarm.entity;

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
 * @Description: 告警策略
 * @Author: jeecg-boot
 * @Date:   2025-03-12
 * @Version: V1.0
 */
@Data
@TableName("cpe_alarm_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cpe_alarm_config对象", description="告警策略")
public class CpeAlarmConfig implements Serializable {
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
	/**启用*/
	@Excel(name = "启用", width = 15, dicCode = "enable_flag")
	@Dict(dicCode = "enable_flag")
    @ApiModelProperty(value = "启用")
    private java.lang.String enable;
	/**被告警人*/
	@Excel(name = "被告警人", width = 15, dictTable = "sys_user where org_code like '#{sys_org_code}%'", dicText = "realname", dicCode = "id")
	@Dict(dictTable = "sys_user where org_code like '#{sys_org_code}%'", dicText = "realname", dicCode = "id")
    @ApiModelProperty(value = "被告警人")
    private java.lang.String userId;
	/**策略名称*/
	@Excel(name = "策略名称", width = 15)
    @ApiModelProperty(value = "策略名称")
    private java.lang.String alarmName;
	/**告警主体*/
	@Excel(name = "告警主体", width = 15, dictTable = "cpe_device where sys_org_code like '#{sys_org_code}%'", dicText = "device_sn", dicCode = "id")
	@Dict(dictTable = "cpe_device where sys_org_code like '#{sys_org_code}%'", dicText = "device_sn", dicCode = "id")
    @ApiModelProperty(value = "告警主体")
    private java.lang.String alarmTarget;
	/**告警策略*/
	@Excel(name = "告警策略", width = 15, dictTable = "sys_sms_template", dicText = "template_name", dicCode = "id")
	@Dict(dictTable = "sys_sms_template", dicText = "template_name", dicCode = "id")
    @ApiModelProperty(value = "告警策略")
    private java.lang.String alarmTemplateId;
	/**接收方式*/
	@Excel(name = "接收方式", width = 15, dicCode = "receive_type")
	@Dict(dicCode = "receive_type")
    @ApiModelProperty(value = "接收方式")
    private java.lang.String receiveType;
}
