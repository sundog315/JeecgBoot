<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol" name="CpeDeviceForm">
          <a-row>
						<a-col :span="24">
							<a-form-item label="设备标识" v-bind="validateInfos.deviceSn" id="CpeDeviceForm-deviceSn" name="deviceSn">
								<a-input v-model:value="formData.deviceSn" placeholder="请输入设备标识"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="设备状态" v-bind="validateInfos.deviceStatusNo" id="CpeDeviceForm-deviceStatusNo" name="deviceStatusNo">
								<j-dict-select-tag v-model:value="formData.deviceStatusNo" dictCode="cpe_device_status" placeholder="请选择设备状态"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="设备型号" v-bind="validateInfos.deviceModuleNo" id="CpeDeviceForm-deviceModuleNo" name="deviceModuleNo">
								<j-dict-select-tag v-model:value="formData.deviceModuleNo" dictCode="cpe_device_module" placeholder="请选择设备型号"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="设备类型" v-bind="validateInfos.deviceTypeNo" id="CpeDeviceForm-deviceTypeNo" name="deviceTypeNo">
								<j-dict-select-tag v-model:value="formData.deviceTypeNo" dictCode="cpe_device_type" placeholder="请选择设备类型"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="关联卡片" v-bind="validateInfos.cardNo" id="CpeDeviceForm-cardNo" name="cardNo">
								<j-dict-select-tag v-model:value="formData.cardNo" dictCode="card_info,card_no,id" placeholder="请选择关联卡片"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="在线卡片" v-bind="validateInfos.onlineCardNo" id="CpeDeviceForm-onlineCardNo" name="onlineCardNo">
								<j-dict-select-tag v-model:value="formData.onlineCardNo" dictCode="card_info,card_no,id" placeholder="请选择在线卡片"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="在线网络" v-bind="validateInfos.onlineNetNo" id="CpeDeviceForm-onlineNetNo" name="onlineNetNo">
								<j-dict-select-tag v-model:value="formData.onlineNetNo" dictCode="cpe_network" placeholder="请选择在线网络"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="在线频段" v-bind="validateInfos.onlineBand" id="CpeDeviceForm-onlineBand" name="onlineBand">
								<a-input v-model:value="formData.onlineBand" placeholder="请输入在线频段"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="所属客户" v-bind="validateInfos.customerName" id="CpeDeviceForm-customerName" name="customerName">
								<j-dict-select-tag v-model:value="formData.customerName" dictCode="" placeholder="请选择所属客户"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="安装位置" v-bind="validateInfos.position" id="CpeDeviceForm-position" name="position">
								<a-input v-model:value="formData.position" placeholder="请输入安装位置"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="备注" v-bind="validateInfos.memo" id="CpeDeviceForm-memo" name="memo">
								<a-input v-model:value="formData.memo" placeholder="请输入备注"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
          </a-row>
        </a-form>
      </template>
    </JFormContainer>
  </a-spin>
</template>

<script lang="ts" setup>
  import { ref, reactive, defineExpose, nextTick, defineProps, computed, onMounted } from 'vue';
  import { defHttp } from '/@/utils/http/axios';
  import { useMessage } from '/@/hooks/web/useMessage';
  import JDictSelectTag from '/@/components/Form/src/jeecg/components/JDictSelectTag.vue';
  import { getValueType } from '/@/utils';
  import { saveOrUpdate } from '../CpeDevice.api';
  import { Form } from 'ant-design-vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';
  const props = defineProps({
    formDisabled: { type: Boolean, default: false },
    formData: { type: Object, default: () => ({})},
    formBpm: { type: Boolean, default: true }
  });
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = reactive<Record<string, any>>({
    id: '',
    deviceSn: '',   
    deviceStatusNo: '',   
    deviceModuleNo: '',   
    deviceTypeNo: '',   
    cardNo: '',   
    onlineCardNo: '',   
    onlineNetNo: '',   
    onlineBand: '',   
    customerName: '',   
    position: '',   
    memo: '',   
  });
  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = reactive({
    deviceSn: [{ required: true, message: '请输入设备标识!'},],
    deviceStatusNo: [{ required: true, message: '请输入设备状态!'},],
    deviceModuleNo: [{ required: true, message: '请输入设备型号!'},],
    deviceTypeNo: [{ required: true, message: '请输入设备类型!'},],
  });
  const { resetFields, validate, validateInfos } = useForm(formData, validatorRules, { immediate: false });

  // 表单禁用
  const disabled = computed(()=>{
    if(props.formBpm === true){
      if(props.formData.disabled === false){
        return false;
      }else{
        return true;
      }
    }
    return props.formDisabled;
  });

  
  /**
   * 新增
   */
  function add() {
    edit({});
  }

  /**
   * 编辑
   */
  function edit(record) {
    nextTick(() => {
      resetFields();
      const tmpData = {};
      Object.keys(formData).forEach((key) => {
        if(record.hasOwnProperty(key)){
          tmpData[key] = record[key]
        }
      })
      //赋值
      Object.assign(formData, tmpData);
    });
  }

  /**
   * 提交数据
   */
  async function submitForm() {
    try {
      // 触发表单验证
      await validate();
    } catch ({ errorFields }) {
      if (errorFields) {
        const firstField = errorFields[0];
        if (firstField) {
          formRef.value.scrollToField(firstField.name, { behavior: 'smooth', block: 'center' });
        }
      }
      return Promise.reject(errorFields);
    }
    confirmLoading.value = true;
    const isUpdate = ref<boolean>(false);
    //时间格式化
    let model = formData;
    if (model.id) {
      isUpdate.value = true;
    }
    //循环数据
    for (let data in model) {
      //如果该数据是数组并且是字符串类型
      if (model[data] instanceof Array) {
        let valueType = getValueType(formRef.value.getProps, data);
        //如果是字符串类型的需要变成以逗号分割的字符串
        if (valueType === 'string') {
          model[data] = model[data].join(',');
        }
      }
    }
    await saveOrUpdate(model, isUpdate.value)
      .then((res) => {
        if (res.success) {
          createMessage.success(res.message);
          emit('ok');
        } else {
          createMessage.warning(res.message);
        }
      })
      .finally(() => {
        confirmLoading.value = false;
      });
  }


  defineExpose({
    add,
    edit,
    submitForm,
  });
</script>

<style lang="less" scoped>
  .antd-modal-form {
    padding: 14px;
  }
</style>
