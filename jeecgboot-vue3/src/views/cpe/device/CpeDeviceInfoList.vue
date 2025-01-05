<template>
  <div class="p-2 erpNativeList">
    <!--查询区域-->
    <div class="jeecg-basic-table-form-container">
      <a-form ref="formRef" @keyup.enter.native="reload" :model="queryParam" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-row :gutter="24">
          <a-col :lg="6">
            <a-form-item name="deviceSn">
              <template #label><span title="设备标识">设备标识</span></template>
              <a-input v-model:value="queryParam.deviceSn" class="query-group-cust"></a-input>
            </a-form-item>
          </a-col>
          <a-col :lg="6">
            <a-form-item name="deviceStatusNo">
              <template #label><span title="设备状态">设备状态</span></template>
              <j-select-multiple placeholder="请选择设备状态" v-model:value="queryParam.deviceStatusNo" dictCode="cpe_device_status" allow-clear />
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :lg="6">
              <a-form-item name="customerName">
                <template #label><span title="所属客户">所属客户</span></template>
                <a-input placeholder="请输入所属客户" v-model:value="queryParam.customerName" allow-clear ></a-input>
              </a-form-item>
            </a-col>
          </template>
          <a-col :xl="6" :lg="7" :md="8" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-col :lg="6">
                <a-button type="primary" preIcon="ant-design:search-outlined" @click="reload">查询</a-button>
                <a-button preIcon="ant-design:reload-outlined" @click="searchReset" style="margin-left: 8px">重置</a-button>
                <a @click="toggleSearchStatus = !toggleSearchStatus" style="margin-left: 8px">
                  {{ toggleSearchStatus ? '收起' : '展开' }}
                  <Icon :icon="toggleSearchStatus ? 'ant-design:up-outlined' : 'ant-design:down-outlined'" />
                </a>
              </a-col>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
  <div class="content">
    <!--引用表格-->
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <!--插槽:table标题-->
      <template #tableTitle>
      </template>
      <!--操作栏-->
      <template #action="{ record }">
        <TableAction :actions="getTableAction(record)" :dropDownActions="getDropDownAction(record)" />
      </template>
      <!--字段回显插槽-->
      <template v-slot:bodyCell="{ column, record, index, text }">
      </template>
    </BasicTable>
    <!--子表表格tab-->
    <a-tabs defaultActiveKey="1" style="margin: 10px">
      <a-tab-pane tab="CPE设备状态表" key="1" >
        <CpeDeviceStatusList />
      </a-tab-pane>
      <a-tab-pane tab="CPE设备邻区信息" key="2" forceRender>
        <CpeDeviceNeighborList />
      </a-tab-pane>
      <a-tab-pane tab="设备远程控制" key="3" forceRender>
        <CpeDeviceFrpList />
      </a-tab-pane>
      <a-tab-pane tab="自动重启" key="5" forceRender>
        <CpeDeviceAutorebootList />
      </a-tab-pane>
      <a-tab-pane tab="操作记录表" key="4" forceRender>
        <CpeOperLogList />
      </a-tab-pane>
     </a-tabs>
  </div>
    <!-- 表单区域 -->
    <CpeDeviceInfoModal ref="registerModal" @success="handleSuccess" />
  </div>
</template>

