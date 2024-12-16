<template>
 <div class="p-2">
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)"/>
      </template>
      <!--字段回显插槽-->
      <template v-slot:bodyCell="{ column, record, index, text }">
      </template>
    </BasicTable>

    <CpeDeviceNeighborModal ref="registerModal" @success="handleSuccess"/>
   </div>
</template>

<script lang="ts" setup>
  import { ref, reactive, unref, inject, watch } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import CpeDeviceNeighborModal from './components/CpeDeviceNeighborModal.vue'
  import { cpeDeviceNeighborColumns } from './CpeDeviceInfo.data';
  import { cpeDeviceNeighborList, cpeDeviceNeighborDelete, cpeDeviceNeighborDeleteBatch, cpeDeviceNeighborExportXlsUrl, cpeDeviceNeighborImportUrl } from './CpeDeviceInfo.api';
  import { isEmpty } from "/@/utils/is";
  import { useMessage } from '/@/hooks/web/useMessage';
  import { downloadFile } from '/@/utils/common/renderUtils';
  
  const toggleSearchStatus = ref<boolean>(false);
  //接收主表id
  const mainId = inject('mainId') || '';
  //提示弹窗
  const $message = useMessage()
  const queryParam = {};
  // 列表页面公共参数、方法
  const { prefixCls, tableContext, onImportXls, onExportXls } = useListPage({
    tableProps: {
      api: cpeDeviceNeighborList,
      columns: cpeDeviceNeighborColumns,
      canResize: false,
      useSearchForm: false,
      actionColumn: {
        width: 180,
        fixed:'right'
      },
      beforeFetch: (params) => {
        return Object.assign(params, queryParam);
      },
    },
    exportConfig: {
      name: 'CPE设备邻区信息',
      url: cpeDeviceNeighborExportXlsUrl,
      params: {
        'cpeId': mainId
      }
    },
    importConfig: {
      url: ()=>{
        return cpeDeviceNeighborImportUrl + '/' + unref(mainId)
      }
    }
  });

  //注册table数据
  const [registerTable, { reload}, { rowSelection, selectedRowKeys }] = tableContext;
  const registerModal = ref();
  const formRef = ref();
  const labelCol = reactive({
    xs:24,
    sm:4,
    xl:6,
    xxl:4
  });
  const wrapperCol = reactive({
    xs: 24,
    sm: 20,
  });
  
  /**
   * 新增事件
   */
  function handleAdd() {
    if (isEmpty(unref(mainId))) {
        $message.createMessage.warning('请选择一个主表信息')
        return;
    }
    registerModal.value.disableSubmit = false;
    registerModal.value.add();
  }

  /**
   * 编辑事件
   */
  async function handleEdit(record: Recordable) {
    registerModal.value.disableSubmit = false;
    registerModal.value.edit(record);
  }

  /**
   * 详情事件
   */
  function handleDetail(record: Recordable) {
    registerModal.value.disableSubmit = true;
    registerModal.value.edit(record);
  }
  
  /**
   * 删除事件
   */
  async function handleDelete(record) {
    await cpeDeviceNeighborDelete({id: record.id}, handleSuccess);
  }

  /**
   * 批量删除事件
   */
  async function batchHandleDelete() {
    await cpeDeviceNeighborDeleteBatch({ids: selectedRowKeys.value}, handleSuccess);
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
        label: '详情',
        onClick: handleDetail.bind(null, record),
      },
    ]
  }
  
  /**
   * 重置
   */
  function searchReset() {
    formRef.value.resetFields();
    selectedRowKeys.value = [];
    //刷新数据
    reload();
  }
  
  watch(mainId, () => {
    queryParam['cpeId'] = unref(mainId);
    reload();
  });
</script>
<style lang="less" scoped>
  .jeecg-basic-table-form-container {
    padding: 0;
    .table-page-search-submitButtons {
      display: block;
      margin-bottom: 24px;
      white-space: nowrap;
    }
    .query-group-cust{
      min-width: 100px !important;
    }
    .query-group-split-cust{
      width: 30px;
      display: inline-block;
      text-align: center
    }
  }
</style>
