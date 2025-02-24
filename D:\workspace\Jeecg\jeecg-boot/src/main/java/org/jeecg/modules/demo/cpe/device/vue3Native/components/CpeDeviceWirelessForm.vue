<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form class="antd-modal-form" v-bind="formItemLayout" ref="formRef" name="CpeDeviceWirelessForm">
          <a-row>
						<a-col :span="24">
							<a-form-item label="2.4G WiFi功能" v-bind="validateInfos.radio24Disabled" id="CpeDeviceWireless-radio24Disabled" name="radio24Disabled">
								<j-switch v-model:value="formData.radio24Disabled"  ></j-switch>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="2.4G信道" v-bind="validateInfos.radio24Channel" id="CpeDeviceWireless-radio24Channel" name="radio24Channel">
								<j-dict-select-tag v-model:value="formData.radio24Channel" dictCode="24g_channel" placeholder="请选择2.4G信道"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="2.4G SSID" v-bind="validateInfos.radio24Ssid" id="CpeDeviceWireless-radio24Ssid" name="radio24Ssid">
								<a-input v-model:value="formData.radio24Ssid" placeholder="请输入2.4G SSID"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="2.4G加密" v-bind="validateInfos.radio24Encryption" id="CpeDeviceWireless-radio24Encryption" name="radio24Encryption">
								<j-dict-select-tag v-model:value="formData.radio24Encryption" dictCode="wireless_encryption" placeholder="请选择2.4G加密"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="2.4G密钥" v-bind="validateInfos.radio24Key" id="CpeDeviceWireless-radio24Key" name="radio24Key">
								<a-input v-model:value="formData.radio24Key" placeholder="请输入2.4G密钥"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="2.4G最大终端数" v-bind="validateInfos.radio24MaxSta" id="CpeDeviceWireless-radio24MaxSta" name="radio24MaxSta">
								<a-input-number v-model:value="formData.radio24MaxSta" placeholder="请输入2.4G最大终端数" style="width: 100%" />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="2.4G功率" v-bind="validateInfos.radio24Power" id="CpeDeviceWireless-radio24Power" name="radio24Power">
								<a-input-number v-model:value="formData.radio24Power" placeholder="请输入2.4G功率" style="width: 100%" />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="5G WiFi功能" v-bind="validateInfos.radio5Disabled" id="CpeDeviceWireless-radio5Disabled" name="radio5Disabled">
								<j-switch v-model:value="formData.radio5Disabled"  ></j-switch>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="5G信道" v-bind="validateInfos.radio5Channel" id="CpeDeviceWireless-radio5Channel" name="radio5Channel">
								<j-dict-select-tag v-model:value="formData.radio5Channel" dictCode="5g_channel" placeholder="请选择5G信道"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="5G SSID" v-bind="validateInfos.radio5Ssid" id="CpeDeviceWireless-radio5Ssid" name="radio5Ssid">
								<a-input v-model:value="formData.radio5Ssid" placeholder="请输入5G SSID"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="5G加密" v-bind="validateInfos.radio5Encryption" id="CpeDeviceWireless-radio5Encryption" name="radio5Encryption">
								<j-dict-select-tag v-model:value="formData.radio5Encryption" dictCode="wireless_encryption" placeholder="请选择5G加密"  allow-clear />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="5G密钥" v-bind="validateInfos.radio5Key" id="CpeDeviceWireless-radio5Key" name="radio5Key">
								<a-input v-model:value="formData.radio5Key" placeholder="请输入5G密钥"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="5G最大终端数" v-bind="validateInfos.radio5MaxSta" id="CpeDeviceWireless-radio5MaxSta" name="radio5MaxSta">
								<a-input-number v-model:value="formData.radio5MaxSta" placeholder="请输入5G最大终端数" style="width: 100%" />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="5G功率" v-bind="validateInfos.radio5Power" id="CpeDeviceWireless-radio5Power" name="radio5Power">
								<a-input-number v-model:value="formData.radio5Power" placeholder="请输入5G功率" style="width: 100%" />
							</a-form-item>
						</a-col>
          </a-row>
        </a-form>
      </template>
    </JFormContainer>
  </a-spin>
</template>

<script lang="ts" setup>
  import { ref, reactive, defineExpose, nextTick, onMounted, inject, defineProps, unref } from 'vue';
  import { defHttp } from '/@/utils/http/axios';
  import { useMessage } from '/@/hooks/web/useMessage';
  import JDictSelectTag from '/@/components/Form/src/jeecg/components/JDictSelectTag.vue';
  import JSwitch from '/@/components/Form/src/jeecg/components/JSwitch.vue';
  import { getValueType } from '/@/utils';
  import { cpeDeviceWirelessSaveOrUpdate } from '../CpeDeviceInfo.api';
  import { Form } from 'ant-design-vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';

  //接收主表id
  const mainId = inject('mainId');
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = reactive<Record<string, any>>({
    id: '',
        radio24Disabled: '',   
        radio24Channel: '',   
        radio24Ssid: '',   
        radio24Encryption: '',   
        radio24Key: '',   
        radio24MaxSta: undefined,
        radio24Power: undefined,
        radio5Disabled: '',   
        radio5Channel: '',   
        radio5Ssid: '',   
        radio5Encryption: '',   
        radio5Key: '',   
        radio5MaxSta: undefined,
        radio5Power: undefined,
  });
  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = {
    radio24Disabled: [{ required: true, message: '请输入2.4G WiFi功能!'},],
    radio24Channel: [{ required: true, message: '请输入2.4G信道!'},],
    radio24Encryption: [{ required: true, message: '请输入2.4G加密!'},],
    radio24Key: [{ required: true, message: '请输入2.4G密钥!'},],
    radio24MaxSta: [{ required: true, message: '请输入2.4G最大终端数!'},],
    radio24Power: [{ required: true, message: '请输入2.4G功率!'},],
    radio5Disabled: [{ required: true, message: '请输入5G WiFi功能!'},],
    radio5Channel: [{ required: true, message: '请输入5G信道!'},],
    radio5Encryption: [{ required: true, message: '请输入5G加密!'},],
    radio5Key: [{ required: true, message: '请输入5G密钥!'},],
    radio5MaxSta: [{ required: true, message: '请输入5G最大终端数!'},],
    radio5Power: [{ required: true, message: '请输入5G功率!'},],
  };
  const { resetFields, validate, validateInfos } = useForm(formData, validatorRules, { immediate: false });
  const props = defineProps({
    disabled: { type: Boolean, default: false },
  });
  const formItemLayout = {
    labelCol: { xs: { span: 24 }, sm: { span: 5 } },
    wrapperCol: { xs: { span: 24 }, sm: { span: 16 } },
  };
  
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
    // 触发表单验证
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
    if (unref(mainId)) {
      model['cpeId'] = unref(mainId);
    }
    await cpeDeviceWirelessSaveOrUpdate(model, isUpdate.value)
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
