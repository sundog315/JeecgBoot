import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
import { h } from 'vue';
import { useGo } from '/@/hooks/web/usePage';
import { Tag } from 'ant-design-vue';

//列表数据
export const columns: BasicColumn[] = [
   {
    title: '生效标志',
    align:"center",
    dataIndex: 'enableFlag_dictText',
    customRender: ({ text }) => {
      const color = text === '生效' ? 'success' : 'error';
      return h(Tag, { color: color }, () => text);
    },
   },
   {
    title: '设备型号',
    align:"center",
    dataIndex: 'deviceModuleNo_dictText'
   },
   {
    title: '脚本名称',
    align:"center",
    dataIndex: 'scriptName'
   },
   {
    title: '脚本存放路径',
    align:"center",
    dataIndex: 'scriptPath'
   },
   {
    title: '当前版本',
    align:"center",
    dataIndex: 'version'
   },
   {
    title: '脚本内容',
    align:"center",
    dataIndex: 'content'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "生效标志",
      field: 'enableFlag',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"enable_flag"
      },
      //colProps: {span: 6},
 	},
	{
      label: "设备型号",
      field: 'deviceModuleNo',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"cpe_device_module"
      },
      //colProps: {span: 6},
 	},
	{
      label: "脚本名称",
      field: 'scriptName',
      component: 'Input',
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '生效标志',
    field: 'enableFlag',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"enable_flag"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入生效标志!'},
          ];
     },
  },
  {
    label: '设备型号',
    field: 'deviceModuleNo',
    component: 'JCheckbox',
    componentProps:{
        dictCode:"cpe_device_module"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入设备型号!'},
          ];
     },
  },
  {
    label: '脚本名称',
    field: 'scriptName',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入脚本名称!'},
          ];
     },
  },
  {
    label: '脚本存放路径',
    field: 'scriptPath',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入脚本存放路径!'},
          ];
     },
  },
  {
    label: '当前版本',
    field: 'version',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入当前版本!'},
          ];
     },
  },
  {
    label: '脚本内容',
    field: 'content',
    component: 'InputTextArea',
    //属性
    componentProps: {
      //可以点击清除图标删除内容
      allowClear: true,
      //是否展示字数
      showCount: true,
      //自适应内容高度，可设置为 true | false 或对象：{ minRows: 2, maxRows: 6 }
      autoSize: {
        //最小显示行数
        minRows: 4,
        //最大显示行数
        maxRows: 10,
      },
    },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入脚本内容!'},
          ];
     },
  },
	// TODO 主键隐藏字段，目前写死为ID
	{
	  label: '',
	  field: 'id',
	  component: 'Input',
	  show: false
	},
];

// 高级查询数据
export const superQuerySchema = {
  enableFlag: {title: '生效标志',order: 0,view: 'list', type: 'string',dictCode: 'enable_flag',},
  deviceModuleNo: {title: '设备型号',order: 1,view: 'checkbox', type: 'string',dictCode: 'cpe_device_module',},
  scriptName: {title: '脚本名称',order: 2,view: 'text', type: 'string',},
  scriptPath: {title: '脚本存放路径',order: 3,view: 'text', type: 'string',},
  version: {title: '当前版本',order: 4,view: 'text', type: 'string',},
  content: {title: '脚本内容',order: 5,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
