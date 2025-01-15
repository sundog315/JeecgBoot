package org.jeecg.modules.cpe.scripts.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
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
 * @Description: 设备脚本管理
 * @Author: jeecg-boot
 * @Date:   2025-01-14
 * @Version: V1.0
 */
@Data
@TableName("cpe_scripts")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="cpe_scripts对象", description="设备脚本管理")
public class CpeScripts implements Serializable {
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
	/**生效标志*/
	@Excel(name = "生效标志", width = 15, dicCode = "enable_flag")
	@Dict(dicCode = "enable_flag")
    @ApiModelProperty(value = "生效标志")
    private java.lang.String enableFlag;
	/**设备型号*/
	@Excel(name = "设备型号", width = 15, dicCode = "cpe_device_module")
	@Dict(dicCode = "cpe_device_module")
    @ApiModelProperty(value = "设备型号")
    private java.lang.String deviceModuleNo;
	/**脚本名称*/
	@Excel(name = "脚本名称", width = 15)
    @ApiModelProperty(value = "脚本名称")
    private java.lang.String scriptName;
	/**脚本存放路径*/
	@Excel(name = "脚本存放路径", width = 15)
    @ApiModelProperty(value = "脚本存放路径")
    private java.lang.String scriptPath;
	/**当前版本*/
	@Excel(name = "当前版本", width = 15)
    @ApiModelProperty(value = "当前版本")
    private java.lang.String version;
	/**脚本内容*/
	@Excel(name = "脚本内容", width = 15)
    @ApiModelProperty(value = "脚本内容")
    private java.lang.String content;
}
