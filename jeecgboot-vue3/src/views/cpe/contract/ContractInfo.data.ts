import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { h } from 'vue';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '合同名称',
    align: 'center',
    dataIndex: 'contractName',
  },
  {
    title: '开始日期',
    align: 'center',
    dataIndex: 'startDate',
    customRender: ({ text }) => {
      text = !text ? '' : text.length > 10 ? text.substr(0, 10) : text;
      return text;
    },
  },
  {
    title: '结束日期',
    align: 'center',
    dataIndex: 'endDate',
    customRender: ({ text }) => {
      text = !text ? '' : text.length > 10 ? text.substr(0, 10) : text;
      return text;
    },
  },
  {
    title: '设备数量',
    align: 'center',
    dataIndex: 'count',
  },
  {
    title: '客户名称',
    align: 'center',
    dataIndex: 'sysOrgCode_dictText',
  },
  {
    title: '合同状态',
    align: 'center',
    dataIndex: 'status_dictText',
  },
];
//查询数据
export const searchFormSchema: FormSchema[] = [];

//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '合同名称',
    field: 'contractName',
    component: 'Input',
    dynamicRules: ({ model, schema }) => {
      return [{ required: true, message: '请输入合同名称!' }];
    },
  },
  {
    label: '开始日期',
    field: 'startDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD',
    },
    dynamicRules: ({ model, schema }) => {
      return [{ required: true, message: '请输入开始日期!' }];
    },
  },
  {
    label: '结束日期',
    field: 'endDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD',
    },
    dynamicRules: ({ model, schema }) => {
      return [{ required: true, message: '请输入结束日期!' }];
    },
  },
  {
    label: '设备数量',
    field: 'count',
    component: 'InputNumber',
  },
  {
    label: '客户',
    field: 'sysOrgCode',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'sys_depart,depart_name,org_code',
    },
    dynamicRules: ({ model, schema }) => {
      return [{ required: true, message: '请选择客户!' }];
    },
  },
  {
    label: '合同状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'contract_status',
    },
    dynamicRules: ({ model, schema }) => {
      return [{ required: true, message: '请选择合同状态!' }];
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

//子表列表数据
export const contractDeviceColumns: BasicColumn[] = [
  {
    title: '套餐结束时间',
    align: 'center',
    dataIndex: 'endTime',
    customRender: ({ text, record }) => {
      if (!text) return '';

      // 格式化日期显示
      const formattedText = text.length > 10 ? text.substr(0, 10) : text;

      try {
        // 直接获取日期对象
        const endTime = new Date(text);
        const mainEndDate = record.contractInfo?.endDate ? new Date(record.contractInfo.endDate) : null;

        // 确保日期有效
        if (!mainEndDate || isNaN(mainEndDate.getTime()) || isNaN(endTime.getTime())) {
          return formattedText;
        }

        // 直接比较日期 - 简化逻辑
        // 如果套餐结束时间早于合同结束日期，标红
        if (endTime < mainEndDate) {
          return h(
            'span',
            {
              style: {
                color: 'red',
                fontWeight: 'bold',
                backgroundColor: '#ffeeee', // 添加背景色增强视觉效果
              },
            },
            formattedText
          );
        }

        // 计算天数差
        const diffTime = endTime.getTime() - mainEndDate.getTime();
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays <= 3) {
          // 相差不足3天，标红
          return h(
            'span',
            {
              style: {
                color: 'red',
                fontWeight: 'bold',
                backgroundColor: '#ffeeee', // 添加背景色增强视觉效果
              },
            },
            formattedText
          );
        } else if (diffDays <= 30) {
          // 相差不足30天，标黄
          return h(
            'span',
            {
              style: {
                color: 'orange',
                fontWeight: 'bold',
                backgroundColor: '#fffbee', // 添加背景色增强视觉效果
              },
            },
            formattedText
          );
        }
      } catch (error) {
        // 出现任何错误，返回原始文本
        return formattedText;
      }

      // 默认返回原始文本
      return formattedText;
    },
  },
  {
    title: '设备标识',
    align: 'center',
    dataIndex: 'deviceSn',
  },
  {
    title: '设备型号',
    align: 'center',
    dataIndex: 'deviceModuleNo_dictText',
  },
  {
    title: '设备类型',
    align: 'center',
    dataIndex: 'deviceTypeNo_dictText',
  },
  {
    title: '关联卡片',
    align: 'center',
    width: 180,
    dataIndex: 'cardNo_dictText',
  },
  {
    title: '在线卡片',
    align: 'center',
    width: 180,
    dataIndex: 'onlineCardNo_dictText',
  },
  {
    title: '在线网络',
    align: 'center',
    dataIndex: 'onlineNetNo_dictText',
  },
  {
    title: '在线频段',
    align: 'center',
    dataIndex: 'onlineBand',
  },
  {
    title: '所属客户',
    align: 'center',
    dataIndex: 'sysOrgCode_dictText',
  },
  {
    title: '安装位置',
    align: 'center',
    dataIndex: 'position',
  },
  {
    title: '备注',
    align: 'center',
    dataIndex: 'memo',
  },
  {
    title: '模组型号',
    align: 'center',
    dataIndex: 'fiveGModule',
  },
];
//子表表单数据
export const contractDeviceFormSchema: FormSchema[] = [
  // TODO 子表隐藏字段，目前写死为ID
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
  {
    label: '合同ID',
    field: 'contractId',
    component: 'Input',
    defaultValue: '${mainId}',
    show: false,
  },
  {
    label: '设备ID',
    field: 'cpeId',
    component: 'JSelectMultiple',
    componentProps: {
      dictCode: 'cpe_device,device_sn,id',
      placeholder: '请选择设备',
    },
    dynamicRules: ({ model, schema }) => {
      return [{ required: true, message: '请选择设备ID!' }];
    },
  },
];

// 高级查询数据
export const superQuerySchema = {
  contractName: { title: '合同名称', order: 0, view: 'text', type: 'string' },
  startDate: { title: '开始日期', order: 1, view: 'date', type: 'string' },
  endDate: { title: '结束日期', order: 2, view: 'date', type: 'string' },
  count: { title: '设备数量', order: 3, view: 'number', type: 'number' },
  customerName: { title: '所属客户', order: 8, view: 'list', type: 'string', dictTable: 'sys_depart', dictCode: 'org_code', dictText: 'depart_name' },
  status: { title: '合同状态', order: 5, view: 'text', type: 'string', dictCode: 'contract_status' },
};
