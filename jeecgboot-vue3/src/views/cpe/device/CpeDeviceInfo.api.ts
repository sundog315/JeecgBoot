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
  cpeDeviceAutorebootList = '/cpe/device/cpeDeviceInfo/listCpeDeviceAutorebootByMainId',
  cpeDeviceAutorebootSave= '/cpe/device/cpeDeviceInfo/addCpeDeviceAutoreboot',
  cpeDeviceAutorebootEdit= '/cpe/device/cpeDeviceInfo/editCpeDeviceAutoreboot',
  cpeDeviceAutorebootDelete = '/cpe/device/cpeDeviceInfo/deleteCpeDeviceAutoreboot',
  cpeDeviceAutorebootDeleteBatch = '/cpe/device/cpeDeviceInfo/deleteBatchCpeDeviceAutoreboot',
  cpeDeviceNetworkList = '/cpe/device/cpeDeviceInfo/listCpeDeviceNetworkByMainId',
  cpeDeviceNetworkSave= '/cpe/device/cpeDeviceInfo/addCpeDeviceNetwork',
  cpeDeviceNetworkEdit= '/cpe/device/cpeDeviceInfo/editCpeDeviceNetwork',
  cpeDeviceNetworkDelete = '/cpe/device/cpeDeviceInfo/deleteCpeDeviceNetwork',
  cpeDeviceNetworkDeleteBatch = '/cpe/device/cpeDeviceInfo/deleteBatchCpeDeviceNetwork',
  cpeSpeedLimitList = '/cpe/device/cpeDeviceInfo/listCpeSpeedLimitByMainId',
  cpeSpeedLimitSave= '/cpe/device/cpeDeviceInfo/addCpeSpeedLimit',
  cpeSpeedLimitEdit= '/cpe/device/cpeDeviceInfo/editCpeSpeedLimit',
  cpeSpeedLimitDelete = '/cpe/device/cpeDeviceInfo/deleteCpeSpeedLimit',
  cpeSpeedLimitDeleteBatch = '/cpe/device/cpeDeviceInfo/deleteBatchCpeSpeedLimit',
  cpeDeviceWirelessList = '/cpe/device/cpeDeviceInfo/listCpeDeviceWirelessByMainId',
  cpeDeviceWirelessSave= '/cpe/device/cpeDeviceInfo/addCpeDeviceWireless',
  cpeDeviceWirelessEdit= '/cpe/device/cpeDeviceInfo/editCpeDeviceWireless',
  cpeDeviceWirelessDelete = '/cpe/device/cpeDeviceInfo/deleteCpeDeviceWireless',
  cpeDeviceWirelessDeleteBatch = '/cpe/device/cpeDeviceInfo/deleteBatchCpeDeviceWireless',
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
export const cpeDeviceAutorebootList = (params) => {
  if(params['cpeId']){
    return defHttp.get({ url: Api.cpeDeviceAutorebootList, params });
  }
  return Promise.resolve({});
}

/**
 * 删除单个
 */
export const cpeDeviceAutorebootDelete = (params,handleSuccess) => {
  return defHttp.delete({ url: Api.cpeDeviceAutorebootDelete, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 * @param params
 */
export const cpeDeviceAutorebootDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.cpeDeviceAutorebootDeleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    }
  });
}

/**
 * 保存或者更新
 * @param params
 */
export const  cpeDeviceAutorebootSaveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.cpeDeviceAutorebootEdit : Api.cpeDeviceAutorebootSave;
  return defHttp.post({ url: url, params },{ isTransformResponse: false });
}

/**
 * 导入
 */
export const cpeDeviceAutorebootImportUrl = '/cpe/device/cpeDeviceInfo/importCpeDeviceAutoreboot'

/**
 * 导出
 */
export const cpeDeviceAutorebootExportXlsUrl = '/cpe/device/cpeDeviceInfo/exportCpeDeviceAutoreboot'

/**
 * 列表接口
 * @param params
 */
export const cpeDeviceNetworkList = (params) => {
  if(params['cpeId']){
    return defHttp.get({ url: Api.cpeDeviceNetworkList, params });
  }
  return Promise.resolve({});
}

/**
 * 删除单个
 */
export const cpeDeviceNetworkDelete = (params,handleSuccess) => {
  return defHttp.delete({ url: Api.cpeDeviceNetworkDelete, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 * @param params
 */
export const cpeDeviceNetworkDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.cpeDeviceNetworkDeleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    }
  });
}

/**
 * 保存或者更新
 * @param params
 */
export const  cpeDeviceNetworkSaveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.cpeDeviceNetworkEdit : Api.cpeDeviceNetworkSave;
  return defHttp.post({ url: url, params },{ isTransformResponse: false });
}

/**
 * 导入
 */
export const cpeDeviceNetworkImportUrl = '/cpe/device/cpeDeviceInfo/importCpeDeviceNetwork'

/**
 * 导出
 */
export const cpeDeviceNetworkExportXlsUrl = '/cpe/device/cpeDeviceInfo/exportCpeDeviceNetwork'

/**
 * 列表接口
 * @param params
 */
export const cpeSpeedLimitList = (params) => {
  if(params['cpeId']){
    return defHttp.get({ url: Api.cpeSpeedLimitList, params });
  }
  return Promise.resolve({});
}

/**
 * 删除单个
 */
export const cpeSpeedLimitDelete = (params,handleSuccess) => {
  return defHttp.delete({ url: Api.cpeSpeedLimitDelete, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 * @param params
 */
export const cpeSpeedLimitDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.cpeSpeedLimitDeleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    }
  });
}

/**
 * 保存或者更新
 * @param params
 */
export const  cpeSpeedLimitSaveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.cpeSpeedLimitEdit : Api.cpeSpeedLimitSave;
  return defHttp.post({ url: url, params },{ isTransformResponse: false });
}

/**
 * 导入
 */
export const cpeSpeedLimitImportUrl = '/cpe/device/cpeDeviceInfo/importCpeSpeedLimit'

/**
 * 导出
 */
export const cpeSpeedLimitExportXlsUrl = '/cpe/device/cpeDeviceInfo/exportCpeSpeedLimit'

/**
 * 列表接口
 * @param params
 */
export const cpeDeviceWirelessList = (params) => {
  if(params['cpeId']){
    return defHttp.get({ url: Api.cpeDeviceWirelessList, params });
  }
  return Promise.resolve({});
}

/**
 * 删除单个
 */
export const cpeDeviceWirelessDelete = (params,handleSuccess) => {
  return defHttp.delete({ url: Api.cpeDeviceWirelessDelete, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 * @param params
 */
export const cpeDeviceWirelessDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.cpeDeviceWirelessDeleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    }
  });
}

/**
 * 保存或者更新
 * @param params
 */
export const  cpeDeviceWirelessSaveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.cpeDeviceWirelessEdit : Api.cpeDeviceWirelessSave;
  return defHttp.post({ url: url, params },{ isTransformResponse: false });
}

/**
 * 导入
 */
export const cpeDeviceWirelessImportUrl = '/cpe/device/cpeDeviceInfo/importCpeDeviceWireless'

/**
 * 导出
 */
export const cpeDeviceWirelessExportXlsUrl = '/cpe/device/cpeDeviceInfo/exportCpeDeviceWireless'

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
