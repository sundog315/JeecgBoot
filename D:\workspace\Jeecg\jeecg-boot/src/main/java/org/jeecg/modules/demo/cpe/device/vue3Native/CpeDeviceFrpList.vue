<template>
 <div class="p-2">
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
        <a-button type="primary" @click="handleAdd" preIcon="ant-design:plus-outlined" v-if="mainId!=''"> 新增</a-button>
        <a-button  type="primary" preIcon="ant-design:export-outlined" @click="onExportXls" v-if="mainId!=''"> 导出</a-button>
        <j-upload-button  type="primary" preIcon="ant-design:import-outlined" @click="onImportXls" v-if="mainId!=''">导入</j-upload-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <template #overlay>
            <a-menu>
              <a-menu-item key="1" @click="batchHandleDelete">
                <Icon icon="ant-design:delete-outlined"></Icon>
                删除
              </a-menu-item>
            </a-menu>
          </template>
          <a-button>批量操作
            <Icon icon="mdi:chevron-down"></Icon>
          </a-button>
        </a-dropdown>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)"/>
      </template>
      <!--字段回显插槽-->
      <template v-slot:bodyCell="{ column, record, index, text }">
      </template>
    </BasicTable>

    <CpeDeviceFrpModal ref="registerModal" @success="handleSuccess"/>
   </div>
</template>

<script lang="ts" setup>
  import { ref, reactive, unref, inject, watch } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import CpeDeviceFrpModal from './components/CpeDeviceFrpModal.vue'
  import { cpeDeviceFrpColumns } from './CpeDeviceInfo.data';
  import { cpeDeviceFrpList, cpeDeviceFrpDelete, cpeDeviceFrpDeleteBatch, cpeDeviceFrpExportXlsUrl, cpeDeviceFrpImportUrl } from './CpeDeviceInfo.api';
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
      api: cpeDeviceFrpList,
      columns: cpeDeviceFrpColumns,
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
      name: '设备远程控制',
      url: cpeDeviceFrpExportXlsUrl,
      params: {
        'cpeId': mainId
      }
    },
    importConfig: {
      url: ()=>{
        return cpeDeviceFrpImportUrl + '/' + unref(mainId)
      }
    }
  });

  //注册table数据
  const [registerTable, { reload,getDataSource}, { rowSelection, selectedRowKeys }] = tableContext;
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
    let dataSource = getDataSource();
    if(dataSource.length >= 1){
      $message.createMessage.warning('一对一子表只能添加一条数据')
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
    await cpeDeviceFrpDelete({id: record.id}, handleSuccess);
  }

  /**
   * 批量删除事件
   */
  async function batchHandleDelete() {
    await cpeDeviceFrpDeleteBatch({ids: selectedRowKeys.value}, handleSuccess);
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
        label: '编辑',
        onClick: handleEdit.bind(null, record),
      },
    ]
  }
  
  /**
   * 下拉操作栏
   */
  function getDropDownAction(record){
    return [
      {
        label: '详情',
        onClick: handleDetail.bind(null, record),
      },
      {
        label: '删除',
        popConfirm: {
          title: '是否确认删除',
          confirm: handleDelete.bind(null, record),
        },
      },
    ];
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
