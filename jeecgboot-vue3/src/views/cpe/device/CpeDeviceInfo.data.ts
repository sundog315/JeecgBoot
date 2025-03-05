import { BasicColumn } from '/@/components/Table';
import { formatTraffic } from '/@/utils/formatter';
import { h } from 'vue';
import { useGo } from '/@/hooks/web/usePage';
import { Tag } from 'ant-design-vue';

//列表数据
export const columns: BasicColumn[] = [
  {
    title: '设备标识',
    align: 'center',
    width: 140,
    dataIndex: 'deviceSn',
  },
  {
    title: '设备状态',
    align: 'center',
    width: 80,
    dataIndex: 'deviceStatusNo_dictText',
    customRender: ({ text }) => {
      const color = text === '在线' ? 'success' : 'error';
      return h(Tag, { color: color }, () => text);
    },
  },
  {
    title: '设备型号',
    align: 'center',
    width: 100,
    dataIndex: 'deviceModuleNo_dictText',
  },
  {
    title: '设备类型',
    align: 'center',
    width: 100,
    dataIndex: 'deviceTypeNo_dictText',
  },
  {
    title: '关联卡片',
    align: 'center',
    width: 200,
    dataIndex: 'cardNo_dictText',
  },
  {
    title: '在线卡片',
    align: 'center',
    width: 200,
    dataIndex: 'onlineCardNo_dictText',
    customRender: ({ text, record }) => {
      const go = useGo();
      const props = {
        style: { color: '#1890ff', cursor: 'pointer' },
        onClick: () => {
          if (record.onlineCardNo) {
            go('/cpe/card/cardInfoList?cardNo=' + record.iccid);
          }
        },
      };
      return h('a', props, text);
    },
  },
  {
    title: '在线网络',
    align: 'center',
    width: 80,
    dataIndex: 'onlineNetNo_dictText',
  },
  {
    title: '频段',
    align: 'center',
    width: 60,
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
    title: '模组型号',
    align: 'center',
    dataIndex: 'fiveGModule',
  },
  {
    title: '5G模块版本',
    align: 'center',
    width: 200,
    dataIndex: 'modemVersion',
  },
  {
    title: 'IMEI',
    align: 'center',
    width: 180,
    dataIndex: 'imei',
  },
  {
    title: 'ICCID',
    align: 'center',
    width: 200,
    dataIndex: 'iccid',
  },
  {
    title: '卡槽',
    align: 'center',
    width: 60,
    dataIndex: 'simSlot',
  },
  {
    title: '状态',
    align: 'center',
    width: 60,
    dataIndex: 'status',
  },
];

