import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createConfirm } = useMessage();

enum Api {
  list = '/contract/info/contractInfo/list',
  save = '/contract/info/contractInfo/add',
  edit = '/contract/info/contractInfo/edit',
  deleteOne = '/contract/info/contractInfo/delete',
  deleteBatch = '/contract/info/contractInfo/deleteBatch',
  importExcel = '/contract/info/contractInfo/importExcel',
  exportXls = '/contract/info/contractInfo/exportXls',
  contractDeviceList = '/contract/info/contractInfo/listContractDeviceByMainId',
  contractDeviceSave = '/contract/info/contractInfo/addContractDevice',
  contractDeviceEdit = '/contract/info/contractInfo/editContractDevice',
  contractDeviceDelete = '/contract/info/contractInfo/deleteContractDevice',
  contractDeviceDeleteBatch = '/contract/info/contractInfo/deleteBatchContractDevice',
}
/**
 * 导出api
 * @param params
 */
export const getExportUrl = Api.exportXls;

/**
 * 导入api
 */
export const getImportUrl = Api.importExcel;
/**
 * 列表接口
 * @param params
 */
export const list = (params) => defHttp.get({ url: Api.list, params });

/**
 * 删除单个
 */
export const deleteOne = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.deleteOne, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};
/**
 * 批量删除
 * @param params
 */
export const batchDelete = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.deleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    },
  });
};
/**
 * 保存或者更新
 * @param params
 */
export const saveOrUpdate = (params, isUpdate) => {
  const url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({ url: url, params });
};
/**
 * 列表接口
 * @param params
 */
export const contractDeviceList = (params) => {
  if (params['contractId']) {
    return defHttp.get({ url: Api.contractDeviceList, params });
  }
  return Promise.resolve({});
};

/**
 * 删除单个
 */
export const contractDeviceDelete = (params, handleSuccess) => {
  return defHttp.delete({ url: Api.contractDeviceDelete, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
};
/**
 * 批量删除
 * @param params
 */
export const contractDeviceDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.contractDeviceDeleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    },
  });
};
/**
 * 保存或者更新
 * @param params
 */
export const contractDeviceSaveOrUpdate = (params, isUpdate) => {
  const url = isUpdate ? Api.contractDeviceEdit : Api.contractDeviceSave;
  return defHttp.post({ url: url, params });
};
/**
 * 导入
 */
export const contractDeviceImportUrl = '/contract/info/contractInfo/importContractDevice';

/**
 * 导出
 */
export const contractDeviceExportXlsUrl = '/contract/info/contractInfo/exportContractDevice';
