import {defHttp} from '/@/utils/http/axios';
import { useMessage } from "/@/hooks/web/useMessage";

const { createConfirm } = useMessage();

enum Api {
  list = '/cpe/card/cardInfo/list',
  save= '/cpe/card/cardInfo/add',
  edit= '/cpe/card/cardInfo/edit',
  deleteOne = '/cpe/card/cardInfo/delete',
  deleteBatch = '/cpe/card/cardInfo/deleteBatch',
  importExcel = '/cpe/card/cardInfo/importExcel',
  exportXls = '/cpe/card/cardInfo/exportXls',
  cardPackageRelList = '/cpe/card/cardInfo/listCardPackageRelByMainId',
  cardPackageRelSave= '/cpe/card/cardInfo/addCardPackageRel',
  cardPackageRelEdit= '/cpe/card/cardInfo/editCardPackageRel',
  cardPackageRelDelete = '/cpe/card/cardInfo/deleteCardPackageRel',
  cardPackageRelDeleteBatch = '/cpe/card/cardInfo/deleteBatchCardPackageRel',
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
export const cardPackageRelList = (params) => {
  if(params['cardId']){
    return defHttp.get({ url: Api.cardPackageRelList, params });
  }
  return Promise.resolve({});
}

/**
 * 删除单个
 */
export const cardPackageRelDelete = (params,handleSuccess) => {
  return defHttp.delete({ url: Api.cardPackageRelDelete, params }, { joinParamsToUrl: true }).then(() => {
    handleSuccess();
  });
}

/**
 * 批量删除
 * @param params
 */
export const cardPackageRelDeleteBatch = (params, handleSuccess) => {
  createConfirm({
    iconType: 'warning',
    title: '确认删除',
    content: '是否删除选中数据',
    okText: '确认',
    cancelText: '取消',
    onOk: () => {
      return defHttp.delete({ url: Api.cardPackageRelDeleteBatch, data: params }, { joinParamsToUrl: true }).then(() => {
        handleSuccess();
      });
    }
  });
}

/**
 * 保存或者更新
 * @param params
 */
export const  cardPackageRelSaveOrUpdate = (params, isUpdate) => {
  let url = isUpdate ? Api.cardPackageRelEdit : Api.cardPackageRelSave;
  return defHttp.post({ url: url, params },{ isTransformResponse: false });
}

/**
 * 导入
 */
export const cardPackageRelImportUrl = '/cpe/card/cardInfo/importCardPackageRel'

/**
 * 导出
 */
export const cardPackageRelExportXlsUrl = '/cpe/card/cardInfo/exportCardPackageRel'
