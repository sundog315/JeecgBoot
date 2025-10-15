<template>
  <div>
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection" :searchInfo="searchInfo">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" @click="handleCreate" preIcon="ant-design:plus-outlined" v-if="mainId != ''"> 新增</a-button>
        <a-button type="primary" preIcon="ant-design:export-outlined" @click="onExportXls" v-if="mainId != ''"> 导出</a-button>
        <j-upload-button type="primary" preIcon="ant-design:import-outlined" @click="onImportXls" v-if="mainId != ''">导入</j-upload-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined" />
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button
            >批量操作
            <Icon icon="mdi:chevron-down" />
          </a-button>
        </a-dropdown>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" />
      </template>
      <!--字段回显插槽-->
      <template #bodyCell="{ column, record, index, text }"> </template>
    </BasicTable>

    <ContractDeviceModal @register="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" setup>
  import { unref, inject, watch } from 'vue';
  import { BasicTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage';
  import { useModal } from '/@/components/Modal';
  import ContractDeviceModal from './components/ContractDeviceModal.vue';
  import { contractDeviceColumns } from './ContractInfo.data';
  import {
    contractDeviceList,
    contractDeviceDelete,
    contractDeviceDeleteBatch,
    contractDeviceExportXlsUrl,
    contractDeviceImportUrl,
  } from './ContractInfo.api';
  import { isEmpty } from '/@/utils/is';
  import { useMessage } from '/@/hooks/web/useMessage';

  //接收主表id
  const mainId = inject('mainId') || '';
  //提示弹窗
  const $message = useMessage();
  //弹窗model
  const [registerModal, { openModal }] = useModal();
  const searchInfo = {};
  // 列表页面公共参数、方法
  const { tableContext, onImportXls, onExportXls } = useListPage({
    tableProps: {
      api: contractDeviceList,
      columns: contractDeviceColumns,
      canResize: false,
      useSearchForm: false,
      actionColumn: {
        width: 180,
        fixed: 'right',
      },
      pagination: {
        current: 1,
        pageSize: 5,
        pageSizeOptions: ['5', '10', '20'],
      },
    },
    exportConfig: {
      name: '合同设备表',
      url: contractDeviceExportXlsUrl,
      params: {
        contractId: mainId,
      },
    },
    importConfig: {
      url: () => {
        return contractDeviceImportUrl + '/' + unref(mainId);
      },
    },
  });

  //注册table数据
  const [registerTable, { reload }, { rowSelection, selectedRowKeys }] = tableContext;

  watch(mainId, () => {
    searchInfo['contractId'] = unref(mainId);
    reload();
  });

  /**
   * 新增事件
   */
  function handleCreate() {
    if (isEmpty(unref(mainId))) {
      $message.createMessage.warning('请选择一个主表信息');
      return;
    }
    openModal(true, {
      isUpdate: false,
      showFooter: true,
    });
  }

  /**
   * 删除事件
   */
  async function handleDelete(record) {
    await contractDeviceDelete({ id: record.id }, handleSuccess);
  }

  /**
   * 批量删除事件
   */
  async function batchHandleDelete() {
    await contractDeviceDeleteBatch({ ids: selectedRowKeys.value }, handleSuccess);
  }

  /**
   * 成功回调
   */
  function handleSuccess() {
    (selectedRowKeys.value = []) && reload();
  }

  /**
   * 操作栏
   */
  function getTableAction(record) {
    return [
      {
        label: '删除',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
          placement: 'topLeft',
        },
      },
    ];
  }
</script>
