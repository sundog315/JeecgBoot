import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { defHttp } from '/@/utils/http/axios';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '启用',
    align: 'center',
    dataIndex: 'enable_dictText',
  },
  {
    title: '被告警人',
    align: 'center',
    dataIndex: 'userId_dictText',
  },
  {
    title: '策略名称',
    align: 'center',
    dataIndex: 'alarmName',
  },
  {
    title: '告警主体',
    align: 'center',
    dataIndex: 'alarmTarget_dictText',
  },
  {
    title: '告警策略',
    align: 'center',
    dataIndex: 'alarmTemplateId_dictText',
  },
  {
    title: '接收方式',
    align: 'center',
    dataIndex: 'receiveType_dictText',
  },
];
//查询数据
export const searchFormSchema: FormSchema[] = [];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '启用',
    field: 'enable',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'enable_flag',
      type: 'radio',
    },
  },
  {
    label: '被告警人',
    field: 'userId',
    component: 'JSelectUser',
    componentProps: {
      labelKey: 'realname',
      rowKey: 'id',
      showSearch: true,
      placeholder: '请选择被告警人',
    },
  },
  {
    label: '策略名称',
    field: 'alarmName',
    component: 'Input',
  },
  {
    label: '告警主体',
    field: 'alarmTarget',
    component: 'ApiSelect',
    componentProps: {
      api: () => {
        return defHttp.get({
          url: '/cpe/device/cpeDevice/list',
          params: {
            pageNo: 1,
            pageSize: 999,
            column: 'createTime',
            order: 'desc',
          },
        });
      },
      resultField: 'records',
      labelField: 'deviceSn',
      valueField: 'id',
      mode: 'multiple',
      placeholder: '请选择告警主体设备',
    },
  },
  {
    label: '告警策略',
    field: 'alarmTemplateId',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'sys_sms_template,template_name,id',
    },
  },
  {
    label: '接收方式',
    field: 'receiveType',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'receive_type',
    },
  },
  // TODO 主键隐藏字段，目前写死为ID
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
];

// 高级查询数据
export const superQuerySchema = {
  enable: { title: '启用', order: 0, view: 'radio', type: 'string', dictCode: 'enable_flag' },
  userId: { title: '被告警人', order: 1, view: 'select-user', type: 'string', orgFields: 'current' },
  alarmName: { title: '策略名称', order: 2, view: 'text', type: 'string' },
  alarmTarget: { title: '告警主体', order: 3, view: 'list', type: 'string', dictTable: 'cpe_device', dictCode: 'id', dictText: 'device_sn' },
  alarmTemplateId: {
    title: '告警策略',
    order: 4,
    view: 'list',
    type: 'string',
    dictTable: 'sys_sms_template',
    dictCode: 'id',
    dictText: 'template_name',
  },
  receiveType: { title: '接收方式', order: 5, view: 'list', type: 'string', dictCode: 'receive_type' },
};

/**
 * 流程表单调用这个方法获取formSchema
 * @param param
 */
export function getBpmFormSchema(_formData): FormSchema[] {
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
