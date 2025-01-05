import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/cpe/device/cpeDeviceInfo/list',
  save= '/cpe/device/cpeDeviceInfo/add',
  edit= '/cpe/device/cpeDeviceInfo/edit',
  rebootOne = '/cpe/device/cpeDeviceInfo/reboot',
  deleteOne = '/cpe/device/cpeDeviceInfo/delete',
  deleteBatch = '/cpe/device/cpeDeviceInfo/deleteBatch',
  importExcel = '/cpe/device/cpeDeviceInfo/importExcel',
  exportXls = '/cpe/device/cpeDeviceInfo/exportXls',
  cpeDeviceStatusList = '/cpe/device/cpeDeviceInfo/listCpeDeviceStatusByMainId',
  cpeDeviceStatusSave= '/cpe/device/cpeDeviceInfo/addCpeDeviceStatus',
  cpeDeviceStatusEdit= '/cpe/device/cpeDeviceInfo/editCpeDeviceStatus',
  cpeDeviceStatusDelete = '/cpe/device/cpeDeviceInfo/deleteCpeDeviceStatus',
  cpeDeviceStatusDeleteBatch = '/cpe/device/cpeDeviceInfo/deleteBatchCpeDeviceStatus',
  cpeDeviceNeighborList = '/cpe/device/cpeDeviceInfo/listCpeDeviceNeighborByMainId',
  cpeDeviceNeighborSave= '/cpe/device/cpeDeviceInfo/addCpeDeviceNeighbor',
  cpeDeviceNeighborEdit= '/cpe/device/cpeDeviceInfo/editCpeDeviceNeighbor',
  cpeDeviceNeighborDelete = '/cpe/device/cpeDeviceInfo/deleteCpeDeviceNeighbor',
  cpeDeviceNeighborDeleteBatch = '/cpe/device/cpeDeviceInfo/deleteBatchCpeDeviceNeighbor',
  cpeDeviceFrpList = '/cpe/device/cpeDeviceInfo/listCpeDeviceFrpByMainId',
  cpeDeviceFrpSave= '/cpe/device/cpeDeviceInfo/addCpeDeviceFrp',
  cpeDeviceFrpEdit= '/cpe/device/cpeDeviceInfo/editCpeDeviceFrp',
  cpeDeviceFrpDelete = '/cpe/device/cpeDeviceInfo/deleteCpeDeviceFrp',
  cpeDeviceFrpDeleteBatch = '/cpe/device/cpeDeviceInfo/deleteBatchCpeDeviceFrp',
  cpeOperLogList = '/cpe/device/cpeDeviceInfo/listCpeOperLogByMainId',
  cpeOperLogSave= '/cpe/device/cpeDeviceInfo/addCpeOperLog',
  cpeOperLogEdit= '/cpe/device/cpeDeviceInfo/editCpeOperLog',
  cpeOperLogDelete = '/cpe/device/cpeDeviceInfo/deleteCpeOperLog',
  cpeOperLogDeleteBatch = '/cpe/device/cpeDeviceInfo/deleteBatchCpeOperLog',
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
export const list = (params) =>
  defHttp.get({ url: Api.list, params });

/**
 * 重启单个
 */
export const rebootOne = (params,handleSuccess) => {
  return defHttp.post({url: Api.rebootOne, params}, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

/**
 * 删除单个
 */
export const deleteOne = (params,handleSuccess) => {
  return defHttp.delete({ url: Api.deleteOne, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

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
    }
  });
}

/**
 * 保存或者更新
 * @param params
 */
export const saveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.edit : Api.save;
  return defHttp.post({ url: url, params },{ isTransformResponse: false });
}
  
/**
 * 列表接口
 * @param params
 */
export const cpeDeviceStatusList = (params) => {
  if(params['cpeId']){
    return defHttp.get({ url: Api.cpeDeviceStatusList, params });
  }
  return Promise.resolve({});
}

/**
 * 删除单个
 */
