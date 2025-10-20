<template>
  <a-spin :spinning="confirmLoading">
    <JFormContainer :disabled="disabled">
      <template #detail>
        <a-form class="antd-modal-form" v-bind="formItemLayout" ref="formRef" name="CpeDeviceClientForm">
          <a-row>
            <a-col :span="24">
              <a-form-item label="客户端IP" v-bind="validateInfos.clientIp" id="CpeDeviceClient-clientIp" name="clientIp">
                <a-input v-model:value="formData.clientIp" placeholder="请输入客户端IP" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="客户端MAC" v-bind="validateInfos.clientMac" id="CpeDeviceClient-clientMac" name="clientMac">
                <a-input v-model:value="formData.clientMac" placeholder="请输入客户端MAC" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="上线时间" v-bind="validateInfos.attachTs" id="CpeDeviceClient-attachTs" name="attachTs">
                <a-input v-model:value="formData.attachTs" placeholder="请输入上线时间" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="刷新时间" v-bind="validateInfos.refreshTs" id="CpeDeviceClient-refreshTs" name="refreshTs">
                <a-input v-model:value="formData.refreshTs" placeholder="请输入刷新时间" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="连接时长" v-bind="validateInfos.conntedDuration" id="CpeDeviceClient-conntedDuration" name="conntedDuration">
                <a-input-number v-model:value="formData.conntedDuration" placeholder="请输入连接时长" style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="上行流量" v-bind="validateInfos.upBytes" id="CpeDeviceClient-upBytes" name="upBytes">
                <a-input-number v-model:value="formData.upBytes" placeholder="请输入上行流量" style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="下行流量" v-bind="validateInfos.downBytes" id="CpeDeviceClient-downBytes" name="downBytes">
                <a-input-number v-model:value="formData.downBytes" placeholder="请输入下行流量" style="width: 100%" />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </template>
    </JFormContainer>
  </a-spin>
</template>

<script lang="ts" setup>
  import { ref, reactive, defineExpose, nextTick, inject, defineProps, unref } from 'vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { getValueType } from '/@/utils';
  import { cpeDeviceClientSaveOrUpdate } from '../CpeDeviceInfo.api';
  import { Form } from 'ant-design-vue';
  import JFormContainer from '/@/components/Form/src/container/JFormContainer.vue';

  //接收主表id
  const mainId = inject('mainId');
  const formRef = ref();
  const useForm = Form.useForm;
  const emit = defineEmits(['register', 'ok']);
  const formData = reactive<Record<string, any>>({
    id: '',
    clientIp: '',
    clientMac: '',
    attachTs: '',
    refreshTs: '',
    conntedDuration: undefined,
    upBytes: undefined,
    downBytes: undefined,
  });
  const { createMessage } = useMessage();
  const labelCol = ref<any>({ xs: { span: 24 }, sm: { span: 5 } });
  const wrapperCol = ref<any>({ xs: { span: 24 }, sm: { span: 16 } });
  const confirmLoading = ref<boolean>(false);
  //表单验证
  const validatorRules = {
    clientIp: [{ required: true, message: '请输入客户端IP!' }],
    clientMac: [{ required: true, message: '请输入客户端MAC!' }],
    attachTs: [{ required: true, message: '请输入上线时间!' }],
    refreshTs: [{ required: true, message: '请输入刷新时间!' }],
    conntedDuration: [{ required: true, message: '请输入连接时长!' }],
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
        if (record.hasOwnProperty(key)) {
          tmpData[key] = record[key];
        }
      });
      //赋值
      Object.assign(formData, tmpData);
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
    await cpeDeviceClientSaveOrUpdate(model, isUpdate.value)
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
