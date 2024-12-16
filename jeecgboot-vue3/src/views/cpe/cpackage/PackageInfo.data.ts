import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '套餐名',
    align: "center",
    dataIndex: 'name'
  },
  {
    title: '套餐类型',
    align: "center",
    dataIndex: 'type_dictText'
  },
  {
    title: '套餐量',
    align: "center",
    dataIndex: 'size'
  },
];

// 高级查询数据
export const superQuerySchema = {
  name: {title: '套餐名',order: 0,view: 'text', type: 'string',},
  type: {title: '套餐类型',order: 1,view: 'number', type: 'number',dictCode: 'pacakge_type',},
  size: {title: '套餐量',order: 2,view: 'number', type: 'number',},
};
