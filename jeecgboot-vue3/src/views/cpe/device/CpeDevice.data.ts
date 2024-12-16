import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
import { h } from 'vue';
import { Tag } from 'ant-design-vue';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '设备标识',
    align: "center",
    dataIndex: 'deviceSn'
  },
  {
    title: '设备状态',
    align: "center",
    dataIndex: 'deviceStatusNo_dictText',
    customRender: ({ text }) => {
      const color = text === '在线' ? 'success' : 'error';
      return h(Tag, { color: color }, () => text);
    },
  },
  {
    title: '设备型号',
    align: "center",
    dataIndex: 'deviceModuleNo_dictText'
  },
  {
    title: '设备类型',
    align: "center",
    dataIndex: 'deviceTypeNo_dictText'
  },
  {
    title: '关联卡片',
    align: "center",
    width: 180,
    dataIndex: 'cardNo_dictText'
  },
  {
    title: '在线卡片',
    align: "center",
    width: 180,
    dataIndex: 'onlineCardNo_dictText'
  },
  {
    title: '在线网络',
    align: "center",
    dataIndex: 'onlineNetNo_dictText'
  },
  {
    title: '在线频段',
    align: "center",
    dataIndex: 'onlineBand'
  },
  {
    title: '所属客户',
    align: "center",
    dataIndex: 'customerName_dictText'
  },
  {
    title: '安装位置',
    align: "center",
    dataIndex: 'position'
  },
  {
    title: '备注',
    align: "center",
    dataIndex: 'memo'
  },
];

// 高级查询数据
export const superQuerySchema = {
  deviceSn: {title: '设备标识',order: 0,view: 'text', type: 'string',},
  deviceStatusNo: {title: '设备状态',order: 1,view: 'list', type: 'string',dictCode: 'cpe_device_status',},
  deviceModuleNo: {title: '设备型号',order: 2,view: 'list', type: 'string',dictCode: 'cpe_device_module',},
  deviceTypeNo: {title: '设备类型',order: 3,view: 'list', type: 'string',dictCode: 'cpe_device_type',},
  cardNo: {title: '关联卡片',order: 4,view: 'list', type: 'string',dictTable: "card_info", dictCode: 'id', dictText: 'card_no',},
  onlineCardNo: {title: '在线卡片',order: 5,view: 'list', type: 'string',dictTable: "card_info", dictCode: 'id', dictText: 'card_no',},
  onlineNetNo: {title: '在线网络',order: 6,view: 'list', type: 'string',dictCode: 'cpe_network',},
  onlineBand: {title: '在线频段',order: 7,view: 'text', type: 'string',},
  customerName: {title: '所属客户',order: 8,view: 'list', type: 'string',dictCode: '',},
  position: {title: '安装位置',order: 9,view: 'text', type: 'string',},
  memo: {title: '备注',order: 10,view: 'text', type: 'string',},
};
