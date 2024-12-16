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

    <CpeOperLogModal ref="registerModal" @success="handleSuccess"/>
   </div>
</template>

<script lang="ts" setup>
  import { ref, reactive, unref, inject, watch } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import CpeOperLogModal from './components/CpeOperLogModal.vue'
  import { cpeOperLogColumns } from './CpeDeviceInfo.data';
  import { cpeOperLogList, cpeOperLogDelete, cpeOperLogDeleteBatch, cpeOperLogExportXlsUrl, cpeOperLogImportUrl } from './CpeDeviceInfo.api';
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
      api: cpeOperLogList,
      columns: cpeOperLogColumns,
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
      name: '操作记录表',
      url: cpeOperLogExportXlsUrl,
      params: {
        'cpeId': mainId
      }
    },
    importConfig: {
      url: ()=>{
        return cpeOperLogImportUrl + '/' + unref(mainId)
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
   * 详情事件
   */
  function handleDetail(record: Recordable) {
    registerModal.value.disableSubmit = true;
    registerModal.value.edit(record);
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