//子表列表数据
export const cpeDeviceStatusColumns: BasicColumn[] = [
  {
    title: '时间戳',
    align: 'center',
    width: 170,
    dataIndex: 'ts',
  },
  {
    title: 'RAT',
    align: 'center',
    width: 80,
    dataIndex: 'rat',
  },
  {
    title: '频段',
    align: 'center',
    width: 60,
    dataIndex: 'onlineBand',
  },
  {
    title: '运营商名称',
    align: 'center',
    dataIndex: 'cops',
  },
  {
    title: 'SINR',
    align: 'center',
    width: 80,
    dataIndex: 'sinr',
  },
  {
    title: 'RSRP',
    align: 'center',
    width: 80,
    dataIndex: 'rsrp',
  },
  {
    title: 'RSRQ',
    align: 'center',
    width: 80,
    dataIndex: 'rsrq',
  },
  {
    title: 'CELLID',
    align: 'center',
    width: 100,
    dataIndex: 'cellId',
  },
  {
    title: 'PCID',
    align: 'center',
    width: 60,
    dataIndex: 'pcid',
  },
  {
    title: 'ARFCN',
    align: 'center',
    width: 80,
    dataIndex: 'arfcn',
  },
  {
    title: '信号带宽',
    align: 'center',
    width: 80,
    dataIndex: 'bandwidth',
  },
  {
    title: '连接状态',
    align: 'center',
    width: 80,
    dataIndex: 'linkStatus',
  },
  {
    title: '连接类型',
    align: 'center',
    width: 80,
    dataIndex: 'linkType',
  },
  {
    title: 'MCC',
    align: 'center',
    width: 60,
    dataIndex: 'mcc',
  },
  {
    title: 'MNC',
    align: 'center',
    width: 60,
    dataIndex: 'mnc',
  },
  {
    title: 'IPV4',
    align: 'center',
    dataIndex: 'ipv4',
  },
  {
    title: 'IPV6',
    align: 'center',
    dataIndex: 'ipv6',
  },
  {
    title: 'DNS1',
    align: 'center',
    dataIndex: 'dns1',
  },
  {
    title: 'DNS2',
    align: 'center',
    dataIndex: 'dns2',
  },
  {
    title: '上行流量',
    align: 'center',
    width: 100,
    dataIndex: 'upBytes',
    customRender: ({ text }) => {
      return formatTraffic(text);
    },
  },
  {
    title: '下行流量',
    align: 'center',
    width: 100,
    dataIndex: 'downBytes',
    customRender: ({ text }) => {
      return formatTraffic(text);
    },
  },
  {
    title: '连接时长',
    align: 'center',
    width: 100,
    dataIndex: 'uptime',
    customRender: ({ text }) => {
      return formatDuration(Number(text));
    },
  },
];
//子表列表数据
export const cpeDeviceNeighborColumns: BasicColumn[] = [
  {
    title: '类型',
    align: 'center',
    dataIndex: 'rat',
  },
  {
    title: 'ARFCN',
    align: 'center',
    dataIndex: 'arfcn',
  },
  {
    title: 'PCID',
    align: 'center',
    dataIndex: 'physicalcellid',
  },
  {
    title: 'RSRP',
    align: 'center',
    dataIndex: 'rsrp',
  },
  {
    title: 'RSRQ',
    align: 'center',
    dataIndex: 'rsrq',
  },
  {
    title: 'RXLEV',
    align: 'center',
    dataIndex: 'rxlev',
  },
];
//子表列表数据
export const cpeDeviceFrpColumns: BasicColumn[] = [
  {
    title: '服务器地址',
    align: 'center',
    dataIndex: 'serverAddr',
  },
  {
    title: '服务器端口',
    align: 'center',
    dataIndex: 'serverPort',
  },
  {
    title: '令牌',
    align: 'center',
    dataIndex: 'token',
    customRender: ({ text }) => {
      return h('div', { class: 'password-cell' }, [
        h('span', { class: 'password-text' }, text ? '********' : ''),
        h(
          'span',
          {
            class: 'eye-icon',
            onClick: (e) => {
              e.stopPropagation();
              const textEl = e.currentTarget.previousElementSibling;
              const eyeEl = e.currentTarget;
              if (textEl.textContent === '********') {
                textEl.textContent = text;
                eyeEl.classList.add('visible');
              } else {
                textEl.textContent = '********';
                eyeEl.classList.remove('visible');
              }
            },
          },
          '👁'
        ),
      ]);
    },
  },
  {
    title: 'SSH映射端口',
    align: 'center',
    dataIndex: 'proxySshRemotePort',
  },
  {
    title: 'HTTP映射端口',
    align: 'center',
    dataIndex: 'proxyHttpRemotePort',
    customRender: ({ text, record }) => {
      if (!text || !record.serverAddr) {
        return text;
      }
      const props = {
        style: { color: '#1890ff', cursor: 'pointer' },
        onClick: () => {
          const url = `http://${record.serverAddr}:${text}`;
          window.open(url, '_blank');
        },
      };
      return h('a', props, text);
    },
  },
];
//子表列表数据
export const cpeDeviceAutorebootColumns: BasicColumn[] = [
  {
    title: '重启定义',
    align: 'center',
    dataIndex: 'schedule',
  },
  {
    title: '重启命令',
    align: 'center',
    dataIndex: 'cmd',
  },
];
//子表列表数据
export const cpeDeviceNetworkColumns: BasicColumn[] = [
  {
    title: '内网地址',
    align: 'center',
    dataIndex: 'ipaddr',
  },
  {
    title: '子网掩码',
    align: 'center',
    dataIndex: 'netmask',
  },
  {
    title: 'DHCP起始地址',
    align: 'center',
    dataIndex: 'dhcpStart',
  },
  {
    title: 'DHCP截至地址',
    align: 'center',
    dataIndex: 'dhcpEnd',
  },
  {
    title: 'DHCP租期',
    align: 'center',
    dataIndex: 'dhcpLeasetime',
  },
];
//子表列表数据
export const cpeSpeedLimitColumns: BasicColumn[] = [
  {
    title: '上传速率',
    align: 'center',
    dataIndex: 'upLimit',
  },
  {
    title: '下载速率',
    align: 'center',
    dataIndex: 'downLimit',
  },
];
//子表列表数据
export const cpeDeviceWirelessColumns: BasicColumn[] = [
  {
    title: '2.4G WiFi功能',
    align: 'center',
    dataIndex: 'radio24Disabled',
    customRender: ({ text }) => {
      return h(Tag, { color: text === '0' ? 'success' : 'error' }, () => (text === '0' ? '启用' : '禁用'));
    },
  },
  {
    title: '2.4G信道',
    align: 'center',
    dataIndex: 'radio24Channel_dictText',
  },
  {
    title: '2.4G SSID',
    align: 'center',
    dataIndex: 'radio24Ssid',
  },
  {
    title: '2.4G加密',
    align: 'center',
    dataIndex: 'radio24Encryption_dictText',
  },
  {
    title: '2.4G密钥',
    align: 'center',
    dataIndex: 'radio24Key',
    customRender: ({ text }) => {
      return h('div', { class: 'password-cell' }, [
        h('span', { class: 'password-text' }, text ? '********' : ''),
        h(
          'span',
          {
            class: 'eye-icon',
            onClick: (e) => {
              e.stopPropagation();
              const textEl = e.currentTarget.previousElementSibling;
              const eyeEl = e.currentTarget;
              if (textEl.textContent === '********') {
                textEl.textContent = text;
                eyeEl.classList.add('visible');
              } else {
                textEl.textContent = '********';
                eyeEl.classList.remove('visible');
              }
            },
          },
          '👁'
        ),
      ]);
    },
  },
  {
    title: '2.4G最大终端数',
    align: 'center',
    dataIndex: 'radio24MaxSta',
  },
  {
    title: '2.4G功率',
    align: 'center',
    dataIndex: 'radio24Power',
  },
  {
    title: '2.4G MAC过滤',
    align: 'center',
    dataIndex: 'radio24Macfilter',
  },
  {
    title: '2.4G不广播',
    align: 'center',
    dataIndex: 'radio24Hidden',
    customRender: ({ text }) => {
      return h(Tag, { color: text === '0' ? 'success' : 'error' }, () => (text === '0' ? '广播' : '不广播'));
    },
  },
  {
    title: '5G WiFi功能',
    align: 'center',
    dataIndex: 'radio5Disabled',
    customRender: ({ text }) => {
      return h(Tag, { color: text === '0' ? 'success' : 'error' }, () => (text === '0' ? '启用' : '禁用'));
    },
  },
  {
    title: '5G信道',
    align: 'center',
    dataIndex: 'radio5Channel_dictText',
  },
  {
    title: '5G SSID',
    align: 'center',
    dataIndex: 'radio5Ssid',
  },
  {
    title: '5G加密',
    align: 'center',
    dataIndex: 'radio5Encryption_dictText',
  },
  {
    title: '5G密钥',
    align: 'center',
    dataIndex: 'radio5Key',
    customRender: ({ text }) => {
      return h('div', { class: 'password-cell' }, [
        h('span', { class: 'password-text' }, text ? '********' : ''),
        h(
          'span',
          {
            class: 'eye-icon',
            onClick: (e) => {
              e.stopPropagation();
              const textEl = e.currentTarget.previousElementSibling;
              const eyeEl = e.currentTarget;
              if (textEl.textContent === '********') {
                textEl.textContent = text;
                eyeEl.classList.add('visible');
              } else {
                textEl.textContent = '********';
                eyeEl.classList.remove('visible');
              }
            },
          },
          '👁'
        ),
      ]);
    },
  },
  {
    title: '5G最大终端数',
    align: 'center',
    dataIndex: 'radio5MaxSta',
  },
  {
    title: '5G功率',
    align: 'center',
    dataIndex: 'radio5Power',
  },
  {
    title: '5G MAC过滤',
    align: 'center',
    dataIndex: 'radio5Macfilter',
  },
  {
    title: '5G不广播',
    align: 'center',
    dataIndex: 'radio5Hidden',
    customRender: ({ text }) => {
      return h(Tag, { color: text === '0' ? 'success' : 'error' }, () => (text === '0' ? '广播' : '不广播'));
    },
  },
];
//子表列表数据
export const cpeOperLogColumns: BasicColumn[] = [
  {
    title: '创建时间',
    align: 'center',
    width: 170,
    dataIndex: 'createTs',
  },
  {
    title: '操作类型',
    align: 'center',
    dataIndex: 'operType',
  },
  {
    title: '操作参数',
    align: 'center',
    dataIndex: 'operParam',
  },
  {
    title: '执行状态',
    align: 'center',
    dataIndex: 'operRetcode',
  },
  {
    title: '操作时间',
    align: 'center',
    width: 170,
    dataIndex: 'operTs',
  },
  {
    title: '操作日志',
    align: 'center',
    dataIndex: 'operLog',
  },
];

