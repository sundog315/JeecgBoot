import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '卡号',
    align:"center",
    dataIndex: 'cardNo'
  },
  {
    title: '短号',
    align:"center",
    dataIndex: 'shortNo'
  },
  {
    title: '接入号',
    align:"center",
    dataIndex: 'joinNo'
  },
  {
    title: '卡片运营商',
    align:"center",
    dataIndex: 'netCorps_dictText'
  },
  {
    title: '是否实名',
    align:"center",
    dataIndex: 'named_dictText'
  },
  {
    title: '所属客户',
    align:"center",
    dataIndex: 'sysOrgCode_dictText'
  },
  {
    title: '实名人',
    align:"center",
    dataIndex: 'namedPerson'
  },
  {
    title: '本周期上传量',
    align:"center",
    dataIndex: 'upBytes',
    customRender: ({ text }) => {
      return formatTraffic(text);
    },
  },
  {
    title: '本周期下载量',
    align:"center",
    dataIndex: 'downBytes',
    customRender: ({ text }) => {
      return formatTraffic(text);
    },
  },
];

//子表列表数据
export const cardPackageRelColumns: BasicColumn[] = [
  {
    title: '卡片',
    align:"center",
    dataIndex: 'cardId_dictText'
  },
  {
    title: '套餐',
    align:"center",
    dataIndex: 'packageId_dictText'
  },
  {
    title: '开始时间',
    align:"center",
    dataIndex: 'startTime',
    customRender:({text}) =>{
      return !text?"":(text.length>10?text.substr(0,10):text)
    },
  },
  {
    title: '结束时间',
    align:"center",
    dataIndex: 'endTime',
    customRender:({text}) =>{
      return !text?"":(text.length>10?text.substr(0,10):text)
    },
  },
  {
    title: '状态',
    align:"center",
    dataIndex: 'status_dictText'
  },
];

// 高级查询数据
export const superQuerySchema = {
  cardNo: {title: '卡号',order: 0,view: 'text', type: 'string',},
  shortNo: {title: '短号',order: 1,view: 'text', type: 'string',},
  joinNo: {title: '接入号',order: 2,view: 'text', type: 'string',},
  netCorps: {title: '卡片运营商',order: 3,view: 'list', type: 'string',dictCode: 'cpe_network',},
  named: {title: '是否实名',order: 4,view: 'number', type: 'number',dictCode: 'card_isnamed',},
  namedPerson: {title: '实名人',order: 5,view: 'text', type: 'string',},
  upBytes: {title: '本周期上传量',order: 6,view: 'number', type: 'number',},
  downBytes: {title: '本周期下载量',order: 7,view: 'number', type: 'number',},
};

/**
 * 格式化流量数据
 * @param bytes 字节数
 * @returns 格式化后的字符串
 */
export const formatTraffic = (bytes: number): string => {
  const KB = 1024;
  const MB = KB * 1024;
  const GB = MB * 1024;
  const TB = GB * 1024;

  if (!bytes && bytes !== 0) {
    return '0 B';
  }

  if (bytes >= TB) {
    return `${(bytes / TB).toFixed(2)} TB`;
  } else if (bytes >= GB) {
    return `${(bytes / GB).toFixed(2)} GB`;
  } else if (bytes >= MB) {
    return `${(bytes / MB).toFixed(2)} MB`;
  } else if (bytes >= KB) {
    return `${(bytes / KB).toFixed(2)} KB`;
  } else {
    return `${bytes.toFixed(0)} B`;
  }
};
