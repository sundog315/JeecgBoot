<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form class="antd-modal-form" v-bind="formItemLayout" ref="formRef" name="CpeDeviceFrpForm">
          <a-row>
						<a-col :span="24">
							<a-form-item label="服务器地址" v-bind="validateInfos.serverAddr" id="CpeDeviceFrp-serverAddr" name="serverAddr">
								<a-input v-model:value="formData.serverAddr" placeholder="请输入服务器地址"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="服务器端口" v-bind="validateInfos.serverPort" id="CpeDeviceFrp-serverPort" name="serverPort">
								<a-input-number v-model:value="formData.serverPort" placeholder="请输入服务器端口" style="width: 100%" />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="令牌" v-bind="validateInfos.token" id="CpeDeviceFrp-token" name="token">
								<a-input v-model:value="formData.token" placeholder="请输入令牌"  allow-clear ></a-input>
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="SSH映射端口" v-bind="validateInfos.proxySshRemotePort" id="CpeDeviceFrp-proxySshRemotePort" name="proxySshRemotePort">
								<a-input-number v-model:value="formData.proxySshRemotePort" placeholder="请输入SSH映射端口" style="width: 100%" />
							</a-form-item>
						</a-col>
						<a-col :span="24">
							<a-form-item label="HTTP映射端口" v-bind="validateInfos.proxyHttpRemotePort" id="CpeDeviceFrp-proxyHttpRemotePort" name="proxyHttpRemotePort">
								<a-input-number v-model:value="formData.proxyHttpRemotePort" placeholder="请输入HTTP映射端口" style="width: 100%" />
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
  import { getValueType } from '/@/utils';
  import { cpeDeviceFrpSaveOrUpdate } from '../CpeDeviceInfo.api';
  import { Form } from 'ant-design-vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';

  //接收主表id
  const mainId = inject('mainId');
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = reactive<Record<string, any>>({
    id: '',
        serverAddr: '',   
        serverPort: undefined,
        token: '',   
        proxySshRemotePort: undefined,
        proxyHttpRemotePort: undefined,
  });
  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = {
    serverAddr: [{ required: true, message: '请输入服务器地址!'},],
    serverPort: [{ required: true, message: '请输入服务器端口!'},],
    token: [{ required: true, message: '请输入令牌!'},],
    proxySshRemotePort: [{ required: true, message: '请输入SSH映射端口!'},],
    proxyHttpRemotePort: [{ required: true, message: '请输入HTTP映射端口!'},],
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
    await cpeDeviceFrpSaveOrUpdate(model, isUpdate.value)
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