<script lang="ts" name="cpe.device-cpeDeviceInfo" setup>
  import { ref, reactive, computed, unref, provide } from 'vue';
  import { BasicTable, useTable, TableAction } from '/@/components/Table';
  import { useListPage } from '/@/hooks/system/useListPage'
  import CpeDeviceInfoModal from './components/CpeDeviceInfoModal.vue'
  import { columns, superQuerySchema } from './CpeDeviceInfo.data';
  import { list, rebootOne, deleteOne, batchDelete, getImportUrl,getExportUrl } from './CpeDeviceInfo.api';
  import { downloadFile } from '/@/utils/common/renderUtils';
  import JDictSelectTag from '/@/components/Form/src/jeecg/components/JDictSelectTag.vue';
  import JSelectMultiple from '/@/components/Form/src/jeecg/components/JSelectMultiple.vue';
  import CpeDeviceStatusList from './CpeDeviceStatusList.vue'
  import CpeDeviceNeighborList from './CpeDeviceNeighborList.vue'
  import CpeDeviceFrpList from './CpeDeviceFrpList.vue'
  import CpeDeviceAutorebootList from './CpeDeviceAutorebootList.vue'
  import CpeOperLogList from './CpeOperLogList.vue'
  import { useUserStore } from '/@/store/modules/user';
  import { cloneDeep } from "lodash-es";
  const formRef = ref();
  const queryParam = reactive<any>({});
  const checkedKeys = ref<Array<string | number>>([]);
  const registerModal = ref();
  const userStore = useUserStore();
   //注册table数据
  const { prefixCls,tableContext,onExportXls,onImportXls } = useListPage({
    tableProps:{
      title: '设备信息表',
      api: list,
      columns,
      canResize:false,
      useSearchForm: false,
      clickToRowSelect: true,
      rowSelection: {type: 'radio'},
      actionColumn: {
        width: 120,
        fixed:'right'
      },
      beforeFetch: async (params) => {
        let rangerQuery = await setRangeQuery();
        return Object.assign(params, rangerQuery);
      },
      pagination: {
        current: 1,
        pageSize: 5,
        pageSizeOptions: ['5', '10', '20'],
      },
    },
    exportConfig: {
      name:"设备信息表",
      url: getExportUrl,
      params: queryParam,
    },
    importConfig: {
      url: getImportUrl,
      success: handleSuccess,
    },
  })

  const [registerTable, { reload },{ rowSelection, selectedRowKeys }] = tableContext
  const mainId = computed(() => (unref(selectedRowKeys).length > 0 ? unref(selectedRowKeys)[0] : ''));
  //下发 mainId,子组件接收
  provide('mainId', mainId);
  
  /**
   * 详情事件
   */
  function handleDetail(record: Recordable) {
    registerModal.value.disableSubmit = true;
    registerModal.value.edit(record);
  }

  /**
   * 重启事件
   */
   async function handleReboot(record) {
    await rebootOne({id: record.id}, handleSuccess);
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
    ];
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
        label: '重启设备',
        popConfirm: {
          title: '是否确认重启',
          confirm: handleReboot.bind(null, record),
          placement: 'topLeft'
        },
        auth: 'cpe.device:cpe_device_info:reboot'
      },
    ];
  }

  

  /* ----------------------以下为原生查询需要添加的-------------------------- */
  const toggleSearchStatus = ref<boolean>(false);
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
   * 重置
   */
  function searchReset() {
    formRef.value.resetFields();
    selectedRowKeys.value = [];
    //刷新数据
    reload();
  }
  

  
  let rangeField = ''
  
  /**
   * 设置范围查询条件
   */
  async function setRangeQuery(){
    let queryParamClone = cloneDeep(queryParam);
    if (rangeField) {
      let fieldsValue = rangeField.split(',');
      fieldsValue.forEach(item => {
        if (queryParamClone[item]) {
          let range = queryParamClone[item];
          queryParamClone[item+'_begin'] = range[0];
          queryParamClone[item+'_end'] = range[1];
          delete queryParamClone[item];
        } else {
          queryParamClone[item+'_begin'] = '';
          queryParamClone[item+'_end'] = '';
        }
      })
    }
    return queryParamClone;
  }
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
    .ant-form-item:not(.ant-form-item-with-help){
      margin-bottom: 16px;
      height: 32px;
    }
    :deep(.ant-picker),:deep(.ant-input-number){
      width: 100%;
    }
  }
  .erpNativeList {
      height: 100%;
      .content {
        background-color: #fff;
        height: 100%;
      }
  }
</style>
