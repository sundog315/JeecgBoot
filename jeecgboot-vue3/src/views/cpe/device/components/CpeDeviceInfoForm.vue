<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form ref="formRef" class="antd-modal-form" :labelCol="labelCol" :wrapperCol="wrapperCol" name="CpeDeviceInfoForm">
          <a-row>
						<a-col :span="24">
							<a-form-item label="设备标识" v-bind="validateInfos.deviceSn" id="CpeDeviceInfoForm-deviceSn" name="deviceSn">
								<a-input v-model:value="formData.deviceSn" placeholder="请输入设备标识"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="设备状态" v-bind="validateInfos.deviceStatusNo" id="CpeDeviceInfoForm-deviceStatusNo" name="deviceStatusNo">
								<j-dict-select-tag v-model:value="formData.deviceStatusNo" dictCode="cpe_device_status" placeholder="请选择设备状态"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="设备型号" v-bind="validateInfos.deviceModuleNo" id="CpeDeviceInfoForm-deviceModuleNo" name="deviceModuleNo">
								<j-dict-select-tag v-model:value="formData.deviceModuleNo" dictCode="cpe_device_module" placeholder="请选择设备型号"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="设备类型" v-bind="validateInfos.deviceTypeNo" id="CpeDeviceInfoForm-deviceTypeNo" name="deviceTypeNo">
								<j-dict-select-tag v-model:value="formData.deviceTypeNo" dictCode="cpe_device_type" placeholder="请选择设备类型"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="关联卡片" v-bind="validateInfos.cardNo" id="CpeDeviceInfoForm-cardNo" name="cardNo">
								<j-dict-select-tag v-model:value="formData.cardNo" dictCode="" placeholder="请选择关联卡片"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="在线卡片" v-bind="validateInfos.onlineCardNo" id="CpeDeviceInfoForm-onlineCardNo" name="onlineCardNo">
								<j-dict-select-tag v-model:value="formData.onlineCardNo" dictCode="" placeholder="请选择在线卡片"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="在线网络" v-bind="validateInfos.onlineNetNo" id="CpeDeviceInfoForm-onlineNetNo" name="onlineNetNo">
								<j-dict-select-tag v-model:value="formData.onlineNetNo" dictCode="cpe_network" placeholder="请选择在线网络"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="在线频段" v-bind="validateInfos.onlineBand" id="CpeDeviceInfoForm-onlineBand" name="onlineBand">
								<a-input v-model:value="formData.onlineBand" placeholder="请输入在线频段"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="所属客户" v-bind="validateInfos.customerName" id="CpeDeviceInfoForm-customerName" name="customerName">
								<j-dict-select-tag v-model:value="formData.customerName" dictCode="" placeholder="请选择所属客户"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="安装位置" v-bind="validateInfos.position" id="CpeDeviceInfoForm-position" name="position">
								<a-input v-model:value="formData.position" placeholder="请输入安装位置"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
            <a-col :span="24">
							<a-form-item label="模组型号" v-bind="validateInfos.position" id="CpeDeviceForm-fiveGModule" name="fiveGModule">
								<a-input v-model:value="formData.fiveGModule" placeholder="请输入模组型号"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="5G模块版本" v-bind="validateInfos.modemVersion" id="CpeDeviceInfoForm-modemVersion" name="modemVersion">
								<a-input v-model:value="formData.modemVersion" placeholder="请输入5G模块版本"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="IMEI" v-bind="validateInfos.imei" id="CpeDeviceInfoForm-imei" name="imei">
								<a-input v-model:value="formData.imei" placeholder="请输入IMEI"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="ICCID" v-bind="validateInfos.iccid" id="CpeDeviceInfoForm-iccid" name="iccid">
								<a-input v-model:value="formData.iccid" placeholder="请输入ICCID"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="SIM卡槽" v-bind="validateInfos.simSlot" id="CpeDeviceInfoForm-simSlot" name="simSlot">
								<a-input-number v-model:value="formData.simSlot" placeholder="请输入SIM卡槽" style="width: 100%" />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="状态" v-bind="validateInfos.status" id="CpeDeviceInfoForm-status" name="status">
								<a-input v-model:value="formData.status" placeholder="请输入状态"  allow-clear ></a-input>
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
  import { saveOrUpdate } from '../CpeDeviceInfo.api';
  import { Form } from 'ant-design-vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';
  
  const props = defineProps({
    formDisabled: { type: Boolean, default: false },
    formData: { type: Object, default: () => ({}) },
    formBpm: { type: Boolean, default: true }
  });
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
        fiveGModule: '',   
        modemVersion: '',   
        imei: '',   
        iccid: '',   
        simSlot: undefined,
        status: '',   
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
  const formRef = ref();
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
      Object.assign(formData,tmpData);
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