export const cpeDeviceStatusDelete = (params,handleSuccess) => {
  return defHttp.delete({ url: Api.cpeDeviceStatusDelete, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 * @param params
 */
export const cpeDeviceStatusDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.cpeDeviceStatusDeleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    }
  });
}

/**
 * 保存或者更新
 * @param params
 */
export const  cpeDeviceStatusSaveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.cpeDeviceStatusEdit : Api.cpeDeviceStatusSave;
  return defHttp.post({ url: url, params },{ isTransformResponse: false });
}

/**
 * 导入
 */
export const cpeDeviceStatusImportUrl = '/cpe/device/cpeDeviceInfo/importCpeDeviceStatus'

/**
 * 导出
 */
export const cpeDeviceStatusExportXlsUrl = '/cpe/device/cpeDeviceInfo/exportCpeDeviceStatus'
  
/**
 * 列表接口
 * @param params
 */
export const cpeDeviceNeighborList = (params) => {
  if(params['cpeId']){
    return defHttp.get({ url: Api.cpeDeviceNeighborList, params });
  }
  return Promise.resolve({});
}

/**
 * 删除单个
 */
export const cpeDeviceNeighborDelete = (params,handleSuccess) => {
  return defHttp.delete({ url: Api.cpeDeviceNeighborDelete, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 * @param params
 */
export const cpeDeviceNeighborDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.cpeDeviceNeighborDeleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    }
  });
}

/**
 * 保存或者更新
 * @param params
 */
export const  cpeDeviceNeighborSaveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.cpeDeviceNeighborEdit : Api.cpeDeviceNeighborSave;
  return defHttp.post({ url: url, params },{ isTransformResponse: false });
}

/**
 * 导入
 */
export const cpeDeviceNeighborImportUrl = '/cpe/device/cpeDeviceInfo/importCpeDeviceNeighbor'

/**
 * 导出
 */
export const cpeDeviceNeighborExportXlsUrl = '/cpe/device/cpeDeviceInfo/exportCpeDeviceNeighbor'


/**
 * 列表接口
 * @param params
 */
export const cpeDeviceFrpList = (params) => {
  if(params['cpeId']){
    return defHttp.get({ url: Api.cpeDeviceFrpList, params });
  }
  return Promise.resolve({});
}

/**
 * 删除单个
 */
export const cpeDeviceFrpDelete = (params,handleSuccess) => {
  return defHttp.delete({ url: Api.cpeDeviceFrpDelete, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 * @param params
 */
export const cpeDeviceFrpDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.cpeDeviceFrpDeleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    }
  });
}

/**
 * 保存或者更新
 * @param params
 */
export const  cpeDeviceFrpSaveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.cpeDeviceFrpEdit : Api.cpeDeviceFrpSave;
  return defHttp.post({ url: url, params },{ isTransformResponse: false });
}

/**
 * 导入
 */
export const cpeDeviceFrpImportUrl = '/cpe/device/cpeDeviceInfo/importCpeDeviceFrp'

/**
 * 导出
 */
export const cpeDeviceFrpExportXlsUrl = '/cpe/device/cpeDeviceInfo/exportCpeDeviceFrp'
 

/**
 * 列表接口
 * @param params
 */
export const cpeOperLogList = (params) => {
  if(params['cpeId']){
    return defHttp.get({ url: Api.cpeOperLogList, params });
  }
  return Promise.resolve({});
}

/**
 * 删除单个
 */
export const cpeOperLogDelete = (params,handleSuccess) => {
  return defHttp.delete({ url: Api.cpeOperLogDelete, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 * @param params
 */
export const cpeOperLogDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.cpeOperLogDeleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    }
  });
}

/**
 * 保存或者更新
 * @param params
 */
export const  cpeOperLogSaveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.cpeOperLogEdit : Api.cpeOperLogSave;
  return defHttp.post({ url: url, params },{ isTransformResponse: false });
}

/**
 * 导入
 */
export const cpeOperLogImportUrl = '/cpe/device/cpeDeviceInfo/importCpeOperLog'

/**
 * 导出
 */
export const cpeOperLogExportXlsUrl = '/cpe/device/cpeDeviceInfo/exportCpeOperLog'