// 高级查询数据
export const superQuerySchema = {
  deviceSn: { title: '设备标识', order: 0, view: 'text', type: 'string' },
  deviceStatusNo: { title: '设备状态', order: 1, view: 'list', type: 'string', dictCode: 'cpe_device_status' },
  deviceModuleNo: { title: '设备型号', order: 2, view: 'list', type: 'string', dictCode: 'cpe_device_module' },
  deviceTypeNo: { title: '设备类型', order: 3, view: 'list', type: 'string', dictCode: 'cpe_device_type' },
  cardNo: { title: '关联卡片', order: 4, view: 'list', type: 'string', dictTable: 'card_info', dictCode: 'id', dictText: 'card_no' },
  onlineCardNo: { title: '在线卡片', order: 5, view: 'list', type: 'string', dictTable: 'card_info', dictCode: 'id', dictText: 'card_no' },
  onlineNetNo: { title: '在线网络', order: 6, view: 'list', type: 'string', dictCode: 'cpe_network' },
  onlineBand: { title: '在线频段', order: 7, view: 'text', type: 'string' },
  customerName: { title: '所属客户', order: 8, view: 'list', type: 'string', dictCode: '' },
  position: { title: '安装位置', order: 9, view: 'text', type: 'string' },
  modemVersion: { title: '5G模块版本', order: 10, view: 'text', type: 'string' },
  imei: { title: 'IMEI', order: 11, view: 'text', type: 'string' },
  iccid: { title: 'ICCID', order: 12, view: 'text', type: 'string' },
  simSlot: { title: 'SIM卡槽', order: 13, view: 'number', type: 'number' },
  status: { title: '状态', order: 14, view: 'text', type: 'string' },
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

/**
 * 格式化连接时长
 * @param seconds 秒数
 * @returns 格式化后的时间字符串
 */
const formatDuration = (seconds: number): string => {
  if (!seconds && seconds !== 0) {
    return '0秒';
  }

  const units = [
    { value: 31536000, label: '年' },
    { value: 2592000, label: '月' },
    { value: 86400, label: '天' },
    { value: 3600, label: '时' },
    { value: 60, label: '分' },
    { value: 1, label: '秒' },
  ];

  // 如果时间小于60秒，直接返回秒数
  if (seconds < 60) {
    return `${seconds}秒`;
  }

  const parts = [];
  let remainingSeconds = seconds;

  // 从大到小依次计算每个单位
  for (const unit of units) {
    const count = Math.floor(remainingSeconds / unit.value);
    if (count > 0) {
      parts.push(`${count}${unit.label}`);
      remainingSeconds %= unit.value;

      // 只显示最大的两个单位
      if (parts.length >= 2) {
        break;
      }
    }
  }

  return parts.join('');
};
